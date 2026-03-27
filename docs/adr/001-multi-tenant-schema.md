# ADR 001 — Architecture multi-tenant par schema PostgreSQL

## Statut
Accepté

## Date
2026-03

## Contexte

financial-erp est un SaaS B2B destiné à des PME, associations et collectivités. Chaque client
(tenant) doit avoir ses données strictement isolées des autres pour des raisons de sécurité,
de conformité RGPD et d'audit financier.

Trois stratégies d'isolation multi-tenant existent :
- **Colonne discriminante** : toutes les tables partagées, une colonne `tenant_id` filtre les données
- **Schema par tenant** : un schema PostgreSQL dédié par tenant, tables séparées
- **Base de données par tenant** : une instance PostgreSQL par tenant

## Décision

Adoption du **multi-schema PostgreSQL** :
- Un schema `public` pour les données globales (comptes, tenants, memberships)
- Un schema `tenant_{slug}` par client pour toutes les données métier

### Structure du schema public

```
public.users         (id, email, password_hash, status, is_super_admin, created_at)
public.tenants       (id, slug, schema_name, plan, status, created_at)
public.memberships   (account_id, tenant_id, is_owner)
```

### Structure d'un schema tenant

```
tenant_x.members           (id, account_id, first_name, last_name, organization_id)
tenant_x.organizations     (id, name, siren, legal_status, naf_code, fiscal_year_end_date)
tenant_x.roles             (id, name, is_system)
tenant_x.privileges        (id, code)
tenant_x.role_privileges   (role_id, privilege_id)
tenant_x.member_roles      (member_id, role_id)
tenant_x.refresh_tokens    (id, token, member_id, expires_at, revoked)
```

### Routing runtime

Le schema courant est résolu à chaque requête via :
1. Extraction de `schema_name` depuis le JWT
2. Positionnement dans `TenantContext` (ThreadLocal) par le `TenantFilter`
3. Hibernate route toutes les requêtes JPA vers ce schema via `CurrentTenantIdentifierResolver`

## Alternatives considérées

**Colonne discriminante** — plus simple à implémenter, mais risque élevé de fuite de données
par oubli d'un filtre `WHERE tenant_id = ?`. Non adapté à un ERP financier où l'isolation
est une exigence forte.

**Base par tenant** — isolation maximale, mais coût opérationnel prohibitif (une instance
PostgreSQL par client) et complexité infrastructure disproportionnée pour une PME SaaS.

## Conséquences

- Les migrations Flyway doivent être jouées sur chaque schema tenant à la création et à chaque
  mise à jour — nécessite une stratégie de migration multi-schema
- `schema_name` est inclus dans le JWT pour éviter un aller-retour base à chaque requête
  (sera externalisé dans Redis quand ce dernier sera introduit)
- Toute requête métier sans `TenantContext` valide doit lever une exception explicite
- Les tests d'intégration utilisent Testcontainers avec un PostgreSQL réel pour valider
  le routing multi-schema
