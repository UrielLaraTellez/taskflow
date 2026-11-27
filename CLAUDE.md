# TaskFlow

REST API for to-do tasks, used as an infrastructure lab. The value is in the infrastructure around the app, not the app itself.

## Stack
- Java 21, Spring Boot 4.2.0, Maven
- Tests: JUnit 5 and Mockito (TDD when possible)
- Database: H2 locally; PostgreSQL (RDS) later

## V1 scope
- Task model: id, title, description, status (PENDING, IN_PROGRESS, DONE) and date
- REST CRUD with validation and error handling
- Health endpoint (`/actuator/health`)
- Unit and integration tests

## Out of scope
Authentication, frontend, pagination, caching and messaging.

## Working rules
- Everything visible to others (code, comments, commit messages, README, docs) is written in English.
- Uriel writes the infrastructure and commands by hand; Claude Code helps with scaffolding, tests and review.
- Uriel must be able to explain any change before accepting it.
- Never commit secrets, keys or credentials.
- Important decisions are recorded in `docs/decisions.md`.

## V1 is done when
It runs on EC2 behind an ALB with health checks, uses RDS, has logs and alarms in CloudWatch, and the README includes a diagram.
