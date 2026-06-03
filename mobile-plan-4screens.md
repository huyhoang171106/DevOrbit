# Mobile Plan — 4 Màn hình: Môn học, Khám phá, Kế hoạch, Cá nhân

> Based on backend API at `devorbit-api/` and current mobile codebase at `devorbit-mobile/`.
> Updated: 2026-05-29 (post feature/bac merge)
> API: `http://104.214.179.110:8080`

---

## Tổng quan API Backend

### Public Endpoints (không cần auth)

| Method | Endpoint | Response | Mobile đã gọi? |
|--------|----------|----------|----------------|
| GET | `/api/courses?q=&subjectType=&semester=&managementUnit=` | `List<CourseSummaryResponse>` | ✅ Đã có — query params wired |
| GET | `/api/courses/{id}` | `CourseDetailResponse` | ✅ |
| GET | `/api/courses/graph` | `KnowledgeGraphResponse` | ✅ |
| GET | `/api/courses/{courseId}/repos?techStack=` | `List<RepoSummaryResponse>` | ✅ |
| GET | `/api/courses/relationships` | `List<CourseRelationshipResponse>` | ✅ |
| GET | `/api/courses/relationships/course/{courseId}` | `List<CourseRelationshipResponse>` | ❌ |
| GET | `/api/repos/{repoId}` | `RepoSummaryResponse` | ✅ Đã có typed endpoint |
| GET | `/api/discovery/recent-repos` | `List<RepoSummaryResponse>` | ✅ |
| GET | `/api/discovery/repos?q=` | `List<RepoSummaryResponse>` | ✅ Đã có — search repos |
| GET | `/api/discovery/top-stacks` | `List<String>` | ✅ |
| GET | `/api/tech-stacks` | `List<TechStackResponse>` | ✅ |
| POST | `/api/ai/subject-qa/query` | `SubjectQaResponse` | ✅ Đã có endpoint + ViewModel |
| POST | `/api/ai/knowledge-graph/query` | `AiQueryResponse` | ✅ |
| POST | `/api/ai/generate-roadmap` | `RoadmapRecommendationResponse` | ✅ Đã có endpoint + ViewModel |
| GET | `/api/ai/repo/{repoId}/summary` | `AiResponse` | ✅ |
| GET | `/api/ai/repo/{repoId}/advice` | `AiResponse` | ✅ |

### Student Endpoints (cần JWT auth)

| Method | Endpoint | Response | Mobile đã gọi? |
|--------|----------|----------|----------------|
| POST | `/api/student/login` | `StudentAuthResponse{token, id, studentCode, fullName, email}` | ✅ |
| POST | `/api/student/register` | `StudentAuthResponse` | ✅ |
| GET | `/api/student/me` | `StudentProfileResponse{id, studentCode, fullName, email}` | ✅ |
| GET | `/api/student/bookmarks` | `List<StudentBookmarkResponse>` | ✅ Đã có — API |
| POST | `/api/student/bookmarks` | `StudentBookmarkResponse` | ✅ Đã có — API |
| DELETE | `/api/student/bookmarks/{id}` | void | ✅ Đã có — API |

---

## Hiện trạng Code Mobile (2026-05-29)

### ApiService.kt — Endpoints đã có

```kotlin
GET  /api/courses                    → List<CourseSummary>
GET  /api/courses/{id}/repos         → List<RepoSummary>
GET  /api/courses/{id}/tutorials     → List<CourseTutorial>
GET  /api/courses/{id}/videos        → List<CourseYoutubePlaylist>
GET  /api/courses/{id}/articles      → List<CourseArticle>
GET  /api/courses/relationships      → List<CourseRelationshipResponse>
GET  /api/courses/graph              → GraphResponse
POST /api/student/login              → Map<String, Any>
POST /api/student/register           → Map<String, Any>
GET  /api/student/me                 → Map<String, Any>
GET  /api/tech-stacks               → List<Map<String, String>>
GET  /api/discovery/recent-repos    → List<Map<String, Any>>
GET  /api/discovery/top-stacks      → List<Map<String, Any>>
GET  /api/ai/repo/{repoId}/summary  → Map<String, Any>
GET  /api/ai/repo/{repoId}/advice   → Map<String, Any>
POST /api/ai/knowledge-graph/query  → Map<String, Any>
```

### ApiService.kt — Endpoints CHƯA có (cần thêm)

