# TaskFlow

A small REST API for to-do tasks, built with Java 21 and Spring Boot 4.2.0.

The app itself is intentionally simple: TaskFlow is an infrastructure lab, and the goal is to run it on AWS (EC2 behind an ALB, RDS, CloudWatch). Architectural decisions are recorded in [`docs/decisions.md`](docs/decisions.md).

## Requirements

- Java 21

Maven is not needed; the project includes the Maven Wrapper (`./mvnw`).

## Run

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080` using an in-memory H2 database. Data is lost on every restart.

## API

| Method | Path | Description | Success |
|---|---|---|---|
| POST | `/api/tasks` | Create a task | 201 + `Location` header |
| GET | `/api/tasks` | List all tasks | 200 |
| GET | `/api/tasks/{id}` | Get one task | 200 |
| PUT | `/api/tasks/{id}` | Replace a task | 200 |
| DELETE | `/api/tasks/{id}` | Delete a task | 204 |
| GET | `/actuator/health` | Health check | 200 |

A task has `id`, `title` (required, max 100), `description` (optional, max 1000), `status` (`PENDING`, `IN_PROGRESS`, `DONE`) and `createdAt` (set by the server). New tasks start as `PENDING`.

Errors are returned as [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) Problem Details (`application/problem+json`), for example:

```json
{"status": 400, "title": "Bad Request", "detail": "Request validation failed", "errors": {"title": "must not be blank"}}
```

## Try it

```bash
# Create
curl -i -X POST localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title": "Buy milk", "description": "2 liters"}'

# List
curl localhost:8080/api/tasks

# Update (status is required on PUT)
curl -X PUT localhost:8080/api/tasks/1 \
  -H 'Content-Type: application/json' \
  -d '{"title": "Buy milk", "description": "2 liters", "status": "DONE"}'

# Delete
curl -i -X DELETE localhost:8080/api/tasks/1

# Health
curl localhost:8080/actuator/health
```
