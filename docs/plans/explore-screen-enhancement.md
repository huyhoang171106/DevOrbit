# Plan: Nâng cấp màn hình Khám phá (Explore Screen)

**Ngày:** 2026-05-15  
**Dự án:** DevOrbit Mobile  
**Tác giả:** Agent

---

## 1. Hiện trạng (Current State)

Màn hình Explore hiện tại là một `LazyColumn` với 3 section chính:

1. **Header** — Tiêu đề "Khám phá" + mô tả phụ
2. **Công nghệ phổ biến** — `Row` chứa tối đa 4 `AssistChip` (top stacks)
3. **Tất cả tech stack** — `Row` chứa tối đa 8 `SuggestionChip`
4. **Repo mới cập nhật** — Danh sách `Card` đơn giản

### Vấn đề hiện tại

| Vấn đề | Chi tiết |
|--------|----------|
| **Overflow ngang** | `Row` không scroll được → chip bị che/tràn trên màn hình hẹp |
| **Thiếu search** | Màn hình "Khám phá" không có thanh tìm kiếm |
| **Loading sơ sài** | Chỉ text "Đang tải..." khi stack rỗng, không có loading cho repos |
| **Không error state** | API fail → emptyList âm thầm, người dùng không biết |
| **Không empty state** | Khi không có dữ liệu → màn hình trống |
| **Thiếu pull-to-refresh** | Không thể kéo để làm mới |
| **Thiếu visual polish** | So với Dashboard (gradient, animation, glassmorphism) → Explore rất plain |
| **Không có category browsing** | Chỉ 2 hàng chip, không có cách duyệt theo danh mục |
| **Thiếu bookmark/favorite** | Không thể lưu repo yêu thích |
| **Không phân trang** | Repos giới hạn ở 1 lần gọi API |
| **Không có click handler** | Chip và Repo Card đều onClick = {} (no-op) |

---

## 2. Mục tiêu (Goals)

1. Biến Explore thành trung tâm khám phá học thuật thực sự (không chỉ là danh sách repo)
2. Hỗ trợ tìm kiếm, duyệt theo tech stack, lưu bookmark
3. Nâng visual quality ngang bằng Dashboard
4. Xử lý đầy đủ các state: loading, error, empty, success
5. Tương tác được: click vào repo → xem chi tiết, click stack → lọc

---

## 3. Kiến trúc đề xuất (Proposed Architecture)

### 3.1. Component Tree

```
ExploreScreen
├── SearchBar (animated, expandable)
├── PullToRefresh wrapper
├── LazyColumn
│   ├── HeroHeader (gradient background, animated)
│   ├── QuickFilters (horizontal scrollable chips)
│   ├── CategoryTabs (segment control: Tất cả | Repo | Tech Stack)
│   ├── TopStacksSection
│   │   ├── SectionHeader("Công nghệ phổ biến")
│   │   └── LazyRow (GlassChip với icon)
│   ├── TechStackGrid
│   │   ├── SectionHeader("Tất cả tech stack")
│   │   └── FlowLayout / LazyRow (chips lọc được)
│   └── RepoSection
│       ├── SectionHeader("Repo mới cập nhật")
│       └── items (ExploreRepoCard với animation)
├── FloatingActionButton (filter/sort)
└── BottomSheet (filter options)
```

### 3.2. Data Flow

```
ExploreViewModel
├── state: StateFlow<ExploreUiState>
│   ├── searchQuery: String
│   ├── selectedCategory: Category
│   ├── selectedStack: String?
│   ├── topStacks: List<TopStack>
│   ├── techStacks: List<TechStackInfo>
│   ├── recentRepos: List<RecentRepo>
│   ├── filteredRepos: List<RecentRepo>  (derived from search + stack)
│   ├── bookmarkedRepoIds: Set<Long>
│   ├── isLoading: Boolean
│   ├── error: String?
│   └── isRefreshing: Boolean
├── onSearch(query) → filter repos locally
├── onSelectStack(stack) → filter repos by stack
├── onRefresh() → reload all
├── onBookmarkToggle(repoId) → toggle bookmark
└── onRepoClick(repo) → navigate to RepoDetailScreen
```

