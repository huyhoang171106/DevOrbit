# 🚀 DevOrbit

**Nền tảng quản lý và khám phá mã nguồn học thuật dành cho sinh viên UIT**

DevOrbit là một hệ thống full-stack được xây dựng nhân dịp kỷ niệm 20 năm thành lập **Trường Đại học Công nghệ Thông tin (UIT)**. Hệ thống giúp sinh viên dễ dàng tra cứu các khoá học và kho mã nguồn (Legacy Repos) đã được tuyển chọn, đồng thời hỗ trợ giảng viên / quản trị viên trong việc sàng lọc và quản lý kho tài nguyên học thuật.

---

## 📋 Mục tiêu

- Cung cấp một **catalogue trực tuyến** các môn học và repository liên quan, phục vụ nhu cầu học tập và nghiên cứu của sinh viên UIT.
- Xây dựng **quy trình kiểm duyệt** (scan → candidate → approve) giúp giảng viên dễ dàng nhập khẩu mã nguồn từ GitHub vào hệ thống một cách có kiểm soát.
- Hỗ trợ đa nền tảng: Web (React) và Mobile (Kotlin Android).
- Mở rộng dần dần: từ MVP tập trung vào duyệt và kiểm duyệt, tiến tới các tính năng cá nhân hoá và cộng đồng (Phase 2+).

---

## ✅ Tính năng đã hoàn thành

### 🔓 Công khai (Public — không cần đăng nhập)

| Tính năng | Mô tả |
|-----------|-------|
| Danh sách môn học | Hiển thị tất cả môn học đang hoạt động, kèm mã môn, tên, loại môn |
| Chi tiết môn học | Xem danh sách repository của một môn, lọc theo tech stack |
| Kho mã nguồn | Xem thông tin repo: tên, mô tả, ngôn ngữ, số sao, tech stack |
| Liên kết GitHub | Mở repository trực tiếp trên GitHub |
| Danh sách tech stack | Có endpoint toàn cục để lấy tech stack và lọc repo theo môn |

### 🔐 Quản trị (Admin — yêu cầu JWT)

| Tính năng | Mô tả |
|-----------|-------|
| Đăng nhập | Xác thực bằng username/password, nhận JWT |
| Quản lý môn học | Thêm, sửa, xoá (soft-delete) môn học |
| Quét GitHub | Nhập course ID + query, backend gọi GitHub Search API và lưu candidate kèm metadata |
| Duyệt candidate | Xem danh sách pending, duyệt hoặc từ chối candidate |
| Quản lý repo đã duyệt | Xem danh sách, sửa và xoá (soft-delete) repo |

### 👤 Sinh viên (Student — yêu cầu đăng ký & JWT) — Phase 2

| Tính năng | Mô tả |
|-----------|-------|
| Đăng ký | Tạo tài khoản với mã số sinh viên, email, mật khẩu |
| Đăng nhập | Xác thực bằng mã số SV + mật khẩu, nhận JWT |
| Bookmark | Lưu môn học hoặc repository vào danh sách cá nhân |
| Xem bookmark | Danh sách các mục đã lưu, có link điều hướng |

### 📱 Mobile (Android — Kotlin Compose)

- Màn hình đăng nhập
- Danh sách môn học (offline-first với SharedPreferences cache)
- Chi tiết môn học + danh sách repo + lọc tech stack
- Chi tiết repo + mở trên GitHub
- Danh sách bookmark và hành động bookmark repo

---

## ❌ Tính năng chưa hoàn thành / còn thiếu

### Backend (API)

| Tính năng | Ghi chú |
|-----------|---------|
| Lịch quét định kỳ | Scheduled scanning (đã xác định là non-goal MVP, chưa implement) |
| Knowledge graph / Roadmap | Phase 2+ (chưa triển khai) |
| Notes / Social vote | Phase 2+ (chưa triển khai) |
| Role hierarchy | Chỉ có ADMIN và STUDENT, chưa có phân cấp chi tiết |

### Web Frontend

| Tính năng | Ghi chú |
|-----------|---------|
| Test coverage | Đã có Vitest, nhưng chưa có test case thực tế cho UI |

### Mobile App

