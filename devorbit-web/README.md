# DevOrbit Web

Giao diện người dùng cho nền tảng **DevOrbit** — hệ thống quản lý và khám phá mã nguồn học thuật, AI photobooth, lộ trình AI dành cho sinh viên Trường Đại học Công nghệ Thông tin (UIT).

Xây dựng với **React 19**, **TypeScript**, **Vite 6**, **Tailwind CSS 3.4**.

---

## 🔐 Authentication

Hai hệ thống xác thực riêng biệt:

| Vai trò | Trang đăng nhập | Token | Chức năng |
|---------|----------------|-------|-----------|
| **Admin** (Quản trị viên) | `/admin/login` | `devorbit-admin-token` (localStorage) | Quản lý môn học, repository, roadmaps, ghi chú, photobooth frames |
| **Student** (Sinh viên) | `/student/login` | `devorbit-student-token` (localStorage) | Bookmark môn học/repository, sử dụng photobooth |

---

## 🎓 Student Pages (Trang Sinh viên)

### 1. Trang chủ — `/` (HomePage)

Trang giới thiệu chính với các section:
- **Hero**: Banner chào mừng với hiệu ứng parallax, gradient, carousel kể chuyện
- **How It Works**: 3 bước giới thiệu (Khám phá → Kết nối → Hoàn thiện) với hoạt ảnh stagger
- **AI Tutor Section**: Bento grid giới thiệu AI Tutor, tóm tắt tự động, lộ trình học tập
- **Discovery Feed**: Dòng thời gian các repository mới nhất

### 2. Danh sách môn học — `/courses` (CourseListPage)

- Hiển thị toàn bộ môn học dạng lưới (grid) với phân trang (30 môn/trang)
- **Tìm kiếm** real-time theo tên hoặc mã môn học (debounce 200ms)
- Sắp xếp theo số lượng repository (giảm dần)
- Mỗi card hiển thị: mã môn, tên, số tín chỉ, số repository

### 3. Chi tiết môn học — `/courses/:courseId` (CourseDetailPage)

- Thông tin chi tiết: mã môn, tên, mô tả, số tín chỉ, giờ lý thuyết/thực hành, loại học phần, đơn vị quản lý
- Mã cũ, môn tương đương
- **Ma trận kiến thức**: Grid repository liên quan đến môn học
- **RepoFilterBar**: Lọc repository theo tech stack
- **Bookmark**: Đánh dấu môn học (yêu cầu đăng nhập sinh viên)
- **Chia sẻ**: Nút chia sẻ repository
- **Góc nhìn học thuật**: Card insight học thuật

### 4. Chi tiết repository — `/repos/:repoId` (RepoDetailPage)

- Thông tin repository: tên hiển thị, mô tả, ngôn ngữ chính, tech stacks, số sao GitHub
- Nút **Truy cập mã nguồn** (mở GitHub tab mới)
- **Bookmark** repository
- **AI Tóm tắt tự động**: Card "Phân tích nội dung" — tóm tắt repository bằng AI
- **AI Tutor**: Card "Chiến lược học tập" — lời khuyên học tập từ AI
- Làm sạch nội dung AI (emoji, markdown) trước hiển thị

### 5. Lập kế hoạch học tập — `/knowledge-graph` (GalaxyPage)

Trang tương tác cho phép sinh viên lập kế hoạch học tập 4 năm tại UIT:

- **KanbanBoard** (dùng `@dnd-kit`): Sắp xếp môn học theo từng học kỳ bằng kéo-thả
- Môn học cố định được gán sẵn theo chương trình đào tạo
- **Môn tự chọn**: Chọn từ danh sách các nhóm (Cơ sở ngành, Chuyên ngành)
- Kiểm tra **điều kiện tiên quyết** khi chọn môn — popup cảnh báo nếu thiếu
- **Theo dõi tín chỉ**: Hiển thị tổng tín chỉ cơ sở ngành / chuyên ngành
- **Trình độ tiếng Anh**: Bộ lọc đầu vào (AV1/AV2/AV3/đã qua)
- **Lộ trình tốt nghiệp** (SE505, SE506, SE507)

### 6. Photobooth — `/photobooth` (PhotoboothPage)

Tạo ảnh kỷ niệm với các khung hình đặc biệt:

