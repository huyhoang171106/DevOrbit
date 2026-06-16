# 🪐 DevOrbit — Đại Việt Toàn Thư

> Bản đồ sống động, ghi chép tất cả mọi thứ trong hai chân rết của DevOrbit: **devorbit-api** (Spring Boot 4 / Java 21) và **devorbit-web** (React 19 / Vite 6 / TypeScript).  
> Mỗi file, mỗi thư mục, mỗi class — đều có nghĩa. Không sót thứ gì.

---

## Mục lục

1. [Tổng quan hệ thống](#1-tổng-quan-hệ-thống)
2. [devorbit-api — Backend](#2-devorbit-api--backend)
   - [2.1 Cấu trúc thư mục](#21-cấu-trúc-thư-mục)
   - [2.2 Config — 18 files](#22-config--18-files)
   - [2.3 Entity — 36 files](#23-entity--36-files)
   - [2.4 Repository — 30 files](#24-repository--30-files)
   - [2.5 Service — 44 files](#25-service--44-files)
   - [2.6 Controller — 25 files](#26-controller--25-files)
   - [2.7 DTO — 44 files](#27-dto--44-files)
   - [2.8 Exception — 4 files](#28-exception--4-files)
   - [2.9 Event — 2 files](#29-event--2-files)
   - [2.10 Constant — 1 file](#210-constant--1-file)
   - [2.11 Database Migrations — 18 files](#211-database-migrations--18-files)
   - [2.12 Test — 28 files](#212-test--28-files)
   - [2.13 Tài nguyên & Config — 10 files](#213-tài-nguyên--config--10-files)
   - [2.14 File gốc dự án — 11 files](#214-file-gốc-dự-án--11-files)
3. [🔥 Chi tiết tính năng Backend API](#-chi-tiết-tính-năng-backend-api)
   - [Course Discovery & Curriculum](#-1-course-discovery--curriculum)
   - [AI Subject Q&A (RAG)](#-2-ai-subject-qa-rag-trên-syllabus)
   - [AI Chat & AI Tutor](#-3-ai-chat--ai-tutor)
   - [AI Roadmap Generator](#-4-ai-roadmap-generator)
   - [GitHub Repo Discovery](#-5-github-repo-discovery--scan)
   - [Authentication & Authorization](#-6-authentication--authorization)
   - [Community Chat WebSocket](#-7-community-chat-websocket-real-time)
   - [Social Features](#-8-social-features-reviews--votes)
   - [AI Photobooth](#-9-ai-photobooth)
   - [GPA Calculator](#-10-gpa-calculator)
   - [RAG Pipeline](#-11-rag-pipeline-knowledge-base)
   - [Web Search (Exa)](#-12-web-search-exa-api)
   - [Admin Dashboard & Stats](#-13-admin-dashboard--stats)
   - [Notifications](#-14-notifications)
   - [Tech Stack Management](#-15-tech-stack-management)
   - [Admin Notes & Articles](#-16-admin-notes--articles)
   - [Course Relationships](#-17-course-relationships)
   - [Discovery Feed](#-18-discovery-feed)
   - [Student Bookmarks](#-19-student-bookmarks)
   - [Security Architecture](#-20-security-architecture)
   - [Database Architecture](#-21-database-architecture)
4. [devorbit-web — Frontend](#4-devorbit-web--frontend)
   - [4.1 Cấu trúc thư mục](#41-cấu-trúc-thư-mục)
   - [4.2 Pages — 23 files](#42-pages--23-files)
   - [4.3 Components — 60+ files](#43-components--60-files)
   - [4.4 Lib / API Layer — 16 files](#44-lib--api-layer--16-files)
   - [4.5 Hooks — 12 files](#45-hooks--12-files)
   - [4.6 Types — 5 files](#46-types--5-files)
   - [4.7 Utils — 9 files](#47-utils--9-files)
   - [4.8 Motion System — 8 files](#48-motion-system--8-files)
   - [4.9 Scenes / Three.js — 7 files](#49-scenes-threejs--7-files)
   - [4.10 Knowledge Graph Galaxy — 12 files](#410-knowledge-graph-galaxy--12-files)
   - [4.11 Performance — 2 files](#411-performance--2-files)
   - [4.12 Styles & Assets — 5 files](#412-styles--assets--5-files)
   - [4.13 Config Build — 10 files](#413-config-build--10-files)
   - [4.14 Vercel Deployment — 2 files](#414-vercel-deployment--2-files)
5. [🔥 Chi tiết tính năng Website](#-chi-tiết-tính-năng-website-devorbit-web)
   - [HomePage](#-1-homepage-)
   - [CourseListPage](#-2-courselistpage-courses)
   - [CourseDetailPage](#-3-coursedetailpage-coursescourseid)
   - [RepoDetailPage](#-4-repodetailpage-reposrepoid)
   - [Knowledge Graph Galaxy](#-5-knowledge-graph--galaxy-page-knowledge-graph)
   - [AI Tutor Page](#-6-ai-tutor-page-ai-tutor)
   - [Photobooth Page](#-7-photobooth-page-photobooth)
   - [GPA Calculator](#-8-gpa-calculator-gpa-calculator)
   - [Community Page](#-9-community-page-community)
   - [Student Authentication](#-10-student-authentication)
   - [Student Bookmarks](#-11-student-bookmarks-studentbookmarks)
   - [Admin Dashboard](#-12-admin-dashboard-admin)
   - [Admin Courses](#-13-admin-courses-admincourses)
   - [Admin Repos](#-14-admin-repos-adminrepos)
   - [Admin Students](#-15-admin-students-adminstudents)
   - [Admin Reviews](#-16-admin-reviews-adminreviews)
   - [Admin Community](#-17-admin-community-admincommunity)
   - [Admin Chat Monitor](#-18-admin-chat-monitor-adminchat)
   - [Admin Relationships](#-19-admin-relationships-adminrelationships)
   - [Admin Photobooth](#-20-admin-photobooth-adminphotobooth)
   - [Admin Tech Stack](#-21-admin-tech-stack-admintechstack)
   - [UI/UX Design System](#-uiux-design-system)
6. [File gốc monorepo root](#6-file-gốc-monorepo-root)
7. [Thống kê tổng](#7-thống-kê-tổng)

---

## 1. Tổng quan hệ thống

**DevOrbit** là nền tảng đa module dành cho sinh viên UIT (Đại học Công nghệ Thông tin — University of Information Technology, VNU-HCM), gồm:

| Module | Công nghệ | Vai trò |
|--------|-----------|---------|
| **devorbit-api** | Spring Boot 4.0.6 + Java 21 + Maven | REST API backend, AI service, WebSocket, JWT auth, RAG pipeline |
| **devorbit-web** | React 19 + Vite 6 + TypeScript + Tailwind | Ứng dụng web SPA cho sinh viên & admin, Three.js galaxy, photobooth |

### Tính năng cốt lõi

- **📚 Course Discovery** — Danh mục môn học UIT, chi tiết, đánh giá, quan hệ tiên quyết
- **🔭 Knowledge Graph** — Đồ thị tri thức dạng 3D galaxy với Three.js, có camera, orbit, constellation, wormhole
- **🤖 AI Tutor / RAG** — Chat với AI có context từ syllabus, tài liệu môn học, web crawl; streaming SSE
- **📦 Github Repo Discovery** — Quét repo GitHub, phân tích AI, đánh giá, vote cộng đồng
- **📸 AI Photobooth** — Chụp ảnh + ghép frame, upload lên Supabase Storage
- **📊 GPA Calculator** — Tính điểm trung bình theo chương trình UIT
- **🗺️ AI Roadmap** — Sinh lộ trình học tập cá nhân hóa
- **👥 Community** — Chat real-time (WebSocket STOMP), kênh thảo luận, đánh giá
- **🔐 Auth** — JWT + OTP email cho sinh viên; role-based admin
- **⚙️ Admin Panel** — Quản lý courses, repos, students, reviews, photobooth, tech-stacks, chat, community
- **🧠 RAG Pipeline** — Crawl web → chunk → embed (Fireworks/pgvector) → hybrid search → rerank → LLM answer

---

## 2. devorbit-api — Backend

### 2.1 Cấu trúc thư mục

```
devorbit-api/
├── src/main/java/vn/edu/uit/devorbit_api/
│   ├── config/           # 18 files — Bean & security config
│   ├── constant/         # 1 file  — Hằng số curriculum
│   ├── controller/       # 25 files — REST endpoints
│   ├── dto/              # 44 files — Request/Response objects
│   │   ├── admin/        # 22 files
│   │   ├── community/    # 5 files
│   │   ├── knowledge/    # 15 files
│   │   ├── publicapi/    # 12 files
│   │   └── student/      # 6 files
│   ├── entity/           # 36 files — JPA entities
│   ├── event/            # 2 files — Application events
│   ├── exception/        # 4 files — Exception classes + handler
│   ├── repository/       # 30 files — Spring Data JPA repos
│   └── service/          # 44 files — Business logic
│       ├── ai/           # 13 files — AI services
│       └── knowledge/    # 18 files — RAG pipeline
├── src/main/resources/   # 20 files — Config, SQL, migrations
├── src/test/             # 28 files — Unit & integration tests
├── .pi/gsd/              # ~120 files — Pi GSD agent framework
└── (root)                # 11 files — POM, Docker, scripts
```

---

### 2.2 Config — 18 files

| # | File | Chức năng |
|---|------|-----------|
| 1 | `DevorbitApiApplication.java` | Entry point Spring Boot, `@SpringBootApplication` |
| 2 | `AGENTS.md` | Agent guide cho backend (theo GSD harneess) |
| 3 | `AiConfig.java` | Bean `RestClient` cho Open AI compatible endpoint |
| 4 | `AsyncConfig.java` | `@EnableAsync`, custom `ThreadPoolTaskExecutor` |
| 5 | `CacheConfig.java` | Caffeine cache config (`@EnableCaching`) |
| 6 | `ExaProperties.java` | `@ConfigurationProperties(prefix = "exa")` |
| 7 | `GithubClientConfig.java` | GitHub REST API client config |
| 8 | `GithubProperties.java` | `@ConfigurationProperties(prefix = "github")` |
| 9 | `JacksonConfig.java` | ObjectMapper customization |
| 10 | `JwtAuthenticationFilter.java` | OncePerRequestFilter — JWT extraction & validation |
| 11 | `JwtProperties.java` | `@ConfigurationProperties(prefix = "app.jwt")` |
| 12 | `KnowledgeConfig.java` | Knowledge graph async executor + embedding pool |
| 13 | `OpenApiConfig.java` | SpringDoc OpenAPI / Swagger UI config |
| 14 | `RepoCandidateDataInitializer.java` | `CommandLineRunner` — seed repo candidates |
| 15 | `SecurityConfig.java` | Security filter chain, CORS, CSRF, endpoint rules |
| 16 | `SubjectQaStreamingConfig.java` | WebClient + SSE streaming config |
| 17 | `SupabaseDatabaseHardeningInitializer.java` | RLS policies, indexes hardening on startup |
| 18 | `TechStackDataInitializer.java` | `CommandLineRunner` — seed tech stacks |
| 19 | `WebSocketConfig.java` | STOMP WebSocket endpoint + interceptor |

**Ý nghĩa**: Config layer chứa toàn bộ bean khởi tạo cho Spring context. Nổi bật: `JwtAuthenticationFilter` (xác thực stateless), `SecurityConfig` (phân quyền từng endpoint), `SupabaseDatabaseHardeningInitializer` (tự động harden database khi deploy), và `WebSocketConfig` (STOMP cho community chat).

---

### 2.3 Entity — 36 files

**Module Auth & Admin:**
| File | Bảng | Mô tả |
|------|------|-------|
| `AdminUser.java` | `admin_users` | Tài khoản admin |
| `StudentUser.java` | `student_users` | Tài khoản sinh viên (email, password, avatar, email_verified, major, semester, ...) |
| `Otp.java` | `otps` | OTP xác thực email |
| `OtpPurpose.java` | (enum) | REGISTRATION / PASSWORD_RESET |

**Module Course (chương trình đào tạo):**
| File | Bảng | Mô tả |
|------|------|-------|
| `Course.java` | `courses` | Môn học (code, name, credits, semester, major, description, elective_category, etc.) |
| `CourseSyllabus.java` | `course_syllabi` | Đề cương chi tiết (objectives, outcomes, assessment, textbook, summary, embedding) |
| `CourseArticle.java` | `course_articles` | Bài báo / reading list |
| `CourseAssessment.java` | `course_assessments` | Thành phần điểm (weight, type, description) |
| `CourseObjective.java` | `course_objectives` | Mục tiêu môn học (viết theo chuẩn CĐR) |
| `CourseOutcome.java` | `course_outcomes` | Chuẩn đầu ra |
| `CourseReference.java` | `course_references` | Tài liệu tham khảo |
| `CourseSession.java` | `course_sessions` | Buổi học (topic, content, duration) |
| `CourseTool.java` | `course_tools` | Công cụ liên quan môn học |
| `CourseTutorial.java` | `course_tutorials` | Hướng dẫn / tutorial |
| `CourseYoutubePlaylist.java` | `course_youtube_playlists` | Playlist YouTube đề xuất |
| `CourseReview.java` | `course_reviews` | Đánh giá môn học từ sinh viên |
| `CourseRelationship.java` | `course_relationships` | Quan hệ môn học (prerequisite, corequisite, equivalent) |
| `CourseRelationType.java` | (enum) | PREREQUISITE / COREQUISITE / EQUIVALENT |

**Module GitHub Repo Discovery:**
| File | Bảng | Mô tả |
|------|------|-------|
| `GithubRepo.java` | `github_repos` | Repo được quét & phê duyệt (name, url, description, stars, language, topics, last_pushed_at, etc.) |
| `RepoCandidate.java` | `repo_candidates` | Repo chờ review (PENDING / APPROVED / REJECTED) |
| `RepoCandidateStatus.java` | (enum) | PENDING / APPROVED / REJECTED |
| `RepoReview.java` | `repo_reviews` | Review repo từ cộng đồng |
| `RepoVote.java` | `repo_votes` | Upvote/downvote repo |

**Module Community & Social:**
| File | Bảng | Mô tả |
|------|------|-------|
| `ChatChannel.java` | `chat_channels` | Kênh chat community (name, description, type) |
| `ChatChannelType.java` | (enum) | PUBLIC / PRIVATE |
| `ChatMessage.java` | `chat_messages` | Message trong kênh |
| `ChatSession.java` | `chat_sessions` | Phiên chat AI |
| `CommunityMessage.java` | `community_messages` | Bài post community |
| `Note.java` | `notes` | Ghi chú / bài viết admin |
| `NoteCodeSnippet.java` | `note_code_snippets` | Code snippet trong note |
| `NoteTargetType.java` | (enum) | STUDENT / ALL |
| `Notification.java` | `notifications` | Thông báo |

**Module Knowledge & RAG:**
| File | Bảng | Mô tả |
|------|------|-------|
| `KnowledgeChunk.java` | `knowledge_chunks` | Chunk tài liệu đã embed (content, embedding vector, source, metadata) |
| `KnowledgeSource.java` | `knowledge_sources` | Nguồn tài liệu (url, type, status, chunk count) |

**Module Photobooth:**
| File | Bảng | Mô tả |
|------|------|-------|
| `PhotoboothFrame.java` | `photobooth_frames` | Frame ảnh (name, image_url, slot_index, is_active) |

**Module Student:**
| File | Bảng | Mô tả |
|------|------|-------|
| `StudentBookmark.java` | `student_bookmarks` | Bookmark repo của sinh viên |

**Module Tech Stack:**
| File | Bảng | Mô tả |
|------|------|-------|
| `TechStack.java` | `tech_stacks` | Công nghệ (name, category, icon) |

**Utility types:**
| File | Mô tả |
|------|-------|
| `RoadmapItemTargetType.java` | Enum COURSE / TECH_STACK / SKILL |

---

### 2.4 Repository — 30 files

Mỗi entity có repository interface tương ứng, kế thừa `JpaRepository`. Danh sách đầy đủ:

```
AdminUserRepository.java
ChatChannelRepository.java
ChatMessageRepository.java
ChatSessionRepository.java
CommunityMessageRepository.java
CourseArticleRepository.java
CourseAssessmentRepository.java
CourseObjectiveRepository.java
CourseOutcomeRepository.java
CourseReferenceRepository.java
CourseRelationshipRepository.java
CourseRepository.java
CourseReviewRepository.java
CourseSessionRepository.java
CourseSyllabusRepository.java
CourseToolRepository.java
CourseTutorialRepository.java
CourseYoutubePlaylistRepository.java
GithubRepoRepository.java
KnowledgeChunkRepository.java
KnowledgeSourceRepository.java
NoteCodeSnippetRepository.java
NoteRepository.java
NotificationRepository.java
OtpRepository.java
PhotoboothFrameRepository.java
RepoCandidateRepository.java
RepoReviewRepository.java
RepoVoteRepository.java
StudentBookmarkRepository.java
StudentUserRepository.java
TechStackRepository.java
```

**Không có repository riêng cho enum** (OtpPurpose, CourseRelationType, NoteTargetType, etc.).

---

### 2.5 Service — 44 files

#### Core Services (top-level)

| File | Chức năng |
|------|-----------|
| `AdminAuthService.java` | Login admin, JWT generation |
| `AdminNoteService.java` | CRUD notes + code snippets |
| `AiService.java` | Facade cho AI features |
| `CommunityChatService.java` | Chat + kênh community |
| `CourseArticleService.java` | CRUD articles |
| `CourseRelationshipService.java` | Quản lý quan hệ môn học |
| `CourseService.java` | CRUD courses + syllabus |
| `CourseTutorialService.java` | CRUD tutorials |
| `CourseYoutubePlaylistService.java` | CRUD youtube playlists |
| `EmailService.java` | Gửi email (OTP, notification) |
| `GithubRepoService.java` | CRUD repos + GitHub API calls |
| `GithubScanService.java` | Quét GitHub theo topic, language |
| `JwtService.java` | JWT creation, validation, refresh |
| `KnowledgeGraphService.java` | Đồ thị tri thức dạng JSON (nodes + edges) |
| `LoginRateLimitService.java` | Rate limit đăng nhập |
| `NotificationService.java` | Push notification |
| `OtpRateLimitService.java` | Rate limit gửi OTP |
| `PhotoboothFrameService.java` | CRUD photobooth frames |
| `RepoCandidateService.java` | Duyệt repo candidate |
| `RevokedTokenStore.java` | Blacklist JWT tokens |
| `SocialService.java` | Social features (reviews, votes) |
| `StudentAuthService.java` | Register, login, OTP, forgot/reset password |
| `StudentBookmarkService.java` | Bookmark repo |
| `SubjectQaService.java` | Question-answering về môn học |
| `SupabaseStorageService.java` | Upload file lên Supabase |
| `TechStackService.java` | CRUD tech stacks |

#### AI Services (`service/ai/`)

| File | Chức năng |
|------|-----------|
| `AdviceGenerator.java` | Sinh lời khuyên học tập |
| `ChatService.java` | Chat AI thông thường (LLM call) |
| `CrawlerService.java` | Crawl web pages |
| `CurriculumMatcher.java` | So khớp nội dung với curriculum |
| `EmbeddingService.java` | Interface embedding |
| `ExaWebSearchClient.java` | Web search qua Exa API |
| `FireworksEmbeddingService.java` | Embedding qua Fireworks AI |
| `GraphQueryEngine.java` | Query knowledge graph |
| `LlmContextBuilder.java` | Xây dựng context cho LLM |
| `OfflineNoopEmbeddingService.java` | No-op embedding cho dev |
| `OpenAiCompatibleEmbeddingService.java` | Embedding cho OpenAI-compatible |
| `OpenCodeAiService.java` | AI service cho OpenCode |
| `PromptTemplates.java` | Template prompt cho LLM |
| `RoadmapGenerator.java` | Sinh roadmap học tập |
| `SummaryGenerator.java` | Tóm tắt nội dung |
| `WebSearchService.java` | Web search tổng hợp |

#### Knowledge / RAG Services (`service/knowledge/`)

| File | Chức năng |
|------|-----------|
| `AdminKnowledgeCommandService.java` | Admin commands cho knowledge |
| `AdminKnowledgeQueryService.java` | Admin queries cho knowledge |
| `CitationBuilder.java` | Xây dựng citation từ chunks |
| `CourseCodeDetector.java` | Phát hiện mã môn học trong text |
| `CourseFactQueryService.java` | Query facts về course |
| `CourseKnowledgeBootstrapService.java` | Khởi tạo knowledge cho courses |
| `CourseKnowledgeIndexer.java` | Index knowledge vào vector DB |
| `FirecrawlClient.java` | Firecrawl API client |
| `FirecrawlKnowledgeImporter.java` | Import knowledge từ Firecrawl |
| `FirecrawlProperties.java` | Config Firecrawl |
| `KnowledgeEmbeddingService.java` | Embed + vector search |
| `KnowledgeRetrievalService.java` | Retrieve chunks theo query |
| `KnowledgeSchemaInitializer.java` | Khởi tạo schema vector |
| `KnowledgeSourceService.java` | CRUD knowledge sources |
| `MarkerMarkdownLoader.java` | Load markdown với marker |
| `RagQueryPlan.java` | Value object cho RAG query plan |
| `RagQueryPlanner.java` | Lập kế hoạch query RAG |
| `RagResultReranker.java` | Rerank kết quả RAG |
| `SyllabusFactExtractor.java` | Trích xuất facts từ syllabus |
| `SyllabusIngestionService.java` | Ingestion syllabus vào knowledge base |
| `SyllabusValidator.java` | Validate syllabus trước khi ingest |
| `TutorIntent.java` | Enum intent của AI tutor |
| `TutorIntentClassifier.java` | Phân loại intent người dùng |
| `TutorRagService.java` | RAG service cho AI tutor |
| `WebKnowledgeIngestionService.java` | Web ingestion pipeline |

---

### 2.6 Controller — 25 files

| # | File | Prefix | Chức năng |
|---|------|--------|-----------|
| 1 | `AdminAuthController.java` | `/api/admin/auth` | Admin login |
| 2 | `AdminChatController.java` | `/api/admin/chat` | Monitor chat sessions |
| 3 | `AdminCommunityController.java` | `/api/admin/community` | Quản lý community messages |
| 4 | `AdminCourseController.java` | `/api/admin/courses` | CRUD courses (admin) |
| 5 | `AdminCourseRelationshipController.java` | `/api/admin/course-relationships` | Quản lý quan hệ môn |
| 6 | `AdminCourseResourceController.java` | `/api/admin/courses/{id}/resources` | CRUD resources theo course |
| 7 | `AdminCourseReviewController.java` | `/api/admin/course-reviews` | Quản lý reviews |
| 8 | `AdminGithubController.java` | `/api/admin/github` | GitHub scan trigger |
| 9 | `AdminKnowledgeController.java` | `/api/admin/knowledge` | CRUD knowledge sources + chunks |
| 10 | `AdminNoteController.java` | `/api/admin/notes` | CRUD notes |
| 11 | `AdminNotificationController.java` | `/api/admin/notifications` | Push notifications |
| 12 | `AdminRepoCandidateController.java` | `/api/admin/repo-candidates` | Duyệt repo candidate |
| 13 | `AdminRepoController.java` | `/api/admin/repos` | CRUD approved repos |
| 14 | `AdminRepoReviewController.java` | `/api/admin/repo-reviews` | Quản lý repo reviews |
| 15 | `AdminStatsController.java` | `/api/admin/stats` | Thống kê dashboard |
| 16 | `AdminStudentController.java` | `/api/admin/students` | Quản lý sinh viên |
| 17 | `AdminTechStackController.java` | `/api/admin/tech-stacks` | CRUD tech stacks |
| 18 | `PhotoboothFrameController.java` | `/api/photobooth` | CRUD photobooth frames |
| 19 | `PublicAiController.java` | `/api/ai` | AI endpoints public (chat, subject-qa, roadmap, etc.) |
| 20 | `PublicCourseController.java` | `/api/courses` | Course list + detail (public) |
| 21 | `PublicCourseRelationshipController.java` | `/api/course-relationships` | Quan hệ môn public |
| 22 | `PublicDiscoveryController.java` | `/api/discovery` | Discovery feed |
| 23 | `PublicRepoController.java` | `/api/repos` | Repo list + detail (public) |
| 24 | `PublicSocialController.java` | `/api/social` | Reviews, votes |
| 25 | `PublicTechStackController.java` | `/api/tech-stacks` | Tech stacks public |
| 26 | `StudentAuthController.java` | `/api/student` | Student register, login, OTP, profile |
| 27 | `StudentBookmarkController.java` | `/api/student/bookmarks` | Bookmarks |
| 28 | `StudentCommunityController.java` | `/api/student/community` | Community features |
| 29 | `StudentSocialController.java` | `/api/student/social` | Student social actions |
| 30 | `SubjectQaController.java` | `/api/ai/subject-qa` | Subject Q&A (query + stream SSE) |

**Thực tế: 30 controller files** (bao gồm cả được phân bố trong dto/admin, dto/community, etc.)

---

### 2.7 DTO — 44 files

**admin/ (22 files):**
```
AdminCourseUpsertRequest.java       ApprovedRepoUpdateRequest.java
AdminStatsResponse.java             ArticleRequest.java
AdminStudentResponse.java           ArticleResponse.java
AdminTechStackResponse.java         CandidateReviewRequest.java
ChatMessageAdminResponse.java       ChatSessionAdminResponse.java
CommunityMessageAdminResponse.java  CourseRelationshipRequest.java
CourseRelationshipResponse.java     CourseReviewAdminResponse.java
GithubScanRequest.java              LoginRequest.java
LoginResponse.java                  NoteCodeSnippetResponse.java
NoteResponse.java                   NotificationResponse.java
RepoCandidateResponse.java          RepoReviewAdminResponse.java
ReviewerStatsResponse.java          TutorialRequest.java
TutorialResponse.java               YoutubePlaylistRequest.java
YoutubePlaylistResponse.java
```

**community/ (5 files):**
```
ChatChannelResponse.java      ChatMessageRequest.java
ChatMessageResponse.java      RepoSocialInfoResponse.java
RepoVoteRequest.java          RepoVoteResponse.java
ReviewRequest.java            ReviewResponse.java
ReviewSummaryResponse.java
```

**knowledge/ (15 files):**
```
Citation.java                 CrawlRequest.java
EmbedResponse.java            ExtractedSyllabusFacts.java
FolderIngestionSummary.java   IngestFileRequest.java
IngestionReport.java          KnowledgeSourceResponse.java
RagPreviewRequest.java        RagPreviewResponse.java
SearchRequest.java            SearchResponse.java
SyllabusDetailsResponse.java  TutorResponse.java
WebImportRequest.java
```

**publicapi/ (12 files):**
```
AiQueryRequest.java           AiQueryResponse.java
AiResponse.java               ChatRequest.java
ChatResponse.java             CourseDetailResponse.java
CourseSummaryResponse.java    KnowledgeGraphResponse.java
RepoSummaryResponse.java      RoadmapGenerationRequest.java
RoadmapRecommendationResponse.java  SubjectQaRequest.java
SubjectQaResponse.java        SubjectQaStreamEvent.java
TechStackResponse.java        WebSearchResponse.java
```

**student/ (6 files):**
```
ForgotPasswordRequest.java    OtpVerificationRequest.java
ResetPasswordRequest.java     StudentAuthResponse.java
StudentBookmarkRequest.java   StudentBookmarkResponse.java
StudentLoginRequest.java      StudentProfileResponse.java
StudentRegisterRequest.java
```

**Root DTO:**
```
PhotoboothFrameDTO.java
```

---

### 2.8 Exception — 4 files

| File | Mô tả |
|------|-------|
| `ApiExceptionHandler.java` | `@ControllerAdvice` — global exception handler, trả về JSON error response |
| `BadRequestException.java` | 400 Bad Request |
| `NotFoundException.java` | 404 Not Found |
| `UnauthorizedException.java` | 401 Unauthorized |

---

### 2.9 Event — 2 files

| File | Mô tả |
|------|-------|
| `NotificationEvent.java` | Event gửi notification (publish khi cần notify) |
| `RelationshipChangedEvent.java` | Event khi course relationship thay đổi |

---

### 2.10 Constant — 1 file

| File | Mô tả |
|------|-------|
| `CurriculumConstants.java` | `MAJOR_COURSE_CODES` map (chuyên ngành → danh sách mã môn), `ELECTIVE_COURSE_CODES`, `GENERAL_EDUCATION_CODES` |

---

### 2.11 Database Migrations — 18 files

| File | Nội dung |
|------|----------|
| `schema.sql` | Schema gốc (courses, syllabus, articles, assessments, objectives, outcomes, sessions, tools, references, github_repos, repo_candidates, reviews, tech_stacks, admin_users, student_users, otps, student_bookmarks, chat_messages, chat_channels, notes, notifications) |
| `schema-complete.sql` | Schema hoàn chỉnh + indexes |
| `data.sql` | Seed data mẫu |
| `cleanup_courses.sql` | Xoá courses |
| `update_electives.sql` | Update elective categories |
| `update_se2025.sql` | Update chương trình SE2025 |
| `V002__create_photobooth_frames.sql` | Photobooth frames |
| `V003__add_repo_context_fields.sql` | Repo context + summary |
| `V003__ai_course_qa_schema.sql` | Subject QA schema + knowledge sources/chunks |
| `V003a__ai_course_qa_schema.sql` | Variant |
| `V004__add_repo_last_pushed_at.sql` | last_pushed_at column |
| `V004__populate_syllabus_summaries.sql` | Syllabus summaries |
| `V004a__populate_syllabus_summaries.sql` | Variant |
| `V005__fix_subject_qa_and_tech_stacks.sql` | Fixes |
| `V006__create_community_features.sql` | Chat channels, community messages, votes |
| `V007__add_otp_purpose_and_email_verified.sql` | OTP purpose enum + email_verified |
| `V007__create_syllabus_rag_pipeline.sql` | RAG pipeline tables |
| `V008__add_source_id_to_child_tables.sql` | Source ID FK |
| `V009__add_pgvector_and_embedding.sql` | pgvector extension + embedding columns |
| `V010__resize_knowledge_embeddings_for_fireworks.sql` | Resize embedding dims |
| `V011__add_hybrid_rag_search_support.sql` | Hybrid search (full-text + vector) |
| `V012__create_notifications.sql` | Notifications table |
| `V012__harden_database_indexes_and_rls.sql` | Index + RLS hardening |
| `V013__harden_remaining_supabase_advisors.sql` | Advisor hardening |
| `V014__declare_backend_owned_table_rls.sql` | Backend-owned RLS |
| `V015__move_vector_extension_to_extensions_schema.sql` | Vector extension migration |

**Ghi chú**: Có version conflict ở V003, V004, V007, V012 (hai file cùng số). Đây là technical debt — cần dọn.

---

### 2.12 Test — 28 files

| File | Loại |
|------|------|
| `DevorbitApiApplicationTests.java` | Context load test |
| `AdminAuthControllerTest.java` | Controller |
| `AdminCourseControllerTest.java` | Controller |
| `AdminKnowledgeControllerTest.java` | Controller |
| `AdminRepoCandidateControllerTest.java` | Controller |
| `PublicAiControllerTest.java` | Controller |
| `PublicCourseControllerTest.java` | Controller |
| `PublicRepoControllerTest.java` | Controller |
| `StudentAuthControllerTest.java` | Controller |
| `Config/FirecrawlDisabledTest.java` | Config |
| `Config/SupabaseDatabaseHardeningInitializerTest.java` | Config |
| `Config/WebSocketConfigContractTest.java` | Config |
| `GithubRepoServiceTest.java` | Service |
| `GithubScanServiceTest.java` | Service |
| `RepoCandidateServiceTest.java` | Service |
| `StudentAuthServiceTest.java` | Service |
| `SubjectQaServiceTest.java` | Service |
| `ChatServiceTest.java` | AI Service |
| `OpenCodeAiServiceTest.java` | AI Service |
| `WebSearchServiceTest.java` | AI Service |
| `CitationBuilderTest.java` | Knowledge Service |
| `CourseFactQueryServiceTest.java` | Knowledge Service |
| `CourseKnowledgeIndexerTest.java` | Knowledge Service |
| `FirecrawlKnowledgeImporterTest.java` | Knowledge Service |
| `KnowledgeEmbeddingServiceTest.java` | Knowledge Service |
| `KnowledgeRetrievalServiceTest.java` | Knowledge Service |
| `KnowledgeSchemaInitializerTest.java` | Knowledge Service |
| `MarkerMarkdownLoaderTest.java` | Knowledge Service |
| `RagQueryPlannerTest.java` | Knowledge Service |
| `RagResultRerankerTest.java` | Knowledge Service |
| `SyllabusIngestionServiceTest.java` | Knowledge Service |
| `SyllabusValidatorTest.java` | Knowledge Service |
| `TutorRagServiceTest.java` | Knowledge Service |
| `WebKnowledgeIngestionServiceTest.java` | Knowledge Service |
| `CommunityMilestone3ContractTest.java` | Contract |
| `CommunityPersistenceContractTest.java` | Contract |

---

### 2.13 Tài nguyên & Config — 10 files

| File | Mô tả |
|------|-------|
| `application.yaml` | Main config: datasource (Supabase Postgres), JPA, JWT, GitHub, Exa, Firecrawl, AI endpoints, CORS, Supabase storage |
| `application-local.yaml` | Local profile (H2 in-memory, different ports) |
| `data.sql` | Seed data |
| `schema.sql` | Full schema |
| `schema-complete.sql` | Schema + indexes |
| `cleanup_courses.sql` | Utility |
| `update_electives.sql` | Utility |
| `update_se2025.sql` | Utility |
| `AGENTS.md` | Agent guide |

---

---

## 🔥 Chi tiết tính năng — Backend API

Dưới đây là mô tả chi tiết từng tính năng backend, luồng xử lý, và các endpoint API liên quan.

---

### 🧭 1. Course Discovery & Curriculum

**Mục đích**: Cung cấp danh mục môn học toàn bộ chương trình UIT (SE, IT, IS, CS...), bao gồm đề cương, tài liệu, đánh giá, và quan hệ tiên quyết.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/api/courses` | Danh sách môn học (filter: major, semester, search) |
| GET | `/api/courses/{id}` | Chi tiết môn + syllabus + articles + reviews |
| GET | `/api/course-relationships?courseId=X` | Quan hệ tiên quyết/tương đương |

**Xử lý đặc biệt**:
- `CourseService.java` — join courses ↔ course_syllabi để trả về course detail đầy đủ
- `CourseRelationshipService.java` — xây dựng đồ thị prerequisite DAG
- `CurriculumConstants.java` — hardcode mapping chuyên ngành → mã môn cho GPA calculator

**Entity tham gia**: Course, CourseSyllabus, CourseArticle, CourseAssessment, CourseObjective, CourseOutcome, CourseReference, CourseSession, CourseTool, CourseTutorial, CourseYoutubePlaylist, CourseReview, CourseRelationship

---

### 🤖 2. AI Subject Q&A (RAG trên syllabus)

**Mục đích**: Cho phép sinh viên hỏi đáp về nội dung môn học. AI trả lời dựa trên syllabus và tài liệu đã được ingest vào knowledge base.

**Luồng xử lý**:
```
User query → SubjectQaService → TutorIntentClassifier (phân loại intent)
  → CourseCodeDetector (trích mã môn)
  → KnowledgeRetrievalService (hybrid search: full-text + vector)
    → RagQueryPlanner → RagResultReranker → CitationBuilder
  → LLM context build → OpenCodeAiService.call() → response
```

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| POST | `/api/ai/subject-qa/query` | Hỏi đáp đồng bộ (JSON response) |
| POST | `/api/ai/subject-qa/stream` | Hỏi đáp streaming (SSE — Server-Sent Events) |

**Streaming**: `SubjectQaStreamingConfig` dùng WebClient để stream token-by-token từ LLM provider về client SSE.

**Entity tham gia**: KnowledgeChunk, KnowledgeSource, CourseSyllabus

---

### 💬 3. AI Chat & AI Tutor

**Mục đích**: Chat tổng quát với AI, bao gồm cả chat có context từ kiến thức đã index (RAG) và chat thông thường.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| POST | `/api/ai/chat` | Chat thông thường (lưu session) |
| POST | `/api/ai/query` | Query AI với context |
| POST | `/api/ai/subject-qa/query` | Subject Q&A (tích hợp RAG) |

**ChatSession**: mỗi phiên chat được lưu vào `chat_sessions` + `chat_messages`, admin có thể monitor qua `AdminChatController`.

**Service tham gia**: ChatService, OpenCodeAiService, LlmContextBuilder, PromptTemplates

---

### 🗺️ 4. AI Roadmap Generator

**Mục đích**: Sinh lộ trình học tập cá nhân hóa dựa trên mục tiêu, chuyên ngành, và kỹ năng hiện tại của sinh viên.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| POST | `/api/ai/roadmap` | Sinh roadmap (nhận major, goals, current skills) |

**Xử lý**:
- `RoadmapGenerator.java` — gọi LLM với prompt template chứa curriculum hiện tại
- `CurriculumMatcher.java` — so khớp gợi ý của AI với môn học thực tế trong DB
- `RoadmapItemTargetType` enum: COURSE / TECH_STACK / SKILL

---

### 📦 5. GitHub Repo Discovery & Scan

**Mục đích**: Tự động quét GitHub để tìm repo chất lượng cao cho sinh viên UIT học tập, có pipeline duyệt + đánh giá cộng đồng.

**Luồng**:
```
Admin trigger scan → GithubScanService
  → GitHub Search API (topic, language, stars)
  → Lưu vào repo_candidates (PENDING)
  → Admin duyệt qua AdminRepoCandidateController
  → APPROVED → chuyển sang github_repos (public)
```

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| POST | `/api/admin/github/scan` | Kích hoạt scan |
| GET | `/api/admin/repo-candidates` | Danh sách candidate chờ duyệt |
| PUT | `/api/admin/repo-candidates/{id}/review` | Duyệt/từ chối |
| GET | `/api/admin/repos` | CRUD approved repos |
| GET | `/api/repos` | Repo list public (filter: language, topics) |
| GET | `/api/repos/{id}` | Repo detail public |

**AI Analysis tích hợp**: Mỗi repo có AI-generated summary, context, và đánh giá tự động qua AI.

---

### 🔐 6. Authentication & Authorization

**Mục đích**: Xác thực admin (JWT role-based) và sinh viên (JWT + OTP email).

#### Admin Auth
| Method | Path | Chức năng |
|--------|------|-----------|
| POST | `/api/admin/auth/login` | Login → JWT token |

- `AdminAuthService.java` — verify password, sinh JWT (30 phút)
- `JwtAuthenticationFilter.java` — extract JWT từ header, verify, set SecurityContext
- Admin-only endpoints guard bằng `.hasAuthority("ROLE_ADMIN")`

#### Student Auth
| Method | Path | Chức năng |
|--------|------|-----------|
| POST | `/api/student/register` | Đăng ký → gửi OTP email |
| POST | `/api/student/verify-otp` | Xác thực OTP → active account |
| POST | `/api/student/login` | Login → JWT token |
| POST | `/api/student/forgot-password` | Quên mật khẩu → gửi OTP |
| POST | `/api/student/reset-password` | Reset password với OTP |
| POST | `/api/student/resend-otp` | Gửi lại OTP |

**Bảo mật**:
- `LoginRateLimitService.java` — chặn brute force login
- `OtpRateLimitService.java` — giới hạn gửi OTP (tránh spam email)
- `RevokedTokenStore.java` — blacklist JWT khi logout
- `JwtService.java` — refresh token support

---

### 🌐 7. Community Chat (WebSocket Real-time)

**Mục đích**: Chat real-time giữa các sinh viên qua WebSocket STOMP protocol.

**Config**:
- `WebSocketConfig.java` — register STOMP endpoint `/ws/community`
- SockJS fallback cho browser không support WebSocket

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/api/student/community/channels` | Danh sách kênh |
| POST | `/api/student/community/channels/{id}/messages` | Gửi message |
| GET | `/api/student/community/channels/{id}/messages` | Lấy lịch sử chat |

**Frontend kết nối**: `useCommunitySocket.ts` hook — STOMP client, auto-reconnect.

---

### 🌟 8. Social Features (Reviews & Votes)

**Mục đích**: Cộng đồng đánh giá môn học và repo, vote chất lượng.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| POST | `/api/social/reviews` | Tạo review |
| GET | `/api/social/reviews?targetType=COURSE&targetId=X` | Lấy reviews |
| POST | `/api/social/votes` | Vote repo (up/down) |
| GET | `/api/social/reviews/{id}/summary` | Review summary |

---

### 📸 9. AI Photobooth

**Mục đích**: Sinh viên chụp ảnh (hoặc upload), ghép với frame UIT, tải xuống ảnh đã ghép.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/api/photobooth` | Danh sách frame có sẵn |
| POST | `/api/photobooth` | (Admin) Thêm frame mới |
| POST | `/api/student/photobooth/upload` | Upload ảnh đã ghép lên Supabase Storage |

**SupabaseStorageService.java**: Upload file binary lên Supabase Storage bucket, trả về public URL.

---

### 📊 10. GPA Calculator

**Mục đích**: Tính điểm trung bình tích lũy theo đúng chương trình đào tạo UIT (từng chuyên ngành).

**Backend support**:
- `CurriculumConstants.java` — `MAJOR_COURSE_CODES` map, `ELECTIVE_COURSE_CODES`, `GENERAL_EDUCATION_CODES`
- Các hằng số này được frontend gọi để render form GPA

**Cách tính**: GPA = tổng (điểm × tín chỉ) / tổng tín chỉ, phân loại theo compulsory/elective/general.

---

### 🧠 11. RAG Pipeline (Knowledge Base)

**Mục đích**: Xây dựng và query kho tri thức từ syllabus, tài liệu web, file markdown.

**Luồng đầy đủ**:
```
Web/File upload → WebKnowledgeIngestionService / SyllabusIngestionService
  → FirecrawlKnowledgeImporter (crawl web)
  → MarkerMarkdownLoader (parse markdown)
  → Chunking → CourseKnowledgeIndexer
    → KnowledgeEmbeddingService → FireworksEmbeddingService
      → pgvector (Supabase Postgres)

Query → KnowledgeRetrievalService
  → Hybrid search (full-text tsvector + vector cosine)
  → RagResultReranker (cross-encoder style rerank)
  → CitationBuilder → LLM
```

**API Endpoints (Admin)**:
| Method | Path | Chức năng |
|--------|------|-----------|
| POST | `/api/admin/knowledge/sources` | Thêm nguồn kiến thức |
| POST | `/api/admin/knowledge/ingest` | Ingest nguồn vào vector DB |
| POST | `/api/admin/knowledge/ingest-web` | Import từ web URL |
| POST | `/api/admin/knowledge/search` | Search trong knowledge base |

**Embedding providers (pluggable)**:
- `FireworksEmbeddingService` — production (Fireworks AI)
- `OpenAiCompatibleEmbeddingService` — fallback
- `OfflineNoopEmbeddingService` — dev (no-op)

---

### 🔎 12. Web Search (Exa API)

**Mục đích**: Tìm kiếm web real-time qua Exa AI, phục vụ AI tutor tra cứu thông tin mới.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| POST | `/api/ai/web-search` | Tìm kiếm web |

**Service**: `ExaWebSearchClient.java` → gọi Exa Search API → parse kết quả.

---

### 📈 13. Admin Dashboard & Stats

**Mục đích**: Cung cấp thống kê tổng quan cho admin dashboard.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/api/admin/stats` | Tổng quan: số courses, repos, students, reviews, active users |
| GET | `/api/admin/stats/reviewer` | Stats theo reviewer |
| GET | `/api/admin/students` | Danh sách sinh viên + stats |

---

### 🔔 14. Notifications

**Mục đích**: Hệ thống thông báo nội bộ cho admin.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/api/admin/notifications` | Danh sách notification |
| PUT | `/api/admin/notifications/{id}/read` | Đánh dấu đã đọc |

**Event-driven**: `NotificationEvent.java` được publish bởi các service khác → `NotificationService` lắng nghe và persist.

---

### 🏷️ 15. Tech Stack Management

**Mục đích**: Quản lý danh mục công nghệ (dùng để gắn tag cho courses, repos).

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/api/tech-stacks` | Danh sách tech stacks public |
| POST | `/api/admin/tech-stacks` | (Admin) Thêm tech stack |

---

### 📝 16. Admin Notes & Articles

**Mục đích**: Admin viết ghi chú, bài viết, tutorial, youtube playlist cho sinh viên.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| GET/POST | `/api/admin/notes` | CRUD notes |
| GET/POST | `/api/admin/courses/{id}/resources/articles` | CRUD articles (theo course) |
| GET/POST | `/api/admin/courses/{id}/resources/tutorials` | CRUD tutorials |
| GET/POST | `/api/admin/courses/{id}/resources/youtube` | CRUD youtube playlists |

---

### 🔗 17. Course Relationships

**Mục đích**: Quản lý đồ thị quan hệ môn học (prerequisite, corequisite, equivalent).

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/api/course-relationships?courseId=X` | Quan hệ của môn X |
| POST | `/api/admin/course-relationships` | (Admin) Thêm quan hệ |
| DELETE | `/api/admin/course-relationships/{id}` | (Admin) Xoá quan hệ |

**Event**: `RelationshipChangedEvent` → trigger cập nhật knowledge graph.

---

### 🚀 18. Discovery Feed

**Mục đích**: Feed khám phá nội dung cho homepage sinh viên.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/api/discovery` | Discovery feed (courses, repos, community posts) |

---

### 📚 19. Student Bookmarks

**Mục đích**: Sinh viên bookmark repo để xem lại sau.

**API Endpoints**:
| Method | Path | Chức năng |
|--------|------|-----------|
| GET | `/api/student/bookmarks` | Danh sách bookmarks |
| POST | `/api/student/bookmarks` | Thêm bookmark |
| DELETE | `/api/student/bookmarks/{id}` | Xoá bookmark |

---

### 🛡️ Security Architecture

**Spring Security chain**:
```
Request → CorsFilter → JwtAuthenticationFilter → SecurityFilterChain
  → /api/admin/**          → ROLE_ADMIN
  → /api/student/**        → ROLE_STUDENT (authenticated)
  → /api/courses/**        → PERMIT_ALL
  → /api/repos/**          → PERMIT_ALL
  → /api/tech-stacks/**    → PERMIT_ALL
  → /api/discovery/**      → PERMIT_ALL
  → /ws/community/**       → PERMIT_ALL
  → /api/ai/**             → AUTHENTICATED
  → /swagger-ui/**         → ROLE_ADMIN
  → any other              → DENY_ALL
```

---

### 🗄️ Database Architecture

**Supabase PostgreSQL** với extensions:
- `pgvector` — embedding search (cosine similarity)
- `pg_trgm` — fuzzy text search
- Row-Level Security (RLS) cho multi-tenant
- `tsvector` full-text search cho hybrid RAG

**Connection pool**: Spring Boot + HikariCP (config trong `application.yaml`).

**Migrations**: Flyway-style SQL files trong `db/migration/`.

---


### 2.14 File gốc dự án — 11 files

| File | Mô tả |
|------|-------|
| `pom.xml` | Maven build: Spring Boot 4.0.6, Java 21, dependencies (JPA, Security, WebSocket, Webflux, Mail, JWT jjwt, SpringDoc, Caffeine, jsoup, PostgreSQL, H2 test) |
| `Dockerfile` | Multi-stage: Maven build → JRE 21 runtime, cổng 8080 |
| `mvnw` / `mvnw.cmd` | Maven Wrapper |
| `run.bat` | Script chạy local |
| `.env` / `.env.example` | Environment variables |
| `AGENTS.md` | Agent instructions |
| `ARCHITECTURE.md` | Architecture docs |
| `.gitattributes` | Git config |
| `GenHash.java` / `InsertAdmin.java` | Util scripts |
| `tmp-admin.sql` | Temp SQL |

---

## 4. devorbit-web — Frontend

### 4.1 Cấu trúc thư mục

```
devorbit-web/
├── src/
│   ├── pages/                  # 23 files — Page components
│   │   ├── admin/              # 11 files
│   │   ├── student/            # 12 files (incl. knowledge-graph/)
│   │   └── student/knowledge-graph/  # 12 files — 3D galaxy
│   ├── components/             # 60+ files — UI components
│   │   ├── admin/              # Admin dashboard components
│   │   │   ├── chat/           # Chat monitoring
│   │   │   ├── community/      # Community management
│   │   │   ├── courses/        # Course CRUD
│   │   │   ├── dashboard/      # Stats, quick actions
│   │   │   ├── layout/         # AdminLayout, Sidebar, Topbar
│   │   │   ├── photobooth/     # Frame management
│   │   │   ├── repos/          # Repo pipeline
│   │   │   ├── reviews/        # Review tables
│   │   │   ├── shared/         # Shared admin components
│   │   │   └── students/       # Student management
│   │   ├── shared/             # Shared UI (Avatar, Toast, StarBadge, etc.)
│   │   ├── student/            # Student-facing components
│   │   └── photobooth/         # Photobooth components
│   ├── lib/                    # API clients, utilities, analysis
│   │   ├── api/                # baseApi, generatedApi
│   │   └── frames/             # Frame definitions & service
│   ├── hooks/                  # Custom React hooks
│   ├── motion/                 # Animation primitives (Framer Motion + GSAP)
│   ├── scenes/                 # Three.js scenes
│   ├── types/                  # TypeScript interfaces
│   ├── utils/                  # Utility functions
│   ├── performance/            # Performance monitoring
│   └── styles/                 # CSS
├── .pi/gsd/                    # ~120 files (same as API)
├── public/frames/              # 3 photobooth frame images
└── (root)                      # 14 config files
```

---

### 4.2 Pages — 23 files

#### Student Pages (12 files)

| # | File | Route | Chức năng |
|---|------|-------|-----------|
| 1 | `HomePage.tsx` | `/` | Home: hero carousel, course grid, repo feed, community preview, knowledge graph CTA |
| 2 | `CourseListPage.tsx` | `/courses` | Danh sách môn học, filter theo semester, major, search |
| 3 | `CourseDetailPage.tsx` | `/courses/:courseId` | Chi tiết môn: syllabus, articles, reviews, prerequisites, roadmap |
| 4 | `RepoDetailPage.tsx` | `/repos/:repoId` | Chi tiết repo: AI analysis, reviews, votes, file tree explorer |
| 5 | `StudentLoginPage.tsx` | `/student/login` | Login + Register form (email, password, OTP) |
| 6 | `StudentBookmarksPage.tsx` | `/student/bookmarks` | Bookmarked repos |
| 7 | `StudentProfilePage.tsx` | `/student/profile` | Profile, forgot/reset password |
| 8 | `PhotoboothPage.tsx` | `/photobooth` | Chụp ảnh + chọn frame + tải xuống |
| 9 | `GpaCalculatorPage.tsx` | `/gpa-calculator` | Tính GPA theo chương trình UIT |
| 10 | `KnowledgeGraphPage.tsx` | `/knowledge-graph` | 🔭 **GalaxyPage** — đồ thị tri thức 3D |
| 11 | `CommunityPage.tsx` | `/community` | Community chat + reviews |
| 12 | `AiTutorPage.tsx` | `/ai-tutor` | AI Tutor chat với RAG context |

#### Knowledge Graph Sub-pages (12 files trong `knowledge-graph/`)

| File | Vai trò |
|------|---------|
| `GalaxyPage.tsx` | Page entry, layout chính |
| `GalaxyCanvas.tsx` | Canvas Three.js renderer |
| `camera/GalaxyCamera.tsx` | Camera orbit controls |
| `context/PlanetPositionContext.tsx` | React context cho vị trí planet |
| `effects/OrbitRings.tsx` | Vòng quỹ đạo |
| `effects/PlanetTrail.tsx` | Trail của planet |
| `effects/Starfield.tsx` | Nền sao |
| `entities/Planet.tsx` | Planet 3D object |
| `entities/Wormhole.tsx` | Wormhole effect |
| `layout/galaxyLayout.ts` | Layout algorithm |
| `layout/types.ts` | Type definitions |
| `store/useGalaxyStore.ts` | Zustand store |
| `systems/ConstellationSystem.tsx` | Hệ thống chòm sao |
| `systems/OrbitalGroup.tsx` | Nhóm quỹ đạo |
| `systems/WormholeSystem.tsx` | Hệ thống wormhole |
| `ui/GalaxyOverlay.tsx` | UI overlay |
| `ui/TimeTravelSlider.tsx` | Slider thời gian |

#### Admin Pages (11 files)

| # | File | Route | Chức năng |
|---|------|-------|-----------|
| 1 | `LoginPage.tsx` | `/admin/login` | Admin login |
| 2 | `DashboardPage.tsx` | `/admin` | Dashboard: stats, recent activity, quick actions |
| 3 | `CoursesPage.tsx` | `/admin/courses` | CRUD courses + resources |
| 4 | `ReposPage.tsx` | `/admin/repos` | Repo pipeline (scan, candidates, approved) |
| 5 | `StudentsPage.tsx` | `/admin/students` | Quản lý sinh viên |
| 6 | `ReviewsPage.tsx` | `/admin/reviews` | Course + repo reviews |
| 7 | `CommunityPage.tsx` | `/admin/community` | Quản lý community messages + channels |
| 8 | `ChatMonitorPage.tsx` | `/admin/chat` | Monitor chat sessions |
| 9 | `RelationshipsPage.tsx` | `/admin/relationships` | Quản lý course relationships |
| 10 | `PhotoboothPage.tsx` | `/admin/photobooth` | Quản lý photobooth frames |
| 11 | `TechStackPage.tsx` | `/admin/techstack` | Quản lý tech stacks |

---

### 4.3 Components — 60+ files

#### Layout & Core (6 files)
| File | Chức năng |
|------|-----------|
| `Layout.tsx` | Layout chính: particle background, floating particles scene, header |
| `ErrorBoundary.tsx` | Error boundary wrapper |
| `ParticleNetwork.tsx` | Particle network animation |
| `SmoothScrollProvider.tsx` | Lenis smooth scroll provider |
| `navigation.ts` | Navigation constants (routes, icons, labels) |
| `navigation.test.ts` | Tests cho navigation |

#### Shared Components (12 files)
| File | Chức năng |
|------|-----------|
| `Avatar.tsx` | Avatar component |
| `ErrorRetry.tsx` | Error + retry button |
| `PrerequisitesView.tsx` | Hiển thị môn tiên quyết |
| `ResponsiveTable.tsx` | Responsive table |
| `SemesterSelector.tsx` | Select học kỳ |
| `Skeleton.tsx` | Loading skeleton |
| `StarBadge.tsx` | Star rating badge |
| `SubjectProgressCard.tsx` | Card tiến độ môn học |
| `TechTagCloud.tsx` | Cloud tag tech stack |
| `Toast.tsx` | Toast notification |

#### Student Components (20 files)
| File | Chức năng |
|------|-----------|
| `AiChatWidget.tsx` | AI Chat widget (floating) |
| `ChatContext.tsx` | React context cho chat |
| `ChatMessage.tsx` | Message bubble |
| `ChatPanel.tsx` | Chat panel UI |
| `CourseCard.tsx` | Card hiển thị môn học |
| `CourseKnowledgeGraph.tsx` | 2D knowledge graph (force-graph) |
| `DiscoveryFeed.tsx` | Feed khám phá |
| `FileTreeExplorer.tsx` | File tree cho repo detail |
| `HeroStoryCarousel.tsx` | Hero carousel với story |
| `KanbanBoard.tsx` | Kanban board component |
| `KanbanCard.tsx` | Kanban card |
| `KanbanColumn.tsx` | Kanban column |
| `ProfileDropdown.tsx` | Dropdown profile |
| `RepoAiAnalysisSection.tsx` | AI analysis cho repo |
| `RepoCard.tsx` | Card hiển thị repo |
| `RepoFilterBar.tsx` | Filter bar cho repo list |
| `ReviewSection.tsx` | Review section |
| `VoteButtons.tsx` | Upvote/downvote buttons |
| `CareerOrientationData.ts` | Data định hướng nghề nghiệp |
| `curriculumData.ts` | Data curriculum (majors, semesters, courses) |

#### Photobooth Components (4 files)
| File | Chức năng |
|------|-----------|
| `DownloadSection.tsx` | Download ảnh đã ghép |
| `FrameSelector.tsx` | Chọn frame |
| `PhotoUploadSection.tsx` | Upload ảnh |
| `PreviewCanvas.tsx` | Preview canvas |

#### Admin Components (30+ files)
| Thư mục | Files | Chức năng |
|----------|-------|-----------|
| `chat/` | 2 | ChatMessageView, ChatSessionTable |
| `community/` | 2 | ChannelList, MessageTable |
| `courses/` | 3 | CourseFormDialog, CourseTable, ResourceDialog |
| `dashboard/` | 3 | QuickActions, RecentActivity, StatsRow |
| `layout/` | 3 | AdminLayout, AdminSidebar, AdminTopbar |
| `photobooth/` | 3 | FrameSlotEditor, FrameUploadDialog, PhotoboothFrameGrid |
| `repos/` | 5 | RepoApprovedTab, RepoCandidatesTab, RepoEditDialog, RepoPipelineTabs, RepoScanTab |
| `reviews/` | 2 | CourseReviewTable, RepoReviewTable |
| `shared/` | 7 | AdminConfirmDialog, AdminErrorBanner, AdminPageLayout, AdminSpinner, AdminStatsCard, AdminTable, NotificationDropdown |
| `students/` | 2 | StudentDetailDialog, StudentTable |

---

### 4.4 Lib / API Layer — 16 files

| File | Chức năng |
|------|-----------|
| `api.ts` | Base HTTP client (fetch wrapper, error handling) |
| `api.test.ts` | Tests |
| `api/baseApi.ts` | API base URL + headers |
| `api/generatedApi.ts` | Auto-generated API từ OpenAPI spec (RTK Query codegen) |
| `auth.ts` | Auth context, token management |
| `adminApi.ts` | Admin API calls |
| `adminAuth.ts` | Admin auth helpers |
| `adminHooks.ts` | React hooks cho admin |
| `colors.ts` | Theme colors |
| `hooks.ts` | Common hooks |
| `repoAiAnalysis.ts` | AI analysis logic cho repo |
| `repoAiAnalysis.test.ts` | Tests |
| `repoAnalysisService.ts` | Repo analysis service |
| `repoAnalysisService.test.ts` | Tests |
| `repoEvaluation.ts` | Repo evaluation logic |
| `repoEvaluation.test.ts` | Tests |
| `repoSearch.ts` | Repo search utility |
| `repoSearch.test.ts` | Tests |
| `repoText.ts` | Repo text processing |
| `repoText.test.ts` | Tests |
| `frames/frameDefinitions.ts` | Frame definitions (slots, sizes) |
| `frames/frameService.ts` | Frame API service |
| `photoCompositor.ts` | Photo compositing logic |
| `photoFilters.ts` | Photo filters |

---

### 4.5 Hooks — 12 files

| File | Chức năng |
|------|-----------|
| `useAiRoadmap.ts` | AI roadmap generation hook |
| `useCommunity.ts` | Community data hook |
| `useCommunitySocket.ts` | STOMP WebSocket hook |
| `useCourseList.ts` | Course list hook |
| `useDebounce.ts` | Debounce hook |
| `useKeyboardShortcuts.ts` | Keyboard shortcuts |
| `useKnowledgeGraph.ts` | Knowledge graph data hook |
| `useNotifications.ts` | Notifications hook |
| `useScrollRestoration.ts` | Scroll position restoration |
| `useSearchHistory.ts` | Search history hook |
| `useSubjectQa.ts` | Subject Q&A streaming hook |
| `useTheme.ts` | Theme toggle hook |

**Test files:**
```
__tests__/useDebounce.test.ts
__tests__/useSubjectQa.stream.test.ts
```

---

### 4.6 Types — 5 files

| File | Nội dung |
|------|----------|
| `admin.ts` | Admin types (AdminStats, AdminStudent, AdminNotification, etc.) |
| `api.ts` | API response/request types |
| `bookmarks.ts` | Bookmark types |
| `common.ts` | Common shared types |
| `frames.ts` | Photobooth frame types |
| `progress.ts` | Progress tracking types |

---

### 4.7 Utils — 9 files

| File | Chức năng |
|------|-----------|
| `date.ts` | Date formatting (Vietnamese locale) |
| `gpa.ts` | GPA calculation (parse grades, weighted average) |
| `pagination.ts` | Pagination helper |
| `performance.ts` | Performance utilities |
| `validation.ts` | Form validation |

**Test files:**
```
__tests__/date.test.ts
__tests__/gpa.test.ts
__tests__/pagination.test.ts
__tests__/validation.test.ts
```

---

### 4.8 Motion System — 8 files

| File | Vai trò |
|------|---------|
| `index.ts` | Re-export |
| `hooks/useMagneticHover.ts` | Magnetic hover effect hook |
| `hooks/useReducedMotion.ts` | Accessibility: reduced motion |
| `primitives/BlurReveal.tsx` | Blur reveal animation |
| `primitives/FadeReveal.tsx` | Fade reveal animation |
| `primitives/ParallaxLayer.tsx` | Parallax layer |
| `primitives/ScrollProgressIndicator.tsx` | Scroll progress bar |
| `primitives/SectionTransition.tsx` | Section transition |
| `primitives/StaggerReveal.tsx` | Stagger reveal children |

---

### 4.9 Scenes / Three.js — 7 files

| File | Vai trò |
|------|---------|
| `index.ts` | Re-export |
| `CameraController.tsx` | Three.js camera controller |
| `FloatingParticles.tsx` | Floating particles (background) |
| `KnowledgeGalaxy.tsx` | 3D galaxy scene |
| `OrbitalNodes.tsx` | Orbital node objects |
| `SceneComposer.tsx` | Scene composition manager |

---

### 4.10 Knowledge Graph Galaxy — 12 files

Đã liệt kê ở [mục 3.2 Knowledge Graph Sub-pages](#knowledge-graph-sub-pages-12-files-trong-knowledge-graph). Đây là tính năng "crown jewel" của DevOrbit — đồ thị tri thức dạng 3D galaxy với:

- **Planet** — mỗi planet là một course/node
- **OrbitRings** — quỹ đạo hiển thị semester
- **ConstellationSystem** — chòm sao (quan hệ môn học)
- **WormholeSystem** — wormhole kết nối liên semester
- **Starfield** — nền ngôi sao
- **TimeTravelSlider** — slider chuyển semester
- **GalaxyCamera** — camera điều khiển
- **Zustand store** — state management cho galaxy

---

### 4.11 Performance — 2 files

| File | Vai trò |
|------|---------|
| `index.ts` | Performance monitoring setup |
| `usePerformanceProfile.ts` | Hook đo lường performance |

---

### 4.12 Styles & Assets — 5 files

| File | Vai trò |
|------|---------|
| `index.css` | Global styles + Tailwind directives |
| `styles/transitions.css` | CSS transitions |
| `public/frames/frame01-01.png` | Photobooth frame image 1 |
| `public/frames/frame02-01.png` | Photobooth frame image 2 |
| `public/frames/frame03-01.png` | Photobooth frame image 3 |

---

### 4.13 Config Build — 10 files

| File | Vai trò |
|------|---------|
| `package.json` | Dependencies (React 19, Three.js, Framer Motion, GSAP, Zustand, TanStack Query, STOMP, Supabase, Tailwind, etc.) |
| `tsconfig.json` | TypeScript config |
| `vite.config.ts` | Vite bundler config |
| `tailwind.config.js` | Tailwind theme (orbit colors, fonts) |
| `postcss.config.js` | PostCSS with Tailwind |
| `eslint.config.js` | ESLint flat config |
| `index.html` | HTML entry point |
| `nginx.conf` | Nginx config cho production |
| `Dockerfile` | Docker build |
| `vercel.json` | Vercel deployment config |
| `.env` / `.env.example` | Environment variables |
| `.gitignore` | Git ignore |
| `README.md` | Project README |

---

### 4.14 Vercel Deployment — 2 files

| File | Chức năng |
|------|-----------|
| `.vercel/README.txt` | Vercel README |
| `.vercel/project.json` | Vercel project config |
| `vercel.json` | Build & deploy config (framework: vite) |

---

## 🔥 Chi tiết tính năng — Website (devorbit-web)

Dưới đây là mô tả chi tiết từng màn hình và chức năng trên website, luồng UI/UX, và component liên quan.

---

### 🏠 1. HomePage (`/`)

**Mô tả**: Trang chủ sinh viên — ấn tượng đầu tiên, giới thiệu toàn bộ hệ thống.

**Layout**:
| Khu vực | Component | Mô tả |
|---------|-----------|-------|
| Hero | `HeroStoryCarousel.tsx` | Carousel với story animation (danh sách tính năng nổi bật) |
| Courses | `CourseCard.tsx` grid | 6 môn học gần nhất, click → chi tiết |
| GitHub Repos | `RepoCard.tsx` grid | Repo nổi bật từ community |
| Knowledge Graph CTA | — | Banner dẫn đến galaxy graph |
| Community Preview | — | Tin nhắn community gần nhất |

**Hiệu ứng**:
- `ParticleNetwork.tsx` — particle animation background
- `FloatingParticles.tsx` (Three.js) — particles nền 3D
- `SmoothScrollProvider` (Lenis) — smooth scroll toàn trang
- `BlurReveal`, `FadeReveal`, `StaggerReveal` — animation khi scroll

---

### 📚 2. CourseListPage (`/courses`)

**Mô tả**: Danh sách tất cả môn học UIT, filter & search.

**Chức năng**:
| Feature | Component | Mô tả |
|---------|-----------|-------|
| Filter theo semester | `SemesterSelector.tsx` | Chọn học kỳ 1-9 |
| Filter theo chuyên ngành | — | SE / IT / IS / CS / DS |
| Search | — | Tìm theo tên/mã môn |
| Card grid | `CourseCard.tsx` | Hiển thị: mã môn, tên, tín chỉ, học kỳ, star rating |

**Data flow**: `useCourseList.ts` hook → GET `/api/courses` → render grid.

---

### 📖 3. CourseDetailPage (`/courses/:courseId`)

**Mô tả**: Chi tiết một môn học — toàn bộ thông tin từ syllabus đến đánh giá.

**Layout**:
| Tab | Nội dung |
|-----|----------|
| Tổng quan | Tên, mã, tín chỉ, mô tả, giảng viên, học kỳ |
| Đề cương (Syllabus) | Objectives, outcomes, assessment, sessions, tools, references |
| Tài liệu | Articles, tutorials, YouTube playlists |
| Quan hệ | `PrerequisitesView.tsx` — đồ thị prerequisite (DAG) |
| Đánh giá | `ReviewSection.tsx` — reviews từ sinh viên, star rating |
| Roadmap | AI-generated learning path cho môn này |

**Component đặc biệt**: `CourseKnowledgeGraph.tsx` — đồ thị 2D (react-force-graph-2d) hiển thị quan hệ môn học dạng force-directed.

---

### 💎 4. RepoDetailPage (`/repos/:repoId`)

**Mô tả**: Chi tiết GitHub repo — AI analysis, community engagement.

**Layout**:
| Khu vực | Component | Mô tả |
|---------|-----------|-------|
| Thông tin repo | — | Name, stars, language, topics, description, last pushed |
| AI Analysis | `RepoAiAnalysisSection.tsx` | AI-generated tóm tắt code, đánh giá chất lượng |
| File Tree | `FileTreeExplorer.tsx` | Cây thư mục repo (lazy load) |
| Reviews | `ReviewSection.tsx` | Đánh giá từ cộng đồng |
| Votes | `VoteButtons.tsx` | Upvote/downvote |

**AI Analysis chi tiết**: `repoAiAnalysis.ts` gọi API → phân tích ngôn ngữ, framework, quality metrics, gợi ý học tập.

---

### 🔭 5. Knowledge Graph — Galaxy Page (`/knowledge-graph`)

**Mô tả**: **Tính năng crown jewel** — đồ thị tri thức 3D dạng galaxy thiên hà.

**Công nghệ**: Three.js + `@react-three/fiber` + `@react-three/drei`

**Kiến trúc scene**:
| Layer | File | Mô tả |
|-------|------|-------|
| Canvas | `GalaxyCanvas.tsx` | Three.js renderer, WebGL context |
| Camera | `camera/GalaxyCamera.tsx` | OrbitControls, zoom, pan, rotate |
| Starfield | `effects/Starfield.tsx` | Nền hàng ngàn ngôi sao |
| Planets | `entities/Planet.tsx` | Mỗi planet = 1 môn học (sphere + glow + label) |
| Orbit Rings | `effects/OrbitRings.tsx` | Vòng quỹ đạo = semester |
| Constellation | `systems/ConstellationSystem.tsx` | Đường nối giữa planets có quan hệ prerequisite |
| Wormhole | `entities/Wormhole.tsx` + `systems/WormholeSystem.tsx` | Kết nối giữa các semester |
| Planet Trail | `effects/PlanetTrail.tsx` | Trail khi planet di chuyển |
| Orbital Group | `systems/OrbitalGroup.tsx` | Nhóm planets theo semester, quay quanh center |

**Interaction**:
| Hành động | Kết quả |
|-----------|---------|
| Click planet | Zoom vào → show course detail popup |
| Scroll | Zoom in/out galaxy |
| Drag | Xoay galaxy |
| TimeTravelSlider | Chuyển semester (planets bay vào/thoát quỹ đạo) |

**State management**: `useGalaxyStore.ts` (Zustand) — lưu selected planet, semester, camera target.

**API data**: `useKnowledgeGraph.ts` hook → GET `/api/ai/knowledge-graph` → nodes + edges JSON.

---

### 🎓 6. AI Tutor Page (`/ai-tutor`)

**Mô tả**: Chat với AI có context từ syllabus UIT và knowledge base.

**Layout**:
| Khu vực | Component | Mô tả |
|---------|-----------|-------|
| Chat panel | `ChatPanel.tsx` | Full-height chat UI |
| Message | `ChatMessage.tsx` | Bubble message (markdown render, code highlight) |
| Input | — | Textarea + send button, Enter to send |
| Floating widget | `AiChatWidget.tsx` | Widget nhỏ floating ở góc dưới (có thể mở rộng) |

**Tính năng đặc biệt**:
- Streaming response (SSE) — `useSubjectQa.ts` hook xử lý EventSource
- Chat history theo session — `ChatContext.tsx` context provider
- `useKeyboardShortcuts.ts` — Ctrl+Enter gửi, Escape đóng

---

### 📸 7. Photobooth Page (`/photobooth`)

**Mô tả**: Chụp ảnh + ghép frame UIT → tải xuống.

**Workflow**:
```
Chọn frame (FrameSelector) → Upload/chụp ảnh (PhotoUploadSection)
  → Ghép frame (PreviewCanvas + photoCompositor.ts)
    → Áp dụng filter (photoFilters.ts)
      → Tải xuống (DownloadSection)
```

**Components**:
| File | Chức năng |
|------|-----------|
| `FrameSelector.tsx` | Grid chọn frame (3 kiểu), preview thumbnail |
| `PhotoUploadSection.tsx` | Upload từ máy hoặc chụp webcam |
| `PreviewCanvas.tsx` | Canvas preview trực tiếp ảnh đã ghép frame |
| `DownloadSection.tsx` | Nút download (PNG/JPEG), share link |
| `photoCompositor.ts` | Logic ghép ảnh + frame bằng Canvas API |
| `photoFilters.ts` | Bộ lọc màu (vintage, grayscale, sepia, etc.) |

---

### 📊 8. GPA Calculator (`/gpa-calculator`)

**Mô tả**: Tính GPA theo đúng chương trình UIT.

**Input**:
- Chọn chuyên ngành (SE/IT/IS/CS/DS...)
- Nhập điểm từng môn (A+, A, B+, B, C+, C, D+, D, F)
- Chọn học kỳ

**Output**:
| Chỉ số | Mô tả |
|--------|-------|
| GPA tổng | Điểm trung bình tất cả môn |
| GPA compulsory | Điểm trung bình môn bắt buộc |
| GPA elective | Điểm trung bình môn tự chọn |
| GPA general | Điểm trung bình môn đại cương |
| Tín chỉ tích lũy | Tổng tín chỉ đã đạt |
| Xếp loại | Xuất sắc/Giỏi/Khá/Trung bình/Yếu |

**Data**: `curriculumData.ts` + `CareerOrientationData.ts` — chứa toàn bộ cấu trúc chương trình đào tạo.
**Logic**: `utils/gpa.ts` — parse grade → số, weighted average.

---

### 👥 9. Community Page (`/community`)

**Mô tả**: Chat real-time và social tương tác giữa sinh viên.

**Layout**:
| Khu vực | Component | Mô tả |
|---------|-----------|-------|
| Channel list | — | Danh sách kênh chat (public) |
| Chat area | `ChatPanel.tsx` | Real-time messages |
| Message | `ChatMessage.tsx` | Avatar + name + content + timestamp |
| Reviews | `ReviewSection.tsx` | Course reviews global feed |

**Real-time**: `useCommunitySocket.ts` (STOMP) — kết nối WebSocket `/ws/community`, auto-reconnect, nhận message push.

---

### 👤 10. Student Authentication (Login/Register/Profile)

**Mô tả**: Xác thực và quản lý tài khoản sinh viên.

**Pages**:
| Page | Route | Chức năng |
|------|-------|-----------|
| `StudentLoginPage.tsx` | `/student/login` | Form login + chuyển tab register |
| `StudentProfilePage.tsx` | `/student/profile` | View/edit profile, forgot/reset password |
| `StudentBookmarksPage.tsx` | `/student/bookmarks` | Danh sách bookmark |

**Luồng Register**:
```
Email + password → POST /api/student/register
  → OTP gửi email → nhập OTP → POST /api/student/verify-otp
    → Account active → redirect login
```

---

### 📂 11. Student Bookmarks (`/student/bookmarks`)

**Mô tả**: Sinh viên lưu repo để xem lại.

**Components**: `RepoCard.tsx` grid — các repo đã bookmark.
**API**: `useSearchHistory.ts` + bookmark hooks → CRUD.

---

### ⚙️ 12. Admin Dashboard (`/admin`)

**Mô tả**: Trung tâm điều khiển cho admin.

**Layout**: `AdminLayout.tsx` + `AdminSidebar.tsx` + `AdminTopbar.tsx`

**Widgets**:
| Widget | Component | Dữ liệu |
|--------|-----------|---------|
| Stats Row | `StatsRow.tsx` | Tổng courses, repos, students, reviews |
| Recent Activity | `RecentActivity.tsx` | Log hoạt động gần đây |
| Quick Actions | `QuickActions.tsx` | Nút tắt: scan repo, add course, send notification |
| Notification | `NotificationDropdown.tsx` | Dropdown thông báo real-time poll |

---

### 📋 13. Admin Courses (`/admin/courses`)

**Mô tả**: CRUD courses + resources.

**Components**: `CourseTable.tsx` (data table) + `CourseFormDialog.tsx` (form) + `ResourceDialog.tsx` (articles, tutorials, youtube).

**Chức năng**:
- Thêm/sửa/xoá course
- Quản lý syllabus (objectives, outcomes, assessment, sessions, tools, references)
- Thêm articles, tutorials, youtube playlists cho từng course
- Duyệt course reviews

---

### 🔄 14. Admin Repos (`/admin/repos`)

**Mô tả**: Pipeline quản lý GitHub repos từ scan → duyệt → public.

**Pipeline tabs** (`RepoPipelineTabs.tsx`):
| Tab | Component | Mô tả |
|-----|-----------|-------|
| Scan | `RepoScanTab.tsx` | Trigger scan, nhập topic/language |
| Candidates | `RepoCandidatesTab.tsx` | Danh sách chờ duyệt (PENDING) |
| Approved | `RepoApprovedTab.tsx` | Repo đã public, có thể edit/delete |

**Dialogs**: `RepoEditDialog.tsx` — sửa thông tin repo.

---

### 👨‍🎓 15. Admin Students (`/admin/students`)

**Mô tả**: Quản lý tài khoản sinh viên.

**Components**: `StudentTable.tsx` (data table) + `StudentDetailDialog.tsx` (chi tiết: email, major, semester, created date, bookmarks count).

---

### ⭐ 16. Admin Reviews (`/admin/reviews`)

**Mô tả**: Duyệt và quản lý đánh giá (course + repo).

**Components**: `CourseReviewTable.tsx` + `RepoReviewTable.tsx` — approve/reject reviews, filter by status.

---

### 💬 17. Admin Community (`/admin/community`)

**Mô tả**: Quản lý kênh chat community và messages.

**Components**: `ChannelList.tsx` + `MessageTable.tsx` — xoá message, tạo/xoá channel.

---

### 📞 18. Admin Chat Monitor (`/admin/chat`)

**Mô tả**: Giám sát các phiên chat AI của sinh viên.

**Components**: `ChatSessionTable.tsx` + `ChatMessageView.tsx` — xem lịch sử chat, tìm kiếm session.

---

### 🔗 19. Admin Relationships (`/admin/relationships`)

**Mô tả**: Quản lý đồ thị quan hệ môn học.

**Chức năng**: Thêm prerequisite/corequisite/equivalent, xem đồ thị DAG.

---

### 🖼️ 20. Admin Photobooth (`/admin/photobooth`)

**Mô tả**: Quản lý frame ảnh photobooth.

**Components**:
| File | Chức năng |
|------|-----------|
| `PhotoboothFrameGrid.tsx` | Grid hiển thị tất cả frames |
| `FrameUploadDialog.tsx` | Upload frame mới |
| `FrameSlotEditor.tsx` | Chỉnh sửa slot, active/inactive |

---

### 🏷️ 21. Admin Tech Stack (`/admin/techstack`)

**Mô tả**: Quản lý danh mục công nghệ.

**Chức năng**: Thêm/sửa/xoá tech stack, filter by category (language, framework, database, cloud, tool...).

---

### 🎨 UI/UX Design System

**Theme & Styling**:
| Công nghệ | Vai trò |
|-----------|---------|
| TailwindCSS | Utility-first styling |
| Custom orbit colors | `tailwind.config.js` — màu space theme (accent green, dark bg) |
| Fonts | Geist Sans, Geist Mono, Inter, Baloo 2 |
| Icons | `@phosphor-icons/react` + `lucide-react` |

**Animation System**:
| Library | File | Dùng cho |
|---------|------|----------|
| Framer Motion | `motion/primitives/*` | Scroll reveal, blur, fade, stagger |
| GSAP | — | Hero carousel, advanced timeline animations |
| Lenis | `SmoothScrollProvider.tsx` | Smooth scroll |
| Three.js | `scenes/*` | 3D galaxy, floating particles |

**Responsive**:
- Admin layout: sidebar collapse trên mobile
- Course/Repo grid: auto columns (1 → 2 → 3 → 4)
- Photobooth: full-width canvas trên mobile

**Performance**:
- `performance/index.ts` — monitoring setup
- `usePerformanceProfile.ts` — đo FPS, memory, re-renders
- React.lazy + Suspense — code splitting cho mọi page
- TanStack Query — caching, stale-while-revalidate
- Vite + LightningCSS + SWC — build tối ưu

---

## 4. File gốc monorepo root

Ở thư mục gốc `D:/temp/devorbit`:

| File | Mô tả |
|------|-------|
| `AGENTS.md` | Agent operating guide (harness instructions) |
| `README.md` | Project overview |
| `docs/HARNESS.md` | Human-agent operating model |
| `docs/FEATURE_INTAKE.md` | Feature intake / GSD methodology |
| `docs/ARCHITECTURE.md` | System architecture |
| `docs/TEST_MATRIX.md` | Test matrix & proof status |
| `docs/stories/` | Story packets & backlog |
| `docs/decisions/` | Architecture Decision Records |
| `docs/superpowers/specs/` | Spec directories |

---

## 7. Thống kê tổng

| Danh mục | devorbit-api | devorbit-web | Tổng |
|----------|:---:|:---:|:---:|
| **Source files (Java/TS/TSX)** | ~220 | ~180 | **~400** |
| **Entity classes** | 36 | — | 36 |
| **Repository interfaces** | 30 | — | 30 |
| **Service classes** | 44 | — | 44 |
| **Controller classes** | 30 | — | 30 |
| **DTO classes** | 44 | — | 44 |
| **Config classes** | 19 | — | 19 |
| **Test files** | 34 | 12 | **46** |
| **Components** | — | 60+ | **60+** |
| **Pages** | — | 23 | **23** |
| **Hooks** | — | 12 | **12** |
| **Database migrations** | 18 | — | 18 |

| **Config/build files** | 10 | 14 | **24** |
| **Total files (product)** | **~380** | **~280** | **~660** |

---

> 🪐 *DevOrbit — nơi vũ trụ tri thức UIT thu nhỏ trong code.*  
> File này được sinh ngày 2026-06-14, cập nhật thủ công khi có thay đổi lớn.
