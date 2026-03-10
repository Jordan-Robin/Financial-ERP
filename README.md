# 🏦 Financial ERP

> **SaaS multi-tenant pour PME** · Solution d'isolation stricte des données via une stratégie multi-schéma PostgreSQL

---

## 🏗 Architecture Technique

Le projet adopte une **Architecture Hexagonale** (Ports & Adapters) combinée aux principes du **DDD** (Domain-Driven Design) pour garantir une évolutivité maximale.

### Structure des Couches

```
┌─────────────────────────────────────────────────────────────┐
│  📡 api/          → Couche Présentation                     │
│     Contrôleurs REST utilisant des DTOs pour découpler      │
│     le contrat d'interface du modèle de données interne     │
├─────────────────────────────────────────────────────────────┤
│  💎 domain/       → Cœur Métier                             │
│     Logique métier pure, entités JPA et interfaces          │
│     de Repository (Ports)                                   │
├─────────────────────────────────────────────────────────────┤
│  🔧 infrastructure/ → Adaptateurs                           │
│     Implémentations techniques (Persistence, Security,      │
│     Intégrations externes)                                  │
├─────────────────────────────────────────────────────────────┤
│  🔗 shared/       → Composants Transverses                  │
│     Gestion globale des exceptions, utilitaires             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 MVP - Étape 1 : Socle Technique

L'objectif est de **valider la stack** sur une base de données unique avant la transition vers le multi-tenant.

| Module | Description |
|--------|-------------|
| 🔐 **Sécurité** | Authentification et contrôle d'accès via Spring Security |
| 💰 **Finance** | Module de base pour la gestion des écritures comptables |
| 💾 **Persistence** | Migration de schéma automatisée avec Flyway |
| 📚 **Documentation** | Interface interactive Swagger/OpenAPI pour le test des endpoints |

---

## ⚙️ Configuration & Lancement

### 1️⃣ Prérequis (Docker)

Lancer l'instance PostgreSQL locale :

```bash
docker-compose up -d
```

### 2️⃣ Variables d'environnement

Créer un fichier `.env` à la racine du projet :

```properties
DB_URL=jdbc:postgresql://localhost:5432/financial_erp_db
DB_USERNAME=user
DB_PASSWORD=password
```

### 3️⃣ Exécution

Lancer l'application via votre IDE ou en ligne de commande :

```bash
./mvnw spring-boot:run
```

---

## 🛠 Roadmap : Vers l'Étape 2

Le passage à l'architecture cible se fera via :

- ✅ Implémentation du **Dynamic Routing DataSource**
- ✅ Gestion de **3 environnements distincts** : `dev`, `staging`, `prod`
- ✅ Isolation physique des données par **schéma PostgreSQL dédié** pour chaque client (tenant)
