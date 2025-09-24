# 📜 Règles de Développement – Projet Java Spring Boot avec IA

## 🏗️ Structure du Projet

- Architecture : **Clean Architecture**
  - `domain/` → Entités métiers
  - `application/` → Services métiers + DAO (interfaces)
  - `infrastructure/` → Implémentations JPA + API REST
  - `adapters/` → Contrôleurs REST (exposition)

- Convention de nommage :
  - Classes : `PascalCase`
  - Variables/méthodes : `camelCase`
  - Fichiers : respect du nom de la classe
  - Routes API : `kebab-case` en anglais (`/api/v1/tasks`)

## ⚙️ Versions & Dépendances

- **Java** : 21
- **Spring Boot** : 3.x
- **ORM** : Spring Data JPA + Hibernate
- **Mapper** : MapStruct
- **Validation** : Jakarta Validation (`@Valid`, `@NotNull`, etc.)
- **BDD** : PostgreSQL (dev) / H2 (tests)
- **Migration BDD** : Liquibase
- **Tests** :
  - JUnit 5
  - Mockito
- **Build Tool** : Maven ou Gradle (selon projet)

## 📁 Arborescence type

```bash
src/
└── main/
    ├── java/com/example/project/
    │   ├── domain/
    │   ├── application/
    │   │   ├── service/
    │   │   └── dao/
    │   ├── infrastructure/
    │   │   ├── api/         # Controllers
    │   │   └── jpa/         # Repositories
    └── resources/
        ├── application.yml
        └── db/changelog/   # Scripts Liquibase

scripts/
├── run.sh
└── setup-db.sh
```

## 🧪 Tests

- Tests unitaires systématiques avec **JUnit** + **Mockito**
- **TDD** obligatoire sur les fonctionnalités clés (ex. assignation de tâches)
- Utilisation de `@SpringBootTest` uniquement pour les tests d’intégration
- Couverture minimale attendue : 80% sur le domaine et les services
- **Nommage** : NomClasseTest.java (ex. TaskServiceTest.java)
- **Organisation** :
  - Tests unitaires dans `src/test/java`
  - Fixtures injectés avec Liquibase (`001-init-users.sql`, etc.)



## 📌 Règles de développement Java / Spring

- Respecter les principes **SOLID**
- Utiliser `@Transactional` pour gérer les traitements critiques
- Centraliser la gestion des erreurs avec @ControllerAdvice => dans `GlobalExceptionHandler.java`
- Mapping DTOs ↔ Entités via MapStruct (componentModel = "spring")
- DTOs nommés avec suffixe Dto (ex : TaskDto)
- Utiliser des enums côté Java pour les statuts (pas contraints en BDD)
- Utiliser le type `var` pour les variables locales
- Limiter au maximum la javadoc. Ne pas en écrire lorsque la signature est explicite

## 🗄️ Base de données

- PostgreSQL via Docker en local
- H2 en mémoire pour les tests
- Génération automatique (ddl-auto=update) uniquement en local
- Versioning via Liquibase

## 🌐 API REST – Design

- Versioning activé : préfixe /api/v1
- Convention :
  - **GET** `/api/v1/tasks` – liste paginée
  - **GET** `/api/v1/tasks/{id}` – détail
  - **POST** `/api/v1/tasks` – création
  - **PUT** `/api/v1/tasks/{id}` – mise à jour
  - **DELETE** `/api/v1/tasks/{id}` – suppression
  - **GET** `/api/v1/users/{id}/tasks` – tâches d’un utilisateur
  - **GET** `/api/v1/tasks/{id}/estimate` – estimation de tâche
- Format JSON pour les erreurs, avec champ errorId


## 🧼 Style et Clean Code

- Limiter les méthodes à 50 lignes max
- Utiliser des noms explicites : assignTaskToUser, closeTaskWithCheck, etc.
- Aucun System.out.println autorisé → utiliser un logger
- Pas de logique métier dans les contrôleurs
- Services = 1 responsabilité métier principale
- Préférer les DTOs légers côté API
- Utiliser Lombok par défaut