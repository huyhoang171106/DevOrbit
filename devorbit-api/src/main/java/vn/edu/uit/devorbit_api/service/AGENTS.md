<!-- Parent: ../../../AGENTS.md -->
<!-- Generated: 2026-04-30 | Updated: 2026-05-16 -->

# service

## Purpose
Business logic services implementing the core functionality of the DevOrbit application. Handle data processing, external API integrations (GitHub, Supabase Storage), authentication, AI-powered features (knowledge graph, roadmap generation, curriculum matching), email sending, and complex operations. ~22 service classes organized by feature domain.

## Key Files

### Course & Content Services
| File | Description |
|------|-------------|
| `CourseService.java` | Course CRUD, graph building, elective group logic, and course queries |
| `CourseRelationshipService.java` | Course prerequisite/relationship mapping logic |
| `CourseArticleService.java` | Course article resource management |
| `CourseTutorialService.java` | Course tutorial resource management |
| `CourseYoutubePlaylistService.java` | Course YouTube playlist resource management |
| `KnowledgeGraphService.java` | Builds and queries the course/repo knowledge graph for discovery |

### GitHub & Repository Services
| File | Description |
|------|-------------|
| `GithubRepoService.java` | GitHub repository CRUD, search, filtering, and metadata processing |
| `GithubScanService.java` | Scans GitHub repositories for tech stack detection, README parsing, topic extraction |
| `RepoCandidateService.java` | Repository candidate submission, review, and approval workflow |

### AI Services
| File | Description |
|------|-------------|
| `AiService.java` | AI service orchestrator — routes queries to appropriate AI sub-services |
| `service/ai/AdviceGenerator.java` | Generates AI-powered learning advice based on course/repo context |
| `service/ai/CurriculumMatcher.java` | Matches GitHub repositories to curriculum courses |
| `service/ai/GraphQueryEngine.java` | Queries the knowledge graph for AI-powered recommendations |
| `service/ai/RoadmapGenerator.java` | Generates personalized learning roadmaps using AI |
| `service/ai/SummaryGenerator.java` | Generates course/repo summaries using AI |

### Learning & Roadmap Services
| File | Description |
|------|-------------|
| `LearningRoadmapService.java` | Learning roadmap CRUD — phases, items, full roadmap lifecycle |

### Authentication & Security
| File | Description |
|------|-------------|
| `AdminAuthService.java` | Admin login, JWT generation, and authentication logic |
| `StudentAuthService.java` | Student registration, login, and JWT authentication |
| `StudentBookmarkService.java` | Student bookmark CRUD and query logic |
| `JwtService.java` | JWT token generation, validation, and claims extraction |

### Utility Services
| File | Description |
|------|-------------|
| `AdminNoteService.java` | Admin notes CRUD with code snippet management |
| `EmailService.java` | Email sending (via Spring Mail) — OTP verification, notifications |
| `TechStackService.java` | Technology stack CRUD and classification |
| `PhotoboothFrameService.java` | Photobooth frame management — listing, creation, image handling |
| `SupabaseStorageService.java` | Supabase Storage integration for file/image uploads and retrieval |

## For AI Agents

### Working In This Directory
- Implement business rules and validations in service layer
- Handle external API calls with proper error handling and retries
- Use `@Service` annotation on all service classes
- Use `@Transactional` for database write operations
- Follow single responsibility principle — each service handles one domain
- AI services live under `service/ai/` sub-package
- New feature services should be named `{Domain}Service.java`

### Testing Requirements
- Unit tests with mocked repositories
- Mock external API calls (GitHub WebClient, Supabase, MailSender)
- Test error scenarios (API failures, invalid data, rate limiting)
- Integration tests with `@DataJpaTest` for repository-dependent services
- Use H2 in-memory database for service integration tests

### Common Patterns
- `@Service` annotation
- `@Transactional` for database operations
- Constructor injection with `@RequiredArgsConstructor` + `final` fields
- Interface + implementation where abstraction is warranted
- Comprehensive error handling with custom exceptions (`NotFoundException`, `BadRequestException`)
- `@Value` or `@ConfigurationProperties` for external configuration
- `WebClient` for reactive HTTP calls to GitHub API

## Dependencies

### Internal
- `../entity/` — Data entities (~22 entities)
- `../repository/` — Data access layer (~18 repositories)
- `../dto/` — Data transfer objects (~30+ DTOs)
- `../config/` — External service configurations (JWT, GitHub, Jackson)
- `../exception/` — Custom exception classes

### External
- Spring Framework — Service layer (DI, transactions)
- GitHub API — Repository data fetching and scanning
- Supabase Storage — File/image storage
- Spring Mail — Email sending
- JWT (jjwt 0.12.6) — Token processing
- Jakarta Validation — Input validation

<!-- MANUAL: -->
