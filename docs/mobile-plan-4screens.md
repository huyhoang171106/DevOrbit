# Mobile Plan — 4 Màn hình: Môn học, Khám phá, Kế hoạch, Cá nhân

> Based on backend API at `devorbit-api/` and current mobile codebase at `devorbit-mobile/`.
> Created: 2026-05-28
> API tested at: `http://104.214.179.110:8080`
>
> ## Test Results (2026-05-28)
> | Endpoint | Status | Note |
> |----------|--------|------|
> | GET /api/courses | ✅ 1040 courses | Real data |
> | GET /api/courses?q=&subjectType=&semester=&managementUnit= | ✅ | Search/filters added |
> | GET /api/courses/{id} | ✅ | Full detail + repos |
> | GET /api/courses/graph | ✅ 30 nodes, 18 links | impactScore=0 everywhere |
> | GET /api/courses/{id}/repos | ✅ | Real repos with githubUrl |
> | GET /api/courses/relationships | ✅ 18 relationships | PREREQUISITE + COREQUISITE |
> | GET /api/repos/{id} | ✅ | |
> | GET /api/discovery/recent-repos | ✅ 10 repos | |
> | GET /api/discovery/top-stacks | ✅ | Fixed — seeded tech_stacks data |
> | GET /api/tech-stacks | ✅ | Fixed — same seed |
> | POST /api/student/register | ✅ | JWT returned |
> | POST /api/student/login | ❌ BUG | Register OK but login fails (env config) |
> | GET /api/student/me | ✅ | |
> | POST /api/student/bookmarks | ✅ | Needs title+url fields |
> | DELETE /api/student/bookmarks/{id} | ✅ | |
> | POST /api/ai/generate-roadmap | ✅ | Real AI response |
> | GET /api/ai/repo/{id}/summary | ✅ | AI markdown |
> | POST /api/ai/knowledge-graph/query | ✅ | |
> | POST /api/ai/subject-qa/query | ✅ | Fixed — resilience + migration tạo bảng |
> | repos?techStack=X filter | ✅ | Fixed — join table now populated |
> 
> ## Backend Fixes Applied (2026-05-28)
>
> | Fix | Files | What changed |
> |-----|-------|-------------|
> | Seed tech_stacks + repo_tech_stacks | `TechStackDataInitializer.java` (new), `V005__fix_subject_qa_and_tech_stacks.sql` (new) | `@PostConstruct` + SQL migration populate join table from `primary_language` column |
> | Subject Q&A resilience | `SubjectQaService.java` (modified) | Try-catch wrapper quanh DB ops, `resolveSession()` helper, graceful fallback không crash |
> | Course search/filter | `PublicCourseController.java`, `CourseService.java` (modified) | Thêm `?q=`, `?subjectType=`, `?semester=`, `?managementUnit=` params |
>
> **Còn lại (back-end side):**
> - Login BUG: code logic đúng, cần debug server thật (JWT_SECRET env var / database state)
> - impactScore=0: cần refine knowledge graph algorithm riêng

---

## Tổng quan API Backend

### Tất cả Public Endpoints (không cần auth)

| Method | Endpoint | Response | Mobile đã gọi? |
|--------|----------|----------|----------------|
| GET | `/api/courses` | `List<CourseSummaryResponse>` | ✅ |
| GET | `/api/courses/{id}` | `CourseDetailResponse` | ✅ |
| GET | `/api/courses/graph` | `KnowledgeGraphResponse` | ✅ |
| GET | `/api/courses/{courseId}/repos?techStack=` | `List<RepoSummaryResponse>` | ✅ |
| GET | `/api/courses/relationships` | `List<CourseRelationshipResponse>` | ✅ |
| GET | `/api/courses/relationships/course/{courseId}` | `List<CourseRelationshipResponse>` | ❌ |
| GET | `/api/repos/{repoId}` | `RepoSummaryResponse` | ✅ (AI summary/advice only) |
| GET | `/api/discovery/recent-repos` | `List<RepoSummaryResponse>` | ✅ |
| GET | `/api/discovery/top-stacks` | `List<String>` | ✅ |
| GET | `/api/tech-stacks` | `List<TechStackResponse>` | ✅ |
| POST | `/api/ai/subject-qa/query` | `SubjectQaResponse` | ❌ |
| POST | `/api/ai/knowledge-graph/query` | `AiQueryResponse` | ✅ |
| POST | `/api/ai/generate-roadmap` | `RoadmapRecommendationResponse` | ❌ |
| GET | `/api/ai/repo/{repoId}/summary` | `AiResponse` | ✅ |
| GET | `/api/ai/repo/{repoId}/advice` | `AiResponse` | ✅ |