| Tính năng | Ghi chú |
|-----------|---------|
| Hilt DI | ✅ Đã có DataModule với Retrofit, OkHttp, Repository injection |
| ViewModel | ✅ Đã có CourseViewModel và AiTutorViewModel |
| AI Insights | ✅ Màn hình RepoDetailScreen có AI summary và tutor advice |
| Login / Register | ❌ Đã xóa khỏi codebase (restructure) |
| Bookmarks | ❌ Đã xóa khỏi codebase (restructure) |
| Tech Stack UI | ❌ Models khớp API, nhưng chưa hiển thị trên UI |
| DI framework | Đang dùng singleton thủ công, chưa có Hilt/Dagger |
| Gradle wrapper (Linux) | Chỉ có `gradlew.bat`, thiếu `gradlew` cho môi trường Unix |

### Infrastructure

| Vấn đề | Ghi chú |
|--------|---------|
| Test integration | Cần DB thật để chạy Spring Boot test (mặc định local dùng PostgreSQL qua Docker Compose) |

---

## 🛠 Công nghệ sử dụng

### Backend
| Công nghệ | Phiên bản |
|-----------|-----------|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | — |
| Spring Security | — |
| Spring WebMVC | — |
| Spring WebFlux (WebClient) | — |
| Hibernate ORM | 7.2.12 |
| PostgreSQL | 16 |
| JWT (jjwt) | 0.12.6 |
| Lombok | — |
| OpenAPI / Swagger | springdoc-openapi 2.8.6 |
| Maven | Wrapper (`mvnw`) |

### Web Frontend
| Công nghệ | Phiên bản |
|-----------|-----------|
| React | 19 |
| TypeScript | ~5.7 |
| Vite | 6 |
| Tailwind CSS | 3.4 |
| React Router | 7 |
| Native Fetch API | — |

### Mobile
| Công nghệ | Phiên bản |
|-----------|-----------|
| Kotlin | 2.0.21 |
| Jetpack Compose | BOM 2024.11.00 |
| Navigation Compose | 2.8.4 |
| Retrofit | 2.11.0 |
| OkHttp | 4.12.0 |
| Gson | — |
| Kotlinx Coroutines | 1.9.0 |
| Gradle | 8.7.3 |

### Infrastructure
| Công nghệ | Ghi chú |
|-----------|---------|
| Docker Compose | 3 service: db, api, web |
| Nginx | Reverse proxy cho web → api |
| PostgreSQL 16 Alpine | Database |

---

## 🚀 Hướng dẫn chạy local

### Yêu cầu
- Docker & Docker Compose
- Java 21+ (nếu chạy backend không qua Docker)
- Node.js 20+ (nếu chạy web không qua Docker)
- GitHub Personal Access Token (để dùng tính năng scan)

### Chạy toàn bộ hệ thống bằng Docker Compose

```bash
# Clone repo
git clone https://github.com/huyhoang171106/DevOrbit.git
cd DevOrbit

# Tạo file .env (hoặc export các biến)
# Bắt buộc: GITHUB_TOKEN=<your-token>
# Khuyến nghị đổi: JWT_SECRET=<secret-dai-hon-32-ky-tu>

# Khởi động
docker compose up -d --build
```

Sau khi chạy:
- **Web:** http://localhost:3000
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Database:** localhost:5432 (user: `huyhoang`, password: `huyhoang`, db: `devorbit_db`)

### Chạy backend riêng (không Docker)

```bash
cd devorbit-api
.\mvnw.cmd spring-boot:run
```

Yêu cầu PostgreSQL đang chạy và cấu hình biến môi trường:
- `DATABASE_URL=jdbc:postgresql://localhost:5432/devorbit_db`
- `DATABASE_USERNAME=huyhoang`
- `DATABASE_PASSWORD=huyhoang`
- `GITHUB_TOKEN=<your-token>`
- `JWT_SECRET=<your-secret>`

### Chạy web riêng (không Docker)

```bash
cd devorbit-web
npm install
npm run dev
```

Dev server chạy tại `http://localhost:5173`, proxy API đến `http://localhost:8080`.

### Chạy mobile (Android)

Mở thư mục `devorbit-mobile/` bằng Android Studio, đảm bảo có thiết bị giả lập chạy API 26+.

> **Lưu ý:** API base URL mặc định là `http://10.0.2.2:8080` (Android emulator → host machine). Nếu backend chạy trên Docker, cần đổi URL hoặc expose port.

---

## 📁 Cấu trúc thư mục

