# DevOrbit — Project Audit

> So sánh giữa mô tả tính năng và code thực tế (Backend: Java Spring Boot; Frontend Web: React + TypeScript; Mobile: Kotlin Compose).  
> Ngày: 2026-05-04

---

## 1. Tổng quan kiến trúc

| Layer | Công nghệ | Mức độ |
|---|---|---|
| Backend API | Spring Boot 4.x, Java 21, Maven | Ổn định |
| Database | PostgreSQL, JPA/Hibernate | Schema cơ bản đã có |
| Frontend Web | React 19, TypeScript, Vite, Tailwind CSS | Cơ bản hoàn chỉnh |
| Mobile | Kotlin, Jetpack Compose | Cơ bản hoàn chỉnh |
| Auth | JWT (admin + student riêng) | Đã có |

---

## 2. Đã có (Implemented)

### 2.1. Hệ thống người dùng & Auth
- [x] Admin users: login, JWT, session
- [x] Student users: register, login, JWT
- [x] Role-based access (admin vs student endpoints)
- [x] Các API controllers: `AdminAuthController`, `StudentAuthController`
- [x] Web UI: `StudentLoginPage`, Admin `LoginPage`
- [x] Mobile UI: `LoginScreen` (login + register)

### 2.2. Vũ trụ Môn học (Subject Orbit) — Cốt lõi
- [x] `courses` table với 67 môn UIT (seed data)
- [x] Course entity: `maMH`, `tenMH`, `soTC`, `lt`, `th`, `loaiMonHoc`
- [x] API: `GET /api/courses` (list), `GET /api/courses/{id}` (detail)
- [x] Web: `CourseListPage`, `CourseDetailPage`
- [x] Mobile: `CourseListScreen`, `CourseDetailScreen`
- [x] Quản lý courses CRUD: `AdminCourseController`

### 2.3. Legacy Repo Finder (Tính năng trọng tâm)
- [x] `GithubScanService`: tìm kiếm repo từ GitHub API
- [x] `repo_candidates` table: luồng duyệt (NEW → APPROVED / REJECTED)
- [x] Candidate review: approve với tech stacks, reject, review note
- [x] `github_repos` table: repo đã duyệt, liên kết với course
- [x] `tech_stacks` + `repo_tech_stacks` join table
- [x] Filter repos by tech stack: `GET /api/courses/{id}/repos?techStack=...`
- [x] Bulk scan all courses (4 query variations mỗi môn)
- [x] Web: `AdminScanPage`, `AdminCandidatesPage`, `AdminReposPage`
- [x] Web components: `RepoCard`, `RepoFilterBar`
- [x] Mobile: `RepoDetailScreen`, `RepoListSection`, `RepoFilterSheet`

### 2.4. Bookmarks
- [x] `student_bookmarks` table
- [x] Bookmark COURSE or REPO
- [x] API: `GET/POST/DELETE /api/student/bookmarks`
- [x] Web: `StudentBookmarksPage`
- [x] Mobile: `BookmarksScreen`

### 2.5. Admin Dashboard
- [x] Pages: Courses, Scan, Candidates, Approved Repos
- [x] Components: `CourseFormDialog`, `CandidateTable`, `ApproveModal`, `ApprovedRepoTable`, `ScanForm`
- [x] Health check: `HealthController`

### 2.6. Infrastructure
- [x] `docker-compose.yml` (db + api + web)
- [x] Dockerfile cho cả API và Web
- [x] OpenAPI/Swagger config
- [x] JWT properties & filter
- [x] GitHub API client config
- [x] Security config (Spring Security)

---

## 3. Còn thiếu (Missing)

### 3.1. Vũ trụ Môn học — Tài nguyên học tập

Nâng cấp Subject Orbit với external resources.

| Item | Mô tả | Gợi ý |
|---|---|---|
| YouTube playlists | Mỗi môn học có danh sách playlist video bài giảng | `course_youtube_playlists` table (URL, title, channel, description) |
| Articles / Papers | Bài báo khoa học, tài liệu tham khảo | `course_articles` table (URL, author, title) |
| Tutorials | Hướng dẫn, document chất lượng cao | `course_tutorials` table (URL, type, title) |
| API endpoints | CRUD cho từng loại tài nguyên | `CourseResourceController` |
| UI sections | Tab/layout hiển thị resources trên CourseDetailPage | Component `CourseResourcesSection` |

### 3.2. Learning Roadmap (Bản đồ lộ trình học tập)

