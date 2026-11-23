# Research: Sous-tâches et progression des tâches

Feature: `specs/001-task-subtasks-progress/spec.md` — Phase 0 (`/speckit.plan`)

Aucun point NEEDS CLARIFICATION subsiste dans le contexte technique : la stack est fixée par la constitution (Spring Boot / Angular / PostgreSQL / Liquibase) et toutes les ambiguïtés produit ont été levées dans la section Clarifications de la spec. Les décisions suivantes couvrent les choix de conception restants.

## D1. Progression : dérivée à la volée vs stockée

**Decision**: La progression n'est PAS stockée. Le pourcentage est calculé à la demande : ratio `sous-tâches terminées / total`, arrondi à l'entier le plus proche (`Math.round` côté frontend ; formule identique si un jour un endpoint l'expose).

**Rationale**: Source de vérité unique = état `done` des sous-tâches. Aucune colonne dérivée à maintenir, aucun risque d'incohérence, aucun recalcul batch. FR-006/FR-008 et SC-002/SC-003 sont satisfaits par simple recalcul à chaque changement.

**Alternatives considered**: Colonne `progress` sur `task` mise à jour transactionnellement à chaque changement de sous-tâche — rejetée : état dérivée redondant, complexité transactionnelle supplémentaire (viol YAGNI), bénéfice nul à cette échelle.

## D2. Schéma `subtask` et ordre d'affichage

**Decision**: Table `subtask` : `id BIGSERIAL PK`, `task_id BIGINT NOT NULL` (FK → `task(id)` `ON DELETE CASCADE`), `title VARCHAR(200) NOT NULL`, `done BOOLEAN NOT NULL DEFAULT FALSE`, `created_at TIMESTAMPTZ NOT NULL DEFAULT now()`. Ordre d'affichage : `ORDER BY created_at, id` (ordre de création, pas de réordonnancement — spec).

**Rationale**: `ON DELETE CASCADE` garantit « supprimer une tâche supprime ses sous-tâches » en une opération atomique (Constitution III). Le couple `(created_at, id)` donne un ordre de création stable et total. Un seul niveau : `subtask` n'a PAS de colonne `parent_subtask_id` — l'absence de la colonne rend structurellement impossible une sous-sous-tâche (FR-002/SC-004).

**Alternatives considered**: Colonne `position` entière pour réordonnancement — rejetée (pas de réordonnancement dans la spec) ; `parent_subtask_id` nullable pour arbre générique — rejetée (profondeur fixée à 1, YAGNI) ; suppression applicative des sous-tâches — rejetée (moins sûr qu'un cascade FK).

## D3. Forme de l'API

**Decision**: Ressource imbriquée sous la tâche, conforme aux conventions `docs/Architecture/url-api-structure.md` (préfixe `/api/v1`, path parameters) :

- `GET    /api/v1/tasks/{taskId}/subtasks` — liste (incluse dans GET task aussi, pour la carte)
- `POST   /api/v1/tasks/{taskId}/subtasks` — création
- `PUT    /api/v1/tasks/{taskId}/subtasks/{id}` — renommage (titre)
- `PATCH  /api/v1/tasks/{taskId}/subtasks/{id}/done` — coche terminée / non terminée (body `{ "done": bool }`)
- `DELETE /api/v1/tasks/{taskId}/subtasks/{id}` — suppression

**Rationale**: L'imbrication encode l'appartenance à exactement une tâche (spec : pas de sous-tâche partagée) et rend l'ajout à une sous-tâche impossible (aucune route `/subtasks/{id}/subtasks`). Codes d'erreur : 400 validation (titre vide/>200), 404 tâche ou sous-tâche introuvable (incl. parent supprimé pendant formulaire ouvert → erreur générique), 409 limite de 50 atteinte (FR-012). Format d'erreur JSON standardisé du `@ControllerAdvice` existant.

**Alternatives considered**: Routes plates `/api/v1/subtasks` avec `taskId` dans le body — rejetée (moins explicite, autoriserait des sous-tâches orphelines) ; PATCH complet sur `/subtasks/{id}` pour titre + done — rejeté en faveur d'une route dédiée `done` : l'opération coche est la plus fréquente et mérite une sémantique étroite.

## D4. Gestion d'état frontend et mise à jour de la barre

**Decision**: `SubtaskService` Angular (zone `application/service`, RxJS) appelle le backend via le proxy. Les sous-tâches sont chargées avec la tâche (payload du kanban). La barre de progression est un calcul pur dans `task-card` à partir de la liste de sous-tâches en mémoire ; pas d'optimistic update : l'UI attend la réponse serveur, en cas d'erreur un message s'affiche et la liste reste dans son état antérieur (spéc, échec serveur).

**Rationale**: SC-003 (mise à jour immédiate) est satisfait par le re-render Angular sur changement d'état local après succès. Le non-optimistic satisfait trivialement « l'affichage reste dans l'état antérieur » sans logique de rollback (YAGNI).

**Alternatives considered**: Optimistic update + rollback — rejeté (complexité, rollback redondant) ; store global type NgRx/signals store — rejeté (aucun besoin de partage cross-composant au-delà de la carte).

## D5. Composants frontend

**Decision**: Réutiliser la zone `ui` existante : `task-card` héberge la barre de progression, le bouton replier/déplier et les miniatures de sous-tâches ; nouveau composant `subtask-form` (petit formulaire création/édition, pré-remplissable, réutilisé par le « crayon »). L'état replié est un état de composant non persisté (rouvre dépliée — spec).

**Rationale**: Correspond à la clarification « tout se gère depuis la carte kanban » ; `task-form` reste inchangé (spéc).

**Alternatives considered**: Modal dédié — rejeté ; logique inline dans `kanban` — rejeté (task-card est déjà la bonne frontière).

## D6. Validation et limites (où les appliquer)

**Decision**: Double validation : Jakarta Validation (`@NotBlank`, `@Size(max=200)`) sur le DTO backend + garde-fou frontend (compteur/maxlength, message clair). Limite 50 : comptage dans la transaction du service avant insertion, erreur 409. Titre vide : refus backend 400 + message frontend.

**Rationale**: Constitution I (règles métier dans `application/service`) ; UX requiert un message clair sans aller-retour pour le cas trivial.

**Alternatives considered**: Contrainte SQL (CHECK / trigger) pour la limite 50 — rejetée comme seule défense (message moins clair), le contrôle applicatif transactionnel suffit ; validation uniquement frontend — rejetée (contournable).

## D7. Comportement du formulaire si la tâche parente disparaît

**Decision**: À la soumission, si le backend répond 404 (tâche supprimée entre-temps), le formulaire reste ouvert et affiche un message d'erreur générique (spéc). Aucune re-synchronisation forcée du kanban au-delà du rafraîchissement existant.

**Rationale**: Comportement exact demandé par la clarification ; 404 existe déjà comme sémantique d'absence.

## D8. Références

- Constitution : `.specify/memory/constitution.md` (hexagonal, test-first, transactions, Liquibase, YAGNI)
- API existante : `docs/Architecture/url-api-structure.md`, `docs/Architecture/db-design.md`
- Mémoire projet : `aidd_docs/memory/{architecture,api,database,design,testing}.md`