- **FrameSelector**: Chọn khung ảnh từ danh sách (được load từ API hoặc định nghĩa sẵn)
- **PhotoUploadSection**: Upload ảnh vào từng slot với:
  - Điều chỉnh vị trí (kéo-thả offset dx/dy)
  - Zoom ảnh
  - **Bộ lọc ảnh**: 8 preset (normal, sepia, vintage, cool, warm, vivid, soft, mono)
- **PreviewCanvas**: Render canvas preview thời gian thực
- **PhotoCompositor**: Canvas engine ghép ảnh theo slot + overlay + filter
- **DownloadSection**: Tải ảnh đã ghép dạng PNG

### 7. Trang đăng nhập sinh viên — `/student/login` (StudentLoginPage)

- Đăng nhập / Đăng ký tài khoản sinh viên
- Form: mã số sinh viên, họ tên, email, mật khẩu

### 8. Trang bookmark — `/student/bookmarks` (StudentBookmarksPage)

- Xem danh sách các môn học và repository đã đánh dấu
- Xoá bookmark
- Đăng xuất

---

## 🛠️ Admin Pages (Trang Quản trị)

Yêu cầu đăng nhập admin. Redirect về `/admin/login` nếu chưa xác thực.

### 1. Bảng điều khiển — `/admin` (AdminDashboardPage)

Menu điều hướng đến tất cả chức năng admin:
- Môn học | Scan GitHub | Ứng viên | Repository đã duyệt
- Roadmaps | Mối quan hệ | Ghi chú | Khung Photobooth

### 2. Quản lý môn học — `/admin/courses` (AdminCoursesPage)

- Danh sách môn học dạng bảng
- **Tạo môn học**: Dialog form với mã, tên, tín chỉ, giờ lý thuyết/thực hành, loại học phần, mô tả
- **Sửa môn học**: Mở dialog với dữ liệu hiện tại (fetch chi tiết từ API)
- **Hủy kích hoạt**: Xóa môn học (soft delete)

### 3. Scan GitHub — `/admin/scan` (AdminScanPage)

- **Scan đơn**: Chọn môn học + query → tìm repository GitHub liên quan
- **Scan toàn bộ**: Quét tất cả môn học tự động (chạy nền 2-3 phút)
- **Terminal UI**: Hiển thị log real-time khi bulk scanning (polling 2s)
- Kết quả hiển thị danh sách ứng viên với: owner/name, status, course code, stars, description

### 4. Ứng viên Repository — `/admin/candidates` (AdminCandidatesPage)

- Lọc theo người phụ trách (Bảo/Bắc/An) với số lượng pending
- Lọc theo mã môn học
- **CandidateTable**: Bảng ứng viên với các thao tác:
  - **Approve**: Phê duyệt → chuyển thành repository chính thức
  - **Reject**: Từ chối ứng viên
- Hiển thị chi tiết: stars, forks, topics, last pushed, readme excerpt
- Phân công reviewer, ghi chú duyệt

### 5. Repository đã duyệt — `/admin/repos` (AdminReposPage)

- Danh sách repository đã được phê duyệt
- **ApprovedRepoTable**: Bảng hiển thị thông tin repository
- **Sửa repository**: Modal form với:
  - Tên hiển thị, mô tả, URL GitHub
  - Ngôn ngữ chính, stars, tech stacks
  - Gán môn học (dropdown)
  - Trạng thái hoạt động (checkbox)
- **Hủy kích hoạt**: Xóa repository

### 6. Lộ trình học tập (Roadmaps) — `/admin/roadmaps` (AdminRoadmapsPage)

Quản lý lộ trình học tập 3 cấp:

| Cấp | Mô tả |
|-----|-------|
| **Roadmap** | Lộ trình tổng thể (title, description, student, public/private) |
| **Phase** | Giai đoạn trong roadmap (title, description, sort order) |
| **Item** | Mục trong phase (targetType: COURSE/REPO, targetId, note, sort order) |

- Expand/collapse từng roadmap và phase
- CRUD đầy đủ cho cả 3 cấp
- Mỗi roadmap hiển thị: title, student code/name, trạng thái public, ngày cập nhật

### 7. Mối quan hệ môn học — `/admin/relationships` (AdminRelationshipsPage)

