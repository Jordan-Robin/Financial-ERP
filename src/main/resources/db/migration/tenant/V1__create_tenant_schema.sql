-- Table Privileges
CREATE TABLE privileges
(
    id               UUID        NOT NULL,
    name             VARCHAR(50) NOT NULL,
    description      VARCHAR(255),
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ NOT NULL,
    created_by       UUID,
    last_modified_by UUID,
    PRIMARY KEY (id),
    CONSTRAINT uk_privilege_name UNIQUE (name)
);

-- Table Rôles
CREATE TABLE roles
(
    id               UUID        NOT NULL,
    name             VARCHAR(50) NOT NULL,
    description      VARCHAR(255),
    is_system        BOOLEAN     NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ NOT NULL,
    created_by       UUID,
    last_modified_by UUID,
    PRIMARY KEY (id),
    CONSTRAINT uk_role_name UNIQUE (name)
);

-- Table de jointure : Roles <-> Privileges
CREATE TABLE role_privileges
(
    role_id      UUID NOT NULL REFERENCES roles (id),
    privilege_id UUID NOT NULL REFERENCES privileges (id),
    PRIMARY KEY (role_id, privilege_id)
);
CREATE INDEX idx_role_privileges_privilege_id ON role_privileges (privilege_id);

-- Table Organizations
CREATE TABLE organizations
(
    id                   UUID         NOT NULL,
    name                 VARCHAR(100) NOT NULL,
    tenant_id            UUID         NOT NULL UNIQUE,
    legal_status         VARCHAR(50),
    siren                VARCHAR(9),
    naf_code             VARCHAR(10),
    fiscal_year_end_date VARCHAR(5),
    created_at           TIMESTAMPTZ  NOT NULL,
    updated_at           TIMESTAMPTZ  NOT NULL,
    created_by           UUID,
    last_modified_by     UUID,
    PRIMARY KEY (id)
);

-- Table Members
CREATE TABLE members
(
    id               UUID         NOT NULL,
    user_id          UUID         NOT NULL,
    first_name       VARCHAR(255) NOT NULL,
    last_name        VARCHAR(255) NOT NULL,
    organization_id  UUID         NOT NULL REFERENCES organizations (id),
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ  NOT NULL,
    created_by       UUID,
    last_modified_by UUID,
    PRIMARY KEY (id)
);
CREATE INDEX idx_members_organization_id ON members (organization_id);
CREATE INDEX idx_members_user_id ON members (user_id);

-- Table de jointure : Members <-> Roles
CREATE TABLE member_roles
(
    id               UUID        NOT NULL,
    member_id        UUID        NOT NULL REFERENCES members (id),
    role_id          UUID        NOT NULL REFERENCES roles (id),
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ NOT NULL,
    created_by       UUID,
    last_modified_by UUID,
    PRIMARY KEY (id),
    CONSTRAINT uk_member_roles_member_role UNIQUE (member_id, role_id)
);
CREATE INDEX idx_member_roles_role_id ON member_roles (role_id);