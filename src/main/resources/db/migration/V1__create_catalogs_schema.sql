-- 1. Create missing custom schemas
CREATE SCHEMA IF NOT EXISTS catalogs;
CREATE SCHEMA IF NOT EXISTS quoting;
CREATE SCHEMA IF NOT EXISTS underwriting;

-- =========================================================================
-- SCHEMAS: CATALOGS
-- =========================================================================
CREATE TABLE IF NOT EXISTS catalogs.coverage_type (
                                                      code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    base_premium NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_coverage_type PRIMARY KEY (code)
    );

CREATE TABLE IF NOT EXISTS catalogs.coverage_item (
                                                      id UUID NOT NULL,
                                                      name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL,
    active BOOL NOT NULL,
    coverage_type_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_coverage_item PRIMARY KEY (id),
    CONSTRAINT fk_coverage_item_type FOREIGN KEY (coverage_type_id) REFERENCES catalogs.coverage_type(code)
    );

CREATE TABLE IF NOT EXISTS catalogs.preexisting_condition (
                                                              code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    active BOOL NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_preexisting_condition PRIMARY KEY (code)
    );

-- =========================================================================
-- SCHEMAS: QUOTING
-- =========================================================================
CREATE TABLE IF NOT EXISTS quoting.quote (
                                             id UUID NOT NULL,
                                             status VARCHAR(255) NOT NULL,
    total_premium NUMERIC,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    applicant_age INT4 NOT NULL,
    applicant_email VARCHAR(255) NOT NULL,
    applicant_name VARCHAR(255) NOT NULL,
    calculated_monthly_premium NUMERIC(10,2),
    coverage_type_code VARCHAR(50),
    zip_code VARCHAR(255) NOT NULL,
    CONSTRAINT pk_quote PRIMARY KEY (id),
    CONSTRAINT fk_quote_coverage_type FOREIGN KEY (coverage_type_code) REFERENCES catalogs.coverage_type(code)
    );

-- =========================================================================
-- SCHEMAS: UNDERWRITING
-- =========================================================================
CREATE TABLE IF NOT EXISTS underwriting.supplemental_health (
                                                                quote_id UUID NOT NULL,
                                                                has_preexisting_conditions BOOL NOT NULL DEFAULT FALSE,
                                                                takes_prescription_medication BOOL NOT NULL DEFAULT FALSE,
                                                                uses_tobacco BOOL NOT NULL DEFAULT FALSE,
                                                                has_spouse_coverage BOOL NOT NULL DEFAULT FALSE,
                                                                created_at TIMESTAMPTZ NOT NULL,
                                                                updated_at TIMESTAMPTZ,
                                                                conditions VARCHAR(255),
    CONSTRAINT pk_supplemental_health PRIMARY KEY (quote_id),
    CONSTRAINT fk_supplemental_health_quote FOREIGN KEY (quote_id) REFERENCES quoting.quote(id) ON DELETE CASCADE
    );