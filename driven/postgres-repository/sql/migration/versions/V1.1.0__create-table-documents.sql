/*==============================================================*/
/* Table: DOCUMENT_STATUS                                         */
/*==============================================================*/

CREATE TABLE "adm-rrhh-digitaldoc".document_status (
    id      SMALLINT    NOT NULL,
    status  VARCHAR(50) NOT NULL,
    CONSTRAINT pk_document_status        PRIMARY KEY (id),
    CONSTRAINT uk_document_status_status UNIQUE (status)
);

COMMENT ON TABLE  "adm-rrhh-digitaldoc".document_status        IS 'Catálogo de estados del ciclo de vida de un documento.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document_status.id     IS 'Identificador numérico del estado.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document_status.status IS 'Nombre técnico del estado (p.ej. PENDING, ENRICHED).';

INSERT INTO "adm-rrhh-digitaldoc".document_status (id, status) VALUES
    (1, 'PENDING'),
    (2, 'ENRICHED'),
    (3, 'PDF_GENERATED'),
    (4, 'STORED'),
    (5, 'PUBLISHED'),
    (6, 'FAILED')
ON CONFLICT DO NOTHING;

/*==============================================================*/
/* Table: TOPIC_LOCALE_LANGUAGE                                   */
/*==============================================================*/

CREATE TABLE "adm-rrhh-digitaldoc".topic_locale_language (
    locale VARCHAR(10)  NOT NULL,
    name   VARCHAR(100) NOT NULL,
    CONSTRAINT pk_topic_locale_language PRIMARY KEY (locale)
);

COMMENT ON COLUMN "adm-rrhh-digitaldoc".topic_locale_language.locale IS 'Código de idioma/región (p.ej. es-ES, pt-PT).';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".topic_locale_language.name   IS 'Nombre legible del idioma.';

INSERT INTO "adm-rrhh-digitaldoc".topic_locale_language (locale, name) VALUES
    ('es-ES', 'Español (España)'),
    ('pt-PT', 'Português (Portugal)')
ON CONFLICT DO NOTHING;

/*==============================================================*/
/* Table: DOCUMENT_STATUS_NAMES                                   */
/*==============================================================*/

CREATE TABLE "adm-rrhh-digitaldoc".document_status_names (
    document_status_id SMALLINT      NOT NULL,
    locale             VARCHAR(10)   NOT NULL,
    description        VARCHAR(500)  NOT NULL,
    CONSTRAINT pk_document_status_names         PRIMARY KEY (document_status_id, locale),
    CONSTRAINT fk_dsn_document_status           FOREIGN KEY (document_status_id)
        REFERENCES "adm-rrhh-digitaldoc".document_status(id),
    CONSTRAINT fk_dsn_locale                    FOREIGN KEY (locale)
        REFERENCES "adm-rrhh-digitaldoc".topic_locale_language(locale)
);

COMMENT ON TABLE  "adm-rrhh-digitaldoc".document_status_names                    IS 'Descripciones i18n de cada estado de documento.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document_status_names.document_status_id IS 'Estado al que pertenece la descripción.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document_status_names.locale             IS 'Idioma de la descripción.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document_status_names.description        IS 'Texto descriptivo del estado en el idioma indicado.';

INSERT INTO "adm-rrhh-digitaldoc".document_status_names (document_status_id, locale, description) VALUES
    (1, 'es-ES', 'El evento ha sido recibido y se ha creado el registro del documento.'),
    (2, 'es-ES', 'El documento ha sido enriquecido con los datos del empleado.'),
    (3, 'es-ES', 'El PDF del documento ha sido generado correctamente.'),
    (4, 'es-ES', 'El PDF ha sido almacenado en el sistema de ficheros.'),
    (5, 'es-ES', 'El documento ha sido publicado y está disponible para el empleado.'),
    (6, 'es-ES', 'El procesamiento del documento ha fallado. Consulte el paso y el mensaje de error.')
ON CONFLICT DO NOTHING;

/*==============================================================*/
/* Table: DOCUMENT                                                */
/*==============================================================*/

CREATE TABLE "adm-rrhh-digitaldoc".document (
    id                 UUID         NOT NULL DEFAULT gen_random_uuid(),
    employee_id        VARCHAR(7)   NOT NULL,
    managed_group_id   VARCHAR(2)   NOT NULL,
    document_status_id SMALLINT     NOT NULL DEFAULT 1,
    failed_step        VARCHAR(50),
    document_path      VARCHAR(500),
    error_message      VARCHAR(2000),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_document                   PRIMARY KEY (id),
    CONSTRAINT uk_document_employee_group    UNIQUE (employee_id, managed_group_id),
    CONSTRAINT fk_document_employee          FOREIGN KEY (employee_id, managed_group_id)
        REFERENCES "adm-rrhh-digitaldoc".topic_employee(employee_id, managed_group_id),
    CONSTRAINT fk_document_status            FOREIGN KEY (document_status_id)
        REFERENCES "adm-rrhh-digitaldoc".document_status(id)
);

COMMENT ON TABLE  "adm-rrhh-digitaldoc".document                    IS 'Relación empleado-documento digital de certificación IA.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document.id                 IS 'UUID generado automáticamente al crear el documento.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document.employee_id        IS 'Identificador del empleado.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document.managed_group_id   IS 'Grupo gestionado al que pertenece el empleado.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document.document_status_id IS 'Estado actual del ciclo de vida del documento.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document.failed_step        IS 'Paso en el que falló el procesamiento: ENRICHMENT, PDF_GENERATION, STORAGE, PUBLICATION.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document.document_path      IS 'Ruta del PDF en el bucket (documents/{id}.pdf).';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document.error_message      IS 'Mensaje de error del último fallo registrado.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document.created_at         IS 'Fecha y hora de creación del registro.';
COMMENT ON COLUMN "adm-rrhh-digitaldoc".document.updated_at         IS 'Fecha y hora de la última actualización.';

CREATE INDEX idx_document_status_id ON "adm-rrhh-digitaldoc".document (document_status_id);

CREATE TABLE "adm-rrhh-digitaldoc".document_aud (
    id                 UUID         NOT NULL DEFAULT gen_random_uuid(),
    rev int4 NOT NULL,
    revtype int2 NULL,
    employee_id        VARCHAR(7)   NOT NULL,
    managed_group_id   VARCHAR(2)   NOT NULL,
    document_status_id SMALLINT     NOT NULL DEFAULT 1,
    failed_step        VARCHAR(50),
    document_path      VARCHAR(500),
    error_message      VARCHAR(2000),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_document_aud PRIMARY KEY (id, rev)
);
