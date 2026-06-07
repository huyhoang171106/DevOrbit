# US-022 Mobile Subject Screen Enhancement

## Status

planned

## Lane

normal

## Product Contract

Nâng cấp toàn diện màn hình Môn học (Courses/Subjects) trong DevOrbit Mobile,
biến nó từ danh sách khóa học cơ bản thành một **Course Hub hoàn chỉnh** với
tra cứu theo học kỳ, lọc thông minh, hiển thị prerequisite, và UX cao cấp.

## Relevant Product Docs

- `devorbit-mobile/.gsd/SPEC.md`
- `devorbit-mobile/.gsd/ROADMAP.md`
- `docs/ARCHITECTURE.md`
- `docs/stories/mobile-course-hub-detail-navigation.md`
- `docs/stories/mobile-repo-tech-stack-filter.md`
- `docs/stories/SE-2025-Curriculum-Integration.md`

## Current State

Màn hình Môn học hiện tại gồm:
- **CourseHubScreen**: Entry point với List/Graph toggle
- **CourseListScreen**: Danh sách khóa học + search cơ bản
- **CourseDetailScreen**: Tab detail (Repos, Tutorials, Videos, Articles)
- **SemesterGraphView**: Semester cards (collapsible)
- **KnowledgeGraphScreen**: Galaxy canvas visualization

### Gaps Identified

| Khu vực | Vấn đề |
|---------|--------|
| **Data Layer** | CourseEntity thiếu `semester`, `loaiMonHoc`; `credits` luôn = 0 |
| **Course List** | Không có semester filter, course type indicator, sort, stats header |
| **Course Detail** | Thiếu prerequisites, description, related courses, course type, semester info |
| **Semester View** | Nodes không clickable, thiếu credit count, thiếu prerequisite arrows |
| **Navigation** | Không có breadcrumb, search thô sơ |
| **UX** | Thiếu empty states, loading skeletons, pull-to-refresh |

---

## Implementation Plan

### Phase A: Data Foundation

Củng cố data layer để hỗ trợ các UI features.

#### A1. CourseEntity — Add missing fields

**File**: `data/local/entity/CourseEntity.kt`

Add fields:
- `semester: Int?` — học kỳ (1-8)
- `loaiMonHoc: String?` — loại môn (BB = compulsory, TC = elective)
- `soTinChi: Int` thay vì `credits` (đồng bộ tên với API response)

```kotlin
@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Long,
    val maMH: String,
    val tenMH: String,
    val credits: Int,
    val semester: Int? = null,
    val loaiMonHoc: String? = null,
    val description: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)
```

**Risk**: tiny
**Migration**: Room fallback-to-destructive-migration (hoặc `fallbackToDestructiveMigration`)

---

#### A2. AcademicRepository — Persist semester & loaiMonHoc

**File**: `data/repository/AcademicRepository.kt`

Hiện tại `refreshCourses()` map `CourseSummary` -> `CourseEntity` bỏ qua `semester` và `loaiMonHoc` và set `credits = 0`.

Fix:
- Map `CourseSummary.semester` -> `CourseEntity.semester`
- Map `CourseSummary.loaiMonHoc` -> `CourseEntity.loaiMonHoc`
- `credits` cần lấy từ API hoặc từ CourseSummary nếu API hỗ trợ

**Risk**: tiny

---

#### A3. CourseDao — Add semester-based queries

**File**: `data/local/dao/CourseDao.kt`

Add:
```kotlin
@Query("SELECT * FROM courses WHERE semester = :semester ORDER BY maMH ASC")
fun getCoursesBySemester(semester: Int): Flow<List<CourseEntity>>

@Query("SELECT DISTINCT semester FROM courses WHERE semester IS NOT NULL ORDER BY semester ASC")
fun getAvailableSemesters(): Flow<List<Int>>

@Query("SELECT SUM(credits) FROM courses")
fun getTotalCredits(): Flow<Int>
```

**Risk**: tiny

---

#### A4. GraphNode — Add credit field

**File**: `domain/model/KnowledgeGraph.kt`

```kotlin
data class GraphNode(
    val id: Long,
    val name: String,
    val code: String,
    val credits: Int = 0,
    val level: Int = 0,
    val impactScore: Double = 0.0,
    val semester: Int? = null,
    val description: String? = null,
)
```

Cần update mapping trong `AcademicRepository.getCourseGraph()`, kiểm tra `GraphNodeDto` có thể expand.

**Risk**: tiny

---

### Phase B: Course Hub Enhancement

Nâng cấp CourseHubScreen và CourseListScreen.

#### B1. CourseHubScreen — Add stats header & semester filter

**File**: `ui/screen/courses/CourseHubScreen.kt`

Add ở đầu CourseHubScreen:
- **StatsRow**: "X môn học · Y tín chỉ · Z học kỳ"
- **SemesterFilterChips**: Horizontal scroll chips cho HK1-HK8 (chọn = filter list)
- "Tất cả" chip để reset filter

```kotlin
@Composable
fun CourseHubHeader(
    totalCourses: Int,
    totalCredits: Int,
    semesterCount: Int,
    selectedSemester: Int?,
    onSemesterSelected: (Int?) -> Unit
)
```

**Risk**: normal

---

#### B2. CourseListScreen — Add course type indicator & semester badge

**File**: `ui/screen/courses/CourseListScreen.kt`

