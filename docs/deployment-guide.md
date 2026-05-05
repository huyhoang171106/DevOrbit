# Hướng dẫn Triển khai và Vận hành Hệ thống DevOrbit

**Phiên bản:** 1.0  
**Ngày cập nhật:** Tháng 5, 2026  
**Tác giả:** Nhóm phát triển DevOrbit — UIT

---

## Mục lục

1. [Phân tích yêu cầu và kiến trúc hệ thống](#section-1-phân-tích-yêu-cầu-và-kiến-trúc-hệ-thống)
2. [Chuẩn bị môi trường phát triển và máy chủ](#section-2-chuẩn-bị-môi-trường-phát-triển-và-máy-chủ)
3. [Quản lý mã nguồn và cấu hình dự án](#section-3-quản-lý-mã-nguồn-và-cấu-hình-dự-án)
4. [Thiết lập và khởi tạo cơ sở dữ liệu](#section-4-thiết-lập-và-khởi-tạo-cơ-sở-dữ-liệu)
5. [Xây dựng và triển khai dịch vụ backend](#section-5-xây-dựng-và-triển-khai-dịch-vụ-backend)
6. [Tích hợp và tối ưu hóa dịch vụ frontend](#section-6-tích-hợp-và-tối-ưu-hóa-dịch-vụ-frontend)
7. [Kiểm thử toàn diện và đảm bảo chất lượng](#section-7-kiểm-thử-toàn-diện-và-đảm-bảo-chất-lượng)
8. [Triển khai và giám sát sản phẩm](#section-8-triển-khai-và-giám-sát-sản-phẩm)
9. [Hướng dẫn sử dụng và bảo trì hệ thống](#section-9-hướng-dẫn-sử-dụng-và-bảo-trì-hệ-thống)
10. [Bảng phân bổ tài nguyên, danh sách công cụ và các lưu ý quan trọng](#section-10-bảng-phân-bổ-tài-nguyên-danh-sách-công-cụ-và-các-lưu-ý-quan-trọng)

---

## SECTION 1: Phân tích yêu cầu và kiến trúc hệ thống

### 1.1. Tổng quan hệ thống DevOrbit

DevOrbit là một nền tảng quản lý và khám phá kho lưu trữ GitHub phục vụ mục đích học tập, dành riêng cho sinh viên ngành Công nghệ Phần mềm. Hệ thống cho phép quản trị viên quét GitHub để tìm kiếm các kho lưu trữ phù hợp với từng môn học, phê duyệt và gán chúng vào các khóa học tương ứng. Sinh viên có thể duyệt danh sách môn học, xem các kho lưu trữ GitHub được phê duyệt, lọc theo tech stack và lưu bookmark.

Hệ thống bao gồm 4 thành phần chính:

| Thành phần | Công nghệ | Vai trò |
|---|---|---|
| **db** | PostgreSQL 16 Alpine | Cơ sở dữ liệu quan hệ lưu trữ toàn bộ dữ liệu |
| **api** | Spring Boot 4.0.6 (Java 21, Maven) | REST API backend xử lý logic nghiệp vụ |
| **web** | React 19 + TypeScript + Vite + Nginx | Giao diện người dùng web (admin + student) |
| **mobile** | Kotlin + Jetpack Compose | Ứng dụng Android dành cho sinh viên |

### 1.2. Kiến trúc tổng thể

#### Sơ đồ luồng dữ liệu (Data Flow Diagram)

```
+----------------------------------------------------------+
|                      NGƯỜI DÙNG                           |
+----+----------------------+---------------------+---------+
     |                      |                     |
     v                      v                     v
+----------+        +---------------+        +-----------+
|  Trình   |        |    Mobile     |        |   Admin   |
|  duyệt   |        |   (Android)   |        |  (Browser)|
|  Student |        |               |        |           |
+-----+----+        +-------+-------+        +-----+-----+
      |                      |                      |
      |  HTTP/HTTPS          |  HTTP/HTTPS          |  HTTP/HTTPS
      |                      |                      |
      v                      v                      v
+----------------------------------------------------------+
|                       NGINX (Port 80)                      |
|  +------------------------------------------------------+ |
|  |  location / -> React SPA (index.html)                | |
|  |  location /api/ -> proxy_pass http://api:8080        | |
|  +------------------------------------------------------+ |
+-----------------------------+----------------------------+
                              |
                              v
                    +--------------------+
                    |  Spring Boot API    |
                    |   (Port 8080)       |
                    |  +---------------+  |
                    |  |JWT Filter     |  |
                    |  +---------------+  |
                    |  |Controllers    |  |
                    |  +---------------+  |
                    |  |Services       |  |
                    |  +---------------+  |
                    |  |Repositories   |  |
                    |  +---------------+  |
                    +----------+---------+
                               |
                               v
                    +--------------------+
                    |  PostgreSQL 16      |
                    |  (Port 5432)        |
                    +--------------------+
```

Mobile App (emulator): Retrofit -> http://10.0.2.2:8080 (trực tiếp)
Mobile App (thiết bị thật): Retrofit -> https://api.example.com

#### Sơ đồ luồng xác thực (Authentication Flow)

```
+----------+        +---------------+        +-----------+
|  Client  |        |  Nginx / API  |        |   JWT     |
|          |        |               |        |  Service  |
+----+-----+        +-------+-------+        +-----+-----+
     |                      |                      |
     |  POST /login         |                      |
     |  (credentials)       |                      |
     +--------------------->|                      |
     |                      |  validate creds      |
     |                      +--------------------->|
     |                      |  generate JWT        |
     |                      |<---------------------+
     |<---- JWT token ------|                      |
     |                      |                      |
     |  GET /protected      |                      |
     |  Authorization:      |                      |
     |  Bearer <token>      |                      |
     +--------------------->|                      |
     |                      | JwtAuthFilter        |
     |                      | validates token      |
     |                      +--------------------->|
     |                      | set SecurityContext  |
     |                      |<---------------------+
     |<---- response -------|                      |
```

---


### 1.3. Bảng công nghệ (Tech Stack)

| Lớp | Công nghệ | Phiên bản | Mục đích |
|---|---|---|---|
| **Backend** | Java (Eclipse Temurin) | 21 | Ngôn ngữ lập trình chính |
| | Spring Boot | 4.0.6 | Framework ứng dụng |
| | Spring Data JPA / Hibernate | 6.x / 6.x | ORM và truy vấn DB |
| | Spring Security | 6.x | Xác thực và phân quyền |
| | Spring Web MVC | 6.x | REST API endpoints |
| | Spring Validation | 6.x | Xác thực dữ liệu đầu vào |
| | Spring WebFlux | 6.x | WebClient gọi GitHub API |
| | jjwt (io.jsonwebtoken) | 0.12.6 | JWT token |
| | Lombok | 1.18.x | Giảm boilerplate code |
| | springdoc-openapi | 2.8.6 | Swagger UI tự động |
| | PostgreSQL JDBC Driver | 42.x | Driver kết nối PostgreSQL |
| **Frontend** | React | 19.0.0 | UI framework |
| | TypeScript | 5.7.x | Kiểu dữ liệu tĩnh |
| | Vite | 6.0.0 | Build tool |
| | Tailwind CSS | 3.4.x | CSS framework |
| | React Router DOM | 7.0.0 | Routing client-side |
| | Vitest | 2.1.8 | Unit testing |
| | @testing-library/react | 16.1.0 | React component testing |
| **Mobile** | Kotlin | 2.0.21 | Ngôn ngữ lập trình |
| | Jetpack Compose | 2024.11.00 BOM | UI declarative |
| | Retrofit | 2.11.0 | HTTP client |
| | OkHttp | 4.12.0 | Network interceptor |
| | Gson | 2.11.0 (converter) | JSON parsing |
| | Android SDK / Compile SDK | 35 | API level target |
| | Kotlin Compose plugin | 2.0.21 | Compose compiler |
| **Infrastructure** | Docker | 24+ | Container runtime |
| | Docker Compose | 2.x | Multi-container orchestration |
| | Nginx | latest (alpine) | Reverse proxy + static serving |
| | PostgreSQL | 16 Alpine | Database |
| | Maven | 3.9+ (wrapper) | Backend build |
| | Node.js | 20 LTS | Frontend build |
| | Gradle | 8.11.1 | Mobile build |


### 1.4. Danh sách API endpoints

Hệ thống API được tổ chức thành 3 nhóm chính: Public (không cần xác thực), Admin (xác thực JWT, admin), Student (xác thực JWT, sinh viên).

#### 1.4.1. Public Endpoints (không cần JWT)

| Phương thức | Endpoint | Mô tả | Controller |
|---|---|---|---|
| **GET** | /api/health | Kiểm tra trạng thái API | HealthController |
| **GET** | /api/courses | Danh sách tất cả môn học | PublicCourseController |
| **GET** | /api/courses/{id} | Chi tiết môn học + repos | PublicCourseController |
| **GET** | /api/courses/{courseId}/repos?techStack= | Repos theo môn, lọc tech stack | PublicRepoController |
| **GET** | /api/tech-stacks | Danh sách tech stack | PublicTechStackController |
| **GET** | /api/repos/{repoId}/tech-stacks | Tech stack của 1 repo | PublicTechStackController |
| **GET** | /swagger-ui/** | Giao diện Swagger UI | springdoc-openapi |
| **GET** | /v3/api-docs/** | OpenAPI JSON spec | springdoc-openapi |

#### 1.4.2. Admin Endpoints (yêu cầu JWT + admin role)

| Phương thức | Endpoint | Mô tả | Controller |
|---|---|---|---|
| **POST** | /api/admin/auth/login | Đăng nhập admin, nhận JWT | AdminAuthController |
| **GET** | /api/admin/courses | Danh sách môn học (cả inactive) | AdminCourseController |
| **GET** | /api/admin/courses/{id} | Chi tiết môn học admin | AdminCourseController |
| **POST** | /api/admin/courses | Tạo môn học mới | AdminCourseController |
| **PUT** | /api/admin/courses/{id} | Cập nhật môn học | AdminCourseController |
| **DELETE** | /api/admin/courses/{id} | Vô hiệu hóa môn học | AdminCourseController |
| **POST** | /api/admin/github/scan | Quét GitHub (1 course) | AdminGithubController |
| **POST** | /api/admin/github/scan-all | Quét GitHub (all courses) | AdminGithubController |
| **GET** | /api/admin/github/scan-logs | Log quét GitHub | AdminGithubController |
| **GET** | /api/admin/repo-candidates | Candidates đang chờ duyệt | AdminRepoCandidateController |
| **POST** | /api/admin/repo-candidates/{id}/approve | Phê duyệt candidate | AdminRepoCandidateController |
| **POST** | /api/admin/repo-candidates/{id}/reject | Từ chối candidate | AdminRepoCandidateController |
| **GET** | /api/admin/repos | Danh sách repos đã duyệt | AdminRepoController |
| **PUT** | /api/admin/repos/{repoId} | Cập nhật repo đã duyệt | AdminRepoController |
| **DELETE** | /api/admin/repos/{repoId} | Xóa repo đã duyệt | AdminRepoController |
| **GET** | /api/admin/courses/relationships | Quan hệ môn học | AdminCourseRelationshipController |
| **GET** | /api/admin/courses/relationships/course/{courseId} | Quan hệ theo môn | AdminCourseRelationshipController |
| **POST** | /api/admin/courses/relationships | Tạo quan hệ môn học | AdminCourseRelationshipController |
| **DELETE** | /api/admin/courses/relationships/{id} | Xóa quan hệ | AdminCourseRelationshipController |
| **GET** | /api/admin/notes | Danh sách ghi chú | AdminNoteController |
| **GET** | /api/admin/notes/{id} | Chi tiết ghi chú | AdminNoteController |
| **DELETE** | /api/admin/notes/{id} | Xóa ghi chú | AdminNoteController |
| **GET** | /api/admin/notes/{noteId}/snippets | Code snippets | AdminNoteController |
| **GET** | /api/admin/roadmaps | Danh sách lộ trình | AdminRoadmapController |
| **GET** | /api/admin/roadmaps/{id} | Chi tiết lộ trình | AdminRoadmapController |
| **POST** | /api/admin/roadmaps | Tạo lộ trình | AdminRoadmapController |
| **PUT** | /api/admin/roadmaps/{id} | Cập nhật lộ trình | AdminRoadmapController |
| **DELETE** | /api/admin/roadmaps/{id} | Xóa lộ trình | AdminRoadmapController |
| **GET/POST** | /api/admin/roadmaps/{roadmapId}/phases | CRUD phases | AdminRoadmapController |
| **GET/POST/PUT/DELETE** | /api/admin/roadmaps/phases/{phaseId}/items | CRUD items | AdminRoadmapController |
| **GET/POST/PUT/DELETE** | /api/admin/courses/{courseId}/resources/* | CRUD resources | AdminCourseResourceController |

#### 1.4.3. Student Endpoints (yêu cầu JWT + student role)

| Phương thức | Endpoint | Mô tả | Controller |
|---|---|---|---|
| **POST** | /api/student/login | Đăng nhập sinh viên | StudentAuthController |
| **POST** | /api/student/register | Đăng ký tài khoản SV | StudentAuthController |
| **GET** | /api/student/me | Thông tin cá nhân SV | StudentAuthController |
| **GET** | /api/student/bookmarks | Danh sách bookmark | StudentBookmarkController |
| **POST** | /api/student/bookmarks | Thêm bookmark | StudentBookmarkController |
| **DELETE** | /api/student/bookmarks/{targetType}/{targetId} | Xóa bookmark | StudentBookmarkController |


### 1.5. Luồng xác thực chi tiết

1. **Admin login:** Gửi request POST /api/admin/auth/login với username và password
2. **Student login:** Gửi request POST /api/student/login với studentCode/email và password
3. **Student register:** Gửi request POST /api/student/register với thông tin sinh viên
4. **JWT generation:** JwtService tạo token với subject = username/studentCode, claim type = ADMIN/STUDENT, issuedAt, expiration
5. **Client lưu token:** Lưu vào localStorage / sessionStorage / AsyncStorage (mobile)
6. **Gửi request có xác thực:** Header Authorization: Bearer <token>
7. **Server xác thực:** JwtAuthenticationFilter (kế thừa OncePerRequestFilter) trích xuất token từ header, gọi JwtService.isTokenValid(), nếu hợp lệ thì parse username và set vào SecurityContextHolder
8. **Spring Security:** SecurityConfig cấu hình:
   - SessionCreationPolicy.STATELESS - không dùng session
   - csrf.disable() - REST API, token-based auth
   - CORS: cho phép tất cả origin pattern, methods: GET/POST/PUT/DELETE/OPTIONS
   - Public endpoints: permitAll() cho /api/courses/**, /api/repos/**, /api/admin/auth/**, /api/student/login, /api/student/register
   - Authenticated: /api/student/** và /api/admin/** yêu cầu xác thực
9. **Password encoding:** BCryptPasswordEncoder - bcrypt hash cho cả admin và student

### 1.6. Tích hợp bên thứ ba

#### GitHub REST API
- Sử dụng WebClient (Spring WebFlux) để gọi GitHub REST API
- Endpoint: GET https://api.github.com/search/repositories?q={query}
- Xác thực: Bearer token trong header (GITHUB_TOKEN)
- Rate limit: 5000 requests/hour với token (so với 60 requests/hour không token)
- Kết quả trả về được map thành RepoCandidate để admin phê duyệt

#### BCrypt Password Hashing
- PasswordEncoder bean của Spring Security
- BCryptPasswordEncoder với độ mạnh mặc định (strength = 10)
- Admin seed password được hash sẵn trong data.sql

---



## SECTION 2: Chuẩn bị môi trường phát triển và máy chủ

### 2.1. Phần mềm yêu cầu

Để phát triển và triển khai hệ thống DevOrbit, cần cài đặt các phần mềm sau:

| Phần mềm | Phiên bản tối thiểu | Mục đích |
|---|---|---|
| **Git** | 2.x | Quản lý phiên bản mã nguồn |
| **Java (Eclipse Temurin JDK)** | 21 LTS | Biên dịch và chạy backend |
| **Node.js** | 20 LTS | Build frontend |
| **Docker** | 24+ | Containerization |
| **Docker Compose** | 2.x | Multi-container orchestration |
| **Maven** | 3.9+ | Build backend (qua wrapper) |
| **PostgreSQL** | 16 | Database (local dev) |
| **Android Studio** | Hedgehog+ | Phát triển mobile |
| **Gradle** | 8.11.1 | Build mobile |

### 2.2. Biến môi trường tham chiếu

Tất cả cấu hình của hệ thống được quản lý qua biến môi trường. File tham chiếu: .env.example tại thư mục gốc dự án.

```bash
# File: .env.example
POSTGRES_DB=devorbit_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=devorbit_local_password
DATABASE_URL=jdbc:postgresql://db:5432/devorbit_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=devorbit_local_password
GITHUB_TOKEN=
JWT_SECRET=replace-with-a-random-256-bit-secret
JWT_EXPIRATION_MINUTES=120
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false
JPA_FORMAT_SQL=false
SERVER_PORT=8080
VITE_API_BASE_URL=/api
```

#### Bảng mô tả chi tiết biến môi trường

| Biến | Giá trị mặc định | Mô tả | Ghi chú sản xuất |
|---|---|---|---|
| POSTGRES_DB | devorbit_db | Tên database PostgreSQL | Nên đặt tên khó đoán |
| POSTGRES_USER | postgres | User PostgreSQL | Tạo user riêng, không dùng postgres |
| POSTGRES_PASSWORD | devorbit_local_password | Mật khẩu PostgreSQL | Phải đổi - mật khẩu mạnh, 16+ ký tự |
| DATABASE_URL | jdbc:postgresql://db:5432/devorbit_db | JDBC connection string | ?sslmode=require cho production |
| DATABASE_USERNAME | postgres | Username kết nối DB | Tạo user riêng với quyền tối thiểu |
| DATABASE_PASSWORD | devorbit_local_password | Mật khẩu kết nối DB | Phải đổi |
| JWT_SECRET | replace-with-a-random-256-bit-secret | Khóa ký JWT (256-bit) | Phải đổi - openssl rand -base64 32 |
| JWT_EXPIRATION_MINUTES | 120 | Thời gian sống của JWT | Production: 30-60 phút |
| GITHUB_TOKEN | (rỗng) | GitHub Personal Access Token | Bắt buộc cho scan feature |
| JPA_DDL_AUTO | update | Chế độ DDL của Hibernate | Production: validate |
| JPA_SHOW_SQL | false | Log câu SQL ra console | Production: false |
| JPA_FORMAT_SQL | false | Format SQL log | Production: false |
| SERVER_PORT | 8080 | Cổng Spring Boot API | Trong Docker: 8080 |
| VITE_API_BASE_URL | /api | Base URL API cho frontend | Luôn là /api (qua Nginx proxy) |

### 2.3. Yêu cầu phần cứng máy chủ

| Môi trường | CPU | RAM | Ổ đĩa | Băng thông | Hệ điều hành |
|---|---|---|---|---|---|
| **Development** | 2 vCPU | 4 GB | 20 GB SSD | 100 Mbps | Windows/macOS/Linux |
| **Staging** | 2 vCPU | 4 GB | 30 GB SSD | 100 Mbps | Ubuntu 22.04 LTS |
| **Production (thấp)** | 4 vCPU | 8 GB | 50 GB SSD | 1 Gbps | Ubuntu 22.04 LTS |
| **Production (trung bình)** | 8 vCPU | 16 GB | 100 GB SSD | 1 Gbps | Ubuntu 22.04 LTS |

### 2.4. Cài đặt Docker

#### Ubuntu 22.04 LTS / Debian 11

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER
docker --version
docker compose version
```

#### Windows (Development)

Tải Docker Desktop từ https://www.docker.com/products/docker-desktop/. Chọn WSL 2 backend (yêu cầu Windows 10/11 Pro/Enterprise).

### 2.5. Cài đặt Java 21 (Eclipse Temurin)

#### Ubuntu/Debian

```bash
wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo gpg --dearmor -o /usr/share/keyrings/adoptium.gpg
echo "deb [signed-by=/usr/share/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/adoptium.list
sudo apt update
sudo apt install -y temurin-21-jdk
java -version
```

#### Windows

Tải từ https://adoptium.net/temurin/releases/?version=21 và chạy file .msi.

### 2.6. Cài đặt Node.js 20 LTS

```bash
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs
node --version
npm --version
```

---



## SECTION 3: Quản lý mã nguồn và cấu hình dự án

### 3.1. Clone repository

```bash
git clone https://github.com/huyhoang171106/DevOrbit.git
cd DevOrbit
```

Cấu trúc thư mục gốc:

```
DevOrbit/
  devorbit-api/          # Spring Boot backend
  devorbit-web/          # React frontend
  devorbit-mobile/       # Android app
  docs/                  # Tài liệu
  docker-compose.yml     # Docker Compose orchestration
  .env.example           # Mẫu biến môi trường
  AGENTS.md              # Hướng dẫn AI agent
  README.md              # Giới thiệu dự án
  run-backend.bat        # Script chạy backend (Windows)
```

### 3.2. Chiến lược branching

#### Khuyến nghị: GitHub Flow (phù hợp với MVP)

```
main <- production-ready code
  +-- feature/xxx <- nhánh tính năng mới
  +-- fix/xxx     <- nhánh sửa lỗi
  +-- hotfix/xxx  <- nhánh vá lỗi khẩn cấp (từ main)
```

Quy trình:
1. Tạo nhánh từ main: git checkout -b feature/add-login-page
2. Phát triển và commit
3. Mở Pull Request lên main
4. Code review + CI checks
5. Merge vào main
6. Deploy từ main

### 3.3. Sao chép và cấu hình biến môi trường

```bash
cp .env.example .env
docker compose config
```

Giải thích chi tiết từng biến:
- POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD: Dùng cho Docker service db để khởi tạo PostgreSQL container
- DATABASE_URL: JDBC URL kết nối từ Spring Boot đến PostgreSQL. Trong Docker, host là db (tên service)
- JWT_SECRET: Khóa bí mật dùng để ký và xác thực JWT token. Yêu cầu tối thiểu 256-bit (32 ký tự Base64). Cực kỳ quan trọng: phải thay đổi trong production
- JWT_EXPIRATION_MINUTES: Thời gian sống của token. Giá trị thấp hơn (30-60 phút) an toàn hơn
- GITHUB_TOKEN: Personal Access Token để gọi GitHub REST API. Token cần quyền repo và public_repo
- JPA_DDL_AUTO: update tạo tự động schema dựa trên entity. validate chỉ kiểm tra schema khớp với entity
- VITE_API_BASE_URL: Chỉ dùng cho frontend build. Giá trị phải là /api để Nginx proxy hoạt động

Lưu ý an ninh: Không bao giờ commit file .env vào Git. File .gitignore của backend (devorbit-api/.gitignore) đã loại trừ .env.

### 3.4. Tạo GitHub Personal Access Token

1. Đăng nhập GitHub -> Settings (avatar góc phải trên)
2. Developer settings (cuối sidebar trái)
3. Personal access tokens -> Tokens (classic) hoặc Fine-grained tokens
4. Generate new token (classic):
   - Note: DevOrbit API Scanner
   - Expiration: No expiration hoặc thời gian phù hợp
   - Scopes: repo (tất cả), public_repo (chỉ public repos)
5. Click Generate token
6. Copy token ngay lập tức (không thể xem lại sau)
7. Dán vào .env: GITHUB_TOKEN=github_pat_...

### 3.5. Sinh JWT Secret

#### Linux / macOS
```bash
openssl rand -base64 32
```

#### Windows (PowerShell)
```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Max 256 }))
```

### 3.6. Cài đặt dependencies

#### Backend (Maven)
```bash
cd devorbit-api
.\mvnw.cmd dependency:resolve    # Windows
./mvnw dependency:resolve         # Linux/macOS
```

#### Frontend (npm)
```bash
cd devorbit-web
npm ci   # clean install dựa trên package-lock.json
```

#### Mobile (Gradle)
```bash
cd devorbit-mobile
.\gradlew.bat build --no-daemon  # Windows
./gradlew build --no-daemon       # Linux/macOS
```

### 3.7. File .gitignore

Backend (devorbit-api/.gitignore):
```
HELP.md
target/
.mvn/wrapper/maven-wrapper.jar
.idea
*.iws
*.iml
*.ipr
.vscode/
.env
src/main/resources/application-local.yaml
```

Frontend (devorbit-web/.gitignore):
```
node_modules
dist
.vite
*.local
```

Giải thích:
- target/ - thư mục build của Maven
- node_modules/ - dependencies của npm (rất nhiều file)
- dist/ - output build của Vite
- .env - biến môi trường chứa secret (quan trọng nhất)
- *.local - file cấu hình local (vd application-local.yaml, .env.local)
- .idea/, .vscode/ - cấu hình IDE cá nhân

### 3.8. Thiết lập Lombok trong IDE

#### IntelliJ IDEA
1. File -> Settings -> Plugins -> Marketplace
2. Tìm kiếm Lombok -> Cài đặt
3. Enable annotation processing: File -> Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors -> Enable annotation processing
4. Khởi động lại IDE

#### VS Code
1. Cài đặt extension Lombok Annotations Support (vscode-lombok)

---



## SECTION 4: Thiết lập và khởi tạo cơ sở dữ liệu

### 4.1. Tổng quan cơ sở dữ liệu

DevOrbit sử dụng PostgreSQL 16 Alpine làm cơ sở dữ liệu chính. Database được quản lý thông qua Docker container với volume persistent để đảm bảo dữ liệu không bị mất khi container restart.

### 4.2. Cấu hình Docker Compose cho DB service

```yaml
services:
  db:
    image: postgres:16-alpine
    container_name: devorbit-db
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${POSTGRES_DB:-devorbit_db}
      POSTGRES_USER: ${POSTGRES_USER:-postgres}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-devorbit_local_password}
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-postgres}"]
      interval: 5s
      timeout: 5s
      retries: 5
```

Giải thích cấu hình:
- image: postgres:16-alpine - PostgreSQL 16 trên Alpine Linux (dung lượng nhỏ)
- container_name: devorbit-db - Tên container cố định để dễ tham chiếu
- restart: unless-stopped - Tự động restart trừ khi bị dừng thủ công
- POSTGRES_DB: Tạo database tự động khi container start
- ports: 5432:5432 - Map port 5432 host -> 5432 container
- volumes: pgdata - Volume persistent lưu dữ liệu DB
- healthcheck: pg_isready - Kiểm tra sẵn sàng trước khi api service start

### 4.3. Khởi tạo database

Khi container db start lần đầu, PostgreSQL tự động:
1. Khởi tạo cluster PostgreSQL
2. Tạo user (mặc định: postgres)
3. Tạo database devorbit_db
4. Set ownership cho user

Kiểm tra kết nối:
```bash
docker exec -it devorbit-db psql -U postgres -d devorbit_db
\dt
SELECT * FROM courses;
\q
```

### 4.4. Hibernate DDL auto-generation

Cấu hình trong application.yaml: spring.jpa.hibernate.ddl-auto: ${JPA_DDL_AUTO:update}

| Giá trị | Hành vi | Sử dụng |
|---|---|---|
| none | Không tự động thay đổi schema | Quản lý schema thủ công |
| validate | Chỉ kiểm tra entity khớp với schema | Production |
| update | Tự động tạo/cập nhật bảng dựa trên entity | Development |
| create | Xóa và tạo lại bảng mỗi lần chạy | Không dùng |
| create-drop | Tạo khi start, xóa khi stop | Testing |

Trong production, nên đặt JPA_DDL_AUTO=validate.

### 4.5. Seed data (data.sql)

File devorbit-api/src/main/resources/data.sql chứa dữ liệu khởi tạo:

67 courses - Danh sách môn học của ngành Kỹ thuật Phần mềm, UIT:
```sql
insert into courses (mamh, tenmh, sotc, lt, th, loaimonhoc)
values
('SS003', 'Tư tưởng Hồ Chí Minh', 2, 2, 0, 'DAI_CUONG'),
('SS007', 'Triết học Mác - Lênin', 3, 3, 0, 'DAI_CUONG'),
('IT001', 'Nhập môn Lập trình', 4, 3, 1, 'BAT_BUOC'),
-- ... (67 rows)
on conflict (mamh) do nothing;
```

1 admin user:
```sql
insert into admin_users (username, password_hash, active)
values ('admin', '\$2b\$12\$3IpxxJtwI/zV8hYwQc8W9eWRkf.l0yocc3Di5FvA65J0QiJ395WSe', true)
on conflict (username) do nothing;
```

Các loại môn học: DAI_CUONG (Đại cương), BAT_BUOC (Bắt buộc), TU_CHON (Tự chọn), TOT_NGHIEP (Tốt nghiệp)

### 4.6. Tối ưu hóa Index

```sql
CREATE INDEX idx_courses_mamh ON courses(mamh);
CREATE INDEX idx_repos_course_id ON approved_repos(course_id);
CREATE INDEX idx_bookmarks_student_code ON bookmarks(student_code);
CREATE INDEX idx_course_relationships_course_id ON course_relationships(course_id);
CREATE INDEX idx_repo_tech_stacks_repo_id ON repo_tech_stacks(repo_id);
CREATE INDEX idx_repos_course_tech_stack ON approved_repos(course_id, tech_stack);
```

### 4.7. Backup và Restore

Backup strategy:
- Hàng ngày (2:00 AM): Full dump pg_dump, lưu giữ 30 ngày
- Mỗi giờ (tùy chọn): WAL archiving, lưu giữ 7 ngày
- Hàng tuần: Off-site copy rsync to S3, lưu giữ 3 tháng

Script backup:
```bash
#!/bin/bash
BACKUP_DIR="/opt/devorbit/backups"
DB_NAME="devorbit_db"
DB_USER="postgres"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/devorbit_${TIMESTAMP}.sql"
mkdir -p ${BACKUP_DIR}
docker exec devorbit-db pg_dump -U ${DB_USER} ${DB_NAME} > ${BACKUP_FILE}
gzip ${BACKUP_FILE}
find ${BACKUP_DIR} -name "devorbit_*.sql.gz" -mtime +30 -delete
```

Cron job:
```bash
0 2 * * * /opt/devorbit/scripts/backup-db.sh >> /var/log/devorbit-backup.log 2>&1
```

Restore:
```bash
gunzip -c devorbit_20260504_020000.sql.gz | docker exec -i devorbit-db psql -U postgres -d devorbit_db
```

### 4.8. Connection Pooling với HikariCP

Spring Boot sử dụng HikariCP làm connection pool mặc định.

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1800000
      pool-name: DevOrbitHikariCP
```

Khuyến nghị sizing: Development 5-10, Staging 10-20, Production (4 vCPU) 20-50

---



## SECTION 5: Xây dựng và triển khai dịch vụ backend

### 5.1. Xây dựng Backend với Maven

```bash
cd devorbit-api
.\mvnw.cmd clean package -DskipTests   # Windows
./mvnw clean package -DskipTests        # Linux/macOS
```

Kết quả: file target/devorbit-api-0.0.1-SNAPSHOT.jar

### 5.2. Docker build cho Backend

```bash
docker build -t devorbit-api ./devorbit-api
```

Multi-stage Dockerfile (devorbit-api/Dockerfile):
```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Giải thích multi-stage:
- Stage 1 (build): Dùng image Maven đầy đủ (JDK + Maven) để compile. Layer COPY pom.xml + RUN mvn dependency:go-offline giúp cache dependencies.
- Stage 2 (runtime): Dùng eclipse-temurin:21-jre-alpine (chỉ ~50MB so với ~300MB JDK) để chạy JAR.

### 5.3. Tuning JVM cho Production

```bash
java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -Dspring.profiles.active=prod \
     -jar app.jar
```

| Flag | Giá trị | Mục đích |
|---|---|---|
| -Xms | 512m | Heap khởi tạo |
| -Xmx | 1024m | Heap tối đa |
| -XX:+UseG1GC | - | G1 Garbage Collector (low-pause) |
| -XX:MaxGCPauseMillis | 200 | Mục tiêu GC pause < 200ms |
| -XX:+HeapDumpOnOutOfMemoryError | - | Auto dump heap khi OOM |

### 5.4. Spring Boot Production Profile

Tạo file application-prod.yaml trong devorbit-api/src/main/resources/:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  datasource:
    hikari:
      maximum-pool-size: 20
server:
  tomcat:
    max-threads: 200
    max-connections: 1000
logging:
  level:
    root: WARN
    vn.edu.uit.devorbit_api: INFO
  file:
    path: /var/log/devorbit
    name: /var/log/devorbit/api.log
```

### 5.5. Nginx Reverse Proxy Configuration

```nginx
# File: devorbit-web/nginx.conf
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files \$uri \$uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://api:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

Giải thích:
- try_files \$uri \$uri/ /index.html: Fallback về index.html (SPA routing)
- proxy_pass http://api:8080: Forward request đến Spring Boot (Docker DNS)
- proxy_set_header X-Real-IP: Forward IP thật của client

### 5.6. SSL/TLS với Let's Encrypt

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d api.devorbit.example.com
sudo certbot renew --dry-run
```

Sau khi Certbot thành công, Nginx config có thêm:
```nginx
server {
    listen 443 ssl;
    server_name api.devorbit.example.com;
    ssl_certificate /etc/letsencrypt/live/api.devorbit.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.devorbit.example.com/privkey.pem;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
}

server {
    listen 80;
    return 301 https://\$server_name\$request_uri;
}
```

### 5.7. Health Check Endpoint

```java
@RestController
public class HealthController {
    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
```

Kiểm tra:
```bash
curl http://localhost:8080/api/health
# {"status":"ok"}
```

### 5.8. Systemd Service Unit (Production không Docker)

```ini
[Unit]
Description=DevOrbit API
After=network.target postgresql.service

[Service]
Type=simple
User=devorbit
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar /opt/devorbit/api/app.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Sử dụng:
```bash
sudo cp devorbit-api.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable devorbit-api
sudo systemctl start devorbit-api
sudo journalctl -u devorbit-api -f
```

### 5.9. Graceful Shutdown

```yaml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

---



## SECTION 6: Tích hợp và tối ưu hóa dịch vụ frontend

### 6.1. Xây dựng Frontend

```bash
cd devorbit-web
npm ci
npm run build
ls -la dist/
```

Quá trình build: tsc -b (TypeScript compilation) + vite build (bundle)

Output mặc định: dist/

```
devorbit-web/dist/
  assets/
    index-xxxxx.js          # JavaScript bundle (hash cho cache busting)
    index-xxxxx.css         # CSS bundle
    react-xxxxx.js          # Vendor chunk (React, React Router)
  favicon.ico
  index.html                # Entry point SPA
```

### 6.2. Multi-stage Dockerfile

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 6.3. Nginx Production Config (mở rộng)

```nginx
server {
    listen 443 ssl http2;
    server_name devorbit.example.com;

    ssl_certificate /etc/letsencrypt/live/devorbit.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/devorbit.example.com/privkey.pem;

    root /usr/share/nginx/html;
    index index.html;

    # Gzip Compression
    gzip on;
    gzip_comp_level 6;
    gzip_min_length 256;
    gzip_types text/plain application/javascript application/json text/css;
    gzip_proxied any;
    gzip_vary on;

    # Static Assets Caching (1 year)
    location ~* \.(?:css|js|png|jpg|jpeg|gif|ico|svg|woff2)$ {
        expires 365d;
        add_header Cache-Control "public, immutable";
        access_log off;
    }

    # HTML (no cache)
    location ~* \.html$ {
        expires -1;
        add_header Cache-Control "no-store, no-cache, must-revalidate";
    }

    # SPA Fallback
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API Proxy
    location /api/ {
        proxy_pass http://api:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
        proxy_buffering on;
    }

    # Security Headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options nosniff;
    add_header X-Frame-Options SAMEORIGIN;
    add_header Referrer-Policy "strict-origin-when-cross-origin";
}
```

### 6.4. Tối ưu hiệu năng Frontend

Vite (vite.config.ts) tự động:
- Code splitting: Tách vendor chunks (React, React Router)
- Tree shaking: Loại bỏ code không dùng
- CSS minification: Tối thiểu CSS
- JS minification: Terser hoặc esbuild
- Asset hashing: Content hash trong filename (cache busting)

```typescript
// vite.config.ts
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: { '/api': 'http://localhost:8080' }
  }
})
```

### 6.5. API Client Frontend

File devorbit-web/src/lib/api.ts xử lý giao tiếp với backend:
- apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? ''
- apiGet, apiPost, apiPut, apiDelete cho public endpoints
- apiAdminGet, apiAdminPost, apiAdminPut, apiAdminDelete cho admin (Bearer token)
- apiStudentGet, apiStudentPost, apiStudentDelete cho student (Bearer token)

Luồng request:
1. VITE_API_BASE_URL = /api (từ biến môi trường)
2. Gọi apiGet('/courses') -> fetch /api/courses
3. Nginx nhận /api/courses -> proxy_pass đến http://api:8080
4. Spring Boot xử lý và trả response

### 6.6. Build và chạy với Docker Compose

```bash
docker compose build
docker compose up -d
docker compose logs -f web
# Truy cập: http://localhost:3000
```

---



## SECTION 7: Kiểm thử toàn diện và đảm bảo chất lượng

### 7.1. Kiểm thử Backend

```bash
cd devorbit-api
.\mvnw.cmd test   # Windows
./mvnw test        # Linux/macOS
```

Hiện tại có 14 tests (JUnit 5 + Spring Boot Test) bao gồm controller tests và service tests.

Dependencies testing trong pom.xml:
- spring-boot-starter-data-jpa-test
- spring-boot-starter-webmvc-test
- spring-security-test

Khuyến nghị bổ sung Integration Test với Testcontainers:
```java
@SpringBootTest
@Testcontainers
class CourseServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CourseService courseService;

    @Test
    void shouldReturnActiveCourses() {
        List<CourseSummaryResponse> courses = courseService.getActiveCourseSummaries();
        assertThat(courses).isNotEmpty();
    }
}
```

### 7.2. Kiểm thử Frontend

```bash
cd devorbit-web
npm test              # vitest
npx vitest --coverage # coverage report
```

Đã cấu hình sẵn: @testing-library/react, @testing-library/jest-dom, jsdom, vitest

Ví dụ test:
```tsx
import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'

describe('CourseList', () => {
  it('renders course list heading', () => {
    render(<CourseList />)
    expect(screen.getByText('Danh sách môn học')).toBeDefined()
  })
})
```

### 7.3. Kiểm thử Mobile

```bash
cd devorbit-mobile
.\gradlew.bat test   # Windows
./gradlew test        # Linux/macOS
```

Testing stack khuyến nghị: JUnit, MockK, Compose Test

### 7.4. Kiểm thử End-to-End

Khuyến nghị dùng Playwright:
```bash
npm init playwright@latest -- --yes
npx playwright install chromium
npx playwright test
```

Test flows chính:
- Login: admin login -> dashboard visible
- Course browsing: browse courses -> 67 course cards
- GitHub scan: admin scan -> candidates found
- Student flow: register -> login -> browse -> bookmark

### 7.5. Kiểm thử tải (Load Testing)

Với k6:
```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 100 },
    { duration: '1m', target: 1000 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const res = http.get('http://localhost:8080/api/courses');
  check(res, {
    'status 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
  sleep(1);
}
```

### 7.6. Checklist kiểm thử bảo mật

| Hạng mục | Trạng thái | Mô tả |
|---|---|---|
| SQL Injection | OK | JPA parameterized queries |
| XSS | OK | React auto-escape, Nginx security headers |
| CSRF | OK | REST API, token-based auth |
| JWT | OK | Xác thực signature, expiry, HS256 |
| CORS | OK | SecurityConfig (giới hạn trong prod) |
| Rate Limiting | Can thieu | Cần cấu hình Nginx limit_req |
| Input Validation | OK | @Valid, @NotBlank, @Email trong DTOs |
| Brute Force Protection | Can thieu | Rate limiting cho login |

### 7.7. Checklist kiểm thử hiệu năng

| Hạng mục | Mục tiêu | Công cụ |
|---|---|---|
| Response time API | < 500ms (p95) | k6, JMeter |
| Concurrent users | 1000 | k6 |
| Requests/second | > 100 RPS | k6 |
| Memory usage | < 70% heap | JMX, Actuator |
| N+1 queries | 0 | Hibernate show_sql |
| Static asset load | < 1s | Lighthouse |

---



## SECTION 8: Triển khai và giám sát sản phẩm

### 8.1. Chiến lược triển khai

#### DevOrbit MVP: Docker Compose

```bash
docker compose up -d --build
docker compose ps
docker compose logs -f
docker compose down
docker compose down -v   # bao gom volume
```

#### Blue-Green Deployment

```
+-- Load Balancer (Nginx/HAProxy) --+
|         |                          |
v         v                          v
+-- Blue (active) --+    +-- Green (standby) --+
|   api:8080        |    |   api:8081           |
|   web:3000        |    |   web:3001           |
+-------------------+    +----------------------+
```

Quy trình:
1. Deploy phiên bản mới lên Green
2. Kiểm tra Green hoạt động ổn định
3. Chuyển traffic từ Blue sang Green qua load balancer
4. Giữ Blue trong 24h (rollback nếu cần)

### 8.2. CI/CD Pipeline với GitHub Actions

```yaml
name: DevOrbit CI/CD

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

env:
  DOCKER_IMAGE_API: ghcr.io/${{ github.repository }}/api
  DOCKER_IMAGE_WEB: ghcr.io/${{ github.repository }}/web

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: devorbit_db
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: testpassword
        ports:
          - 5432:5432
        health-check: pg_isready
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Backend Tests
        run: cd devorbit-api && ./mvnw test -B
        env:
          DATABASE_URL: jdbc:postgresql://localhost:5432/devorbit_db
          DATABASE_USERNAME: postgres
          DATABASE_PASSWORD: testpassword
      - name: Set up Node.js 20
        uses: actions/setup-node@v4
        with:
          node-version: '20'
      - name: Frontend Tests
        run: cd devorbit-web && npm ci && npm test -- --run

  build-and-push:
    needs: test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Log in to GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - name: Build and push API image
        uses: docker/build-push-action@v5
        with:
          context: ./devorbit-api
          push: true
          tags: ${{ env.DOCKER_IMAGE_API }}:latest,${{ env.DOCKER_IMAGE_API }}:${{ github.sha }}

  deploy:
    needs: build-and-push
    runs-on: ubuntu-latest
    environment: production
    steps:
      - name: Deploy via SSH
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          username: ${{ secrets.DEPLOY_USER }}
          key: ${{ secrets.DEPLOY_SSH_KEY }}
          script: |
            cd /opt/devorbit
            git pull
            docker compose pull
            docker compose up -d --remove-orphans
            docker image prune -f
```

### 8.3. Logging

#### Spring Boot Logging (Logback)

Tạo file logback-spring.xml trong devorbit-api/src/main/resources/:
```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %highlight(%-5level) [%thread] %cyan(%logger{36}) - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH:-/var/log/devorbit}/api.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH:-/var/log/devorbit}/api.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

#### Log Rotation với logrotate

```bash
# /etc/logrotate.d/devorbit
/var/log/devorbit/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    copytruncate
}
```

### 8.4. Monitoring

#### Prometheus + Grafana

Thêm Micrometer Prometheus dependency vào pom.xml:
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Cấu hình Spring Boot Actuator:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,info
  metrics:
    tags:
      application: devorbit-api
```

Prometheus config (prometheus.yml):
```yaml
scrape_configs:
  - job_name: 'devorbit-api'
    scrape_interval: 15s
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['api:8080']
```

Docker Compose monitoring:
```yaml
services:
  prometheus:
    image: prom/prometheus:latest
    ports: ["9090:9090"]
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:latest
    ports: ["3001:3000"]
    depends_on:
      - prometheus
```

### 8.5. Alerting

Prometheus Alertmanager rules:
```yaml
groups:
  - name: devorbit
    rules:
      - alert: APIInstanceDown
        expr: up{job="devorbit-api"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "DevOrbit API is down"

      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.05
        for: 5m
        labels:
          severity: warning

      - alert: HighResponseTime
        expr: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])) > 2
        for: 5m
        labels:
          severity: warning

      - alert: HighMemoryUsage
        expr: jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} > 0.85
        for: 10m
        labels:
          severity: warning
```

### 8.6. Backup Automation

Script backup hàng ngày:
```bash
#!/bin/bash
BACKUP_DIR="/opt/devorbit/backups"
DB_CONTAINER="devorbit-db"
DB_NAME="devorbit_db"
DB_USER="postgres"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/devorbit_${TIMESTAMP}.sql"

mkdir -p "${BACKUP_DIR}"
docker exec "${DB_CONTAINER}" pg_dump -U "${DB_USER}" "${DB_NAME}" > "${BACKUP_FILE}"
gzip "${BACKUP_FILE}"

# Xoa backup cu hon 30 ngay
find "${BACKUP_DIR}" -name "devorbit_*.sql.gz" -mtime +30 -delete
```

Cron job:
```bash
0 2 * * * /opt/devorbit/scripts/backup.sh >> /var/log/devorbit-backup.log 2>&1
0 3 * * 0 /opt/devorbit/scripts/backup-remote.sh >> /var/log/devorbit-remote-backup.log 2>&1
```

---



## SECTION 9: Hướng dẫn sử dụng và bảo trì hệ thống

### 9.1. API Documentation (Swagger UI)

Tài liệu API tự động được tạo bởi springdoc-openapi (OpenApiConfig.java).

Truy cập:
- Swagger UI: http://host:8080/swagger-ui.html
- OpenAPI JSON: http://host:8080/v3/api-docs

Cấu hình Swagger:
```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DevOrbit API")
                        .version("1.0.0")
                        .description("REST API for DevOrbit - GitHub repository management")
                        .contact(new Contact()
                                .name("DevOrbit Team")
                                .email("devorbit@uit.edu.vn")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development"),
                        new Server().url("/").description("Production / Docker")
                ));
    }
}
```

### 9.2. Thông tin đăng nhập Admin

| Trường | Giá trị |
|---|---|
| Username | admin |
| Password | admin (bcrypt hash trong data.sql) |
| Endpoint | POST /api/admin/auth/login |

CẢNH BÁO: Đổi mật khẩu ngay lập tức trong production.

### 9.3. Quy trình làm việc Admin

#### Bước 1: Đăng nhập
```bash
curl -X POST http://localhost:8080/api/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

#### Bước 2: Quản lý môn học
```bash
# Xem danh sach
curl -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8080/api/admin/courses
# Tao moi
curl -X POST -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"mamh":"CS999","tenmh":"Machine Learning","sotc":3,"lt":2,"th":1,"loaimonhoc":"TU_CHON"}' \
  http://localhost:8080/api/admin/courses
# Cap nhat
curl -X PUT -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/courses/1
# Vo hieu hoa
curl -X DELETE -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/courses/1
```

#### Bước 3: GitHub Scan
```bash
# Quet mot mon
curl -X POST -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"courseId":1,"query":"machine learning spring boot"}' \
  http://localhost:8080/api/admin/github/scan
# Quet tat ca
curl -X POST -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/github/scan-all
# Xem log
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/github/scan-logs
```

#### Bước 4: Phê duyệt Candidates
```bash
# Xem danh sach
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/repo-candidates
# Phe duyet
curl -X POST -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"techStacks":["Spring Boot","Java"],"description":"A comprehensive Spring Boot project"}' \
  http://localhost:8080/api/admin/repo-candidates/1/approve
# Tu choi
curl -X POST -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/admin/repo-candidates/1/reject
```

### 9.4. Quy trình làm việc Student

```bash
# Dang ky
curl -X POST http://localhost:8080/api/student/register \
  -H "Content-Type: application/json" \
  -d '{"studentCode":"SE123456","email":"student@uit.edu.vn","password":"securepass123","fullName":"Nguyen Van A"}'

# Dang nhap
curl -X POST http://localhost:8080/api/student/login \
  -H "Content-Type: application/json" \
  -d '{"studentCode":"SE123456","password":"securepass123"}'

# Duyet mon hoc
curl http://localhost:8080/api/courses
curl http://localhost:8080/api/courses/1
curl "http://localhost:8080/api/courses/1/repos?techStack=Spring+Boot"

# Bookmark
curl -X POST -H "Authorization: Bearer $STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"targetType":"COURSE","targetId":1}' \
  http://localhost:8080/api/student/bookmarks
```

### 9.5. Quy trình nâng cấp phiên bản

```bash
# 1. Backup database
docker exec devorbit-db pg_dump -U postgres devorbit_db > pre_upgrade_backup.sql
# 2. Pull code moi
git pull origin main
# 3. Build va deploy
docker compose build --no-cache
docker compose up -d
# 4. Kiem tra
curl http://localhost:8080/api/health
```

### 9.6. Quy trình Patch/Hotfix

```bash
git checkout -b hotfix/fix-login-error
# Sua loi, test
git add .
git commit -m "fix: login error special characters"
git checkout main
git merge hotfix/fix-login-error
docker compose build --no-cache api
docker compose up -d api
```

### 9.7. Nâng cấp hạ tầng

PostgreSQL upgrade:
```bash
docker exec devorbit-db pg_dump -U postgres devorbit_db > full_backup.sql
docker compose down db
# Update docker-compose.yml image: postgres:17-alpine
docker compose up -d db
cat full_backup.sql | docker exec -i devorbit-db psql -U postgres -d devorbit_db
```

### 9.8. Kế hoạch khôi phục thảm họa (Disaster Recovery)

Phân loại mức độ:
| Mức độ | Mô tả | RTO |
|---|---|---|
| Critical | Toàn bộ hệ thống ngừng | < 1 giờ |
| High | Tính năng chính không hoạt động | < 4 giờ |
| Medium | Tính năng phụ bị ảnh hưởng | < 24 giờ |
| Low | Không ảnh hưởng người dùng | < 1 tuần |

#### Kịch bản 1: Database corruption
```bash
docker compose stop api
gunzip -c /opt/devorbit/backups/devorbit_latest.sql.gz > restore.sql
cat restore.sql | docker exec -i devorbit-db psql -U postgres -d devorbit_db
docker compose start api
curl http://localhost:8080/api/health
```

#### Kịch bản 2: Application crash
```bash
docker compose logs --tail=100 api
docker compose restart api
# Neu van crash, rollback
docker compose down api
docker run -d --name devorbit-api devorbit-api:previous-tag
```

#### Kịch bản 3: Server failure
1. Provision server mới
2. Cài Docker, Git
3. Clone code: git clone https://github.com/huyhoang171106/DevOrbit.git
4. Copy .env.production
5. docker compose up -d
6. Restore từ backup mới nhất
7. Update DNS records

#### Post-Mortem Report Template
```markdown
# Post-Mortem: [Ten su co]
Ngay: YYYY-MM-DD
Muc do: Critical/High/Medium/Low
Thoi gian phat hien: HH:MM
Thoi gian khac phuc: HH:MM

## Tom tat
## Nguyen nhan goc re
## Timeline
## Hanh dong khac phuc
## Bai hoc kinh nghiem
```

---



## SECTION 10: Bảng phân bổ tài nguyên, danh sách công cụ và các lưu ý quan trọng

### 10.1. Yêu cầu phần cứng chi tiết

| Môi trường | CPU | RAM | Storage (SSD) | Network | OS | Số containers |
|---|---|---|---|---|---|---|
| **Development** | 2 cores | 4 GB | 20 GB | 100 Mbps | Windows/macOS/Linux | 3 (db, api, web) |
| **Staging** | 2 vCPU | 4 GB | 30 GB | 100 Mbps | Ubuntu 22.04 LTS | 3-4 (+ monitoring) |
| **Production (Low)** | 4 vCPU | 8 GB | 50 GB | 1 Gbps | Ubuntu 22.04 LTS | 5-7 (+ monitoring) |
| **Production (Medium)** | 8 vCPU | 16 GB | 100 GB | 1 Gbps | Ubuntu 22.04 LTS | 8-12 (+ scaling) |

Storage breakdown (Production Low):
| Thành phần | Dung lượng | Mô tả |
|---|---|---|
| PostgreSQL data | 5-10 GB | Dữ liệu database |
| Docker images | 2-3 GB | Images cho db, api, web |
| Application logs | 5-10 GB | Log rotation 30 ngày |
| Database backups | 10-20 GB | 30 ngày backup |
| OS + Docker | 15-20 GB | Ubuntu, Docker tools |
| **Tổng** | **~50 GB** | |

### 10.2. Danh sách phần mềm và công cụ

| Công cụ | Phiên bản | Mục đích | Bắt buộc? |
|---|---|---|---|
| Java (Eclipse Temurin) | 21 | Backend runtime | Yes |
| Maven | 3.9+ | Backend build (wrapper) | Yes |
| Node.js | 20 LTS | Frontend build | Yes |
| Docker Engine | 24+ | Container runtime | Yes |
| Docker Compose | 2.x | Multi-container orchestration | Yes |
| PostgreSQL | 16 | Database | Yes |
| Nginx | latest (alpine) | Reverse proxy, static serve | Yes |
| Git | latest | Version control | Yes |
| Gradle | 8.11.1 | Mobile build | No |
| Android SDK | 35 | Mobile build | No |
| Prometheus | latest | Metrics collection | Optional |
| Grafana | latest | Metrics visualization | Optional |
| Certbot | latest | SSL certificate | Yes (HTTPS) |
| k6 | latest | Load testing | No |
| Playwright | latest | E2E testing | No |

### 10.3. Danh sách rủi ro (Risk Register)

| Rủi ro | Xác suất | Tác động | Mức độ | Biện pháp giảm thiểu |
|---|---|---|---|---|
| JWT secret bị lộ | Thấp | Nghiêm trọng | Cao | Env var, không hardcode, rotate định kỳ |
| Database corruption | Thấp | Nghiêm trọng | Cao | Backup hàng ngày, WAL archiving |
| GitHub API rate limit | Trung bình | Cao | Cao | Dùng token, cache, xử lý HTTP 429 |
| DDoS attack | Thấp | Cao | Trung bình | Rate limiting, Cloudflare/WAF |
| Credentials trong .env commit | Trung bình | Nghiêm trọng | Cao | .gitignore, pre-commit hook |
| SSL cert hết hạn | Trung bình | Cao | Cao | Certbot auto-renew, monitoring |
| Lỗi migration database | Trung bình | Cao | Cao | Test staging, backup trước migration |
| Lỗi bảo mật dependency | Trung bình | Cao | Cao | npm audit, Dependabot |
| Single point of failure | Thấp | Nghiêm trọng | Cao | Docker Swarm/K8s, multi-host |

### 10.4. Troubleshooting Guide

#### Docker Compose fails to start
```bash
# Kiem tra port conflicts
netstat -ano | findstr :5432   # Windows
netstat -tulpn | grep 5432     # Linux
# Kiem tra Docker version
docker --version
docker compose version
# Kiem tra .env file
Test-Path .env   # Windows PowerShell
# Kiem tra logs
docker compose logs
```

#### API returns 403 Forbidden
```bash
# Kiem tra token
curl -I -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/admin/courses
# Decode JWT payload
echo $TOKEN | cut -d. -f2 | base64 -d
# Kiem tra Authorization header format: "Bearer <token>"
```

#### Frontend shows blank page
- Kiểm tra browser console (F12) - lỗi JavaScript?
- Kiểm tra Network tab - API call thành công?
- Kiểm tra VITE_API_BASE_URL = /api
- Kiểm tra CORS error
- Kiểm tra Nginx config: try_files $uri $uri/ /index.html

#### Database connection refused
```bash
docker ps | grep devorbit-db
docker logs devorbit-db --tail=50
docker exec devorbit-api ping db
docker exec devorbit-db psql -U postgres -d devorbit_db -c "SELECT 1;"
df -h /var/lib/docker/volumes/
```

#### GitHub scan returns empty
```bash
docker exec devorbit-api env | grep GITHUB_TOKEN
curl -H "Authorization: Bearer $GITHUB_TOKEN" https://api.github.com/rate_limit
docker compose logs api | grep -i github
```

#### Mobile app can't connect
- Emulator: API_BASE_URL = http://10.0.2.2:8080 (10.0.2.2 = localhost host)
- Real device: API_BASE_URL = http://<IP>:8080 (cùng network WiFi)
- Android cleartext traffic: thêm android:usesCleartextTraffic="true"

#### Build fails
```bash
java -version  # Must be 21
node --version # Must be 20 LTS
# Maven wrapper
cd devorbit-api && .\mvnw.cmd --version
# Clean npm
cd devorbit-web && rm -rf node_modules package-lock.json && npm install
```

### 10.5. Mẹo tối ưu hiệu năng

#### HikariCP Connection Pool
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      leak-detection-threshold: 60000
```

Công thức: pool_size = (core_count * 2) + effective_spindle_count

#### PostgreSQL Tuning (cho 4GB RAM)
```
shared_buffers = 1GB              # 25% RAM
effective_cache_size = 3GB        # 75% RAM
work_mem = 64MB
maintenance_work_mem = 256MB
random_page_cost = 1.1            # SSD
max_connections = 100
```

#### JVM Tuning
```
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-Xms512m -Xmx1024m
-XX:+HeapDumpOnOutOfMemoryError
```

### 10.6. Security Checklist cho Production Go-Live

- [ ] JWT Secret: Da thay doi tu mac dinh (openssl rand -base64 32)
- [ ] Admin password: Da thay doi tu 'admin'
- [ ] PostgreSQL password: Da thay doi
- [ ] HTTPS: Da enable (Let's Encrypt hoac paid certificate)
- [ ] CORS: Da set explicit allowed origins
- [ ] JPA DDL: Da set validate (khong dung update)
- [ ] Seed data: Da xoa hoac comment data.sql cho production
- [ ] Rate Limiting: Da cau hinh Nginx limit_req
- [ ] Fail2ban: Da cai dat cho SSH
- [ ] Unattended-upgrades: Da cau hinh
- [ ] Database WAL archiving: Da bat cho PITR
- [ ] Monitoring + Alerting: Da cau hinh
- [ ] Backup: Da test restore thanh cong
- [ ] Docker security: non-root user, gioi han resource
- [ ] Network Security: Port 5432/8080 khong expose truc tiep
- [ ] Firewall: Chi mo port 80, 443, 22
- [ ] Dependency scanning: npm audit, Dependabot
- [ ] Incident Response Plan: Runbook da san sang

---

## Phụ lục

### A. Các file cấu hình quan trọng

| File | Đường dẫn | Mô tả |
|---|---|---|
| Docker Compose | docker-compose.yml | Orchestration toàn bộ services |
| Backend config | devorbit-api/src/main/resources/application.yaml | Spring Boot config chính |
| Seed data | devorbit-api/src/main/resources/data.sql | 67 courses + 1 admin |
| Dockerfile API | devorbit-api/Dockerfile | Multi-stage build backend |
| Dockerfile Web | devorbit-web/Dockerfile | Multi-stage build frontend |
| Nginx config | devorbit-web/nginx.conf | Reverse proxy + static serve |
| Vite config | devorbit-web/vite.config.ts | Frontend build config |
| Env mẫu | .env.example | Mẫu biến môi trường |
| POM | devorbit-api/pom.xml | Maven dependencies |
| Package.json | devorbit-web/package.json | NPM dependencies |
| Security config | devorbit-api/.../config/SecurityConfig.java | Spring Security |
| JWT filter | devorbit-api/.../config/JwtAuthenticationFilter.java | JWT authentication |
| API client | devorbit-web/src/lib/api.ts | Frontend API client |
| Mobile config | devorbit-mobile/app/build.gradle.kts | Mobile build config |
| OpenAPI config | devorbit-api/.../config/OpenApiConfig.java | Swagger config |

### B. Lệnh hữu ích

```bash
# Docker
docker compose up -d --build          # Build va chay
docker compose down                   # Dung
docker compose logs -f                # Xem logs
docker compose logs --tail=100 api    # Logs backend
docker exec -it devorbit-db psql -U postgres -d devorbit_db  # DB console

# Backend
cd devorbit-api && .\mvnw.cmd clean package -DskipTests   # Build
cd devorbit-api && .\mvnw.cmd test                        # Test

# Frontend
cd devorbit-web && npm ci                                   # Install
cd devorbit-web && npm run build                            # Build
cd devorbit-web && npm test                                 # Test

# Backup/Restore
docker exec devorbit-db pg_dump -U postgres devorbit_db > backup.sql
cat backup.sql | docker exec -i devorbit-db psql -U postgres -d devorbit_db

# Health Check
curl http://localhost:8080/api/health
curl http://localhost:3000/api/health

# Git
git clone https://github.com/huyhoang171106/DevOrbit.git
git pull
git checkout -b feature/xxx
```

### C. Tài liệu tham khảo

- Spring Boot 4.0.6 Documentation: https://docs.spring.io/spring-boot/docs/4.0.6/reference/html/
- React 19 Documentation: https://react.dev/
- Vite Documentation: https://vitejs.dev/
- Docker Documentation: https://docs.docker.com/
- PostgreSQL 16 Documentation: https://www.postgresql.org/docs/16/
- Nginx Documentation: https://nginx.org/en/docs/
- Jetpack Compose: https://developer.android.com/develop/ui/compose
