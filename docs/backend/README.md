# DevOrbit Backend

> **Spring Boot 4 (Java 21)** — REST API cho hệ thống khám phá mã nguồn học thuật,
> knowledge graph, AI photobooth, và lộ trình học tập dành cho sinh viên UIT
> (chương trình Kỹ thuật Phần mềm, Khoá 20-2025).

---

## 1. Tổng quan

```
Client Layer (React 19 / Jetpack Compose)
       ↓ HTTP REST (JSON)
┌─────────────────────────────────────┐
│ Controller Layer (19 controllers)   │
│  Admin · Public · Student           │
├─────────────────────────────────────┤
│ Security Layer (JWT Filter)         │
├─────────────────────────────────────┤
│ Service Layer (22 services)         │
│  Auth · Course · GitHub · AI · ... │
├─────────────────────────────────────┤
│ Repository Layer (18 JPA repos)     │
├─────────────────────────────────────┤
│ PostgreSQL (23 tables)              │
└─────────────────────────────────────┘
```

**Tech stack**: Spring Boot 4.0.6 · Java 21 · PostgreSQL 16 · JWT (jjwt 0.12.6) · SpringDoc OpenAPI · WebClient (GitHub API) · Supabase Storage · Maven

---

## 2. Cấu trúc package

```
vn.edu.uit.devorbit_api/
├── DevorbitApiApplication.java       # Entrypoint (@SpringBootApplication)
├── config/                           # 8 config classes
│   ├── SecurityConfig.java           # SecurityFilterChain + CORS
│   ├── JwtAuthenticationFilter.java  # OncePerRequestFilter
│   ├── JwtProperties.java            # app.jwt.secret/expiration record
│   ├── GithubClientConfig.java       # WebClient → api.github.com
│   ├── GithubProperties.java         # app.github.token record
│   ├── ElectiveGroupConfig.java      # Môn tự chọn theo nhóm
│   ├── JacksonConfig.java            # ObjectMapper
│   ├── OpenApiConfig.java            # Swagger UI
│   └── RepoCandidateDataInitializer.java
├── constant/
│   └── CurriculumConstants.java      # KTPM curriculum (mã môn, học kỳ)
├── controller/                       # 19 REST controllers
│   ├── admin/    (9)                 # Auth, Course, Resource, Relationship, GitHub, Candidate, Repo, Roadmap, Note
│   ├── public/   (6)                 # Course, Repo, Discovery, TechStack, Relationship, AI
│   └── other/    (4)                 # StudentAuth, StudentBookmark, Health, FileUpload, PhotoboothFrame
├── dto/          (30+ records)       # admin/, publicapi/, student/
├── entity/       (23 JPA entities)
├── event/        (RelationshipChangedEvent)
├── exception/    (ApiExceptionHandler + 3 custom exceptions)
├── repository/   (18 Spring Data JPA)
└── service/      (22 services)
    └── ai/                           # SummaryGenerator, AdviceGenerator, RoadmapGenerator,
                                      # GraphQueryEngine, CurriculumMatcher
```

---

## 3. Domain Model

### 3.1 Users & Auth

`admin_users` (id, username, passwordHash, active) — Admin
`student_users` (id, studentCode, fullName, email, passwordHash, active) — Sinh viên
`otp` (id, email, code, expiresAt, used) — OTP email verification

### 3.2 Course & Curriculum

`course` (id, maMH, tenMH, tinChi, khoa, semester, electiveGroup, active, description) — Môn học
`course_relationships` (id, courseId, relatedCourseId, relationType: PREREQUISITE/COMPLEMENTARY/COREQUISITE)
`course_articles` (id, courseId, title, url, source) — Bài viết tham khảo
`course_tutorials` (id, courseId, title, url, platform, isFree) — Tutorial
`course_youtube_playlists` (id, courseId, playlistId, title, channel, videoCount, thumbnailUrl)

### 3.3 GitHub

`github_repos` (id, repoName, description, githubUrl, techStack, subjectId, displayName, primaryLanguage, active, courseId, stars)
`repo_candidates` (id, courseId, scanQuery, githubOwner, githubName, githubUrl, description, primaryLanguage, topics, stars, forks, lastPushedAt, readmeExcerpt, status: NEW→REVIEWING→APPROVED→PROMOTED/REJECTED, reviewedBy, reviewedAt, reviewNote)
`tech_stacks` (id, name, category)
`repo_tech_stacks` (repoId, techStackId) — N:N join

### 3.4 Learning Roadmap

