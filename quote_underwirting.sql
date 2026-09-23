-- -------------------------------------------------------------
-- TablePlus 26.9.12(770)
--
-- https://tableplus.com/
--
-- Database: insurance_quote_db
-- Generation Time: 2026-09-23 09:41:10.3940
-- -------------------------------------------------------------


DROP TABLE IF EXISTS "quoting"."quote";
-- Table Definition
CREATE TABLE "quoting"."quote" (
    "id" uuid NOT NULL,
    "status" varchar(255) NOT NULL,
    "total_premium" numeric,
    "created_at" timestamptz NOT NULL,
    "updated_at" timestamptz,
    "applicant_age" int4 NOT NULL,
    "applicant_email" varchar(255) NOT NULL,
    "applicant_name" varchar(255) NOT NULL,
    "calculated_monthly_premium" numeric(10,2),
    "coverage_type_code" varchar(50),
    "zip_code" varchar(255) NOT NULL,
    PRIMARY KEY ("id")
);

INSERT INTO "quoting"."quote" ("id", "status", "total_premium", "created_at", "updated_at", "applicant_age", "applicant_email", "applicant_name", "calculated_monthly_premium", "coverage_type_code", "zip_code") VALUES
('122ec65c-0794-4b40-a393-2edc36402b32', 'EXPIRED', NULL, '2026-09-21 08:23:15.500761-06', '2026-09-21 08:55:00.09661-06', 31, 'misael.garayr@gmail.com', 'Misael Garay', NULL, NULL, '99664'),
('1858b32e-88d0-4290-ab15-2398386d0783', 'EXPIRED', NULL, '2026-09-21 08:38:37.515531-06', '2026-09-21 09:10:00.087679-06', 66, 'misael.garayr@gmail.com', 'Katya Cabrera', 327.60, 'STANDARD', '99664'),
('b6164539-423f-43c5-b314-7a73e10c088a', 'DRAFT', NULL, '2026-09-23 00:36:16.222681-06', '2026-09-23 00:42:09.871007-06', 97, 'misael.garay@hotmail.com', 'Ivan Enrique', 150.00, 'STANDARD', '76116'),
('d977028c-b0be-44c3-b204-c8b0a0565de3', 'EXPIRED', NULL, '2026-09-21 09:29:33.120199-06', '2026-09-21 10:00:00.073586-06', 76, 'misael.garay@hotmail.com', 'Ivan Garay', 90.00, 'BASIC', '76116'),
('f25c9d00-4292-4557-88d0-8f5658ab48bc', 'EXPIRED', NULL, '2026-09-21 03:32:53.093819-06', '2026-09-21 04:15:00.055944-06', 31, 'misael.garay@hotmail.com', 'Misael Garya', NULL, NULL, '76116');

INSERT INTO "underwriting"."supplemental_health" ("quote_id", "has_preexisting_conditions", "takes_prescription_medication", "uses_tobacco", "has_spouse_coverage", "created_at", "updated_at", "conditions") VALUES
('1858b32e-88d0-4290-ab15-2398386d0783', 't', 't', 't', 't', '2026-09-21 08:39:59.405187-06', '2026-09-21 08:39:59.405215-06', 'HEART_DISEASE,HYPERTENSION,CANCER_HISTORY'),
('b6164539-423f-43c5-b314-7a73e10c088a', 'f', 't', 'f', 'f', '2026-09-23 00:36:45.418164-06', '2026-09-23 00:42:09.880234-06', NULL),
('d977028c-b0be-44c3-b204-c8b0a0565de3', 'f', 'f', 't', 'f', '2026-09-21 09:29:40.350317-06', '2026-09-21 09:29:40.350339-06', NULL);

