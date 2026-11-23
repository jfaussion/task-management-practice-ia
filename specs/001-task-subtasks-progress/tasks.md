# Tasks: Sous-tâches et progression des tâches

**Input**: Design documents from `/specs/001-task-subtasks-progress/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/, quickstart.md

**Tests**: Inclus. La constitution (`.specify/memory/constitution.md`, Principe II Test-First, NON-NÉGOCIABLE) impose des tests écrits en premier et verts avant toute fin de tâche. Chaque story porte donc ses tâches de test.

**Organization**: Tasks grouped by user story (US1 = CRUD sous-tâches P1, US2 = coche + barre de progression P2) pour implémentation et validation indépendantes.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2)
- Include exact file paths in descriptions

## Path Conventions

- **Web app (monorepo existant)** : `backend/springboot/src/`, `frontend/src/` — conformes à plan.md § Project Structure.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Environnement prêt, branche et base à jour.

- [ ] T001 Vérifier branche `feature/001-task-subtasks-progress`, réinitialiser la base avec `./scripts/setup-db.sh` et lancer `./scripts/run.sh` pour valider l'état de départ (kanban existant fonctionnel, aucun sous-tâche)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Schéma, agrégat `Subtask` et exposition des sous-tâches dans les payloads Task — requis par US1 ET US2.

⚠️ **CRITICAL**: No user story work can begin until this phase is complete

- [ ] T002 Créer le changelog Liquibase `backend/springboot/src/main/resources/db/changelog/003-add-subtasks.sql` : table `subtask` avec `Subtask.id` BIGSERIAL PK, `Subtask.task_id` BIGINT NOT NULL FK→`task(id) ON DELETE CASCADE` (suppression tâche ⇒ sous-tâches supprimées, data-model.md), `Subtask.title` VARCHAR(200) NOT NULL (non vide, ≤ 200 caractères, doublons autorisés — data-model.md), `Subtask.done` BOOLEAN NOT NULL DEFAULT FALSE (data-model.md), `Subtask.created_at` TIMESTAMPTZ NOT NULL DEFAULT now() (ordre d'affichage `created_at, id` — data-model.md) ; index sur `task_id` ; l'inclure dans `db.changelog-master.yaml`
- [ ] T003 [P] Créer le modèle domaine `Subtask` dans `backend/springboot/src/main/java/com/neosoft/practice_software/domain/model/` avec les champs et règles de data-model.md (pas de `parent_subtask_id` : la profondeur 1 est structurelle, FR-002)
- [ ] T004 [P] Créer `SubtaskEntity` dans `backend/springboot/src/main/java/com/neosoft/practice_software/infrastructure/jpa/entity/` et `SubtaskRepository` dans `infrastructure/jpa/repository/` (Spring Data, mapping FK cascade de T002)
- [ ] T005 Créer le port `SubtaskDao` dans `backend/springboot/src/main/java/com/neosoft/practice_software/application/dao/`, son impl JPA dans `infrastructure/jpa/dao/` et le mapper entity↔domain dans `infrastructure/jpa/mapper/` (hexagonal : service ne voit jamais l'entity)
- [ ] T006 [P] Créer les DTO dans `backend/springboot/src/main/java/com/neosoft/practice_software/infrastructure/api/dto/` (`SubtaskDto`, `CreateSubtaskRequest` avec `@NotBlank @Size(max=200)` sur le titre, `UpdateSubtaskTitleRequest`, `SetSubtaskDoneRequest`) et les mappers MapStruct dans `infrastructure/api/mapper/` conformes à contracts/subtasks-api.md
- [ ] T007 Créer les exceptions domaine dans `backend/springboot/src/main/java/com/neosoft/practice_software/domain/exception/` (`SubtaskNotFoundException`, `SubtaskLimitExceededException`, `SubtaskValidationException`) et leurs mappings dans le `@ControllerAdvice` existant vers 404 / 409 / 400 avec le format d'erreur JSON standardisé (contracts/subtasks-api.md § Erreurs)
- [ ] T008 Enrichir le payload des tâches : `GET /api/v1/tasks` et `GET /api/v1/tasks/{id}` incluent `subtasks` triées par `Subtask.created_at` puis `Subtask.id` (ordre de création, data-model.md) — mapper DTO + service de tâche dans `infrastructure/api/`

**Checkpoint**: Schéma migré, agrégat et payloads prêts — US1 et US2 peuvent démarrer.

---

## Phase 3: User Story 1 - Décomposer une tâche en sous-tâches (Priority: P1) 🎯 MVP

**Goal**: Créer/renommer/supprimer des sous-tâches (1 niveau max, ≤ 50 par tâche, titre non vide ≤ 200) directement depuis la carte kanban, avec miniatures et repliage.

**Independent Test**: Créer une tâche, ajouter/modifier/supprimer plusieurs sous-tâches sur sa carte, vérifier qu'ajouter une sous-tâche à une sous-tâche est impossible (quickstart.md § S1, S3, S4).

### Tests for User Story 1 (constitution : Test-First, écrire d'abord et voir échouer) ⚠️

- [ ] T009 [P] [US1] Écrire `SubtaskServiceTest` dans `backend/springboot/src/test/java/com/neosoft/practice_software/application/service/` : création (201, `done=false` par défaut), renommage, suppression sans toucher la tâche, refus titre vide/après trim et > 200 caractères (400), refus 51e sous-tâche (409, limite 50 atomique), cascade suppression tâche ⇒ sous-tâches, contraintes de `Subtask.title` et `Task.subtasks` (0..50) citées depuis data-model.md
- [ ] T010 [P] [US1] Écrire `SubtaskControllerTest` dans `backend/springboot/src/test/java/com/neosoft/practice_software/infrastructure/api/controller/` : codes 201/200/204/400/404/409 de contracts/subtasks-api.md, 404 quand la tâche parente n'existe pas (formulaire ouvert pendant suppression parente), absence totale de route permettant d'attacher une sous-tâche à une sous-tâche (FR-002/SC-004)
- [ ] T011 [P] [US1] Écrire les specs frontend dans `frontend/src/app/` : `application/service/subtask.service.spec.ts` (appels via proxy, erreurs → message sans changement d'état), `ui/subtask-form/subtask-form.spec.ts` (création, édition pré-remplie, messages titre vide/>200), `ui/task-card/task-card.spec.ts` (miniatures, boutons crayon/poubelle + confirmation, replier/déplier, auto-dépli à l'ajout, état replié non persisté)

### Implementation for User Story 1

- [ ] T012 [US1] Implémenter `SubtaskService` (+ impl) dans `backend/springboot/src/main/java/com/neosoft/practice_software/application/service/` : create (trim titre, validation `Subtask.title` non vide ≤ 200 de data-model.md, comptage + insertion dans la même transaction pour la limite de 50 sur `Task.subtasks` — data-model.md), rename, delete, list triée `Subtask.created_at, Subtask.id` (data-model.md)
- [ ] T013 [US1] Implémenter `SubtaskController` dans `backend/springboot/src/main/java/com/neosoft/practice_software/infrastructure/api/controller/` : `GET/POST /api/v1/tasks/{taskId}/subtasks`, `PUT /api/v1/tasks/{taskId}/subtasks/{subtaskId}`, `DELETE /api/v1/tasks/{taskId}/subtasks/{subtaskId}` exactement selon contracts/subtasks-api.md
- [ ] T014 [P] [US1] Créer `frontend/src/app/domain/model/subtask.model.ts` et `frontend/src/app/application/service/subtask.service.ts` (RxJS, proxy `/api/v1`, gestion d'erreur : message clair + état antérieur conservé, pas d'optimistic update — research.md D4)
- [ ] T015 [US1] Créer le composant `frontend/src/app/ui/subtask-form/` : petit formulaire création/édition réutilisé par le crayon (pré-rempli), validation miroir titre vide/> 200 avec messages clairs, reste ouvert avec erreur générique si la tâche parente disparaît (research.md D7)
- [ ] T016 [US1] Compléter `frontend/src/app/ui/task-card/` : bouton « + » ouvrant subtask-form, miniatures avec case à cocher affichée (état visuel seul, câblage PATCH en US2), icône crayon, icône poubelle avec boîte de confirmation avant suppression (FR-004), bouton replier/déplier (déplié par défaut, non persisté — FR-010), auto-dépli à l'ajout

**Checkpoint**: User Story 1 entièrement fonctionnelle et testable seule : CRUD sous-tâches depuis le kanban, limites et erreurs en place, suite `./scripts/test.sh` verte. Aucune barre de progression encore.

---

## Phase 4: User Story 2 - Suivre la progression via la barre sur la carte (Priority: P2)

**Goal**: Cocher/décocher chaque sous-tâche depuis sa miniature et afficher sur la carte une barre + pourcentage (arrondi entier le plus proche) recalculés immédiatement à chaque changement.

**Independent Test**: Sur une tâche avec sous-tâches (US1), cocher/décocher/supprimer et vérifier la barre : 0 %, 50 %, 100 %, 75 %, 33 % pour 1/3 (quickstart.md § S2, S4, S6).

### Tests for User Story 2 (constitution : Test-First, écrire d'abord et voir échouer) ⚠️

- [ ] T017 [P] [US2] Étendre `SubtaskServiceTest` et `SubtaskControllerTest` (`backend/springboot/src/test/java/...`) : toggle `Subtask.done` true↔false (data-model.md), `PATCH /api/v1/tasks/{taskId}/subtasks/{id}/done` → 200/400 (body non booléen)/404 selon contracts/subtasks-api.md
- [ ] T018 [P] [US2] Étendre `frontend/src/app/ui/task-card/task-card.spec.ts` : barre absente sans sous-tâche (FR-009), 0 % / 50 % / 100 % / 75 %, arrondi 1/3 → 33 %, recalcul immédiat à l'ajout/coche/décoche/suppression (FR-008, SC-003), position entre champs de la tâche et liste des miniatures (FR-007), cas d'une unique sous-tâche 0 %↔100 %

### Implementation for User Story 2

- [ ] T019 [US2] Ajouter `toggleDone` dans `SubtaskService` (`backend/springboot/src/main/java/com/neosoft/practice_software/application/service/`) et `PATCH /api/v1/tasks/{taskId}/subtasks/{subtaskId}/done` dans `SubtaskController` (body `{ "done": bool }` — contracts/subtasks-api.md)
- [ ] T020 [US2] Câbler la case à cocher des miniatures au PATCH via `frontend/src/app/application/service/subtask.service.ts` et implémenter dans `frontend/src/app/ui/task-card/` la barre de progression Tailwind (fraction `done/total`, pourcentage arrondi à l'entier le plus proche, mise à jour à chaque add/toggle/delete — FR-006/FR-007/FR-008)

**Checkpoint**: US1 + US2 fonctionnelles ensemble : progression visible et exacte sur chaque carte, suite `./scripts/test.sh` verte.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Documentation, validation de bout en bout.

- [ ] T021 [P] Mettre à jour `docs/Architecture/url-api-structure.md` (endpoints sous-tâches) et `docs/Architecture/db-design.md` (table `subtask`, FK cascade) conformément au schéma réellement migré
- [ ] T022 [P] Mettre à jour la mémoire projet `aidd_docs/memory/api.md`, `database.md` et `design.md` (ressource Subtask, composant subtask-form, barre de progression)
- [ ] T023 Exécuter la validation manuelle de `specs/001-task-subtasks-progress/quickstart.md` § S1–S5 dans le navigateur et corriger tout écart
- [ ] T024 Lancer `./scripts/test.sh` (suite complète backend + frontend) : doit être verte avant fin de feature

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: aucune dépendance, immédiat
- **Foundational (Phase 2)**: dépend de T001 — BLOQUE US1 et US2
- **US1 (Phase 3)**: dépend de la Phase 2 uniquement ; aucun couplage avec US2
- **US2 (Phase 4)**: dépend de la Phase 2 ; réutilise les miniatures et le service créés en US1 (T014, T016) mais reste testable indépendamment sur des sous-tâches créées via US1
- **Polish (Phase 5)**: dépend des stories retenues complètes

### User Story Dependencies

- **US1 (P1)**: démarre dès la Phase 2. MVP de la fonctionnalité.
- **US2 (P2)**: démarre après US1 (la case à cocher et les miniatures de T016 sont son point d'ancrage). Son test indépendant suppose des sous-tâches existantes.

### Within Each User Story

- Tests d'abord (T009–T011, T017–T018), échouants, puis implémentation
- Backend (service → contrôleur) avant frontend pour chaque endpoint
- Story complète et verte avant de passer à la suivante

### Parallel Opportunities

- Phase 2 : T003, T004, T006 en parallèle (fichiers distincts)
- US1 : T009, T010, T011 (trois suites de tests) en parallèle ; T014 en parallèle du backend T012–T013
- US2 : T017 et T018 en parallèle
- Phase 5 : T021 et T022 en parallèle

---

## Parallel Example: User Story 1

```text
# Tests d'abord, ensemble :
Task: "SubtaskServiceTest in backend/springboot/src/test/java/.../application/service/"
Task: "SubtaskControllerTest in backend/springboot/src/test/java/.../infrastructure/api/controller/"
Task: "specs frontend subtask.service / subtask-form / task-card in frontend/src/app/"

