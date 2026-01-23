### 📌 Diagramme de class - Clean Archi

```mermaid
classDiagram
    %% --- Controller ---
    class TasksController {
        +GetTasks()
        +GetTaskById(id)
        +CreateTask(createTaskDto)
        +UpdateTask(id, updateTaskDto)
        +AssignTask(id, assigneeId)
        +UpdateStatus(id, status)
    }

    %% --- Service Interface ---
    class ITaskService {
        <<interface>>
        +GetAllTasksAsync()
        +GetTasksByStatusAsync(status)
        +GetTasksByAssigneeAsync(assigneeId)
        +GetTaskByIdAsync(id)
        +CreateTaskAsync(task)
        +UpdateTaskAsync(id, task)
        +AssignTaskAsync(taskId, assigneeId)
        +UpdateTaskStatusAsync(taskId, status)
        +EstimateTaskTimeAsync(taskId)
    }

    %% --- Application Service ---
    class TaskService {
        +GetAllTasksAsync()
        +GetTasksByStatusAsync(status)
        +GetTasksByAssigneeAsync(assigneeId)
        +GetTaskByIdAsync(id)
        +CreateTaskAsync(task)
        +UpdateTaskAsync(id, task)
        +AssignTaskAsync(taskId, assigneeId)
        +UpdateTaskStatusAsync(taskId, status)
        +EstimateTaskTimeAsync(taskId)
    }

    %% --- Repository Interface ---
    class ITaskRepository {
        <<interface>>
        +FindAllAsync()
        +FindByStatusAsync(status)
        +FindByAssigneeIdAsync(assigneeId)
        +FindByIdAsync(id)
        +FindByTitleAndAssigneeIdAsync(title, assigneeId)
        +SaveAsync(task)
        +UpdateAsync(task)
        +ExistsByIdAsync(id)
        +ExistsByTitleAndAssigneeIdAsync(title, assigneeId)
    }

    %% --- Repository Implementation ---
    class TaskRepository {
        +FindAllAsync()
        +FindByStatusAsync(status)
        +FindByAssigneeIdAsync(assigneeId)
        +FindByIdAsync(id)
        +FindByTitleAndAssigneeIdAsync(title, assigneeId)
        +SaveAsync(task)
        +UpdateAsync(task)
        +ExistsByIdAsync(id)
        +ExistsByTitleAndAssigneeIdAsync(title, assigneeId)
    }

    %% --- EF Core DbContext ---
    class TaskManagementContext {
        +DbSet~TaskDomain~ Tasks
        +DbSet~User~ Users
        +OnModelCreating()
        +SaveChangesAsync()
    }

    %% --- Domain Model ---
    class TaskDomain {
        +Guid Id
        +string Title
        +string Description
        +string Status
        +string Priority
        +DateTime? DueDate
        +Guid? AssigneeId
        +User? Assignee
        +DateTime CreatedAt
        +DateTime UpdatedAt
    }

    class User {
        +Guid Id
        +string Username
        +string Email
        +string Role
        +DateTime CreatedAt
        +DateTime UpdatedAt
        +ICollection~TaskDomain~ AssignedTasks
    }

    %% --- Relations ---
    TasksController --> ITaskService : délègue à
    ITaskService --> TaskService : implémente
    TaskService --> ITaskRepository : utilise
    TaskRepository --> ITaskRepository : implémente
    TaskRepository --> TaskManagementContext : utilise
    TaskManagementContext --> TaskDomain : retourne
    TaskManagementContext --> User : retourne
    TaskDomain --> User : assigné à

```


---

### 📂 Mise en correspondance des namespaces

| Élément | Namespace / Project |
|--|--|
| `TasksController` | `TaskManager.API.Controllers` |
| `ITaskService` | `TaskManager.Application.Service` |
| `TaskService` | `TaskManager.Application.Service` |
| `ITaskRepository` (interface) | `TaskManager.Application.Interface` |
| `TaskRepository` | `TaskManager.Infrastructure.Repository` |
| `TaskManagementContext` | `TaskManager.Infrastructure.EFCore` |
| `TaskDomain`, `User` | `TaskManager.Domain.Models` |
| **DTOs** | `TaskManager.API.Dtos` |
| **Validators** | `TaskManager.API.Validators` |
| **Mappers** | `TaskManager.API.Mapper` |
| **Exceptions** | `TaskManager.Domain.Exceptions` |


---

### 📌 Use Case : Création d'une tâche (`POST /api/v1/tasks`)

```mermaid
sequenceDiagram
    participant Client as Frontend (Angular)
    participant Controller as TasksController
    participant Service as TaskService
    participant Repository as TaskRepository
    participant DbContext as TaskManagementContext
    participant DB as SQLite

    Client->>Controller: POST /api/v1/tasks (CreateTaskDTO)
    Controller->>Controller: Validation (FluentValidation)
    Controller->>Controller: Map DTO to TaskDomain
    Controller->>Service: CreateTaskAsync(taskDomain)
    Service->>Repository: SaveAsync(taskDomain)
    Repository->>DbContext: Tasks.Add(taskDomain)
    Repository->>DbContext: SaveChangesAsync()
    DbContext->>DB: INSERT INTO Tasks (...)
    DB-->>DbContext: Task persisted
    DbContext-->>Repository: TaskDomain entity
    Repository-->>Service: TaskDomain entity
    Service-->>Controller: TaskDomain entity
    Controller->>Controller: Map TaskDomain to TaskDTO
    Controller-->>Client: 201 Created + TaskDTO
```

---

### 🏗️ Architecture détaillée

#### Couche API (TaskManager.API)
- **Controllers**: Points d'entrée REST (TasksController, UsersController)
- **DTOs**: Objets de transfert (CreateTaskDTO, TaskDTO, ErrorResponse)
- **Validators**: Validation avec FluentValidation (CreateTaskValidator)
- **Mappers**: Extensions de mapping (TaskExtensions, UserExtensions)
- **Exception Handling**: GlobalExceptionHandler pour gérer les erreurs

#### Couche Application (TaskManager.Application)
- **Services**: Logique métier (TaskService, UserService)
- **Interfaces**: Contrats de service (ITaskService, IUserService)
- **Repository Interfaces**: Contrats d'accès aux données (ITaskRepository, IUserRepository)

#### Couche Infrastructure (TaskManager.Infrastructure)
- **Repositories**: Implémentations EF Core (TaskRepository, UserRepository)
- **DbContext**: Configuration EF Core (TaskManagementContext)
- **Migrations**: Gestion du schéma de base de données
- **Seeding**: Données initiales (TaskManagementContextSeed)

#### Couche Domain (TaskManager.Domain)
- **Models**: Entités métier (TaskDomain, User)
- **Exceptions**: Exceptions métier (FunctionalException, TechnicalException)

---

### 🔄 Flux de données

1. **Requête HTTP** → Controller (API Layer)
2. **Validation** → FluentValidation
3. **Mapping DTO → Domain** → Extension methods
4. **Logique métier** → Service (Application Layer)
5. **Accès aux données** → Repository (Infrastructure Layer)
6. **ORM** → Entity Framework Core
7. **Base de données** → SQLite
8. **Retour** → Domain → DTO → JSON Response

---

### 🎯 Principes appliqués

- **Separation of Concerns**: Chaque couche a une responsabilité claire
- **Dependency Inversion**: Les dépendances pointent vers les abstractions
- **Interface Segregation**: Interfaces spécifiques et cohérentes
- **Single Responsibility**: Chaque classe a une seule raison de changer
- **Testabilité**: Architecture facilement testable avec mocking