```kotlin
GET  /api/courses?q=&subjectType=&semester=&managementUnit=  // ✅ đã có search/filter
POST /api/ai/subject-qa/query                                 // ✅ đã có AI Q&A
POST /api/ai/generate-roadmap                                 // ✅ đã có AI roadmap
GET  /api/discovery/repos?q=                                  // ✅ đã có search repos
GET  /api/courses/relationships/course/{courseId}             // ✅ đã có endpoint client
POST /api/student/bookmarks                                   // ✅ đã có add bookmark
GET  /api/student/bookmarks                                   // ✅ đã có list bookmarks
DELETE /api/student/bookmarks/{id}                            // ✅ đã có remove bookmark
```

### BookmarkRepository — ĐÃ CÓ, gọi API

```kotlin
// BookmarkRepositoryImpl.kt — in-memory list, không persist
private val bookmarks = mutableListOf<Bookmark>()
// Không có API call
```

### Repositories hiện có

| Repository | File | Status |
|-----------|------|--------|
| `AcademicRepository` | `data/repository/AcademicRepository.kt` | ✅ Working — courses, graph, detail |
| `AuthRepository` | `data/repository/AuthRepositoryImpl.kt` | ✅ Working — login, register, profile |
| `BookmarkRepository` | `data/repository/BookmarkRepositoryImpl.kt` | ✅ Đã có — gọi Student Bookmark API |
| `DiscoveryRepository` | `data/repository/DiscoveryRepositoryImpl.kt` | ✅ Working — recent repos, top stacks |
| `AiRepository` | `data/repository/AiRepositoryImpl.kt` | ✅ Working — summary, advice, KG query |
| `ResourceRepository` | `data/repository/ResourceRepositoryImpl.kt` | ✅ Working — tutorials, videos, articles |

### ViewModels hiện có

| ViewModel | File | State |
|-----------|------|-------|
| `CourseViewModel` | `ui/viewmodel/CourseViewModel.kt` | ✅ Working — load courses, graph, detail, repos |
| `ExploreViewModel` | `ui/viewmodel/ExploreViewModel.kt` | ✅ Working — load recent repos, top stacks |
| `ProfileViewModel` | `ui/viewmodel/ProfileViewModel.kt` | ✅ Working — profile, bookmarks (mock), dark mode |
| `StudyPlanViewModel` | ✅ Đã có | Generate AI roadmap, toggle progress in memory |
| `SubjectQaViewModel` | ✅ Đã có | Chat session state + Subject Q&A API |

---

## PHẦN 1: MÔN HỌC (Courses Screen)

### Đã có

- `CourseHubScreen` — Danh sách + Toggle LIST/GALAXY view
- `CourseDetailScreen` — Chi tiết môn: repos, tutorials, videos, articles
- `RepoDetailScreen` — Chi tiết repo + AI summary/advice
- `CourseViewModel` — Load courses, graph, detail, repos, navigation state

### Gap Analysis

| # | Gap | Backend API | Mức độ |
|---|-----|-------------|--------|
| C1 | **Search/Filter môn học** — ✅ Đã có search bar + subject chips, gọi query params backend | `GET /api/courses?q=&subjectType=&semester=&managementUnit=` | DONE |
| C2 | **Filter repos theo tech stack** — ✅ Đã có chip filter trong CourseDetailScreen | `GET /api/courses/{id}/repos?techStack=X` | DONE |
| C3 | **Course type filter chips** — `loaiMonHoc` (DAI_CUONG/CHUYEN_NGANH/CO_SO) chưa filter được | Response已有 | MEDIUM |
| C4 | **Prerequisite info** — `prerequisiteMH`, `previousMH` chưa hiển thị | Response已有 | MEDIUM |
| C5 | **AI Subject Q&A** — ✅ Đã có `SubjectQaViewModel` + `SubjectQaScreen` cơ bản | `POST /api/ai/subject-qa/query` | DONE |
| C6 | **Bookmark môn học** — ✅ Đã có nút bookmark từ CourseDetailScreen, dùng Student Bookmark API | `POST /api/student/bookmarks` | DONE |
| C7 | **Learning objectives / Grading criteria** — Chưa hiển thị | Response已有 | LOW |
| C8 | **Course relationship per-course** — Chưa dùng endpoint `/courses/relationships/course/{id}` | `GET /api/courses/relationships/course/{courseId}` | LOW |

### Chi tiết Gap

---

