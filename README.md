# TaskManagementAPI

An easy-to-run REST backend for managing Employees and Tasks. This README explains how to set up the project locally, run it, and test all implemented APIs (employee and admin flows).

**Contents**
- **Setup**: prerequisites and how to run locally
- **Tech stack**: core libraries and versions
- **Quick start**: build & run commands
- **Database setup & migrations**
- **APIs**: overview + curl/Postman examples
- **Screenshots / recording**: where to add them
- **Assumptions & bonus features**
- **Git cleanup**: remove unwanted tracked files and push a clean commit

**Setup (local)**
- Prerequisites:
  - Java 21
  - Maven (`./mvnw` wrapper included)
  - PostgreSQL (local)

- Create a local Postgres database and user (example):
  - DB URL: `jdbc:postgresql://localhost:5432/ProU`
  - DB user: `postgres`, password: `123456` (change as needed)

- Configure DB in `src/main/resources/application.properties` if different.

**Build & run**
1. Build (skip tests during local dev):
```bash
./mvnw -DskipTests package
```
2. Run (dev):
```bash
./mvnw spring-boot:run
# or run the packaged jar
java -Dserver.port=9090 -jar target/TaskManagementAPI-0.0.1-SNAPSHOT.jar
```

The app starts on port `9090` by default. Flyway will run migrations on startup.

**Tech stack**
- Java 21
- Spring Boot 4 (Spring Web, Spring Data JPA, Spring Security)
- PostgreSQL
- Flyway for DB migrations
- Maven build

**Database migrations**
- Migrations live at `src/main/resources/db/migration` and run automatically on startup.

**APIs (high-level)**
- `POST /signup` — public signup (creates pending signup)
- `GET /admin/approvals` — admin lists pending signups
- `POST /admin/approvals/{id}/approve` — admin approves signup (creates user + employee)
- `POST /login` and `POST /auth/login` — credential check (returns informational token)
- `GET /employees` — list employees (ADMIN/EMPLOYEE)
- `POST /employees` — create employee (ADMIN only)
- `GET /tasks` — list tasks (ADMIN sees all; EMPLOYEE sees only their assigned tasks)
- `GET /tasks/{id}` — get task by id
- `POST /tasks` — create task (ADMIN only)
- `PUT /tasks/{id}` — update task (ADMIN full update; EMPLOYEE only `status` for assigned tasks)
- `DELETE /tasks/{id}` — delete task (ADMIN only)
- `GET /tasks/dashboard` — grouped tasks view (ADMIN/EMPLOYEE)

See the `src/main/java/com/ProU/TaskManagementAPI/Controllers` folder for controller details and DTOs for exact request shapes.

**Request payload notes**
- `CreateTaskRequest` expects JSON fields:
  - `title` (string, required)
  - `description` (string, optional)
  - `status` (enum: `NOT_STARTED`, `IN_PROGRESS`, `COMPLETED`, required)
  - `assignedToId` (number, optional)
  - `dueDate` (ISO-8601 string, optional) e.g. `2025-11-13T00:00:00+05:30`

Example create-task (admin):
```json
{
  "title": "Demo for Nanda",
  "description": "Assigned to Nanda",
  "status": "NOT_STARTED",
  "assignedToId": 2,
  "dueDate": "2025-11-13T00:00:00+05:30"
}
```

**Quick curl examples**
- Login (public):
```bash
curl -i -X POST -H "Content-Type: application/json" \
  -d '{"username":"admin@prou","password":"123456"}' \
  http://localhost:9090/login
```
- List tasks as employee (basic auth):
```bash
curl -i -u "nanda@prou:nanda" http://localhost:9090/tasks
```

**Screenshots / Short screen recording**
- Add images or a short recording under `docs/images/` or `docs/video/` and reference them here. Example:
  - `docs/images/tasks-list.png`
  - `docs/video/demo.mp4`
- Git will not track large video files well; prefer compressed GIF or a short MP4 (<10-20MB) or upload to a shared drive and link.

**Assumptions & Bonus features implemented**
- Assumptions:
  - App runs locally with Postgres. Example DB credentials provided above.
  - Authentication uses HTTP Basic for now; tokens returned by `/auth/login` are informational only.
- Bonus / implemented extras:
  - Signup -> admin approval flow: public `POST /signup` creates a pending signup; admin approves via `/admin/approvals/{id}/approve` with password and role.
  - Role-based access: `ROLE_ADMIN` vs `ROLE_EMPLOYEE` enforced in `SecurityConfig`.
  - Employee ownership checks: employees can only update `status` of tasks assigned to them.
  - Dev-friendly PasswordEncoder fallback to accept legacy plaintext seeds (remove in production).
