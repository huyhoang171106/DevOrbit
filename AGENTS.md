# Repository Guidelines

## Project Overview

DevOrbit is a multi-platform university course advisory portal for UIT (University of Information Technology) students. The system links academic syllabi with GitHub repositories, provides AI-powered advisory tutoring (RAG), real-time community chat, a 3D knowledge graph visualization, GPA calculation, and a photobooth feature. It is a monorepo containing three independently built applications sharing a PostgreSQL database (hosted on Supabase).

## Architecture & Data Flow

**Three-tier architecture** with three independent frontends:

- **`devorbit-api/`** — Spring Boot 4.0.6 REST API (Java 21, Maven). Handles auth (stateless JWT), CRUD, AI/RAG pipelines, WebSocket community chat (STOMP), caching (Caffeine), and OpenAPI docs. Controllers are role-split: `Public*`, `Student*`, `Admin*`.
- **`devorbit-web/`** — React 19 SPA (TypeScript 5.7, Vite 6, Tailwind CSS). Lazy-loaded routes, Zustand/Redux Toolkit for state, Three.js/R3F for 3D knowledge graph, GSAP/Framer Motion for animation, Supabase client for realtime.
- **`devorbit-mobile/`** — Android client (Kotlin 2.0.21, Jetpack Compose, Hilt DI, Room, Retrofit).

**Data flow**: Frontends → REST/WebSocket → Spring Boot API → PostgreSQL (Supabase) with RLS policies. AI features route through OpenCode AI, Fireworks embeddings, Exa search, and Firecrawl crawling services.

## Key Directories

| Directory | Purpose |
|---|---|
| `devorbit-api/src/main/java/vn/edu/uit/devorbit_api/` | API source: controllers, services, entities, DTOs, config, exceptions, events |
| `devorbit-api/src/test/java/` | API tests (JUnit 5, Mockito, H2) |
| `devorbit-api/src/main/resources/` | Spring Boot config (`application.yaml`, profiles) |
| `devorbit-web/src/` | Frontend source: pages, hooks, components, lib, utils, types |
| `devorbit-web/src/lib/` | Shared API helpers, evaluation logic, streaming utils |
| `devorbit-mobile/` | Android app source |
| `supabase/migrations/` | SQL migrations (RLS, pgvector extensions) |
| `docs/` | Architecture, API reference, database schema, setup, deployment, troubleshooting |
| `scripts/` | Python automation (report generation, review pipeline, curriculum code generation) |

## Development Commands

### Backend (`devorbit-api/`)

```bash
rtk .\devorbit-api\mvnw.cmd test -f devorbit-api\pom.xml          # run tests
rtk .\devorbit-api\mvnw.cmd compile -B -f devorbit-api\pom.xml    # compile only
rtk .\devorbit-api\run.bat                                         # start locally (loads .env)
```

### Frontend (`devorbit-web/`)

```bash
cd devorbit-web; rtk npm install       # install dependencies
cd devorbit-web; rtk npm run dev       # start Vite dev server
cd devorbit-web; rtk npm test -- --run # run Vitest once
cd devorbit-web; rtk npm run build     # type-check + production build
```

### Mobile (`devorbit-mobile/`)

```bash
cd devorbit-mobile; rtk .\gradlew.bat test   # run Android/JVM tests
```

### Docker

```bash
docker-compose up    # PostgreSQL 16 + API + Web (with health checks)
```

## Code Conventions & Common Patterns

### Formatting & Naming

Follow `.editorconfig`: UTF-8, LF endings, spaces (not tabs), final newline.

- **Indentation**: 2 spaces default; 4 spaces for Java/Kotlin.
- **Java packages**: `vn.edu.uit.devorbit_api` — classes named by role: `CourseService`, `StudentAuthController`, `CommunityPresenceServiceTest`.
- **React/TypeScript**: PascalCase for components, camelCase for hooks/helpers. Use shared API helpers from `devorbit-web/src/lib/api.ts` (fetch-based with JWT Bearer auth, role-specific wrappers: `apiGet/Post`, `apiStudentGet/Post`, `apiAdminGet/Post`).
- **Kotlin**: Follow Android conventions, Jetpack Compose naming.

### Architecture Patterns

- **API**: Spring MVC controllers → service layer → JPA repositories. Role-based endpoint splitting (public/student/admin). Global exception handling via `ApiExceptionHandler` with custom exceptions (`BadRequestException`, `NotFoundException`, `UnauthorizedException`). Application events for decoupled logic (`NotificationEvent`, `RelationshipChangedEvent`).
- **Web**: React Router v7 with lazy-loaded routes. Zustand + Redux Toolkit for state. React Query for server state. Framer Motion for animation. STOMP WebSocket client for community chat.
- **Mobile**: Hilt for DI, Room for local DB, Retrofit for networking, Jetpack Compose for UI.

### Error Handling

- **API**: `@ControllerAdvice` global handler with custom exception types. DTO validation via Jakarta annotations.
- **Web**: `ErrorBoundary` wrapper at app root. API helpers auto-redirect on 403 for student routes.
- **Mobile**: Kotlin coroutines with structured error handling.

## Important Files

