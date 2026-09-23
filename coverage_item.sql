-- -------------------------------------------------------------
-- TablePlus 26.9.12(770)
--
-- https://tableplus.com/
--
-- Database: insurance_quote_db
-- Generation Time: 2026-09-23 09:32:39.5990
-- -------------------------------------------------------------


DROP TABLE IF EXISTS "catalogs"."coverage_item";
-- Table Definition
CREATE TABLE "catalogs"."coverage_item" (
    "id" uuid NOT NULL,
    "name" varchar(255) NOT NULL,
    "description" varchar(255),
    "created_at" timestamptz NOT NULL,
    "active" bool NOT NULL,
    "coverage_type_id" varchar(255) NOT NULL,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "catalogs"."preexisting_condition";
-- Table Definition
CREATE TABLE "catalogs"."preexisting_condition" (
    "code" varchar(50) NOT NULL,
    "name" varchar(100) NOT NULL,
    "active" bool NOT NULL DEFAULT true,
    "created_at" timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY ("code")
);

DROP TABLE IF EXISTS "catalogs"."coverage_type";
-- Table Definition
CREATE TABLE "catalogs"."coverage_type" (
    "code" varchar(50) NOT NULL,
    "name" varchar(100) NOT NULL,
    "base_premium" numeric(10,2) NOT NULL,
    "created_at" timestamptz NOT NULL DEFAULT now(),
    "updated_at" timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY ("code")
);

INSERT INTO "catalogs"."coverage_item" ("id", "name", "description", "created_at", "active", "coverage_type_id") VALUES
('102220b0-2f78-4b79-a196-34503f80f47a', 'Hospitalization Allowance', 'Daily cash stipend during qualified hospital stays.', '2026-09-21 03:40:10.047715-06', 't', 'STANDARD'),
('1173ab38-e892-4f73-b463-2cc8ff920903', 'Premium Life & Estate Protection', 'Maximum life benefit coverage including estate protection support.', '2026-09-21 03:40:10.047715-06', 't', 'PREMIUM'),
('1ca35de1-c4f8-4561-a53a-f007a45d1ad0', 'Accidental Death Benefit', 'Additional payout in the event of accidental death.', '2026-09-21 03:40:10.047715-06', 't', 'BASIC'),
('2caeed0d-3b33-4f30-bf30-541d0410441c', 'Emergency Medical Expense', 'Coverage for urgent emergency room and outpatient care.', '2026-09-21 03:40:10.047715-06', 't', 'BASIC'),
('327d52a6-31de-4274-8f56-7d8fc973745a', 'Global Emergency Evacuation', 'International medical repatriation and emergency transport.', '2026-09-21 03:40:10.047715-06', 't', 'PREMIUM'),
('565b36c9-c3f2-4743-a2e2-482278951189', 'Surgical Care Benefit', 'Financial coverage for inpatient surgical procedures.', '2026-09-21 03:40:10.047715-06', 't', 'STANDARD'),
('5ebd0156-c78b-4988-b52b-640664486deb', 'Severe & Chronic Diseases Protection', 'Full coverage for severe, terminal, and long-term chronic conditions.', '2026-09-21 03:40:10.047715-06', 't', 'PREMIUM'),
('7f86b2e5-f8b8-40f3-9563-e27bc6638994', 'Critical Illness Coverage', 'Lump sum payment upon diagnosis of major critical illnesses.', '2026-09-21 03:40:10.047715-06', 't', 'STANDARD'),
('a2ccd38a-0908-4216-a56d-fc1a69a3a16e', 'Total & Permanent Disability', 'Income replacement benefit in case of permanent total disability.', '2026-09-21 03:40:10.047715-06', 't', 'PREMIUM'),
('a2fbc106-b64d-43b4-851b-f325dcd1af8e', 'Preventive & Mental Health Care', 'Routine annual wellness checks, diagnostics, and therapy sessions.', '2026-09-21 03:40:10.047715-06', 't', 'PREMIUM'),
('a9d4f903-af3f-4ccc-bd4d-abebf2aaaf80', 'Basic Life Protection', 'Standard payout coverage for essential life protection.', '2026-09-21 03:40:10.047715-06', 't', 'BASIC'),
('f397734b-0209-4857-a4fd-ddc837509c7b', 'Standard Life Protection', 'Comprehensive life protection with extended benefit limits.', '2026-09-21 03:40:10.047715-06', 't', 'STANDARD');

INSERT INTO "catalogs"."preexisting_condition" ("code", "name", "active", "created_at") VALUES
('CANCER_HISTORY', 'Cancer History', 't', '2026-09-19 19:26:25.801813-06'),
('DIABETES', 'Diabetes', 't', '2026-09-19 19:26:25.801813-06'),
('HEART_DISEASE', 'Heart Disease', 't', '2026-09-19 19:26:25.801813-06'),
('HYPERTENSION', 'Hypertension', 't', '2026-09-19 19:26:25.801813-06'),
('OTHER', 'Other', 't', '2026-09-19 19:26:25.801813-06');

INSERT INTO "catalogs"."coverage_type" ("code", "name", "base_premium", "created_at", "updated_at") VALUES
('BASIC', 'Basic', 50.00, '2026-09-19 19:29:36.125948-06', '2026-09-19 19:29:36.125948-06'),
('PREMIUM', 'Premium', 200.00, '2026-09-19 19:29:36.125948-06', '2026-09-19 19:29:36.125948-06'),
('STANDARD', 'Standard', 100.00, '2026-09-19 19:29:36.125948-06', '2026-09-19 19:29:36.125948-06');

