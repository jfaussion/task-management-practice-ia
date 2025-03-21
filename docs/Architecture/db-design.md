## 🧭 Project Context & Requirements

- **Type d’application** : Application locale de gestion de tâches (support pédagogique)
- **Données à stocker** :
  - Utilisateurs (stockés en base avec un rôle simple)
  - Tâches (CRUD complet, estimation de durée)
  - Estimations directement liées aux tâches (pas d’entité séparée)
- **Technologie choisie** : SQL (PostgreSQL pour l’exécution, H2 pour les tests)
- **Volume de données attendu** : Faible (moins de 10 000 lignes par table)
- **Opérations fréquentes** :
  - `READ` → liste, détail de tâche
  - `WRITE` → 
    - création de tâche  
    - assignation d’une tâche à un utilisateur  
    - changement de statut (ex: en cours, terminé)  
    - mise à jour du contenu ou des métadonnées
  - `DELETE` → suppression de tâche
  - Estimation de durée basée sur les propriétés de la tâche
- **Pas d’autres objets métier** à stocker (ni logs, ni historique, ni types dynamiques)


## 🗃️ SQL Specifics

- ✅ Base de données : **SQL (PostgreSQL)** avec ORM **Spring Data JPA + Hibernate**
- ✅ Relations définies :
  - **Un utilisateur peut avoir plusieurs tâches** (1-N)
  - Chaque tâche peut être assignée à un utilisateur (nullable)
- ✅ Contraintes spécifiques :
  - `UNIQUE (user_id, task_name)` → un utilisateur **ne peut pas avoir deux tâches avec le même nom**
  - `assignee_id` → **nullable** (une tâche peut exister sans être assignée)
  - Le statut (`TODO`, `IN_PROGRESS`, `DONE`) **n’est pas contraint au niveau de la base**
    - ✅ Validé uniquement côté Java (par enum ou validation applicative)
- ❌ Pas d’autres contraintes complexes (`CHECK`, `FOREIGN KEY` personnalisées)


## 🧱 Entity & Relationship Definition

### 📌 Entities

#### 🧑 User
- `id` (UUID ou auto-incrément)
- `username` (String, required)
- `email` (String, optional)
- `role` (Enum/String → ex. USER, ADMIN)
- `created_at` (Timestamp)
- `updated_at` (Timestamp)

#### ✅ Constraints
- `username` should be unique per system

---

#### ✅ Task
- `id` (UUID ou auto-incrément)
- `title` (String, required)
- `description` (Text, optional)
- `status` (Enum-like String, ex: TODO, IN_PROGRESS, DONE — contrôlé côté Java)
- `priority` (String ou int, contrôlé côté Java)
- `due_date` (Date, optional)
- `created_at` (Timestamp)
- `updated_at` (Timestamp)
- `assignee_id` (nullable FK → User)

#### ✅ Constraints
- `title` must be **unique per user**
- `assignee_id` is optional (nullable foreign key)
- No audit log, no estimation stored at DB level

---

### 🔗 Relationships

- `User (1) — (N) Task` via `assignee_id`

````mermaid
classDiagram
    class User {
        +UUID id
        +String username
        +String email
        +String role
        +LocalDateTime created_at
        +LocalDateTime updated_at
    }

    class Task {
        +UUID id
        +String title
        +String description
        +String status
        +String priority
        +LocalDate due_date
        +LocalDateTime created_at
        +LocalDateTime updated_at
    }

    User "1" --> "0..*" Task : assigns
````

## 🔍 Performance & Indexing

### ✅ Indexation
- Ajout d’index sur :
  - `status` (pour filtrer les tâches par état)
  - `assignee_id` (pour charger les tâches d’un utilisateur donné)
- Pas d’index prévu sur `priority` ou `due_date` (non filtrés fréquemment)

### ⚖️ Normalisation
- Pas de normalisation stricte imposée (pas de 3NF systématique)
  - But : rester lisible et simple pour la formation
- Données statiques (comme `status`) contrôlées côté Java (pas via table jointe)

### 🔍 Recherche texte
- ❌ Aucun besoin de recherche full-text
- ❌ Aucun moteur de recherche (ex: PostgreSQL FTS, Elasticsearch)

### 📊 Volumétrie
- <10k lignes par table → indexation légère suffisante


## 🧪 Fixtures (Test Data)

### 📦 Volume de données
- Environ **100 lignes**
  - ~5 utilisateurs
  - ~95 tâches (réparties équitablement)

### 🛠️ Méthode d’injection
- ✅ Utilisation de **Liquibase** avec des **scripts SQL `insert`** dans `changelog` :
  - Exemples de fichiers :
    - `db/changelog/001-init-users.sql`
    - `db/changelog/002-init-tasks.sql`

### 👤 Données utilisateurs par défaut
- 3–5 utilisateurs :
  - Avec des rôles variés (`USER`, `ADMIN`)
  - Ajout d’un nom, email fictif, et date de création
- Exemple :
  ```sql
  INSERT INTO users (id, username, role, email, created_at) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'alice', 'ADMIN', 'alice@example.com', now()),
    ('550e8400-e29b-41d4-a716-446655440002', 'bob', 'USER', 'bob@example.com', now());
  ```

### ✅ Tâches simulées
- Réparties entre les utilisateurs créés
- `status` variés : `TODO`, `IN_PROGRESS`, `DONE`
- Titre unique par utilisateur
- Description semi-aléatoire, `due_date` aléatoire sur ±15 jours

### ❌ Pas d'estimation injectée (non persistée)
