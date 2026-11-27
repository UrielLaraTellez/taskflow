# Decisiones de TaskFlow

## 2026-11-26 — Java 21 y Spring Boot 4.2.0

**Contexto:** Proyecto nuevo para practicar infraestructura (AWS, Terraform, Docker, Kubernetes). Spring Initializr ya no ofrece la línea 3.x; la vigente es la 4.x.

**Decisión:** Java 21 (LTS) con Spring Boot 4.2.0 y Maven.

**Alternativas:** Spring Boot 3.5.x (fuera del catálogo de Initializr y sin soporte comunitario); Java 25 (LTS más reciente, pero las empresas tardan en adoptarlo).

**Consecuencias:** Algunos nombres de starters y librerías cambian respecto a Boot 3. Muchas empresas siguen en 3.x, pero los conceptos (REST, JPA, validación, actuator, tests) son los mismos.