`learning_roadmaps` → `roadmap_phases` (sortOrder) → `roadmap_items` (targetType: COURSE/REPO/NOTE, targetId, title, note, sortOrder)

### 3.5 Others

`student_bookmarks` (studentId, targetType, targetId)
`notes` + `note_code_snippets` — Ghi chú quản trị
`photobooth_frames` (courseId, imageUrl, uploader)

---

## 4. API Endpoints

### 4.1 Public (permitAll)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/health` | Health check |
| GET | `/api/courses` | Danh sách môn học |
| GET | `/api/courses/{id}` | Chi tiết môn |
| GET | `/api/courses/{id}/resources` | Articles, tutorials, playlists |
| GET | `/api/repos` | Danh sách repo |
| GET | `/api/repos/{id}` | Chi tiết repo |
| GET | `/api/repos/{id}/summary` | AI summary |
| GET | `/api/repos/{id}/advice` | AI advice |
| GET | `/api/knowledge-graph` | Knowledge graph (cached) |
| GET | `/api/discovery` | Khám phá tổng hợp |
| GET | `/api/tech-stacks` | Tech stacks |
| POST | `/api/ai/roadmap` | Sinh lộ trình AI |
| POST | `/api/ai/query` | Query KG bằng NL |
| GET | `/api/photobooth/frames` | Danh sách frame |

### 4.2 Student

Public: `POST /api/student/send-otp`, `verify-otp`, `login`, `register`
Authenticated: `GET /profile`, `GET/POST/DELETE /bookmarks[/{id}]`

### 4.3 Admin (ROLE_ADMIN)

**Endpoints**: CRUD courses, resources (articles/tutorials/youtube), relationships, repos, candidates (review), roadmaps (phases, items), notes, GitHub scan, photobooth frames, file upload, Swagger UI

Full list: `/api/admin/courses`, `/api/admin/repos`, `/api/admin/candidates`, `/api/admin/github/scan`, `/api/admin/roadmaps`, `/api/admin/notes`, `/api/photobooth/**`, `/swagger-ui/**`, `/v3/api-docs/**`

---

## 5. Security

### Flow

```
Client → POST /login → { token }
Client → GET /api/... + Bearer <token> → JwtAuthenticationFilter
  → validate JWT (HMAC-SHA256)
  → set SecurityContext (role: ADMIN/STUDENT)
  → Controller (authorized by SecurityFilterChain)
```

### JWT

- **Secret**: `JWT_SECRET` env (mặc định cảnh báo log)
- **TTL**: `JWT_EXPIRATION_MINUTES` (default 120)
- **Claims**: `sub` (username/studentCode), `role` (ADMIN/STUDENT)
- **Password**: BCrypt (`BCryptPasswordEncoder`)

### Route security

| Pattern | Access |
|---------|--------|
| `/api/admin/auth/login`, `/api/courses/**`, `/api/repos/**`, `/api/ai/**`, `/api/discovery/**`, `/api/tech-stacks/**`, `/api/health` | PermitAll |
| `GET /api/photobooth/**` | PermitAll |
| `/api/student/send-otp`, `verify-otp`, `login`, `register` | PermitAll |
| `/api/student/**` | Authenticated |
| `/api/photobooth/**` (non-GET), `/api/admin/**`, `/swagger-ui/**`, `/v3/api-docs/**` | ROLE_ADMIN |
| Khác | DenyAll |

### CORS

Cho phép: localhost:3000, :5173, :5174, production IPs. Credentials = true.

---

## 6. Services chi tiết

### 6.1 Authentication

`AdminAuthService` — admin login (BCrypt + JWT)
`StudentAuthService` — OTP email → verify → register/login (BCrypt + JWT)
`JwtService` — generate/validate/parse JWT
`EmailService` — send OTP via Gmail SMTP

### 6.2 Course

`CourseService` — CRUD course + mapping entities → response DTOs
`CourseRelationshipService` — CRUD quan hệ giữa các môn
`CourseArticleService` / `CourseTutorialService` / `CourseYoutubePlaylistService` — CRUD tài nguyên
`KnowledgeGraphService` — Build graph từ courses + relationships, tính level + impact score, cache

### 6.3 GitHub

`GithubScanService` — Scan GitHub Search API (multi-query/course), rate-limited, virtual thread README fetch
`GithubRepoService` — CRUD approved repos
`RepoCandidateService` — Review workflow (NEW → REVIEWING → APPROVED → PROMOTED)

### 6.4 AI Engine (rule-based, không LLM)