### Student Endpoints (cần JWT auth)

| Method | Endpoint | Response | Mobile đã gọi? |
|--------|----------|----------|----------------|
| POST | `/api/student/login` | `StudentAuthResponse{token, id, studentCode, fullName, email}` | ✅ |
| POST | `/api/student/register` | `StudentAuthResponse` | ✅ |
| GET | `/api/student/me` | `StudentProfileResponse{id, studentCode, fullName, email}` | ✅ |
| GET | `/api/student/bookmarks` | `List<StudentBookmarkResponse>` | ✅ |
| POST | `/api/student/bookmarks` | `StudentBookmarkResponse` | ✅ |
| DELETE | `/api/student/bookmarks/{id}` | void | ✅ |

---

## PHẦN 1: MÔN HỌC (Courses Screen)

### 1.1 Hiện trạng

**Đã có:**
- `CourseHubScreen` — Danh sách + Toggle "Học kỳ" view
- `CourseDetailScreen` — Chi tiết môn: repos, tutorials, videos, articles
- `RepoDetailScreen` — Chi tiết repo + AI summary/advice
- `CourseViewModel` — Load courses, graph, detail, repos
- `ApiService` — GET courses, repos, tutorials, videos, articles, graph, relationships

**Thiếu / cần cải thiện:**

### 1.2 Gap Analysis

| # | Gap | Backend API | Mức độ ưu tiên |
|---|-----|-------------|----------------|
| C1 | **Search/Filter môn học — Mobile UI** — Backend đã hỗ trợ `?q=`, client-side filter vẫn OK | `GET /api/courses?q=keyword&subjectType=X&semester=N&managementUnit=X` | HIGH |
| C2 | **Filter repos theo tech stack — Mobile UI** — Backend fixed (tech_stacks populated), mobile need wire up | `GET /api/courses/{id}/repos?techStack=X` | HIGH |
| C3 | **Course detail subjects** — Field `subjectType` (DAI_CUONG/CHUYEN_NGANH/CO_SO) chưa hiển thị | Response已有，UI未用 | MEDIUM |
| C4 | **Prerequisite info** — Field `prerequisiteMH`, `previousMH` chưa hiển thị trong detail | Response已有 | MEDIUM |
| C5 | **AI Subject Q&A — Mobile UI** — Backend fixed with resilience, need mobile chat UI | `POST /api/ai/subject-qa/query` | MEDIUM |
| C6 | **Bookmark môn học** — Nút lưu môn trong CourseDetailScreen chưa có | `POST /api/student/bookmarks` (targetType="COURSE") | HIGH |
| C7 | **Learning objectives / Grading criteria** — Chưa hiển thị trong detail | Response已有 | LOW |
| C8 | **Course relationship per-course** — Chưa dùng endpoint `/courses/relationships/course/{id}` | `GET /api/courses/relationships/course/{courseId}` | LOW |

### 1.3 Chi tiết từng Gap

---

#### C1: Search/Filter môn học

**Current state:**
```kotlin
// CourseHubScreen.kt — hiển thị toàn bộ courses, không có search
CourseListScreen(viewModel = viewModel, onCourseClick = { viewModel.openCourse(it) })
```

**Backend:** `GET /api/courses?q=&subjectType=&semester=&managementUnit=` đã hỗ trợ search/filter trực tiếp.

```java
// Spring controller — filter params passthrough
@GetMapping
public List<CourseSummaryResponse> getCourses(
    @RequestParam(required = false) String q,
    @RequestParam(required = false) String subjectType,
    @RequestParam(required = false) Integer semester,
    @RequestParam(required = false) String managementUnit) { ... }
```

`CourseSummaryResponse`:
```java
public record CourseSummaryResponse(
    Long id, String code, String name, String description,
    Long repoCount, Integer semester, int credits,
    String loaiMonHoc,     // "DAI_CUONG", "CHUYEN_NGANH", "CO_SO"
    String managementUnit  // "CNPM", "HTTT", etc.
) {}
```

**Plan:**
- File: `CourseHubScreen.kt`
- Thêm `TextField` search bar trên header
- Gọi `GET /api/courses?q=...` thay vì client-side filter (hoặc hybrid: search server, chips client)
- Thêm chip filter: "Tất cả" | "Đại cương" | "Chuyên ngành" | "Cơ sở" → map to `?subjectType=DAI_CUONG|CHUYEN_NGANH|CO_SO`
- Nếu muốn real-time search, debounce 300ms trước khi gọi API
- Fallback client-side filter vẫn OK nếu muốn đơn giản

