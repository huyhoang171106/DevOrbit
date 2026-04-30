<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# DevOrbit

## Purpose
DevOrbit is a comprehensive knowledge-management and learning-support platform specifically designed for UIT (University of Information Technology) students. Built for the university's 20th anniversary, it serves as a real production platform rather than a demo project. The platform connects students with educational resources, legacy code repositories, and structured learning pathways to enhance their academic and professional development.

## Key Files
| File | Description |
|------|-------------|
| `docker-compose.yml` | Multi-service Docker orchestration for the entire application stack |

## Subdirectories
| Directory | Purpose |
|-----------|---------|
| `devorbit-api/` | Java Spring Boot backend API (see `devorbit-api/AGENTS.md`) |
| `devorbit-web/` | React web frontend application (see `devorbit-web/AGENTS.md`) |
| `devorbit-mobile/` | Kotlin Android mobile application (see `devorbit-mobile/AGENTS.md`) |
| `docs/` | Project documentation and specifications (see `docs/AGENTS.md`) |
| `org/` | Organization-related content and resources (see `org/AGENTS.md`) |

## For AI Agents

### Working In This Directory
- This is a multi-service application with coordinated deployment via Docker Compose
- All services depend on the PostgreSQL database for data persistence
- Use the docker-compose.yml for local development and deployment
- Each service (api, web) has its own Dockerfile in respective directories

### Testing Requirements
- Run full-stack integration tests using Docker Compose
- Test database connectivity and migrations before deployment
- Ensure all services start correctly and communicate properly

### Common Patterns
- JWT-based authentication across all services
- PostgreSQL as the primary database
- RESTful API design in the backend
- Environment variable configuration for all services

## Dependencies

### Internal
- `devorbit-api/` - Spring Boot backend with JPA entities and REST controllers
- `devorbit-web/` - React frontend with Vite build system
- `devorbit-mobile/` - Kotlin Android app for mobile access
- `docs/` - Documentation for development and deployment

### External
- PostgreSQL 16 - Primary database
- Docker & Docker Compose - Container orchestration
- JWT - Authentication tokens
- GitHub API - Repository data ingestion

<!-- MANUAL: -->