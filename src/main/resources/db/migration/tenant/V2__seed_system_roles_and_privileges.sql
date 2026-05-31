-- Privileges
INSERT INTO privileges (id, name, description)
VALUES (gen_random_uuid(), 'MEMBER_READ', 'Consulter les membres'),
       (gen_random_uuid(), 'MEMBER_INVITE', 'Inviter un membre'),
       (gen_random_uuid(), 'MEMBER_UPDATE', 'Modifier un membre'),
       (gen_random_uuid(), 'MEMBER_REMOVE', 'Retirer un membre'),
       (gen_random_uuid(), 'USER_UPDATE_PASSWORD', 'Redéfinir le mot de passe user'),
       (gen_random_uuid(), 'USER_UPDATE_STATUS', 'Mettre à jour le statut utilisateur'),
       (gen_random_uuid(), 'ROLE_READ', 'Consulter les rôles'),
       (gen_random_uuid(), 'ROLE_CREATE', 'Créer un rôle'),
       (gen_random_uuid(), 'ROLE_UPDATE', 'Modifier un rôle'),
       (gen_random_uuid(), 'ROLE_DELETE', 'Supprimer un rôle'),
       (gen_random_uuid(), 'ORGANIZATION_READ', 'Consulter l''organisation'),
       (gen_random_uuid(), 'ORGANIZATION_UPDATE', 'Modifier l''organisation'),
       (gen_random_uuid(), 'TENANT_SETTINGS_READ', 'Consulter les paramètres du tenant'),
       (gen_random_uuid(), 'TENANT_SETTINGS_UPDATE', 'Modifier les paramètres du tenant');

-- Rôle système TENANT_SUPER_ADMIN
INSERT INTO roles (id, name, description, is_system, created_at, updated_at, created_by, last_modified_by)
VALUES (gen_random_uuid(), 'TENANT_SUPER_ADMIN', 'Administration complète du tenant', true, now(), now(), null, null);

-- Attribution de tous les privileges au rôle TENANT_SUPER_ADMIN
INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id
FROM roles r,
     privileges p
WHERE r.name = 'TENANT_SUPER_ADMIN' AND p.name != 'USER_UPDATE_PASSWORD';