-- Creates the revoked_tokens table

CREATE TABLE IF NOT EXISTS revoked_tokens (
    jti        VARCHAR(36)  NOT NULL,
    expires_at TIMESTAMP    NOT NULL,

    CONSTRAINT pk_revoked_tokens PRIMARY KEY (jti)
);