- Quản lý 3 loại quan hệ:
  - **PREREQUISITE** (Tiên quyết): Môn A cần hoàn thành môn B trước
  - **COMPLEMENTARY** (Bổ sung): Môn A và B bổ trợ cho nhau
  - **COREQUISITE** (Song hành): Môn A và B học cùng kỳ
- Bảng hiển thị: mã môn → loại quan hệ → mã môn liên quan
- Thêm/Xoá quan hệ qua dialog chọn dropdown

### 8. Ghi chú sinh viên — `/admin/notes` (AdminNotesPage)

- Bảng danh sách ghi chú của sinh viên
- Hiển thị: tiêu đề, mã sinh viên, đối tượng (COURSE/REPO/NONE), số snippet code
- **NoteDetailDialog**: Xem chi tiết ghi chú + code snippets
- Xoá ghi chú

### 9. Quản lý khung Photobooth — `/admin/photobooth-frames` (AdminPhotoboothFramesPage)

- Danh sách khung ảnh dạng card grid
- **Tải khung mới**: UploadDialog — nhập tên, số lượng ảnh (1/2/3/4/6), upload ảnh nền
- **Sửa tên**: Click inline edit
- **Slot Editor**: Canvas editor để chỉnh vị trí slot:
  - Kéo-thả thủ công (click 2 điểm để tạo slot)
  - **Auto Detect**: Flood fill tự động phát hiện vùng trong suốt trên ảnh PNG
  - Chỉnh sửa thông số: x, y, width, height, borderRadius
  - Màu sắc phân biệt từng slot
- **Upload ảnh overlay**: Upload ảnh nền khung
- **Xoá khung**

---

## 🤖 AI Features (Tính năng AI)

### AI Summary & Advice

- Gọi API `GET /api/ai/repo/:repoId/summary` — Tóm tắt repository bằng AI
- Gọi API `GET /api/ai/repo/:repoId/advice` — Lời khuyên học tập từ AI Tutor
- **contentCleaner**: Làm sạch markdown, emoji, heading khỏi output AI trước hiển thị

### AI Knowledge Graph Query

- Hook `useAiGraphQuery` — Mutation tìm kiếm đồ thị tri thức bằng ngôn ngữ tự nhiên
- POST `/api/ai/knowledge-graph/query` — Backend parse câu hỏi với pattern matching

### AI Roadmap Generator

- Hook `useAiRoadmap` — Sinh lộ trình học tập cá nhân hoá
- POST `/api/ai/generate-roadmap` — Scenario Engine dựa trên knowledge graph + curriculum rules
- Input: `learningGoals`, `careerPath`
- Output: summary, recommended courses, graduation tracks, elective pools

---

## 🧩 Components & Hỗ trợ

### Motion Components (`/motion` — re-exported)

Thư viện animation dùng Framer Motion với các component:
- `FadeReveal`, `BlurReveal`, `StaggerReveal`, `StaggerItem`
- `SectionTransition` (atmosphere: glow/deep/light)
- `ParallaxLayer`, `ScrollProgressIndicator`

### Theme (Permanent Dark Mode)

- Theme là dark mode cố định
- `theme.tsx` là stub — không còn toggle light/dark

### ParticleNetwork

Component nền particle network dạng canvas.

### ErrorBoundary

Bọc toàn bộ ứng dụng để bắt lỗi React.

### Layout

Layout chính với Navigation + Main content + Footer.

---

## 🌐 API Integration

- **apiGet/apiPost/apiPut/apiDelete**: Public API
- **apiStudentGet/apiStudentPost/apiStudentDelete**: Sinh viên (token từ localStorage)
- **apiAdminGet/apiAdminPost/apiAdminPut/apiAdminDelete**: Admin (token truyền vào)
- **apiUpload**: Upload file (FormData)
- Cache control: GET requests dùng `must-revalidate`
- Normalize response: techStacks tự động chuyển thành string array

### Custom Hooks

| Hook | Chức năng |
|------|-----------|
| `useKnowledgeGraph` | React Query — fetch đồ thị tri thức (5min stale, 30min GC) |
| `useCourseList` | React Query — fetch danh sách môn học |
| `useAiGraphQuery` | Mutation — query đồ thị tri thức bằng NLP |
| `useAiRoadmap` | Mutation — sinh lộ trình học tập cá nhân |
| `useMouseParallax` | Hiệu ứng parallax theo chuột |
| `useRequireAuth` | Redirect nếu chưa đăng nhập admin |
| `useApiFetch` | Generic fetch với loading/error state |

