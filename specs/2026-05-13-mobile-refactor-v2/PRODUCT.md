# DevOrbit Mobile — Product Spec v2

## 1. Vấn đề & Cơ hội

### Hiện trạng

DevOrbit Mobile là Android app (Kotlin + Jetpack Compose) đang ở trạng thái **nửa hoàn thiện, nửa hỗn loạn**:

- App đã có giao diện Compose, navigation, Hilt DI, Room database, Retrofit API client.
- Nhiều **file trùng lặp**: 2 `AcademicRepository`, 2 `DevOrbitDatabase`, 2 `CourseListScreen`, 2 navigation định nghĩa.
- **Hai hệ thống DI song song**: `NetworkModule` (object singleton manual) và `DataModule` (Hilt module).
- **Hai hệ thống navigation**: `Screen.kt` (sealed class, 5 tabs English) và `BottomNavItem.kt` (5 tabs Vietnamese).
- Nhiều screen là stub _"Coming Soon"_: Progress, Execution, Copilot tabs đều trống.
- `repository/AcademicRepository` (gói `repository`) dùng engine classes ảo, `data/repository/AcademicRepository` (gói `data.repository`) dùng DAO + Room flow thực tế — **2 class cùng tên, chức năng khác nhau, gây lỗi compile tiềm tàng**.
- Engine classes (`BurnoutEngine`, `GpaEngine`, `WorkloadEngine`, `TaskBreakdownEngine`, `RecommendationEngine`, `KnowledgeGraphEngine`, `SimulationEngine`, `RiskEngine`) đã có interface nhưng logic đơn giản / chưa hoàn chỉnh.

### Cơ hội

Backend API đã cung cấp nhiều endpoint chưa được khai thác, và các local engine đã viết sẵn cần được hoàn thiện, gắn với UI thực tế.

---

## 2. Vision: "Người bạn đồng hành học tập cho sinh viên UIT"

DevOrbit Mobile là app giúp sinh viên UIT:
- **Khám phá** môn học, kho mã nguồn, tech stack
- **Hiểu** chương trình đào tạo qua Knowledge Graph
- **Học tập** với tài nguyên số (YouTube, articles, tutorials)
- **Tổ chức** việc học với planner, task breakdown
- **Theo dõi** tiến độ, workload, GPA
- **Tương tác** với AI để hỏi về môn học và repo

---

## 3. Navigation & Màn hình

### 6 tabs navigation

| # | Tab | Icon | Mục đích |
|---|---|---|---|
| 🏠 | **Dashboard** | Home | Tổng quan: health ring, today tasks, quick actions |
| 📚 | **Courses** | Book | Danh mục môn học + chi tiết + repos + resources |
| 🧠 | **Knowledge** | Psychology | Knowledge Graph + AI Study Assistant |
| 🔭 | **Explore** | Explore/Compass | Discovery feed, Tech Stack Explorer |
| 📋 | **Plan** | Assignment | Planner, Task Breakdown, Flashcards |
| 👤 | **Profile** | Person | Auth, Bookmarks, Notes, Flashcards, Settings |

### 3.1 Dashboard

| Thành phần | Mô tả |
|---|---|
| Academic Health Ring | % sức khoẻ học tập (dựa trên overdue tasks, workload, consistency) |
| Next Action Card | Gợi ý việc cần làm ngay (từ Recommendations engine) |
| Today's Tasks | Danh sách task hôm nay, có checkbox |
| Quick Actions | 📋 Mở Plan · 📊 Mở Analytics · 💬 Hỏi AI |

### 3.2 Courses

**Course List:**
- Lưới card môn học, search + filter theo học kỳ / loại môn
- Mỗi card: mã MH, tên, tín chỉ, số lượng repo, tech stack tags
- Pull-to-refresh

**Course Detail:**
- Header: tên môn, mã, credits, mô tả
- **Repos tab**: Danh sách repo, filter tech stack, AI summary badge, Open on GitHub
- **Resources tab**: YouTube playlists | Articles | Tutorials — mỗi loại một section, click để xem
- **Relationships tab**: Môn tiên quyết / song hành / hậu quyết (dạng list hoặc mini graph)

**Repo Detail:**
- Thông tin: name, description, stars, language, tech stack list
- AI Summary (`GET /api/ai/repo/{id}/summary`)
- AI Tutor Advice (`GET /api/ai/repo/{id}/advice`)
- Nút Open in GitHub, Share
- Bookmark action

### 3.3 Knowledge

**Knowledge Graph:**
- Force-directed graph: nodes = môn học, links = relationships
- Màu theo học kỳ, kích thước theo impact score
- Pinch-to-zoom, drag, tap node → xem course detail
- Filter toolbar: lọc theo học kỳ, loại môn

**AI Study Assistant:**
- Chat interface, hỏi về môn học, lộ trình, kiến thức
- Backend: `POST /api/ai/knowledge-graph/query`
- Gợi ý câu hỏi nhanh: "Môn này cần học gì trước?", "Môn này có khó không?"
- Hiển thị kết quả dạng text + course links

### 3.4 Explore

**Discovery Feed:**
- Recent repos: danh sách repo mới scan
- Top tech stacks: cloud tag hoặc list

**Tech Stack Explorer:**
- Browse danh sách tech stack
- Chọn tech → xem tất cả repos có tech đó
- Search tech stack

### 3.5 Plan

**Study Planner:**
- Danh sách LearningTasks
- Nút "Generate Plan" → gọi `StudyPlannerEngine` → sinh weekly phases
- Weekly view: progress bar mỗi tuần, danh sách tasks
- Drag task giữa các tuần
- Nút "Balance Workload" → tự động phân bổ lại

