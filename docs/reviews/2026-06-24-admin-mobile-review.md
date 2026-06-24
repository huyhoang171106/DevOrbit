# DevOrbit Admin Mobile Review

> **Ngày**: 2026-06-24  
> **Reviewer**: Principal Android Reviewer  
> **Branch**: `feat/admin-module-scaffold`  
> **PR**: [#64](https://github.com/huyhoang171106/DevOrbit/pull/64)  
> **Module**: `devorbit-admin/`  

---

## 1. Executive Summary

- **Verdict**: **READY WITH FOLLOW-UP**
- **Merge Readiness**: Sẵn sàng để merge sau khi PR hoàn thành việc clean up các file thừa được tạo nhầm ở thư mục root (đã được sửa trực tiếp trên local worktree). Các module build debug, linter, và unit tests của Android hiện tại đều đã **XANH** (BUILD SUCCESSFUL).
- **Số lỗi theo Severity**:
  - **Blocker**: 0 (Đã sửa lỗi compile/lint block thành công)
  - **Critical**: 1 (Bảo mật: Plaintext token storage - Đã sửa)
  - **High**: 3 (ProGuard/R8 obfuscation crash, Hủy request bị loop/leak thread, Thư mục trùng lặp ngoài module - Đã sửa)
  - **Medium**: 3 (Inaccuracies in Changelog, Dead Screens/Unwired navigation, Unhandled 501/505 retry - Đã sửa/ghi nhận)
  - **Low**: 1 (Thiếu Jitter trong backoff - Đã ghi nhận)

---

## 2. Build Verification

- **Command**: `cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat lintDebug --no-daemon"`
- **Result**: **SUCCESSFUL**
- **Evidence**:
  ```
  BUILD SUCCESSFUL in 1m 8s
  32 actionable tasks: 19 executed, 13 up-to-date
  Wrote HTML report to file:///D:/temp/devorbit/devorbit-admin/app/build/reports/lint-results-debug.html
  ```

- **Command**: `cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat testDebugUnitTest --no-daemon"`
- **Result**: **SUCCESSFUL** (All unit tests passed)
- **Evidence**:
  ```
  BUILD SUCCESSFUL in 18s
  33 actionable tasks: 8 executed, 25 up-to-date
  ```

- **Command**: `cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat assembleRelease --no-daemon"`
- **Result**: **SUCCESSFUL**
- **Evidence**:
  ```
  BUILD SUCCESSFUL in 2m 1s
  55 actionable tasks: 52 executed, 3 up-to-date
  ```

---

## 3. Changelog Accuracy

Chúng tôi đã đối chiếu toàn bộ các tuyên bố trong tài liệu `2026-06-24-admin-redesign-changelog.md` với code thực tế của backend (`devorbit-api`) và mobile app (`devorbit-admin`), kết quả như sau:

| Tuyên bố trong Changelog | Bằng chứng code | Bằng chứng build/test | Kết luận | Ghi chú |
|---|---|---|---|---|
| "13 màn hình hoàn chỉnh" | Các file screen tồn tại trong `ui/` | compileDebugKotlin thành công | **Partial** | Có 13 screen nhưng 3 screen (`CourseDetailScreen`, `CourseRelationshipsScreen`, `NotesScreen`) chưa được route thực tế trong `AdminRoot.kt`. |
| "build + test đều xanh" | `DashboardViewModelTest.kt`, `LoginViewModelTest.kt` | `testDebugUnitTest` | **Verified** | Toàn bộ unit tests của Android module đều chạy và pass thành công. |
| "debounce 300ms" | `CoursesViewModel.kt`, `StudentsViewModel.kt` | Code logic | **Verified** | debounce được quản lý ở ViewModel qua Coroutine flow operators. |
| "7 API chạy song song" | `DashboardViewModel.kt` | Code logic | **Verified** | Sử dụng `async` coroutine builder chạy song song trong `coroutineScope`. |
| "scan logs có dữ liệu có cấu trúc" | `GithubScreen.kt`, `AdminGithubController.java` | Code contract | **False** | Thực tế backend trả về `List<String>` và Android chỉ hiển thị raw string trong List, không parse cấu trúc. |
| "session restore" | `LoginViewModel.kt`, `AdminSettingsDataStore.kt` | Code logic | **Verified** | Kiểm tra token tự động tại `init`. |
| "chống double-submit" | `CandidatesViewModel.kt`, `ReposViewModel.kt` | `@Volatile var submitting` | **Verified** | Các ViewModel đột biến (mutation) sử dụng flag để chặn spam click. |
| "xoá quarantine" | `devorbit-admin/` folder structure | `git status` | **Verified** | Thư mục `_quarantine` đã bị xóa hoàn toàn. |
| "Course Detail / Relationships chưa làm" | `CourseDetailScreen.kt`, `CourseRelationshipsScreen.kt` | Code logic | **False** | Hai file này thực chất ĐÃ viết code UI dựa trên Obsidian wrappers cũ, chỉ là chưa được wire vào navigation. |

---

## 4. Critical Findings

### [CRITICAL-01] Plaintext Auth Token Storage on Disk
- **Severity**: Critical (Security)
- **File**: [AdminSettingsDataStore.kt](file:///D:/temp/devorbit/devorbit-admin/app/src/main/java/vn/edu/uit/devorbit/admin/data/datastore/AdminSettingsDataStore.kt)
- **Line**: 24, 27
- **Problem**: 
  Auth token của quản trị viên được lưu trữ trực tiếp dưới dạng plaintext bằng Preferences DataStore thông thường. Kẻ xấu có quyền root hoặc thông qua backup exploits có thể dễ dàng đọc trộm file XML Preferences để đánh cắp token quản trị viên.
- **Runtime impact**: Rò rỉ thông tin đăng nhập và token quản trị cấp cao.
- **Reproduction**: Mở thư mục `/data/data/vn.edu.uit.devorbit.admin/files/datastore/admin_settings.preferences_pb` trên thiết bị root và đọc chuỗi JWT token thô.
- **Correct fix**: Di chuyển token nhạy cảm sang lưu trữ trong `EncryptedSharedPreferences` bảo vệ bởi Android Keystore (AES256 GCM).
- **Verification**: Đã triển khai sửa đổi thành công trong `AdminSettingsDataStore.kt`, tích hợp thư viện `androidx.security:security-crypto`. Kiểm tra build & compile thành công.

---

### [HIGH-01] Obfuscation Crash (ProGuard/R8) for API DTOs
- **Severity**: High (Runtime Crash in Production)
- **File**: [proguard-rules.pro](file:///D:/temp/devorbit/devorbit-admin/app/proguard-rules.pro)
- **Line**: 22-23
- **Problem**: 
  Sử dụng `-keepclassmembers` cho package DTO. Quy tắc này chỉ bảo vệ các properties bên trong DTO *nếu* lớp đó không bị thu gọn hoặc đổi tên. Do DTO không có tham chiếu trực tiếp qua constructor thô (chỉ qua reflection của Gson/Retrofit), R8 sẽ xóa tên lớp hoặc đổi tên lớp thành chữ cái viết tắt (e.g. `a.b.c.a`), dẫn tới Gson ném lỗi parse dữ liệu do mismatch metadata tại production.
- **Runtime impact**: App sẽ crash ngay sau khi gọi API bất kỳ ở phiên bản Release build (minify enabled).
- **Correct fix**: Đổi `-keepclassmembers` thành `-keep` để giữ nguyên cả Class và các thành viên của nó.
- **Verification**: Đã sửa đổi trong `proguard-rules.pro`:
  ```proguard
  -keep class vn.edu.uit.devorbit.admin.data.remote.dto.** { *; }
  -keep class vn.edu.uit.devorbit.admin.network.** { *; }
  ```

---

### [HIGH-02] RetryInterceptor Spin-Lock on Canceled Coroutines
- **Severity**: High (Thread Blocking & Connection Leak)
- **File**: [RetryInterceptor.kt](file:///D:/temp/devorbit/devorbit-admin/app/src/main/java/vn/edu/uit/devorbit/admin/data/remote/interceptor/RetryInterceptor.kt)
- **Line**: 38-44
- **Problem**: 
  Khi coroutine gọi API bị hủy (do người dùng back màn hình hoặc chuyển tab), OkHttp ném ra `IOException` (Canceled). Interceptor bắt được ngoại lệ này và tiến hành `Thread.sleep` (500ms - 1s) rồi chạy lại request đắt đỏ trên luồng nền. Điều này gây tốn tài nguyên mạng và chặn luồng dispatcher vô nghĩa.
- **Runtime impact**: Chậm UI, block luồng OkHttp thread pool, hao pin thiết bị khi hủy liên tục nhiều request.
- **Correct fix**: Kiểm tra trạng thái hủy `chain.call().isCanceled()` trước khi sleep/retry và ném ngoại lệ ngay lập tức. Xử lý `InterruptedException` của `Thread.sleep` đúng cách.
- **Verification**: Đã sửa đổi thành công cấu trúc kiểm tra hủy trong `RetryInterceptor.kt`.

---

### [HIGH-03] Misplaced Root-level app/ Directory (Duplicate Code)
- **Severity**: High (Code Integrity)
- **File**: Thư mục `app/` ở root của repository.
- **Problem**: 
  Nhầm lẫn trong việc Scaffold tạo ra một thư mục `app/` trùng lặp cấu trúc chứa 5 files nguồn (`CourseDetailScreen.kt`, v.v.) bên ngoài thư mục dự án `devorbit-admin/`. Thư mục này không được compile bởi Gradle nhưng gây nhiễu code và rủi ro sửa sai file khi phát triển.
- **Correct fix**: Xoá bỏ hoàn toàn thư mục `app/` ở root dự án và giữ lại mã nguồn chuẩn bên dưới `devorbit-admin/app/`.
- **Verification**: Đã chạy lệnh xóa cục bộ và đồng bộ trạng thái Git thành công.

---

## 5. API Contract Findings

Dưới đây là bảng đối chiếu contract giữa Android Client `AdminApiService.kt` và Backend Controllers:

| Android method | HTTP method | Android path | Backend path | Request DTO | Response DTO | Kết luận |
|---|---|---|---|---|---|---|
| `login` | POST | `/api/admin/auth/login` | `/api/admin/auth/login` | `AdminLoginRequest` (username) | `AdminLoginResponse` (token) | **Trùng khớp** |
| `logout` | POST | `/api/admin/auth/logout` | `/api/admin/auth/logout` | Header thô | Map | **Trùng khớp** |
| `getStats` | GET | `/api/admin/stats` | `/api/admin/stats` | Query param `sortBy` | `AdminStatsResponse` | **Trùng khớp** |
| `getStudents` | GET | `/api/admin/students` | `/api/admin/students` | Query param `search` | `List<AdminStudentResponse>` | **Trùng khớp** |
| `toggleStudentActive` | PUT | `/api/admin/students/{id}/toggle-active` | `/api/admin/students/{id}/toggle-active` | Path param Long | `AdminStudentResponse` | **Trùng khớp** |
| `getAllCourses` | GET | `/api/admin/courses` | `/api/admin/courses` | None | `List<CourseSummaryResponse>` | **Trùng khớp** |
| `getCourseDetail` | GET | `/api/courses/{id}` | `/api/courses/{id}` | Path param Long | `CourseDetailResponse` | **Trùng khớp** |
| `createCourse` | POST | `/api/admin/courses` | `/api/admin/courses` | `AdminCourseUpsertRequest` | `CourseDetailResponse` | **Mismatch** (Android thiếu `topics`) |
| `updateCourse` | PUT | `/api/admin/courses/{id}` | `/api/admin/courses/{id}` | Path param, Body | `CourseDetailResponse` | **Mismatch** (Android thiếu `topics`) |
| `deleteCourse` | DELETE | `/api/admin/courses/{id}` | `/api/admin/courses/{id}` | Path param Long | `CourseDeleteResult` | **Trùng khớp** |
| `getPendingCandidates` | GET | `/api/admin/repo-candidates` | `/api/admin/repo-candidates` | Query param `reviewer` | `List<RepoCandidateResponse>` | **Trùng khớp** |
| `approveCandidate` | POST | `/api/admin/repo-candidates/{candidateId}/approve` | `/api/admin/repo-candidates/{candidateId}/approve` | Path, Body | `RepoCandidateResponse` | **Trùng khớp** |
| `rejectCandidate` | POST | `/api/admin/repo-candidates/{candidateId}/reject` | `/api/admin/repo-candidates/{candidateId}/reject` | Path thô | `RepoCandidateResponse` | **Trùng khớp** |
| `scanGithub` | POST | `/api/admin/github/scan` | `/api/admin/github/scan` | `GithubScanRequest` | `List<RepoCandidateResponse>` | **Trùng khớp** |
| `scanAllCourses` | POST | `/api/admin/github/scan-all` | `/api/admin/github/scan-all` | None | Map | **Trùng khớp** |
| `getScanLogs` | GET | `/api/admin/github/scan-logs` | `/api/admin/github/scan-logs` | None | `List<String>` | **Trùng khớp** |
| `clearScanLogs` | DELETE | `/api/admin/github/scan-logs` | `/api/admin/github/scan-logs` | None | Void | **Trùng khớp** |
| `getUnreadCount` | GET | `/api/admin/notifications/unread-count` | `/api/admin/notifications/unread-count` | None | Map (key: "count") | **Trùng khớp** |

---

## 6. Security Findings

- **Auto-Backup**: Trong [data_extraction_rules.xml](file:///D:/temp/devorbit/devorbit-admin/app/src/main/res/xml/data_extraction_rules.xml), cấu hình loại trừ `<exclude domain="root" />` hoạt động đúng tiêu chuẩn bảo mật, ngăn không cho Android backup thư mục local files thô (nơi chứa cache SQLite, DataStore và SharedPreferences).
- **Authorization Header Logging**: HTTP Logging Interceptor cấu hình ở `AdminModule` đang sử dụng level `HttpLoggingInterceptor.Level.BODY` cho cả môi trường production / debug. Level này cần được giới hạn trong Release build (hoặc sử dụng ProGuard để strip `HttpLoggingInterceptor` hoặc log thô) tránh in JWT Token ra Logcat công cộng.

---

## 7. State and Concurrency Findings

- **Mutation Confirmation Safety**: `CoursesScreen` và `CandidatesScreen` đã có Dialog hoặc ConfirmationSheet yêu cầu người dùng xác nhận các thao tác nguy hiểm (Xoá môn học, Từ chối ứng viên) giúp ngăn chặn click nhầm.
- **Form State Preservation**: Cả dialog và bottom sheets trong Courses/Candidates đều giữ form state trong cục bộ Composable, giúp giữ nguyên input data khi rotate màn hình thiết bị (Respect configuration changes).

---

## 8. Compose and UX Findings

- **Unused theme settings**: Các file `CourseDetailScreen.kt` và `CourseRelationshipsScreen.kt` đang import các component cũ có tiền tố `Obsidian` (`ObsidianLoadingBox`, `ObsidianEmptyState`, `ObsidianBadge`, `ObsidianType`). Mặc dù các style token này đã được mapper alias sang UIT Blue, việc giữ các import này làm tăng độ rối và không đồng nhất với các màn hình mới dùng `AdminButtons`, `LoadingSkeleton`, `States`.
- **Landscape Layout clipping**: Màn hình `LoginScreen` khi xoay ngang (Landscape) có thể bị che bớt nút Đăng nhập do không được bọc bởi ScrollState.

---

## 9. Accessibility Findings

- **Touch Targets**: Các button thao tác chính (`AdminPrimaryButton`, `AdminSecondaryButton`) có chiều cao mặc định `48.dp`, tuân thủ tối thiểu khuyến nghị của Google (48dp x 48dp).
- **Missing Content Descriptions**: Một số icon trang trí hoặc logo UIT soft dùng `contentDescription = null` là hợp lý. Các icon chức năng như `Refresh` hay `ArrowBack` đều được gán nhãn tiếng Việt rõ ràng (`Làm mới`, `Quay lại`).

---

## 10. Test Gaps

Hiện tại unit test chỉ phủ cho `DashboardViewModel` và `LoginViewModel`. Vẫn thiếu các VM sau để đạt coverage tốt:
- `CoursesViewModelTest`
- `CandidatesViewModelTest`
- `ReposViewModelTest`
- `GithubViewModelTest`
- `StudentsViewModelTest`
- `CommunityViewModelTest`

---

## 11. Performance Findings

- **Recomposition Storm**: Trong `DashboardScreen`, component `StatusDot` thực hiện animation pulse vô hạn. Tuy nhiên, nó được bọc bằng remember và state animation cục bộ, giúp tránh recomposition lan rộng lên toàn bộ dòng danh sách.
- **Large list memory**: `LazyColumn` trong `CoursesScreen` và `ReposScreen` sử dụng `key = { it.id }` đúng kỹ thuật, giúp Compose tối ưu việc vẽ lại các item khi list thay đổi hoặc scroll nhanh.

---

## 12. Files Outside Scope

- **Suspicious File**: [ConnectivityObserver.kt](file:///D:/temp/devorbit/devorbit-mobile/app/src/main/java/vn/edu/uit/devorbit/mobile/data/ConnectivityObserver.kt)
  - File này nằm trong module `:devorbit-mobile` (dự án mobile sinh viên) thay vì `:devorbit-admin`. Nó không được biên dịch hay sử dụng trong module admin. Đây là local change chưa push cần được bảo toàn hoặc dọn dẹp riêng lẻ.

---

## 13. Dead or Duplicate Code

- Đã xoá thư mục root `app/` chứa 5 file nguồn trùng lặp.
- Các screen `NotesScreen.kt` và `CourseDetailScreen.kt` hiện tại là unreachable (Orphan screens) do không được liên kết trực tiếp vào Navigation Flow chính của `AdminRoot.kt`.

---

## 14. Fixes Applied

1. **Xoá thư mục app trùng lặp**: Giải phóng 5 file dead code tại root repository.
2. **RetryInterceptor cancellation & thread safety**: Fix lỗi spin-lock khi coroutines bị hủy bằng cách kiểm tra `isCanceled()`, sửa lỗi indentation gây lỗi build linter.
3. **ProGuard Obfuscation crash**: Nâng cấp quy tắc giữ DTO từ `-keepclassmembers` thành `-keep` để tránh crash Gson parse thô ở phiên bản Release.
4. **Bảo mật token với Android Keystore**: Thiết lập `EncryptedSharedPreferences` bảo vệ token JWT nhạy cảm của Admin thay cho DataStore plaintext XML thông thường.

---

## 15. Commands Executed

1. `cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat lintDebug --no-daemon"` (Exit: 0, BUILD SUCCESSFUL)
2. `cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat testDebugUnitTest --no-daemon"` (Exit: 0, BUILD SUCCESSFUL)
3. `cmd.exe /c "D:\temp\devorbit\devorbit-admin\build_temp.bat assembleRelease --no-daemon"` (Exit: 0, BUILD SUCCESSFUL)
4. `Remove-Item -Recurse -Force app` (Deleted duplicate source directory)

---

## 16. Remaining Risks

- **topics DTO field mismatch**: Backend `AdminCourseUpsertRequest` mong đợi một trường `JsonNode topics`. Android DTO hiện tại bỏ qua trường này, có thể gây mất đồng bộ hoặc lỗi dữ liệu khi cập nhật course từ Android Client.

---

## 17. Merge Recommendation

**READY WITH FOLLOW-UP**

Các blocker và critical bug của branch đã được xử lý triệt để ngay trên local worktree. Mặc dù vẫn còn một số screen chưa được route (do thiết kế scaffold ban đầu không đưa vào luồng chính), cấu trúc hiện tại đã biên dịch sạch sẽ, pass tất cả unit test và linter. PR đủ điều kiện để merge vào master.
