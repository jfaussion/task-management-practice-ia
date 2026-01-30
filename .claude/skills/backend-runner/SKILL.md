---
name: backend-runner
description: Run, test, or build any backend (Spring Boot, NestJS, .NET)
---

# Backend Runner Skill

Run and test any of the three backend implementations (Spring Boot, NestJS, or .NET) for the task management application.

## Usage

```
/backend-runner [springboot|nestjs|dotnet] [run|test|build]
```

## Examples

- `/backend-runner springboot run` - Start the Spring Boot server
- `/backend-runner nestjs test` - Run NestJS tests
- `/backend-runner dotnet build` - Build the .NET project

## Instructions

When this skill is invoked:

1. **Parse the arguments** to determine which backend and which action to perform
2. **Validate** that the requested backend exists in the project
3. **Execute the appropriate command** based on the backend and action:

### Spring Boot
- `run`: `cd backend/springboot && ./mvnw spring-boot:run`
- `test`: `cd backend/springboot && ./mvnw test`
- `build`: `cd backend/springboot && ./mvnw package`

### NestJS
- `run`: `cd backend/nestjs && npm run start:dev`
- `test`: `cd backend/nestjs && npm test`
- `build`: `cd backend/nestjs && npm run build`

### .NET
- `run`: `cd backend/dotnet/src/TaskManager && dotnet run --project Api/TaskManager.API.csproj`
- `test`: `cd backend/dotnet/src/TaskManager && dotnet test`
- `build`: `cd backend/dotnet/src/TaskManager && dotnet build`

4. **Provide feedback** on the server status and URL (usually http://localhost:8080)
5. **Handle errors** gracefully if dependencies are missing

## Default Behavior

If no arguments are provided, ask the user which backend and action they want to perform.