#### C1: Search/Filter môn học

**Current:** Hiển thị toàn bộ courses, không có search bar.

**Backend:** `GET /api/courses?q=keyword&subjectType=DAI_CUONG&semester=3&managementUnit=CNPM`

**Plan:**
- File: `CourseHubScreen.kt`
- Thêm `TextField` search bar trên header
- Thêm chip filter: "Tất cả" | "Đại cương" | "Chuyên ngành" | "Cơ sở"
- Gọi `getCourses(q=..., subjectType=...)` thay vì client-side filter
- Hoặc hybrid:保持 client filter + thêm server search khi cần real-time

**Verify:** Gõ search → list filter. Click chip → filter theo loại.

---

#### C2: Filter repos theo tech stack

**Current:** Hiển thị tech stack chips nhưng không filter được.

**Backend:** `GET /api/courses/{courseId}/repos?techStack=React`

**Plan:**
- File: `CourseDetailScreen.kt` — thêm `selectedTechStack` state
- Khi click tech stack chip → gọi lại API với `?techStack=X`
- Thêm chip "Tất cả" để clear filter

**Verify:** Click tech stack → repos filter. Click "Tất cả" → hiện tất cả.

---

#### C5: AI Subject Q&A

**Current:** Chưa có.

**Backend:**
```java
POST /api/ai/subject-qa/query
Body: { "message": "...", "sessionId": "uuid" }
Response: { "answer": "...", "sessionId": "uuid", "sources": [...] }
```

**Plan:**
1. `ApiService.kt` — thêm endpoint
2. Tạo `SubjectQaRequest.kt`, `SubjectQaResponse.kt` trong dto/
3. Tạo `SubjectQaViewModel.kt` — manages chat session, messages list
4. Tạo `SubjectQaScreen.kt` — Chat UI (message list + input field)
5. Navigate từ CourseDetailScreen → floating button "Hỏi AI"

**Verify:** Gửi câu hỏi → nhận answer. History hiển thị correctly.

---

#### C6: Bookmark môn học

**Current:** `BookmarkRepository` là mock (in-memory), không gọi API.

**Backend:**
```java
POST /api/student/bookmarks  (cần JWT)
Body: { "targetType": "COURSE", "targetId": 123 }
Response: { "id": 1, "targetType": "COURSE", "targetId": 123, ... }
```

**Plan:**
1. `BookmarkRepositoryImpl.kt` — sửa để gọi API thay vì mock
   ```kotlin
   @Singleton
   class BookmarkRepositoryImpl @Inject constructor(
       private val apiService: ApiService,
       private val authInterceptor: AuthInterceptor
   ) : BookmarkRepository {
       override suspend fun addBookmark(bookmark: Bookmark) {
           apiService.addBookmark(mapOf(
               "targetType" to bookmark.targetType,
               "targetId" to bookmark.targetId.toString()
           ))
       }
       // ...
   }
   ```
2. `ApiService.kt` — thêm bookmarks endpoints
3. `CourseDetailScreen.kt` — thêm bookmark icon button (top-right)
4. `CourseViewModel.kt` — thêm `fun toggleBookmark(courseId: Long)`

**Verify:** Click heart → toggle. Vào Profile → thấy bookmark mới.

---

## PHẦN 2: KHÁM PHÁ (Explore Screen)

### Đã có

- `ExploreScreen` — Hiển thị top tech stacks, recent repos
- `ExploreViewModel` — Load data từ `DiscoveryRepository`
- `DiscoveryRepositoryImpl` — Gọi recent-repos, top-stacks, tech-stacks

### Gap Analysis

| # | Gap | Backend API | Mức độ |
|---|-----|-------------|--------|
| E1 | **Search repos** — ✅ Đã có search bar gọi `GET /api/discovery/repos?q=` | `GET /api/discovery/repos?q=` | DONE |
| E2 | **Repo detail navigation** — ✅ Click repo trong Explore mở `RepoDetailScreen` | `GET /api/repos/{repoId}` | DONE |
| E3 | **Tech stack filter** — ✅ Click tech stack filter repo, click lại clear | Client-side | DONE |
| E4 | **Load more repos** — Chỉ hiển thị 10 repos gần nhất | Backend chỉ có `findTop10` | MEDIUM |
| E5 | **Repo tech stacks display** — ✅ `RepoSummaryResponse.techStacks` parse bằng DTO typed | Cần parse proper | DONE |
| E6 | **AI Summary trong explore repos** — Không có tóm tắt AI | `GET /api/ai/repo/{repoId}/summary` | MEDIUM |
| E7 | **Empty state / error state** — ✅ Đã có loading, error retry, empty state | N/A | DONE |

