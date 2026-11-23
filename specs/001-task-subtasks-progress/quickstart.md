# Quickstart: Sous-tâches et progression des tâches

Guide de validation de bout en bout pour `specs/001-task-subtasks-progress/spec.md`. Les détails d'implémentation vivent dans `data-model.md`, `contracts/subtasks-api.md` et `tasks.md`.

## Prérequis

- Docker (PostgreSQL) et JDK 21, Node/Angular CLI.
- Base réinitialisée : `./scripts/setup-db.sh`
- Application lancée : `./scripts/run.sh` (backend Spring Boot + frontend `ng serve` via proxy `proxy.conf.json`)
- Ouvrir l'URL du frontend affichée par `ng serve`.

## Scénarios de validation

### S1 — Créer et gérer des sous-tâches (US1, P1)

1. Créer une tâche via le formulaire existant (inchangé).
2. Sur la carte de la tâche dans le kanban, cliquer « + » → le petit formulaire de sous-tâche s'ouvre.
3. Ajouter un titre (ex. « Maquette ») → la miniature apparaît, décochée, liste dépliée.
4. Ajouter une seconde sous-tâche → s'ajoute à la suite sans affecter la première.
5. Cliquer le « crayon » d'une miniature → le formulaire se réouvre pré-rempli ; modifier et valider → titre conservé.
6. Cliquer la poubelle d'une miniature → boîte de confirmation ; annuler → rien n'est supprimé ; confirmer → la miniature disparaît, la tâche reste.
7. Recharger la page → sous-tâches et états intacts (FR-013, SC-005).

### S2 — Barre de progression (US2, P2)

1. Sur une tâche avec 4 sous-tâches non terminées : barre vide, « 0 % ».
2. Cocher 2 miniatures : barre à moitié remplie, « 50 % », sans rechargement (SC-003).
3. Cocher la 3e puis la 4e : « 100 % » immédiatement.
4. Décocher une miniature : la barre redescend à « 75 % ».
5. Supprimer une sous-tâche (confirmée) : pourcentage recalculé sur le restant.
6. Sur une tâche sans sous-tâche : aucune barre affichée (FR-009).

### S3 — Limites et validations (FR-011, FR-012)

1. Soumettre le formulaire avec un titre vide → refus, message clair, rien n'est créé.
2. Coller un titre de 201 caractères → refus avec message clair.
3. Créer 50 sous-tâches (script ou manuellement) puis tenter une 51e → refus, message « limite atteinte », aucune création.
4. Vérifier que l'ajout à une sous-tâche est impossible depuis l'UI (pas de « + » sur une miniature) — FR-002.

### S4 — Comportements UI (FR-010, clarifications)

1. Replier la liste de sous-tâches via le bouton de la carte → miniatures masquées.
2. Ajouter une sous-tâche pendant que la liste est repliée → la liste se déplie automatiquement.
3. Recharger → la liste est toujours dépliée (état replié non persisté).
4. Deux sous-tâches avec le même titre → autorisées.
5. Cas 1/3 terminée → pourcentage affiché « 33 % » (arrondi au plus proche).
6. Tâche avec une seule sous-tâche : cochée → 100 %, décochée → 0 %.

### S5 — Erreurs serveur (clarifications)

1. Arrêter le backend, tenter de cocher une sous-tâche → message d'erreur clair ; redémarrer le backend et vérifier que l'affichage correspond bien à l'état antérieur (rien n'a été enregistré).
2. Supprimer une tâche pendant que son formulaire de sous-tâche est ouvert dans un autre onglet, puis valider → le formulaire reste ouvert, message d'erreur générique.
3. Supprimer une tâche ayant des sous-tâches → les sous-tâches disparaissent avec elle (recharger pour vérifier).

## Validation technique

- Suite complète : `./scripts/test.sh` (JUnit backend + Karma/Jasmine frontend, ChromeHeadless) → doit être verte (Constitution II).
- Build rapide avant commit : `cd frontend && ng build`
- Contrat API : rejouer les appels de `contracts/subtasks-api.md` (curl) sur une tâche réelle, vérifier codes 201/200/204/400/404/409.
- Schéma : vérifier que la table `subtask` et sa FK cascade existent après `./scripts/setup-db.sh` (changelog `003-add-subtasks.sql`), cf. `data-model.md`.
