# Coding Assertions

Les vérifications qui doivent passer pour que du code compte comme fait. Minimal, exécuté après chaque changement.

## Avant commit

Le garde rapide.

| Ordre | Commande | Vérifie |
| ----- | -------- | ------- |
| 1 | `cd frontend && ng build` | typecheck/build Angular |

## Avant push

Le garde lourd.

| Ordre | Commande | Vérifie |
| ----- | -------- | ------- |
| 1 | `./scripts/test.sh` | tests backend (variante visée) + frontend |

Commandes par backend :
- Spring Boot : `cd backend/springboot && mvn clean package`
- NestJS : `cd backend/nestjs && npm test`
- .NET : `cd backend/dotnet/src/TaskManager && dotnet test`
- Frontend : `cd frontend && ng test -- --browsers=ChromeHeadless --watch=false`

## Comportement

Si un correctif est nécessaire, lancer 1 agent par assertion à corriger (ex. typechecking / tests / règles violées sur la catégorie UI = 3 agents).
