CREATE SCHEMA IF NOT EXISTS cloudstorage;

CREATE TABLE IF NOT EXISTS "user"
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_role
(
    user_id BIGINT,
    role VARCHAR(50),
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_role FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS file
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT,
    file_name  VARCHAR(255) NOT NULL,
    file_size  BIGINT       NOT NULL,
    mime_type  VARCHAR(100) NOT NULL,
    url        VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_file_user FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS file_access
(
    id           BIGSERIAL PRIMARY KEY,
    file_id      BIGINT,
    user_id      BIGINT,
    access_level VARCHAR(50) NOT NULL,
    CONSTRAINT fk_access_file FOREIGN KEY (file_id) REFERENCES file (id) ON DELETE CASCADE,
    CONSTRAINT fk_access_user FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_log
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT,
    action     VARCHAR(255) NOT NULL,
    file_id    BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
    CONSTRAINT fk_audit_file FOREIGN KEY (file_id) REFERENCES file (id) ON DELETE CASCADE
);
