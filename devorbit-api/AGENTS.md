<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-20 -->

# devorbit-api

## Purpose
Spring Boot backend API for the DevOrbit platform, providing RESTful endpoints for course management, GitHub repository data, AI-powered knowledge graph & roadmap generation, photobooth frame management, authentication, and administrative functions. Built with Java 21 and Spring Boot 4.0.6, this service handles data persistence, external API integrations (GitHub, Supabase Storage, email), and serves both the web and mobile applications.

## Key Files
| File | Description |
|------|-------------|
| `pom.xml` | Maven project configuration with Spring Boot, Security, Data JPA, JWT (jjwt 0.12.6), PostgreSQL, Flyway, OpenAPI dependencies |
| `Dockerfile` | Multi-stage build: Maven 3.9 + Eclipse Temurin 21 → Eclipse Temurin 21-jre-alpine |
| `mvnw.cmd` | Maven wrapper for Windows |
| `.env` | Runtime secrets (DATABASE_URL, JWT_SECRET, GITHUB_TOKEN, SUPABASE_URL/KEY) |
| `src/main/resources/application.yaml` | Spring Boot configuration (database, JWT, GitHub, Supabase, CORS, mail) |
| `src/main/resources/application-local.yaml` | Local development overrides |
| `src/main/resources/data.sql` | Database seed data (courses, repos, tech stacks) |
| `src/main/resources/schema.sql` | Full schema definition (~380 lines) |
| `src/main/resources/schema-complete.sql` | Complete schema with all tables, enums, indexes (~340 lines) |
| `src/main/resources/db/migration/V002__create_photobooth_frames.sql` | Flyway migration for photobooth frames table |
| `src/main/java/vn/edu/uit/devorbit_api/DevorbitApiApplication.java` | Main Spring Boot application class |

## Subdirectories
| Directory | Count | Purpose |
|-----------|-------|---------|
| `config/` | 9 classes | Spring configuration (JWT, Security, GitHub, OpenAPI, Jackson, ElectiveGroup, RepoCandidateDataInitializer) |
| `constant/` | 1 class | Application constants (CurriculumConstants for SE2025) |
| `controller/` | 20 classes | REST controllers: 9 Admin, 6 Public, 2 Student, Health, PhotoboothFrame, FileUpload |
| `dto/` | 43 DTOs | Data transfer objects in admin/ (26), publicapi/ (10), student/ (5), plus root PhotoboothFrameDTO |
| `entity/` | 22 entities | JPA entity classes covering all domain tables |
| `event/` | 1 class | Domain events (RelationshipChangedEvent) |
| `exception/` | 4 classes | Custom exceptions (BadRequest, NotFound, Unauthorized) + global ApiExceptionHandler |
| `repository/` | 18 repos | Spring Data JPA repositories |
| `service/` | 25 services | Business logic services including 5 AI services (ai/ subpackage), auth, course, repo, photobooth, storage |
| `test/` | — | Unit and integration tests (controller + service tests) |

## For AI Agents

### Working In This Directory
- Use Maven wrapper (`mvnw.cmd`) for builds on Windows
- Follow Spring Boot conventions for configuration and structure
- Controllers organized by access level: `Admin*` (9), `Public*` (6), `Student*` (2), plus misc
- JWT auth required for `/api/admin/**` and `/api/student/**` — Public endpoints open
- Public endpoints: `/api/courses/**`, `/api/repos/**`, `/api/tech-stacks`, `/api/ai/**`, `/api/discovery`, `/api/course-relationships`, `/api/photobooth-frames/**`, `/api/health`
- GitHub API integration requires `GITHUB_TOKEN` in `.env` — handles repo scanning, candidate approval
- Supabase Storage integration for photobooth frame image uploads (config via `.env` SUPABASE_URL/KEY/BUCKET)
- Supabase Pooler used for production DB connection (`aws-1-ap-northeast-1.pooler.supabase.com:6543`)
- CORS configured for localhost:3000, localhost:5173/5174, production IPs, Cloudflare tunnel
- Flyway migration: single migration V002 for photobooth frames (DDL via schema.sql, flyway for versioned changes)
- AI services in `service/ai/` package: AdviceGenerator, CurriculumMatcher, GraphQueryEngine, RoadmapGenerator, SummaryGenerator
- `RepoCandidateDataInitializer` seeds initial repo candidates on startup via `CommandLineRunner`

### Testing Requirements
- Run `mvnw.cmd test` for unit tests
- Controller tests use `@WebMvcTest` with mocked services
- H2 in-memory database used for integration tests
- Test JWT authentication flows with `@WithMockUser`
- Verify GitHub API rate limiting handling

### Common Patterns
- RESTful API design with proper HTTP status codes
- JWT-based authentication with role-based access control (ADMIN, STUDENT)
- Spring Data JPA for database operations
- Lombok (`@Data`, `@Builder`, `@RequiredArgsConstructor`) for reducing boilerplate
- OpenAPI/Swagger (springdoc 2.8.6) for API documentation
- `@RequiredArgsConstructor` with `final` fields for constructor injection
- Service layer pattern with `@Service` and `@Transactional`
- Config properties classes (`GithubProperties`, `JwtProperties`) for externalized config
- `@ExceptionHandler` via `ApiExceptionHandler` for consistent error response format

## Dependencies

### Internal
- `../devorbit-web/` — React 19 (Vite 6) frontend consuming API endpoints
- `../devorbit-mobile/` — Kotlin Jetpack Compose Android app consuming API endpoints

### External
| Dependency | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 4.0.6 | Framework (parent POM) |
| Spring Data JPA | 4.0.x | ORM for PostgreSQL |
| Spring Security | 6.x | Authentication/authorization with JWT |
| Spring Mail | 4.0.x | Email service (SMTP Gmail) |
| Spring WebFlux | 4.0.x | WebClient for GitHub API calls |
| PostgreSQL | 16 | Primary database (via Supabase Pooler) |
| Flyway | — | Database migration (schema versioning) |
| jjwt | 0.12.6 | JWT token creation/validation |
| GitHub REST API | v3 | Repository data, scanning |
| Supabase Storage | — | Photobooth frame image uploads |
| Lombok | — | Code generation (getters, builders) |
| springdoc-openapi | 2.8.6 | OpenAPI/Swagger UI documentation |

## Beginner-Friendly Guide

If you are new to this codebase, start with `ARCHITECTURE.md`.
It explains the 3-layer architecture (Controller → Service → Repository)
with step-by-step request lifecycle tracing.

<!-- MANUAL: -->
