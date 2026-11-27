# TaskFlow Decision Log

## 2026-11-26 — Java 21 and Spring Boot 4.2.0

**Context:** New project used as an infrastructure lab (AWS, Terraform, Docker, Kubernetes). Spring Initializr no longer offers the 3.x line; 4.x is the current one.

**Decision:** Java 21 (LTS) with Spring Boot 4.2.0 and Maven.

**Alternatives:** Spring Boot 3.5.x (no longer in Initializr's catalog and past community support); Java 25 (newest LTS, but adoption in companies is still ramping up).

**Consequences:** Some starter and library names differ from Boot 3. Many companies still run 3.x, but the core concepts (REST, JPA, validation, actuator, testing) are the same.

## 2026-11-26 — Package by feature

**Context:** The V1 API has a single domain (tasks). The code can be grouped by layer (`controller/`, `service/`, `repository/`) or by feature (`task/`).

**Decision:** Package by feature: `com.taskflow.task` holds the entity, enum, repository, service, controller and DTOs (in `task.dto`). Cross-cutting error handling lives in `com.taskflow.error`.

**Alternatives:** Package by layer, which is common in tutorials but spreads one feature across many packages.

**Consequences:** Everything about tasks is in one place and classes can stay package-private where possible. A future feature gets its own package.

## 2026-11-26 — Task timestamp is a server-set createdAt

**Context:** The original V1 scope listed a "date" field without saying what it meant. `date` is also a reserved word in SQL.

**Decision:** The field is `createdAt` (`Instant`), set by the server when the task is first persisted and never updated. Clients cannot send it.

**Alternatives:** A client-provided `dueDate` (`LocalDate`), which adds validation rules (e.g. past dates) without adding infrastructure value.

**Consequences:** Request DTOs have no date field. A due date can be added later as a separate, optional field.

## 2026-11-26 — ProblemDetail as the error format

**Context:** The API needs a consistent JSON body for errors (validation, not found, malformed requests, unexpected failures).

**Decision:** Use Spring's `ProblemDetail` (RFC 9457). A `@RestControllerAdvice` extends `ResponseEntityExceptionHandler`, adds a field-level `errors` map for validation failures, and maps `TaskNotFoundException` to 404. Unexpected exceptions return a generic 500 without stack traces.

**Alternatives:** A custom error DTO, which is more code to maintain and not a standard.

**Consequences:** Error responses follow a public standard and Spring's own exceptions are already handled by the base class.

## 2026-11-26 — ddl-auto=create-drop for now, schema migration deferred

**Context:** Locally the app runs on in-memory H2. Production will use PostgreSQL on RDS, where the schema must survive restarts and deployments.

**Decision:** Use `spring.jpa.hibernate.ddl-auto=create-drop` for local development and tests. Schema migration (e.g. Flyway with `ddl-auto=validate`) is decided in the RDS step.

**Alternatives:** Adding Flyway from the start, which is more setup before there is a real database to migrate.

**Consequences:** Data is lost on every restart locally. This setting must not reach the RDS environment.
