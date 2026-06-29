# DevOrbit — Slide Báo Cáo

---

## SLIDE 1 — Tiêu đề

**DevOrbit — Hệ thống quản lý kho lưu trữ môn học và hỗ trợ học tập**

- Sinh viên thực hiện: [tên nhóm]
- Đề tài: Ứng dụng Android cho nền tảng quản lý kho lưu trữ môn học
- Giảng viên hướng dẫn: [tên thầy]
- Ngày báo cáo: 28/06/2026

---

## SLIDE 2 — Mục tiêu đề tài

- Xây dựng nền tảng giúp sinh viên UIT tìm kiếm, duyệt kho lưu trữ mã nguồn môn học
- Hỗ trợ quản trị viên quản lý môn học, kho lưu trữ, đánh giá từ cộng đồng
- Tích hợp AI Tutor hỗ trợ trả lời câu hỏi môn học theo thời gian thực
- Cho phép sinh viên lên kế hoạch học tập cá nhân và nhóm
- Cộng đồng trực tuyến để trao đổi, thảo luận

---

## SLIDE 3 — Tổng quan kiến trúc hệ thống

Hệ thống gồm 3 thành phần chính:

| Thành phần | Mô tả |
|---|---|
| `devorbit-api/` | Backend Spring Boot (Java 21), REST API, WebSocket, AI |
| `devorbit-admin/` | Ứng dụng Android quản trị — quản lý môn học, kho, sinh viên |
| `devorbit-mobile/` | Ứng dụng Android sinh viên — duyệt kho, AI Tutor, cộng đồng |

- Hai app Android independent, chia sẻ chung một backend
- Frontend web (React) cũng tồn tại nhưng không trọng tâm báo cáo hôm nay

---

## SLIDE 4 — Backend (`devorbit-api/`)

- Framework: Spring Boot 3.5.4, Java 21
- Database: Supabase (PostgreSQL), Supabase Auth
- WebSocket: SockJS + STOMP cho chat cộng đồng
- AI: Tích hợp OpenAI-compatible API (deepseek-v4-flash) cho AI Tutor và tóm tắt kho

Tổng số controller: 36

| Nhóm | Số lượng | Vai trò |
|---|---|---|
| Admin-only | 17 | Quản lý từ phía admin |
| Student-only | 10 | Chức năng sinh viên |
| Shared/Public | 9 | Cả hai app đều dùng hoặc chưa dùng |

---

## SLIDE 5 — Ứng dụng Admin (`devorbit-admin/`)

**Vai trò:** Dashboard quản trị cho admin

Các chức năng chính:
- **Dashboard** — Xem thống kê tổng quan (tổng sinh viên, môn học, kho lưu trữ)
- **Quản lý sinh viên** — Danh sách, tìm kiếm, kích hoạt/vô hiệu hóa
- **Quản lý môn học** — CRUD môn học, resources (tutorial, YouTube, bài viết), mối quan hệ môn học
- **Quản lý kho lưu trữ** — Danh sách, chỉnh sửa, xóa, đồng bộ GitHub, đánh giá AI
- **Duyệt đề xuất kho** — Admin duyệt/reject đề xuất kho từ sinh viên
- **Quản lý đánh giá** — Xem và xóa đánh giá kho/môn học
- **GitHub Automation** — Quét GitHub tự động, duyệt tự động kho
- **Cộng đồng** — Quản lý kênh chat, tin nhắn
- **Công nghệ** — CRUD tag tech stack
- **Ghi chú, Thông báo, Báo cáo**

---

## SLIDE 6 — Ứng dụng Mobile (`devorbit-mobile/`)

**Vai trò:** Ứng dụng dành cho sinh viên

Các tab chính (bottom nav):

| Tab | Chức năng |
|---|---|
| Hôm nay | Trang chủ, streak học tập, gợi ý AI, nhiệm vụ hôm nay |
| Môn học | Danh sách môn học, đồ thị quan hệ, chi tiết kho lưu trữ |
| AI Tutor | Chat AI hỏi đáp môn học theo thời gian thực |
| Kế hoạch | Quản lý nhiệm vụ cá nhân, kế hoạch nhóm |
| Cộng đồng | Chat kênh cộng đồng, xem thành viên online |

