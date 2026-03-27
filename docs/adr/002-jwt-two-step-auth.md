# ADR 002 — Authentification JWT en deux étapes

## Statut
Accepté

## Date
2026-03

## Contexte

Un utilisateur peut appartenir à plusieurs tenants avec des rôles différents dans chacun.
L'authentification doit permettre de résoudre le bon tenant et de charger les rôles associés
avant d'émettre un token utilisable pour les requêtes métier.

## Décision

### Authentification en deux étapes

**Étape 1 — `/api/auth/login`**

Vérification email/password dans `public.users`.

- Si l'account appartient à **un seul tenant** : retourne directement un AccessToken complet
- Si l'account appartient à **plusieurs tenants** : retourne un OnboardingToken + liste des tenants

Contenu de l'OnboardingToken :
```json
{
  "sub": "uuid-account",
  "email": "john@doe.com",
  "type": "onboarding",
  "tenants": [
    { "id": "uuid-tenant", "slug": "nike", "name": "Nike France" }
  ],
  "exp": 1234567890
}
```

**Étape 2 — `/api/auth/token`**

Échange de l'OnboardingToken contre un AccessToken tenant-scopé.

Contenu de l'AccessToken :
```json
{
  "sub": "uuid-account",
  "email": "john@doe.com",
  "tenant_id": "uuid-tenant",
  "schema_name": "tenant_nike",
  "member_id": "uuid-member",
  "roles": ["TENANT_ADMIN"],
  "is_owner": true,
  "exp": 1234567890
}
```

`schema_name` est inclus directement dans le token pour éviter un aller-retour base
à chaque requête. Il sera externalisé dans un cache Redis quand celui-ci sera introduit (Phase 7).

Les privilèges ne sont **pas** inclus dans le token car ils sont dynamiques et modifiables
par le tenant admin — les inclure entraînerait une désynchronisation en cas de modification
en cours de session. Ils sont chargés depuis la base à chaque requête (cache JVM en attendant Redis).

---

### Refresh tokens — stockage dans `public.refresh_tokens`

Les refresh tokens sont stockés dans le schema `public` et non dans chaque schema tenant.

Structure :
```
public.refresh_tokens (
  id, account_id, tenant_id, member_id,
  jti, expires_at, revoked, created_at
)
```

La révocation sur logout cible uniquement `WHERE account_id = X AND tenant_id = Y` —
les sessions du même compte sur d'autres tenants ne sont pas affectées.

#### Pourquoi `public` plutôt que le schema tenant

- **Gouvernance centralisée** : rotation, révocation, détection d'abus et reporting
  depuis un seul endroit
- **Simplicité opérationnelle** : pas N tables à auditer et monitorer
- **Conformité** : plus simple à soumettre à un audit de sécurité
- Le besoin "banni d'un tenant mais pas des autres" est couvert par le filtre `tenant_id`

---

### Mode support super-admin (X-Tenant-Override)

> ⚠️ Concept validé, implémentation à affiner avant développement.

Permet à un compte `is_super_admin` d'entrer dans le schema d'un tenant pour du support.

Principes :
- Implémenté via un **token de support dédié** avec claim `support_mode: true`
  (pas un simple header libre — risque de backdoor)
- Durée de vie courte (15 minutes maximum)
- Chaque accès audité en base : qui, quand, quel tenant, raison
- `TenantFilter` détecte `support_mode: true` et route vers le tenant cible
- Les actions sont taguées dans les logs (`support_mode: true`, `support_by: uuid`)

Points à affiner avant implémentation : durée exacte du token de support,
nécessité d'une double validation/approbation, périmètre lecture seule ou écriture,
format de l'audit log.

## Alternatives considérées

**Token unique avec tenant résolu via subdomain** (`nike.financial-erp.com`) — élégant
mais complexité infrastructure disproportionnée au stade actuel. À reconsidérer si la
roadmap prévoit des subdomains par tenant.

**Inclure les privilèges dans le token** — éviterait un aller-retour base, mais les
privilèges étant modifiables dynamiquement, le token serait rapidement désynchronisé.
Non retenu.

**Refresh tokens dans le schema tenant** — isolation maximale par tenant, mais complexité
opérationnelle élevée (N tables à monitorer, audit fragmenté). Non retenu au profit de
`public.refresh_tokens` avec filtre `tenant_id`.

## Conséquences

- Le frontend doit gérer le flow en deux étapes et stocker temporairement l'OnboardingToken
- Si un account n'a qu'un seul tenant, le flow est transparent (une seule requête)
- `CustomUserDetailsService` doit supporter les deux contextes (public pour étape 1, tenant pour étape 2)
- Les endpoints `/api/auth/**` sont publics dans `SecurityFilterChain` — la sécurité
  repose sur la validation des tokens, pas sur Spring Security
- La table `public.refresh_tokens` doit être indexée sur `jti`, `account_id`, et `tenant_id`