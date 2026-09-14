# Testing

Comment le projet est testé : les couches, les outils et les conventions. Où vivent les tests et comment les lancer.

## Stratégie

- Tests unitaires uniquement (pas d'e2e en place). Côté backend, tests du service métier ; côté frontend, tests de composants Angular.

## Outils

- Spring Boot : JUnit (via Maven).
- NestJS : runner par défaut (`npm test`).
- .NET : `dotnet test`.
- Frontend : Karma + Jasmine, ChromeHeadless en CI.

## Conventions

- Les tests vivent à côté du code (`*.spec.ts` côté frontend, `src/test` côté NestJS).
- La CI (`.github/workflows/build.yml`) lance les tests des 4 variantes sur chaque push/PR vers `main`.

## Run

- Tout : `./scripts/test.sh`
- Backend individuel : voir `coding-assertions.md`
- Frontend : `cd frontend && ng test -- --browsers=ChromeHeadless --watch=false`

## Browser QA

- Entrée : `./scripts/run.sh` puis ouvrir l'URL du frontend (`ng serve`).
- Auth : aucune authentification en place.
- État : réinitialiser la base via `./scripts/setup-db.sh` (PostgreSQL via Docker).
