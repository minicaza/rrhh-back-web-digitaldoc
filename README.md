# rrhh-back-web-digitaldoc

Microservicio responsable del procesamiento y consulta de los **documentos digitales de certificación de uso de IA** de los empleados de Mercadona.

---

## Contexto en la solución

El sistema completo consta de **3 microservicios** que colaboran sobre una base de datos PostgreSQL compartida:

```
topic: employee (Avro)
       │
       ▼
┌──────────────────────────────────────┐
│  rrhh-back-snk-digitaldoc            │
│  consume employee → persiste PENDING │
│  llama POST /documents/process [UUID]│
└────────────────┬─────────────────────┘
                 ▼
┌──────────────────────────────────────┐
│  rrhh-back-web-digitaldoc  ← este   │
│  procesamiento + consultas           │
└──────────────────────────────────────┘
             ▲
             │ POST /documents/process [UUID, UUID, ...]
┌──────────────────────────────────────┐
│  rrhh-back-btc-digitaldoc            │
│  reprocesa FAILEDs diariamente 03:00 │
└──────────────────────────────────────┘
```

**Decisión clave**: este micro es el **único que escribe en `document` después del estado PENDING**. El SNK solo captura el evento Kafka y crea el registro inicial. El BTC únicamente redirige los FAILEDs de vuelta a este micro. Toda la lógica de negocio vive aquí.

---

## Por qué 3 micros y no 1 ni 2

- **SNK**: captura el evento Kafka de forma desacoplada. Si el WEB cae, el SNK persiste PENDING de todas formas y devuelve OK al consumer Kafka — el BTC recogerá el documento más tarde. Sin SNK, un fallo del WEB provocaría que el consumer reintentara el mismo mensaje indefinidamente.
- **WEB**: centraliza el procesamiento y las consultas. Tanto el SNK (flujo normal) como el BTC (reprocesamiento) llaman a la misma API, sin duplicar lógica.
- **BTC**: reprocesamiento diario de FAILEDs vía CronJob k8s. Al ser un batch independiente, no afecta al throughput del SNK ni al WEB en tiempo real.

---

## Qué hace este micro

1. **Acepta** una lista de UUIDs de documentos vía `POST /documents/process` y los despacha al pipeline.
2. **Ejecuta** el pipeline en un hilo asíncrono (`@Async`) por cada documento siguiendo 4 pasos:
   - `EnrichmentStep` — llama a la API externa del cardgenerator y cachea la respuesta (Caffeine, TTL 10 min).
   - `PdfGenerationStep` — genera el PDF del certificado con PDFBox.
   - `BucketStorageStep` — sube el PDF a MinIO/S3 en la ruta determinista `documents/{documentId}.pdf`.
   - `KafkaPublicationStep` — publica el evento de disponibilidad vía outbox transaccional.
3. **Expone** endpoints REST de consulta: por id, por empleado, por estado y estado con i18n.

---

## API REST

Base path: `/human-resources/rrhh/digitaldoc/v1`

| Método | Ruta | Respuesta | Descripción |
|--------|------|-----------|-------------|
| `POST` | `/documents/process` | `200 OK` | Lista de UUIDs para procesar. El SNK envía 1, el BTC hasta 100 por chunk. |
| `GET`  | `/documents/{documentId}` | `200 / 404` | Documento por UUID con nombre de estado localizado. |
| `GET`  | `/documents/{documentId}/status` | `200 / 404` | Estado actual con descripción i18n (header `Accept-Language`). |
| `GET`  | `/documents/employee?employeeId=X&managedGroupId=Y` | `200 / 404` | Documento de un empleado concreto. |
| `GET`  | `/documents?status=PUBLISHED&firstPage=1&pageSize=10` | `200` | Lista paginada de documentos por estado. |

Swagger UI disponible en local: `http://localhost:8080/human-resources/rrhh/digitaldoc/v1/swagger-ui/index.html`

---

## Ciclo de vida del documento

```
PENDING → ENRICHED → PDF_GENERATED → STORED → PUBLISHED
   ↓          ↓            ↓             ↓
 FAILED    FAILED        FAILED        FAILED
```

Las transiciones válidas están declaradas en `DocumentStatus.allowedNextStatuses()`. Si un step falla, el documento pasa a `FAILED` y se registra el error en `document_error` con el nombre del step (`ENRICHMENT`, `PDF_GENERATION`, `STORAGE`, `PUBLICATION`). Cuando el BTC reenvía el UUID, el orquestador consulta el último registro de `document_error` para retomar desde el step correcto.