**CourseListItem** updates:
- Add `loaiMonHoc` badge: "BB" (compulsory) với màu plasma, "TC" (elective) với màu aurora
- Add `semester` badge: "HK3" nhỏ
- Show `description` preview (1 line) nếu có
- Thêm `impactScore` dot color indicator (import từ SemesterGraphView logic)

**Empty state**: Khi search không có kết quả, hiển thị:
```
🔍 No courses found
"Try adjusting your search or filter"
```

**Risk**: normal

---

#### B3. CourseListScreen — Add sort options

**File**: `ui/screen/courses/CourseListScreen.kt`

Add sort dropdown/chips:
- By code (default)
- By name
- By semester
- By credits

```kotlin
enum class CourseSortBy { CODE, NAME, SEMESTER, CREDITS }
```

**Risk**: tiny

---

### Phase C: Course Detail Enrichment

Cải thiện CourseDetailScreen.

#### C1. CourseDetailScreen — Add info header

**File**: `ui/CourseDetailScreen.kt`

Add info section above tabs:
- **Course code**: `maMH` với `cosmic` style
- **Semester**: "Học kỳ X"
- **Loại môn**: compulsory/elective badge
- **Credits**: "X tín chỉ"
- **Description**: Expandable text (show/collapse)

**Risk**: normal

---

#### C2. CourseDetailScreen — Add Prerequisites tab

**File**: `ui/CourseDetailScreen.kt`

New tab: "Prerequisites" (hiển thị nếu có dữ liệu)

Data:
- Sử dụng `CourseRelationshipEntity` từ Room + `CourseDao.getCourseById()`
- Hiển thị môn học prerequisite với status (đã học/chưa học)

**Risk**: normal

---

#### C3. CourseDetailScreen — Add Related Courses section

**File**: `ui/CourseDetailScreen.kt`

Trong Prerequisites tab hoặc tab riêng:
- Các môn cùng học kỳ
- Các môn có liên quan (từ CourseRelationshipEntity)
- Tap để navigate đến course detail khác

**Risk**: normal

---

#### C4. CourseViewModel — Add detail enrichment methods

**File**: `ui/viewmodel/CourseViewModel.kt`

Add:
- `loadPrerequisites(courseId: Long)` — fetch relationships từ dao/server
- `loadRelatedCourses(courseId: Long)` — courses cùng semester
- `coursePrerequisites` state
- `courseType` state tracking

**Risk**: normal

---

### Phase D: Graph & Semester View Polish

Cải thiện SemesterGraphView.

#### D1. SemesterCard — Add credit stats & làm cho nodes clickable

**File**: `ui/screen/courses/CourseHubScreen.kt` (SemesterCard, CourseNodeRow)

**SemesterCard**:
- Show "X tín chỉ" next to "X môn"
- Progress bar: số môn đã hoàn thành / tổng số

**CourseNodeRow**:
- Thêm `clickable` -> `onCourseClick(courseId)` để mở CourseDetailScreen từ graph
- Hiển thị credit count: `3 TC`
- Hiển thị `loaiMonHoc` indicator (BB/TC dot)

**Risk**: normal

---

#### D2. SemesterCard — Add prerequisite lines

**File**: `ui/screen/courses/CourseHubScreen.kt`

Vẽ đường kết nối giữa các course nodes trong cùng semester card:
- Dựa vào `CourseRelationshipEntity.type` = "PREREQUISITE"
- Arrow line: source -> target
- Màu sắc: plasma (50% alpha)

Implementation:
- Sử dụng `Canvas` trong SemesterCard
- Tính toán vị trị tương đối giữa các nodes dựa trên list order

**Risk**: high (custom Canvas drawing trong card layout)

---

#### D3. KnowledgeGraphScreen — Navigate to course detail

**File**: `ui/screen/knowledge/KnowledgeGraphScreen.kt`

KnowledgePortalCard:
- Thêm action button "Xem chi tiết" để mở CourseDetailScreen
- Kết nối navigation: cần pass callback `onCourseDetailClick(courseId: Long)`

**Risk**: normal

---

### Phase E: Premium UX

#### E1. Loading skeletons

**File**: `ui/screen/courses/CourseListScreen.kt`

- Thay thế CircularProgressIndicator bằng shimmer skeleton cards
- 3-5 shimmer items trong khi data loading

**Risk**: tiny

---

#### E2. Pull-to-refresh

**File**: `ui/screen/courses/CourseListScreen.kt`

- Wrap LazyColumn trong `PullToRefreshBox` (Material 3)
- Gọi `viewModel.refreshCourses()` trên refresh

**Risk**: tiny

---

#### E3. Empty states

**File**: `ui/screen/courses/CourseListScreen.kt`

- **Search empty**: hình minh họa + message gợi ý
- **No courses**: "Chưa có dữ liệu môn học. Kéo để refresh."
- **Error state**: retry button

**Risk**: tiny

---

#### E4. CourseHubScreen — Animated transitions

**File**: `ui/screen/courses/CourseHubScreen.kt`

- AnimatedContent cho List <-> Graph toggle
- AnimatedVisibility cho semester filter chips
- AnimatedVisibility cho stats header collapse

**Risk**: normal

---

## Validation

| Layer | Expected proof |
|-------|---------------|
| Unit | `CourseDaoTest` covers new semester/credit queries |
| Unit | `CourseHubNavigationStateTest` extended for semester filter |
| Unit | `RepoFilterStateTest` remains passing |
| Platform | `:app:testDebugUnitTest` compiles and runs |
| Platform | Room migration tests pass |

## Harness Delta

GitNexus MCP tools available for impact analysis before editing each symbol.
