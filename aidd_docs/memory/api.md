# API

La surface HTTP de l'API : son style, les ressources principales et les contrats.

## Style

- REST, toutes les routes préfixées par `/api/v1`.
- Côté Spring Boot : contrôleurs dans `infrastructure` (architecture hexagonale), services dans `application/service`.

## Ressources

- `Task` : CRUD complet, assignation à des utilisateurs.
- `User` : utilisateurs cibles de l'assignation.

## Contrats

- Conventions : path parameters pour les identifiants, versioning par préfixe (`/api/v1`).
- La spécification vit dans `docs/Architecture/url-api-structure.md` et `docs/Architecture/db-design.md`.