### 3.3. Domain Layer Changes

```kotlin
// Expand RecentRepo với bookmark support
data class RecentRepo(
    val id: Long,
    val name: String,
    val description: String,
    val githubUrl: String,
    val language: String,
    val stars: Int,
    val forks: Int,        // NEW
    val courseName: String?,
    val updatedAt: String?  // NEW
)

// Thêm search/filter methods vào DiscoveryRepository
interface DiscoveryRepository {
    suspend fun getTechStacks(): List<TechStackInfo>
    suspend fun getRecentRepos(): List<RecentRepo>
    suspend fun getTopStacks(): List<TopStack>
    suspend fun searchRepos(query: String): List<RecentRepo>  // NEW
}
```

---

## 4. Kế hoạch thực hiện (Execution Plan)

### Phase 1: Data Layer & ViewModel Refactor

**Task 1.1 — Mở rộng domain model**
- File: `domain/repository/DiscoveryRepository.kt`
- Thêm `forks`, `updatedAt` vào `RecentRepo`
- Thêm `searchRepos(query)` vào interface
- ⏱ ~15 phút | Độ phức tạp: Thấp

**Task 1.2 — Cập nhật repository implementation**
- File: `data/repository/DiscoveryRepositoryImpl.kt`
- Parse thêm fields mới từ API response
- Implement `searchRepos` (gọi API search nếu có, fallback filter local)
- ⏱ ~15 phút | Độ phức tạp: Thấp

**Task 1.3 — Refactor ViewModel**
- File: `ui/viewmodel/ExploreViewModel.kt`
- Thêm search state, category state, bookmark set
- Thêm methods: `onSearchQueryChanged`, `onSelectStack`, `onRefresh`, `onBookmarkToggle`
- ⏱ ~30 phút | Độ phức tạp: Trung bình

### Phase 2: UI Components

**Task 2.1 — ExploreSearchBar**
- File mới: `ui/screen/explore/components/ExploreSearchBar.kt`
- Animated search bar với glassmorphism style
- Expand on focus, filter suggestions
- ⏱ ~30 phút | Độ phức tạp: Trung bình

**Task 2.2 — ExploreRepoCard**
- File mới: `ui/screen/explore/components/ExploreRepoCard.kt`
- Card cao cấp với glassmorphism, gradient accent
- Bookmark button, language badge, star/fork count
- Entrance animation
- ⏱ ~20 phút | Độ phức tạp: Trung bình

**Task 2.3 — TechStackChip & SectionHeader**
- File mới: `ui/screen/explore/components/TechStackChip.kt`
- Chip có icon, màu sắc động theo stack
- Trạng thái selected/unselected
- ⏱ ~15 phút | Độ phức tạp: Thấp

**Task 2.4 — CategoryTabs**
- Segmented control: "Tất cả", "Repository", "Tech Stack"
- Animated indicator
- ⏱ ~15 phút | Độ phức tạp: Thấp

### Phase 3: Rewrite ExploreScreen

**Task 3.1 — Rewrite ExploreScreen.kt**
- File: `ui/screen/explore/ExploreScreen.kt`
- Tích hợp tất cả components mới
- Pull-to-refresh via `pullRefresh`
- Animated section entrance
- Loading shimmer effect
- Error state với retry button
- Empty state với illustration message
- ⏱ ~60 phút | Độ phức tạp: Cao

**Task 3.2 — Navigation integration**
- Kết nối repo click → navigate to RepoDetailScreen
- Bookmark persistence (shared preferences hoặc local DB)
- ⏱ ~20 phút | Độ phức tạp: Trung bình

---

## 5. Chi tiết thiết kế UI (UI Design Specs)

### 5.1. Color Palette (Explore-specific)

