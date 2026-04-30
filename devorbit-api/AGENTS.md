<!-- Parent: ../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# devorbit-api

## Purpose
Spring Boot backend API for the DevOrbit platform, providing RESTful endpoints for course management, GitHub repository data, authentication, and administrative functions. Built with Java 21 and Spring Boot 4.x, this service handles data persistence, external API integrations (GitHub), and serves both the web and mobile applications.

## Key Files
| File | Description |
|------|-------------|
| `pom.xml` | Maven project configuration with Spring Boot dependencies |
| `Dockerfile` | Containerization configuration for the API service |
| `src/main/resources/application.yaml` | Spring Boot configuration (database, JWT, etc.) |
| `src/main/resources/data.sql` | Database initialization script |
| `src/main/java/vn/edu/uit/devorbit_api/DevorbitApiApplication.java` | Main Spring Boot application class |

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `src/main/java/vn/edu/uit/devorbit_api/config/` | Spring configuration classes (JWT, Security, GitHub, OpenAPI) |
| `src/main/java/vn/edu/uit/devorbit_api/controller/` | REST controllers organized by access level |
| `src/main/java/vn/edu/uit/devorbit_api/dto/` | Data transfer objects (admin, publicapi, student) |
| `src/main/java/vn/edu/uit/devorbit_api/entity/` | JPA entity classes |
| `src/main/java/vn/edu/uit/devorbit_api/exception/` | Custom exception handling |
| `src/main/java/vn/edu/uit/devorbit_api/repository/` | Spring Data JPA repositories |
| `src/main/java/vn/edu/uit/devorbit_api/service/` | Business logic services |
| `src/test/` | Unit and integration tests |
| `target/` | Maven build artifacts |

## For AI Agents

### Working In This Directory
- Use Maven for dependency management and builds
- Follow Spring Boot conventions for configuration and structure
- Controllers are organized by access level: Admin*, Public*, Student*
- JWT authentication required for protected endpoints
- GitHub API integration requires token configuration

### Testing Requirements
- Run `mvn test` for unit tests
- Integration tests require PostgreSQL database
- Test JWT authentication flows
- Verify GitHub API rate limiting handling

### Common Patterns
- RESTful API design with proper HTTP status codes
- JWT-based authentication with role-based access control
- Spring Data JPA for database operations
- Lombok for reducing boilerplate code
- OpenAPI/Swagger for API documentation

## Dependencies

### Internal
- `../devorbit-web/` - Consumes API endpoints
- `../devorbit-mobile/` - Consumes API endpoints

### External
- Spring Boot 4.x - Framework
- Spring Data JPA - ORM
- PostgreSQL - Database
- JWT - Authentication
- GitHub API - Repository data
- Lombok - Code generation
- OpenAPI/Swagger - API documentation

<!-- MANUAL: -->