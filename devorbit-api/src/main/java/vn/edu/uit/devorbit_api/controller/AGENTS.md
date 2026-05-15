<!-- Parent: ../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-04-30 -->

# controller

## Purpose
REST API controllers handling HTTP requests and responses for the DevOrbit backend. Organized by access level (Admin, Public, Student) with JWT authentication and role-based authorization.

## Key Files
| File | Description |
|------|-------------|
| `AdminAuthController.java` | Admin authentication endpoints |
| `AdminCourseController.java` | Course management for admins |
| `AdminGithubController.java` | GitHub integration management |
| `AdminRepoCandidateController.java` | Repository candidate approval workflow |
| `AdminRepoController.java` | Approved repository management |
| `PublicCourseController.java` | Public course browsing endpoints |
| `PublicRepoController.java` | Public repository search and browsing |
| `PublicTechStackController.java` | Technology stack information |
| `StudentAuthController.java` | Student authentication endpoints |
| `GithubRepoController.java` | GitHub API proxy endpoints |
| `HealthController.java` | Application health checks |

## For AI Agents

### Working In This Directory
- Controllers follow REST conventions
- Admin controllers require admin role
- Public controllers are open access
- Student controllers require student authentication
- Use @PreAuthorize annotations for authorization

### Testing Requirements
- Test all authentication scenarios
- Verify role-based access control
- Test error handling and validation
- Integration tests with services

### Common Patterns
- @RestController annotations
- @RequestMapping for endpoint grouping
- @PreAuthorize for security
- ResponseEntity for HTTP responses
- Validation with @Valid

## Dependencies

### Internal
- `../service/` - Business logic services
- `../repository/` - Data access layer
- `../dto/` - Request/response models
- `../config/` - Security and authentication config

### External
- Spring Web MVC - REST framework
- Spring Security - Authentication/authorization

<!-- MANUAL: -->