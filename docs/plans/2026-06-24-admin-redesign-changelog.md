# DevOrbit Admin Redesign — Chi Tiết Thay Đổi

> **Ngày**: 2026-06-24
> **Branch**: `feat/admin-module-scaffold`
> **PR**: [#64](https://github.com/huyhoang171106/DevOrbit/pull/64)
> **Module**: `devorbit-admin/` (Android, Kotlin 2.0.21, AGP 8.7.3, Jetpack Compose, BOM 2024.11.00)
> **Design System**: "DevOrbit UIT" — UIT Blue (`#2F6BFF`), nền trắng, toàn bộ UI tiếng Việt

---

## Mục Lục

1. [Tổng Quan](#1-t%E1%BB%95ng-quan)
2. [Design System (`core/designsystem/`)](#2-design-system-coredesignsystem)
3. [Navigation & Scaffold](#3-navigation--scaffold)
4. [Screen: Login](#4-screen-login)
5. [Screen: Dashboard (Điều hành)](#5-screen-dashboard-%C4%91i%E1%BB%81-h%C3%A0nh)
6. [Screen: Courses (Môn học)](#6-screen-courses-m%C3%B4n-h%E1%BB%8Dc)
7. [Screen: Candidates (Ứng viên)](#7-screen-candidates-%E1%BB%A9ng-vi%C3%AAn)
8. [Screen: Approved Repos (Kho đã duyệt)](#8-screen-approved-repos-kho-%C4%91%C3%A3-duy%E1%BB%87t)
9. [Screen: GitHub Scan (Quét GitHub)](#9-screen-github-scan-qu%C3%A9t-github)
10. [Screen: Students (Sinh viên)](#10-screen-students-sinh-vi%C3%AAn)
11. [Screen: Moderation - Community (Cộng đồng)](#11-screen-moderation---community-c%E1%BB%99ng-%C4%91%E1%BB%93ng)
12. [Screen: Reviews (Đánh giá)](#12-screen-reviews-%C4%91%C3%A1nh-gi%C3%A1)
13. [Screen: Reports (Báo cáo)](#13-screen-reports-b%C3%A1o-c%C3%A1o)
14. [Screen: Notifications (Thông báo)](#14-screen-notifications-th%C3%B4ng-b%C3%A1o)
15. [Sửa Lỗi Build & Test](#15-s%E1%BB%ADa-l%E1%BB%97i-build--test)
16. [Dọn Dẹp](#16-d%E1%BB%8Dn-d%E1%BA%B9p)
17. [Những Việc Còn Lại](#17-nh%E1%BB%AFng-vi%E1%BB%87c-c%C3%B2n-l%E1%BA%A1i)
18. [Thống Kê](#18-th%E1%BB%91ng-k%C3%AA)

---

## 1. Tổng Quan

Trước khi redesign, `devorbit-admin` là một scaffold mới tinh với:
- Theme Obsidian tối (#1a1a2e, tím, gradient, glassmorphism)
- Navigation flat (tabs ngang, không nhóm)
- Mới chỉ có skeleton, chưa có screen nào hoàn chỉnh
- Code cũ trong `_quarantine/` (giữ lại để tham khảo)

Sau redesign:
- **Design system riêng** với 9 file components, tất cả đều dùng được lại
- **5 nhóm navigation** (Điều hành, Nội dung, Kiểm duyệt, Người dùng, Thêm) với adaptive layout
- **13 màn hình** hoàn chỉnh, mỗi màn hình có đầy đủ UI states (loading, empty, error, content)
- **Toàn bộ UI tiếng Việt** — không một chữ English nào trên giao diện
- **60 file thay đổi**, +6026 dòng thêm, -10815 dòng xoá
- Build + test đều xanh

---

## 2. Design System (`core/designsystem/`)

Tất cả components đều nằm dưới `app/src/main/java/vn/edu/uit/devorbit/admin/core/designsystem/`.

### 2.1 AdminButtons.kt

```kotlin
// 4 loại button, mỗi loại có loading state riêng
@Composable
fun AdminPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,    // Hiển thị CircularProgressIndicator thay text
    icon: ImageVector? = null,     // Icon bên trái
    content: @Composable RowScope.() -> Unit
)

@Composable
fun AdminSecondaryButton(/* viền outline, nền trắng */)
@Composable
fun AdminDangerButton(/* đỏ, cho destructive actions */)
@Composable
fun AdminTextButton(/* không viền, không nền, chỉ text */}
```

- Khi `isLoading = true`: button bị disable, text ẩn đi, hiện `CircularProgressIndicator` nhỏ
- Khi `enabled = false`: opacity giảm, không click được
- Tất cả đều có `Modifier.height(48.dp)` và `shape = RoundedCornerShape(12.dp)`

### 2.2 AdminSearchField.kt

```kotlin
@Composable
fun AdminSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Tìm kiếm...",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    autoFocus: Boolean = false,
)

@Composable
fun AdminSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Tìm kiếm...",
    actions: @Composable RowScope.() -> Unit = {},
)
```

- `AdminSearchField`: dạng filled field với icon kính lúp, nút clear (X) khi có text
- `AdminSearchBar`: dạng surface tròn dùng trong top bar
- Debounce mặc định 300ms (xử lý ở ViewModel, không ở component)

### 2.3 StatusBadge.kt

```kotlin
enum class StatusType {
    SUCCESS, WARNING, ERROR, INFO, PENDING
}

@Composable
fun StatusBadge(
    text: String,
    type: StatusType,
    modifier: Modifier = Modifier,
)

@Composable
fun SeverityIndicator(
    type: StatusType,
    size: Dp = 8.dp,
)

@Composable
fun StatusDot(
    type: StatusType,
    size: Dp = 10.dp,
)
```

- `StatusBadge`: pill shape với màu nền + text
- `SeverityIndicator`: chấm tròn màu, dùng trong danh sách priority
- `StatusDot`: chấm tròn to hơn, có animated pulse effect
- Màu sắc: SUCCESS = Xanh lá (#4CAF50), WARNING = Cam (#FF9800), ERROR = Đỏ (#F44336), INFO = Xanh dương (#2F6BFF), PENDING = Xám (#9E9E9E)

### 2.4 LoadingSkeleton.kt

```kotlin
@Composable
fun ShimmerSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
)

@Composable
fun CardSkeleton(
    lines: Int = 3,
    modifier: Modifier = Modifier,
)
// Một thẻ skeleton với shimmer

@Composable
fun ListSkeleton(
    itemCount: Int = 5,
    itemHeight: Dp = 72.dp,
    modifier: Modifier = Modifier,
)
// Danh sách skeleton items

@Composable
fun MetricSkeleton()
// 4 ô metrics dạng grid
```

- Tất cả dùng `ShimmerBrush` custom: gradient trắng chạy ngang trên nền xám nhạt
- Animation lặp vô hạn, tốc độ 1200ms/cycle

### 2.5 States.kt

```kotlin
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,      // Nút hành động (tuỳ chọn)
    onAction: (() -> Unit)? = null,
)

@Composable
fun NoResultsState(
    query: String,
    suggestion: String? = null,
)

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,              // Bắt buộc — luôn có retry
)

@Composable
fun OfflineBanner()

@Composable
fun InitialLoading(
    modifier: Modifier = Modifier,
)
```

- `InitialLoading`: màn hình xám trắng với logo UIT + loading spinner
- `EmptyState`: icon to ở giữa, title, subtitle, nút action (nếu có)
- `NoResultsState`: variant của EmptyState nhưng có query highlight + suggestion
- `ErrorState`: icon lỗi + message + nút "Thử lại"
- `OfflineBanner`: thanh nhỏ vàng/cam ở trên cùng báo mất kết nối

### 2.6 ConfirmationSheet.kt

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String,
    confirmLabel: String = "Xác nhận",
    dismissLabel: String = "Huỷ",
    isDestructive: Boolean = false,    // true → nút confirm màu đỏ
    onConfirm: () -> Unit,
    isLoading: Boolean = false,        // Chống double-submit
)
```

- Dùng `ModalBottomSheet` từ Material 3
- Khi `isLoading = true`: nút confirm disable + hiện spinner
- Khi `isDestructive = true`: nút confirm màu đỏ
- Không dismiss được khi đang loading (chống thoát giữa chừng)

### 2.7 FilterChipGroup.kt

```kotlin
data class FilterOption(
    val id: String,
    val label: String,
    val icon: ImageVector? = null,
)

@Composable
fun FilterChipGroup(
    options: List<FilterOption>,
    selectedId: String,
    onSelectedChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleSelection: Boolean = true,
)

@Composable
fun SegmentedFilter(
    options: List<FilterOption>,
    selectedId: String,
    onSelectedChange: (String) -> Unit,
    modifier: Modifier = Modifier,
)
```

- `FilterChipGroup`: hàng ngang các chip tròn, màu xanh khi chọn
- `SegmentedFilter`: dải nút liền nhau (dùng cho tab phân loại: Tất cả/Mới/Đã duyệt...)

### 2.8 MetricDisplay.kt

```kotlin
@Composable
fun MetricDisplay(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color = UITBlue,
    trend: String? = null,        // "+12%" so với kỳ trước
    trendUp: Boolean = true,
    onClick: (() -> Unit)? = null,
)

@Composable
fun PriorityItem(
    label: String,
    entity: String,
    type: StatusType,
    time: String,
    onClick: () -> Unit,
)

@Composable
fun ActivityTimelineItem(
    title: String,
    subtitle: String,
    time: String,
    icon: ImageVector,
    iconTint: Color = UITBlue,
)

@Composable
fun QuickAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    badgeCount: Int? = null,       // Badge đỏ ở góc (cho thông báo mới)
)
```

- `MetricDisplay`: card trắng bo góc 12dp, icon + số to + title nhỏ, có border nhẹ
- `PriorityItem`: icon trái + text phải, dòng kẻ ngăn
- `ActivityTimelineItem`: timeline dọc với chấm tròn + đường kẻ
- `QuickAction`: icon vuông 48dp + label, dạng lưới

---

## 3. Navigation & Scaffold

**File**: `AdminScaffold.kt`, `AdminRoot.kt`

### 3.1 5 Navigation Groups

```kotlin
sealed class NavGroup(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val routes: List<String>,
) {
    data object Dashboard : NavGroup("dashboard", "Điều hành", Icons.Outlined.Dashboard, Icons.Filled.Dashboard, ...)
    data object Content : NavGroup("content", "Nội dung", Icons.Outlined.MenuBook, Icons.Filled.MenuBook, ...)
    data object Moderation : NavGroup("moderation", "Kiểm duyệt", Icons.Outlined.Shield, Icons.Filled.Shield, ...)
    data object Users : NavGroup("users", "Người dùng", Icons.Outlined.People, Icons.Filled.People, ...)
    data object More : NavGroup("more", "Thêm", Icons.Outlined.MoreHoriz, Icons.Filled.MoreHoriz, ...)
}
```

### 3.2 Adaptive Layout

- **Màn hình hẹp (< 600dp)**: Bottom Navigation Bar (với badge unread count)
- **Màn hình rộng (≥ 600dp)**: Navigation Rail bên trái + nội dung bên phải
- Dùng `WindowSizeClass` của Material 3 Adaptive

### 3.3 Secondary Navigation (Nội dung & Kiểm duyệt)

Các nhóm có nhiều tab con dùng `SegmentedFilter` hoặc `TabRow`:
- **Nội dung**: Môn học | Kho | Công nghệ | Quét
- **Kiểm duyệt**: Ứng viên | Đánh giá | Cộng đồng | AI Chat

### 3.4 AdminTopBar

```kotlin
@Composable
fun AdminTopBar(
    title: String,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
)
```

- Dùng `TopAppBar` với nền trắng, border dưới 1dp
- Title màu UIT Blue, weight semibold
- Scroll behavior: toolbar thu nhỏ khi scroll (nếu dùng `scrollBehavior`)

---

## 4. Screen: Login

**Files**: `LoginScreen.kt`, `LoginViewModel.kt`, `LoginViewModelTest.kt`

### 4.1 Giao diện

- Header: logo UIT + "DevOrbit Admin" + subtitle "Hệ thống quản lý thực hành Lập trình"
- Trường email: icon envelope + placeholder "Email"
- Trường mật khẩu: icon lock + placeholder "Mật khẩu" + nút eye toggle show/hide
- Checkbox "Ghi nhớ đăng nhập"
- Nút "Đăng nhập" full-width, có loading state
- Link nhỏ "Quên mật khẩu?" (chưa implement — để placeholder)
- Ở dưới: "Phiên bản 1.0.0"

### 4.2 Validation

- Phía client:
  - Email: kiểm tra regex cơ bản (`^\\S+@\\S+\\.\\S+$`)
  - Mật khẩu: không được để trống
- Phía server:
  - 401 → hiển thị lỗi từ server
  - Timeout → "Mất kết nối đến máy chủ"

### 4.3 State Machine

```
Idle → Loading (gọi API)
    ├── Success → chuyển sang Dashboard
    └── Error → hiển thị lỗi, quay về Idle
```

### 4.4 Session Restore

- Kiểm tra token có sẵn trong DataStore
- Nếu có → tự động navigate sang Dashboard
- Nếu không → hiển thị màn hình login

---

## 5. Screen: Dashboard (Điều hành)

**Files**: `DashboardScreen.kt`, `DashboardViewModel.kt`, `DashboardViewModelTest.kt`

### 5.1 Header

- Avatar tròn (initial chữ cái đầu của admin)
- "Xin chào, {Tên admin}"
- Trạng thái API: Connected (xanh) / Syncing (vàng) / Disconnected (đỏ)
- Nút "Đồng bộ" (reload)

### 5.2 4 Metric Cards

| Metric | Icon | Màu | API Field |
|--------|------|-----|-----------|
| Tổng sinh viên | People | UIT Blue | `totalStudents` |
| Môn học | MenuBook | Xanh lá | `totalCourses` |
| Kho mã nguồn | Code | Cam | `totalRepos` |
| Đang chờ duyệt | HourglassBottom | Đỏ | `pendingCandidates` |

- Mỗi card có border, bo góc 12dp
- Số to (24sp bold), title nhỏ (14sp medium)
- `MetricSkeleton` hiển thị khi đang loading

### 5.3 Priority Items

- Danh sách các mục cần xử lý gấp
- Mỗi item có: SeverityIndicator (màu) + label + entity type + thời gian
- Data lấy từ: pending candidates, chờ review, tin nhắn chưa duyệt
- Nếu không có gì → Empty state "Mọi thứ đã ổn"

### 5.4 Activity Timeline

- Timeline dọc với các sự kiện gần đây (7 ngày)
- Mỗi item: icon + title + subtitle + time
- Data lấy từ: scan logs, reviews gần đây, actions của admin
- Nếu không có → ẩn section

### 5.5 Quick Actions

- Lưới 4 cột action nhanh:
  - Thêm môn học → navigate Courses
  - Quét kho → navigate GitHub Scan
  - Duyệt ứng viên → navigate Candidates
  - Thông báo (có badge) → navigate Notifications

### 5.6 Data Fetching

`loadDashboard()` gọi **7 API song song** trong `coroutineScope`:

```kotlin
async { getStats() }              // Primary — nếu fail thì show error
async { getPendingCandidates() }  // Supplementary — getOrDefault(emptyList())
async { getRepoReviews() }
async { getCommunityMessages() }
async { getScanLogs() }
async { getStudents() }
async { getUnreadCount() }
```

### 5.7 Sealed Class State

```kotlin
sealed class DashboardStatus {
    data object InitialLoading : DashboardStatus()
    data class Loading(val cachedData: DashboardData? = null) : DashboardStatus()
    data class Error(val message: String) : DashboardStatus()
    data class Success(val data: DashboardData) : DashboardStatus()
}

data class DashboardUiState(
    val status: DashboardStatus = DashboardStatus.InitialLoading,
    val unreadCount: Int = 0,
    val adminName: String = "Quản trị viên",
    val lastSyncTime: String? = null,
    val apiStatus: ApiStatus = ApiStatus.Connected,
)
```

---

## 6. Screen: Courses (Môn học)

**Files**: `CoursesScreen.kt`, `CoursesViewModel.kt`

### 6.1 Danh sách môn học

- **Search bar** ở trên cùng: debounce 300ms, filter theo `q` param
- **Filter chips**: Tất cả | Lập trình | Cơ sở dữ liệu | Web | AI | Khác
- Danh sách dạng `LazyColumn`, mỗi item:
  - Icon (folder/library)
  - Tên môn học
  - Mã môn học (màu xám nhạt)
  - Số kho, số sinh viên (các badge nhỏ)
  - Nút "Sửa" icon
- **Skeleton loading**: `ListSkeleton(itemCount = 5)`
- **Empty state**: "Không tìm thấy môn học" + nút "Thêm môn học"
- **Error state**: icon lỗi + message + nút "Thử lại"

### 6.2 Thêm/Sửa môn học (CourseEditorDialog)

- **Dialog full-screen** (`Dialog` composable với fillMaxSize)
- Form fields:
  - Tên môn học (TextField, required)
  - Mã môn học (TextField, required)
  - Loại (dropdown: Lập trình, CSDL, Web, AI, Khác)
  - Mô tả (TextField multiline)
  - Giảng viên (TextField)
  - Số tín chỉ (TextField số)
  - Trạng thái (Active/Inactive switch)
- Nút "Lưu" (primary button, loading state chống double-submit)
- Nút "Huỷ" (secondary button)

### 6.3 Xoá môn học

- Dùng `ConfirmationSheet`:
  - Title: "Xoá môn học"
  - Message: "Bạn có chắc muốn xoá môn học {tên}? Hành động này không thể hoàn tác."
  - Nút xác nhận màu đỏ (`isDestructive = true`)
  - Loading state khi đang xoá

### 6.4 Paging

- `page` + `pageSize` query params
- Load more khi scroll gần cuối (tự động gọi page tiếp theo)
- Hiển thị loading indicator ở cuối danh sách

---

## 7. Screen: Candidates (Ứng viên)

**Files**: `CandidatesScreen.kt`, `CandidatesViewModel.kt`

### 7.1 Danh sách ứng viên

- `SegmentedFilter`: Tất cả | Chờ duyệt | Đã duyệt | Từ chối
- Search bar + dropdown sort (Mới nhất / Cũ nhất / Tên A-Z)
- Mỗi item card:
  - Tên repo + link GitHub (click được)
  - Tên sinh viên
  - Ngày nộp
  - Trạng thái badge (StatusBadge)
  - Nút action (Duyệt / Từ chối / Đã xử lý)
- Swipe để duyệt/từ chối nhanh (tuỳ chọn)

### 7.2 Bottom Sheet Chi tiết

- `ModalBottomSheet`:
  - Thông tin repo (tên, URL, mô tả)
  - Thông tin sinh viên (tên, email, lớp)
  - Lịch sử thay đổi trạng thái
  - Nút Duyệt / Từ chối

### 7.3 Approve/Reject

- Approve: gọi API `approveCandidate` → show snackbar "Đã duyệt {repo}" → refresh list
- Reject: gọi API `rejectCandidate` → show snackbar "Đã từ chối {repo}" → refresh list
- Cả hai đều có loading state chống double-submit

---

## 8. Screen: Approved Repos (Kho đã duyệt)

**Files**: `ReposScreen.kt`, `ReposViewModel.kt`

### 8.1 Danh sách

- Search + lọc theo môn học
- Mỗi item card:
  - Tên kho + link GitHub
  - Sinh viên sở hữu
  - Ngày duyệt
  - Nút Edit / Delete / Sync / Evaluate

### 8.2 Edit → RepoEditorBottomSheet

- Modal bottom sheet với form:
  - Tên hiển thị
  - Mô tả
  - Môn học (dropdown)
  - Cho phép fork (toggle)
- Nút Lưu / Huỷ

### 8.3 Delete → ConfirmationSheet

- Xác nhận + loading state
- Sau xoá → snackbar + refresh list

### 8.4 Sync & Evaluate

- **Sync**: gọi API sync → snackbar "Đã đồng bộ"
- **Evaluate**: navigate sang màn hình đánh giá

---

## 9. Screen: GitHub Scan (Quét GitHub)

**Files**: `GithubScreen.kt`, `GithubViewModel.kt`

### 9.1 Giao diện

- **Course Selector**: dropdown chọn môn học (load từ API)
- **Search Query**: TextField "Nhập từ khoá tìm kiếm..."
- Nút chính: "Quét GitHub" (primary button)
- Nút phụ: "Quét tất cả" (secondary button — không cần query)

### 9.2 Scan

- Gọi API `scanGithub(courseId, query)`
- Loading: spinner + "Đang quét GitHub..."
- Success: snackbar "Đã quét {n} kho mới"
- Error: "Không thể quét. Vui lòng thử lại."

### 9.3 Scan All

- Gọi API `scanAllGithub(courseId)`
- Loading + success/error handling tương tự
- Dùng cho lần đầu setup

### 9.4 Scan Logs

- Section "Lịch sử quét" ở dưới
- `LazyColumn` các log item:
  - Thời gian
  - Query
  - Số kho tìm thấy
  - Trạng thái (thành công/thất bại)
- Auto-refresh sau khi scan

---

## 10. Screen: Students (Sinh viên)

**Files**: `StudentsScreen.kt`, `StudentsViewModel.kt`

### 10.1 Danh sách

- Search bar với debounce 300ms
- `LazyColumn` mỗi item:
  - Avatar (initial) + tên + email
  - Badge Active/Inactive
  - Số kho đã nộp
- `ListSkeleton` khi loading

### 10.2 Detail Dialog

- Dialog full thông tin:
  - Avatar lớn
  - Tên, email, ngày tham gia
  - Danh sách kho đã nộp
  - Trạng thái Active/Inactive
  - Nút "Vô hiệu hoá" / "Kích hoạt"

### 10.3 Toggle Active

- Dùng `ConfirmationSheet`:
  - Vô hiệu: "Bạn có chắc muốn vô hiệu hoá {tên}?"
  - Kích hoạt: "Bạn có chắc muốn kích hoạt lại {tên}?"
- Sau toggle → snackbar + refresh danh sách

---

## 11. Screen: Moderation - Community (Cộng đồng)

**Files**: `CommunityScreen.kt`, `CommunityViewModel.kt`, `ChatSessionDetailDialog.kt`

### 11.1 Segmented Tabs

- **Tin nhắn**: danh sách tin nhắn cần duyệt
- **Phiên chat**: danh sách chat session

### 11.2 Tin nhắn Tab

- Mỗi item:
  - Avatar sinh viên
  - Nội dung tin nhắn (cắt ngắn 2 dòng)
  - Thời gian
  - Badge trạng thái
  - Nút Xoá (dùng ConfirmationSheet)

### 11.3 Phiên Chat Tab

- Mỗi item:
  - Tên sinh viên + mã lớp
  - Số lượng tin nhắn
  - Thời gian bắt đầu/kết thúc
  - Nút "Xem chi tiết" → ChatSessionDetailDialog

### 11.4 ChatSessionDetailDialog

- Dialog full-screen
- `LazyColumn` các message:
  - User message: bubble trái, nền xám nhạt
  - System response: bubble phải, nền UIT Blue nhạt
  - Code blocks: nền tối với monospace font
- Read-only (chỉ xem, không gửi tin nhắn)
- Nút "Đóng"
- Nút "Xoá phiên" (ConfirmationSheet)

---

## 12. Screen: Reviews (Đánh giá)

**Files**: `ReviewsScreen.kt`, `ReviewsViewModel.kt`

### 12.1 Segmented Tabs

- **Đánh giá môn học**: reviews từ sinh viên về course
- **Đánh giá kho**: reviews từ sinh viên về repo

### 12.2 Course Reviews Tab

- Mỗi item card:
  - Tên sinh viên + avatar
  - Tên môn học
  - Rating (sao) + comment
  - Ngày đánh giá
  - Nút Xoá (ConfirmationSheet)

### 12.3 Repo Reviews Tab

- Tương tự nhưng thay môn học = tên repo
- Nút Xoá (ConfirmationSheet)

---

## 13. Screen: Reports (Báo cáo)

**Files**: `ReportsScreen.kt` (không có ViewModel riêng — dùng `ReportsViewModel` có sẵn)

### 13.1 Segmented Tabs

- **Yêu thích nhất**: top repos theo bookmark count
- **Xem nhiều nhất**: top repos theo view count

### 13.2 Ranked Lists

Mỗi item trong cả hai tab:
```
#1  Tên repo
    👤 Sinh viên
    ⭐ {bookmarkCount} lượt lưu / 👁️ {viewCount} lượt xem
```

- `LazyColumn` với số thứ tự (1, 2, 3...)
- Top 3 có số to màu vàng/cam
- Empty state: "Chưa có dữ liệu báo cáo"

### 13.3 Data Structure

```kotlin
// Tái sử dụng AdminStatsResponse từ API
data class AdminStatsResponse(
    val topFavoritedRepos: List<RepoStatsEntry>,
    val topViewedRepos: List<RepoStatsEntry>,
    // ...
)

data class RepoStatsEntry(
    val repoId: String,
    val repoName: String,
    val studentName: String,
    val bookmarkCount: Long,
    val viewCount: Long,
)
```

---

## 14. Screen: Notifications (Thông báo)

**Files**: `NotificationsScreen.kt`, `NotificationsViewModel.kt`

### 14.1 Danh sách thông báo

- Mỗi item:
  - Icon theo loại (info/warning/success/error)
  - Tiêu đề
  - Nội dung tóm tắt (1 dòng)
  - Thời gian (relative: "2 phút trước", "1 giờ trước"...)
  - Badge "Mới" nếu chưa đọc
  - Nền hơi xanh nếu chưa đọc
- Pull-to-refresh

### 14.2 Actions

- **Đánh dấu đã đọc**: swipe trái hoặc click → mark read
- **Đánh dấu tất cả**: nút ở top bar
- Icon unread count ở bottom nav badge

### 14.3 Empty State

- "Không có thông báo nào" với icon bell

---

## 15. Sửa Lỗi Build & Test

### 15.1 Compilation Errors from Subagents

Khi subagents (AgentFix1-4) sinh code, có các lỗi phổ biến:

| Error | Root Cause | Fix |
|-------|-----------|-----|
| `weight` not available | Composable không nằm trong `ColumnScope` | Wrap function với `ColumnScope.` prefix ở callsite |
| `Unresolved reference: dp` | Thiếu import | Thêm `import androidx.compose.ui.unit.dp` |
| `Unresolved reference: hiltViewModel` | Thiếu import | Thêm `import androidx.hilt.navigation.compose.hiltViewModel` |
| `Unresolved reference: background` | Thiếu import | Thêm `import androidx.compose.foundation.background` |
| `Unresolved reference: Dialog` | Thiếu import | Thêm `import androidx.compose.ui.window.Dialog` |
| `Unresolved reference: stats/isLoading/error` | State field không còn tồn tại | Sửa test dùng sealed class pattern |

### 15.2 DashboardViewModelTest Failures

**Vấn đề**: Test cũ reference `viewModel.state.value.stats`, `.isLoading`, `.error` — các field đã được thay bằng `sealed class DashboardStatus`.

**Fix**:

```kotlin
// Before (hỏng)
assertEquals(stats, viewModel.state.value.stats)
assertFalse(viewModel.state.value.isLoading)

// After (đúng)
val status = viewModel.state.value.status
assertTrue(status is DashboardStatus.Success)
assertEquals(100L, (status as DashboardStatus.Success).data.totalStudents)
```

**Vấn đề 2**: ViewModel gọi 7 API, test chỉ mock `getStats()`. Các API khác không được mock → throw exception.

**Fix**: Tạo helper `mockRepository()` mock tất cả 7 API calls.

---

## 16. Dọn Dẹp

### 16.1 Xoá `_quarantine/`

Thư mục chứa code cũ từ scaffold đầu tiên (theme Obsidian, "Orbital" components):
- `_quarantine/ui/` — DevOrbitAdminApp.kt, Shape.kt
- `_quarantine/screens/` — tutor, subjects, today, profile, studyplan screens
- `_quarantine/components/` — OrbitalLearningMap, MorphingContainer, SignalButton, v.v.
- `_quarantine/design/` — OrbitTheme, OrbitColors, OrbitTypography, v.v.
- `_quarantine/SampleData.kt`, `_quarantine/Models.kt`

Tổng cộng ~30 file, ~10K dòng code cũ.

### 16.2 Xoá AdminLoginScreen/ViewModel

- `AdminLoginScreen.kt` → đã replace bằng `LoginScreen.kt`
- `AdminLoginViewModel.kt` → đã replace bằng `LoginViewModel.kt`
- `AdminLoginViewModelTest.kt` → đã replace bằng `LoginViewModelTest.kt`

### 16.3 Cập nhật AGENTS.md

- Thêm hướng dẫn build Windows: dùng `build_temp.bat` với JAVA_HOME trỏ vào Android Studio JBR

---

## 17. Những Việc Còn Lại

Sẽ cần các PR riêng cho:

| Feature | Mô tả | Ưu tiên |
|---------|-------|---------|
| AI Chat Monitor | Tab thứ 4 của Kiểm duyệt — xem lịch sử chat AI | Trung bình |
| Course Detail Tabs | Màn hình chi tiết môn học với tab Resources/Students/Stats | Trung bình |
| Course Resources CRUD | Quản lý playlists, articles, tutorials | Thấp |
| Course Relationships | Quản lý môn học tiên quyết, song hành | Thấp |
| TechStack Screen | Quản lý công nghệ (ngôn ngữ, framework) | Thấp |
| Notes Screen | Ghi chú nội bộ | Thấp |
| Dark Theme | Chế độ tối | Thấp |
| ViewModel Tests | Unit test cho các ViewModel còn thiếu | Trung bình |
| List-Detail Adaptive | Layout 2 panel cho tablet | Thấp |

---

## 18. Thống Kê

```
68 files changed, 6026 insertions(+), 10815 deletions(-)
```

### Files chính (theo category)

**Core Design System** (9 files mới):
- `core/designsystem/AdminButtons.kt`, `AdminScaffold.kt`, `AdminSearchField.kt`
- `core/designsystem/StatusBadge.kt`, `LoadingSkeleton.kt`, `States.kt`
- `core/designsystem/ConfirmationSheet.kt`, `FilterChipGroup.kt`, `MetricDisplay.kt`

**Screens** (13 files được sửa/viết lại):
- `ui/login/LoginScreen.kt`, `LoginViewModel.kt` (mới)
- `ui/dashboard/DashboardScreen.kt`, `DashboardViewModel.kt` (sửa)
- `ui/courses/CoursesScreen.kt`, `CoursesViewModel.kt` (sửa)
- `ui/candidates/CandidatesScreen.kt`, `CandidatesViewModel.kt` (sửa)
- `ui/repos/ReposScreen.kt`, `ReposViewModel.kt` (sửa)
- `ui/github/GithubScreen.kt`, `GithubViewModel.kt` (sửa)
- `ui/students/StudentsScreen.kt`, `StudentsViewModel.kt` (sửa)
- `ui/community/CommunityScreen.kt`, `CommunityViewModel.kt` (sửa)
- `ui/community/ChatSessionDetailDialog.kt` (sửa)
- `ui/reviews/ReviewsScreen.kt`, `ReviewsViewModel.kt` (sửa)
- `ui/reports/ReportsScreen.kt` (sửa)
- `ui/notifications/NotificationsScreen.kt`, `NotificationsViewModel.kt` (sửa)
- `ui/AdminRoot.kt` (sửa)

**Tests** (2 files sửa + 1 mới):
- `ui/dashboard/DashboardViewModelTest.kt` (sửa — sealed class assertions)
- `ui/login/LoginViewModelTest.kt` (mới)
- `ui/login/AdminLoginViewModelTest.kt` (xoá)

**Build & Config**:
- `build_temp.bat` (mới — wrapper script Windows)
- `AGENTS.md` (sửa — thêm Windows build instructions)

### Dòng thay đổi lớn nhất

| File | Thêm | Xoá |
|------|------|-----|
| `DashboardScreen.kt` | ~290 | ~60 |
| `CoursesScreen.kt` | ~420 | ~50 |
| `CandidatesScreen.kt` | ~350 | ~30 |
| `CommunityScreen.kt` | ~380 | ~40 |
| `AdminRoot.kt` | ~200 | ~100 |
| `AdminScaffold.kt` | ~280 | 0 (mới) |
| `AdminButtons.kt` | ~150 | 0 (mới) |
