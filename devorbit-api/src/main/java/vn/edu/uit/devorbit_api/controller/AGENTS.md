<!-- Parent: ../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-16 -->

# controller

## Purpose
REST API controllers handling HTTP requests and responses for the DevOrbit backend. Organized by access level (Admin, Public, Student) with JWT authentication and role-based authorization. ~17 controllers covering course management, GitHub integration, AI services, photobooth, knowledge graph, roadmaps, and authentication.

## Key Files

### Admin Controllers (`/api/admin/**`)
| File | Description |
|------|-------------|
| `AdminAuthController.java` | Admin authentication endpoints (login with JWT token response) |
| `AdminCourseController.java` | Full CRUD for course management |
| `AdminCourseRelationshipController.java` | Manage course prerequisite/relationship mappings |
| `AdminCourseResourceController.java` | Manage course learning resources: articles, tutorials, YouTube playlists |
| `AdminGithubController.java` | Trigger GitHub repository scanning and indexing |
| `AdminNoteController.java` | Admin note management with code snippet support |
| `AdminRepoCandidateController.java` | Repository candidate approval workflow (review, approve, reject) |
| `AdminRepoController.java` | CRUD for approved GitHub repositories |
| `AdminRoadmapController.java` | Learning roadmap CRUD — phases, items, and full roadmap management |
| `AdminElectiveController.java` | Elective course group management and assignment |

### Public Controllers (`/api/**`)
| File | Description |
|------|-------------|
| `PublicCourseController.java` | Public course browsing: list all courses, get by ID, course graph |
| `PublicCourseRelationshipController.java` | Public course relationship browsing |
| `PublicRepoController.java` | Public repository search and browsing with filtering |
| `PublicTechStackController.java` | Technology stack listing and filtering |
| `PublicAiController.java` | AI-powered endpoints: query advice, generate roadmap, curriculum matching |
| `PublicDiscoveryController.java` | Discovery feed combining courses, repos, and learning paths |
| `PhotoboothFrameController.java` | Photobooth frame listing and image retrieval |

### Auth & Utility Controllers
| File | Description |
|------|-------------|
| `StudentAuthController.java` | Student registration and login with JWT |
| `StudentBookmarkController.java` | Student bookmark management for courses and repos |
| `FileUploadController.java` | File/image upload handling (photobooth frame uploads) |
| `HealthController.java` | Application health check endpoint |

## For AI Agents

### Working In This Directory
- Controllers follow REST conventions with `@RestController`
- Admin controllers are under `/api/admin/**` — require ADMIN role via JWT
- Public controllers are under `/api/**` — open access (no auth required)
- Student controllers are under `/api/student/**` — require STUDENT role via JWT
- Use `@PreAuthorize` annotations or `SecurityFilterChain` route matchers for authorization
- New controllers should follow the established naming convention: `{AccessLevel}{Feature}Controller.java`

### Testing Requirements
- Test all authentication scenarios (valid JWT, expired, missing, wrong role)
- Verify role-based access control (admin endpoints reject students, etc.)
- Test error handling with `@ControllerAdvice` (ApiExceptionHandler)
- Integration tests with mocked services via `@WebMvcTest`

### Common Patterns
- `@RestController` + `@RequestMapping("/api/...")` annotations
- Constructor injection with `@RequiredArgsConstructor` on `final` fields
- `ResponseEntity<T>` for HTTP responses with proper status codes
- Validation with `@Valid` on request bodies
- `@Operation` / `@Tag` Swagger annotations for API documentation
- `@CrossOrigin` where CORS is needed

## Dependencies

### Internal
- `../service/` — Business logic services (~22 services)
- `../repository/` — Data access layer (~18 repositories)
- `../dto/` — Request/response models (~30+ DTOs)
- `../config/` — Security configuration (JWT filter)

### External
- Spring Web MVC — REST framework
- Spring Security — Authentication/authorization
- OpenAPI/Swagger (springdoc 2.8.6) — API documentation

<!-- MANUAL: -->
