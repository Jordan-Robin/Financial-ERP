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

-- Table Invitation Tokens
CREATE TABLE public.invitation_tokens
(
    id               UUID         NOT NULL,
    user_id          UUID         NOT NULL REFERENCES public.users (id),
    token_hash       VARCHAR(255) NOT NULL,
    expires_at       TIMESTAMPTZ  NOT NULL,
    used_at          TIMESTAMPTZ,
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ  NOT NULL,
    created_by       UUID,
    last_modified_by UUID,
    PRIMARY KEY (id)
);
CREATE INDEX idx_invitation_tokens_user_id ON public.invitation_tokens (user_id);