**Verify:** Gõ search → list filter real-time. Click chip → filter theo loại.

---

#### C2: Filter repos theo tech stack

**Current state:**
```kotlin
// CourseDetailScreen — hiển thị tech stack chips nhưng không filter được
```

**Backend:** `GET /api/courses/{courseId}/repos?techStack=React`

**Plan:**
- File: `CourseViewModel.kt` — thêm param `techStack: String?` vào `loadCourseDetail`
- File: `CourseDetailScreen.kt` — thêm `var selectedTechStack by remember { mutableStateOf<String?>(null) }`
- Khi click tech stack chip → gọi lại API với `?techStack=X`
- Hiển thị "Tất cả" chip để clear filter

**Verify:** Click tech stack chip → repos list filter. Click "Tất cả" → hiện tất cả.

---

#### C5: AI Subject Q&A

**Current state:** Chưa có

**Backend:**
```java
// POST /api/ai/subject-qa/query
public record SubjectQaRequest(@NotBlank String message, UUID sessionId) {}
public record SubjectQaResponse(String answer, UUID sessionId, List<Long> relevantNodeIds, List<String> sources, String type) {}
```

**Plan:**
- File: `ApiService.kt` — thêm endpoint
  ```kotlin
  @POST("/api/ai/subject-qa/query")
  suspend fun querySubjectQa(@Body body: SubjectQaRequest): SubjectQaResponse
  ```
- File: Tạo `SubjectQaRequest.kt`, `SubjectQaResponse.kt` trong dto/
- File: Tạo `SubjectQaViewModel.kt` — manages chat session, messages list
- File: Tạo `SubjectQaScreen.kt` — Chat UI (message list + input field)
- Navigate từ CourseDetailScreen → floating button "Hỏi AI"

**Verify:** Gửi câu hỏi → nhận answer. History hiển thị correctly.

---

#### C6: Bookmark môn học

**Current state:** `BookmarkRepository` đã có, `ProfileScreen` hiển thị bookmarks. Nhưng không có nút bookmark từ CourseDetail.

**Backend:**
```java
// POST /api/student/bookmarks  (cần JWT)
public record StudentBookmarkRequest(String targetType, Long targetId) {}
// targetType = "COURSE" | "REPO" | "ARTICLE"
```

**Plan:**
- File: `CourseDetailScreen.kt` — thêm bookmark icon button (top-right)
- File: `CourseViewModel.kt` — thêm `fun toggleBookmark(courseId: Long)`
- Logic: check `bookmarkRepository.isBookmarked("COURSE", courseId)` → show filled/hollow heart
- On click → `bookmarkRepository.addBookmark()` hoặc `removeBookmark()`

**Verify:** Click heart → toggle. Vào Profile → thấy bookmark mới.

---

## PHẦN 2: KHÁM PHÁ (Explore Screen)

### 2.1 Hiện trạng

**Đã có:**
- `ExploreScreen` — Hiển thị top tech stacks, recent repos
- `ExploreViewModel` — Load data từ `DiscoveryRepository`
- `DiscoveryRepository` — Gọi 3 endpoints: recent-repos, top-stacks, tech-stacks

**Thiếu / cần cải thiện:**

### 2.2 Gap Analysis

| # | Gap | Backend API | Mức độ ưu tiên |
|---|-----|-------------|----------------|
| E1 | **Search repos** — Không thể search repo theo tên | Client-side filter on recentRepos | HIGH |
| E2 | **Repo detail navigation** — Click repo trong explore không mở detail | `GET /api/repos/{repoId}` | HIGH |
| E3 | **Tech stack filter** — Click tech stack nên filter repos | Client-side hoặc `GET /api/discovery/recent-repos` + filter | HIGH |
| E4 | **Load more repos** — Chỉ hiển thị 10 repos gần nhất, không có pagination | Backend chỉ có `findTop10` | MEDIUM |
| E5 | **Repo tech stacks display — Mobile** — `RepoSummaryResponse.techStacks` là `List<TechStackResponse>`, backend OK | Cần parse proper | HIGH |
| E6 | **AI Summary trong explore repos** — Không có tóm tắt AI cho explore repos | `GET /api/ai/repo/{repoId}/summary` | MEDIUM |
| E7 | **Empty state / error state** — Không có loading/error UX | N/A | MEDIUM |

