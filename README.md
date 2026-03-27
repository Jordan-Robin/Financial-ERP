# Financial ERP

Backend Spring Boot d'un ERP financier orienté SaaS, construit pour démontrer des pratiques d'architecture, de securite et de qualite logicielle proches d'un contexte production.

## Vue rapide

### Schema 1 - Architecture backend

```text
Client HTTP
    |
    v
Auth/User/Organization Controllers (api)
    |
    v
Services metier (domain)
    |
    v
Ports (interfaces repository)
    |
    v
Adapters infrastructure (JPA, Security, Config)
    |
    v
PostgreSQL + Flyway
```

### Tableau 1 - Composants et preuves techniques

| Composant | Responsabilite | Preuve dans le code |
| --- | --- | --- |
| Security | AuthN/AuthZ JWT + method security | `src/main/java/com/jordanrobin/financial_erp/infrastructure/security/SecurityConfig.java` |
| Auth API | Login + refresh token | `src/main/java/com/jordanrobin/financial_erp/api/auth/AuthController.java` |
| Token service | Generation/validation des tokens | `src/main/java/com/jordanrobin/financial_erp/domain/auth/token/TokenService.java` |
| Flyway strategy | Decisions de migration (public + tenant) | `docs/adr/003-flyway-migration-strategy.md` |
| Multi-tenant ADR | Isolement par schema PostgreSQL | `docs/adr/001-multi-tenant-schema.md` |
| Qualite | Tests auth, securite, services | `src/test/java/com/jordanrobin/financial_erp/api/auth/AuthControllerTest.java` |

## Ce que ce projet demontre

- Conception backend modulaire avec architecture hexagonale (Ports/Adapters) et separation claire des responsabilites.
- Securite applicative avec Spring Security, JWT RSA et controle d'acces par roles/privileges.
- Migrations de base de donnees versionnees avec Flyway, en preparation d'un mode multi-tenant multi-schema.
- Approche testable (unitaires + tests d'integration) avec base PostgreSQL reelle via Testcontainers.
- Documentation d'API avec OpenAPI/Swagger pour faciliter l'integration front/back et les tests manuels.

## Architecture

Le code suit une structure inspiree DDD + Hexagonal:

- `api/` : controllers REST, DTO, mapping API.
- `domain/` : logique metier, entites, services, ports (interfaces repository).
- `infrastructure/` : implementations techniques (persistence, securite, configuration).
- `shared/` : composants transverses (erreurs, utilitaires, conventions communes).

Cette separation permet d'evoluer vers une architecture SaaS multi-tenant sans coupler la logique metier aux details techniques.

## Securite

- Authentification JWT avec signatures RSA (clés dans `src/main/resources/certs/` en local).
- `Spring Security` + `@EnableMethodSecurity` pour proteger les endpoints au niveau HTTP et metier.
- Initialisation des roles/privileges et tests dedies a la chaine d'authentification.
- Base de travail pour un flow cible multi-tenant: selection de tenant et token scope par tenant.

## Persistence et data model

- PostgreSQL comme base principale.
- Flyway pour versionner le schema et garantir des deploiements reproductibles.
- ADRs techniques dans `docs/adr/` pour tracer les decisions structurantes:
  - `docs/adr/001-multi-tenant-schema.md`
  - `docs/adr/002-jwt-two-step-auth.md`
  - `docs/adr/003-flyway-migration-strategy.md`

## Qualite logicielle

- Tests unitaires sur les services metier et la securite.
- Tests controller/API pour verifier les contrats HTTP et les cas d'autorisation.
- Tests d'integration sur PostgreSQL containerise pour reduire l'ecart entre local et CI/CD.

## Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA + Hibernate
- PostgreSQL
- Flyway
- Testcontainers
- Maven
- OpenAPI / Swagger

## Lancer le projet en local

### 1) Demarrer PostgreSQL

```bash
docker-compose up -d
```

### 2) Configurer les variables d'environnement

Créer un fichier `.env` a la racine:

```properties
DB_URL=jdbc:postgresql://localhost:5432/financial_erp_db
DB_USERNAME=user
DB_PASSWORD=password
```

### 3) Lancer l'application

```bash
./mvnw spring-boot:run
```

### 4) Lancer les tests

```bash
./mvnw test
```

## Documentation API

Une fois l'application demarree, la documentation interactive est accessible via Swagger UI (selon la configuration active).

---

Projet en evolution continue, avec un objectif clair: converger vers une plateforme SaaS multi-tenant robuste, sécurisée et exploitable en environnement de production.
