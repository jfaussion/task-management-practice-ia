# API Contract: Sous-tâches

Feature: `specs/001-task-subtasks-progress/spec.md` — Phase 1

Base : `/api/v1` (préfixe obligatoire), identifiants en path parameters, style REST, erreurs JSON standardisées via `@ControllerAdvice` (format `docs/Architecture/url-api-structure.md`). Pas d'authentification. Le CRUD `Task` existant n'est pas modifié ; `GET /api/v1/tasks` et `GET /api/v1/tasks/{id}` incluent désormais `subtasks` dans leur payload.

## Représentation Subtask

```json
{
  "id": 42,
  "taskId": 7,
  "title": "Rédiger la maquette",
  "done": false,
  "createdAt": "2026-11-20T10:15:00Z"
}
```

Payload de tâche enrichi (extrait) :

```json
{
  "id": 7,
  "...": "champs existants inchangés",
  "subtasks": [
    { "id": 42, "taskId": 7, "title": "Rédiger la maquette", "done": false, "createdAt": "..." }
  ]
}
```

La liste est triée par `createdAt` puis `id` (ordre de création).

## Endpoints

### GET /api/v1/tasks/{taskId}/subtasks

Liste les sous-tâches d'une tâche, ordre de création.

- `200 OK` → `Subtask[]` (tableau vide si aucune)
- `404` → tâche inexistante

### POST /api/v1/tasks/{taskId}/subtasks

Crée une sous-tâche.

Request body :

```json
{ "title": "Rédiger la maquette" }
```

- `201 Created` → `Subtask` créée (`done=false` par défaut)
- `400` → titre vide (après trim) ou > 200 caractères
- `404` → tâche inexistante (cas : parent supprimée pendant formulaire ouvert)
- `409` → la tâche a déjà 50 sous-tâches (FR-012)

### PUT /api/v1/tasks/{taskId}/subtasks/{subtaskId}

Renomme la sous-tâche.

Request body :

```json
{ "title": "Nouveau titre" }
```

- `200 OK` → `Subtask` mise à jour
- `400` → titre vide ou > 200 caractères
- `404` → tâche ou sous-tâche inexistante

### PATCH /api/v1/tasks/{taskId}/subtasks/{subtaskId}/done

Coche / décoche la sous-tâche.

Request body :

```json
{ "done": true }
```

- `200 OK` → `Subtask` mise à jour
- `400` → `done` absent ou non booléen
- `404` → tâche ou sous-tâche inexistante

### DELETE /api/v1/tasks/{taskId}/subtasks/{subtaskId}

Supprime la sous-tâche (confirmation demandée côté UI avant l'appel ; l'API ne fait pas de double validation).

- `204 No Content`
- `404` → tâche ou sous-tâche inexistante

## Erreurs

Format standardisé (existant) :

```json
{
  "timestamp": "2026-11-20T10:25:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'title'",
  "path": "/api/v1/tasks/7/subtasks",
  "errorId": "abc123xyz"
}
```

Messages métier attendus :

| Cas | Status | Message |
|-----|--------|---------|
| Titre vide | 400 | titre requis (non vide) |
| Titre > 200 caractères | 400 | titre limité à 200 caractères |
| Limite 50 atteinte | 409 | limite de 50 sous-tâches par tâche atteinte |
| Tâche/sous-tâche introuvable | 404 | ressource introuvable |

## Règles de niveau (FR-002)

Aucun endpoint ne permet d'attacher une sous-tâche à une sous-tâche : la ressource est uniquement imbriquée sous `/tasks/{taskId}`, et le modèle ne porte pas de `parent_subtask_id`. Toute tentative est structurellement impossible via l'API (SC-004).