Xây dựng kế hoạch học tập Markdown (phong cách Obsidian/Notion).

| Item | Mô tả | Gợi ý |
|---|---|---|
| Roadmap entity | `learning_roadmaps` table (student_id, title, description, markdown_content, is_public) | Đã có trong `schema-complete.sql` |
| Phases | Phân chia roadmap thành các giai đoạn | `roadmap_phases` table |
| Roadmap items | Ghim course/repo vào phase với note riêng | `roadmap_items` table |
| Markdown editor | Chỉnh sửa nội dung roadmap dạng Markdown | react-markdown + editor (CodeMirror/ProseMirror) |
| API CRUD | Quản lý roadmaps, phases, items | `RoadmapController`, `RoadmapService` |
| Web UI | `RoadmapListPage`, `RoadmapEditorPage`, `RoadmapViewPage` | React components |
| Pin from repo | Nút "Add to Roadmap" trên RepoCard | Modal chọn roadmap + phase |
| Mobile UI | `RoadmapScreen`, `RoadmapEditorScreen` | Kotlin Compose |

### 3.3. Knowledge Graph (Sơ đồ liên kết tri thức)

Trực quan hóa mối quan hệ môn học (tiên quyết, bổ trợ) bằng sơ đồ tương tác.

| Item | Mô tả | Gợi ý |
|---|---|---|
| Course relationships | `course_relationships` table (course_id, related_course_id, relation_type) | Đã có trong `schema-complete.sql` |
| PREREQUISITE data | Danh sách môn tiên quyết cho từng môn | Seed data cần soạn từ chương trình đào tạo UIT |
| API | `GET /api/courses/{id}/relationships`, `GET /api/courses/graph` | Neo4j queries hoặc CTE recursion |
| Graph visualization | Sơ đồ tương tác trên web | D3.js / vis-network / reactflow component |
| Mobile graph | Hiển thị sơ đồ trên mobile | Canvas/WebView hoặc Compose Canvas |

### 3.4. Split-view Note Editor (Trình soạn thảo ghi chú)

Giao diện vừa xem tài liệu/video vừa ghi chú code snippet.

| Item | Mô tả | Gợi ý |
|---|---|---|
| Notes table | `notes` table (student_id, title, content_markdown, target_type, target_id) | Đã có trong `schema-complete.sql` |
| Code snippets | `note_code_snippets` table (note_id, language, code, caption) | Đã có trong `schema-complete.sql` |
| API CRUD | Quản lý notes + snippets | `NoteController`, `NoteService` |
| Split-view layout | UI: trái là tài liệu/video, phải là note editor | CSS grid / flexbox split pane |
| Code snippet editor | Highlight code với syntax coloring | Monaco Editor / CodeMirror |
| Video embed | Nhúng YouTube playlist vào split view | iframe + state management |
| Web UI | `NoteEditorPage`, `NoteListPage` | Components |
| Mobile UI | `NoteEditorScreen` | Kotlin Compose |
| Link từ CourseDetail | Nút "Take Notes" trên trang chi tiết | Liên kết tới NoteEditor với target pre-filled |

---

## 4. Sơ đồ database quan hệ

```
admin_users ───── admin sessions

student_users ───┬── student_bookmarks ───┬── COURSE
                 │                        └── REPO
                 │
                 ├── learning_roadmaps ─── roadmap_phases ─── roadmap_items ───┬── COURSE
                 │                                                           └── REPO
                 │
                 └── notes ───┬── COURSE/REPO/NONE
                              └── note_code_snippets

courses ───┬── course_youtube_playlists
           ├── course_articles
           ├── course_tutorials
           ├── course_relationships ─── related_course (self-ref)
           ├── github_repos ───┬── repo_tech_stacks ─── tech_stacks
           │                   └── course (fk)
           └── repo_candidates
```

---

## 5. Ưu tiên phát triển

| Ưu tiên | Tính năng | Lý do |
|---|---|---|
| P0 | External resources (YT/articles/tutorials) | Hoàn thiện Subject Orbit — ít effort nhất, tác động lớn |
| P0 | Course relationships + seed data | Dữ liệu nền cho Knowledge Graph |
| P1 | Learning Roadmap CRUD | Tính năng cốt lõi thứ 2 — effort lớn |
| P2 | Knowledge Graph visualization | Phụ thuộc vào course relationships |
| P3 | Split-view Note Editor | Phức tạp nhất, nhiều component nhất |

---

*Generated by project audit — matching feature descriptions against codebase.*
