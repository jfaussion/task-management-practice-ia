### 📌 Diagramme de class - Clean Archi
````mermaid
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
        +findAll()
        +findById(id)
        +save(task)
        +deleteById(id)
    }

    %% --- DAO Implementation ---
    class TaskDaoImpl {
        +findAll()
        +findById(id)
        +save(task)
        +deleteById(id)
    }

    %% --- Prisma Service ---
    class PrismaService {
        +task
        +user
        +connect()
        +disconnect()
    }

    %% --- Domain Model ---
    class Task {
        +id: string
        +title: string
        +description: string
        +status: string
        +priority: string
        +dueDate: Date
        +assignee: User
    }

    class User {
        +id: string
        +username: string
        +email: string
        +role: string
    }


    %% --- Relations ---
    TaskController --> TaskService : uses
    TaskService --> TaskServiceImpl : implements
    TaskServiceImpl --> TaskDao : uses
    TaskDaoImpl --> TaskDao : implements
    TaskDaoImpl --> PrismaService : uses
    Task --> User : assigned to
````


### 📂 Mise en correspondance des packages

| Élément | Package |
|--|--|
| `TaskController` | `infrastructure.api` |
| `TaskService` (interface)| `application.service` |
| `TaskServiceImpl` | `application.service.impl` |
| `TaskDao` (interface) | `application.dao` |
| `TaskDaoImpl` | `infrastructure.data.dao` |
| `Prisma.service` | `infrastructure.data.prisma` |
| `Task`, `User` | `domain.model` |

### 📌 Use Case : Création d’une tâche (`POST /api/v1/tasks`)

````mermaid
sequenceDiagram
    participant Client as Frontend (Angular)
    participant Controller as TaskController
    participant Service as TaskServiceImpl
    participant Dao as TaskDaoImpl
    participant Prisma as PrismaService
    participant DB as PostgreSQL

    Client->>Controller: POST /api/tasks (TaskDto)
    Controller->>Service: createTask(taskDto)
    Service->>Dao: save(Task)
    Dao->>Prisma: prisma.task.create()
    Prisma->>DB: INSERT INTO tasks (...)
    DB-->>Prisma: Task persisted
    Prisma-->>Dao: Task entity
    Dao-->>Service: Task entity
    Service-->>Controller: TaskDto (created)
    Controller-->>Client: 201 Created + TaskDto
````