### Chi tiết Gap

---

#### E1: Search repos

**Current:** LazyColumn hiển thị repos, không có search.

**Backend:** `GET /api/discovery/repos?q=keyword`

**Plan:**
1. `ApiService.kt` — thêm endpoint
   ```kotlin
   @GET("/api/discovery/repos")
   suspend fun searchDiscoveryRepos(@Query("q") query: String): List<Map<String, Any>>
   ```
2. `DiscoveryRepository.kt` — thêm `suspend fun searchRepos(query: String): List<RecentRepo>`
3. `ExploreScreen.kt` — thêm `TextField` search bar
4. Filter client-side hoặc gọi API mới

**Verify:** Gõ search → repos filter real-time.

---

#### E2: Repo detail navigation

**Current:** Click repo → không làm gì.

**Plan:**
1. `ExploreScreen.kt` — thêm `onRepoClick: (Long) -> Unit` parameter
2. `ExploreViewModel.kt` — thêm `selectedRepoId: StateFlow<Long?>` + `fun openRepo(id: Long)`
3. Tạo `ExploreRepoDetailScreen.kt` — Hiển thị repo detail + AI summary
4. Hoặc reuse `RepoDetailScreen` từ CourseHubScreen

**Verify:** Click repo → mở detail screen. Back → quay lại explore.

---

#### E3: Tech stack filter

**Current:** Hiển thị tech stacks nhưng không filter được.

**Plan:**
1. `ExploreScreen.kt` — thêm `selectedTechStack` state
2. Khi click tech stack chip → filter `recentRepos` theo tech stack
3. Hiển thị "Tất cả" chip để clear

**Verify:** Click tech stack → repos filter. Click "Tất cả" → hiện tất cả.

---

#### E5: Parse TechStacks properly

**Current:** `getRecentDiscoveryRepos()` trả về `List<Map<String, Any>>`, parse sai.

**Plan:**
1. `ApiService.kt` — thay `Map<String, Any>` bằng proper DTO
   ```kotlin
   @GET("/api/discovery/recent-repos")
   suspend fun getRecentDiscoveryRepos(): List<RepoSummaryResponse>
   ```
2. `DiscoveryRepositoryImpl.kt` — map từ `RepoSummaryResponse` → `RecentRepo`
3. Field `techStacks` trong `RepoSummaryResponse` là `List<TechStackResponse>`, cần parse

**Verify:** Tech stacks hiển thị đúng trong repo cards.

---

## PHẦN 3: KẾ HOẠH (Plan Screen)

### Đã có

- `StudyPlannerScreen` — UI hiển thị plan, phases, items
- `StudyPlannerEngine` — Local engine generate plan từ tasks
- `StudyPlan`, `StudyPhase`, `StudyItem` domain models
- `MainScreen.PlanTabView()` — Wrap StudyPlannerScreen, TODO callbacks

### Gap Analysis

| # | Gap | Backend API | Mức độ |
|---|-----|-------------|--------|
| P1 | **Plan chưa connect** — ✅ `PlanTabView` đã dùng `StudyPlanViewModel` | Không có backend endpoint lưu plan | DONE |
| P2 | **AI Generate Roadmap** — ✅ Mobile gọi backend AI roadmap endpoint | `POST /api/ai/generate-roadmap` | DONE |
| P3 | **Persist study plan** — Plan chỉ trong memory, restart mất | Dùng本地DataStore/Room | HIGH |
| P4 | **Input configuration** — Không cho user set giờ học/ngày, deadline | N/A | MEDIUM |
| P5 | **Progress tracking** — ✅ Toggle item completed trong ViewModel, chưa persist | N/A | PARTIAL |
| P6 | **Breakdown task** — Nút "Phân tích" chưa hoạt động | N/A | LOW |

### Chi tiết Gap

---

#### P1 + P2: AI Roadmap Generation

**Current:** `PlanTabView` truyền `studyPlan = null`.

**Backend:**
```java
POST /api/ai/generate-roadmap
Body: { "learningGoals": "...", "careerPath": "..." }
Response: { "phases": [...], "summary": "..." }
```

