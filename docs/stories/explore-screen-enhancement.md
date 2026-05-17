# US-019 Explore Screen Enhancement

## Status

planned

## Lane

normal

## Product Contract

Nâng cấp màn hình Explore (Khám phá) từ danh sách repo đơn giản thành trung tâm khám phá học thuật với search, bookmark, filter theo tech stack, và các trạng thái UI đầy đủ (loading, error, empty). Thiết kế đồng bộ với Cosmic Design System đã dùng trên Dashboard.

## Relevant Product Docs

- `docs/plans/explore-screen-enhancement.md` (plan chi tiết)
- `docs/ARCHITECTURE.md`
- `devorbit-mobile` DesignSystem (`ui/theme/DesignSystem.kt`)

## Acceptance Criteria

- [ ] **Search bar**: Thanh tìm kiếm animated, expand on focus, filter repos local theo query
- [ ] **Horizontal scroll**: Tech stack chips scroll ngang được (LazyRow), không bị overflow
- [ ] **Stack filter**: Click chip → filter repos theo stack, click lại → clear filter
- [ ] **Bookmark toggle**: Mỗi repo card có nút bookmark, state persist qua DataStore
- [ ] **Pull-to-refresh**: Kéo xuống làm mới tất cả dữ liệu
- [ ] **Category tabs**: Segmented control (Tất cả / Repository / Tech Stack)
- [ ] **Loading state**: Shimmer/skeleton trong khi fetch dữ liệu
- [ ] **Error state**: Message + retry button khi API fail
- [ ] **Empty state**: Message thân thiện khi không có kết quả
- [ ] **Repo card**: Glassmorphism card với gradient accent, language badge, star/fork count
- [ ] **Navigation**: Click repo → mở RepoDetailScreen
- [ ] **Entrance animation**: Cards xuất hiện với stagger animation

## Design Notes

- UI components tách riêng trong `ui/screen/explore/components/`
- Giữ nguyên theme token từ DesignSystem, không thêm màu mới
- Entrance animation dùng `animateItemPlacement` + `AnimatedVisibility`
- Bookmark lưu trong DataStore (key-value), không cần DB
- Search filter local (client-side) — chỉ `/searchRepos` API khi có backend support

## Data Layer Changes

```kotlin
// Domain model mở rộng
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

// Repository interface mới
interface DiscoveryRepository {
    // ... existing methods
    suspend fun searchRepos(query: String): List<RecentRepo>  // NEW
}
```

## New Files

| File | Purpose |
|------|---------|
| `ui/screen/explore/components/ExploreSearchBar.kt` | Animated search bar |
| `ui/screen/explore/components/ExploreRepoCard.kt` | Glassmorphism repo card |
| `ui/screen/explore/components/TechStackChip.kt` | Selectable tech stack chip |
| `ui/screen/explore/components/CategoryTabs.kt` | Segmented category control |

## Modified Files

| File | Change |
|------|--------|
| `domain/repository/DiscoveryRepository.kt` | Expand RecentRepo model |
| `data/repository/DiscoveryRepositoryImpl.kt` | Parse new fields + search |
| `ui/viewmodel/ExploreViewModel.kt` | Add search/filter/bookmark/refresh |
| `ui/screen/explore/ExploreScreen.kt` | Rewrite with all new components |
| `ui/MainScreen.kt` | Pass navigation callback |

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | `ExploreViewModel` tests for search, filter, bookmark toggle |
| Integration | Not required; no API contract change |
| Platform | `:app:testDebugUnitTest` + Android compile through Gradle |

## Dependencies

- Bookmark persistence: DataStore Preferences đã setup trong project
- API `/discovery/search-repos`: Optional fallback về local filter

## Harness Delta

N/A — sama với harness hiện tại (GitNexus đã active).
