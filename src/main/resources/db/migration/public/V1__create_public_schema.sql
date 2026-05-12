-- Table Users
CREATE TABLE public.users
(
    id               UUID         NOT NULL,
    email            VARCHAR(100) NOT NULL,
    password_hash    VARCHAR(255),
    status           VARCHAR(50)  NOT NULL,
    is_super_admin   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ  NOT NULL,
    created_by       UUID,
    last_modified_by UUID,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_email UNIQUE (email)
);

-- Table Tenants
CREATE TABLE public.tenants
(
    id               UUID         NOT NULL,
    slug             VARCHAR(255) NOT NULL,
    schema_name      VARCHAR(255) NOT NULL,
    status           VARCHAR(50)  NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ  NOT NULL,
    created_by       UUID,
    last_modified_by UUID,
    PRIMARY KEY (id),
    CONSTRAINT uk_tenants_slug UNIQUE (slug),
    CONSTRAINT uk_tenants_schema_name UNIQUE (schema_name)
);

-- Table de jointure : Users <-> Tenants
CREATE TABLE public.memberships
(
    id               UUID        NOT NULL,
    user_id          UUID        NOT NULL REFERENCES public.users (id),
    tenant_id        UUID        NOT NULL REFERENCES public.tenants (id),
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ NOT NULL,
    created_by       UUID,
    last_modified_by UUID,
    PRIMARY KEY (id),
    CONSTRAINT uk_memberships_user_tenant UNIQUE (user_id, tenant_id)
);
CREATE INDEX idx_memberships_user_id ON public.memberships (user_id);
CREATE INDEX idx_memberships_tenant_id ON public.memberships (tenant_id);

-- Table Refresh Tokens
CREATE TABLE public.refresh_tokens
(
    id         UUID         NOT NULL,
    user_id    UUID         NOT NULL REFERENCES public.users (id),
    tenant_id  UUID         NOT NULL REFERENCES public.tenants (id),
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ  NOT NULL,
    revoked_at TIMESTAMPTZ,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_refresh_tokens_active
    ON public.refresh_tokens (user_id, tenant_id) WHERE revoked_at IS NULL;
CREATE INDEX idx_refresh_tokens_user_id ON public.refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_tenant_id ON public.refresh_tokens (tenant_id);

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