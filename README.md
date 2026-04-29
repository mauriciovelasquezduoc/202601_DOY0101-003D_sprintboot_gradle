# Alumnos API — version00 (Gradle)

REST API para gestión de alumnos construida con Spring Boot 3, siguiendo arquitectura hexagonal y una pirámide de tests completa.

---

## Tabla de contenidos

- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Requisitos previos](#requisitos-previos)
- [Configuración local](#configuración-local)
- [Operación con Docker](#operación-con-docker)
- [API Reference](#api-reference)
- [Pirámide de tests](#pirámide-de-tests)
- [Cobertura y calidad](#cobertura-y-calidad)
- [CI/CD](#cicd)

---

## Stack tecnológico

| Categoría | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 3.4.5 |
| Build tool | Gradle | 9.x |
| Persistencia | Spring Data JPA + Hibernate | 6.x |
| Base de datos | H2 (in-memory) | 2.x |
| Seguridad | Spring Security | 6.x |
| Documentación API | SpringDoc OpenAPI (Swagger) | 2.5.0 |
| Mapeo de objetos | Lombok | 1.18.x |
| Tests unitarios | JUnit 5 + Mockito | 5.x / 5.x |
| Tests de integración | Spring Boot Test / MockMvc | 3.4.5 |
| Tests de repositorio | @DataJpaTest | 3.4.5 |
| Tests de aceptación | Cucumber | 7.20.1 |
| Tests de contrato | Spring Cloud Contract | 4.2.1 |
| Cobertura | JaCoCo | 0.8.13 |
| Mutation testing | PIT (Pitest) | 1.19.x |
| Análisis estático | SonarQube / SonarCloud | — |
| Contenedores | Docker + Docker Compose | — |

---

## Arquitectura

El proyecto sigue **arquitectura hexagonal (ports & adapters)**:

```
src/main/java/cl/duocuc/alumnos/
├── domain/                     # Entidades de dominio puras (sin dependencias)
│   └── Alumno.java
├── application/                # Casos de uso / lógica de negocio
│   └── AlumnoService.java
├── config/                     # Configuración transversal
│   ├── GlobalExceptionHandler.java
│   └── SecurityConfig.java
└── infrastructure/             # Adaptadores (entrada y salida)
    ├── controller/             # Adaptador de entrada: REST
    │   └── AlumnoController.java
    ├── entity/                 # Adaptador de salida: JPA
    │   └── AlumnoEntity.java
    ├── mapper/                 # Conversión dominio ↔ infraestructura
    │   └── AlumnoMapper.java
    ├── repository/             # Puerto de salida: Spring Data
    │   └── AlumnoRepository.java
    └── config/                 # Configuración de infraestructura
        ├── OpenApiConfig.java
        └── WebConfig.java
```

---

## Requisitos previos

| Herramienta | Versión mínima | Verificar |
|---|---|---|
| JDK | 21 | `java -version` |
| Gradle | 9.x (o usar `./gradlew`) | `gradle -version` |
| Docker | 24.x | `docker -v` |
| Docker Compose | 2.x | `docker compose version` |
| Git | 2.x | `git --version` |

---

## Configuración local

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd jobs/java/version00
```

### 2. Compilar el proyecto

```bash
# Con el wrapper incluido (recomendado)
./gradlew build -x test

# O con Gradle instalado globalmente
gradle build -x test
```

### 3. Ejecutar la aplicación

```bash
./gradlew bootRun
```

La aplicación levanta en `http://localhost:8080`.

### 4. Verificar que funciona

```bash
curl http://localhost:8080/alumnos
# Respuesta esperada: []
```

### 5. Acceder a la documentación interactiva

| Interfaz | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| ReDoc | http://localhost:8080/redoc.html |
| H2 Console | http://localhost:8080/h2-console |

> **H2 Console:** JDBC URL `jdbc:h2:mem:testdb`, usuario `sa`, sin contraseña.

### 6. Perfiles disponibles

```bash
# Perfil de desarrollo
./gradlew bootRun --args='--spring.profiles.active=dev'

# Perfil de producción
./gradlew bootRun --args='--spring.profiles.active=prod'
```

---

## Operación con Docker

### Dockerfile (build multi-etapa)

El `Dockerfile` usa un build en dos etapas:

1. **Etapa build:** imagen `gradle:8.13.0-jdk21` — compila y empaqueta el jar
2. **Etapa runtime:** imagen `eclipse-temurin:21-jre-jammy` — solo el JRE, imagen final liviana (~200MB)

Características de seguridad:
- Corre con usuario no-root (`appuser`)
- Healthcheck integrado
- Sin herramientas de build en la imagen final

#### Construir la imagen manualmente

```bash
docker build -t alumnos-app:latest .
```

#### Ejecutar el contenedor manualmente

```bash
docker run -d \
  --name alumnos-app \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  alumnos-app:latest
```

#### Verificar el contenedor

```bash
# Ver logs
docker logs alumnos-app -f

# Ver estado del healthcheck
docker inspect --format='{{.State.Health.Status}}' alumnos-app

# Detener y eliminar
docker stop alumnos-app && docker rm alumnos-app
```

### Docker Compose

La forma recomendada para levantar el entorno completo:

```bash
# Levantar en background
docker compose up -d

# Ver logs en tiempo real
docker compose logs -f

# Ver estado de los servicios
docker compose ps

# Detener y eliminar contenedores
docker compose down

# Detener, eliminar contenedores y volúmenes
docker compose down -v
```

> La aplicación estará disponible en `http://localhost:8080` una vez que el healthcheck reporte `healthy` (~30 segundos).

---

## API Reference

### Base URL

```
http://localhost:8080/alumnos
```

### Endpoints

| Método | Ruta | Descripción | Body |
|---|---|---|---|
| `GET` | `/alumnos` | Listar todos los alumnos | — |
| `POST` | `/alumnos` | Crear un alumno | `{"nombre":"Juan","apellido":"Pérez"}` |
| `PUT` | `/alumnos/{id}` | Actualizar un alumno | `{"nombre":"Juan","apellido":"Soto"}` |
| `DELETE` | `/alumnos/{id}` | Eliminar un alumno | — |
| `GET` | `/alumnos/export` | Exportar alumnos a CSV | — |
| `POST` | `/alumnos/import` | Importar alumnos desde CSV | `Juan,Pérez\nAna,López` |

### Ejemplos con curl

```bash
# Crear alumno
curl -X POST http://localhost:8080/alumnos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Pérez"}'

# Listar alumnos
curl http://localhost:8080/alumnos

# Actualizar alumno (id=1)
curl -X PUT http://localhost:8080/alumnos/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Soto"}'

# Eliminar alumno (id=1)
curl -X DELETE http://localhost:8080/alumnos/1

# Exportar CSV
curl http://localhost:8080/alumnos/export

# Importar CSV
curl -X POST http://localhost:8080/alumnos/import \
  -H "Content-Type: text/plain" \
  -d $'Juan,Pérez\nAna,López'
```

---

## Pirámide de tests

El proyecto implementa una pirámide de tests completa con 5 capas:

```
                    ╔══════════════════════╗
                    ║   CONTRACT TESTS     ║  3 contratos
                    ║  Spring Cloud        ║  (producer side)
                    ╚══════════════════════╝
                  ╔════════════════════════════╗
                  ║     ACCEPTANCE TESTS       ║  7 escenarios
                  ║       Cucumber BDD         ║  E2E con HTTP real
                  ╚════════════════════════════╝
              ╔══════════════════════════════════════╗
              ║        INTEGRATION TESTS             ║
              ║  @WebMvcTest (8) + @SpringBootTest(2)║
              ╚══════════════════════════════════════╝
          ╔══════════════════════════════════════════════╗
          ║           REPOSITORY TESTS                   ║
          ║           @DataJpaTest (6)                   ║
          ╚══════════════════════════════════════════════╝
      ╔══════════════════════════════════════════════════════╗
      ║                  UNIT TESTS                          ║
      ║   AlumnoServiceTest(5) + AlumnoMapperTest(4)         ║
      ║   GlobalExceptionHandlerTest(2)  = 11 tests          ║
      ╚══════════════════════════════════════════════════════╝
```

### Capa 1 — Unit Tests (11 tests)

Tests rápidos, sin Spring context, con mocks de Mockito.

| Clase de test | Tests | Qué verifica |
|---|---|---|
| `AlumnoServiceTest` | 5 | Lógica de negocio: listar, crear, actualizar, eliminar |
| `AlumnoMapperTest` | 4 | Conversión `Alumno` ↔ `AlumnoEntity`, incluyendo nulos |
| `GlobalExceptionHandlerTest` | 2 | Handler de excepciones retorna HTTP 500 con mensaje |

```bash
# Ejecutar solo unit tests
./gradlew test --tests "cl.duocuc.alumnos.AlumnoServiceTest"
./gradlew test --tests "cl.duocuc.alumnos.infrastructure.mapper.AlumnoMapperTest"
```

### Capa 2 — Repository Tests (6 tests)

Tests con `@DataJpaTest` — levanta solo el contexto JPA con H2 en memoria. Verifica que las operaciones de base de datos funcionan correctamente.

| Clase de test | Tests | Qué verifica |
|---|---|---|
| `AlumnoRepositoryTest` | 6 | save, findAll, findById, deleteById, update contra H2 real |

```bash
./gradlew test --tests "cl.duocuc.alumnos.infrastructure.repository.AlumnoRepositoryTest"
```

### Capa 3 — Integration Tests (10 tests)

Tests con contexto Spring parcial o completo.

| Clase de test | Anotación | Tests | Qué verifica |
|---|---|---|---|
| `AlumnoControllerTest` | `@WebMvcTest` | 8 | Todos los endpoints REST con MockMvc y mocks del servicio |
| `DemoApplicationTest` | `@SpringBootTest` | 2 | Contexto completo levanta sin errores |

```bash
./gradlew test --tests "cl.duocuc.alumnos.infrastructure.controller.AlumnoControllerTest"
```

### Capa 4 — Acceptance Tests / BDD (7 escenarios)

Tests de aceptación escritos en Gherkin con Cucumber. Levantan el servidor completo en un puerto aleatorio y hacen llamadas HTTP reales con `TestRestTemplate`.

**Feature:** `src/test/resources/features/alumnos.feature`

| Escenario | Qué verifica |
|---|---|
| Listar alumnos cuando no hay ninguno | GET /alumnos retorna lista vacía |
| Crear un alumno exitosamente | POST /alumnos persiste y retorna el alumno |
| Listar alumnos después de crear uno | Flujo crear → listar |
| Actualizar un alumno existente | PUT /alumnos/{id} modifica el recurso |
| Eliminar un alumno existente | DELETE /alumnos/{id} elimina el recurso |
| Exportar alumnos a CSV | GET /alumnos/export retorna formato CSV |
| Importar alumnos desde CSV | POST /alumnos/import procesa el CSV |

> La BD se limpia antes de cada escenario con `cleanup.sql` para garantizar aislamiento.

```bash
./gradlew test --tests "cl.duocuc.alumnos.cucumber.CucumberRunnerTest"
```

### Capa 5 — Contract Tests (3 contratos)

Tests de contrato con Spring Cloud Contract (producer side). Verifican que la API cumple los contratos definidos, garantizando compatibilidad con los consumidores.

**Contratos:** `src/test/resources/contracts/alumnos/`

| Contrato | Qué verifica |
|---|---|
| `listar_alumnos.groovy` | GET /alumnos retorna JSON array |
| `crear_alumno.groovy` | POST /alumnos retorna alumno con id numérico |
| `exportar_csv.groovy` | GET /alumnos/export retorna texto plano |

```bash
# Generar y ejecutar tests de contrato
./gradlew generateContractTests contractTest
```

### Ejecutar todos los tests

```bash
# Todos los tests + reporte de cobertura
./gradlew clean test jacocoTestReport

# Build completo incluyendo contratos y verificación de cobertura
./gradlew clean generateContractTests contractTest test jacocoTestReport check
```

---

## Cobertura y calidad

### JaCoCo

El build falla si la cobertura baja del **80%** en instrucciones y ramas.

```bash
# Generar reporte HTML
./gradlew jacocoTestReport

# Ver reporte
open build/reports/jacoco/test/html/index.html
```

### Mutation Testing (PIT)

Verifica la calidad real de los tests mutando el código fuente. El build falla si el mutation score baja del **80%**.

```bash
./gradlew pitest

# Ver reporte
open build/reports/pitest/index.html
```

### SonarQube / SonarCloud

```bash
# Requiere token configurado en sonar.token
./gradlew sonar -Dsonar.token=<tu-token>
```

---

## CI/CD

El proyecto incluye configuración de GitHub Actions en `.github/workflows/ci.yml`.

El pipeline está dividido en **4 jobs secuenciales** que garantizan trazabilidad completa desde el desarrollo hasta la producción:

```
┌─────────────────────────────────────────────────────────────────┐
│  JOB 1: Build & Test                                            │
│  ├── ./gradlew clean check jacocoTestReport                     │
│  ├── Cobertura JaCoCo ≥ 80% (falla si no se cumple)            │
│  ├── Artefacto: jacoco-coverage-report                          │
│  ├── Artefacto: test-results                                    │
│  └── Artefacto: cucumber-report                                 │
└────────────────────┬────────────────────────────────────────────┘
                     │ (solo si tests pasan)
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│  JOB 2: Security Analysis                                       │
│  ├── SonarCloud (SAST) — Quality Gate bloquea si falla         │
│  │   sonar.qualitygate.wait=true                                │
│  └── Snyk (SCA) — Bloquea si hay vulnerabilidades HIGH/CRITICAL │
└────────────────────┬────────────────────────────────────────────┘
                     │ (solo si seguridad pasa)
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│  JOB 3: Docker Build & Push                                     │
│  ├── Build imagen multi-stage (gradle → jre)                   │
│  ├── Push a GitHub Container Registry (GHCR)                   │
│  └── Tags: latest (main), sha-XXXXX, branch-name               │
└────────────────────┬────────────────────────────────────────────┘
                     │ (solo en push a main)
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│  JOB 4: Deploy — environment: production                        │
│  ├── docker compose up -d --build                               │
│  ├── Health check: espera estado "healthy"                      │
│  └── Smoke test: curl /alumnos                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Secrets requeridos en GitHub

Configurar en **Settings → Secrets and variables → Actions**:

| Secret | Descripción |
|---|---|
| `SONAR_TOKEN` | Token de autenticación de SonarCloud |
| `SONAR_ORGANIZATION` | Organización en SonarCloud |
| `SNYK_TOKEN` | Token de autenticación de Snyk |

> `GITHUB_TOKEN` es generado automáticamente por GitHub Actions.

### Dependabot

El archivo `.github/dependabot.yml` configura actualizaciones automáticas semanales para:
- Dependencias Gradle (`build.gradle`)
- Dependencias de GitHub Actions (`.github/workflows/`)

---

## Orquestación con Kubernetes

Además de Docker Compose, el proyecto incluye manifiestos Kubernetes en `k8s/` para despliegues en entornos de mayor escala.

```
k8s/
├── deployment.yaml   # 2 réplicas, RollingUpdate, health checks, límites de recursos
├── service.yaml      # ClusterIP en puerto 80 → 8080
├── hpa.yaml          # HorizontalPodAutoscaler: 2-5 réplicas según CPU/memoria
└── secret.yaml       # Plantilla de secrets (NO commitear valores reales)
```

### Características del despliegue Kubernetes

- **Alta disponibilidad:** 2 réplicas mínimas con `RollingUpdate` (sin downtime)
- **Escalabilidad automática:** HPA escala de 2 a 5 pods según carga (CPU > 70%, memoria > 80%)
- **Seguridad:** usuario no-root, `allowPrivilegeEscalation: false`, capabilities dropeadas
- **Gestión de secrets:** variables sensibles via `secretKeyRef`
- **Límites de recursos:** requests y limits definidos para CPU y memoria

### Comandos de despliegue en Kubernetes

```bash
# Aplicar todos los manifiestos
kubectl apply -f k8s/

# Ver estado del despliegue
kubectl get pods -l app=alumnos-app
kubectl get hpa alumnos-app-hpa

# Ver logs
kubectl logs -l app=alumnos-app -f

# Escalar manualmente
kubectl scale deployment alumnos-app --replicas=3
```

---

## Uso de Inteligencia Artificial

Este proyecto utilizó herramientas de IA como apoyo en las siguientes áreas:

| Herramienta | Uso |
|---|---|
| Kiro (Amazon) | Generación de código base, configuración de plugins Gradle, estructura de tests |
| — | Todas las decisiones de arquitectura, justificaciones técnicas y reflexiones son propias del equipo |

> Todo contenido generado con IA fue revisado, validado y adaptado por el equipo. Las reflexiones individuales a continuación son de autoría propia, sin asistencia de IA.
> Referencia: https://bibliotecas.duoc.cl/ia

---

## Reflexiones individuales

### Integrante 1

> *[Escribir aquí la reflexión personal sobre el aprendizaje obtenido en este proyecto: qué fue lo más desafiante, qué conceptos de DevOps quedaron más claros, y cuál fue tu contribución específica al equipo. Mínimo 150 palabras. Sin uso de IA.]*

### Integrante 2

> *[Escribir aquí la reflexión personal sobre el aprendizaje obtenido en este proyecto: qué fue lo más desafiante, qué conceptos de DevOps quedaron más claros, y cuál fue tu contribución específica al equipo. Mínimo 150 palabras. Sin uso de IA.]*

---

## Estructura del proyecto

```
version00/
├── src/
│   ├── main/
│   │   ├── java/cl/duocuc/alumnos/
│   │   │   ├── domain/
│   │   │   ├── application/
│   │   │   ├── config/
│   │   │   └── infrastructure/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── static/redoc.html
│   └── test/
│       ├── java/cl/duocuc/alumnos/
│       │   ├── AlumnoServiceTest.java
│       │   ├── DemoApplicationTest.java
│       │   ├── config/GlobalExceptionHandlerTest.java
│       │   ├── contract/AlumnoContractBase.java
│       │   ├── cucumber/
│       │   │   ├── AlumnoSteps.java
│       │   │   ├── CucumberRunnerTest.java
│       │   │   └── CucumberSpringConfiguration.java
│       │   └── infrastructure/
│       │       ├── controller/AlumnoControllerTest.java
│       │       ├── mapper/AlumnoMapperTest.java
│       │       └── repository/AlumnoRepositoryTest.java
│       └── resources/
│           ├── cleanup.sql
│           ├── features/alumnos.feature
│           └── contracts/alumnos/
│               ├── listar_alumnos.groovy
│               ├── crear_alumno.groovy
│               └── exportar_csv.groovy
├── build.gradle
├── settings.gradle
├── gradle.properties
├── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## Licencia

MIT