---

## Diseño — decisiones relevantes

### Patrón Strategy para el pipeline

Cada step implementa la interfaz `DocumentStep`:

```java
DocumentStatus fromStatus();      // estado en el que aplica el step
DocumentStatus toStatus();        // estado tras éxito
FailedStep failedStep();          // identificador para document_error
void execute(Document doc);       // I/O externa, sin transacción abierta
boolean handlesOwnPersistence();  // true solo en KafkaPublicationStep
```

El orquestador (`DocumentPipelineOrchestrator`) busca el step aplicable con `stream().filter().findFirst()`. Tras cada éxito, persiste el nuevo estado en una transacción corta `REQUIRES_NEW` — nunca se abre una transacción que abarque I/O externa.

### KafkaPublicationStep — outbox atómico

`KafkaPublicationStep` devuelve `handlesOwnPersistence() = true` porque la publicación en Kafka y la actualización del estado a PUBLISHED **deben ser atómicas**: inserción en `o_outbox` + `UPDATE document` en una única transacción `REQUIRES_NEW` en `OutboxPublicationAdapter`. El relay del outbox solo leerá el registro una vez confirmado el commit.

### Procesamiento asíncrono

`POST /documents/process` retorna `200 OK` inmediatamente. El procesamiento real ocurre en el pool `documentExecutor` (`ThreadPoolTaskExecutor`, 8–32 hilos, cola de 500, política `CallerRunsPolicy`). Esto desacopla la captura (SNK) del procesamiento y garantiza que, si el WEB cae tras aceptar, los documentos quedan en `PENDING` para que el BTC los recoja.

### Idempotencia

- `UNIQUE (employee_id, managed_group_id)` en `document` → un empleado tiene como máximo un documento por grupo.
- El UUID del documento se genera al crear el PENDING y se reutiliza en todos los reintentos.
- Path de bucket determinista: `documents/{documentId}.pdf` → los reintentos sobreescriben el mismo objeto.
- Documentos PUBLISHED enviados de nuevo al pipeline → el orquestador no encuentra ningún step aplicable (`fromStatus == PUBLISHED` no existe) y termina sin efectos secundarios.

### Transacciones cortas

Cada cambio de estado del repositorio es `@Transactional(propagation = REQUIRES_NEW)` y usa `findById` + `setDocumentStatusId` + `save()`. Esto garantiza que Hibernate Envers audite cada cambio en `document_aud` y que el fallo en un step no deshaga los estados ya confirmados de los steps anteriores.

### Auditoría con Hibernate Envers

`DocumentMO` está anotado con `@Audited`. Los cambios de estado nunca se hacen con bulk JPQL (`@Modifying @Query`) porque Envers no intercepta esas actualizaciones. El `save()` normal pasa por el session tracking de Hibernate y genera la entrada correspondiente en `document_aud`.

### Cache en el enrichment

`EnrichmentClientAdapter` usa `@Cacheable(value = "enrichment", key = "#employeeId + '|' + #managedGroupId", unless = "#result == null")` con Caffeine (TTL 10 min, máx. 10.000 entradas). Si el BTC reintenta un documento que ya fue enriquecido en la misma ventana de 10 min, la llamada al cardgenerator no se repite.

### Transiciones validadas en código

Las transiciones válidas viven en `DocumentStatus.allowedNextStatuses()` (mirror de la tabla `document_status_transitions` del plan). El método `canTransitionTo(target)` permite validar cualquier transición en runtime sin consultar BBDD.

### i18n de estados

`document_status_names` almacena las descripciones de cada estado en varios idiomas. El endpoint `GET /documents/{id}/status` acepta el header `Accept-Language` y devuelve la descripción en el idioma solicitado (por defecto `es-ES`).

---

## Módulos Maven

```
rrhh-back-web-digitaldoc/
├── application/                  Dominio + puertos + servicios (sin dependencias de FWK)
├── driving/
│   └── api-rest/                 Controladores generados desde swagger.yaml (API-first)
├── driven/
│   ├── postgres-repository/      JPA + Envers + outbox transaccional (fwkcna-starter-outbox-avro-jpa-register)
│   ├── enrichment-rest-client/   WebClient al cardgenerator con @Cacheable Caffeine
│   ├── pdf-generator/            PDFBox — generación del certificado
│   └── bucket-client/            fwkcna-starter-buckets — MinIO/S3
└── boot/                         Application.java + AsyncConfig + docker-compose
```

---

## Modelo de datos

