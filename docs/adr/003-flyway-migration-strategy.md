# ADR 003 — Stratégie de migration Flyway multi-schema

## Statut
Accepté

## Date
2026-03

## Contexte

L'architecture multi-schema (voir ADR 001) implique deux types de migrations :
- Les migrations du schema `public` (tables globales, stables)
- Les migrations des schemas tenant (tables métier, jouées à la création de chaque tenant
  et à chaque mise à jour)

Flyway gère nativement un seul schema par instance de configuration. Il faut définir
une stratégie pour gérer les deux types.

## Décision

### Structure des migrations

```
src/main/resources/db/
  migration/
    public/
      V1__create_users.sql
      V2__create_tenants.sql
      V3__create_memberships.sql
    tenant-template/
      V1__create_organizations.sql
      V2__create_members.sql
      V3__create_roles_and_privileges.sql
      V4__seed_default_roles.sql
```

### Exécution

- **Schema public** : Flyway configuré classiquement, s'exécute au démarrage de l'application
- **Schema tenant** : Flyway exécuté programmatiquement à chaque création de tenant,
  en pointant vers le nouveau schema et le dossier `tenant-template`

### Création d'un tenant

```
1. INSERT INTO public.tenants (slug, schema_name, ...)
2. CREATE SCHEMA tenant_{slug}
3. Flyway.configure().schemas("tenant_{slug}").locations("tenant-template").load().migrate()
4. Seed rôles templates (is_system = true)
5. Sync privilèges depuis l'enum code → base
```

### Règles

- Migrations **idempotentes** : utilisation de `IF NOT EXISTS`, `INSERT ... ON CONFLICT DO NOTHING`
- Pas de `DROP` en production — toute suppression passe par une migration de renommage ou désactivation
- Les migrations `tenant-template` doivent pouvoir être rejouées sur tous les schemas existants
  lors d'une mise à jour (script de maintenance)

### Environnements

| Environnement | Flyway | ddl-auto |
|---|---|---|
| dev | enabled, auto | validate |
| staging | enabled, auto | none |
| prod | enabled, contrôlé CI/CD | none |

## Alternatives considérées

**Liquibase** — plus flexible pour le multi-schema, mais plus verbeux (XML/YAML) et moins
répandu dans l'écosystème Spring Boot. Flyway avec configuration programmatique suffit
pour notre cas d'usage.

**ddl-auto: update** — solution initiale, abandonnée car non reproductible, dangereuse
en production et incompatible avec une gestion sérieuse des migrations.

## Conséquences

- Testcontainers est utilisé dans les tests d'intégration pour valider les migrations
  sur un PostgreSQL réel — les migrations doivent passer sur le container de test
- Toute modification de schéma passe obligatoirement par une migration Flyway, jamais
  manuellement en base
- Un script de migration pour les tenants existants doit être prévu à chaque nouvelle
  migration `tenant-template`