**Plan:**
1. `ApiService.kt` — thêm endpoint
   ```kotlin
   @POST("/api/ai/generate-roadmap")
   suspend fun generateRoadmap(@Body body: RoadmapGenerationRequest): RoadmapRecommendationResponse
   ```
2. Tạo `RoadmapRequest.kt`, `RoadmapResponse.kt` trong dto/
3. Tạo `StudyPlanRepository.kt` — interface để generate + load plan
4. Tạo `StudyPlanViewModel.kt` — manages plan state, generation, persistence
5. Update `StudyPlannerScreen.kt` — thêm input dialog:
   - TextField: "Mục tiêu học tập"
   - TextField: "Lĩnh vực nghề nghiệp"
   - Button "Tạo lộ trình"
6. Update `MainScreen.kt` — connect `PlanTabView` với `StudyPlanViewModel`

**Verify:** Nhập mục tiêu → click tạo → plan hiển thị với phases.

---

#### P3: Persist Study Plan

**Current:** Plan chỉ trong memory.

**Plan:**
1. Tạo `PlanDataStore.kt` trong data/datastore/
   ```kotlin
   class PlanDataStore @Inject constructor(
       private val dataStore: DataStore<Preferences>
   ) {
       suspend fun savePlan(plan: StudyPlan) // serialize to JSON
       fun loadPlan(): Flow<StudyPlan?>       // deserialize from JSON
       suspend fun clearPlan()
   }
   ```
2. `StudyPlanViewModel.kt` — load plan on init, save on change

**Verify:** Tạo plan → restart app → plan còn.

---

#### P4: Input Configuration

**Plan:**
- Thêm vào input dialog:
  - Slider: "Giờ học/ngày" (1-8h, default 2)
  - DatePicker: Deadline (optional)
- These feed into `StudyPlannerEngine.generatePlan()`

**Verify:** Set giờ học → plan phân bổ đúng.

---

#### P5: Progress Tracking

**Plan:**
- `StudyPlanViewModel.kt` — `fun toggleItem(itemId: String)`
- Update `_state.value.plan` → save to DataStore

**Verify:** Toggle item → progress bar update → persist sau restart.

---

## PHẦN 4: CÁ NHÂN (Profile Screen)

### Đã có

- `ProfileScreen` — Hiển thị student info, bookmarks, dark mode, logout
- `ProfileViewModel` — Load student info từ DataStore, bookmarks (mock)
- `AuthRepository` — Login, register, getProfile, logout
- `BookmarkRepository` — getAllBookmarks, addBookmark, removeBookmark (mock)
- `AuthScreen` — Login/Register UI

### Gap Analysis

| # | Gap | Backend API | Mức độ |
|---|-----|-------------|--------|
| R1 | **Edit profile** — Không thể sửa tên/email | Không có backend endpoint | LOW |
| R2 | **Bookmark detail navigation** — Click bookmark không mở detail | Client-side navigation | MEDIUM |
| R3 | **Bookmark subtitle** — `StudentBookmarkResponse.subtitle` chưa hiển thị | Response已有 | LOW |
| R4 | **App version / About** — Không có mục About | N/A | LOW |
| R5 | **Statistics** — Không hiển thị thống kê | N/A (tính từ local data) | MEDIUM |
| R6 | **Notification settings** — Không có settings thông báo | N/A | LOW |
| R7 | **Force refresh profile** — Profile từ `/api/student/me` không được gọi lại | `GET /api/student/me` | MEDIUM |
| R8 | **Error handling auth** — Login/Register không hiển thị lỗi chi tiết | Response có thể chứa error | MEDIUM |
| R9 | **BookmarkRepository is MOCK** — Currently in-memory list, không gọi API | `GET/POST/DELETE /api/student/bookmarks` | **CRITICAL** |
| R10 | **Login bug on backend** — Register works but login fails | Backend password hash issue | HIGH |

### Chi tiết Gap

---

#### R2: Bookmark detail navigation

**Current:** Click bookmark → không làm gì.

**Plan:**
1. `ProfileScreen.kt` — thêm `onBookmarkClick: (Bookmark) -> Unit` callback
2. `ProfileViewModel.kt` — thêm `fun openBookmark(bookmark: Bookmark)`
3. Logic: check `bookmark.targetType` → navigate đến CourseDetailScreen hoặc RepoDetailScreen

**Verify:** Click bookmark → mở đúng detail screen.

---