### 2.3 Chi tiết từng Gap

---

#### E1: Search repos

**Current state:**
```kotlin
// ExploreScreen.kt — LazyColumn hiển thị repos, không có search
items(uiState.recentRepos) { repo -> ... }
```

**Plan:**
- File: `ExploreScreen.kt` — thêm `TextField` search bar
- Filter client-side: `recentRepos.filter { it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }`
- State: `var searchQuery by remember { mutableStateOf("") }`

**Verify:** Gõ search → repos filter real-time.

---

#### E2: Repo detail navigation

**Current state:** Click repo → không làm gì

**Plan:**
- File: `ExploreScreen.kt` — thêm `onRepoClick: (Long) -> Unit` parameter
- File: `ExploreViewModel.kt` — thêm `selectedRepoId: StateFlow<Long?>` + `fun openRepo(id: Long)`
- File: Tạo `ExploreRepoDetailScreen.kt` — Hiển thị repo detail + AI summary
- Hoặc reuse `RepoDetailScreen` từ CourseHubScreen (nếu compatible)

**Verify:** Click repo → mở detail screen. Back → quay lại explore.

---

#### E3: Tech stack filter

**Current state:** Hiển thị tech stacks nhưng không filter được

**Plan:**
- File: `ExploreScreen.kt` — thêm `var selectedTechStack by remember { mutableStateOf<String?>(null) }`
- Khi click tech stack chip → filter `recentRepos` theo tech stack
- Backend `RepoSummaryResponse` có `techStacks: List<TechStackResponse>`, filter client-side
- Hiển thị "Tất cả" chip để clear

**Verify:** Click tech stack → repos filter. Click "Tất cả" → hiện tất cả.

---

#### E5: Parse TechStacks properly

**Current state:**
```kotlin
// ApiService.kt — parse sai
@GET("/api/discovery/recent-repos")
suspend fun getRecentDiscoveryRepos(): List<Map<String, Any>>
```

**Plan:**
- File: `ApiService.kt` — thay `Map<String, Any>` bằng proper DTO
  ```kotlin
  @GET("/api/discovery/recent-repos")
  suspend fun getRecentDiscoveryRepos(): List<RepoSummaryResponse>
  ```
- File: `DiscoveryRepositoryImpl.kt` — map từ `RepoSummaryResponse` → `RecentRepo`
- Field `techStacks` trong `RepoSummaryResponse` là `List<TechStackResponse>`, cần parse

**Verify:** Tech stacks hiển thị đúng trong repo cards.

---

## PHẦN 3: KẾ HOẠH (Plan Screen)

### 3.1 Hiện trạng

**Đã có:**
- `StudyPlannerScreen` — UI hiển thị plan, phases, items
- `StudyPlannerEngine` — Local engine generate plan từ tasks
- `StudyPlan`, `StudyPhase`, `StudyItem` domain models
- `MainScreen.PlanTabView()` — Wrap StudyPlannerScreen, TODO callbacks

**Thiếu / cần cải thiện:**

### 3.2 Gap Analysis

| # | Gap | Backend API | Mức độ ưu tiên |
|---|-----|-------------|----------------|
| P1 | **Plan chưa connect** — `PlanTabView` truyền `studyPlan = null`, callbacks là TODO | Không có backend endpoint lưu plan | HIGH |
| P2 | **AI Generate Roadmap** — Backend có `POST /api/ai/generate-roadmap` nhưng mobile dùng local engine | `POST /api/ai/generate-roadmap` | HIGH |
| P3 | **Persist study plan** — Plan chỉ存在于内存, app restart mất | Không có backend. Dùng本地DataStore/Room | HIGH |
| P4 | **Input configuration** — Không cho user set giờ học/ngày, deadline, priority | N/A | MEDIUM |
| P5 | **Progress tracking** — Toggle item completed nhưng không lưu持久 | N/A | MEDIUM |
| P6 | **Breakdown task** — Nút "Phân tích" chưa hoạt động | N/A | LOW |

### 3.3 Chi tiết từng Gap

---

#### P1 + P2: AI Roadmap Generation

**Current state:**
```kotlin
// MainScreen.kt
PlanTabView() {
    StudyPlannerScreen(
        studyPlan = null,  // ← NULL
        onGeneratePlan = { /* TODO: Connect StudyPlannerEngine */ },
        onToggleItem = { /* TODO */ },
        onBreakdownTask = { /* TODO */ }
    )
}
```

