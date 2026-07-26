-- Creates the users table

CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL       NOT NULL,
    email         VARCHAR(255)    NOT NULL,
    password      VARCHAR(255)    NOT NULL,
    role          VARCHAR(50)     NOT NULL,

    CONSTRAINT pk_users       PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);
