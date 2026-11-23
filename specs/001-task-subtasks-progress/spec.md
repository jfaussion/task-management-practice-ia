# Feature Specification: Sous-tâches et progression des tâches

**Feature Branch**: `feature/001-task-subtasks-progress`

**Created**: 2026-11-20

**Status**: Draft

**Input**: User description: "Je souhaite créer des sous-taches dans mes tâches. On s'arrêtera au 1er niveau, c'est-à-dire qu'il n'y aura pas de sous-taches dans des sous-taches. Il faut pouvoir gérer la progression des tâches de manière générale. C'est-à-dire avoir une petite barre de progression assez visuelle qui nous indique le pourcentage également de terminé, par exemple, 50% de terminé."

## Clarifications

### Session 2026-11-20

- Q: Où l'utilisateur gère-t-il les sous-tâches d'une tâche ? → A: Sur la carte de tâche du kanban, via un bouton « + » qui ouvre un petit formulaire dédié à la création de sous-tâche ; la case à cocher « terminée » est directement sur la miniature de chaque sous-tâche dans le kanban. La création/édition de tâche reste inchangée.
- Q: Comment l'utilisateur marque-t-il une sous-tâche comme terminée ? → A: Case à cocher affichée directement sur la miniature de la sous-tâche dans le kanban.
- Q: Comment l'utilisateur renomme-t-il une sous-tâche depuis le kanban ? → A: Icône « crayon » sur la miniature qui réouvre le petit formulaire de création pré-rempli avec le titre actuel.
- Q: Où s'affiche la barre de progression et existe-t-il une progression globale ? → A: La barre de progression est propre à chaque tâche et s'affiche sur sa carte du kanban, entre les champs classiques de la tâche et la liste des sous-tâches. Il n'y a pas de progression globale agrégée.
- Q: Dans quel ordre les sous-tâches s'affichent-elles sur la carte ? → A: Dans l'ordre de création ; pas de réordonnancement manuel.
- Q: Les miniatures de sous-tâches sont-elles toujours visibles sur la carte ? → A: Affichées par défaut (dépliées), avec un bouton sur la carte permettant à l'utilisateur de replier/déplier la liste de sous-tâches à la demande.
- Q: Que se passe-t-il quand l'utilisateur supprime une sous-tâche depuis la miniature ? → A: Une boîte de dialogue de confirmation s'affiche avant chaque suppression ; sans confirmation, rien n'est supprimé.
- Q: Combien de sous-tâches une tâche peut-elle au maximum contenir ? → A: 50 sous-tâches maximum par tâche ; au-delà, l'ajout est refusé avec un message clair.
- Q: L'état replié/déplié de la liste de sous-tâches survit-il au rechargement de la page ? → A: Non, la liste se rouvre toujours dépliée après rechargement.
- Q: Quand l'utilisateur ajoute une sous-tâche alors que la liste est repliée, que se passe-t-il ? → A: La liste se déplie automatiquement pour montrer la nouvelle sous-tâche.
- Q: Deux sous-tâches d'une même tâche peuvent-elles porter le même titre ? → A: Oui, les doublons de titres sont autorisés.
- Q: Que se passe-t-il quand une action sur une sous-tâche échoue côté serveur (par exemple indisponibilité temporaire) ? → A: Un message d'erreur clair s'affiche et l'affichage reste dans l'état antérieur à l'action ; la donnée n'est pas enregistrée.
- Q: Que devient le petit formulaire de sous-tâche si la tâche parente est supprimée pendant qu'il est ouvert ? → A: Le formulaire reste ouvert et l'enregistrement échoue avec un message d'erreur générique.
- Q: Quelle est la longueur maximale acceptée pour le titre d'une sous-tâche ? → A: 200 caractères maximum ; au-delà, l'enregistrement est refusé avec un message clair.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Décomposer une tâche en sous-tâches (Priority: P1)

Un utilisateur crée une tâche comme avant (le formulaire de création/édition de tâche reste inchangé). Depuis la carte de cette tâche dans le kanban, il clique sur un bouton « + » qui ouvre un petit formulaire dédié pour créer une sous-tâche. Chaque sous-tâche apparaît ensuite comme une miniature sous la carte de tâche, avec sa case à cocher « terminée » directement visible. Pour renommer une sous-tâche, l'utilisateur clique sur l'icône « crayon » de sa miniature, ce qui réouvre le même petit formulaire pré-rempli avec le titre actuel. La suppression se fait également depuis le kanban. Les sous-tâches ne peuvent pas elles-mêmes contenir de sous-sous-tâches : la profondeur est limitée à un seul niveau.

**Why this priority**: C'est le cœur de la demande. Sans la capacité de créer des sous-tâches, ni la gestion de progression ni la barre de progression n'existent. C'est le MVP de la fonctionnalité.

**Independent Test**: Peut être testé indépendamment en créant une tâche, puis en ajoutant, modifiant et supprimant plusieurs sous-tâches sur cette tâche, et en vérifiant qu'une tentative d'ajouter une sous-tâche à une sous-tâche est refusée.

