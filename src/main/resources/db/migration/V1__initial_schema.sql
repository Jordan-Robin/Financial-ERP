-- Séquences
CREATE SEQUENCE IF NOT EXISTS privilege_sequence START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS role_sequence START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS user_sequence START WITH 1 INCREMENT BY 50;

-- Table Privileges
CREATE TABLE privileges
(
    privilege_id BIGINT       NOT NULL,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255),
    created_at   TIMESTAMP(6) NOT NULL,
    updated_at   TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (privilege_id),
    CONSTRAINT uk_privilege_name UNIQUE (name)
);

-- Table Roles
CREATE TABLE roles
(
    role_id     BIGINT       NOT NULL,
    name        VARCHAR(25)  NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP(6) NOT NULL,
    updated_at  TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (role_id),
    CONSTRAINT uk_role_name UNIQUE (name)
);

-- Table Users
CREATE TABLE users
(
    user_id    BIGINT       NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT uk_user_email UNIQUE (email)
);

-- Table de jointure : Roles <-> Privileges
CREATE TABLE role_privileges
(
    role_id      BIGINT NOT NULL,
    privilege_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, privilege_id),
    CONSTRAINT fk_role_priv_role FOREIGN KEY (role_id) REFERENCES roles (role_id),
    CONSTRAINT fk_role_priv_priv FOREIGN KEY (privilege_id) REFERENCES privileges (privilege_id)
);

-- Table de jointure : Users <-> Roles
CREATE TABLE users_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles (role_id)
);