**Backend:**
```java
// POST /api/ai/generate-roadmap
public record RoadmapGenerationRequest(
    @NotBlank @Size(max = 2000) String learningGoals,
    @NotBlank @Size(max = 200) String careerPath
) {}
public record RoadmapRecommendationResponse(...) {} // cần check response shape
```

**Plan:**
- File: `ApiService.kt` — thêm endpoint
  ```kotlin
  @POST("/api/ai/generate-roadmap")
  suspend fun generateRoadmap(@Body body: RoadmapGenerationRequest): RoadmapRecommendationResponse
  ```
- File: Tạo `RoadmapRequest.kt`, `RoadmapResponse.kt` trong dto/
- File: Tạo `StudyPlanRepository.kt` — interface để generate + load plan
- File: Tạo `StudyPlanViewModel.kt` — manages plan state, generation, persistence
  ```kotlin
  data class PlanUiState(
      val plan: StudyPlan? = null,
      val loading: Boolean = false,
      val error: String? = null,
      val showInput: Boolean = true  // show goal input dialog
  )
  ```
- File: Update `StudyPlannerScreen.kt` — thêm input dialog:
  - TextField: "Mục tiêu học tập" (learningGoals)
  - TextField: "Lĩnh vực nghề nghiệp" (careerPath)
  - Button "Tạo lộ trình"
- File: Update `MainScreen.kt` — connect `PlanTabView` với `StudyPlanViewModel`

**Verify:** Nhập mục tiêu → click tạo → plan hiển thị với phases.

---

#### P3: Persist Study Plan

**Current state:** Plan chỉ trong内存

**Plan:**
- File: Tạo `PlanDataStore.kt` trong data/datastore/
  ```kotlin
  class PlanDataStore @Inject constructor(
      private val dataStore: DataStore<Preferences>
  ) {
      suspend fun savePlan(plan: StudyPlan) // serialize to JSON
      fun loadPlan(): Flow<StudyPlan?>       // deserialize from JSON
      suspend fun clearPlan()
  }
  ```
- File: `StudyPlanViewModel.kt` — load plan on init, save on change
- Serialization: dùng Gson/Moshi serialize `StudyPlan` → JSON string

**Verify:** Tạo plan → restart app → plan còn.

---

#### P4: Input Configuration

**Plan:**
- Thêm vào input dialog:
  - Slider: "Giờ học/ngày" (1-8h, default 2)
  - DatePicker: Deadline (optional)
- These feed into `StudyPlannerEngine.generatePlan()`
- `availableHoursPerDay` parameter

**Verify:** Set giờ học → plan phân bổ đúng.

---

#### P5: Progress Tracking

**Plan:**
- File: `StudyPlanViewModel.kt` — `fun toggleItem(itemId: String)`
  ```kotlin
  fun toggleItem(itemId: String) {
      val plan = _state.value.plan ?: return
      val updated = plan.copy(
          phases = plan.phases.map { phase ->
              phase.copy(items = phase.items.map { item ->
                  if (item.id == itemId) item.copy(completed = !item.completed) else item
              })
          }
      )
      _state.update { it.copy(plan = updated) }
      viewModelScope.launch { planDataStore.savePlan(updated) }
  }
  ```

**Verify:** Toggle item → progress bar update → persist sau restart.

---

## PHẦN 4: CÁ NHÂN (Profile Screen)

### 4.1 Hiện trạng

**Đã có:**
- `ProfileScreen` — Hiển thị student info, bookmarks, dark mode, logout
- `ProfileViewModel` — Load student info từ DataStore, bookmarks
- `AuthRepository` — Login, register, getProfile, logout
- `BookmarkRepository` — getAllBookmarks, addBookmark, removeBookmark
- `AuthScreen` — Login/Register UI

**Thiếu / cần cải thiện:**

### 4.2 Gap Analysis

| # | Gap | Backend API | Mức độ ưu tiên |
|---|-----|-------------|----------------|
| R1 | **Edit profile** — Không thể sửa tên/email | Không có backend endpoint | LOW |
| R2 | **Bookmark detail navigation** — Click bookmark không mở detail | Client-side navigation | MEDIUM |
| R3 | **Bookmark subtitle** — `StudentBookmarkResponse.subtitle` chưa hiển thị | Response已有 | LOW |
| R4 | **App version / About** — Không có mục About | N/A | LOW |
| R5 | **Statistics** — Không hiển thị thống kê (số môn đã bookmark, số giờ học) | N/A (tính từ local data) | MEDIUM |
| R6 | **Notification settings** — Không có settings thông báo | N/A | LOW |
| R7 | **Force refresh profile** — Profile từ `/api/student/me` không được gọi lại | `GET /api/student/me` | MEDIUM |
| R8 | **Error handling auth** — Login/Register không hiển thị lỗi chi tiết | Response có thể chứa error | MEDIUM |
| R9 | **BookmarkRepository is MOCK** — Currently in-memory list, not calling API (⚠️ backend endpoints exist, flush-side only) | `GET/POST/DELETE /api/student/bookmarks` | **CRITICAL** |
| R10 | **Login bug on backend** — Register works but login fails | Backend password hash issue | HIGH |