Phụ: Thông báo, Hồ sơ sinh viên, Chi tiết kho lưu trữ (31KB — màn hình lớn nhất)

---

## SLIDE 7 — Kiến trúc kỹ thuật (cả hai app)

| Thành phần | Thư viện |
|---|---|
| Ngôn ngữ | Kotlin 2.0.21, JVM target 17 |
| UI | Jetpack Compose (BOM 2024.11.00), Material 3 |
| DI | Dagger Hilt 2.51.1 |
| Networking | Retrofit 2.11.0 + Gson + OkHttp 4.12.0 |
| Async | Kotlin Coroutines 1.9.0 + Flow |
| Lưu trữ | DataStore Preferences 1.1.1 |
| Navigation | Navigation Compose 2.8.4 |
| Android SDK | compileSdk 35, minSdk 26, targetSdk 35 |
| Build | AGP 8.13.2, Gradle 8.13 |

**Mobile app bổ sung:**
- Room database (`devorbit_db`, version 11) — 6 entities, 6 DAOs
- STOMP WebSocket client cho chat cộng đồng
- 16 ViewModels
- 3 Hilt modules: `DataModule`, `RepositoryModule`, `CommunityModule`

**Admin app:**
- Không có local database (stateless, gọi API trực tiếp)
- 1 Hilt module: `AdminModule`
- Dark theme "Obsidian"

---

## SLIDE 8 — Room Database (Mobile app)

| Entity | Bảng | Chức năng |
|---|---|---|
| `CourseEntity` | courses | Lưu thông tin môn học |
| `RepoEntity` | repos | Lưu thông tin kho lưu trữ |
| `CourseRelationshipEntity` | relationships | Mối quan hệ tiền quyết/môn liên quan |
| `DailyActivityEntity` | daily_activities | Hoạt động học tập hàng ngày |
| `SemesterCourseEntity` | semester_courses | Môn học theo học kỳ |
| `TechStackEntity` | tech_stacks | Tag công nghệ |

- Phiên bản hiện tại: v11 (đã qua nhiều migration)
- DAOs trả về `Flow` để UI tự cập nhật khi dữ liệu thay đổi

---

## SLIDE 9 — AI Tutor & Tính năng AI

**AI Tutor (Student app):**
- Hỏi đáp môn học qua chat
- Streaming SSE (Server-Sent Events) — câu trả lời hiển thị từng phần theo thời gian thực
- Đồng thời hỗ trợ chế độ đồng bộ (không streaming)
- Endpoint: `POST /api/ai/subject-qa/stream` (streaming), `POST /api/ai/subject-qa/query` (đồng bộ)

**Các chức năng AI khác:**
- Tóm tắt kho lưu trữ bằng AI: `GET /api/ai/repo/{id}/summary`
- Gợi ý cải thiện kho: `GET /api/ai/repo/{id}/advice`
- Truy vấn đồ thị kiến thức: `POST /api/ai/knowledge-graph/query`
- Tạo lộ trình học tập: `POST /api/ai/generate-roadmap`

Backend sử dụng RAG pipeline (Retrieval-Augmented Generation) với model `deepseek-v4-flash`

---

## SLIDE 10 — Cộng đồng & Realtime

**Kiến trúc WebSocket:**
- Backend đăng ký SockJS tại `/ws/community` (`WebSocketConfig`)
- Mobile client kết nối qua `/ws/community/websocket` (transport URL)
- Xác thực: JWT gửi qua STOMP `CONNECT` frame (`Authorization: Bearer {token}`), không phải qua URL handshake
- Giao thức: STOMP frames (CONNECT, SUBSCRIBE, SEND, MESSAGE)

**Tính năng:**
- Danh sách kênh chat
- Chat tin nhắn theo kênh (phân trang)
- Upload ảnh (multipart)
- Xem thành viên đang online
- Auto-reconnect với exponential backoff khi mất kết nối

---

## SLIDE 11 — Bảng tổng hợp API Endpoints

