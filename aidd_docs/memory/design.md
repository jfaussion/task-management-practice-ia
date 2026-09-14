# Design

Le style visuel du frontend et ses conventions.

- Tailwind CSS 4 comme système de style, pas de CSS componentnel lourd.
- Composants UI dans `frontend/src/app/ui` : `kanban` (vue tableau), `task-card`, `task-form`.
- Structure Angular standalone : zones `ui` (présentation), `application/service` (accès données), `domain/model` (modèle).
