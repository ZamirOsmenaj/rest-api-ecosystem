# Employee Management System (EMS) — Sample REST API

A small, self-contained Spring Boot REST API for managing **departments** and **employees**.
Built as a reference implementation of a typical corporate Java backend: layered
architecture, DTOs, centralized exception handling, a uniform response envelope,
request logging with correlation IDs, OpenAPI docs, and a Dockerized runtime
backed by PostgreSQL.

It's intentionally scoped to two related resources so the *patterns* — not the
domain — are what you take away from it.

---

## 1. Tech stack

| Concern              | Choice                                            |
|-----------------------|---------------------------------------------------|
| Language / runtime    | Java 21                                            |
| Framework             | Spring Boot 3.5.x (Spring MVC, Spring Data JPA)    |
| Database (Docker)     | PostgreSQL 16                                      |
| Database (local dev)  | H2 (in-memory)                                     |
| Validation             | Jakarta Bean Validation                            |
| API docs               | springdoc-openapi (Swagger UI)                    |
| Build                 | Maven                                              |
| Boilerplate reduction | Lombok                                             |
| Containerization       | Docker, multi-stage build, docker-compose         |
| Tests                  | JUnit 5, Mockito, AssertJ, MockMvc                |

## 2. Architecture

Standard layered architecture, one direction of dependency only (top to bottom):

```
HTTP request
    │
    ▼
RequestLoggingFilter        (correlation id, request/response logging)
    │
    ▼
Controller                  (HTTP concerns: routes, status codes, @Valid)
    │
    ▼
Service (interface + impl)  (business rules, transactions)
    │
    ▼
Repository (Spring Data)    (persistence)
    │
    ▼
Entity (JPA)  ◄────────────  Mapper  ────────────►  DTO (request / response)
```

Cross-cutting concerns sit beside this stack:

- **GlobalExceptionHandler** (`@RestControllerAdvice`) turns every exception into
  a consistent `ErrorResponse` JSON body with the right HTTP status.
- **ApiResponse<T>** wraps every *successful* response in the same envelope
  (`success`, `message`, `data`, `timestamp`), so clients never have to guess
  the shape of a response.
- **RequestLoggingFilter** stamps every request with a correlation id
  (`X-Request-Id`), logs entry/exit with duration, and puts the id into the
  SLF4J MDC so every log line for that request can be correlated, even across
  threads.

### Package layout

```
com.company.ems
├── EmsApplication.java
├── config/             OpenAPI metadata, JPA auditing
├── controller/          DepartmentController, EmployeeController
├── service/              interfaces
│   └── impl/             implementations
├── repository/          Spring Data JPA repositories
├── model/entity/         BaseEntity, Department, Employee
├── dto/
│   ├── request/          input payloads (validated)
│   └── response/         output payloads
├── mapper/               manual entity <-> DTO mapping
├── exception/            custom exceptions + ErrorResponse + GlobalExceptionHandler
├── response/             ApiResponse<T> success envelope
└── filter/               RequestLoggingFilter
```

### Domain model

- **Department**: `id, name, code, description, createdAt, updatedAt`
- **Employee**: `id, firstName, lastName, email, phoneNumber, designation, salary,
  dateOfJoining, active, department (many-to-one), createdAt, updatedAt`

Every entity extends `BaseEntity`, which carries the id, audit timestamps
(populated automatically via Spring Data JPA auditing) and an optimistic-locking
`@Version` column.

---

## 3. Running it

### Option A — Local, no Docker (H2 in-memory DB)

Requires Java 21 and Maven (or use the included wrapper if you add one).

```bash
mvn spring-boot:run
```

This activates the `dev` profile by default (see `application.yml`), which uses
an in-memory H2 database — nothing to install, data resets on restart. The H2
console is available at `http://localhost:8080/h2-console`
(JDBC URL: `jdbc:h2:mem:emsdb`, user `sa`, empty password).

### Option B — Docker Compose (PostgreSQL, closer to a real deployment)

Requires Docker and Docker Compose.

```bash
docker compose up --build
```

This builds the app image (multi-stage Dockerfile: Maven build stage → slim
JRE runtime stage running as a non-root user) and starts it alongside a
PostgreSQL container. The app waits for Postgres to report healthy before
starting. Once up:

- API base URL: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`

Stop everything with `docker compose down` (add `-v` to also drop the
Postgres data volume).

### Running the tests

```bash
mvn test
```

---

## 4. API reference & curl examples

All endpoints return the `ApiResponse` envelope on success:

```json
{
  "success": true,
  "message": "Employee created successfully",
  "data": { "...": "..." },
  "timestamp": "2026-06-26T10:15:30Z"
}
```

...and the `ErrorResponse` envelope on failure:

```json
{
  "success": false,
  "status": 404,
  "error": "RESOURCE_NOT_FOUND",
  "message": "Employee not found with id: 99",
  "path": "/api/v1/employees/99",
  "timestamp": "2026-06-26T10:15:30Z"
}
```

### Departments

**Create a department**
```bash
curl -X POST http://localhost:8080/api/v1/departments \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Engineering",
        "code": "ENG",
        "description": "Builds and maintains the product"
      }'
