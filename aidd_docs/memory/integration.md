# Integration

Comment ce système s'intègre avec des services externes. La carte de chaque outil autour du projet vit dans la mémoire `ecosystem.md`.

## Services externes

- PostgreSQL : base de données principale, lancée via Docker (`scripts/setup-db.sh`), config côté Spring Boot (`application` properties, migrations Liquibase).
- Prisma (côté NestJS) : accès base depuis `backend/nestjs/prisma/`.

## Conventions d'appel

- Pas de service tiers externe (API de paiement, etc.) en place.
- PostgreSQL est le seul état hors du dépôt : le réinitialiser via `scripts/setup-db.sh`.