```
devorbit/
├── devorbit-api/                    # Spring Boot backend
│   ├── src/main/java/vn/edu/uit/devorbit_api/
│   │   ├── config/                  # Security, JWT, OpenAPI, WebClient
│   │   ├── controller/              # Admin*, Public*, Student* controllers
│   │   ├── dto/                     # Request/Response DTOs
│   │   ├── entity/                  # JPA entities
│   │   ├── exception/               # Global exception handler + custom exceptions
│   │   ├── repository/              # JPA repositories
│   │   └── service/                 # Business logic services
│   ├── src/main/resources/          # application.yaml, data.sql
│   ├── Dockerfile
│   └── pom.xml
├── devorbit-web/                    # React + Vite web frontend
│   ├── src/
│   │   ├── components/              # Reusable UI components
│   │   ├── lib/                     # API client, auth helpers, hooks
│   │   ├── pages/                   # Admin & Student pages
│   │   └── types/                   # TypeScript type definitions
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── devorbit-mobile/                 # Kotlin Android app
│   ├── app/src/main/java/vn/edu/uit/devorbit/mobile/
│   │   ├── model/                   # Data models
│   │   ├── network/                 # Retrofit API service + OkHttp
│   │   ├── repository/              # Repository + cache
│   │   └── ui/                      # Compose screens
│   ├── build.gradle.kts
│   └── settings.gradle.kts
├── docs/                            # Project documentation
├── specs/                           # Design specifications (spec-driven)
├── docker-compose.yml               # Multi-service orchestration
├── AGENTS.md                        # AI agent guidance (root)
└── README.md                        # This file
```

---

## 📊 Tổng quan trạng thái dự án

| Phân hệ | Trạng thái | Ghi chú |
|---------|-----------|---------|
| Backend API | ✅ Hoàn thành cốt lõi | 10 controllers, 8 services, 7 entities, 17 DTOs, 14 tests pass |
| Admin pipeline | ✅ Hoàn thành | Login → CRUD course → GitHub scan → candidate review → repo management |
| Public browsing | ✅ Hoàn thành | Course list → repo list → tech stack filter → GitHub link |
| Student auth + bookmark | ✅ Hoàn thành (Phase 2) | Register, login, JWT, bookmark course/repo, list/delete |
| Web frontend | ✅ Hoàn thành | 10 routes, 9 pages, 9 components, end-to-end với API |
| Mobile app | ⚠️ Cơ bản hoàn thành | Đã có Hilt DI, ViewModel, AI insights. Thiếu màn hình login/register/bookmark (đã xóa trong restructuring). Tech stack models khớp API, còn thiếu UI hiển thị tech stacks. |
| Infrastructure | ✅ Hoàn thành | Docker Compose 3 services, health checks, nginx proxy |
| Tests | ⚠️ Backend có, Web/Mobile chưa | 14 tests backend (controller + service), web 0, mobile 0 |

---

## 🔮 Kế hoạch phát triển

### Hiện tại (Phase hiện tại)
- ✅ MVP: Duyệt khoá học + repository, quy trình kiểm duyệt admin
- ✅ Phase 2 (một phần): Tài khoản sinh viên, bookmark

### Tương lai gợi ý
1. Thêm tech stack UI vào RepoDetailScreen (đã có models, thiếu display)
2. Thêm lại màn hình đăng ký + bookmark cho mobile (đã xóa trong restructure)
3. Bổ sung test cho web (Vitest / Playwright)
4. Knowledge graph và roadmap
5. Ghi chú cá nhân (notes)
6. Ghi chú cá nhân (notes)
7. Vote / đánh giá repository
8. Lịch quét GitHub định kỳ
9. Dashboard thống kê cho admin
10. CI/CD pipeline

---

## 📝 Ghi chú

- Dự án đang ở giai đoạn phát triển tích cực, một số thành phần có thể thay đổi.
- JWT secret mặc định trong `docker-compose.yml` chỉ dùng cho local dev. Khi triển khai thực tế, **bắt buộc** override bằng biến môi trường `JWT_SECRET`.
- DB password trong `docker-compose.yml` cũng chỉ dùng cho local. Sử dụng Docker secrets hoặc biến môi trường cho production.
- Chi tiết kỹ thuật và conventions cho từng phân hệ xem trong file `AGENTS.md` tương ứng.

---

## 👨‍💻 Đóng góp

Dự án được phát triển bởi sinh viên UIT. Mọi đóng góp đều được hoan nghênh. Vui lòng tạo issue hoặc pull request trên GitHub.

---

## 📄 Giấy phép

Dự án học thuật nội bộ — Trường Đại học Công nghệ Thông tin (UIT).
