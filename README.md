# Task Manager API

API REST de gestion de tâches et d'utilisateurs, construite avec Spring Boot. Le projet met l'accent sur une architecture en couches, la validation des entrées, les migrations de base de données, la gestion des transactions et la cohérence des mises à jour concurrentes.

## Fonctionnalités

- Création et consultation d'utilisateurs.
- Création, consultation paginée, modification et suppression de tâches.
- Association d'une tâche à un utilisateur.
- Validation des requêtes et réponses d'erreur structurées.
- Persistance MySQL avec migrations Flyway.
- Cache Redis pour la lecture d'une tâche par identifiant, avec un TTL de 10 minutes.
- Détection des modifications concurrentes grâce au verrouillage optimiste JPA.
- Profils de configuration `dev`, `staging` et `prod`.
- Endpoints Actuator pour la santé, les informations et les métriques.

## Architecture

Le code suit une architecture en couches organisée par domaine (`task` et `user`) :

```text
com.ma
├── task
│   ├── controller   # Endpoints REST et codes HTTP
│   ├── dto          # Contrats d'entrée et de sortie
│   ├── entity       # Modèle JPA
│   ├── enums        # État d'une tâche
│   ├── mapper       # Conversion DTO / entité
│   ├── repository   # Accès aux données
│   └── service      # Règles métier et transactions
├── user
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── repository
│   └── service
├── common           # Configuration transverse, dont Redis
└── exception        # Exceptions métier et format d'erreur REST
```

Les contrôleurs exposent les ressources HTTP et délèguent le traitement aux services. Les DTO isolent le contrat API du modèle de persistance; les mappers assurent la conversion. Les repositories Spring Data JPA encapsulent les opérations de base de données. `open-in-view` est désactivé afin de garder le chargement des données dans les limites des transactions de service.

## API

Préfixe des routes : `/api`.

| Méthode | Route | Description |
| --- | --- | --- |
| `GET` | `/api/task` | Liste paginée des tâches (`page`, `size`, `sort`) |
| `GET` | `/api/task/{id}` | Détail d'une tâche |
| `POST` | `/api/task` | Crée une tâche associée à un utilisateur existant |
| `PUT` | `/api/task/{id}` | Modifie le titre et la description; la version est requise |
| `DELETE` | `/api/task/{id}` | Supprime une tâche |
| `POST` | `/api/users` | Crée un utilisateur |
| `GET` | `/api/users` | Liste les utilisateurs |

Exemple de création d'une tâche :

```http
POST /api/task
Content-Type: application/json
```

```json
{
  "title": "Préparer la livraison",
  "description": "Vérifier les migrations et les tests",
  "userId": 1
}
```

Les erreurs de validation renvoient `400`, une ressource absente `404` et un conflit de version `409`. Les endpoints Actuator configurés sont `/actuator/health`, `/actuator/info` et `/actuator/metrics`.

## Choix d'ingénierie

### Profils Spring

Les fichiers `application-dev.yaml`, `application-staging.yaml` et `application-prod.yaml` séparent les paramètres par environnement. En développement, Hibernate utilise `ddl-auto: update` et les requêtes SQL sont visibles. En staging et en production, `ddl-auto: validate` laisse Flyway gérer le schéma et Hibernate vérifier sa cohérence au démarrage. Activez un profil avec `SPRING_PROFILES_ACTIVE`.

### Migrations Flyway

Les scripts versionnés du dossier `src/main/resources/db/migration` créent et font évoluer le schéma : `V1__create_tasks.sql`, puis `V2__add_created_at.sql`. En staging et en production, les changements de schéma doivent être apportés par une nouvelle migration, et non par une modification rétroactive des scripts déjà appliqués.

### Transactions et concurrence

Les services portent les frontières transactionnelles. Les lectures utilisent `@Transactional(readOnly = true)` et les écritures une transaction standard. L'entité `Task` utilise `@Version`; la version reçue lors d'une modification permet de détecter une édition obsolète, retournée comme un conflit HTTP `409`.

### Cache Redis

`@Cacheable` met en cache `TaskService.findById` sous le cache `tasks`, avec l'identifiant comme clé. `CacheConfig` applique un TTL de 10 minutes et désactive la mise en cache des valeurs nulles. **L'invalidation du cache lors des mises à jour et suppressions n'est pas encore implémentée**; c'est une évolution nécessaire pour éviter de servir une valeur devenue obsolète.

### Validation et erreurs

Les DTO utilisent Jakarta Bean Validation et les contrôleurs déclenchent la validation avec `@Valid`. `GlobalExceptionHandler` centralise les réponses pour les erreurs de validation, les ressources absentes et les conflits de concurrence.

### Sécurité et secrets

**Spring Security n'est pas actuellement configuré** : les routes API ne disposent donc pas encore d'un contrôle d'accès par rôle. L'authentification JWT/Keycloak et les autorisations `USER` / `ADMIN` constituent une piste d'évolution, pas une fonctionnalité actuelle.

Ne placez jamais de vrais identifiants dans Git. Le profil dev importe actuellement `application-secrets.properties`, présent dans le dépôt; ce fichier ne doit contenir que des valeurs locales non sensibles. Avant de publier ou déployer le projet, retirez ce fichier du suivi Git et fournissez les paramètres via l'environnement ou un gestionnaire de secrets. Les profils staging et prod attendent `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST` et `REDIS_PORT`.

> **Attention pour le développement :** `application-dev.yaml` autorise actuellement `clean-on-validation-error` et ne désactive pas `clean`. Une erreur de validation Flyway peut donc entraîner un nettoyage du schéma. Ne connectez pas ce profil à des données à conserver; désactivez `clean-on-validation-error` et activez `clean-disabled` pour une configuration locale plus sûre.

## Prérequis

- Java 21
- MySQL, avec une base de données créée avant le démarrage
- Redis accessible par l'application

### Lancer en développement

Le profil dev importe `application-secrets.properties` pour `db.url`, `db.username` et `db.password`. Configurez aussi `REDIS_HOST` et `REDIS_PORT` (par exemple `localhost` et `6379`). La base attendue par la configuration fournie est `taskmanager_dev`.

Sur Windows :

```powershell
$env:SPRING_PROFILES_ACTIVE = "dev"
$env:REDIS_HOST = "localhost"
$env:REDIS_PORT = "6379"
./mvnw.cmd spring-boot:run
```

Sur Linux ou macOS :

```bash
export SPRING_PROFILES_ACTIVE=dev
export REDIS_HOST=localhost
export REDIS_PORT=6379
./mvnw spring-boot:run
```

L'API écoute sur `http://localhost:8080`.

### Exécuter les tests

```bash
./mvnw test
```

Sur Windows, utilisez `./mvnw.cmd test`. Le test actuellement présent vérifie le chargement du contexte Spring.

## Pistes d'amélioration

- Ajouter `@CacheEvict` ou une stratégie équivalente après modification et suppression d'une tâche.
- Ajouter Spring Security, l'authentification JWT/Keycloak et des autorisations par rôle.
- Ajouter des tests unitaires et d'intégration pour les endpoints, migrations, conflits optimistes et comportements du cache.
- Sécuriser les paramètres Flyway du profil dev et sortir le fichier de secrets du contrôle de version.
- Documenter le contrat HTTP avec OpenAPI/Swagger.