### 4.3 Chi tiết từng Gap

---

#### R2: Bookmark detail navigation

**Current state:**
```kotlin
// ProfileScreen.kt — click bookmark không làm gì
items(state.bookmarks) { bookmark ->
    Surface(..., onClick = { /* ??? */ }) {
        Text(bookmark.title)
        Text(bookmark.targetType)
    }
}
```

**Plan:**
- File: `ProfileScreen.kt` — thêm `onBookmarkClick: (Bookmark) -> Unit` callback
- File: `ProfileViewModel.kt` — thêm `fun openBookmark(bookmark: Bookmark)`
- Logic: check `bookmark.targetType` → navigate đến CourseDetailScreen hoặc RepoDetailScreen
  - "COURSE" → open course detail (cần navigate to Courses tab + open course)
  - "REPO" → open repo detail

**Verify:** Click bookmark → mở đúng detail screen.

---

#### R5: Statistics

**Plan:**
- File: `ProfileScreen.kt` — thêm section "Thống kê"
  ```kotlin
  item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
          StatCard(label = "Môn đã lưu", value = "${state.bookmarks.size}")
          StatCard(label = "Học kỳ hiện tại", value = "HK${currentSemester}")
          StatCard(label = "Điểm TB", value = "N/A")  // nếu có data
      }
  }
  ```

**Verify:** Hiển thị số bookmark, học kỳ hiện tại.

---

#### R7: Force refresh profile

**Plan:**
- File: `ProfileViewModel.kt` — thêm `fun refreshProfile()`
  ```kotlin
  fun refreshProfile() {
      viewModelScope.launch {
          authRepository.getProfile().onSuccess { info ->
              settingsDataStore.setStudentName(info.fullName)
              settingsDataStore.setStudentCode(info.studentCode)
          }
      }
  }
  ```
- Gọi khi pull-to-refresh hoặc khi app foreground

**Verify:** Thay đổi tên trên backend → pull refresh → tên cập nhật.

---

## Implementation Priority

### Phase 1 — Core (1-2 ngày)
1. **C1** Search/filter môn học
2. **E1** Search repos trong explore
3. **E5** Parse tech stacks properly
4. **P1+P2** AI Roadmap generation
5. **P3** Persist study plan

### Phase 2 — Enhancement (1-2 ngày)
6. **C2** Filter repos theo tech stack
7. **C6** Bookmark môn học
8. **E2** Repo detail navigation từ explore
9. **E3** Tech stack filter trong explore
10. **P4** Input configuration cho plan
11. **P5** Progress tracking

### Phase 3 — Polish (1 ngày)
12. **C5** AI Subject Q&A
13. **R2** Bookmark detail navigation
14. **R5** Statistics trong profile
15. **R7** Force refresh profile
16. **E7** Loading/error states

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
| `network/ApiService.kt` | Thêm 3 endpoints: roadmap, subject-qa, bookmarks |
| `ui/screen/courses/CourseHubScreen.kt` | Search bar, tech stack filter |
| `ui/screen/courses/CourseDetailScreen.kt` | Bookmark button, tech stack filter |
| `ui/screen/explore/ExploreScreen.kt` | Search, repo click, tech stack filter |
| `ui/screen/plan/StudyPlannerScreen.kt` | Input dialog, connect ViewModel |
| `ui/screen/profile/ProfileScreen.kt` | Stats, bookmark click, refresh |
| `ui/viewmodel/CourseViewModel.kt` | Bookmark toggle, tech stack filter |
| `ui/viewmodel/ExploreViewModel.kt` | Search state, repo navigation |
| `ui/viewmodel/ProfileViewModel.kt` | Refresh, bookmark navigation |
| `ui/MainScreen.kt` | Connect PlanTabView |
| `domain/repository/DiscoveryRepository.kt` | Thêm search/filter methods |
| `data/repository/DiscoveryRepositoryImpl.kt` | Implement new methods |
