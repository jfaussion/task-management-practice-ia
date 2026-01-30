# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Task Management Practice - A full-stack application with an Angular frontend and three backend implementations (Spring Boot, NestJS, and .NET 8). This is a pedagogical project for AI-assisted development practice. The application manages users and tasks with CRUD operations.

## Repository Structure

```
├── frontend/              # Angular 19 application
├── backend/
│   ├── springboot/        # Spring Boot 3.4 backend (Java 21) with JPA + Liquibase
│   ├── nestjs/            # NestJS backend with Prisma ORM
│   └── dotnet/            # .NET 8 backend with Entity Framework Core
├── docs/                  # Project documentation
│   ├── Architecture/      # DB design, API structure specs
│   └── Bootstrap/         # Setup guides for each technology
```

## Commands

### Frontend (Angular)

```bash
cd frontend
npm install
npm start              # Dev server with proxy to backend (localhost:4200)
npm run build          # Production build
npm test               # Unit tests with Karma
```

### Backend - Spring Boot (Primary)

```bash
cd backend/springboot
./mvnw spring-boot:run              # Run with H2 in-memory database
./mvnw test                         # Run tests
./mvnw package                      # Build JAR
```

H2 console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`).

### Backend - NestJS

```bash
cd backend/nestjs
npm install
cp .env.example .env   # Configure DATABASE_URL (SQLite default)
npm run start:dev      # Dev server with watch mode (runs Prisma generate/migrate/seed)
npm run test           # Unit tests
npm run test:e2e       # E2E tests
npm run lint           # ESLint
```

Prisma commands:
```bash
npm run prisma:generate   # Generate Prisma client
npm run prisma:migrate    # Deploy migrations
npm run prisma:reset      # Reset database
npm run prisma:seed       # Seed database
```

### Backend - .NET

```bash
cd backend/dotnet/src/TaskManager
dotnet restore
dotnet build
dotnet run --project Api/TaskManager.API.csproj   # Runs with Swagger in dev
```

## Architecture

All three backends follow Clean/Hexagonal Architecture with the same layer structure:

```
domain/           → Domain models (User, Task) and exceptions
application/
  ├── service/    → Service interfaces and implementations
  └── dao/        → Data access interfaces (repository pattern)
infrastructure/
  ├── api/        → Controllers, DTOs, mappers, exception handlers
  └── jpa|data/   → DAO implementations, database configuration
```

### Spring Boot Backend

- **Java 21** with Spring Boot 3.4
- **JPA + Hibernate** for persistence
- **Liquibase** for database migrations (`db/changelog/`)
- **MapStruct** for DTO/Entity mapping
- **Jakarta Validation** for input validation
- **Lombok** for boilerplate reduction

### NestJS Backend

- **Prisma ORM** with SQLite (configurable via `.env`)
- Token-based DI (e.g., `@Inject(USER_SERVICE)`)
- Prisma schema at `prisma/schema.prisma`

### .NET Backend

- **EF Core** with SQLite
- **FluentValidation** for input validation
- 4 projects: Domain, Application, Infrastructure, API

### Frontend

- Angular 19 standalone components
- Tailwind CSS 4 for styling
- Proxy configuration forwarding `/api` to `http://localhost:8080`

## Database Schema

All backends implement the same data model:

- **User**: id (UUID), username (unique), email, role (USER/ADMIN), timestamps
- **Task**: id (UUID), title, description, status (TODO/IN_PROGRESS/DONE), priority, dueDate, assigneeId (nullable FK), timestamps
- **Constraint**: title must be unique per user (assignee)

## API Endpoints

All backends expose REST APIs at `http://localhost:8080`. Spring Boot uses `/api/v1` prefix:

| Endpoint | Description |
|----------|-------------|
| `GET /api/v1/users` | List users (paginated) |
| `GET /api/v1/users/:id` | Get user by ID |
| `POST /api/v1/users` | Create user |
| `PUT /api/v1/users/:id` | Update user |
| `DELETE /api/v1/users/:id` | Delete user |
| `GET /api/v1/tasks` | List tasks (paginated, filterable) |
| `GET /api/v1/tasks/:id` | Get task by ID |
| `POST /api/v1/tasks` | Create task |
| `PUT /api/v1/tasks/:id` | Update task |
| `DELETE /api/v1/tasks/:id` | Delete task |
| `GET /api/v1/users/:id/tasks` | Get tasks for a user |

Query params for filtering: `?status=done&priority=high&page=1&limit=10`

## Error Response Format

Standardized JSON error format across all backends:
```json
{
  "timestamp": "2025-03-21T10:25:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'name'",
  "path": "/api/v1/tasks",
  "errorId": "abc123xyz"
}
```

## Running Tests

```bash
# Frontend
cd frontend && npm test

# Spring Boot
cd backend/springboot && ./mvnw test

# NestJS
cd backend/nestjs && npm test
cd backend/nestjs && npm run test:e2e

# .NET
cd backend/dotnet/src/TaskManager && dotnet test
```

## Documentation

Refer to `docs/` for detailed specifications:
- `docs/Architecture/db-design.md` - Database schema and relationships
- `docs/Architecture/url-api-structure.md` - Full API specification
- `docs/Bootstrap/` - Setup guides for each technology stack
