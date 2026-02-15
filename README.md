🏦 Financial ERP
SaaS multi-tenant pour PME > Solution d'isolation stricte des données via une stratégie multi-schéma PostgreSQL.

🏗 Architecture Technique
Le projet adopte une Architecture Hexagonale (Ports & Adapters) combinée aux principes du DDD (Domain-Driven Design) pour garantir une évolutivité maximale.

api/ (Couche Présentation) : Contrôleurs REST utilisant des DTOs pour découpler le contrat d'interface du modèle de données interne.

domain/ (Cœur Métier) : Contient la logique métier pure, les entités JPA et les interfaces de Repository (Ports).

infrastructure/ (Adaptateurs) : Implémentations techniques (Persistence, Security, Intégrations externes).

shared/ : Composants transverses (Gestion globale des exceptions, utilitaires).

🎯 MVP - Étape 1 : Socle Technique
L'objectif actuel est de valider la stack sur une base de données unique avant la transition vers le multi-tenant.

Sécurité : Authentification et contrôle d'accès via Spring Security.

Finance : Module de base pour la gestion des écritures comptables.

Persistence : Migration de schéma automatisée avec Flyway.

Documentation : Interface interactive Swagger/OpenAPI pour le test des endpoints.

⚙️ Configuration & Lancement
1. Prérequis (Docker)
   Lancer l'instance PostgreSQL locale :

Bash
docker-compose up -d
2. Variables d'environnement
   Créer un fichier .env à la racine du projet :

Extrait de code
DB_URL=jdbc:postgresql://localhost:5432/financial_erp_db
DB_USERNAME=user
DB_PASSWORD=password
3. Exécution
   Lancer l'application via votre IDE ou en ligne de commande :

Bash
./mvnw spring-boot:run
🛠 Roadmap : Vers l'Étape 2
Le passage à l'architecture cible se fera via :

L'implémentation du Dynamic Routing DataSource.

La gestion de 3 environnements distincts : dev, staging, prod.

L'isolation physique des données par schéma PostgreSQL dédié pour chaque client (tenant).