**Acceptance Scenarios**:

1. **Given** une tâche existante sans sous-tâches, **When** l'utilisateur ajoute une première sous-tâche avec un titre, **Then** la sous-tâche apparaît associée à la tâche, avec l'état « non terminée » par défaut.
2. **Given** une tâche ayant déjà des sous-tâches, **When** l'utilisateur en ajoute une nouvelle, **Then** elle s'ajoute à la liste sans affecter les autres.
3. **Given** une sous-tâche existante, **When** l'utilisateur tente de lui ajouter une sous-tâche, **Then** l'action est refusée car la profondeur est limitée à un niveau.
4. **Given** une sous-tâche existante, **When** l'utilisateur la renomme, **Then** le nouveau titre est conservé.
5. **Given** une sous-tâche existante, **When** l'utilisateur la supprime, **Then** elle disparaît de la tâche sans supprimer la tâche elle-même.
6. **Given** une tâche sans sous-tâches, **When** l'utilisateur consulte sa progression, **Then** la progression est considérée comme inapplicable (aucune barre affichée).

---

### User Story 2 - Suivre la progression d'une tâche via sa barre sur la carte (Priority: P2)

L'utilisateur coche directement la case « terminée » sur la miniature de chaque sous-tâche dans le kanban, au fil de l'avancement du travail. Sur la carte de la tâche, entre les champs classiques de la tâche et la liste des miniatures de sous-tâches, une barre de progression reflète l'avancement : elle indique visuellement le pourcentage de sous-tâches terminées de cette tâche. Il n'y a pas de progression globale agrégée : « gérer la progression des tâches de manière générale » signifie que chaque tâche possède sa propre barre.

**Why this priority**: C'est la seconde moitié explicite de la demande (« petite barre de progression... pourcentage de terminé »). Elle dépend de l'existence des sous-tâches mais apporte la valeur visible : voir où en est réellement une tâche sans lire ses sous-tâches une par une.

**Independent Test**: Peut être testé indépendamment (sur une tâche disposant déjà de sous-tâches) en marquant certaines sous-tâches comme terminées et en vérifiant que la barre et le pourcentage de la carte se mettent à jour correctement.

**Acceptance Scenarios**:

1. **Given** une tâche avec 4 sous-tâches, toutes non terminées, **When** l'utilisateur consulte la tâche, **Then** la barre de progression est vide et affiche 0 %.
2. **Given** une tâche avec 4 sous-tâches dont 2 sont terminées, **When** l'utilisateur consulte la tâche, **Then** la barre est remplie à moitié et affiche 50 %.
3. **Given** une tâche avec 4 sous-tâches dont 3 sont terminées, **When** l'utilisateur marque la dernière comme terminée, **Then** la barre passe à 100 % immédiatement.
4. **Given** une tâche à 100 %, **When** l'utilisateur repasse une sous-tâche en non terminée, **Then** la barre redescend en conséquence (par exemple à 75 % si 3 restent terminées sur 4).
5. **Given** une tâche avec des sous-tâches, **When** l'utilisateur en supprime une, **Then** le pourcentage est recalculé sur le nombre restant.

---

### Edge Cases