**Task Breakdown:**
- Input: mục tiêu text + độ khó (easy/medium/hard)
- Output: danh sách các bước nhỏ có estimate thời gian
- Mỗi bước có thể add vào planner
- Checkbox để mark done → next action tự động chuyển

**GPA Forecast:**
- Dự báo GPA cuối kỳ dựa trên điểm hiện tại + các môn đang học (dùng `GpaEngine`)
- Xem ảnh hưởng từng môn lên GPA

**Flashcards:**
- Tạo bộ flashcards theo môn học
- Mỗi card: mặt trước (câu hỏi) + mặt sau (đáp án)
- Study mode: xem lần lượt, tự đánh giá "Nhớ" / "Quên"
- Thống kê: số card đã thuộc / tổng số, session hôm nay
- Sắp xếp: card dễ quên lên trước (spaced repetition cơ bản)

### 3.6 Profile

**Auth:**
- Đăng ký: nhập MSSV + email + password + OTP -> gọi backend
- Đăng nhập: MSSV + password -> nhận JWT
- Lưu token trong DataStore

**Bookmarks:**
- Danh sách course + repo đã bookmark
- Lưu local Room, sync với backend nếu có API sau này
- Swipe để xoá

**Subject Notes:**
- Tạo ghi chú markdown cho mỗi môn học
- Có thể gắn với repo cụ thể
- Backend entity Note tương tự: title, contentMarkdown, targetType (COURSE/REPO), targetId
- Sync với backend nếu user đã login

**Analytics Hub (trong Profile hoặc tab riêng):**
- **Workload Analysis**: Biểu đồ cột thời gian học theo tuần/môn, cảnh báo quá tải
- **Semester Timeline**: Timeline 16 tuần với milestones + deadlines
- **Burnout Detection**: Phân tích overdue tasks + consistency → cảnh báo

**Settings:**
- Theme (dark/light/system)
- API base URL
- Notification preferences
- Export data
- Clear cache

---

## 4. Tính năng theo Backend API

| # | Feature | API | Status |
|---|---|---|---|
| 1 | Course List + Detail | `GET /api/courses` + `/{id}` | ✅ Refactor |
| 2 | Repos by Course + Tech Filter | `GET /api/courses/{id}/repos?techStack=` | ✅ Refactor |
| 3 | Course Relationships | `GET /api/courses/relationships` + `/course/{id}` | ✅ Refactor |
| 4 | Knowledge Graph | `GET /api/courses/graph` | ✅ Refactor |
| 5 | Resources (tutorials/videos/articles) | `GET /api/courses/{id}/tutorials|videos|articles` | 🆕 Xây UI mới |
| 6 | AI Repo Summary | `GET /api/ai/repo/{id}/summary` | 🆕 |
| 7 | AI Tutor Advice | `GET /api/ai/repo/{id}/advice` | 🆕 |
| 8 | AI Knowledge Graph Query | `POST /api/ai/knowledge-graph/query` | 🆕 |
| 9 | Tech Stack List | `GET /api/tech-stacks` | 🆕 |
| 10 | Discovery Feed | `GET /api/discovery/recent-repos|top-stacks` | 🆕 |
| 11 | Student Auth (OTP + Login + Register) | `POST /api/student/*` | 🆕 |
| 12 | Student Profile | `GET /api/student/me` | 🆕 |

---

## 5. Tính năng Local Engine (không backend)

| # | Feature | Engine | Status |
|---|---|---|---|
| 1 | Study Planner | `StudyPlannerEngine` | ✅ Giữ + cải thiện |
| 2 | Task Breakdown | `TaskBreakdownEngine` | ✅ Giữ + cải thiện |
| 3 | Workload Analysis | `WorkloadEngine` | ✅ Giữ + cải thiện |
| 4 | Burnout Detection | `BurnoutEngine` | ✅ Giữ + cải thiện |
| 5 | GPA Forecast | `GpaEngine` | ✅ Giữ + cải thiện |
| 6 | Risk Assessment | `RiskEngine` | ✅ Giữ + cải thiện |
| 7 | Recommendations | `RecommendationEngine` | ✅ Giữ + cải thiện |
| 8 | Flashcards + Spaced Rep | New engine | 🆕 Xây lại |
| 9 | Subject Notes (local) | Note CRUD | 🆕 Mới |

---

## 6. Behavior Invariants

1. **Offline-first**: Mọi dữ liệu hiển thị đều lấy từ local cache trước, sync background sau.
2. **Không crash khi API fail**: Empty state + retry button.
3. **JWT persistent**: Token lưu trong DataStore.
4. **Graph động**: Knowledge Graph tải từ API, fallback cache.
5. **Engine stateless**: Engine classes là pure functions, không giữ state.
6. **Navigation đồng nhất**: Dùng duy nhất `Screen` sealed class.
7. **DI thống nhất**: Chỉ dùng Hilt.
8. **Mỗi unique name**: Không class trùng tên.
9. **Test coverage > 60%** cho data layer và engine logic.

---

## 7. Non-goals

- AI Roadmap Generator (backlog)
- Learning Roadmap Viewer (backlog)
- Course Comparison (backlog)
- Grade Tracker / What-If GPA (backlog)
- Deadline Calendar (backlog)
- Study Timer
- Study Streaks / XP / Gamification
- Contribution Heatmap
- Digital Twin
- Simulation / Cascade Failure
- Focus Mode / Pomodoro
- Syllabus Parser
- Push Notification (Phase 2)
- iOS version