```

**List all departments**
```bash
curl http://localhost:8080/api/v1/departments
```

**Get a department by id**
```bash
curl http://localhost:8080/api/v1/departments/1
```

**Update a department**
```bash
curl -X PUT http://localhost:8080/api/v1/departments/1 \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Engineering",
        "code": "ENG",
        "description": "Builds, ships and operates the product"
      }'
```

**Delete a department** (fails with 409 if employees are still assigned to it)
```bash
curl -X DELETE http://localhost:8080/api/v1/departments/1
```

### Employees

**Create an employee**
```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
        "firstName": "Ada",
        "lastName": "Lovelace",
        "email": "ada.lovelace@example.com",
        "phoneNumber": "+15551234567",
        "designation": "Principal Engineer",
        "salary": 125000.00,
        "dateOfJoining": "2024-01-15",
        "departmentId": 1
      }'
```

**Get an employee by id**
```bash
curl http://localhost:8080/api/v1/employees/1
```

**List/search employees** — supports pagination, sorting and optional filters
(`departmentId`, `active`, free-text `search` across first/last name and email):
```bash
# Plain pagination
curl "http://localhost:8080/api/v1/employees?page=0&size=10&sort=lastName,asc"

# Filter by department and active status
curl "http://localhost:8080/api/v1/employees?departmentId=1&active=true"

# Free-text search
curl "http://localhost:8080/api/v1/employees?search=ada"
```

**Update an employee (full update)**
```bash
curl -X PUT http://localhost:8080/api/v1/employees/1 \
  -H "Content-Type: application/json" \
  -d '{
        "firstName": "Ada",
        "lastName": "Lovelace",
        "email": "ada.lovelace@example.com",
        "phoneNumber": "+15551234567",
        "designation": "Distinguished Engineer",
        "salary": 145000.00,
        "dateOfJoining": "2024-01-15",
        "departmentId": 1
      }'
```

**Activate / deactivate an employee (partial update)**
```bash
curl -X PATCH http://localhost:8080/api/v1/employees/1/status \
  -H "Content-Type: application/json" \
  -d '{ "active": false }'
```

**Delete an employee**
```bash
curl -X DELETE http://localhost:8080/api/v1/employees/1
```

### Operational endpoints

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/v3/api-docs        # raw OpenAPI spec
```
Or open `http://localhost:8080/swagger-ui.html` in a browser for interactive docs.

---

## 5. Using Postman instead

Import [`postman/EMS-API.postman_collection.json`](postman/EMS-API.postman_collection.json)
into Postman. It defines a `baseUrl` collection variable (defaults to
`http://localhost:8080/api/v1`) plus `departmentId` / `employeeId` variables you
can update after creating your first records. Requests are grouped under
**Departments**, **Employees** and **Ops**, in the same order as the curl
examples above.

---

## 6. Notable design choices (and why)

- **DTOs in both directions** — controllers never accept or return JPA entities
  directly, which keeps the persistence model free to evolve without breaking
  the public API contract, and avoids leaking lazy-loading proxies into JSON.
- **Manual mappers instead of MapStruct** — kept dependency-free and easy to
  step through for learning purposes. Swap in MapStruct if the mapping surface
  grows enough to justify the build-time code generation.
- **One dynamic JPQL query for employee search** — a single `@Query` with
  null-safe optional predicates handles "list all", "filter by department",
  "filter by status" and "free-text search" without building query strings by
  hand or introducing the Specification API for a search this simple.
- **Business-rule exceptions are distinct from not-found/validation** —
  `BusinessRuleViolationException` (e.g. "can't delete a department with
  employees in it") is modeled separately from `ResourceNotFoundException` and
  bean-validation errors, because they map to different HTTP semantics and
  different client-side handling.
- **Correlation id via MDC** — every log line for a request carries the same
  `requestId`, which is what makes grepping a corporate log aggregator for "all
  log lines belonging to this one failed request" actually possible.
- **Optimistic locking (`@Version`)** on `BaseEntity` — a deliberate nod to
  what's needed once more than one instance of the service can update the same
  row concurrently, even though a single-instance demo won't exercise it.

## 7. Possible next steps

This project deliberately stops short of things that are simple to bolt on but
would add noise to a learning reference:

- Authentication/authorization (Spring Security + JWT/OAuth2)
- Rate limiting / API gateway concerns
- Flyway/Liquibase migrations instead of `ddl-auto: update`
- Caching (Spring Cache + Redis) for read-heavy endpoints
- Distributed tracing (Micrometer Tracing + OpenTelemetry) on top of the
  existing correlation-id logging
- CI pipeline (GitHub Actions) running `mvn verify` and building/pushing the
  Docker image

## 8. A note on Spring Boot 4

Spring Boot 4.x (built on Spring Framework 7) is now the version line the
Spring team recommends for new projects, but it shipped recently enough that
its modularized starters and changed defaults are still settling across the
ecosystem. This project targets the last 3.x line (3.5, in active OSS support
through June 2026) since it's the most stable, well-documented target for a
learning/reference project; the layered architecture here carries over to a
4.x upgrade largely unchanged.