- Que se passe-t-il quand une tâche a exactement une seule sous-tâche ? La barre doit passer directement de 0 % à 100 % selon l'état de cette unique sous-tâche.
- Que se passe-t-il quand toutes les sous-tâches d'une tâche sont supprimées ? La tâche redevient une tâche sans sous-tâches : la barre de progression disparaît de sa carte.
- Que se passe-t-il quand on supprime une tâche qui a des sous-tâches ? Les sous-tâches sont supprimées avec elle.
- Comment le pourcentage est-il arrondi quand il n'est pas entier (par exemple 1 sur 3 terminée) ? Le pourcentage affiché est arrondi à l'entier le plus proche.
- Que se passe-t-il si l'utilisateur supprime une sous-tâche depuis la miniature ? Une boîte de dialogue de confirmation s'affiche d'abord ; la sous-tâche n'est supprimée qu'après confirmation.
- Que se passe-t-il si l'utilisateur tente d'ajouter une sous-tâche à une tâche qui en compte déjà 50 ? L'ajout est refusé avec un message clair indiquant la limite atteinte.
- Que se passe-t-il si l'utilisateur ajoute une sous-tâche alors que la liste est repliée ? La liste se déplie automatiquement pour montrer la nouvelle sous-tâche.
- Que se passe-t-il si deux sous-tâches d'une même tâche portent le même titre ? C'est autorisé : le titre n'est pas un identifiant.
- Que se passe-t-il quand une action sur une sous-tâche échoue côté serveur ? Un message d'erreur clair s'affiche et l'affichage revient à l'état antérieur à l'action ; rien n'est enregistré.
- Que devient le petit formulaire de sous-tâche si la tâche parente est supprimée pendant qu'il est ouvert ? Le formulaire reste ouvert et l'enregistrement échoue avec un message d'erreur générique.
- Que se passe-t-il si le titre d'une sous-tâche dépasse 200 caractères ? L'enregistrement est refusé avec un message clair.
- Que se passe-t-il si le titre d'une sous-tâche est vide ? L'enregistrement est refusé avec un message clair.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le système DOIT permettre à un utilisateur d'ajouter une sous-tâche à une tâche existante.
- **FR-002**: Le système DOIT limiter la profondeur des sous-tâches à un seul niveau : une sous-tâche ne peut pas contenir de sous-tâche.
- **FR-003**: Le système DOIT permettre à un utilisateur de modifier le titre d'une sous-tâche existante.
- **FR-004**: Le système DOIT permettre à un utilisateur de supprimer une sous-tâche sans supprimer la tâche parente, après confirmation explicite via une boîte de dialogue.
- **FR-005**: Le système DOIT permettre à un utilisateur de marquer une sous-tâche comme terminée ou non terminée.
- **FR-006**: Le système DOIT calculer le pourcentage de progression d'une tâche comme le ratio de ses sous-tâches terminées sur le total de ses sous-tâches.
- **FR-007**: Le système DOIT afficher, pour chaque tâche ayant au moins une sous-tâche, sur sa carte du kanban, une barre de progression visuelle accompagnée du pourcentage de sous-tâches terminées, placée entre les champs classiques de la tâche et la liste des miniatures de sous-tâches.
- **FR-008**: Le système DOIT mettre à jour la barre de progression et le pourcentage dès qu'une sous-tâche change d'état, est ajoutée ou est supprimée.
- **FR-009**: Le système DOIT traiter une tâche sans sous-tâches comme n'ayant pas de progression applicable (aucune barre affichée).
- **FR-010**: Le système DOIT afficher la liste des sous-tâches dépliée par défaut sur chaque carte et permettre à l'utilisateur de la replier ou de la déplier à la demande.
- **FR-011**: Le système DOIT refuser l'enregistrement d'une sous-tâche dont le titre est vide ou dépasse 200 caractères, avec un message clair.
- **FR-012**: Le système DOIT limiter à 50 le nombre de sous-tâches d'une tâche et refuser tout ajout au-delà avec un message clair.
- **FR-013**: Le système DOIT conserver les sous-tâches et leur état de façon persistante, de sorte qu'ils soient encore présents après rechargement de l'application.

### Key Entities *(include if feature involves data)*

- **Tâche**: L'agrégat principal existant. Elle peut désormais posséder zéro ou plusieurs sous-tâches. Sa progression est dérivée de ses sous-tâches.
- **Sous-tâche**: Élément rattaché à une tâche (et uniquement à une tâche). Possède un titre (obligatoire, 200 caractères maximum) et un état terminé/non terminé. Ne peut pas contenir d'autres sous-tâches.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Un utilisateur peut créer, modifier, terminer et supprimer des sous-tâches sur une tâche en moins de 30 secondes sans formation.
- **SC-002**: Le pourcentage affiché correspond exactement au ratio de sous-tâches terminées sur le total, arrondi à l'entier le plus proche, dans 100 % des cas testés.
- **SC-003**: La barre de progression et le pourcentage se mettent à jour immédiatement (sans rechargement manuel) après tout changement d'état d'une sous-tâche.
- **SC-004**: Aucune sous-tâche ne peut contenir de sous-tâche : toute tentative est systématiquement refusée.
- **SC-005**: Après rechargement de l'application, les sous-tâches et leurs états sont intégralement conservés.

## Assumptions

- La progression d'une tâche est définie exclusivement par le ratio de ses sous-tâches terminées ; aucune autre source de progression (date, statut manuel) n'est introduite.
- Une tâche sans sous-tâches n'a pas de progression : elle n'affiche pas de barre sur sa carte.
- Il n'existe pas de progression globale agrégée : la progression est uniquement par tâche, visible sur chaque carte du kanban.
- Le pourcentage est arrondi à l'entier le plus proche pour l'affichage.
- Toute sous-tâche appartient à exactement une tâche ; il n'y a pas de sous-tâche partagée entre plusieurs tâches.
- La modification du titre d'une tâche existante continue de fonctionner ; la gestion des sous-tâches s'y ajoute sans changer le comportement existant des tâches.
- L'interface reste celle du kanban existant ; la barre de progression s'intègre aux cartes de tâches sans nouvelle page dédiée.
- Le formulaire de création/édition de tâche existant reste inchangé : il ne gère que les champs de la tâche elle-même.
- Toute la gestion des sous-tâches se fait directement depuis le kanban : création via un bouton « + » sur la carte de tâche, renommage via l'icône « crayon » de la miniature (qui réouvre le petit formulaire pré-rempli), suppression et case à cocher « terminée » sur la miniature.
- Les sous-tâches s'affichent dans l'ordre de leur création ; il n'y a pas de réordonnancement manuel.
- La liste des sous-tâches est affichée dépliée par défaut sur chaque carte ; un bouton sur la carte permet de la replier ou de la déplier à la demande. L'état replié n'est pas persisté : après rechargement, la liste se rouvre toujours dépliée.