| File | Role |
|---|---|
| `devorbit-api/src/main/java/.../DevorbitApiApplication.java` | Spring Boot entry point (`@SpringBootApplication`, `@EnableAsync`, `@EnableScheduling`) |
| `devorbit-api/src/main/java/.../config/SecurityConfig.java` | Spring Security filter chain, JWT auth, CORS, role-based access |
| `devorbit-api/src/main/java/.../config/WebSocketConfig.java` | STOMP broker config, `/ws/community` endpoint |
| `devorbit-api/src/main/resources/application.yaml` | Main Spring Boot config (datasource, AI services, JWT, mail, cache) |
| `devorbit-api/pom.xml` | Maven POM: Java 21, Spring Boot 4.0.6, all dependencies |
| `devorbit-api/run.bat` | Windows startup script (loads .env, JVM tuning, spring-boot:run) |
| `devorbit-web/src/main.tsx` | Vite/React entry point |
| `devorbit-web/src/App.tsx` | Root component (QueryClientProvider, BrowserRouter, ErrorBoundary) |
| `devorbit-web/src/router.tsx` | React Router config with lazy-loaded routes |
| `devorbit-web/src/lib/api.ts` | HTTP client layer with JWT auth and role-specific wrappers |
| `devorbit-web/vite.config.ts` | Vite config (SWC, compression, chunk splitting, dev proxy) |
| `devorbit-web/package.json` | Frontend dependencies and scripts |
| `docker-compose.yml` | Full-stack orchestration (PostgreSQL + API + Web) |
| `.env.example` | Master environment template |
| `docs/architecture.md` | System architecture documentation |
| `docs/api.md` | REST API and WebSocket reference |
| `docs/database.md` | Database schema (37 tables, RLS policies, pgvector) |

## Runtime & Tooling Preferences

- **Root orchestrator**: Bun 1.3.14 (declared in root `package.json`)
- **API**: Java 21, Maven 3.9.14 (via wrapper), Spring Boot 4.0.6
- **Web**: Node.js, npm, Vite 6, TypeScript 5.7
- **Mobile**: JDK, Gradle 8.11.1 (via wrapper), Kotlin 2.0.21, Android SDK 35
- **Database**: PostgreSQL 16 (Supabase-hosted), pgvector for embeddings
- **CI/CD**: GitHub Actions (API compile check + web type-check/test on PRs; weekly npm audit + OWASP dependency scan)
- **Dependabot**: Configured for all ecosystems
- **Editor**: VS Code / IntelliJ recommended; `.editorconfig` enforces consistency

### Environment Configuration

- `.env` files per module with `.env.example` templates
- Spring profiles: `default`, `local` (ddl-auto=update, show-sql), `test` (H2 in-memory), `lifecycle` (H2 with custom schema)
- Vite env prefix for frontend config
- Gradle `BuildConfig` for mobile

## Testing & QA

### Test Frameworks

| Layer | Framework | Runner |
|---|---|---|
| API | JUnit 5 + Mockito + AssertJ + H2 | Maven (`mvnw.cmd test`) |
| Web | Vitest 4 + React Testing Library | npm (`npm test -- --run`) |
| Mobile | JUnit 4 + Gson | Gradle (`gradlew.bat test`) |

### Test Conventions

- **API tests**: Place in `devorbit-api/src/test/java/` mirroring source package structure. Name `*Test.java`. Use `@ExtendWith(MockitoExtension.class)` for unit tests, `@WebMvcTest` for controller slice tests, `@SpringBootTest` + `@Transactional` for integration tests. H2 in-memory DB for test profile.
- **Web tests**: Colocated `*.test.ts` or `*.test.tsx` files. Use `vi.mock()` for module mocking, `vi.useFakeTimers()` for date mocking. Testing Library for component renders.
- **Mobile tests**: Standard JUnit 4 conventions in `devorbit-mobile/src/test/`.

### Running Tests

```bash
rtk .\devorbit-api\mvnw.cmd test -f devorbit-api\pom.xml          # API tests
cd devorbit-web; rtk npm test -- --run                              # Web tests
cd devorbit-mobile; rtk .\gradlew.bat test                         # Mobile tests
```

### Coverage & CI

- **No coverage thresholds configured** in any layer.
- CI runs web tests on PRs; API only compiles (no test run in CI); mobile tests not in CI.
- Run targeted tests first, then the relevant module build before finishing.

## Commit & Pull Request Guidelines

- Conventional-style messages: `fix(admin) delete community message`, `fix: lifecycle cleanup for course & repo deletion`.
- Branch naming: `feature/`, `bugfix/`, `chore/`, `docs/` prefixes.
- Keep commits focused; mention the affected area.
- PRs should link stories/issues when available, note schema or environment changes, include screenshots for UI work.
- Never claim runtime success without command or smoke-test evidence.

## Security & Configuration Tips

- Do not commit secrets from `.env` files. Supabase posture is backend-owned.
- Migrations belong in `supabase/migrations/`.
- API runtime proof should use `devorbit-api/run.bat`.
- Preserve unrelated local changes in dirty worktree.
- Spring Security: stateless JWT auth, role-based access (ADMIN/STUDENT), CORS config.