`AiService` — Facade cho tất cả AI endpoints
`SummaryGenerator` — Tóm tắt repo từ metadata
`AdviceGenerator` — Lời khuyên dựa trên impact score + downstream count
`RoadmapGenerator` — Sinh lộ trình từ knowledge graph + career goals
`GraphQueryEngine` — Parse NL query → lọc/duyệt graph
`CurriculumMatcher` — So khớp course → KTPM program

### 6.5 Others

`StudentBookmarkService` — Bookmarks
`LearningRoadmapService` — CRUD roadmaps + phases + items
`AdminNoteService` — Notes + code snippets (cho course/repo)
`SupabaseStorageService` — Upload file → Supabase Storage
`TechStackService` — CRUD công nghệ
`PhotoboothFrameService` — CRUD frames

---

## 7. Knowledge Graph Engine

### Construction

```
1. Load active courses + all relationships
2. Filter → chỉ giữ MANDATORY_CODES (KTPM)
3. Nodes: mỗi course → node (level, impactScore, val = 12 + repoCount*1.5)
4. Links: PREREQUISITE (có hướng), COMPLEMENTARY/COREQUISITE (vô hướng)
```

### Level (Topological Sort)

```
Queue ← nodes with inDegree == 0
while queue: level[neighbor] = max(level[neighbor], level[current] + 1)
```

### Impact Score

```
score = unlockedCount×0.4 + depth×0.3 + bottleneck×0.3
normalized = (score / maxScore) × 10.0
```

### Cache

`@Cacheable("knowledgeGraph")`. Evict on `RelationshipChangedEvent`.

---

## 8. GitHub Scan Engine

```
scanAll():
  for each course → 4 queries ("MAMH uit", "MAMH assignment", "MAMH lab", "name repository")
    → GitHub Search API (per_page=100)
    → 2050ms delay (rate limit)
    → Skip forks + existing URLs
    → Save RepoCandidate(status=NEW)
    → Virtual thread fetch README excerpt (500 chars)
```

---

## 9. Error Handling

| Exception | HTTP | Response |
|-----------|------|----------|
| `NotFoundException` | 404 | `{"error": "..."}` |
| `UnauthorizedException` | 401 | `{"error": "..."}` |
| `BadRequestException` | 400 | `{"error": "..."}` |
| `MethodArgumentNotValidException` | 400 | `{"error": "field: msg, ..."}` |
| `Exception` (unhandled) | 500 | `{"error": "Internal server error"}` |

---

## 10. Cấu hình

### Base (application.yaml)

| Key | Default |
|-----|---------|
| `app.jwt.secret` | required |
| `app.jwt.expiration-minutes` | 120 |
| `app.github.token` | optional |
| `app.cors.allowed-origins` | localhost:3000, :5173, :5174 |
| `server.port` | 8080 |
| `supabase.url/key/bucket` | optional |

### Local profile (`application-local.yaml`)

`ddl-auto: update` · `show-sql: true` · kích hoạt với `--spring-boot.run.profiles=local`

---

## 11. Testing

```
src/test/
├── DevorbitApiApplicationTests.java
├── controller/  (5 test classes)
│   ├── AdminAuthControllerTest.java
│   ├── AdminCourseControllerTest.java
│   ├── AdminRepoCandidateControllerTest.java
│   ├── PublicCourseControllerTest.java
│   └── PublicRepoControllerTest.java
└── service/
    └── RepoCandidateServiceTest.java
```

Test DB: H2 in-memory. Security: `@WithMockUser`.

---

## 12. Database Tables (23)

`admin_users` · `student_users` · `otp` · `course` · `course_relationships` · `course_articles` · `course_tutorials` · `course_youtube_playlists` · `github_repos` · `repo_candidates` · `tech_stacks` · `repo_tech_stacks` · `learning_roadmaps` · `roadmap_phases` · `roadmap_items` · `student_bookmarks` · `notes` · `note_code_snippets` · `photobooth_frames`

---

## 13. Quick Start

```bash
# PostgreSQL
docker run -d --name devorbit-db -e POSTGRES_DB=devorbit_db \
  -e POSTGRES_PASSWORD=huyhoang -p 5432:5432 postgres:16

# Environment
export JWT_SECRET="your-256-bit-secret"
export DATABASE_URL=jdbc:postgresql://localhost:5432/devorbit_db

# Run (local profile)
cd devorbit-api
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Build
mvn clean package -DskipTests
java -jar target/devorbit-api-0.0.1-SNAPSHOT.jar
```
