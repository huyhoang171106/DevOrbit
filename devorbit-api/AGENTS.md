<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-16 -->

# devorbit-api

## Purpose
Spring Boot backend API for the DevOrbit platform, providing RESTful endpoints for course management, GitHub repository data, AI-powered knowledge graph & roadmap generation, photobooth frame management, authentication, and administrative functions. Built with Java 21 and Spring Boot 4.0.6, this service handles data persistence, external API integrations (GitHub, Supabase Storage, email), and serves both the web and mobile applications.

## Key Files
| File | Description |
|------|-------------|
| `pom.xml` | Maven project configuration with Spring Boot, Security, Data JPA, JWT (jjwt 0.12.6), PostgreSQL, Flyway, OpenAPI dependencies |
| `Dockerfile` | Containerization configuration for the API service |
| `mvnw.cmd` | Maven wrapper for Windows |
| `src/main/resources/application.yaml` | Spring Boot configuration (database, JWT, GitHub, Supabase) |
| `src/main/resources/application-local.yaml` | Local development overrides |
| `src/main/resources/data.sql` | Database seed data (courses, repos, tech stacks) |
| `src/main/resources/schema.sql` | Full schema definition |
| `src/main/resources/schema-complete.sql` | Complete schema with all tables, enums, indexes |
| `src/main/resources/db/migration/V002__create_photobooth_frames.sql` | Flyway migration for photobooth frames table |
| `src/main/java/vn/edu/uit/devorbit_api/DevorbitApiApplication.java` | Main Spring Boot application class |

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `.pi/agents/` | Agent supervisor configuration for AI-assisted development |
| `src/main/java/vn/edu/uit/devorbit_api/config/` | Spring configuration classes (JWT, Security, GitHub, OpenAPI, Jackson, ElectiveGroup) |
| `src/main/java/vn/edu/uit/devorbit_api/constant/` | Application constants (CurriculumConstants) |
| `src/main/java/vn/edu/uit/devorbit_api/controller/` | REST controllers organized by access level (Admin, Public, Student) |
| `src/main/java/vn/edu/uit/devorbit_api/dto/` | Data transfer objects (~30+ DTOs in admin/, publicapi/, student/ packages) |
| `src/main/java/vn/edu/uit/devorbit_api/entity/` | JPA entity classes (~22 entities) |
| `src/main/java/vn/edu/uit/devorbit_api/event/` | Domain events (RelationshipChangedEvent) |
| `src/main/java/vn/edu/uit/devorbit_api/exception/` | Custom exception handling and global error handler |
| `src/main/java/vn/edu/uit/devorbit_api/repository/` | Spring Data JPA repositories (~18 repositories) |
| `src/main/java/vn/edu/uit/devorbit_api/service/` | Business logic services (~22 services) |
| `src/test/` | Unit and integration tests (controller + service tests) |

## For AI Agents

### Working In This Directory
- Use Maven wrapper (`mvnw.cmd`) for builds on Windows
- Follow Spring Boot conventions for configuration and structure
- Controllers are organized by access level: `Admin*`, `Public*`, `Student*`
- JWT authentication required for protected endpoints (`/api/admin/**`, `/api/student/**`)
- Public endpoints: `/api/courses/**`, `/api/repos/**`, `/api/tech-stacks`, `/api/ai/**`, `/api/discovery`, `/api/photobooth-frames/**`
- GitHub API integration requires `github.token` configuration
- Supabase Storage integration for photobooth frame image uploads
- Flyway migrations for schema versioning (in `src/main/resources/db/migration/`)
- AI services in `service/ai/` package: AdviceGenerator, CurriculumMatcher, GraphQueryEngine, RoadmapGenerator, SummaryGenerator

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
- Lombok for reducing boilerplate code
- OpenAPI/Swagger for API documentation
- `@RequiredArgsConstructor` with `final` fields for constructor injection
- Service layer pattern with `@Service` and `@Transactional`

## Dependencies

### Internal
- `../devorbit-web/` - Consumes API endpoints
- `../devorbit-mobile/` - Consumes API endpoints

### External
- Spring Boot 4.0.6 - Framework
- Spring Data JPA - ORM
- Spring Security - Authentication/authorization
- Spring Mail - Email service
- PostgreSQL 16 - Database
- Flyway - Database migration
- JWT (jjwt 0.12.6) - Token-based authentication
- GitHub API - Repository data and scanning
- Supabase Storage - File/image storage
- Lombok - Code generation
- OpenAPI/Swagger (springdoc 2.8.6) - API documentation

<!-- MANUAL: -->