Tổng số endpoint chính:

| Nhóm | Số endpoint | Ví dụ |
|---|---|---|
| Auth (cả hai) | ~10 | `/api/student/login`, `/api/auth/refresh` |
| Khóa học & Kho | ~15 | `/api/courses`, `/api/repos/{id}` |
| AI | 6 | `/api/ai/subject-qa/stream` |
| Cộng đồng | ~5 | `/api/student/community/channels/{id}/messages` |
| Kế hoạch nhóm | ~17 | `/api/student/group-plans` |
| Nhiệm vụ cá nhân | 5 | `/api/student/tasks` |
| Quản trị (admin) | ~50 | `/api/admin/courses`, `/api/admin/repos` |
| Khác (thông báo, bookmark, review) | ~15 | |

---

## SLIDE 12 — Build & Chạy

**Yêu cầu:**
- Java 17 (`JAVA_HOME`)
- Android SDK 35 (`ANDROID_HOME`)
- Gradle 8.13 (qua wrapper)

**Lệnh build:**
```bash
# Cả hai app đều dùng
./gradlew compileDebugKotlin     # Kiểm tra biên dịch
./gradlew testDebugUnitTest      # Chạy unit test
./gradlew assembleDebug          # Build APK debug
./gradlew assembleRelease        # Build APK release
```

**Cấu hình môi trường:**
- File `.env` trong từng thư mục app
- `ADMIN_API_BASE_URL` cho admin, `MOBILE_API_BASE_URL` cho mobile
- Giá trị mặc định: `http://10.0.2.2:8080` (localhost qua emulator)

---

## SLIDE 13 — Theme & Thiết kế giao diện

| | Admin | Mobile |
|---|---|---|
| Theme | Dark "Obsidian" — nền tối, xanh UIT | Light "Cosmic" — UIT 2026 Light Edition |
| Navigation | Bottom nav 4 tab + Command Menu | Floating pill nav 5 tab |
| Màu chính | UIT Blue trên nền tối | `PrimaryBlue #0056B3`, `WarmWhite #FAFAFA` |
| Typography | Custom scale | System sans-serif (Inter/Roboto) |
| Truy cập | — | WCAG AA+ (13.5:1 contrast) |

---

## SLIDE 14 — Testing

**Admin app:**
- Unit test: JUnit 4.13.2, Mockito Kotlin 5.4.0
- Chạy: `./gradlew testDebugUnitTest`

**Mobile app:**
- Same + Gson 2.11.0 (cho test STOMP parsing)
- Room migration tests
- Chạy: `./gradlew testDebugUnitTest`

**Release:**
- Admin: ProGuard/R8 enabled (`isMinifyEnabled = true`, `isShrinkResources = true`)
- Mobile: chưa bật minification

---

## SLIDE 15 — Kết quả & Demo

*(Slide này cần điền nội dung thực tế khi demo)*

- Demo đăng nhập admin → xem dashboard thống kê
- Demo login sinh viên → duyệt danh sách môn học → xem chi tiết kho
- Demo AI Tutor: hỏi câu hỏi → xem streaming câu trả lời
- Demo cộng đồng: mở kênh chat → gửi tin nhắn → xem thành viên online
- Demo lên kế hoạch: tạo nhiệm vụ cá nhân → tạo kế hoạch nhóm

---

## SLIDE 16 — Hạn chế & Hướng phát triển

**Hạn chế hiện tại:**
- AI Tutor đôi khi timeout do phụ thuộc vào service bên ngoài (deepseek-v4-flash)
- Community messages endpoint có lúc trả lỗi 500 lần đầu (cần retry)
- Mobile app chưa bật ProGuard/R8 cho release build
- Không có push notification (chỉ poll)

**Hướng phát triển:**
- Push notification qua FCM
- PWA fallback cho web
- Offline-first với Room + sync
- Thêm tính năng collaborative editing cho kế hoạch nhóm
- Cải thiện AI Tutor với context dài hơn

---

## SLIDE 17 — Cảm ơn

**Cảm ơn thầy đã lắng nghe!**

Câu hỏi & Thảo luận