#### R5: Statistics

**Plan:**
- `ProfileScreen.kt` — thêm section "Thống kê"
  ```kotlin
  item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
          StatCard(label = "Môn đã lưu", value = "${state.bookmarks.size}")
          StatCard(label = "Học kỳ hiện tại", value = "HK${currentSemester}")
      }
  }
  ```

**Verify:** Hiển thị số bookmark, học kỳ hiện tại.

---

#### R7: Force refresh profile

**Plan:**
- `ProfileViewModel.kt` — thêm `fun refreshProfile()`
- Gọi khi pull-to-refresh hoặc khi app foreground

**Verify:** Thay đổi tên trên backend → pull refresh → tên cập nhật.

---

## Implementation Priority

### Phase 1 — Core (1-2 ngày)
1. **C1** Search/filter môn học — ✅ Đã có
2. **E1** Search repos trong explore — ✅ Đã có
3. **E5** Parse tech stacks properly — ✅ Đã có
4. **R9** Fix BookmarkRepository — ✅ Đã có
5. **P1+P2** AI Roadmap generation — ✅ Đã có
6. **P3** Persist study plan — `PlanDataStore.kt`

### Phase 2 — Enhancement (1-2 ngày)
7. **C2** Filter repos theo tech stack — ✅ Đã có
8. **C6** Bookmark môn học — ✅ Đã có
9. **E2** Repo detail navigation từ explore — ✅ Đã có
10. **E3** Tech stack filter trong explore — ✅ Đã có
11. **P4** Input configuration cho plan — `StudyPlannerScreen.kt`
12. **P5** Progress tracking — ⚠️ Đã có in-memory, chưa persist

### Phase 3 — Polish (1 ngày)
13. **C5** AI Subject Q&A — ✅ Đã có basic screen + ViewModel
14. **R2** Bookmark detail navigation — `ProfileScreen.kt`
15. **R5** Statistics trong profile — `ProfileScreen.kt`
16. **R7** Force refresh profile — `ProfileViewModel.kt`
17. **E7** Loading/error states — all screens

---

## Files cần tạo mới

| File | Purpose |
|------|---------|
| `data/remote/dto/RoadmapRequest.kt` | AI roadmap request DTO |
| `data/remote/dto/RoadmapResponse.kt` | AI roadmap response DTO |
| `data/remote/dto/SubjectQaRequest.kt` | Subject Q&A request DTO |
| `data/remote/dto/SubjectQaResponse.kt` | Subject Q&A response DTO |
| `data/datastore/PlanDataStore.kt` | Study plan persistence |
| `domain/repository/StudyPlanRepository.kt` | Plan repository interface |
| `data/repository/StudyPlanRepositoryImpl.kt` | Plan repository impl |
| `ui/viewmodel/StudyPlanViewModel.kt` | Plan screen ViewModel |
| `ui/viewmodel/SubjectQaViewModel.kt` | Subject Q&A ViewModel |
| `ui/screen/plan/SubjectQaScreen.kt` | AI chat UI |
| `ui/screen/explore/ExploreRepoDetailScreen.kt` | Explore repo detail |

## Files cần sửa

| File | Changes |
|------|---------|
| `network/ApiService.kt` | Thêm 7 endpoints: search courses, search repos, bookmarks, subject-qa, roadmap |
| `data/repository/BookmarkRepositoryImpl.kt` | Sửa mock → gọi API thực |
| `ui/screen/courses/CourseHubScreen.kt` | Search bar, chip filter |
| `ui/screen/courses/CourseDetailScreen.kt` | Bookmark button, tech stack filter |
| `ui/screen/explore/ExploreScreen.kt` | Search, repo click, tech stack filter |
| `ui/screen/plan/StudyPlannerScreen.kt` | Input dialog, connect ViewModel |
| `ui/screen/profile/ProfileScreen.kt` | Stats, bookmark click, refresh |
| `ui/viewmodel/CourseViewModel.kt` | Bookmark toggle, tech stack filter |
| `ui/viewmodel/ExploreViewModel.kt` | Search state, repo navigation |
| `ui/viewmodel/ProfileViewModel.kt` | Refresh, bookmark navigation |
| `ui/MainScreen.kt` | Connect PlanTabView |
| `domain/repository/DiscoveryRepository.kt` | Thêm searchRepos method |
| `data/repository/DiscoveryRepositoryImpl.kt` | Implement searchRepos |
