# Project Brief

Ce que ce projet est, le problème qu'il résout et son langage métier. Le « pourquoi » non dérivable, pas le « comment ».

## Ce que c'est

- Application de gestion de tâches (monorepo) qui démontre l'usage de l'IA dans le développement logiciel : génération de tests, documentation et correction de bugs.
- Le dépôt propose **3 backends équivalents** (Spring Boot, NestJS, .NET) : l'utilisateur choisit la techno qui lui convient.

## Pourquoi il existe

- Servir de projet d'exercice / de démonstration des pratiques IA (AIDD), avec une base métier simple et compréhensible.

## Langage métier

Les termes qu'un contributeur doit connaître pour lire le code.

| Terme | Signification |
| ----- | ------------- |
| Task | Tâche : l'agrégat principal, gérée en CRUD complet |
| User | Utilisateur : cible de l'assignation des tâches, avec une limite d'assignation par utilisateur |
| Kanban | Vue du frontend : tableau de tâches (carte + formulaire) |

## Fonctionnalités clés

- CRUD complet des tâches
- Assignation des tâches à des utilisateurs avec limitation par utilisateur
- Gestion transactionnelle pour éviter les incohérences
- Tests unitaires générés par IA (JUnit, Karma/Jasmine)