### API Endpoints Used

```
GET    /api/courses                                  — Danh sách môn học
GET    /api/courses/:courseId                        — Chi tiết môn học
GET    /api/courses/graph                            — Đồ thị tri thức
GET    /api/repos/:repoId                            — Chi tiết repository
GET    /api/ai/repo/:repoId/summary                  — AI tóm tắt
GET    /api/ai/repo/:repoId/advice                   — AI lời khuyên
POST   /api/ai/knowledge-graph/query                 — AI query đồ thị
POST   /api/ai/generate-roadmap                      — AI sinh lộ trình
POST   /api/admin/auth/login                         — Admin đăng nhập
POST   /api/admin/courses                            — Tạo môn học
PUT    /api/admin/courses/:id                        — Sửa môn học
DELETE /api/admin/courses/:id                        — Hủy kích hoạt môn học
POST   /api/admin/github/scan                        — Scan GitHub đơn
POST   /api/admin/github/scan-all                    — Scan toàn bộ
GET    /api/admin/github/scan-logs                   — Log scan
GET    /api/admin/repo-candidates                    — DS ứng viên
GET    /api/admin/repo-candidates/stats              — Thống kê reviewer
POST   /api/admin/repo-candidates/:id/approve        — Duyệt ứng viên
POST   /api/admin/repo-candidates/:id/reject          — Từ chối ứng viên
GET    /api/admin/repos                              — DS repo đã duyệt
PUT    /api/admin/repos/:id                          — Sửa repo
DELETE /api/admin/repos/:id                          — Xoá repo
CRUD   /api/admin/roadmaps                           — Roadmaps
CRUD   /api/admin/roadmaps/phases                    — Phases
CRUD   /api/admin/roadmaps/items                     — Items
GET    /api/admin/courses/relationships              — Quan hệ môn học
POST   /api/admin/courses/relationships              — Tạo quan hệ
DELETE /api/admin/courses/relationships/:id          — Xoá quan hệ
GET    /api/admin/notes                              — DS ghi chú
DELETE /api/admin/notes/:id                          — Xoá ghi chú
CRUD   /api/photobooth/frames                        — Frames photobooth
POST   /api/student/login                            — SV đăng nhập
POST   /api/student/register                         — SV đăng ký
GET    /api/student/bookmarks                        — DS bookmark
POST   /api/student/bookmarks                        — Thêm bookmark
DELETE /api/student/bookmarks/:id                    — Xoá bookmark
```

---

## 🧰 Tech Stack

| Layer | Công nghệ |
|-------|-----------|
| Framework | React 19 |
| Language | TypeScript 5.7+ (strict mode) |
| Build | Vite 6 |
| Styling | Tailwind CSS 3.4 |
| Routing | React Router DOM 7 (lazy-loaded pages) |
| Server State | TanStack React Query 5 |
| Client State | Zustand 5 |
| Animation | Framer Motion 12 |
| Icons | @phosphor-icons/react |
| Drag & Drop | @dnd-kit (core + sortable + utilities) |
| Canvas | PhotoCompositor (custom) |

---

## 📁 Project Structure

```
src/
├── pages/
│   ├── admin/           # 9 admin pages
│   └── student/         # 8 student pages (incl. knowledge-graph/)
├── components/
│   ├── admin/           # Admin components (tables, dialogs, forms)
│   ├── student/         # Student components (cards, KanbanBoard, graphs)
│   ├── photobooth/      # Photobooth components
│   └── ui/              # Shared UI components
├── hooks/               # Custom React hooks
├── lib/
│   ├── api.ts           # API client
│   ├── auth.ts          # Authentication helpers
│   ├── frames/          # Frame definitions & service
│   ├── photoCompositor.ts   # Canvas compositing engine
│   ├── photoFilters.ts      # 8 photo filter presets
│   ├── contentCleaner.ts    # AI content sanitization
│   ├── colors.ts            # Color utilities
│   ├── theme.tsx            # Dark mode stub
│   └── hooks.ts             # useRequireAuth, useApiFetch
└── types/
    ├── api.ts           # All API types
    └── frames.ts        # Frame types
```
