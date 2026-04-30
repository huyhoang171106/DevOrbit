<!-- Parent: ../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# service

## Purpose
Business logic services implementing the core functionality of the DevOrbit application. Handle data processing, external API integrations (GitHub), authentication, and complex operations.

## Key Files
| File | Description |
|------|-------------|
| `CourseService.java` | Course management business logic |
| `GithubService.java` | GitHub API integration and data processing |
| `AuthService.java` | Authentication and authorization logic |
| `RepoService.java` | Repository data management and approval workflow |
| `TechStackService.java` | Technology classification services |

## For AI Agents

### Working In This Directory
- Implement business rules and validations
- Handle external API calls with proper error handling
- Use @Service annotation
- Implement transactional operations where needed
- Follow single responsibility principle

### Testing Requirements
- Unit tests for business logic
- Mock external API calls
- Test error scenarios
- Integration tests with repositories

### Common Patterns
- @Service annotation
- @Transactional for database operations
- Dependency injection with @Autowired
- Interface + implementation pattern
- Comprehensive error handling

## Dependencies

### Internal
- `../entity/` - Data entities
- `../repository/` - Data access
- `../dto/` - Data transfer objects
- `../config/` - External service configurations

### External
- Spring Framework - Service layer
- GitHub API - Repository data
- JWT - Token processing

<!-- MANUAL: -->