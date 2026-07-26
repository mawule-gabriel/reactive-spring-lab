-- Creates the departments table

CREATE TABLE IF NOT EXISTS departments (
    id          BIGSERIAL       NOT NULL,
    name        VARCHAR(150)    NOT NULL,
    description VARCHAR(500),
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_departments PRIMARY KEY (id),
    CONSTRAINT uq_departments_name UNIQUE (name)
);