Las migraciones Flyway viven en `driven/postgres-repository/sql/migration/versions/` y se ejecutan **únicamente en este micro**. El SNK y el BTC apuntan a la misma BBDD con `spring.flyway.enabled=false`.

| Tabla | Migración | Propósito |
|-------|-----------|-----------|
| `topic_managed_group` | V1.0.0 | Catálogo de grupos gestionados (replica del topic) |
| `topic_employee` | V1.0.0 | Datos del empleado (upsert por el SNK en cada evento) |
| `document_status` | V1.1.0 | Catálogo de estados: PENDING, ENRICHED, PDF_GENERATED, STORED, PUBLISHED, FAILED |
| `topic_locale_language` | V1.1.0 | Idiomas soportados (es-ES, pt-PT) |
| `document_status_names` | V1.1.0 | Descripciones i18n de cada estado por idioma |
| `document` | V1.1.0 | Un registro por empleado/grupo — estado actual del ciclo de vida |
| `document_aud` | V1.1.0 | Auditoría Hibernate Envers de cada cambio en `document` |
| `document_error` | V1.1.0 | Historial de errores por paso del pipeline |
| `o_outbox` | V1.2.0 | Tabla de outbox transaccional leída por el relay para publicar a Kafka |

> Las transiciones válidas entre estados **no son una tabla BBDD**: viven en `DocumentStatus.allowedNextStatuses()` y se validan en runtime via `canTransitionTo()`.

---

## Configuración

### Propiedades clave (`application.yml`)

| Propiedad | Descripción |
|-----------|-------------|
| `spring.cache.caffeine.spec` | TTL y tamaño del cache Caffeine para el enrichment (`maximumSize=10000,expireAfterWrite=10m`) |
| `fwkcna.outbox.schema-registries.sr-basic.url` | URL del Schema Registry |
| `outbox.topic.employee-digital-document` | Tópico Kafka de salida |
| `fwkcna.buckets[0].id` / `bucket-name` | Identificador y nombre del bucket |
| `cardgenerator.base-url` | URL base del API externo de certificaciones |

### Variables de entorno en producción

| Variable | Descripción |
|----------|-------------|
| `SCHEMA_REGISTRY_URL` | URL del Schema Registry |
| `KAFKA_USER` / `KAFKA_PASSWORD` | Credenciales Basic Auth del Schema Registry |
| `KAFKA_TOPIC_OUT` | Override del tópico Kafka de salida |
| `BUCKET_ENDPOINT` | Endpoint del bucket (MinIO/GCS/NetApp) |
| `BUCKET_ACCESS_KEY` / `BUCKET_SECRET_KEY` | Credenciales del bucket |

---

## Arranque en local

### Infraestructura

```bash
cd devops/docker
docker compose up -d
```

Levanta: PostgreSQL (5432), MinIO (9000/9001), ZooKeeper, Kafka (29092), Schema Registry (9081), AKHQ (8089).

> Si el SNK ya está corriendo, Kafka y Schema Registry ya están levantados — no hace falta volver a levantarlos.

Crear el bucket en MinIO antes de la primera ejecución:

```bash
# Consola web: http://localhost:9001  (usuario: minio / contraseña: minio123)
# O con el CLI:
mc alias set local http://localhost:9000 minio minio123
mc mb local/rrhh-documents
```

### Aplicación

```bash
mvn spring-boot:run -pl boot -Dspring-boot.run.profiles=local
```

---

## API externa — cardgenerator

```
GET http://localhost:8081/managed-groups/{managedGroupId}/employees/{employeeId}/ai-certification
```

Devuelve los datos del empleado y su estado de certificación IA. Los resultados se cachean 10 minutos. Un `404` del cardgenerator provoca que el step `ENRICHMENT` falle y el documento pase a `FAILED`.

---

## Mejoras futuras

- **Lock distribuido** (PG advisory lock) para evitar que dos instancias del WEB procesen el mismo documento simultáneamente. El outbox ya garantiza consistencia; el lock evitaría trabajo duplicado.
- **Cache en consultas GET** del WEB con invalidación al cambiar de estado.
- **Métricas custom Micrometer**: contadores por estado, histogramas de duración por step.
- **Logs estructurados con MDC**: propagación de `documentId`, `employeeId`, `managedGroupId` en todos los logs del pipeline.
- **Tests de integración con Testcontainers**: Kafka + PostgreSQL + MinIO + WireMock para el cardgenerator.
- **Dead Letter Topic** en el consumer del SNK para eventos que fallen tras N reintentos.
- **Auth real** en el REST client del SNK al WEB (ADFS client-credentials).
