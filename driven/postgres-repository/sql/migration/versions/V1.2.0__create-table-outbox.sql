CREATE TABLE "adm-rrhh-digitaldoc".o_outbox (
    cod_n_idoutbox int8 NOT NULL,
    aggregateid bytea NULL,
    aggregatetype varchar(255) NULL,
    payload bytea NULL,
    fec_dt_creacion timestamp NULL,
    headers text NULL,
    CONSTRAINT o_outbox_pkey PRIMARY KEY (cod_n_idoutbox)
);

CREATE SEQUENCE "adm-rrhh-digitaldoc".o_outbox_id_seq START WITH 1 INCREMENT BY 1 CACHE 1 CYCLE;

CREATE TABLE "adm-rrhh-digitaldoc".o_outbox_offset (
    txt_outbox_table varchar(255) NOT NULL,
    cod_n_last_message_processed int8 NULL,
    fec_dt_update_date timestamp NULL,
    CONSTRAINT o_outbox_offset_pkey PRIMARY KEY (txt_outbox_table)
);
