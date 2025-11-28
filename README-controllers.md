# Controller Layer (Layer 1) — TaskManagementAPI

This document describes the controller layer and DTOs I added.

## What I created

Controllers (package: `com.ProU.TaskManagementAPI.Controllers`):
- `AuthController` — POST `/auth/login`
- `EmployeeController` — POST `/employees`, GET `/employees`
- `TaskController` — standard CRUD + `/tasks/dashboard`

DTOs (package: `com.ProU.TaskManagementAPI.DTO`):
- `AuthRequest` (username, password)
- `AuthResponse` (token, role)
- `CreateEmployeeRequest` (name, email)
- `EmployeeDto` (id, name, email, createdAt)
- `CreateTaskRequest` (title, description, status, assignedToId, dueDate)
- `UpdateTaskRequest` (nullable fields for update)
- `TaskDto` (id, title, description, status, assignedToId, dueDate, createdAt, updatedAt)
- `DashboardDto` (lists grouped by status)
- `TaskStatus` enum

Services (interfaces & simple mock implementations):
- `AuthService` + `MockAuthService` (in-memory users, returns a simple base64 token and role)
- `EmployeeService` + `MockEmployeeService` (in-memory store)
- `TaskService` + `MockTaskService` (in-memory store)

## How controllers behave
- Controllers are intentionally thin: they accept DTOs, validate them, call the service layer, and return DTO responses.
- `AuthController` returns `401` when credentials are invalid.

## How to build & run
From project root:

```bash
./mvnw -DskipTests package
java -jar target/TaskManagementAPI-0.0.1-SNAPSHOT.jar
```

The app will start on port 8080 by default.

## Quick curl examples

Login (valid creds):

```bash
curl -i -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"user1","password":"pass1"}'
```

Expected response: HTTP 200 and JSON like:

```json
{ "token": "dXNlcjE6VVNFUjoMTY5...", "role": "USER" }
```

Login (invalid creds) => HTTP 401:

```bash
curl -i -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"user1","password":"wrong"}'
```

Create employee:

```bash
curl -i -X POST http://localhost:8080/employees \
  -H 'Content-Type: application/json' \
  -d '{"name":"Alice","email":"alice@example.com"}'
```

Create task (example):

```bash
curl -i -X POST http://localhost:8080/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"My Task","description":"Do it","status":"NOT_STARTED"}'
```

## Notes & next steps
- Token is a simple base64 payload for now. For production use, integrate a real JWT library (jjwt) and sign tokens.
- Add security configuration to enforce role-based access on admin endpoints.
- Add persistence (JPA entities + repositories) instead of in-memory mocks.

## Changes made (files added/edited)
- Added many DTOs under `src/main/java/com/ProU/TaskManagementAPI/DTO`
- Added services and mock implementations under `src/main/java/com/ProU/TaskManagementAPI/Services` and `Services/impl`
- Added controllers under `src/main/java/com/ProU/TaskManagementAPI/Controllers`
- Modified `pom.xml` to include `spring-boot-starter-validation`


---
If you want, I can now:
- Replace the mock token with a real JWT implementation (jjwt) and add basic SecurityConfig
- Add unit tests / MockMvc integration tests for controllers
- Wire controllers to real JPA entities

Which of these would you like next?
