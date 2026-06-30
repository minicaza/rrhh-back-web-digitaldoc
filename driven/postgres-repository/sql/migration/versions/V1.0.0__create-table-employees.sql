CREATE SCHEMA IF NOT EXISTS "adm-rrhh-digitaldoc";

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE "adm-rrhh-digitaldoc".topic_managed_group (
    managed_group_id             varchar(2)   NOT NULL,
    financial_company_public_id  varchar(2)   NULL,
    name                         varchar(250) NULL,
    short_name                   varchar(45)  NULL,
    is_visible                   bool         DEFAULT false NULL,
    CONSTRAINT topic_managed_group_pkey PRIMARY KEY (managed_group_id)
);

INSERT INTO "adm-rrhh-digitaldoc".topic_managed_group VALUES
  ('08', '08', 'MERCADONA, S.A.', 'MERCADONA', true),
  ('09', '09', 'IRMÃDONA', 'IRMÃDONA', true)
  ON CONFLICT DO NOTHING
;

CREATE TABLE "adm-rrhh-digitaldoc".topic_employee (
    employee_id varchar(7) NOT NULL,
    managed_group_id varchar(2) NOT NULL,
    "name" varchar(50) NULL,
    first_surname varchar(50) NULL,
    second_surname varchar(50) NULL,
    favourite_name varchar(50) NULL,
    is_active bool NULL,
    professional_email_address varchar(320) NULL,
    user_id varchar(20) NULL,
    full_name TEXT GENERATED ALWAYS AS (
      upper(
        btrim(
          regexp_replace(
            coalesce(name, '') || ' ' ||
            coalesce(first_surname, '') || ' ' ||
            coalesce(second_surname, '') || ' ' ||
            coalesce(favourite_name, ''),
            '\s+', ' ', 'g'
          )
        )
      )
      ) STORED,
    CONSTRAINT topic_employee_pkey PRIMARY KEY (employee_id, managed_group_id)
);
CREATE INDEX topic_employee_full_name_idx ON "adm-rrhh-digitaldoc".topic_employee USING gin (full_name gin_trgm_ops);
CREATE INDEX topic_employee_user_id_idx ON "adm-rrhh-digitaldoc".topic_employee USING btree (user_id);

ALTER TABLE "adm-rrhh-digitaldoc".topic_employee
  ADD CONSTRAINT topic_employee_managed_group_id_fkey FOREIGN KEY (managed_group_id)
  REFERENCES "adm-rrhh-digitaldoc".topic_managed_group(managed_group_id);