# Backend et frontend en parallèle une fois les tests échouants :
Task: "SubtaskService + SubtaskController (backend)"
Task: "subtask.model.ts + subtask.service.ts (frontend)"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Phase 1 : Setup
2. Phase 2 : Foundational (CRITICAL — bloque tout)
3. Phase 3 : US1 → STOP et valider indépendamment (quickstart § S1, S3, S4)
4. Livrable déjà utile : décomposition des tâches sans progression

### Incremental Delivery

1. Setup + Foundational → fondation prête
2. US1 → valider (MVP)
3. US2 → valider (progression complète)
4. Polish → docs à jour + quickstart § S1–S5 + suite verte

### Parallel Team Strategy

Après la Phase 2 : un agent sur US1 backend (T009, T010, T012, T013), un sur US1 frontend (T011, T014–T016) ; US2 enchaîne après intégration US1.

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Tests écrits en premier et échouants avant implémentation (Constitution II)
- Commits conventionnels par tâche ou groupe logique — SEULEMENT sur demande explicite de l'utilisateur (Constitution, Development Workflow)
- Stop at any checkpoint to validate story independently
- Champs contraints cités depuis `specs/001-task-subtasks-progress/data-model.md` : `Subtask.task_id`, `Subtask.title`, `Subtask.done`, `Subtask.created_at`, `Task.subtasks` (0..50) — T002, T003, T009, T012, T017, T018

---

## Phase 6: Convergence

- [ ] T025 Documenter les endpoints sous-tâches dans `docs/Architecture/url-api-structure.md` et la table `subtask` (UUID PK, FK `tasks(id) ON DELETE CASCADE`, index `task_id`) dans `docs/Architecture/db-design.md`, conformément au schéma réellement migré — T021 (missing)
- [ ] T026 Mettre à jour la mémoire projet `aidd_docs/memory/api.md`, `database.md` et `design.md` (ressource Subtask, composant subtask-form, barre de progression dérivée sur task-card) — T022 (missing)
- [ ] T027 Exécuter la validation manuelle de `specs/001-task-subtasks-progress/quickstart.md` § S1–S5 dans le navigateur (création, coche + barre 0/50/100/75 %, arrondi 1/3 → 33 %, erreurs et confirmation de suppression) et corriger tout écart — T023 (missing)