| Element | Color Token | Hex |
|---------|-------------|-----|
| Search bar bg | Glass | `0x1AFFFFFF` |
| Search icon | Plasma | `#7B61FF` |
| Selected chip | Plasma | `#7B61FF` |
| Unselected chip | GlassBorder | `0x33FFFFFF` |
| Card surface | Nebula | `#10101A` |
| Card border | GlassBorder | `0x33FFFFFF` |
| Bookmark active | Aurora | `#00F5A0` |
| Bookmark inactive | TextTertiary | `0x66FFFFFF` |

### 5.2. Typography Scale

| Element | Style | Size | Weight |
|---------|-------|------|--------|
| Hero title | display | 32sp | Bold |
| Section header | command | 14sp | Medium |
| Repo name | body | 16sp | Normal |
| Description | label | 11sp | Bold |
| Metric (stars/forks) | label | 11sp | Bold |

### 5.3. Spacing

| Token | Value | Usage |
|-------|-------|-------|
| atomic | 4dp | Icon padding |
| small | 8dp | Between chips |
| medium | 16dp | Section padding |
| large | 24dp | Card inner padding |
| cosmic | 48dp | Hero section |

### 5.4. Animation Specs

| Animation | Type | Duration | Easing |
|-----------|------|----------|--------|
| Search bar expand | height + width | 300ms | FastOutSlowIn |
| Card entrance | fadeIn + slideUp | 400ms | FastOutSlowIn |
| Chip select | scale + color | 200ms | Spring |
| Pull-to-refresh | rotation | Linear | default |
| Section stagger | translateY | 100ms stagger | FastOutSlowIn |

---

## 6. File changes summary

| File | Action | Phase |
|------|--------|-------|
| `domain/repository/DiscoveryRepository.kt` | Edit — expand model | 1 |
| `data/repository/DiscoveryRepositoryImpl.kt` | Edit — parse new fields | 1 |
| `ui/viewmodel/ExploreViewModel.kt` | Rewrite — add search/bookmark/filter | 1 |
| `ui/screen/explore/components/ExploreSearchBar.kt` | **New** | 2 |
| `ui/screen/explore/components/ExploreRepoCard.kt` | **New** | 2 |
| `ui/screen/explore/components/TechStackChip.kt` | **New** | 2 |
| `ui/screen/explore/components/CategoryTabs.kt` | **New** | 2 |
| `ui/screen/explore/ExploreScreen.kt` | Rewrite — integrate all | 3 |
| `ui/MainScreen.kt` | Edit — pass navigation callback | 3 |
| `ui/screen/explore/ExploreScreen.kt` (navigation) | Edit — connect RepoDetail | 3 |

---

## 7. Rủi ro (Risks)

| Rủi ro | Mitigation |
|--------|------------|
| API không hỗ trợ search/searchRepos | Fallback về local filter trên dữ liệu đã fetch |
| Bookmark state mất khi restart | Dùng DataStore preferences để persist |
| Animation performance trên thiết bị thấp | Giới hạn số lượng animation đồng thời, dùng `drawWithCache` |
| Pull-to-refresh conflict với LazyColumn scroll | Dùng `pullRefresh` modifier chuẩn của Material3 |

---

## 8. Definition of Done

- [ ] Search bar hoạt động (gõ text → filter repos local)
- [ ] Tech stack chips scroll ngang được
- [ ] Click chip → filter repos theo stack
- [ ] Repo card hiển thị đẹp (glassmorphism, gradient accent)
- [ ] Bookmark toggle hoạt động
- [ ] Pull-to-refresh làm mới dữ liệu
- [ ] Loading state hiển thị shimmer/skeleton
- [ ] Error state hiển thị message + retry button
- [ ] Empty state hiển thị message thân thiện
- [ ] Click repo → navigate đến RepoDetailScreen
- [ ] Category tabs chuyển đổi view
- [ ] Compile không lỗi

---

## 9. Tổng thời gian ước lượng

| Phase | Tasks | Thời gian |
|-------|-------|-----------|
| Phase 1: Data + ViewModel | 3 tasks | ~60 phút |
| Phase 2: UI Components | 4 tasks | ~80 phút |
| Phase 3: Screen + Integration | 2 tasks | ~80 phút |
| **Total** | **9 tasks** | **~220 phút (~3.5h)** |
