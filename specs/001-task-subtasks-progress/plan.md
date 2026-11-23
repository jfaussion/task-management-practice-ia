# Implementation Plan: Sous-tâches et progression des tâches

**Branch**: `001-task-subtasks-progress` | **Date**: 2026-11-20 | **Spec**: `specs/001-task-subtasks-progress/spec.md`

**Input**: Feature specification from `/specs/001-task-subtasks-progress/spec.md`

## Summary

Ajouter des sous-tâches à un seul niveau sur les tâches existantes, gérées directement depuis les cartes du kanban (création via « + », renommage via « crayon », coche « terminée », suppression avec confirmation), et afficher sur chaque carte une barre de progression (pourcentage de sous-tâches terminées, arrondi à l'entier le plus proche). Technique : nouvelle table `subtask` (Liquibase), agrégat sous-tâche côté backend hexagonal, API REST imbriquée `POST/PUT/PATCH/DELETE /api/v1/tasks/{taskId}/subtasks[...]`, composants Angular sur `task-card` + service RxJS ; la progression est dérivée à la volée, jamais stockée.

## Technical Context

**Language/Version**: Java 21 (Spring Boot, `backend/springboot`) ; TypeScript / Angular 19 standalone (`frontend`)

**Primary Dependencies**: Spring Boot (Web, Data JPA, Liquibase, Validation), Angular 19 + RxJS + Tailwind CSS 4 ; MapStruct (mappers DTO)

**Storage**: PostgreSQL (Docker local via `./scripts/setup-db.sh`), migrations Liquibase uniquement

**Testing**: JUnit via Maven (backend, `src/test`) ; Karma + Jasmine, ChromeHeadless (frontend, `*.spec.ts`) ; suite complète `./scripts/test.sh`

**Target Platform**: Web (navigateur local), API REST `/api/v1` via proxy dev `proxy.conf.json`

**Project Type**: Monorepo web (backend API + frontend SPA), pas d'e2e en place

**Performance Goals**: Mise à jour de la barre de progression immédiate côté client (SC-003, sans rechargement) ; aucune contrainte de débit spécifiée

**Constraints**: Profondeur sous-tâche = 1 niveau (refus de sous-sous-tâche) ; ≤ 50 sous-tâches par tâche ; titre ≤ 200 caractères, non vide ; pas d'authentification ; aucune dépendance tierce nouvelle

**Scale/Scope**: 2 entités impactées (task, subtask), 1 changelog Liquibase, ~6 endpoints REST, 3 composants frontend touchés (task-card, nouveau subtask-form, task service)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principe | Statut | Notes |
|----------|--------|-------|
| I. Hexagonal Architecture | PASS | `SubtaskService` dans `application/service`, DAO dans `infrastructure/jpa/dao`, contrôleur dans `infrastructure/api/controller`, modèle dans `domain/model`. Aucun contrôleur n'appelle un DAO. |
| II. Test-First | PASS | Tests backend (service : création, limite 50, validation titre, suppression en cascade) et frontend (`task-card.spec.ts`, `subtask-form.spec.ts`, service spec) écrits avec l'implémentation ; suite `./scripts/test.sh` verte exigée. |
| III. Transactional Integrity | PASS | La limite de 50 sous-tâches est vérifiée atomiquement avec l'insertion (comptage + insert dans la même transaction service). Suppression d'une tâche avec sous-tâches = une seule opération (cascade FK `ON DELETE CASCADE`). |
| IV. Schema by Migration Only | PASS | Nouveau changelog `003-add-subtasks.sql` (table `subtask`, FK vers `task` avec `ON DELETE CASCADE`), référencé dans `db.changelog-master.yaml`. Aucune édition manuelle PostgreSQL. |
| V. Simplicity & YAGNI | PASS | Aucune nouvelle dépendance ; progression dérivée calculée à la volée (pas de colonne stockée ni de job de recalcul) ; pas de réordonnancement ni de persistance de l'état replié (spéc). |

Post-design : aucun gate violé, pas de section Complexity Tracking nécessaire.

## Project Structure

### Documentation (this feature)

```text
specs/001-task-subtasks-progress/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── subtasks-api.md  # Contrat REST /api/v1/tasks/{taskId}/subtasks
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
backend/springboot/
├── src/main/java/com/neosoft/practice_software/
│   ├── application/
│   │   ├── dao/                        # SubtaskDao (port)
│   │   └── service/ (+ impl/)          # SubtaskService : CRUD + limites + validation
│   ├── domain/
│   │   ├── model/                      # Subtask (modèle domaine)
│   │   └── exception/                  # SubtaskLimitExceededException, etc.
│   └── infrastructure/
│       ├── api/
│       │   ├── controller/             # SubtaskController (REST /api/v1/tasks/{taskId}/subtasks)
│       │   ├── dto/                    # SubtaskDto, CreateSubtaskRequest, UpdateSubtitleRequest
│       │   └── mapper/                 # MapStruct domain <-> DTO
│       └── jpa/
│           ├── entity/                 # SubtaskEntity
│           ├── dao/                    # impl du port
│           ├── mapper/                 # entity <-> domain
│           └── repository/             # Spring Data SubtaskRepository
├── src/main/resources/db/changelog/
│   ├── 003-add-subtasks.sql            # table subtask + FK ON DELETE CASCADE
│   └── db.changelog-master.yaml        # inclusion 003
└── src/test/java/...                   # tests JUnit service + contrôleur

frontend/
├── src/app/
│   ├── application/service/            # subtask.service.ts (RxJS, proxy /api/v1)
│   ├── domain/model/                   # subtask.model.ts
│   └── ui/
│       ├── task-card/                  # barre de progression + miniatures + bouton replier
│       └── subtask-form/               # petit formulaire création/édition sous-tâche
└── src/app/**/*.spec.ts                # tests Karma/Jasmine
```

**Structure Decision**: Monorepo existant conservé tel quel : backend hexagonal Spring Boot + frontend Angular. La fonctionnalité s'insère dans les zones existantes (`application`, `domain`, `infrastructure` côté backend ; `ui`, `application/service`, `domain/model` côté frontend) en ajoutant une nouvelle agrégat `Subtask` et un nouveau composant `subtask-form`. Aucune nouvelle racine de projet.

## Complexity Tracking

> Vide : aucune violation de constitution à justifier.
