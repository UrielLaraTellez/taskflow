# TaskFlow Decision Log

## 2026-11-26 — Java 21 and Spring Boot 4.2.0

**Context:** New project used as an infrastructure lab (AWS, Terraform, Docker, Kubernetes). Spring Initializr no longer offers the 3.x line; 4.x is the current one.

**Decision:** Java 21 (LTS) with Spring Boot 4.2.0 and Maven.

**Alternatives:** Spring Boot 3.5.x (no longer in Initializr's catalog and past community support); Java 25 (newest LTS, but adoption in companies is still ramping up).

**Consequences:** Some starter and library names differ from Boot 3. Many companies still run 3.x, but the core concepts (REST, JPA, validation, actuator, testing) are the same.
