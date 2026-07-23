-- Creates the employees table

CREATE TABLE IF NOT EXISTS employees (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    first_name    VARCHAR(100)    NOT NULL,
    last_name     VARCHAR(100)    NOT NULL,
    email         VARCHAR(255)    NOT NULL,
    job_title     VARCHAR(150)    NOT NULL,
    salary        DECIMAL(15, 2)  NOT NULL,
    hire_date     DATE            NOT NULL,
    department_id BIGINT          NOT NULL,
    created_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_employees          PRIMARY KEY (id),
    CONSTRAINT uq_employees_email    UNIQUE (email),
    CONSTRAINT fk_employees_dept     FOREIGN KEY (department_id)
                                       REFERENCES departments(id)
                                       ON DELETE RESTRICT
                                       ON UPDATE CASCADE
);
