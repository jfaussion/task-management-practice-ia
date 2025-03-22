### 📌 Diagramme de class - Clean Archi


```mermaid
classDiagram
    %% --- Controller ---
    class TaskController {
        +getTasks()
        +getTaskById(id)
        +createTask(taskDto)
        +updateTask(id, taskDto)
        +deleteTask(id)
    }

    %% --- Service Interface ---
    class TaskService {
        <<interface>>
        +findAllTasks()
        +findTaskById(id)
        +createTask(taskDto)
        +updateTask(id, taskDto)
        +deleteTask(id)
    }

    %% --- Application Service ---
    class TaskServiceImpl {
        +findAllTasks()
        +findTaskById(id)
        +createTask(taskDto)
        +updateTask(id, taskDto)
        +deleteTask(id)
    }

    %% --- DAO Interface ---
    class TaskDao {
        <<interface>>
        +List<Task> findAll()
        +Optional<Task> findById(UUID id)
        +Task save(Task task)
        +void deleteById(UUID id)
    }

    %% --- DAO Implementation ---
    class TaskDaoImpl {
        +List<Task> findAll()
        +Optional<Task> findById(UUID id)
        +Task save(Task task)
        +void deleteById(UUID id)
    }

    %% --- Repository Interface ---
    class JpaTaskRepository {
        +findAll()
        +findById(id)
        +save(task)
        +deleteById(id)
    }

    %% --- Domain Model ---
    class Task {
        +UUID id
        +String title
        +String description
        +String status
        +String priority
        +LocalDate dueDate
        +User assignee
    }

    class User {
        +UUID id
        +String username
        +String email
        +String role
    }

    %% --- Relations ---

    TaskController --> TaskService : délègue à
    TaskService --> TaskServiceImpl : implemente
    TaskServiceImpl --> TaskDao : utilise
    TaskDaoImpl --> TaskDao : implémente
    TaskDaoImpl --> JpaTaskRepository : utilise
    JpaTaskRepository --> Task : retourne
    Task --> User : assigné à

```


---

### 📂 Mise en correspondance des packages

| Élément | Package |
|--|--|
| `TaskController` | `infrastructure.api` |
| `TaskService` | `application.service` |
| `TaskServiceImpl` | `application.service.impl` |
| `TaskDao` (interface) | `application.dao` |
| `TaskDaoImpl` | `infrastructure.jpa.dao` |
| `JpaTaskRepository` | `infrastructure.jpa.repository` |
| `Task`, `User` | `domain.model` |


---

Parfait ! Voici un **diagramme de séquence** basé sur l’architecture Clean que tu as fournie et le **use case de création d’une tâche (`POST /api/v1/tasks`)**.

---

### 📌 Use Case : Création d’une tâche (`POST /api/v1/tasks`)

```mermaid
sequenceDiagram
    participant Client as Frontend (Angular)
    participant Controller as TaskController
    participant Service as TaskServiceImpl
    participant Dao as TaskDaoImpl
    participant Repo as JpaTaskRepository
    participant DB as PostgreSQL

    Client->>Controller: POST /api/v1/tasks (taskDto)
    Controller->>Service: createTask(taskDto)
    Service->>Dao: save(Task)
    Dao->>Repo: save(Task)
    Repo->>DB: INSERT INTO tasks (...)
    DB-->>Repo: Task persisted
    Repo-->>Dao: Task entity
    Dao-->>Service: Task entity
    Service-->>Controller: TaskDto (created)
    Controller-->>Client: 201 Created + TaskDto
```

---

