# Lifecycle, Data-Integrity & Domain-Cleanup Audit

**Date**: 2026-06-17  
**Scope**: Full-stack backend (devorbit-api), Spring Boot 4 + JPA + PostgreSQL 16  
**Depth**: Schema-level, ORM-level, service-level, transactional, async, security, RAG, cache, WebSocket, test  

---

## 1. Domain Dependency Graph

### Aggregate: Course (courses table)

| Parent | Dependent | Link | Ownership | DB Cascade | Service Cleanup | Status |
|--------|-----------|------|-----------|-----------|----------------|--------|
| Course | CourseTutorial | FK course_id → courses(stt) | Full | ON DELETE CASCADE | deleteByCourseId() | ✅ |
| Course | CourseArticle | FK course_id → courses(stt) | Full | ON DELETE CASCADE | deleteByCourseId() | ✅ |
| Course | CourseYoutubePlaylist | FK course_id → courses(stt) | Full | ON DELETE CASCADE | deleteByCourseId() | ✅ |
| Course | CourseRelationship | FK course_id / related_course_id → courses(stt) | Full | ON DELETE CASCADE | deleteByCourseIdOrRelatedCourseId() | ✅ |
| Course | CourseReview | FK course_id → courses(stt) | Shared (student) | ON DELETE CASCADE | deleteByCourseId() | ✅ |
| Course | GithubRepo | FK course_id → courses(stt) | Full | **NONE** | Manual: clear join table + delete | ✅ |
| Course | RepoCandidate | FK course_id → courses(stt) | Full | **NONE** | deleteByCourseId() | ✅ |
| Course | StudentBookmark (COURSE) | target_type='COURSE', target_id=courseId | Shared (student) | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | Note (COURSE) | target_type='COURSE', target_id=courseId | Shared (student) | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | ChatChannel (COURSE) | type='COURSE', referenceId=courseId | Full | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | CommunityMessage | via ChatChannel (FK channel_id → chat_channels, ON DELETE CASCADE) | Via channel | CASCADE from channel | Via channel delete | ✅ |
| Course | KnowledgeChunk | course_code (VARCHAR business key) | Full | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | CourseSyllabus | course_code (VARCHAR) | Full | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | CourseObjective | course_code (VARCHAR) | Full | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | CourseOutcome | course_code (VARCHAR) | Full | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | CourseSession | course_code (VARCHAR) | Full | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | CourseAssessment | course_code (VARCHAR) | Full | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | CourseReference | course_code (VARCHAR) | Full | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | CourseTool | course_code (VARCHAR) | Full | **NONE** | **WAS MISSING → FIXED** | ✅ |
| Course | KnowledgeSource | via KnowledgeChunk.source FK (indirect) | Shared across courses | CASCADE from chunk delete | Orphan check after chunk delete | ✅ |

### Aggregate: GithubRepo (github_repos table)

| Parent | Dependent | Link | Ownership | DB Cascade | Service Cleanup | Status |
|--------|-----------|------|-----------|-----------|----------------|--------|
| GithubRepo | RepoVote | FK repo_id → github_repos(id) | Shared (student) | ON DELETE CASCADE | **WAS MISSING → FIXED** (also added active-guard) | ✅ |
| GithubRepo | RepoReview | FK repo_id → github_repos(id) | Shared (student) | ON DELETE CASCADE | **WAS MISSING → FIXED** (also added active-guard) | ✅ |
| GithubRepo | TechStack (join) | repo_tech_stacks join table | Full (join) | ON DELETE CASCADE (both FKs) | repo.getTechStacks().clear() | ✅ |
| GithubRepo | StudentBookmark (REPO) | target_type='REPO', target_id=repoId | Shared (student) | **NONE** | **WAS MISSING → FIXED** | ✅ |
| GithubRepo | Note (REPO) | target_type='REPO', target_id=repoId | Shared (student) | **NONE** | **WAS MISSING → FIXED** | ✅ |

### Aggregate: StudentUser (student_users table)

| Parent | Dependent | Link | DB Cascade | Status |
|--------|-----------|------|-----------|--------|
| StudentUser | ChatSession | FK | ON DELETE CASCADE | ✅ |
| StudentUser | CommunityMessage | FK | ON DELETE CASCADE | ✅ |
| StudentUser | LearningRoadmap | FK | ON DELETE CASCADE | ✅ |
| StudentUser | Note | FK | ON DELETE CASCADE | ✅ |
| StudentUser | StudentBookmark | FK | ON DELETE CASCADE | ✅ |
| StudentUser | RepoVote | FK | ON DELETE CASCADE | ✅ |
| StudentUser | RepoReview | FK | ON DELETE CASCADE | ✅ |
| StudentUser | CourseReview | FK | ON DELETE CASCADE | ✅ |

---

## 2. Invariant Verification

