# TaskFlow

API REST de tareas pendientes, usada como laboratorio de infraestructura. Lo valioso es la infraestructura alrededor, no la app.

## Stack
- Java 21, Spring Boot 4.2.0, Maven
- Tests: JUnit 5 y Mockito (TDD cuando sea posible)
- Base de datos: H2 en local; PostgreSQL (RDS) después

## Alcance de la V1
- Modelo Tarea: id, título, descripción, estado (PENDIENTE, EN_PROGRESO, HECHA) y fecha
- CRUD REST con validaciones y manejo de errores
- Endpoint de health (`/actuator/health`)
- Tests unitarios y de integración

## Fuera de alcance
Autenticación, frontend, paginación, cachés y mensajería.

## Reglas de trabajo
- Uriel escribe a mano la infraestructura y los comandos; Claude Code ayuda con andamiaje, tests y revisión.
- Antes de aceptar un cambio, Uriel debe poder explicarlo.
- Nunca subir secretos, claves ni credenciales al repo.
- Las decisiones importantes se registran en `docs/decisiones.md`.

## V1 terminada cuando
Corre en EC2 detrás de un ALB con health checks, usa RDS, tiene logs y alarmas en CloudWatch, y el README incluye un diagrama.