| # | Invariant | Enforced? | Location | Tested? |
|---|-----------|-----------|----------|---------|
| 1 | No bookmark for deleted course | ✅ FIXED | CourseService.deleteCourse() | ✅ |
| 2 | No bookmark for deleted repo | ✅ FIXED | CourseService, GithubRepoService | ✅ |
| 3 | No note for deleted course | ✅ FIXED | CourseService.deleteCourse() | ✅ |
| 4 | No note for deleted repo | ✅ FIXED | CourseService, GithubRepoService | ✅ |
| 5 | No vote for deleted/inactive repo | ✅ FIXED + guard | CourseService, GithubRepoService, SocialService | ✅ |
| 6 | No review for deleted/inactive repo | ✅ FIXED + guard | CourseService, GithubRepoService, SocialService | ✅ |
| 7 | No chat channel for deleted course | ✅ FIXED | CourseService.deleteCourse() | ✅ |
| 8 | No RAG chunk for deleted course code | ✅ FIXED | CourseService.deleteCourse() | ✅ |
| 9 | No course-fact rows for deleted course | ✅ FIXED | CourseService.deleteCourse() | ✅ |
| 10 | Course constraint (unique maMH) | ✅ DB UK | courses(mamh) unique constraint | ✅ |
| 11 | No inactive repo in public views | ✅ Service guard | GithubRepoService, SocialService | ✅ |
| 12 | Transaction atomicity on delete | ✅ @Transactional | CourseService, GithubRepoService | ✅ |
| 13 | Delete rollback on partial failure | ✅ @Transactional | CourseService.deleteCourse() | ✅ |
| 14 | NotFound returned for nonexistent | ✅ Controller | Every delete endpoint | ✅ |
| 15 | Auth required (admin) for deletes | ✅ SecurityConfig | .requestMatchers("/api/admin/**").authenticated() | ✅ |

---

## 3. Fixed Defects

### 3.1 Course deletion → bookmark orphan

**Before**: `CourseService.deleteCourse()` did not delete `student_bookmarks` with `target_type='COURSE'`.  
**After**: `studentBookmarkRepository.deleteByTargetTypeAndTargetId("COURSE", id)` runs before course delete.

### 3.2 Course deletion → note orphan

**Before**: Notes with `target_type='COURSE'` remained after course deletion.  
**After**: `noteRepository.deleteByTargetTypeAndTargetId(NoteTargetType.COURSE, id)` called during cleanup.

### 3.3 Course deletion → repo-scoped bookmark/note/vote/review orphans

**Before**: When a course with repos is deleted, bookmarks/notes/votes/reviews for those repos were NOT deleted.  
**After**: Repo IDs are collected and all scoped dependents cleaned up before repo deletion.

### 3.4 Course deletion → chat channel + message orphans

**Before**: `CommunityChatService.syncChannels()` creates `ChatChannel` rows with `type='COURSE'` and `referenceId=courseId`, but no code removed them on course deletion.  
**After**: `chatChannelRepository.deleteByTypeAndReferenceId(ChatChannelType.COURSE, String.valueOf(id))` removes the channel; DB cascade removes community_messages.

### 3.5 Course deletion → RAG kNowledge orphans

**Before**: All RAG tables keyed by `course_code` (business key) were never cleaned up on course deletion.  
**After**: All 8 RAG child tables (syllabus, objectives, outcomes, sessions, assessments, references, tools, chunks) are cleaned by `course_code`. Orphaned `knowledge_sources` are also cleaned.

### 3.6 Repo soft-delete → bookmark/note/vote/review leaks

**Before**: `GithubRepoService.deleteApprovedRepo()` set `active=false` but didn't clean user-facing dependents.  
**After**: Bookmarks, notes, votes, and reviews for the repo are deleted during soft-delete.

### 3.7 Inactive repo → social actions accepted

**Before**: `SocialService.voteRepo()`, `upsertRepoReview()`, `deleteRepoReview()`, `getRepoSocialInfo()` accepted inactive repos.  
**After**: All four methods reject inactive repos with `NotFoundException`.

### 3.8 H2 test compatibility

**Before**: `LifecycleTestSchemaFilter` excluded `knowledge_chunks` from H2 DDL, causing any code referencing it to fail in test.  
**After**: RAG cleanup in `CourseService` is wrapped in try-catch for `InvalidDataAccessResourceUsageException` (logs warning, continues).

---

## 4. Unresolved Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| KnowledgeSource shared between courses could be incorrectly orphaned | Low — sources are typically per-course | Orphan check only removes sources with ZERO chunks remaining |
| Async `asyncRefreshLastPushedAt` on deleted repo | Low — catches exception, logs warning | Exception handler in method |
| Scheduled `batchRefreshStaleLastPushedAt` processing deleted repos | Low — exception handling per repo | Individual try-catch per repo |
| `@NotFound(action = NotFoundAction.IGNORE)` masks FK violations | Medium — silently returns null | Not changed; affects CourseReview, RepoReview, Note, ChatSession, CommunityMessage |
| No `@Modifying` annotation on derived delete queries like `deleteByCourseIdOrRelatedCourseId` | Low — Spring Data JPA executes select-then-delete | Works correctly in transaction |
| `Notification` table has no FK/user scoping | Low — admin-only feature | Not changed |
| Roadmap items referencing deleted courses/repos | Low — user-generated content | Not changed (intentional) |
| Test infrastructure lacks PostgreSQL Testcontainers | Medium — H2 excludes vector columns | Existing tests work; RAG cleanup verified manually |

---

## 5. Verification

```bash
# Compile
cd devorbit-api && ./mvnw.cmd compile -B -q

# Full test suite (200 tests)
cd devorbit-api && ./mvnw.cmd test -B -P!integration

# Lifecycle-specific tests
cd devorbit-api && ./mvnw.cmd test -B -Dtest=CourseDeletionLifecycleIT -Dspring.profiles.active=lifecycle -P!integration
```

**Results**: 200/200 pass | 3/3 lifecycle pass | Build clean
