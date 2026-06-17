# DevOrbit - Cẩm Nang Ôn Tập & Bảo Vệ Đồ Án (Feynman Technique)

> **Tác giả**: Đồng hành cùng bạn bởi Antigravity (AI Pair Programming Partner)
> **Phương pháp**: Feynman Technique — Giải thích bằng ngôn ngữ đơn giản trước, đi sâu vào kỹ thuật và codebase thực tế sau.
> **Mục tiêu**: Giúp bạn tự tin giải thích dự án, trả lời mọi câu hỏi của Hội đồng phản biện, và sẵn sàng sửa code/thêm tính năng ngay tại chỗ.

---

## 1. Bản đồ tổng thể DevOrbit

### 1.1 Giải thích như nói với người chưa biết lập trình
Hãy tưởng tượng **DevOrbit** giống như một **Trung tâm hỗ trợ học tập số** dành cho sinh viên chuyên ngành Kỹ thuật Phần mềm (KTPM) tại trường Đại học Công nghệ Thông tin (UIT). 

Trước đây, khi sinh viên muốn học một môn chuyên ngành (ví dụ: *Công nghệ phần mềm* - SE104), họ gặp rất nhiều khó khăn:
1. Không biết môn này cần học trước những môn gì (điều kiện tiên quyết).
2. Không biết tìm các dự án mẫu (repository GitHub) chất lượng của khóa trước ở đâu.
3. Không có ai giải đáp thắc mắc về đề cương chi tiết hoặc tài liệu học tập ngay lập tức.
4. Không có phòng thảo luận trực tuyến thời gian thực để trao đổi bài vở.

**DevOrbit giải quyết vấn đề này như thế nào?**
* **Kệ sách thông minh (Cơ sở dữ liệu PostgreSQL + Supabase)**: Nơi lưu trữ thông tin của toàn bộ môn học, mối quan hệ giữa các môn học, các tài khoản sinh viên, các bài note cá nhân, và các repository GitHub mẫu đã được quản trị viên phê duyệt.
* **Người thủ thư đắc lực (Backend Java Spring Boot)**: Chạy ngầm để nhận yêu cầu từ sinh viên, kiểm tra xem sinh viên đó có đăng nhập hợp lệ không (dùng thẻ thông hành JWT), truy vấn dữ liệu từ database, gọi dịch vụ AI để tư vấn, gửi tin nhắn chat, hoặc tự động quét GitHub để tìm thêm các repository mẫu mới.
* **Giao diện tương tác (Frontend React)**: Website trực quan giúp sinh viên xem bản đồ môn học (Knowledge Graph), lưu trữ note học tập, chat trực tiếp với các sinh viên khác, và trò chuyện với Trợ lý ảo (AI Tutor).
* **Trợ lý ảo (AI Tutor với RAG)**: Giống như một người thầy "mở sách" (Retrieval-Augmented Generation). Khi bạn hỏi: "Môn SE104 học những gì?", AI sẽ lục tìm trong kho đề cương môn học của hệ thống trước, sau đó sắp xếp thông tin lại và trả lời bạn một cách chính xác nhất, tránh việc tự bịa ra thông tin (hallucination).
* **Bảng tin nhanh (WebSocket/STOMP)**: Cho phép sinh viên gửi và nhận tin nhắn trong phòng chat cộng đồng tức thời mà không cần phải tải lại trang.

### 1.2 Sơ đồ ASCII dòng chảy hệ thống

```text
                                        [ Client Browser ]
                                                │
                                    ┌───────────┴───────────┐
                       HTTP Requests│                       │WebSockets (STOMP)
                        (JWT Bearer)│                       │(Presence & Chat)
                                    ▼                       ▼
                        ┌───────────────────────────────────────┐
                        │          JwtAuthenticationFilter      │  (Spring Security)
                        └───────────────────┬───────────────────┘
                                            │
                                            ▼
                        ┌───────────────────────────────────────┐
                        │       REST Controllers / Endpoints    │  (Spring Web)
                        └───────────────────┬───────────────────┘
                                            │
                             ┌──────────────┴──────────────┐
                             ▼                             ▼
                  ┌────────────────────┐         ┌────────────────────┐
                  │    Core Services   │         │    AI/RAG Services │  (Virtual Threads)
                  └──────────┬─────────┘         └─────────┬──────────┘
                             │                             │
                             │   ┌─────────────────────────┘
                             ▼   ▼
                        ┌───────────────────────────────────────┐
                        │      JPA Repositories (Data Layer)    │  (Spring Data JPA)
                        └───────────────────┬───────────────────┘
                                            │
                                            ▼
                        ┌───────────────────────────────────────┐
                        │        PostgreSQL Database            │  (pgvector + FTS)
                        └───────────────────┬───────────────────┘
                                            │
                                            ▼
                        ┌───────────────────────────────────────┐
                        │          Supabase Hardening           │  (RLS & Storage Policies)
                        └───────────────────────────────────────┘
```

---

## 2. Kiến trúc hệ thống

Dự án áp dụng kiến trúc phân tầng tiêu chuẩn (Layered Architecture) kết hợp với các dịch vụ bổ trợ bên ngoài (GitHub API, Fireworks AI, Firecrawl).

### 2.1 Ý nghĩa các tầng trong codebase
* **Controller (Tầng giao diện lập trình - Web Layer)**: Đóng vai trò là "người tiếp tân". Nhận các HTTP requests từ frontend, phân tích tham số (`@RequestParam`, `@PathVariable`, `@RequestBody`), gọi tầng Service xử lý, và trả về dữ liệu định dạng JSON cùng mã trạng thái HTTP thích hợp (200 OK, 201 Created, 401 Unauthorized, v.v.).
* **Service (Tầng nghiệp vụ - Business Logic Layer)**: Đóng vai trò là "não bộ". Nơi chứa toàn bộ logic xử lý nghiệp vụ, tính toán, kiểm tra quyền hạn, điều phối giao dịch (`@Transactional`), gọi các tích hợp ngoài, và đảm bảo dữ liệu toàn vẹn trước khi ghi xuống DB.
* **Repository (Tầng truy cập dữ liệu - Data Access Layer)**: Đóng vai trò là "thủ kho". Là các interface kế thừa `JpaRepository` của Spring Data JPA. Giúp thực hiện các câu lệnh truy vấn SQL (được Spring tự động sinh ra dựa trên tên phương thức hoặc qua `@Query`).
* **Entity (Tầng thực thể - Domain Model)**: Ánh xạ trực tiếp 1-1 với các bảng trong PostgreSQL thông qua JPA annotations (`@Entity`, `@Table`, `@Column`).
* **DTO (Data Transfer Object - Đối tượng chuyển đổi dữ liệu)**: Các record hoặc class trung gian dùng để giới hạn thông tin truyền tải giữa Client và Server (ví dụ: không trả về mật khẩu hash khi lấy thông tin profile).
* **Configuration (Tầng cấu hình - Config Layer)**: Khởi tạo các Spring Bean và thiết lập thông số cho toàn hệ thống (Bảo mật, WebSockets, Thread Pools, Cache, v.v.).
* **Security Filter (Bộ lọc bảo vệ)**: Interceptor chặn các request gửi đến để phân tích token JWT, xác thực danh tính người dùng trước khi chuyển giao tiếp cho Controller.
* **Exception Handler (Bộ xử lý lỗi)**: Gom tất cả lỗi phát sinh trong hệ thống về một mối để định dạng lại phản hồi JSON đẹp mắt gửi về Client (ví dụ: `BadRequestException` trả về 400 Bad Request).

### 2.2 Luồng phụ thuộc giữa các tầng
```text
Controller ──> Service ──> Repository ──> Entity / PostgreSQL DB
```
* **Quy tắc bắt buộc**: Tầng trên gọi tầng dưới, không có chiều ngược lại. Controller tuyệt đối không gọi trực tiếp Repository hay database.
* **Vì sao không đặt logic trong Controller?** Controller chỉ làm nhiệm vụ tiếp nhận đầu vào và trả về đầu ra. Nếu đặt logic nghiệp vụ tại đây, code sẽ bị lặp lại, khó viết Unit Test (vì phụ thuộc vào Servlet Context), và làm hệ thống mất đi tính module hóa.

### 2.3 Bảng phân loại thành phần hệ thống

| Thành phần | Trách nhiệm | Được gọi bởi | Gọi đến | Ví dụ thực tế trong codebase |
| :--- | :--- | :--- | :--- | :--- |
| **Controller** | Tiếp nhận HTTP API | Client (Frontend/Postman) | Service | [StudentAuthController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentAuthController.java) |
| **Service** | Xử lý logic nghiệp vụ | Controller, Event Listener | Repository, External Client | [StudentAuthService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/StudentAuthService.java) |
| **Repository** | Truy vấn CSDL | Service | Database (Hibernate/PostgreSQL) | [CourseRepository](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/CourseRepository.java) |
| **Entity** | Ánh xạ CSDL | Repository, Service | Không gọi ai | [Course](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/Course.java) |
| **DTO** | Đóng gói dữ liệu | Controller, Service | Không gọi ai | [StudentProfileResponse](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/student/StudentProfileResponse.java) |
| **Config** | Cấu hình hệ thống | Spring Boot Framework | Không gọi ai | [SecurityConfig](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SecurityConfig.java) |
| **Filter** | Xác thực JWT | Spring Security Chain | Service, Filter Chain | [JwtAuthenticationFilter](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/JwtAuthenticationFilter.java) |

---

## 3. Năm luồng nghiệp vụ quan trọng nhất

### Luồng 1: Đăng ký, đăng nhập & xác thực JWT

#### Giải thích Feynman (Cho học sinh cấp hai)
> Tưởng tượng việc vào trường học cần có thẻ học sinh. Khi bạn đăng ký mới (Register), trường sẽ gửi một mã số xác nhận (OTP) về hòm thư của bạn để xác minh bạn đúng là chủ nhân của email đó. Khi bạn nhập đúng OTP, tài khoản của bạn được kích hoạt.
> Khi bạn đăng nhập (Login) bằng mật khẩu đúng, trường sẽ phát cho bạn một tấm **thẻ thông hành đặc biệt (JWT)**. Trên tấm thẻ này có ghi rõ tên bạn và quyền hạn của bạn. Tấm thẻ này được niêm phong bằng chữ ký số của trường. Mỗi lần bạn muốn vào thư viện hay phòng máy (gọi API được bảo vệ), bạn chỉ cần trình tấm thẻ này ra. Bảo vệ sẽ nhìn con dấu niêm phong (chữ ký số) để cho bạn vào ngay lập tức mà không cần hỏi lại mật khẩu. Khi bạn đi ra và trả thẻ (Logout), thẻ đó sẽ bị đưa vào danh sách đen (Revoked Tokens) để không ai có thể nhặt được và dùng lại.

#### Sơ đồ luồng kỹ thuật
```text
Frontend                        Controller                      Service                       Repository / DB
   │                                │                              │                                 │
   │─── POST /login (Credentials) ──>                              │                                 │
   │                                ├─── login() ─────────────────>│                                 │
   │                                │                              ├─── findByStudentCode() ────────>│
   │                                │                              │<── StudentUser (Password Hash)──│
   │                                │                              │                                 │
   │                                │                              ├─── BCrypt password match check  │
   │                                │                              ├─── generateToken(JWT)           │
   │                                │                              │                                 │
   │                                ├<── StudentAuthResponse ──────│                                 │
   │<── JWT + Profile (HTTP 200) ───│                              │                                 │
```

#### File và Method tham gia
1. [StudentAuthController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentAuthController.java) -> `login()`, `register()`, `verifyOtp()`, `logout()`.
2. [StudentAuthService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/StudentAuthService.java) -> `login()`, `register()`, `verifyOtp()`, `logout()`.
3. [JwtService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/JwtService.java) -> `generateToken()`, `isTokenValid()`, `extractUsername()`.
4. [RevokedTokenStore](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/RevokedTokenStore.java) -> `revoke()`, `isRevoked()`.
5. [StudentUserRepository](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/StudentUserRepository.java) -> `findByStudentCode()`, `findByEmail()`.

#### Dữ liệu đi qua hệ thống
* **Request DTO**: `StudentLoginRequest` (chứa `studentCode`, `password`) được validate bằng `@Valid` và `@NotBlank`.
* **Database Query**: `SELECT * FROM student_users WHERE student_code = ?` để lấy `StudentUser`.
* **Response DTO**: `StudentAuthResponse` (chứa `token` JWT, `id`, `studentCode`, `fullName`, `email`, `avatar`).
* **HTTP Status**: 200 OK nếu thành công, 401 Unauthorized nếu sai thông tin hoặc tài khoản chưa kích hoạt.

#### Các trường hợp lỗi
* Nhập sai mã sinh viên hoặc mật khẩu: Trả về lỗi 401 với thông báo `"Tên đăng nhập hoặc mật khẩu không đúng"`.
* Tài khoản chưa kích hoạt qua OTP: Trả về lỗi 401 với thông báo `"Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email."`.
* Gửi quá nhiều yêu cầu gửi OTP hoặc đăng nhập thất bại liên tục: Bị chặn bởi `loginRateLimitService` hoặc `otpRateLimitService` trả về 429 Too Many Requests (chống brute-force).

#### Câu hỏi giảng viên có thể hỏi & Câu trả lời ngắn gọn khi bảo vệ

1. **Hỏi: Cơ chế Rate Limiting trong đăng nhập hoạt động thế nào trong dự án?**
   * **Trả lời**: Hệ thống sử dụng [LoginRateLimitService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/LoginRateLimitService.java) lưu trong bộ nhớ tạm thời (`ConcurrentHashMap`) đếm số lần đăng nhập sai theo cặp (StudentCode, IP). Nếu vượt quá số lần cấu hình (ví dụ: 5 lần), hệ thống sẽ từ chối xử lý trong một khoảng thời gian nhất định để ngăn chặn tấn công dò mật khẩu.
2. **Hỏi: Tại sao mật khẩu lưu trong DB của bạn lại là các chuỗi ký tự ngẫu nhiên? Bạn dùng thuật toán gì?**
   * **Trả lời**: Mật khẩu được mã hóa một chiều bằng thuật toán BCrypt thông qua lớp `BCryptPasswordEncoder` được cấu hình tại [SecurityConfig.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SecurityConfig.java#L77-L79). Thuật toán này tự động sinh muối (salt) ngẫu nhiên cho mỗi lần băm nên hai tài khoản có cùng mật khẩu vẫn có chuỗi hash hoàn toàn khác nhau trong database.
3. **Hỏi: Khi người dùng nhấn Đăng xuất, bạn xử lý Token JWT như thế nào trên Server khi mà JWT bản chất là không trạng thái (stateless)?**
   * **Trả lời**: Em sử dụng class [RevokedTokenStore](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/RevokedTokenStore.java). Khi gọi endpoint `/logout`, Token JWT gửi lên sẽ được bóc tách mã định danh độc nhất `jti` (JWT ID) cùng với thời điểm hết hạn `expiresAt`. Hệ thống ghi nhận `jti` này vào một `ConcurrentHashMap` in-memory. Bộ lọc `JwtAuthenticationFilter` sẽ kiểm tra chéo, nếu token nào nằm trong danh sách đen này sẽ bị từ chối ngay lập tức, ngay cả khi nó vẫn còn thời hạn hiệu lực.
4. **Hỏi: Làm sao bạn giải quyết bài toán dọn dẹp bộ nhớ cho danh sách Token đã bị thu hồi trong `RevokedTokenStore`?**
   * **Trả lời**: Tại lớp `RevokedTokenStore`, hàm `evictExpired()` (hoặc kiểm tra lười biếng trong `isRevoked()`) sẽ tự động xóa các token đã quá hạn tự nhiên của nó ra khỏi Map để tránh việc bản đồ in-memory phình to gây tràn bộ nhớ RAM (Memory Leak).
5. **Hỏi: Nếu StudentUser đăng ký bằng một mã sinh viên đã tồn tại nhưng chưa kích hoạt email (active = false), hệ thống xử lý thế nào?**
   * **Trả lời**: Theo logic xử lý tại [StudentAuthService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/StudentAuthService.java#L85-L94), nếu mã sinh viên đã tồn tại nhưng tài khoản chưa kích hoạt, hệ thống sẽ thực hiện xóa OTP cũ của email đó, xóa bản ghi tài khoản chưa kích hoạt cũ, rồi tiến hành đăng ký mới từ đầu để tránh rác database.
6. **Hỏi: Sự khác biệt lớn nhất giữa `@Valid` ở Controller và các Annotation validate trong Entity (như `@Column(nullable = false)`) là gì?**
   * **Trả lời**: `@Valid` ở Controller thực hiện kiểm tra định dạng dữ liệu đầu vào ngay tại cửa ngõ tầng Web (ví dụ: email có đúng định dạng, mật khẩu có bị trống). Còn `@Column(nullable = false)` là ràng buộc tầng CSDL do JPA/Hibernate định nghĩa để sinh cấu trúc bảng DB, hoạt động khi dữ liệu chuẩn bị lưu xuống. Thực hiện kiểm tra sớm ở Controller giúp giảm tải truy cập DB không cần thiết.
7. **Hỏi: OTP được sinh ra như thế nào và thời gian hết hạn được cấu hình ở đâu?**
   * **Trả lời**: OTP là một chuỗi số ngẫu nhiên gồm 6 chữ số sinh bằng `SecureRandom` trong lớp `StudentAuthService.java#L310-L313`. Thời gian hết hạn mặc định là 10 phút, được cấu hình động qua thuộc tính `app.otp.expiration-minutes` nạp từ file `application.yaml` thông qua annotation `@Value`.
8. **Hỏi: Nếu token JWT bị sửa đổi một ký tự trên đường truyền, Spring Security có phát hiện ra không? Tại sao?**
   * **Trả lời**: Có phát hiện ra. Vì ở lớp [JwtService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/JwtService.java#L88-L94), khi phân tích token (parseToken), thư viện `jjwt` sử dụng `SecretKey` (HMAC-SHA256) để tính toán lại chữ ký số (Signature) của phần Header và Payload rồi so sánh với chữ ký gửi lên. Nếu token bị sửa đổi, chữ ký sẽ không khớp, hàm `parseSignedClaims()` ném ra exception và hệ thống lập tức trả về lỗi 401.

---

### Luồng 2: Quản lý môn học & Repository liên kết

#### Giải thích Feynman (Cho học sinh cấp hai)
> Hãy tưởng tượng hệ thống như một kho sách của trường. Mỗi quyển sách đại diện cho một **Môn học (Course)** chứa thông tin về mã môn, tên môn, số tín chỉ. Tuy nhiên, chỉ đọc lý thuyết thì chán, sinh viên cần những tài liệu thực hành hoặc đồ án mẫu. 
> Do đó, hệ thống cho phép liên kết môn học đó với các **Thư mục code mẫu (GitHub Repository)**. Quản trị viên (Admin) phê duyệt các repository chất lượng từ sinh viên gửi lên, gắn thẻ nó thuộc về môn học nào, rồi lưu lại. Sinh viên vào trang môn học sẽ thấy ngay danh sách các code mẫu chất lượng để tham khảo trực tiếp, giúp việc học thực tế hơn nhiều.

#### Sơ đồ luồng kỹ thuật
```text
Frontend                         Controller                     Service                         Repository
   │                                 │                             │                                 │
   │─── GET /api/courses (active) ──>│                             │                                 │
   │                                 ├─── getActiveCourses() ─────>│                                 │
   │                                 │                             ├── findActiveWithRepoCount...() ─>│
   │                                 │                             │                                 ├─── Run SQL JOIN
   │                                 │                             │<── CourseSummaryResponse List ──│
   │                                 ├<── CourseSummaryResponse ───│                                 │
   │<── Course List (HTTP 200) ──────│                                                               │
```

#### File và Method tham gia
1. [PublicCourseController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/PublicCourseController.java) -> `getActiveCourses()`, `getCourseDetail()`.
2. [CourseService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/CourseService.java) -> `getActiveCourseSummaries()`, `getCourseDetail()`.
3. [CourseRepository](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/CourseRepository.java) -> `findActiveWithRepoCountSortedByRepoCount()`.
4. [GithubRepoRepository](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/GithubRepoRepository.java) -> `findByCourseIdAndActiveTrue()`.

#### Dữ liệu đi qua hệ thống
* **Database Query**: Câu lệnh JPQL thực hiện `LEFT JOIN` giữa bảng `Course` và bảng `GithubRepo` (lọc các repo `active = true`), nhóm theo các thuộc tính của Course và đếm `COUNT(r)` số lượng repository liên kết.
* **Response DTO**: `CourseSummaryResponse` chứa thông tin môn học kèm trường `repoCount` (số lượng repo GitHub liên kết).

#### Các trường hợp lỗi
* Môn học không tồn tại hoặc bị ẩn (isOpen = false): API trả về lỗi 400 Bad Request hoặc 404 Not Found kèm thông điệp rõ ràng.
* Môn học không có repository nào liên kết: Câu truy vấn `LEFT JOIN` vẫn trả về thông tin môn học bình thường với trường `repoCount` bằng 0 nhờ cơ chế của phép join trái.

#### Câu hỏi giảng viên có thể hỏi & Câu trả lời ngắn gọn khi bảo vệ

1. **Hỏi: Tại sao bạn dùng `LEFT JOIN` thay vì `INNER JOIN` trong câu truy vấn lấy danh sách môn học kèm số lượng repository?**
   * **Trả lời**: Vì nếu sử dụng `INNER JOIN`, những môn học mới khởi tạo hoặc chưa có bất kỳ repository nào được liên kết sẽ bị loại bỏ hoàn toàn khỏi danh sách kết quả. Sử dụng `LEFT JOIN` đảm bảo tất cả các môn học đang hoạt động đều được hiển thị, môn nào chưa có repo thì số lượng hiển thị sẽ là 0.
2. **Hỏi: Trường `stt` trong class `Course` đóng vai trò gì? Tại sao lại dùng `@Column(name = "stt")` cho thuộc tính `id`?**
   * **Trả lời**: Vì cấu trúc dữ liệu môn học được đồng bộ trực tiếp từ sơ đồ cơ sở dữ liệu gốc của nhà trường. Trong bảng dữ liệu gốc, cột khóa chính tự tăng được đặt tên là `stt` (Số thứ tự). Em dùng annotation `@Column(name = "stt")` để ánh xạ chính xác thuộc tính `id` kiểu Java với cột `stt` trong database PostgreSQL.
3. **Hỏi: Dữ liệu của trường `topics` trong Entity `Course` có kiểu dữ liệu là gì? Tại sao bạn lại chọn kiểu đó?**
   * **Trả lời**: Trường `topics` sử dụng kiểu dữ liệu `JsonNode` của thư viện Jackson, được chú thích bằng `@JdbcTypeCode(SqlTypes.JSON)`. Em chọn kiểu này vì danh sách các chủ đề học tập của môn học là động, có cấu trúc phân tầng phức tạp (không cố định số cột). Lưu trữ dưới dạng JSON giúp dễ dàng đọc ghi và đồng bộ cấu trúc phức tạp từ DB lên mà không cần tạo thêm một bảng liên kết trung gian rườm rà.
4. **Hỏi: Tại sao bạn lại gom thông tin trả về của môn học vào một DTO như `CourseSummaryResponse` thay vì trả về thẳng thực thể `Course`?**
   * **Trả lời**: Trả về trực tiếp thực thể `Course` có ba nhược điểm lớn: Một là gây dư thừa dữ liệu (chuyển các trường không cần thiết cho giao diện danh sách gây tốn băng thông); Hai là dễ dẫn đến lỗi vòng lặp vô hạn khi tuần tự hóa JSON (nếu thực thể có quan hệ hai chiều); Ba là lộ các trường nhạy cảm hoặc logic nghiệp vụ nội bộ. DTO giúp đóng gói dữ liệu tối ưu nhất cho frontend.
5. **Hỏi: Trong `CourseRepository.java`, làm sao Spring Data JPA biết cách triển khai phương thức `findByMaMH(String maMH)` mà không cần viết SQL?**
   * **Trả lời**: Spring Data JPA sử dụng cơ chế dịch tên phương thức (Query Creation from Method Names). Khi nó thấy tiền tố `findBy` kết hợp với thuộc tính `MaMH` (tương ứng với trường `maMH` trong Entity), nó sẽ tự động phân tích cú pháp và tạo ra câu lệnh SQL: `SELECT * FROM courses WHERE mamh = ?` tại thời điểm runtime.
6. **Hỏi: Chức năng `isOpen` của môn học hoạt động như thế nào?**
   * **Trả lời**: Biến `isOpen` đại diện cho việc môn học đó có đang được giảng dạy tích cực trong chương trình đào tạo hiện tại hay không. Endpoint công khai của sinh viên chỉ lấy các môn học có `isOpen = true`, trong khi màn hình Admin có thể lấy toàn bộ để quản lý.
7. **Hỏi: Làm thế nào bạn tránh lỗi N+1 query khi lấy danh sách repository liên kết của nhiều môn học cùng lúc?**
   * **Trả lời**: Em thực hiện gom toàn bộ mã môn học thành một tập hợp rồi gọi hàm truy vấn lô `findByCourseIdInAndActiveTrue` trong một câu lệnh duy nhất thay vị lặp qua từng môn học rồi thực hiện truy vấn cơ sở dữ liệu riêng lẻ.
8. **Hỏi: Ràng buộc khóa ngoại giữa môn học và các repository liên kết được cấu hình như thế nào?**
   * **Trả lời**: Trong bảng `github_repos`, trường `course_id` được định nghĩa là khóa ngoại tham chiếu đến cột `stt` của bảng `courses`. Thiết lập Cascade delete được cấu hình ở mức CSDL và JPA để tự động dọn dẹp các repo liên kết hoặc đưa về trạng thái thích hợp khi môn học bị xóa.

---

### Luồng 3: Quét tự động Repository qua GitHub API

#### Giải thích Feynman (Cho học sinh cấp hai)
> Thay vì quản trị viên phải đi tìm từng dự án code mẫu trên mạng để copy vào hệ thống (rất mất công và dễ bỏ sót), hệ thống có một chú robot thông minh chạy ngầm (Background Worker). 
> Chú robot này sẽ sử dụng mã thông hành (GitHub Token) gõ cửa thư viện GitHub. Robot tìm kiếm theo công thức: *"Tìm tất cả mã nguồn công khai có chứa chữ 'UIT' và mã môn học 'SE104'"*. Sau đó, robot lọc ra các dự án xịn nhất (dựa trên lượt yêu thích - Stars, không lấy bản clone/fork), tải sơ lược nội dung giới thiệu (README) và sơ đồ thư mục (File Tree) về làm hồ sơ ứng viên (RepoCandidate). Quản trị viên chỉ cần ngồi click duyệt để đưa các ứng viên xuất sắc này lên kệ sách chính thức.

#### Sơ đồ luồng kỹ thuật
```text
Admin Web Console                 Controller                      Service                        GitHub REST API
   │                                  │                              │                                 │
   │── POST /scan-all (Start bulk) ──>│                              │                                 │
   │                                  ├─── scanAll() ────────────────>│                                 │
   │                                  │     (Virtual Thread starts)  │                                 │
   │<── HTTP 202 Accepted (Response) ─│                              │─── GET /search/repositories ───>│
   │    (Admin continues working)     │                              │    (Query: "SE104" uit)         │
   │                                  │                              │<── JSON Response ───────────────│
   │                                  │                              ├─── filter forks & duplicates    │
   │                                  │                              ├─── fetch README & File Tree     │
   │                                  │                              ├─── save candidates in DB        │
```

#### File và Method tham gia
1. [AdminGithubController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminGithubController.java) -> `scan()`, `scanAll()`, `getScanLogs()`.
2. [GithubScanService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/GithubScanService.java) -> `scanCourse()`, `scanAll()`, `fetchRepositoryContext()`, `fetchReadmeExcerpt()`.
3. [GithubClientConfig](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/GithubClientConfig.java) -> Khởi tạo bean `WebClient` gọi sang api.github.com.

#### Dữ liệu đi qua hệ thống
* **Request Input**: `GithubScanRequest` (gồm `courseId` và từ khóa `query`).
* **API GitHub**: `GET https://api.github.com/search/repositories?q={query}&per_page=100`.
* **Repository Context**: Robot gọi tiếp `GET /repos/{owner}/{repo}/readme` and `GET /repos/{owner}/{repo}/git/trees/HEAD?recursive=1`.
* **Database Save**: Lưu vào bảng `repo_candidates` với trạng thái ban đầu là `NEW`.

#### Các trường hợp lỗi
* Chạm giới hạn lượt gọi (Rate Limit) của GitHub: Hệ thống bắt lỗi HTTP 403, ghi nhận nhật ký và cho luồng quét ngủ đông 45 giây (`Thread.sleep(45000)`) trước khi tiếp tục.
* Repository private hoặc đã bị xóa: Robot bỏ qua lỗi kết nối (trong khối try-catch trống) và tiếp tục xử lý các repository khác mà không làm gián đoạn toàn bộ tiến trình quét.

#### Câu hỏi giảng viên có thể hỏi & Câu trả lời ngắn gọn khi bảo vệ

1. **Hỏi: Tại sao endpoint `/scan-all` lại trả về HTTP 202 Accepted ngay lập tức mà không đợi quét xong?**
   * **Trả lời**: Vì quét toàn bộ môn học tốn rất nhiều thời gian (gọi nhiều API ngoài, chờ đợi timeout). Nếu bắt Admin chờ phản hồi đồng bộ, trình duyệt sẽ bị treo (timeout). Do đó, em thiết kế bất đồng bộ: Controller nhận yêu cầu, đẩy tác vụ vào một Thread Pool chạy ngầm rồi trả về mã HTTP 202 ngay để Admin có thể làm việc khác. Admin có thể xem tiến độ qua log realtime `/scan-logs`.
2. **Hỏi: Cơ chế chạy ngầm bất đồng bộ này sử dụng công nghệ gì mới của Java 21?**
   * **Trả lời**: Em sử dụng **Virtual Threads** (Luồng ảo) của Java 21 thông qua cú pháp `Thread.ofVirtual().start(...)` hoặc thiết lập executor trong `AdminGithubController.java#L32-L33`. Virtual threads cực kỳ nhẹ, không chiếm dụng luồng hệ điều hành (platform threads) nên hệ thống có thể chạy hàng ngàn tác vụ quét ngầm mà không gây tốn RAM hay nghẽn CPU.
3. **Hỏi: Làm sao hệ thống tránh việc quét trùng lặp các repository đã tồn tại trong hệ thống?**
   * **Trả lời**: Tại lớp [GithubScanService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/GithubScanService.java#L125-L130), trước khi thực hiện vòng lặp lưu, hệ thống nạp sẵn tất cả URL GitHub đang có từ hai bảng `repo_candidates` và `github_repos` vào một bộ chứa `HashSet` (truy cập độ phức tạp O(1)). Trong quá trình quét, nếu URL nào đã nằm trong set này thì sẽ bị bỏ qua lập tức.
4. **Hỏi: Bạn lọc các thư mục rác (như `node_modules`, `build`) trong File Tree của repository như thế nào trước khi lưu?**
   * **Trả lời**: Em định nghĩa một danh sách đen `IGNORED_TREE_SEGMENTS` gồm các từ khóa như `.git`, `node_modules`, `target`, `dist`, v.v. tại lớp [GithubScanService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/GithubScanService.java#L37-L40). Hàm lọc `isUsefulTreePath` sẽ phân tách đường dẫn thành các phân đoạn, nếu chứa thư mục rác hoặc độ sâu thư mục vượt quá 3 (`MAX_FILE_TREE_DEPTH`), nó sẽ loại bỏ phần tử đó.
5. **Hỏi: Nội dung file README của GitHub trả về dưới dạng gì? Bạn giải mã như thế nào?**
   * **Trả lời**: API GitHub trả về nội dung file README được mã hóa dưới dạng chuỗi Base64. Em sử dụng lớp tiện ích Java chuẩn: `new String(Base64.getMimeDecoder().decode(readme))` để giải mã về dạng văn bản thường (UTF-8), sau đó dùng biểu thức chính quy loại bỏ khoảng trắng thừa để tối ưu bộ nhớ.
6. **Hỏi: Nếu API GitHub bị sập hoặc mất kết nối mạng, hệ thống của bạn có bị crash không?**
   * **Trả lời**: Không. Toàn bộ các bước gọi API ngoài (như lấy README, lấy File Tree) đều được bọc trong các khối `try-catch` bắt các exception chung. Nếu xảy ra lỗi kết nối, hệ thống ghi nhận log cảnh báo và trả về giá trị null cho trường đó, các thông tin cơ bản khác vẫn được lưu trữ và tiến trình quét tiếp tục bình thường.
7. **Hỏi: Làm sao bạn đảm bảo tính an toàn cho Token GitHub không bị lộ trong mã nguồn?**
   * **Trả lời**: Token GitHub được nạp từ biến môi trường `GITHUB_TOKEN` thông qua file `.env` hoặc file cấu hình `application.yaml`. Em không bao giờ hardcode token vào file Java. Thêm vào đó, file `.env` được cấu hình trong `.gitignore` để tránh đẩy lên kho chứa công khai.
8. **Hỏi: Bạn cấu hình timeout cho các yêu cầu HTTP gọi sang GitHub như thế nào?**
   * **Trả lời**: Em cấu hình timeout chặt chẽ cho WebClient hoặc HttpClient bằng cách sử dụng `.timeout(Duration.ofSeconds(8))` cho các cuộc gọi thông tin phụ và tối đa 30 giây cho luồng tìm kiếm chính để tránh việc ứng dụng bị treo vô hạn nếu máy chủ GitHub phản hồi chậm.

---

### Luồng 4: Phòng Chat Cộng Đồng & WebSocket Presence

#### Giải thích Feynman (Cho học sinh cấp hai)
> REST API giống như việc bạn gửi thư giấy: gửi đi rồi đợi phản hồi. Còn WebSocket giống như việc hai người đang gọi điện thoại trực tiếp: đường dây luôn thông suốt, ai nói gì bên kia nghe thấy ngay lập tức.
> Trong phòng chat của DevOrbit, khi bạn kết nối vào hệ thống (CONNECT), một hệ thống kiểm tra sẽ xác thực thẻ JWT của bạn. Khi bạn tham gia vào một phòng chat (SUBSCRIBE), bạn sẽ được đăng ký vào danh sách những người đang online của phòng đó (Presence). Khi một bạn gửi tin nhắn (SEND), tin nhắn đó được lưu vào database trước, sau đó ngay lập tức được loa phóng thanh (Message Broker) phát đến tai tất cả những bạn đang đăng ký nghe phòng đó thời gian thực. Nếu bạn tắt trình duyệt hoặc mất mạng (DISCONNECT), hệ thống tự động gạch tên bạn khỏi danh sách online để người khác biết bạn đã đi ngủ.

#### Sơ đồ luồng kỹ thuật
```text
Client (React App)             WebSocket Gateway (Spring)       Presence Listener              PostgreSQL DB
      │                                    │                            │                             │
      │─── CONNECT (Authorization header) ─>                            │                             │
      │    (Token checked & authenticated) │                            │                             │
      │                                    │                            │                             │
      │─── SUBSCRIBE /topic/channel/1 ─────>                            │                             │
      │                                    ├─── SessionSubscribeEvent ─>│                             │
      │                                    │                            ├─── presence.subscribe() ───>│
      │                                    │                            │    (Saves in DB)            │
      │                                    │                            ├─── Broadcast presence list  │
      │                                    │                            │    to /topic/channel/1/presence
```

#### File và Method tham gia
1. [WebSocketConfig](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/WebSocketConfig.java) -> Cấu hình tiền tố `/app`, `/topic`, đăng ký điểm kết nối `/ws/community` và chặn xác thực JWT bằng `ChannelInterceptor`.
2. [CommunityPresenceEventListener](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/CommunityPresenceEventListener.java) -> `@EventListener` lắng nghe các sự kiện subscribe, unsubscribe và disconnect của Spring WebSocket.
3. [CommunityPresenceService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/CommunityPresenceService.java) -> `subscribe()`, `unsubscribe()`, `disconnect()`. Ghi nhận thông tin phiên (sessionId, subscriptionId) vào DB.
4. [StudentCommunityController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentCommunityController.java) -> `@MessageMapping("/chat.send/{channelId}")` xử lý tin nhắn chat từ sinh viên gửi lên.

#### Dữ liệu đi qua hệ thống
* **Tin nhắn gửi đi**: `ChatMessageRequest` chứa thuộc tính `content` dạng text.
* **WebSocket Destination**: Client gửi đến `/app/chat.send/{channelId}`. Hệ thống broadcast kết quả về `/topic/channel/{channelId}` dưới dạng `ChatMessageResponse`.
* **Thông tin online**: Broadcast về `/topic/channel/{channelId}/presence` chứa danh sách sinh viên online sắp xếp theo bảng chữ cái.

#### Các trường hợp lỗi
* Kết nối WebSocket mà không gửi kèm Header `Authorization` hợp lệ: Bộ chặn interceptor của `WebSocketConfig` ném ra `AccessDeniedException` và ngắt kết nối ngay lập tức.
* Mất mạng đột ngột: Spring sinh ra sự kiện `SessionDisconnectEvent`, `presenceService.disconnect()` tự động xóa tất cả session đó ra khỏi bảng `community_presences` để danh sách online của phòng chat luôn chính xác.

#### Câu hỏi giảng viên có thể hỏi & Câu trả lời ngắn gọn khi bảo vệ

1. **Hỏi: Xác thực người dùng trong kết nối WebSocket diễn ra vào thời điểm nào? Vì sao không dùng cơ chế Filter thông thường?**
   * **Trả lời**: Bộ lọc Filter của Spring Security thông thường chỉ xử lý các request HTTP truyền thống. WebSocket sử dụng giao thức nâng cấp (Upgrade). Em thực hiện xác thực ngay khi Client gửi yêu cầu kết nối **CONNECT** đầu tiên bằng cách dùng một `ChannelInterceptor` cấu hình tại [WebSocketConfig.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/WebSocketConfig.java#L47-L81). Interceptor bóc tách header `Authorization` trong khung tin STOMP, xác thực token JWT thông qua `JwtService`, rồi lưu thông tin người dùng vào context của phiên WebSocket đó.
2. **Hỏi: Trạng thái online/presence của sinh viên được lưu trữ ở đâu? Tại sao bạn lại chọn giải pháp đó?**
   * **Trả lời**: Dữ liệu presence được lưu trữ trực tiếp trong bảng cơ sở dữ liệu `community_presences` (ánh xạ qua thực thể `CommunityPresence`). Việc lưu trữ trong DB giúp đảm bảo tính đồng bộ tuyệt đối khi hệ thống scale ngang (nhiều node backend chạy song song), tránh việc mất dấu danh sách online nếu lưu in-memory cục bộ trên một server đơn lẻ.
3. **Hỏi: Điều gì xảy ra khi sinh viên ngắt kết nối đột ngột (tắt máy, mất mạng)? Làm sao hệ thống phát hiện ra?**
   * **Trả lời**: Khi kết nối TCP bị ngắt đột ngột, máy chủ WebSocket (Tomcat/Spring) sẽ nhận biết được thông qua cơ chế Ping/Pong của giao thức WebSocket. Spring lập tức phát ra sự kiện `SessionDisconnectEvent`. Lớp [CommunityPresenceEventListener.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/CommunityPresenceEventListener.java#L62-L66) sẽ bắt lấy sự kiện này, lấy ra `sessionId` bị ngắt, gọi service để xóa mọi bản ghi hiện diện liên quan trong DB, và broadcast danh sách online mới cho các thành viên còn lại.
4. **Hỏi: Cơ chế phân luồng tin nhắn (Message Routing) hoạt động thế nào với các tiền tố `/app` và `/topic`?**
   * **Trả lời**: Tiền tố `/app` được cấu hình là điểm đến của ứng dụng (Application Destination Prefixes). Các tin nhắn gửi tới đây sẽ được định tuyến đến các hàm xử lý nghiệp vụ `@MessageMapping` trong Controller. Còn `/topic` là tiền tố của Simple Broker (Bộ môi giới tin nhắn đơn giản). Các tin nhắn gửi đến đây sẽ được phát trực tiếp tới những Client đang đăng ký lắng nghe (subscribe) đường dẫn đó mà không đi qua Controller.
5. **Hỏi: Đoạn code nào thực hiện chức năng broadcast danh sách online đến sinh viên trong kênh?**
   * **Trả lời**: Hàm `broadcast` trong lớp `CommunityPresenceEventListener.java#L83-L85` sử dụng lớp `SimpMessagingTemplate` để gửi dữ liệu danh sách online đến địa chỉ `/topic/channel/{channelId}/presence`.
6. **Hỏi: Bạn thiết lập chính sách bảo mật chống tấn công tiêm nhiễm dữ liệu (database injection) cho bảng `community_presences` trên Supabase như thế nào?**
   * **Trả lời**: Trong file migration [20260617221800_create_community_presences.sql](file:///d:/temp/devorbit/supabase/migrations/20260617221800_create_community_presences.sql#L15-L20), em kích hoạt Row Level Security (RLS) cho bảng này và tạo một chính sách `"Deny direct API access"` từ chối tất cả mọi thao tác đọc/ghi trực tiếp từ phía Client (anon và authenticated). Chỉ các kết nối dùng quyền tối cao (`service_role`) của Backend mới được thực hiện thao tác.
7. **Hỏi: Làm sao bạn phân biệt được nhiều tab trình duyệt khác nhau của cùng một sinh viên trong phòng chat?**
   * **Trả lời**: Hệ thống phân biệt dựa trên tổ hợp của `sessionId` (mã phiên kết nối WebSocket độc nhất mỗi lần mở tab) và `subscriptionId` (mã định danh đăng ký kênh cụ thể của tab đó). Do đó, một sinh viên mở nhiều tab vẫn được lưu trữ thành nhiều dòng hiện diện riêng biệt trong DB, khi tắt một tab thì chỉ tab đó bị xóa.
8. **Hỏi: Bạn làm thế nào để tin nhắn chat luôn hiển thị theo đúng thứ tự thời gian trên giao diện?**
   * **Trả lời**: Ở tầng cơ sở dữ liệu, cột `created_at` được gán mặc định bằng thời gian hệ thống. Khi sinh viên tải lại lịch sử tin nhắn qua API `/messages`, câu truy vấn trong `CommunityChatService` sẽ sắp xếp dữ liệu theo thứ tự thời gian tăng dần (`ORDER BY createdAt ASC`).

---

### Luồng 5: AI Tutor & Trực Quan Hóa Lộ Trình RAG

#### Giải thích Feynman (Cho học sinh cấp hai)
> RAG giống như một học sinh đi thi nhưng được **phép mở sách**. Nếu chỉ hỏi AI thông thường (như ChatGPT), nó có thể trả lời lung tung vì nó không biết gì về chương trình đào tạo của trường UIT. 
> Với hệ thống của chúng ta:
> 1. Khi bạn hỏi: *"Môn SE104 học thế nào?"*, hệ thống sẽ làm nhiệm vụ đi **tìm sách trước**. Nó truy vấn cơ sở dữ liệu để lấy đề cương môn Công nghệ Phần mềm (SE104) và tìm các đoạn tài liệu liên quan bằng công nghệ tìm kiếm thông minh (Vector Similarity Search).
> 2. Hệ thống xếp đống tài liệu này lại làm ngữ cảnh (Context), rồi gói kèm câu hỏi của bạn gửi cho AI. Hệ thống nói với AI: *"Hãy đọc đống tài liệu chuẩn này và trả lời sinh viên, cấm tự bịa thông tin"*.
> 3. AI sẽ phân tích và trả về câu trả lời chuẩn xác nhất. Đặc biệt hơn, hệ thống còn hỗ trợ **trả lời dạng dòng chảy (SSE Streaming)**: AI nghĩ đến đâu chữ chạy ra đến đấy trên màn hình chứ không bắt bạn đợi lâu. Nếu câu hỏi liên quan đến lộ trình học tập, AI còn tự động cấu trúc câu trả lời thành sơ đồ các kỳ học trực quan trên frontend để bạn dễ theo dõi.

#### Sơ đồ luồng kỹ thuật
```text
React Frontend                  SubjectQaController              SubjectQaService             VectorStore / LLM
      │                                  │                             │                              │
      │── POST /stream (User query) ────>│                             │                              │
      │                                  ├─── streamQuery() ──────────>│                              │
      │                                  │ (Virtual Thread pool)       ├─── build DB & RAG context    │
      │                                  │                             │    (Hybrid retrieval)        │
      │                                  │                             │    (Calls pgvector & FTS)    │
      │                                  │                             │                              │
      │                                  │                             ├─── Call Fireworks LLM        │
      │                                  │                             │    (Stream response)         │
      │<── SSE Stream (Deltas/Chunks) ───┼─────────────────────────────┼──────────────────────────────│
      │    (e.g., status, delta, complete)                              │                              │
```

#### File và Method tham gia
1. [SubjectQaController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/SubjectQaController.java) -> `stream()` trả về đối tượng `SseEmitter` để duy trì kết nối stream.
2. [SubjectQaService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/SubjectQaService.java) -> `streamQuery()`, `prepareQuery()`. Xử lý gom ngữ cảnh DB, lọc ý định (intent), điều phối RAG và Web Search song song.
3. [KnowledgeRetrievalService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/knowledge/KnowledgeRetrievalService.java) -> `search()` thực hiện tìm kiếm ngữ cảnh hybrid.
4. [KnowledgeChunkRepository](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/KnowledgeChunkRepository.java) -> `searchHybrid()` thực hiện tìm kiếm kết hợp pgvector và Full-Text Search.
5. [FireworksEmbeddingService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/ai/FireworksEmbeddingService.java) -> `embed()` gọi API ngoài để sinh vector embedding 4096 chiều từ câu hỏi của sinh viên.
6. [OpenCodeAiService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/ai/OpenCodeAiService.java) -> `streamCompletion()` gọi API LLM và trả về luồng dữ liệu (Flux/SSE).

#### Dữ liệu đi qua hệ thống
* **Đầu vào request**: `SubjectQaRequest` (gồm `message`, `sessionId`).
* **SSE Stream Events**: Phục vụ streaming thời gian thực qua các loại event:
  - `status`: Báo tiến trình hiện tại (ví dụ: "Đang tìm kiếm trong kho RAG").
  - `search_result`: Trả về kết quả tìm kiếm web bổ trợ.
  - `delta`: Các mảnh văn bản nhỏ do LLM sinh ra thời gian thực.
  - `complete`: Trả về toàn bộ DTO kết quả cuối cùng bao gồm nguồn trích dẫn và điểm tự tin.

#### Các trường hợp lỗi
* Kết nối mạng hoặc API AI bị timeout/lỗi 429: Hệ thống tự động chuyển sang cơ chế **Fallback**. Nó sẽ chuyển sang gọi chế độ One-shot (đồng bộ) thông thường bằng `generateCompletion` với prompt tối giản để đảm bảo sinh viên luôn nhận được câu trả lời thay vì thông báo lỗi thô thiển.
* Câu hỏi hoàn toàn không liên quan đến môn học của UIT: Phân loại ý định (`TutorIntentClassifier`) sẽ nhận dạng và hệ thống trả về câu trả lời thân thiện nhắc nhở sinh viên tập trung vào chuyên môn.

#### Câu hỏi giảng viên có thể hỏi & Câu trả lời ngắn gọn khi bảo vệ

1. **Hỏi: Cơ chế tìm kiếm Hybrid RAG trong dự án của bạn hoạt động như thế nào?**
   * **Trả lời**: Hệ thống kết hợp hai phương pháp tìm kiếm trong một câu truy vấn SQL duy nhất tại lớp [KnowledgeChunkRepository.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/KnowledgeChunkRepository.java#L81-L134): Một là **Vector Search** (dùng khoảng cách Cosine `<=>` của pgvector) để tìm nghĩa ngữ nghĩa; Hai là **Full-Text Search** (FTS dùng `tsvector` của PostgreSQL) để tìm chính xác từ khóa thô. Điểm số từ hai phương pháp được dung hợp bằng thuật toán **RRF (Reciprocal Rank Fusion)** kèm theo điểm cộng thêm (boost) cho các nguồn chính thống (Syllabus) để ra được những đoạn tài liệu chuẩn xác nhất.
2. **Hỏi: Tại sao bạn lại thực hiện RAG và Web Search song song (Parallel execution) trong `SubjectQaService`?**
   * **Trả lời**: Việc tìm kiếm tài liệu trong kho RAG nội bộ và gọi API tìm kiếm Web bên ngoài là hai tác vụ mạng độc lập và tốn thời gian. Nếu thực hiện tuần tự, thời gian chờ sẽ là tổng của cả hai tác vụ. Em sử dụng `CompletableFuture.allOf().join()` tại lớp [SubjectQaService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/SubjectQaService.java#L688-L726) để kích hoạt cả hai tác vụ chạy đồng thời trên hai luồng ảo khác nhau, giúp giảm thời gian phản hồi xuống chỉ bằng thời gian của tác vụ lâu nhất.
3. **Hỏi: Làm sao bạn giải quyết bài toán nhớ ngữ cảnh của các lượt chat trước (Conversation History) trong RAG?**
   * **Trả lời**: Em duy trì một bản đồ bộ nhớ `sessionSummaries` lưu trữ tóm tắt các lượt hội thoại trước theo từng `sessionId`. Khi sinh viên đặt câu hỏi mới, tóm tắt này sẽ được tự động đính kèm vào phần cuối của `systemPrompt` tại lớp [SubjectQaService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/SubjectQaService.java#L887-L893) để LLM biết được ngữ cảnh câu chuyện cũ mà không cần phải truyền lại toàn bộ lịch sử chat dài dằng dặc gây tốn token và chi phí.
4. **Hỏi: Tại sao bạn phải thiết lập trì hoãn 2 giây (sleep) trong phương thức `warmUpCaches` khi khởi động ứng dụng?**
   * **Trả lời**: Trong quá trình khởi động (Warmup), hệ thống sẽ chủ động lấy 10 môn học phổ biến nhất để sinh embedding trước. Vì gọi API của Fireworks AI liên tục ở tốc độ cao sẽ dễ bị lỗi giới hạn tần suất (HTTP 429 Rate Limit), em chủ động đặt `Thread.sleep(2000)` giữa các lần gửi yêu cầu để giãn cách thời gian gọi API an toàn.
5. **Hỏi: Biến `embeddingDegraded` dùng để làm gì?**
   * **Trả lời**: Đây là cơ chế tự vệ của hệ thống. Nếu cuộc gọi lấy embedding từ Fireworks AI bị lỗi liên tục (sập mạng hoặc hết hạn API key), hệ thống sẽ đánh dấu `embeddingDegraded = true`. Trong vòng 1 phút tiếp theo, hệ thống sẽ tự động bỏ qua bước tìm kiếm ngữ nghĩa Vector để tránh bắt sinh viên chờ đợi lâu trong vô vọng, thay vào đó nó sẽ chuyển sang tìm kiếm từ khóa FTS truyền thống và dùng cơ chế Web Search dự phòng.
6. **Hỏi: Điểm tự tin (Confidence Score) của câu trả lời được tính toán dựa trên các yếu tố nào?**
   * **Trả lời**: Điểm tự tin được tính toán tại lớp `SubjectQaService` dựa trên tỷ lệ đóng góp của ngữ cảnh: Có tìm thấy môn học trong DB không (trọng số lớn), số lượng tài liệu chính thống tìm được trong kho RAG, và việc có dữ liệu Web tìm kiếm hỗ trợ hay không. Nếu không có bất kỳ nguồn dữ liệu chuẩn nào, điểm tự tin sẽ thấp và hệ thống sẽ khuyến cáo người dùng kiểm tra lại thông tin.
7. **Hỏi: Làm sao frontend React hiển thị được nội dung chữ chạy thời gian thực (streaming) từ backend?**
   * **Trả lời**: Backend sử dụng lớp `SseEmitter` để đẩy dữ liệu dạng Server-Sent Events (SSE). Ở phía Frontend, mã nguồn sử dụng API `EventSource` hoặc thư viện fetch hỗ trợ stream để lắng nghe kênh kết nối, mỗi khi nhận được một sự kiện `delta` từ server, nó sẽ lập tức cộng dồn chuỗi văn bản vào state hiển thị trên màn hình.
8. **Hỏi: Bạn làm cách nào để giảm thiểu hiện tượng AI nói sảng, bịa đặt thông tin (Hallucination)?**
   * **Trả lời**: Em áp dụng 3 kỹ thuật: Một là nhúng bảng kiểm tra dữ liệu khả dụng (Data Availability) vào prompt để AI biết rõ hệ thống hiện có thông tin gì; Hai là thiết lập System Prompt cực kỳ nghiêm ngặt bắt buộc AI chỉ được trích dẫn link tài liệu và code thật từ ngữ cảnh cung cấp; Ba là nếu không tìm thấy dữ liệu nào, AI được chỉ thị phải nói rõ là *"DevOrbit không có thông tin"* thay vì cố trả lời bừa.

---

## 4. Java và Spring Boot được sử dụng trong dự án

### 4.1 Tính đóng gói (Encapsulation)
* **Ý nghĩa đơn giản**: Giống như việc bạn đi xe máy chỉ cần biết vặn ga là xe chạy, không cần biết piston hoạt động thế nào. Mọi linh kiện phức tạp đều được che đậy dưới lớp vỏ xe.
* **Minh chứng trong code**: Lớp [StudentUser](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/StudentUser.java) khai báo các thuộc tính nhạy cảm như `passwordHash` là `private`. Các tác vụ bên ngoài muốn truy xuất hoặc sửa đổi phải thông qua các hàm getter/setter công khai hoặc builder được Lombok tự động sinh ra.
* **Tại sao dùng**: Bảo vệ dữ liệu nội bộ của đối tượng không bị sửa đổi tùy tiện từ bên ngoài, giúp kiểm soát tính toàn vẹn dữ liệu (ví dụ: trim khoảng trắng của tên trước khi lưu).
* **Câu hỏi của thầy**: *Em hãy chỉ ra tính đóng gói được thể hiện ở đâu trong các Entity?*
* **Trả lời**: Dạ, trong mọi Entity như `StudentUser`, toàn bộ các trường dữ liệu đều được khai báo là `private`. Các lớp dịch vụ bên ngoài không thể can thiệp trực tiếp vào giá trị của biến mà phải đi qua các phương thức Get/Set do thư viện Lombok tự động tạo ra bằng `@Getter` và `@Setter` đặt ở đầu class.

### 4.2 Tính trừu tượng (Abstraction)
* **Ý nghĩa đơn giản**: Thiết kế ra một cái khung (Interface) định nghĩa những việc cần làm, còn làm cụ thể thế nào (Implementation) thì để các lớp con tự quyết định.
* **Minh chứng trong code**: Lớp interface [EmbeddingService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/ai/EmbeddingService.java) định nghĩa hai hàm cơ bản `embed(String text)` và `dimensions()`. Chúng ta có các lớp hiện thực cụ thể là [FireworksEmbeddingService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/ai/FireworksEmbeddingService.java) (gọi API Fireworks), `OpenAiCompatibleEmbeddingService` (gọi OpenAi), và `OfflineNoopEmbeddingService` (chế độ offline không hoạt động).
* **Tại sao dùng**: Giúp code linh hoạt. Khi cần đổi nhà cung cấp AI (ví dụ chuyển từ Fireworks sang OpenAI), tầng nghiệp vụ gọi `EmbeddingService` hoàn toàn không phải sửa một dòng code nào, chỉ cần thay lớp cấu hình khởi tạo Bean.
* **Câu hỏi của thầy**: *Nếu dự án muốn chuyển đổi nhà cung cấp dịch vụ sinh Vector Embedding thì phải sửa những đâu?*
* **Trả lời**: Dạ, nhờ tính trừu tượng của Interface `EmbeddingService`, em chỉ cần viết một lớp mới hiện thực interface này (ví dụ `OpenAiEmbeddingService`). Tầng nghiệp vụ RAG hoàn toàn giữ nguyên vì nó chỉ phụ thuộc vào interface trừu tượng chứ không quan tâm bên dưới gọi API nào.

### 4.3 Dependency Injection (DI) & Constructor Injection
* **Ý nghĩa đơn giản**: Thay vì một class tự đi tìm và khởi tạo các đối tượng phụ thuộc (tự mua nguyên liệu làm nhà), Spring IoC Container sẽ tự động "bơm" (inject) các đối tượng phụ thuộc đã được chuẩn bị sẵn vào lớp đó thông qua hàm khởi dựng (Constructor).
* **Minh chứng trong code**: Tại lớp [SubjectQaService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/SubjectQaService.java#L87-L118), hàm khởi dựng yêu cầu nạp vào `CourseRepository`, `WebSearchService`, v.v. Spring Boot khi khởi động sẽ tự quét và nạp các thực thể Bean này vào hàm khởi dựng một cách tự động.
* **Tại sao dùng**: Giúp các class lỏng lẻo với nhau (loose coupling), cực kỳ dễ viết bài kiểm tra (Unit Test) bằng cách nhét các đối tượng giả lập (Mock object) vào constructor mà không cần chạy cả hệ thống Spring.
* **Câu hỏi của thầy**: *Tại sao em dùng Constructor Injection thay vì dùng `@Autowired` trực tiếp trên các thuộc tính biến?*
* **Trả lời**: Dạ, Constructor Injection là best practice được Spring khuyến nghị vì 3 lý do: Một là đảm bảo các thuộc tính phụ thuộc là bất biến (`final`); Hai là ngăn ngừa lỗi tham chiếu null (NullPointerException) khi ứng dụng chạy vì Spring bắt buộc phải nạp đủ các bean phụ thuộc mới khởi tạo được class; Ba là không phụ thuộc vào Spring Container, dễ dàng viết test và truyền Mock qua constructor.

### 4.4 Java 8+ Optional
* **Ý nghĩa đơn giản**: Là một chiếc hộp bảo vệ. Chiếc hộp này có thể chứa một giá trị hoặc trống rỗng. Nó bắt buộc lập trình viên phải kiểm tra chiếc hộp trước khi lấy đồ ra, tránh việc thò tay vào hộp rỗng và bị cắn (NullPointerException).
* **Minh chứng trong code**: Trong file [StudentAuthService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/StudentAuthService.java#L56-L60), hàm truy vấn trả về `Optional<StudentUser>`. Em sử dụng `.orElseThrow()` để ném lỗi ngay nếu tài khoản không tồn tại, tránh xử lý tiếp với giá trị null.
* **Tại sao dùng**: Xử lý an toàn các trường hợp dữ liệu có thể không tồn tại trong database, viết code gọn gàng theo phong cách Functional Programming.
* **Câu hỏi của thầy**: *Lớp `Optional` giải quyết triệt để lỗi gì trong lập trình Java?*
* **Trả lời**: Dạ, lớp `Optional` giải quyết lỗi kinh đoán `NullPointerException` (lỗi tham chiếu null). Nó ép buộc người viết code phải tường minh xử lý trường hợp dữ liệu bị rỗng bằng các hàm an toàn như `.isPresent()`, `.map()`, hoặc `.orElseThrow()`.

### 4.5 Java 8+ Stream API & Lambda Expressions
* **Ý nghĩa đơn giản**: Stream giống như một băng chuyền trong nhà máy. Dữ liệu chạy dọc băng chuyền, đi qua các bộ lọc (filter), bộ chuyển đổi (map), rồi được đóng hộp lại (collect). Lambda là các hàm ẩn danh viết cực ngắn để ra lệnh cho băng chuyền hoạt động.
* **Minh chứng trong code**: Lớp [CommunityPresenceService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/CommunityPresenceService.java#L54-L58):
  ```java
  List<Long> affectedChannels = presences.stream()
      .map(p -> p.getChannel().getId())
      .distinct()
      .toList();
  ```
* **Tại sao dùng**: Giúp xử lý các tập hợp (Collection) dữ liệu cực kỳ nhanh, code ngắn gọn, dễ đọc hơn rất nhiều so với dùng các vòng lặp `for` lồng nhau truyền thống.
* **Câu hỏi của thầy**: *Em hãy chuyển đoạn code dùng Stream API ở trên về dạng vòng lặp `for` truyền thống?*
* **Trả lời**: 
  ```java
  List<Long> affectedChannels = new ArrayList<>();
  for (CommunityPresence p : presences) {
      Long channelId = p.getChannel().getId();
      if (!affectedChannels.contains(channelId)) {
          affectedChannels.add(channelId);
      }
  }
  ```
  Sử dụng Stream giúp mã nguồn khai báo rõ ràng ý định xử lý hơn là phải viết các dòng code lặp chi tiết thủ công.

---

## 5. Annotation Cheat Sheet

Dưới đây là các Annotation thực tế xuất hiện trong dự án DevOrbit:

| Annotation | Ý nghĩa đơn giản | Spring sử dụng khi nào | Ví dụ thực tế trong dự án |
| :--- | :--- | :--- | :--- |
| `@RestController` | Đánh dấu lớp là Controller trả về dữ liệu JSON | Đăng ký class với Spring MVC để tiếp nhận các API Endpoint | [StudentAuthController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentAuthController.java#L37) |
| `@RequestMapping` | Định nghĩa đường dẫn gốc cho Controller | Định tuyến các request có tiền tố đường dẫn khớp vào lớp xử lý | [StudentAuthController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentAuthController.java#L38) |
| `@PostMapping` | Tiếp nhận request phương thức POST | Sử dụng cho các tác vụ tạo mới dữ liệu, đăng nhập hoặc gửi form | [StudentAuthController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentAuthController.java#L46) |
| `@GetMapping` | Tiếp nhận request phương thức GET | Sử dụng cho các tác vụ đọc dữ liệu từ server | [StudentAuthController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentAuthController.java#L120) |
| `@Service` | Đánh dấu lớp dịch vụ nghiệp vụ | Đăng ký class vào IoC Container như một Bean tầng nghiệp vụ | [StudentAuthService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/StudentAuthService.java#L32) |
| `@Repository` | Đánh dấu lớp truy cập database | Đăng ký và kích hoạt các tính năng tự sinh SQL của Spring Data | [CourseRepository](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/CourseRepository.java#L32) |
| `@Component` | Đánh dấu một Bean dùng chung | Đăng ký class tiện ích chung vào IoC Container | [CourseKnowledgeIndexer](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/knowledge/CourseKnowledgeIndexer.java#L27) |
| `@Entity` | Đánh dấu class ánh xạ CSDL | Báo cho Hibernate biết để quản lý class này như một bảng DB | [Course](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/Course.java#L27) |
| `@Table` | Định nghĩa tên bảng tương ứng trong DB | Sử dụng khi tên class Java khác với tên bảng trong CSDL thực tế | [Course](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/Course.java#L28) |
| `@Id` | Xác định thuộc tính là Khóa chính | Xác định cột định danh duy nhất cho bản ghi thực thể | [Course](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/Course.java#L39) |
| `@Transactional` | Quản lý giao dịch tự động | Đảm bảo tính toàn vẹn dữ liệu (commit hoặc rollback toàn bộ nếu lỗi) | [StudentAuthService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/StudentAuthService.java#L84) |
| `@Valid` | Kích hoạt kiểm tra dữ liệu đầu vào | Sử dụng kết hợp với các ràng buộc trong DTO để validate dữ liệu HTTP | [StudentAuthController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentAuthController.java#L47) |
| `@RequestBody` | Bóc tách phần Body của request HTTP | Chuyển đổi chuỗi JSON từ Client gửi lên thành đối tượng Java | [StudentAuthController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentAuthController.java#L47) |
| `@PathVariable` | Lấy biến từ đường dẫn URL | Lấy các tham số động trên URL (ví dụ: `/courses/{id}`) | [StudentCommunityController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentCommunityController.java#L35) |
| `@AuthenticationPrincipal` | Lấy thông tin người dùng hiện tại | Trích xuất nhanh thông tin định danh (studentCode) từ SecurityContext | [StudentAuthController](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentAuthController.java#L121) |
| `@Configuration` | Khai báo lớp cấu hình hệ thống | Đánh dấu lớp chứa các hàm khởi tạo cấu hình Spring Bean | [SecurityConfig](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SecurityConfig.java#L21) |
| `@Bean` | Khởi tạo một đối tượng dùng chung | Spring chạy hàm này và lưu đối tượng trả về vào IoC container | [SecurityConfig](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SecurityConfig.java#L34) |
| `@EventListener` | Lắng nghe sự kiện trong hệ thống | Kích hoạt hàm chạy khi nhận được sự kiện Spring phát ra | [CommunityPresenceEventListener](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/CommunityPresenceEventListener.java#L28) |

---

## 6. JWT và Spring Security

### 6.1 Giải thích Feynman về luồng Bảo mật
Spring Security hoạt động giống như một **chốt kiểm soát an ninh** ở cổng trường. 
Khi request đi vào:
1. `JwtAuthenticationFilter` chặn lại, lục tìm thẻ an ninh trong Header `Authorization` (dưới dạng `Bearer <token>`).
2. Nếu tìm thấy token, filter nhờ `JwtService` kiểm tra xem token này chữ ký có chuẩn không, có bị hết hạn hay nằm trong danh sách đen `RevokedTokenStore` không.
3. Nếu tất cả đều hợp lệ, filter bốc thông tin tên tài khoản, phân loại vai trò (`ROLE_STUDENT` hoặc `ROLE_ADMIN`) gói vào đối tượng `UsernamePasswordAuthenticationToken` rồi đặt vào túi áo của chốt an ninh (`SecurityContextHolder`).
4. Khi request đi sâu vào các phòng ban (Controller), cấu hình phân quyền tại `SecurityConfig` sẽ kiểm tra chốt an ninh: Nếu đường dẫn yêu cầu quyền Admin (`/api/admin/**`) mà túi áo người dùng chỉ có thẻ Sinh viên (`ROLE_STUDENT`), chốt sẽ chặn lại và trả về lỗi 403 Forbidden ("Bạn không có quyền truy cập").

### 6.2 Sơ đồ luồng xác thực JWT

```text
Request HTTP ──> [ JwtAuthenticationFilter ] ──(Không có Token)──> [ Đi tiếp vào filter tiếp theo ]
                         │
                    (Có Token)
                         ▼
                [ Kiểm tra chữ ký ] ──(Token lỗi/hết hạn)──> [ Trả về 401 Unauthorized ]
                         │
                 (Token hợp lệ)
                         ▼
            [ Check RevokedTokenStore ] ──(Token bị revoke)──> [ Trả về 401 Unauthorized ]
                         │
                  (Chưa bị revoke)
                         ▼
            [ Nạp SecurityContextHolder ] ──> [ Đi tiếp vào SecurityFilterChain ]
                                                       │
                                            [ Kiểm tra phân quyền ]
                                            ├── /api/student/** cần ROLE_STUDENT (Khớp -> Đi tiếp)
                                            └── /api/admin/** cần ROLE_ADMIN (Không khớp -> Trả về 403 Forbidden)
```

### 6.3 Bộ 20 câu hỏi vấn đáp Spring Security và JWT

1. **Hỏi: JWT là gì? Cấu trúc của nó gồm những phần nào?**
   * **Trả lời**: JWT (JSON Web Token) là phương thức xác thực không trạng thái dưới dạng chuỗi ký tự. Cấu trúc gồm 3 phần phân tách bởi dấu chấm (`.`): Header (chứa thuật toán ký), Payload (chứa thông tin người dùng/claims), và Signature (chữ ký bảo mật).
2. **Hỏi: JWT trong hệ thống của bạn được ký bằng thuật toán gì? Khóa ký nằm ở đâu?**
   * **Trả lời**: JWT được ký bằng thuật toán **HMAC-SHA256** thông qua khóa bí mật `secretKey`. Khóa bí mật được lấy cấu hình từ biến môi trường thông qua thuộc tính nạp vào [JwtProperties.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/JwtProperties.java).
3. **Hỏi: Phân biệt sự khác biệt về mã trạng thái HTTP giữa lỗi 401 và lỗi 403?**
   * **Trả lời**: Lỗi **401 Unauthorized** xảy ra khi hệ thống không xác định được bạn là ai (không có token hoặc token hết hạn). Lỗi **403 Forbidden** xảy ra khi hệ thống đã nhận diện được bạn (đăng nhập thành công) nhưng bạn không có đủ quyền truy cập vào tài nguyên yêu cầu (ví dụ: sinh viên đòi vào trang quản trị).
4. **Hỏi: Spring Security cấu hình hai lỗi 401 và 403 ở đâu?**
   * **Trả lời**: Tại lớp [SecurityConfig.java#L61-L71](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SecurityConfig.java#L61-L71), thông qua các cấu hình `authenticationEntryPoint` (cho lỗi 401) và `accessDeniedHandler` (cho lỗi 403).
5. **Hỏi: Tại sao bạn vô hiệu hóa tính năng CSRF (`csrf().disable()`) trong cấu hình bảo mật?**
   * **Trả lời**: Vì hệ thống của chúng ta sử dụng kiến trúc REST API không trạng thái (stateless) xác thực qua token JWT lưu trữ ở Client chứ không dùng cơ chế Session/Cookie truyền thống của trình duyệt, do đó không có nguy cơ bị tấn công giả mạo yêu cầu chéo trang (CSRF).
6. **Hỏi: Làm sao bạn biết tài khoản sinh viên đã bị Admin khóa (active = false) ngay lập tức khi họ cố gọi API bằng một token JWT cũ vẫn còn hạn?**
   * **Trả lời**: Tại lớp [JwtAuthenticationFilter.java#L56-L66](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/JwtAuthenticationFilter.java#L56-L66), khi xác thực token dạng `STUDENT`, bộ lọc luôn thực hiện một câu truy vấn nhanh vào database: `studentUserRepository.findByStudentCode` để kiểm tra cờ `active`. Nếu cờ này bằng `false`, bộ lọc lập tức trả về lỗi 403 và chặn request lại, kể cả khi token JWT gửi lên hoàn toàn hợp lệ về mặt chữ ký và thời gian.
7. **Hỏi: `SecurityContextHolder` là gì? Thời gian tồn tại của dữ liệu trong đó là bao lâu?**
   * **Trả lời**: `SecurityContextHolder` là nơi lưu trữ chi tiết bảo mật của phiên làm việc hiện tại của ứng dụng. Vì ứng dụng cấu hình cơ chế Stateless (`SessionCreationPolicy.STATELESS`), thông tin trong `SecurityContextHolder` chỉ tồn tại trong vòng đời của một request HTTP duy nhất và sẽ bị xóa đi ngay khi request đó kết thúc.
8. **Hỏi: Làm sao phân biệt được Token của Sinh viên và Admin khi giải mã JWT?**
   * **Trả lời**: Khi tạo token tại lớp `JwtService.java`, em nhúng thêm một claim tự định nghĩa là `claim("type", tokenType)` với giá trị là `"STUDENT"` hoặc `"ADMIN"`. Khi giải mã tại filter, em đọc claim này để gán role tương ứng (`ROLE_STUDENT` hoặc `ROLE_ADMIN`).
9. **Hỏi: Tại sao bạn cấu hình permitAll cho đường dẫn `/ws/community/**` trong SecurityConfig? Vậy phòng chat có bị truy cập tự do không?**
   * **Trả lời**: Cần permitAll ở mức HTTP của Spring Security để cho phép mọi kết nối bắt tay nâng cấp (Handshake) giao thức ban đầu đi qua. Tuy nhiên, phòng chat không bị tự do truy cập vì em đã chặn xác thực Token JWT ở mức giao thức STOMP trong `WebSocketConfig` ngay khi client gửi khung CONNECT.
10. **Hỏi: Endpoint tài liệu Swagger API được phân quyền như thế nào trong dự án của bạn?**
    * **Trả lời**: Chỉ có tài khoản có quyền quản trị viên mới được quyền xem Swagger: `.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasAuthority("ROLE_ADMIN")` (dòng 57 file `SecurityConfig.java`).
11. **Hỏi: Điều gì xảy ra nếu thời gian trên máy chủ chạy Backend bị sai lệch so với thời gian thực?**
    * **Trả lời**: Việc kiểm tra thời hạn hết hạn của token JWT dựa trên thời gian hệ thống của server. Nếu đồng hồ server bị sai lệch tiến hoặc lùi quá nhiều, token sinh ra có thể bị coi là hết hạn ngay lập tức hoặc kéo dài vô hạn, gây lỗi xác thực hàng loạt.
12. **Hỏi: CORS là gì? Bạn cấu hình CORS như thế nào trong SecurityConfig?**
    * **Trả lời**: CORS (Cross-Origin Resource Sharing) là cơ chế bảo mật của trình duyệt ngăn chặn client gọi API sang một domain khác. Em cấu hình CORS cho phép các origin (ví dụ: localhost:5173 của React) gọi API sang Backend kèm theo truyền chứng chỉ bảo mật (credentials) bằng cách dùng `CorsConfigurationSource` tại dòng 82 file `SecurityConfig.java`.
13. **Hỏi: Có rủi ro an ninh gì nếu dùng in-memory `RevokedTokenStore` khi hệ thống mở rộng quy mô chạy nhiều instance Backend?**
    * **Trả lời**: Nếu chạy nhiều node Backend (cluster), một node nhận yêu cầu logout sẽ chỉ lưu token đó vào RAM cục bộ của nó. Các node khác sẽ không hề biết và vẫn cho phép token đó truy cập. Để giải quyết, khi scale hệ thống lớn cần chuyển sang dùng Redis tập trung để lưu danh sách đen token này.
14. **Hỏi: Claim `jti` là gì? Nó đóng vai trò gì trong việc chống tấn công Replay Attack?**
    * **Trả lời**: `jti` (JWT ID) cung cấp một mã định danh độc nhất toàn cầu cho mỗi token JWT sinh ra. Nó giúp phân biệt các token khác nhau của cùng một người dùng, hỗ trợ việc thu hồi chính xác một token cụ thể khi logout.
15. **Hỏi: Làm sao bạn lấy được địa chỉ IP thật của Client khi họ gửi yêu cầu đăng nhập?**
    * **Trả lời**: Em viết hàm tiện ích `extractClientIp` tại lớp `StudentAuthService.java#L298-L308` phân tích các Header `X-Forwarded-For` và `X-Real-IP` (do các máy chủ Proxy/Nginx gán khi chuyển tiếp request) trước khi lấy địa chỉ trực tiếp `request.getRemoteAddr()`.
16. **Hỏi: BCrypt mã hóa mật khẩu có thể giải mã ngược lại được không?**
    * **Trả lời**: Không. BCrypt là hàm băm một chiều (One-way hash). Hệ thống kiểm tra mật khẩu bằng cách băm mật khẩu người dùng nhập vào rồi so khớp chuỗi băm đó với chuỗi băm trong DB thông qua hàm `passwordEncoder.matches()`.
17. **Hỏi: `OncePerRequestFilter` đảm bảo điều gì cho bộ lọc JWT?**
    * **Trả lời**: Lớp này đảm bảo filter xác thực JWT chỉ chạy đúng **một lần duy nhất** cho mỗi request đi vào hệ thống, tránh việc lọc lặp lại rườm rà khi request được chuyển tiếp nội bộ (Forward) giữa các servlet.
18. **Hỏi: Nếu token JWT hết hạn, client React sẽ nhận diện và xử lý như thế nào?**
    * **Trả lời**: Client React nhận được HTTP Status 401 từ API, bộ xử lý interceptor của axios/fetch trên frontend sẽ tự động xóa token trong localStorage/sessionStorage và chuyển hướng người dùng về trang Login.
19. **Hỏi: Rủi ro của việc hardcode JWT Secret mặc định trong code là gì? Lớp `JwtService` phòng chống thế nào?**
    * **Trả lời**: Nếu lộ secret key, kẻ tấn công có thể tự sinh ra token JWT giả mạo quyền Admin để phá hoại hệ thống. Tại hàm dựng của `JwtService.java#L33-L42`, nếu phát hiện cấu hình môi trường Production/Staging mà vẫn dùng khóa mặc định, ứng dụng sẽ lập tức ném ra lỗi `IllegalStateException` và dừng hoạt động để bảo vệ hệ thống.
20. **Hỏi: Class `JwtProperties` nạp dữ liệu cấu hình bằng cách nào?**
    * **Trả lời**: Lớp này sử dụng `@ConfigurationProperties(prefix = "app.jwt")` để tự động nạp các thuộc tính cấu hình từ file `application.yaml` vào một lớp Record bất biến, giúp quản lý cấu hình tập trung và an toàn.

---

## 7. Database, JPA và Migration

### 7.1 Mối quan hệ giữa các Thực thể chính (JPA Entities)
Dựa trên schema PostgreSQL thực tế của DevOrbit:
* **Courses (courses)**: Khóa chính là cột `stt` (id kiểu Java). Mã môn học `mamh` là duy nhất.
* **StudentUser (student_users)**: Khóa chính `id` tự tăng. Mã sinh viên `studentCode` là duy nhất.
* **CourseRelationship (course_relationships)**: Thể hiện mối quan hệ đồ thị giữa các môn học.
  - Quan hệ **Many-to-One** đến `Course` (qua cột `course_id` - môn học đích).
  - Quan hệ **Many-to-One** đến `Course` (qua cột `related_course_id` - môn học liên quan/tiên quyết).
  - Loại mối quan hệ được định nghĩa bằng Enum `CourseRelationType` (`PREREQUISITE` - tiên quyết, `CO_REQ` - song hành, `COMPLEMENTARY` - bổ túc).
* **GithubRepo (github_repos)**: 
  - Quan hệ **Many-to-One** đến `Course` (qua cột `course_id`).
* **CommunityPresence (community_presences)**:
  - Quan hệ **Many-to-One** đến `ChatChannel` (qua `channel_id`).
  - Quan hệ **Many-to-One** đến `StudentUser` (qua cột `student_code`).
* **KnowledgeChunk (knowledge_chunks)**:
  - Quan hệ **Many-to-One** đến `KnowledgeSource` (qua `source_id`).
  - Chứa cột `parent_chunk_id` tự tham chiếu đến chính nó để xây dựng liên kết cây phân cấp.

### 7.2 Quản lý Migration & Supabase Hardening
* Hệ thống quản lý cấu trúc bảng thông qua SQL schema và nạp dữ liệu ban đầu bằng các lớp Data Initializer (`TechStackDataInitializer.java`, `RepoCandidateDataInitializer.java`).
* Cơ chế tự bảo vệ và chuẩn bị cấu trúc CSDL chạy ngầm thông qua lớp [SupabaseDatabaseHardeningInitializer](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SupabaseDatabaseHardeningInitializer.java). Khi ứng dụng khởi động thành công, nó sẽ tự động kiểm tra và cấu hình các chỉ mục (Indexes) cho tất cả các khóa ngoại để tối ưu hóa hiệu năng truy vấn, đồng thời áp dụng chính sách chặn truy cập API trực tiếp trên 36 bảng của Supabase để buộc mọi luồng đi qua API Spring Boot.

### 7.3 Bảng ánh xạ CSDL thực tế

| Entity | Table | Ràng buộc chính | Repository | Service sử dụng |
| :--- | :--- | :--- | :--- | :--- |
| **StudentUser** | `student_users` | `student_code` unique, `email` unique | `StudentUserRepository` | `StudentAuthService`, `CommunityPresenceService` |
| **Course** | `courses` | `mamh` unique | `CourseRepository` | `CourseService`, `KnowledgeGraphService` |
| **CourseRelationship** | `course_relationships` | FK `course_id`, FK `related_course_id` | `CourseRelationshipRepository` | `CourseRelationshipService` |
| **GithubRepo** | `github_repos` | FK `course_id`, `github_url` unique | `GithubRepoRepository` | `GithubRepoService`, `GithubScanService` |
| **CommunityPresence** | `community_presences` | FK `channel_id`, FK `student_code` | `CommunityPresenceRepository` | `CommunityPresenceService` |
| **KnowledgeChunk** | `knowledge_chunks` | FK `source_id`, `embedding` vector(4096) | `KnowledgeChunkRepository` | `KnowledgeRetrievalService`, `KnowledgeEmbeddingService` |
| **KnowledgeSource** | `knowledge_sources` | `content_hash` unique | `KnowledgeSourceRepository` | `CourseKnowledgeBootstrapService` |

### 7.4 Bộ 15 câu hỏi về Database, JPA và Hibernate

1. **Hỏi: Sự khác biệt giữa DTO và Entity là gì? Tại sao không dùng Entity truyền trực tiếp lên UI?**
   * **Trả lời**: Entity là class Java ánh xạ trực tiếp cấu trúc bảng DB vật lý và được Hibernate quản lý trạng thái. DTO là đối tượng trung chuyển dữ liệu thuần túy, không có trạng thái JPA. Không đưa Entity lên UI vì sẽ làm rò rỉ cấu trúc DB, lộ dữ liệu nhạy cảm, làm tăng dung lượng truyền tải và dễ dính lỗi Serialization hoặc lazy loading exception.
2. **Hỏi: Lazy Loading (Tải lười) trong JPA là gì? Nó được cấu hình như thế nào trong các mối quan hệ?**
   * **Trả lời**: Lazy Loading (`fetch = FetchType.LAZY`) là cơ chế chỉ tải dữ liệu liên quan khi thuộc tính đó được gọi tường minh bằng code (getter). Nếu không dùng, Hibernate sẽ không truy vấn dữ liệu đó. Em cấu hình LAZY cho tất cả các quan hệ `@ManyToOne` (như trong `CommunityMessage.java#L25`) để tránh việc tải thừa dữ liệu làm chậm ứng dụng.
3. **Hỏi: Lỗi `LazyInitializationException` xảy ra khi nào? Làm sao khắc phục?**
   * **Trả lời**: Xảy ra khi ta cố truy cập một thuộc tính Lazy Loading sau khi Transaction của Spring đã đóng và Session của Hibernate đã bị ngắt. Cách khắc phục là bọc tác vụ bằng `@Transactional` để giữ Session mở, hoặc sử dụng câu truy vấn JPQL dùng `JOIN FETCH` để nạp sớm dữ liệu.
4. **Hỏi: Annotation `@Transactional` hoạt động như thế nào ở tầng dưới?**
   * **Trả lời**: Spring sử dụng cơ chế AOP (Aspect-Oriented Programming) tạo ra một lớp Proxy bọc ngoài class dịch vụ. Khi gọi hàm có `@Transactional`, Proxy sẽ mở một Transaction vật lý ở DB. Nếu hàm chạy trơn tru, Proxy ra lệnh `commit` dữ liệu. Nếu xảy ra bất kỳ RuntimeException nào, Proxy tự động gọi lệnh `rollback` toàn bộ để giữ DB sạch sẽ.
5. **Hỏi: Sự khác biệt giữa `GenerationType.IDENTITY` và `GenerationType.SEQUENCE` trong việc tạo Khóa chính?**
   * **Trả lời**: `IDENTITY` dựa hoàn toàn vào cột tự tăng (auto-increment) của PostgreSQL, giá trị khóa được sinh ra sau khi thực hiện câu lệnh INSERT. `SEQUENCE` sử dụng một đối tượng Sequence độc lập trong DB để lấy trước giá trị khóa trước khi INSERT. Dự án sử dụng `IDENTITY` cho khóa chính bảng sinh viên.
6. **Hỏi: Chỉ mục (Index) CSDL hoạt động thế nào? Tại sao lớp `SupabaseDatabaseHardeningInitializer` lại tự động tạo index cho khóa ngoại?**
   * **Trả lời**: Index giống như bảng mục lục ở cuối sách giúp tìm thông tin nhanh mà không cần lật từng trang. Trong PostgreSQL, các khóa chính được tự động tạo Index, nhưng khóa ngoại (Foreign Key) thì không. Nếu không tạo Index cho khóa ngoại, các câu lệnh JOIN hoặc xóa dữ liệu có ràng buộc sẽ phải quét toàn bộ bảng (Table Scan) cực kỳ chậm. Hàm `createMissingForeignKeyIndexes` tự động tạo Index cho tất cả các khóa ngoại để tối ưu tốc độ.
7. **Hỏi: Ràng buộc `UniqueConstraint` trên bảng `student_bookmarks` gồm những cột nào? Ý nghĩa của nó?**
   * **Trả lời**: Gồm 3 cột: `student_id`, `target_type`, và `target_id`. Ràng buộc này đảm bảo một sinh viên không thể tạo trùng lặp bookmark cho cùng một đối tượng (ví dụ: bookmark môn học SE104 hai lần).
8. **Hỏi: Cột `embedding` trong bảng `knowledge_chunks` được định nghĩa thế nào trong PostgreSQL? Tại sao kích thước lại là 4096?**
   * **Trả lời**: Được định nghĩa là kiểu dữ liệu `vector(4096)` thuộc extension `vector`. Kích thước là 4096 vì mô hình ngôn ngữ sinh vector (Fireworks Qwen Embedding) trả về vector đặc trưng có đúng 4096 chiều. Kích thước này bắt buộc phải trùng khớp tuyệt đối giữa model AI và khai báo cột DB.
9. **Hỏi: Cách bạn ánh xạ trường kiểu Enum trong JPA (như `OtpPurpose` hay `CourseRelationType`)? Dùng kiểu nào an toàn hơn?**
   * **Trả lời**: Sử dụng `@Enumerated(EnumType.STRING)` (như trong `Otp.java#L22`). Kiểu `STRING` sẽ lưu chuỗi chữ của Enum vào DB (ví dụ: 'EMAIL_VERIFICATION'), an toàn hơn kiểu `ORDINAL` (chỉ lưu số thứ tự 0, 1, 2) vì nếu ta thêm Enum mới ở giữa, thứ tự số bị đảo lộn sẽ làm sai lệch dữ liệu cũ.
10. **Hỏi: Hibernate Batch Processing được cấu hình thế nào trong dự án? Mục tiêu của nó?**
    * **Trả lời**: Cấu hình trong `application.yaml` qua các biến `spring.jpa.properties.hibernate.jdbc.batch_size` (cấu hình trong commit `15b6947`). Mục tiêu là gom nhiều câu lệnh INSERT/UPDATE độc lập thành một lô duy nhất gửi xuống DB một lần để giảm số phiên khứ hồi mạng (Network Roundtrips), tăng hiệu năng ghi dữ liệu.
11. **Hỏi: Cascade delete (Xóa liên đới) được sử dụng ở đâu trong CSDL?**
    * **Trả lời**: Sử dụng ở các bảng quan hệ phụ thuộc như `community_presences` hay `chat_messages`. Khi một `student_users` bị xóa tài khoản, toàn bộ tin nhắn chat và thông tin online liên quan của sinh viên đó sẽ tự động bị xóa theo nhờ ràng buộc `ON DELETE CASCADE`.
12. **Hỏi: Sự khác biệt giữa `save()` và `saveAndFlush()` trong Spring Data JPA?**
    * **Trả lời**: `save()` chỉ đưa đối tượng vào bộ nhớ cache của Hibernate (Persistence Context), dữ liệu chỉ thực sự ghi xuống DB khi kết thúc Transaction. `saveAndFlush()` ép buộc Hibernate phải gửi ngay câu lệnh SQL xuống DB lập tức để đồng bộ dữ liệu tại thời điểm đó, rất hữu ích khi cần lấy ID tự tăng ngay để xử lý bước tiếp theo.
13. **Hỏi: Làm thế nào bạn thực thi một câu lệnh SQL thuần (Native Query) bằng JPA?**
    * **Trả lời**: Sử dụng annotation `@Query` đặt trên phương thức Repository và thiết lập thuộc tính `nativeQuery = true` (ví dụ: câu truy vấn `searchHybrid` trong `KnowledgeChunkRepository.java`).
14. **Hỏi: Làm sao bạn lưu một trường kiểu JSON từ Java xuống PostgreSQL thông qua Hibernate?**
    * **Trả lời**: Em sử dụng thư viện Jackson để định nghĩa kiểu trường là `JsonNode` (như trong `Course.java#L166`) kết hợp với chú thích `@JdbcTypeCode(SqlTypes.JSON)`. Hibernate sẽ tự động biến đổi đối tượng JSON của Java thành chuỗi văn bản JSONB trước khi ghi xuống cột tương ứng trong CSDL.
15. **Hỏi: Làm sao để ghi đè hoặc cấu hình lại cơ chế tự tạo bảng của Hibernate trong Production?**
    * **Trả lời**: Thiết lập thuộc tính `spring.jpa.hibernate.ddl-auto` về giá trị `none` hoặc `validate` trong file `application-prod.yaml` để ngăn chặn Hibernate tự ý thay đổi cấu trúc bảng gây mất mát dữ liệu thực tế, việc cập nhật cấu trúc phải đi qua các file script migration chính thống.

---

## 8. Dịch vụ AI Tutor & Retrieval-Augmented Generation (RAG)

### 8.1 Quy trình RAG Pipeline thực tế trong codebase

```text
Tài liệu gốc (DB / Web)
        │
        ▼ (CourseKnowledgeBootstrapService)
Tạo Markdown trung gian
        │
        ▼ (CourseKnowledgeIndexer)
Phân đoạn (Chunking) ──(Cắt 4000 ký tự, đè 500 ký tự gối đầu)
        │
        ├── Emits SECTION_SUMMARY (Tóm tắt mục lớn)
        └── Emits DETAIL (Phần chi tiết gán parent_chunk_id)
        │
        ▼ (FireworksEmbeddingService - API 4096 chiều)
Sinh vector embedding cho từng đoạn
        │
        ▼ (PostgreSQL pgvector)
Lưu vào bảng knowledge_chunks (cột embedding)
```

Khi sinh viên đặt câu hỏi:
1. **Phân tích câu hỏi**: `SubjectQaService` dùng Regex quét lấy mã môn học (ví dụ: `"SE104"`).
2. **Tìm kiếm Hybrid**: Gọi hàm `knowledgeChunkRepository.searchHybrid` thực hiện tìm kiếm kép RRF (pgvector + FTS) cộng thêm điểm ưu tiên theo nguồn gốc tài liệu để lấy ra các đoạn text chất lượng nhất.
3. **Gọi Firecrawl (Dự phòng Web)**: Nếu intent đòi hỏi thông tin cập nhật trên Web, gọi `webSearchService` lấy top 5 kết quả, sau đó gọi `FirecrawlClient` cào chi tiết 2 trang web đầu tiên để lấy văn bản thô nhét vào ngữ cảnh.
4. **Xây dựng Prompt**: `LlmContextBuilder` lắp ráp dữ liệu môn học từ DB + Chunks RAG + Text cào Web thành ngữ cảnh Prompt kèm chỉ thị chống ảo tưởng.
5. **Stream LLM**: Gọi `OpenCodeAiService.streamCompletion` đẩy prompt sang LLM, nhận luồng SSE trả về Client.

### 8.2 Bộ 25 câu hỏi chuyên sâu về AI Tutor & RAG

1. **Hỏi: RAG là gì? Tại sao phải dùng RAG thay vì gửi thẳng câu hỏi cho LLM?**
   * **Trả lời**: RAG (Retrieval-Augmented Generation) là quy trình tìm kiếm tài liệu liên quan trước rồi mới đưa cho LLM tổng hợp câu trả lời. Phải dùng RAG vì LLM mặc định không có tri thức về dữ liệu nội bộ (ví dụ: chương trình đào tạo của UIT), và LLM dễ bị ảo tưởng (hallucination) tự bịa thông tin. RAG cung cấp ngữ cảnh thực tế làm căn cứ giúp AI trả lời chính xác 100%.
2. **Hỏi: Kích thước Chunk Size và Chunk Overlap trong dự án của bạn là bao nhiêu? Tại sao lại chọn con số đó?**
   * **Trả lời**: Mục tiêu kích thước chunk (`TARGET_CHUNK_SIZE`) là **4000 ký tự**, tối đa `MAX_CHUNK_SIZE` là **5000 ký tự**, độ gối đầu (`OVERLAP_CHARS`) là **500 ký tự** (cấu hình trong `CourseKnowledgeIndexer.java#L34-L36`). Kích thước này đủ lớn để bao quát trọn vẹn ý nghĩa của một phân mục giáo trình mà không vượt quá giới hạn ngữ cảnh (Context Window) của LLM, gối đầu 500 ký tự giúp giữ sự liền mạch thông tin giữa hai phân đoạn liền kề.
3. **Hỏi: Cơ chế phân mảnh hai tầng (Hierarchical Chunking) hoạt động thế nào trong dự án?**
   * **Trả lời**: Khi phân tích markdown, `CourseKnowledgeIndexer` sinh ra hai loại chunk: `SECTION_SUMMARY` (tóm tắt tổng quát mục lớn) và `DETAIL` (các đoạn chi tiết cắt nhỏ). Mỗi chunk chi tiết đều lưu liên kết `parent_chunk_id` trỏ về chunk tóm tắt của nó. Cơ chế này giúp khi tìm kiếm tìm trúng đoạn chi tiết, ta vẫn có thể truy xuất ngữ cảnh mục lớn của nó để câu trả lời mạch lạc hơn.
4. **Hỏi: Embedding is gì? Làm thế nào để tính toán sự tương đồng giữa hai vector?**
   * **Trả lời**: Embedding là quá trình chuyển đổi một đoạn văn bản thành một chuỗi số thực (vector) thể hiện tọa độ ngữ nghĩa của nó trong không gian đa chiều. Sự tương đồng giữa hai vector được tính toán bằng công thức toán học **Cosine Similarity**: chia tích vô hướng của hai vector cho tích độ dài của chúng. Kết quả càng gần 1 thể hiện hai đoạn văn bản càng có nghĩa giống nhau.
5. **Hỏi: Tại sao bạn phải convert mảng float[] sang String khi lưu vector xuống PostgreSQL?**
   * **Trả lời**: Vì driver JDBC chuẩn của PostgreSQL không hỗ trợ truyền trực tiếp kiểu dữ liệu mảng vector của extension `pgvector` dưới dạng kiểu Java. Em phải tự viết hàm chuyển đổi `vectorToPgString` định dạng mảng float thành chuỗi text dạng `"[0.1,0.2,-0.5]"` rồi thực hiện ép kiểu `CAST(? AS vector)` trong câu lệnh SQL.
6. **Hỏi: Reciprocal Rank Fusion (RRF) hoạt động như thế nào trong truy vấn `searchHybrid`?**
   * **Trả lời**: Thuật toán RRF thực hiện cộng điểm đảo thứ hạng của kết quả từ hai luồng tìm kiếm (Vector Search và FTS). Công thức: `Score = 1 / (60 + R_vector) + 1 / (60 + R_fts)`. Kết quả nào đứng hạng cao ở cả hai luồng sẽ có điểm tổng hợp RRF vượt trội, đảm bảo kết quả vừa khớp từ khóa vừa khớp ý nghĩa ngữ nghĩa.
7. **Hỏi: Bạn cấu hình các điểm cộng thêm (Boost) cho những loại tài liệu nào trong câu lệnh SQL lai?**
   * **Trả lời**: Em boost cho các nguồn có độ tin cậy cao: Nguồn `OFFICIAL` được cộng thêm 0.040, nguồn tài liệu `SYLLABUS` cộng thêm 0.025, và các chunk loại `SECTION_SUMMARY` được ưu tiên cộng 0.010 để hỗ trợ tìm kiếm tổng quát.
8. **Hỏi: Nếu API sinh vector (Embedding Provider) bị timeout hoặc gặp lỗi mạng, hệ thống xử lý thế nào?**
   * **Trả lời**: Hệ thống kích hoạt cơ chế tự bảo vệ degradation tại `SubjectQaService.java#L67-L70`. Nó sẽ tạm thời đánh dấu embedding bị suy giảm và chuyển sang chạy chế độ tìm kiếm Full-Text Search đơn thuần kết hợp Web Search để duy trì tính hoạt động của ứng dụng.
9. **Hỏi: Phân loại ý định (Intent Classification) trong RAG giúp ích gì cho hệ thống?**
   * **Trả lời**: Giúp hệ thống nhận diện nhanh câu hỏi thuộc loại gì (Chào hỏi, hỏi lộ trình, hay hỏi chi tiết môn học). Nếu là chào hỏi, hệ thống trả lời trực tiếp mà không cần gọi RAG/Web Search đắt đỏ, giúp tiết kiệm chi phí và tài nguyên đáng kể.
10. **Hỏi: Làm sao bạn cào nội dung từ các trang web bên ngoài thời gian thực để đưa vào ngữ cảnh RAG?**
    * **Trả lời**: Hệ thống tích hợp dịch vụ **Firecrawl** (thông qua `FirecrawlClient`). Khi phát hiện câu hỏi cần tìm thông tin ngoài, hệ thống lấy URL từ kết quả tìm kiếm Google/Exa rồi gọi Firecrawl cào sạch nội dung trang web đó chuyển đổi thành dạng Markdown sạch để nhét thẳng vào context của LLM.
11. **Hỏi: Rủi ro khi AI sinh câu trả lời bị trống (Blank Response) trong luồng streaming là gì và bạn khắc phục thế nào?**
    * **Trả lời**: Đôi khi luồng stream HTTP bị đứt giữa chừng hoặc LLM không trả về ký tự nào. Ở phương thức `completeStream`, nếu phát hiện buffer câu trả lời bị trống, hệ thống sẽ tự động kích hoạt cuộc gọi đồng bộ One-shot dự phòng để lấy bằng được câu trả lời đầy đủ trả về cho Client.
12. **Hỏi: Tại sao bạn không lưu toàn bộ dữ liệu cào Web vào Vector Database?**
    * **Trả lời**: Dữ liệu cào Web có tính biến động cao, tính chính xác không tuyệt đối và dung lượng rất lớn. Việc nhồi nhét dữ liệu rác từ Web vào Vector DB nội bộ sẽ làm loãng tri thức chính thống của trường. Em chỉ cào và sử dụng tạm thời làm ngữ cảnh bổ sung cho lượt chat đó chứ không lưu trữ lâu dài.
13. **Hỏi: Nếu database sử dụng vector(1536) của OpenAI nhưng trong Java bạn truyền vector 4096 chiều của Qwen thì hậu quả gì?**
    * **Trả lời**: PostgreSQL sẽ lập tức ném ra lỗi cú pháp nghiêm trọng và từ chối thực hiện câu lệnh chèn/truy vấn vì kích thước vector không khớp với định nghĩa cột. Lớp `FireworksEmbeddingService` có hàm `validateDimensions` để kiểm tra độ dài mảng float đầu ra trước khi gửi xuống DB để bắt lỗi sớm.
14. **Hỏi: Làm sao bạn truy vết nguồn (Citations) để trả về cho người dùng biết AI lấy thông tin từ đâu?**
    * **Trả lời**: Khi truy vấn `knowledge_chunks`, câu lệnh SQL thực hiện `JOIN` với bảng `knowledge_sources` để lấy tên file gốc và URL. Các thông tin này được gom lại thành mảng `sources` trả về trong DTO `SubjectQaResponse` để frontend render thành các nút liên kết tài liệu nguồn.
15. **Hỏi: WebSearchService sử dụng API bên ngoài nào để tìm kiếm thông tin?**
    * **Trả lời**: Sử dụng dịch vụ tìm kiếm của **Exa** (cấu hình trong `ExaProperties.java`) để thực hiện các câu truy vấn tìm kiếm chuyên sâu trên các trang web học tập và diễn đàn công nghệ.
16. **Hỏi: Làm thế nào bạn ngăn chặn việc lạm dụng tính năng Web Search gây tốn chi phí API?**
    * **Trả lời**: Em áp dụng giới hạn cứng tại lớp `SubjectQaService.java#L64-L65`: Mỗi phiên chat sinh viên (`sessionId`) chỉ được thực hiện tối đa 3 lần gọi Web Search. Nếu vượt quá giới hạn này, hệ thống sẽ tự động tắt tính năng tìm kiếm web và chỉ sử dụng kho dữ liệu RAG nội bộ sẵn có.
17. **Hỏi: Làm sao để nạp dữ liệu từ các file PDF/Markdown của đề cương môn học vào hệ thống?**
    * **Trả lời**: Em viết dịch vụ indexer [CourseKnowledgeBootstrapService](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/knowledge/CourseKnowledgeBootstrapService.java) chạy ngầm lúc ứng dụng sẵn sàng. Nó tự động chuyển đổi cấu trúc môn học của DB sang dạng Markdown, băm hash kiểm tra thay đổi, rồi đẩy vào lớp phân mảnh và sinh vector lưu xuống PostgreSQL.
18. **Hỏi: Khóa ngoại giữa `knowledge_chunks` và `knowledge_sources` được thiết lập như thế nào?**
    * **Trả lời**: Được liên kết qua trường `source_id` tham chiếu đến bảng `knowledge_sources` với thiết lập `ON DELETE CASCADE`. Khi xóa một nguồn tài liệu, tất cả các phân mảnh vector liên quan sẽ tự động bị xóa sạch.
19. **Hỏi: Tại sao bạn chọn lưu trữ Vector Database ngay trong PostgreSQL (pgvector) thay vì dùng Pinecone hay Qdrant?**
    * **Trả lời**: Việc tích hợp pgvector trực tiếp vào PostgreSQL giúp hệ thống cực kỳ gọn nhẹ: không cần cài đặt thêm phần mềm máy chủ độc lập, tận dụng được các ràng buộc khóa ngoại ACID chuẩn của SQL, dễ dàng viết các câu truy vấn JOIN kết hợp dữ liệu bảng truyền thống với vector tương đồng trong một câu lệnh duy nhất.
20. **Hỏi: Làm thế nào hệ thống của bạn tự động dọn dẹp các chunk cũ của một source khi source đó được index lại?**
    * **Trả lời**: Ở đầu phương thức `indexMarkdown` trong lớp `CourseKnowledgeIndexer.java#L51`, hệ thống luôn gọi `knowledgeChunkRepository.deleteBySourceId(source.getId())` để xóa sạch toàn bộ các chunk cũ của nguồn đó trước khi tiến hành ghi đè dữ liệu phân mảnh mới.
21. **Hỏi: Lộ trình học tập trực quan (Roadmap) được sinh ra từ đâu?**
    * **Trả lời**: Khi sinh viên hỏi về lộ trình, hệ thống gọi `RoadmapGenerator` phân tích môn học, gọi `KnowledgeGraphService` để tính toán các môn học tiên quyết, rồi đóng gói cấu trúc JSON phân kỳ (Semester) gửi về cho React UI tự động vẽ sơ đồ hình tròn/dây truyền học tập trực quan.
22. **Hỏi: Làm sao để kiểm thử độc lập chất lượng tìm kiếm RAG mà không cần gọi LLM?**
    * **Trả lời**: Em tạo endpoint `/api/admin/knowledge/search` công khai cho Admin (chỉ dùng quyền Admin) để test thử kết quả truy xuất thô của Hybrid Search kèm theo điểm số chi tiết cho từng chunk văn bản.
23. **Hỏi: Tại sao bạn lưu `rawText` của tài liệu trong bảng `knowledge_sources`?**
    * **Trả lời**: Để phục vụ cho việc hiển thị lại toàn bộ tài liệu gốc cho Admin kiểm tra khi cần thiết, hoặc hỗ trợ việc chia nhỏ lại chunk nếu cấu hình kích thước phân mảnh thay đổi trong tương lai.
24. **Hỏi: Tham số `temperature` của LLM được cấu hình như thế nào trong dự án?**
    * **Trả lời**: Được cấu hình thấp (thường bằng 0.1 hoặc 0.2) tại lớp `AiConfig.java` để đảm bảo LLM trả lời mang tính logic, bám sát ngữ cảnh tài liệu học tập chuẩn cung cấp hơn là sinh chữ mang tính sáng tạo bay bổng.
25. **Hỏi: Làm thế nào hệ thống của bạn tự động dọn dẹp các chunk cũ của một source khi source đó được index lại?**
    * **Trả lời**: Ở đầu phương thức `indexMarkdown` trong lớp `CourseKnowledgeIndexer.java#L51`, hệ thống luôn gọi `knowledgeChunkRepository.deleteBySourceId(source.getId())` để xóa sạch toàn bộ các chunk cũ của nguồn đó trước khi tiến hành ghi đè dữ liệu phân mảnh mới.

---

## 9. GitHub Scanning & Crawling

* **Cơ chế**: `GithubScanService` tích hợp trực tiếp với API GitHub REST qua `/search/repositories`.
* **Rate Limits**: Giới hạn của GitHub Search API rất nghiêm ngặt (30 yêu cầu/phút đối với token xác thực). 
* **Quản lý**: Tiến trình bulk scan sử dụng `Thread.sleep(2050)` giữa các đợt quét của mỗi môn học để tránh bị khóa. Khi phát hiện mã lỗi 403 (Rate Limit), tiến trình tự động ngủ đông 45 giây.
* **Metadata**: Robot lưu trữ các trường dữ liệu gồm Stars, Forks, Primary Language, Topics, Description, LastPushedAt.

### Bộ 15 câu hỏi về GitHub Scanning

1. **Hỏi: Tại sao bạn không quét cả các repository bị Fork trong kết quả tìm kiếm?**
   * **Trả lời**: Để đảm bảo chất lượng. Các repository bị fork thường là bản sao nguyên bản của người khác, không phản ánh đúng dự án tự làm của sinh viên khóa đó. Lọc bỏ fork giúp giữ danh sách sạch sẽ.
2. **Hỏi: WebClient được cấu hình như thế nào để gọi API GitHub?**
   * **Trả lời**: Được cấu hình tại lớp [GithubClientConfig](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/GithubClientConfig.java) với Base URL là `https://api.github.com`, nạp token từ cấu hình và thiết lập header `User-Agent` bắt buộc của GitHub.
3. **Hỏi: Làm sao bạn biết thời điểm đẩy code mới nhất (Activity Date) của một repository?**
   * **Trả lời**: Em gọi API lấy commit mới nhất `/repos/{owner}/{repo}/commits` trên nhánh mặc định. Nếu không lấy được, em sẽ lùi về lấy trường `pushed_at` hoặc `updated_at` trong metadata của repo.
4. **Hỏi: Làm sao bạn hiển thị lịch sử logs quét cho Admin xem thời gian thực?**
   * **Trả lời**: Em duy trì một danh sách thread-safe `scanLogs` dạng `Collections.synchronizedList(new ArrayList<>())` trong `GithubScanService.java`. Mỗi bước quét đều được định dạng timestamp ghi vào danh sách này để endpoint `/scan-logs` lấy về hiển thị.
5. **Hỏi: Tại sao bạn giới hạn kích thước lưu trữ README Excerpt là 1200 ký tự?**
   * **Trả lời**: File README có thể rất dài (lên tới hàng trăm KB). Lưu toàn bộ vào DB sẽ gây lãng phí dung lượng bộ nhớ. 1200 ký tự đầu tiên là đủ để giới thiệu tổng quan dự án làm gì và công nghệ sử dụng.
6. **Hỏi: Cách hệ thống xử lý khi gặp một repository không có file README?**
   * **Trả lời**: Hệ thống sẽ gán trường `hasReadme = false` và `readmeExcerpt = null`. Điều này vẫn cho phép lưu repo candidate nhưng sẽ ghi nhận thông tin thiếu hụt cho Admin biết khi duyệt.
7. **Hỏi: Lớp `RepoCandidate` có các trạng thái (Status) nào?**
   * **Trả lời**: Có 3 trạng thái định nghĩa trong Enum `RepoCandidateStatus`: `NEW` (mới quét về), `APPROVED` (được duyệt đưa lên danh sách chính), và `REJECTED` (bị từ chối).
8. **Hỏi: Chuyện gì xảy ra khi Admin bấm "Duyệt" (Approve) một Repo Candidate?**
   * **Trả lời**: Hệ thống sẽ chuyển trạng thái candidate sang `APPROVED`, đồng thời copy toàn bộ dữ liệu của candidate sang bảng chính `github_repos` để hiển thị công khai cho sinh viên tra cứu.
9. **Hỏi: Tại sao hệ thống giới hạn độ sâu thư mục quét tối đa là 3?**
   * **Trả lời**: Vì sinh viên chỉ cần xem cấu trúc thư mục tổng quan (như các thư mục src, docs, tests) để hình dung kiến trúc dự án. Quét quá sâu sẽ làm tăng dung lượng lưu trữ File Tree trong DB một cách vô ích.
10. **Hỏi: Cơ chế Virtual Threads trong quét Github mang lại lợi ích gì so với Thread thông thường?**
    * **Trả lời**: Giúp việc sleep chờ đợi rate limit không gây nghẽn luồng hệ thống. Ta có thể thoải mái cho luồng ảo sleep mà không làm hao tổn bộ nhớ RAM của hệ thống.
11. **Hỏi: GitHub API có phân biệt chữ hoa chữ thường trong từ khóa tìm kiếm không?**
    * **Trả lời**: API tìm kiếm của GitHub không phân biệt chữ hoa chữ thường. Tuy nhiên, hệ thống vẫn chuẩn hóa chữ hoa mã môn học trước khi lưu để đồng bộ dữ liệu.
12. **Hỏi: Làm sao bạn kiểm tra quyền hạn Admin trước khi cho phép gọi API quét GitHub?**
    * **Trả lời**: Cấu hình tại lớp `SecurityConfig.java` yêu cầu mọi đường dẫn có tiền tố `/api/admin/**` bắt buộc phải có quyền `ROLE_ADMIN`.
13. **Hỏi: Biến `scanRunning` kiểu `AtomicBoolean` giải quyết bài toán gì?**
    * **Trả lời**: Tránh việc Admin nhấn nút quét hàng loạt nhiều lần liên tiếp gây đè luồng. Lệnh `compareAndSet` đảm bảo chỉ có duy nhất một tiến trình quét bulk scan chạy tại một thời điểm.
14. **Hỏi: API GitHub Search trả về tối đa bao nhiêu kết quả cho một truy vấn?**
    * **Trả lời**: GitHub giới hạn tối đa 1000 kết quả cho mỗi câu truy vấn tìm kiếm. Hệ thống cấu hình lấy tối đa 100 kết quả chất lượng nhất (`per_page=100`) để tối ưu hiệu năng.
15. **Hỏi: Bạn làm thế nào để tự động liên kết repository quét được với đúng môn học?**
    * **Trả lời**: Mỗi lần quét được thực hiện theo môn học cụ thể (`courseId`). Bản ghi candidate sinh ra sẽ lưu trực tiếp khóa ngoại trỏ tới thực thể `Course` đang quét.

---

## 10. Community Chat & WebSocket

* **Giao thức**: WebSocket Message Broker sử dụng STOMP sub-protocol.
* **Xác thực**: Kiểm tra token JWT tại thời điểm bắt tay CONNECT thông qua `ChannelInterceptor`.
* **Presence**: Đồng bộ trạng thái online thời gian thực lưu trong bảng CSDL `community_presences`. Lắng nghe các sự kiện `SessionSubscribeEvent`, `SessionUnsubscribeEvent`, `SessionDisconnectEvent`.

### Bộ 15 câu hỏi về WebSocket & Chat

1. **Hỏi: Tại sao WebSocket lại tốt hơn HTTP Long Polling cho chức năng chat?**
   * **Trả lời**: HTTP Long Polling yêu cầu client liên tục gửi request thăm dò lên server, gây tốn băng thông và trễ tin nhắn. WebSocket duy trì một kết nối TCP song phương liên tục, giúp truyền tin nhắn hai chiều ngay lập tức với overhead cực nhỏ.
2. **Hỏi: STOMP là gì? Tại sao bạn dùng nó trên WebSocket?**
   * **Trả lời**: STOMP (Simple Text Oriented Messaging Protocol) là giao thức truyền tin hướng văn bản chạy trên WebSocket. Dùng STOMP giúp định nghĩa rõ ràng cấu trúc tin nhắn (Header, Body) và cơ chế đăng ký kênh (Publish/Subscribe) thay vì phải tự viết khung truyền tin thô.
3. **Hỏi: Khung tin CONNECT của STOMP chứa thông tin gì nhạy cảm?**
   * **Trả lời**: Chứa Header `Authorization` chứa token JWT của sinh viên dùng để thực hiện xác thực bảo mật trước khi kết nối được thiết lập thành công.
4. **Hỏi: Làm sao để gửi một tin nhắn chat đến đúng phòng cụ thể?**
   * **Trả lời**: Client gửi tin nhắn đến địa chỉ `/app/chat.send/{channelId}`. Controller xử lý và lưu tin nhắn, sau đó phát ra địa chỉ `/topic/channel/{channelId}` để tất cả các client đang lắng nghe phòng đó nhận được.
5. **Hỏi: Sự kiện `SessionSubscribeEvent` kích hoạt khi nào?**
   * **Trả lời**: Kích hoạt khi một Client gửi khung tin SUBSCRIBE đăng ký lắng nghe một địa chỉ cụ thể trên WebSocket Broker.
6. **Hỏi: Làm sao bạn trích xuất được `channelId` từ địa chỉ subscribe động (ví dụ: `/topic/channel/5`)?**
   * **Trả lời**: Em sử dụng biểu thức chính quy `Pattern.compile("^/topic/channel/(\\d+)$")` tại lớp `CommunityPresenceEventListener.java#L23` để bắt và chuyển đổi nhóm số thành `channelId` kiểu Long.
7. **Hỏi: Sự khác biệt giữa `SimpleMessagingTemplate` và `@SendTo` là gì?**
   * **Trả lời**: `@SendTo` là annotation tĩnh chỉ gửi được tin nhắn về một địa chỉ cố định cấu hình sẵn. `SimpleMessagingTemplate` là đối tượng Java động giúp ta gửi tin nhắn đến bất kỳ địa chỉ nào một cách linh hoạt trong code.
8. **Hỏi: Bạn lưu tin nhắn chat vào bảng nào trong database?**
   * **Trả lời**: Tin nhắn chat được lưu vào bảng `community_messages` (ánh xạ qua thực thể `CommunityMessage`) bao gồm nội dung, thời gian tạo, và khóa ngoại trỏ tới sinh viên gửi cùng kênh chat tương ứng.
9. **Hỏi: Có cơ chế xóa tin nhắn (deleted) không?**
   * **Trả lời**: Thực thể `CommunityMessage` có cờ `deleted` kiểu boolean. Khi sinh viên thu hồi tin nhắn, hệ thống cập nhật cờ này thành `true` chứ không xóa vật lý bản ghi để giữ tính vết lịch sử.
10. **Hỏi: Bạn làm thế nào để hạn chế spam tin nhắn trong phòng chat?**
    * **Trả lời**: Có thể áp dụng interceptor đo tần suất gửi tin nhắn của từng sessionId hoặc tích hợp bộ lọc từ ngữ thô tục ở tầng Service trước khi broadcast tin nhắn đi.
11. **Hỏi: Làm sao để sinh viên biết được có những ai đang online trong phòng chat?**
    * **Trả lời**: Client đăng ký lắng nghe đường dẫn `/topic/channel/{channelId}/presence`. Mỗi khi có sự thay đổi về trạng thái online, backend tự động broadcast danh sách online mới về đường dẫn này.
12. **Hỏi: Bạn sắp xếp danh sách sinh viên online theo thứ tự nào?**
    * **Trả lời**: Sắp xếp theo tên hiển thị của sinh viên (`displayName`) không phân biệt chữ hoa chữ thường bằng hàm so sánh `String.CASE_INSENSITIVE_ORDER` tại lớp `CommunityPresenceService.java#L76`.
13. **Hỏi: WebSocket Heartbeat là gì? Bạn cấu hình nó như thế nào?**
    * **Trả lời**: Heartbeat là cơ chế gửi các khung tin trống định kỳ giữa Client và Server để kiểm tra đường truyền còn sống hay không. Spring WebSocket tự động quản lý việc này qua cấu hình mặc định của điểm kết nối SockJS.
14. **Hỏi: Nếu client gửi tin nhắn đến một `channelId` không tồn tại thì sao?**
    * **Trả lời**: Tầng Service xử lý sẽ ném ra `IllegalArgumentException` ("Channel not found"), luồng xử lý dừng lại và tin nhắn lỗi sẽ không được lưu hay broadcast đi.
15. **Hỏi: Giao thức SockJS giúp giải quyết vấn đề gì trong ứng dụng Web?**
    * **Trả lời**: Giúp duy trì kết nối chat hoạt động bình thường trên các trình duyệt cũ hoặc các hệ thống mạng Proxy bị chặn giao thức WebSocket bằng cách tự động lùi về các cơ chế dự phòng như HTTP Streaming hay Ajax Polling.

---

## 11. Giảng viên mở ngẫu nhiên một file (Chi tiết 25 File trọng yếu)

Dưới đây là cẩm nang trả lời nhanh (20-40 giây) cho từng file khi thầy cô kích chuột mở ngẫu nhiên:

### 1. [SecurityConfig.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SecurityConfig.java)
* **Loại**: Configuration (Tầng bảo mật).
* **Trách nhiệm**: Cổng an ninh trung tâm. Định nghĩa các quy tắc phân quyền cho mọi endpoint API, thiết lập cơ chế Stateless, cấu hình CORS, tắt CSRF, gán bộ mã hóa mật khẩu BCrypt, và tích hợp bộ lọc xác thực `JwtAuthenticationFilter`.
* **Ai gọi / Gọi ai**: Gọi `PasswordEncoder`, `CorsConfigurationSource`. Được Spring Boot tự động nạp khi khởi chạy.
* **Hàm chính**: `securityFilterChain` thiết lập các luật bảo vệ.
* **Xóa đi thì sao**: Toàn bộ hệ thống sẽ không còn được bảo vệ, bất kỳ ai cũng có thể gọi các API nội bộ hoặc xem dữ liệu của sinh viên khác.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là file cấu hình bảo mật chính của ứng dụng sử dụng Spring Security. Nó phân cấp các URL: public hoàn toàn cho việc tra cứu môn học, yêu cầu đăng nhập đối với AI Tutor, và bắt buộc có ROLE_ADMIN đối với các API quản trị hệ thống."*

### 2. [JwtAuthenticationFilter.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/JwtAuthenticationFilter.java)
* **Loại**: Security Filter (Bộ lọc xác thực).
* **Trách nhiệm**: Đánh chặn mọi request đi vào hệ thống để kiểm tra Header `Authorization`. Thực hiện xác thực token, kiểm tra danh sách đen logout, xác minh trạng thái hoạt động của sinh viên, và nạp danh tính vào SecurityContext của Spring.
* **Ai gọi / Gọi ai**: Được gọi bởi Spring Security filter chain trước lớp `UsernamePasswordAuthenticationFilter`. Gọi tới `JwtService`, `RevokedTokenStore`, `StudentUserRepository`.
* **Hàm chính**: `doFilterInternal` thực hiện lọc và xác thực.
* **Xóa đi thì sao**: Hệ thống sẽ không thể nhận diện được ai đang đăng nhập, mọi request yêu cầu xác thực đều bị từ chối (401).
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là bộ lọc kiểm soát an ninh chạy trước mỗi request. Nhiệm vụ của nó là bóc tách token Bearer JWT từ Header, giải mã lấy mã sinh viên, kiểm tra xem tài khoản có bị khóa trong DB không, và thiết lập quyền hạn tương ứng cho request đó."*

### 3. [JwtService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/JwtService.java)
* **Loại**: Service (Mã hóa/Bảo mật).
* **Trách nhiệm**: Quản lý vòng đời của token JWT. Tạo token mới sau khi đăng nhập thành công, giải mã token để lấy thông tin claims (mã sinh viên, phân loại quyền, ngày hết hạn), và kiểm tra tính hợp lệ của chữ ký.
* **Ai gọi / Gọi ai**: Gọi thư viện `io.jsonwebtoken`. Được gọi bởi `JwtAuthenticationFilter` và các Service xác thực.
* **Hàm chính**: `generateToken` (tạo khóa an ninh), `parseToken` (giải mã).
* **Xóa đi thì sao**: Không thể cấp phát thẻ thông hành mới cho người dùng đăng nhập và không thể giải mã thông tin an ninh gửi lên.
* **Nói trước hội đồng**: *"Thưa thầy/cô, lớp dịch vụ này chịu trách nhiệm ký và giải mã Token JWT bằng thuật toán HMAC-SHA256. Nó chứa cơ chế an toàn tự động dừng hệ thống nếu phát hiện môi trường production chạy bằng secret key mặc định nhằm tránh lỗ hổng bảo mật."*

### 4. [RevokedTokenStore.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/RevokedTokenStore.java)
* **Loại**: Service (An ninh).
* **Trách nhiệm**: Quản lý danh sách đen chứa các token JWT đã bị thu hồi khi người dùng nhấn Đăng xuất. Lưu trữ in-memory dùng `ConcurrentHashMap` hỗ trợ đa luồng an toàn.
* **Ai gọi / Gọi ai**: Được gọi bởi `StudentAuthService` (để ghi nhận revoke) và `JwtAuthenticationFilter` (để kiểm tra cấm).
* **Hàm chính**: `revoke()` lưu token, `isRevoked()` kiểm tra trạng thái.
* **Xóa đi thì sao**: Người dùng nhấn đăng xuất xong vẫn có thể dùng lại token cũ để truy cập API cho đến khi token đó hết hạn tự nhiên.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là kho lưu trữ danh sách đen các token đã đăng xuất. Để tối ưu RAM, em lưu in-memory kèm cơ chế dọn dẹp định kỳ các token đã quá hạn tự nhiên của nó ra khỏi Map."*

### 5. [StudentAuthController.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentAuthController.java)
* **Loại**: Controller (Web Layer).
* **Trách nhiệm**: Cung cấp các API công khai cho sinh viên: Đăng nhập, đăng ký tài khoản, xác thực OTP kích hoạt, gửi lại mã OTP, đặt lại mật khẩu, lấy thông tin cá nhân hiện tại, và cập nhật avatar thông qua Supabase Storage.
* **Ai gọi / Gọi ai**: Gọi `StudentAuthService`, `SupabaseStorageService`.
* **Hàm chính**: `login()`, `register()`, `uploadAvatar()`.
* **Xóa đi thì sao**: Giao diện đăng nhập/đăng ký của sinh viên trên React sẽ hoàn toàn bị tê liệt do mất endpoint kết nối.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là Controller quản lý toàn bộ luồng đăng nhập, đăng ký và cập nhật hồ sơ của sinh viên. Nó sử dụng `@AuthenticationPrincipal` để lấy trực tiếp mã sinh viên đã xác thực từ token một cách an toàn."*

### 6. [StudentAuthService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/StudentAuthService.java)
* **Loại**: Service (Business Logic Layer).
* **Trách nhiệm**: Thực hiện nghiệp vụ xác thực của sinh viên: Mã hóa mật khẩu bằng BCrypt, tạo bản ghi OTP lưu vào DB và gọi EmailService gửi mail, kích hoạt trạng thái active sau khi khớp OTP, đổi mật khẩu, kiểm tra tần suất giới hạn (Rate limit).
* **Ai gọi / Gọi ai**: Gọi `StudentUserRepository`, `OtpRepository`, `JwtService`, `EmailService`, `RateLimitServices`. Được gọi bởi `StudentAuthController`.
* **Hàm chính**: `login()`, `register()`, `verifyOtp()`.
* **Xóa đi thì sao**: Mất đi logic xử lý cốt lõi của luồng xác thực sinh viên.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là lớp dịch vụ xử lý logic nghiệp vụ đăng ký và đăng nhập của sinh viên. Nó đảm bảo tài khoản mới đăng ký phải kích hoạt thành công qua mã OTP gửi về email trường học trước khi cho phép đăng nhập."*

### 7. [SubjectQaController.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/SubjectQaController.java)
* **Loại**: Controller (AI Web Layer).
* **Trách nhiệm**: Cung cấp endpoint cho Trợ lý ảo tư vấn học tập. Endpoint `/query` để hỏi đáp thông thường và endpoint `/stream` trả về kiểu `text/event-stream` để chữ chạy thời gian thực trên giao diện React.
* **Ai gọi / Gọi ai**: Gọi `SubjectQaService`.
* **Hàm chính**: `stream()` quản lý luồng SSE.
* **Xóa đi thì sao**: Sinh viên không thể tương tác hay gửi câu hỏi trò chuyện với AI Tutor được nữa.
* **Nói trước hội đồng**: *"Thưa thầy/cô, Controller này tiếp nhận câu hỏi tư vấn học tập từ sinh viên. Nó hỗ trợ gọi API dạng Server-Sent Events (SSE) giúp phản hồi của AI được truyền về giao diện theo dạng chạy chữ thời gian thực."*

### 8. [SubjectQaService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/SubjectQaService.java)
* **Loại**: Service (AI Orchestrator).
* **Trách nhiệm**: Nhận câu hỏi, quét lấy mã môn học bằng regex, thực hiện song song tìm kiếm RAG nội bộ và Web search, cào văn bản bằng Firecrawl, nhồi dữ liệu chống ảo tưởng vào prompt, gọi LLM, lưu lịch sử chat và xử lý fallback nếu sập mạng.
* **Ai gọi / Gọi ai**: Gọi `KnowledgeRetrievalService`, `OpenCodeAiService`, `WebSearchService`, `FirecrawlClient`, `CourseRepository`. Được gọi bởi `SubjectQaController`.
* **Hàm chính**: `streamQuery()` điều phối luồng streaming, `prepareQuery()` chuẩn bị ngữ cảnh.
* **Xóa đi thì sao**: AI Tutor sẽ bị mất đi toàn bộ khả năng truy xuất dữ liệu môn học thực tế, chỉ còn trả lời bừa dựa trên tri thức cũ của LLM.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là lớp đầu não của RAG pipeline. Nó điều phối việc trích xuất thực thể môn học, kích hoạt tìm kiếm Vector kết hợp FTS trong DB và quét Web song song, đồng thời thiết lập cơ chế tự vệ nếu API AI bị gián đoạn."*

### 9. [KnowledgeRetrievalService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/knowledge/KnowledgeRetrievalService.java)
* **Loại**: Service (RAG Retrieval).
* **Trách nhiệm**: Thực hiện tìm kiếm ngữ nghĩa hybrid: Lấy các biến thể câu hỏi mở rộng, gọi API sinh vector embedding, định dạng vector gửi xuống PostgreSQL chạy câu lệnh SQL lai (searchHybrid), gom kết quả loại bỏ trùng lặp và kích hoạt Reranker.
* **Ai gọi / Gọi ai**: Gọi `EmbeddingService`, `KnowledgeChunkRepository`, `RagQueryPlanner`, `RagResultReranker`. Được gọi bởi `SubjectQaService`.
* **Hàm chính**: `search()` thực hiện tìm kiếm và xếp hạng.
* **Xóa đi thì sao**: Hệ thống RAG mất đi khả năng tìm kiếm thông tin ngữ nghĩa trong cơ sở dữ liệu.
* **Nói trước hội đồng**: *"Thưa thầy/cô, lớp này thực hiện nghiệp vụ tìm kiếm lai (Hybrid Search). Nó lấy câu hỏi của sinh viên, sinh vector đặc trưng và gọi câu lệnh SQL phối hợp pgvector cùng FTS để lấy ra các đoạn giáo trình khớp nhất."*

### 10. [KnowledgeChunkRepository.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/repository/KnowledgeChunkRepository.java)
* **Loại**: Repository (Data Access Layer).
* **Trách nhiệm**: Thực hiện các câu lệnh SQL native phức tạp chèn phân mảnh không kèm vector, cập nhật vector, tìm kiếm vector thuần, và đặc biệt là truy vấn `searchHybrid` kết hợp pgvector và PostgreSQL FTS dùng thuật toán fusion RRF.
* **Ai gọi / Gọi ai**: Được gọi bởi `KnowledgeRetrievalService`, `KnowledgeEmbeddingService`, `CourseKnowledgeIndexer`.
* **Hàm chính**: `searchHybrid()` thực hiện tìm kiếm lai, `updateEmbeddingVector()` ghi vector.
* **Xóa đi thì sao**: Không thể ghi hay tìm kiếm dữ liệu phân mảnh trong PostgreSQL.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là Repository quản lý bảng phân mảnh tài liệu. Nó chứa câu truy vấn SQL Native thực hiện dung hợp RRF giữa xếp hạng Vector Cosine và xếp hạng từ khóa FTS kèm theo tính điểm ưu tiên cho nguồn tài liệu chính thống."*

### 11. [CourseKnowledgeBootstrapService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/knowledge/CourseKnowledgeBootstrapService.java)
* **Loại**: Service (RAG Bootstrapper).
* **Trách nhiệm**: Tự động chuẩn bị dữ liệu RAG cho các môn học. Lấy thông tin môn học và repository liên kết từ DB, định dạng thành chuỗi văn bản Markdown sạch, tính mã băm SHA-256 để kiểm tra trùng lặp trước khi gọi cắt nhỏ và sinh vector.
* **Ai gọi / Gọi ai**: Gọi `KnowledgeSourceRepository`, `CourseKnowledgeIndexer`, `KnowledgeEmbeddingService`. Được gọi bởi `SubjectQaService` (chế độ lười).
* **Hàm chính**: `ensureCourseIndexed()` đảm bảo dữ liệu môn học được nạp vào kho vector.
* **Xóa đi thì sao**: Hệ thống sẽ không thể tự động đồng bộ dữ liệu môn học mới từ cơ sở dữ liệu truyền thống sang kho tìm kiếm vector.
* **Nói trước hội đồng**: *"Thưa thầy/cô, lớp này hoạt động theo cơ chế Lazy Bootstrap. Khi sinh viên hỏi về một môn học lần đầu tiên, nó sẽ tự động lấy thông tin từ DB truyền thống để chuyển đổi sang Markdown và sinh vector nạp vào kho RAG."*

### 12. [CourseKnowledgeIndexer.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/knowledge/CourseKnowledgeIndexer.java)
* **Loại**: Component (Tách từ/Phân mảnh).
* **Trách nhiệm**: Cắt nhỏ văn bản Markdown gốc thành các phân mảnh nhỏ có cấu trúc gối đầu (overlap), tự động nhận diện tiêu đề chương/mục lớn để sinh ra các chunk dạng `SECTION_SUMMARY`, tách lấy số trang tài liệu để phục vụ trích nguồn.
* **Ai gọi / Gọi ai**: Gọi `KnowledgeChunkRepository`. Được gọi bởi `CourseKnowledgeBootstrapService`.
* **Hàm chính**: `indexMarkdown()`, `chunkMarkdown()`.
* **Xóa đi thì sao**: Tài liệu thô sẽ bị nhồi thẳng vào API AI gây quá giới hạn token hoặc bị cắt thô thiển làm mất ngữ nghĩa câu chữ.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là bộ cắt tài liệu. Nó phân tách giáo trình dựa trên tiêu đề đề mục, sinh ra các chunk tóm tắt tổng quan liên kết với các chunk chi tiết qua ID cha con nhằm bảo toàn cấu trúc cây thư mục tài liệu."*

### 13. [KnowledgeEmbeddingService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/knowledge/KnowledgeEmbeddingService.java)
* **Loại**: Service (RAG Vectorizer).
* **Trách nhiệm**: Điều phối việc sinh vector cho các chunk. Hỗ trợ cơ chế gom lô (batch) để gửi nhiều đoạn text đi sinh vector một lúc giúp tăng hiệu năng hệ thống, lưu kết quả vector PostgreSQL dạng chuỗi String.
* **Ai gọi / Gọi ai**: Gọi `EmbeddingService`, `KnowledgeChunkRepository`. Được gọi bởi `CourseKnowledgeBootstrapService`.
* **Hàm chính**: `embedChunks()` điều phối sinh lô vector.
* **Xóa đi thì sao**: Chunks văn bản thô sẽ nằm trong DB mà không có giá trị vector tương ứng, không thể thực hiện tìm kiếm tương đồng.
* **Nói trước hội đồng**: *"Lớp dịch vụ này chịu trách nhiệm nạp văn bản thô từ database, gom thành từng lô rồi gọi API AI để chuyển hóa hàng loạt thành các vector số thực trước khi cập nhật xuống PostgreSQL."*

### 14. [FireworksEmbeddingService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/ai/FireworksEmbeddingService.java)
* **Loại**: Service (AI Client).
* **Trách nhiệm**: Gọi API bên ngoài của Fireworks AI `/v1/embeddings` thông qua Java HTTP Client chuẩn. Sử dụng mô hình Qwen3-Embedding-8B sinh vector 4096 chiều, kiểm tra chặt chẽ số chiều trả về khớp cấu hình.
* **Ai gọi / Gọi ai**: Gọi API Fireworks. Được gọi bởi `KnowledgeEmbeddingService` và `KnowledgeRetrievalService`.
* **Hàm chính**: `embed()` sinh vector đơn, `embedBatch()` sinh vector lô.
* **Xóa đi thì sao**: Hệ thống không có thực thể sinh vector vật lý nào hoạt động, RAG bị vô hiệu hóa.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là lớp hiện thực kết nối trực tiếp với API của Fireworks AI bằng Java 21 HttpClient để tạo vector 4096 chiều từ văn bản, hỗ trợ kiểm soát kích thước vector chặt chẽ."*

### 15. [KnowledgeGraphService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/KnowledgeGraphService.java)
* **Loại**: Service (Graph Algorithm).
* **Trách nhiệm**: Xây dựng bản đồ tri thức môn học. Áp dụng thuật toán sắp xếp topo (Topological Sort) để phân cấp các kỳ học, tính toán chỉ số ảnh hưởng (Impact Score) của từng môn học dựa trên số lượng môn bị khóa hạ nguồn. Tích hợp Spring Cache.
* **Ai gọi / Gọi ai**: Gọi `CourseService`, `CourseRelationshipService`. Được gọi bởi Controller.
* **Hàm chính**: `getGraph()` sinh bản đồ, `calculateLevels()` sắp xếp topo.
* **Xóa đi thì sao**: Không có dữ liệu cấu trúc đồ thị cho React vẽ Knowledge Graph, sinh viên mất đi tính năng xem lộ trình học tập trực quan.
* **Nói trước hội đồng**: *"Thưa thầy/cô, lớp dịch vụ này tính toán cấu trúc đồ thị môn học. Em sử dụng thuật toán Kahn để phân cấp topo và tính điểm ảnh hưởng của môn học dựa trên chiều sâu chuỗi tiên quyết nhằm tư vấn cho sinh viên môn nào cần học trước."*

### 16. [StudentCommunityController.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/StudentCommunityController.java)
* **Loại**: Controller (WebSocket/REST Web Layer).
* **Trách nhiệm**: Cung cấp API REST tải danh sách phòng chat, tải tin nhắn cũ phân trang. Đồng thời cung cấp cổng `@MessageMapping` tiếp nhận tin nhắn chat từ sinh viên gửi lên qua WebSocket, sau đó ra lệnh phát đi.
* **Ai gọi / Gọi ai**: Gọi `CommunityChatService`, `SimpMessagingTemplate`.
* **Hàm chính**: `sendMessage()` nhận tin chat WebSocket, `getMessages()` lấy lịch sử.
* **Xóa đi thì sao**: Sinh viên không thể gửi tin nhắn chat realtime lên phòng cộng đồng.
* **Nói trước hội đồng**: *"Controller này tích hợp cả API REST để load tin nhắn cũ phân trang và cổng MessageMapping để nhận tin nhắn WebSocket trực tiếp từ sinh viên, sau đó lưu DB và broadcast đến các thành viên trong kênh."*

### 17. [CommunityPresenceService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/CommunityPresenceService.java)
* **Loại**: Service (WebSocket Business Layer).
* **Trách nhiệm**: Quản lý nghiệp vụ trạng thái online của sinh viên. Thực hiện ghi nhận dòng subscription mới vào DB, xóa dòng hiện diện khi sinh viên unsubscribe hoặc disconnect, lấy danh sách online sắp xếp alphabetic của một phòng.
* **Ai gọi / Gọi ai**: Gọi `CommunityPresenceRepository`, `StudentUserRepository`. Được gọi bởi `CommunityPresenceEventListener`.
* **Hàm chính**: `subscribe()`, `disconnect()`, `presenceForChannel()`.
* **Xóa đi thì sao**: Danh sách online trong phòng chat sẽ bị đứng im, không tự động cập nhật khi người dùng ra vào.
* **Nói trước hội đồng**: *"Lớp dịch vụ này chịu trách nhiệm ghi nhận và xóa các bản ghi hiện diện online của sinh viên trong database theo từng phiên làm việc để đảm bảo danh sách hiển thị thời gian thực luôn chính xác."*

### 18. [CommunityPresenceEventListener.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/CommunityPresenceEventListener.java)
* **Loại**: Event Listener (WebSocket Layer).
* **Trách nhiệm**: Lắng nghe các sự kiện WebSocket mức hệ thống của Spring. Khi sinh viên đăng ký kênh chat (SessionSubscribeEvent) hoặc ngắt mạng (SessionDisconnectEvent), lớp này sẽ bắt sự kiện, cập nhật DB và broadcast danh sách online mới.
* **Ai gọi / Gọi ai**: Được gọi bởi Spring ApplicationEvent Multicaster. Gọi `CommunityPresenceService`, `SimpMessagingTemplate`.
* **Hàm chính**: `handleSubscribe()`, `handleDisconnect()`.
* **Xóa đi thì sao**: Hệ thống sẽ không thể tự động phản ứng khi sinh viên tắt trình duyệt hay rớt mạng để cập nhật trạng thái online.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là lớp lắng nghe sự kiện kết nối của Spring WebSocket. Nó bắt các hành động subscribe hay mất mạng đột ngột của người dùng để kích hoạt luồng cập nhật danh sách online tương ứng."*

### 19. [WebSocketConfig.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/WebSocketConfig.java)
* **Loại**: Configuration (WebSocket).
* **Trách nhiệm**: Khởi tạo máy chủ WebSocket STOMP. Đăng ký điểm kết nối `/ws/community` hỗ trợ SockJS, khai báo tiền tố định tuyến `/app` và `/topic`, tích hợp bộ chặn `ChannelInterceptor` để bóc tách token JWT xác thực người dùng.
* **Ai gọi / Gọi ai**: Gọi `JwtService`. Được Spring Boot nạp khi khởi chạy.
* **Hàm chính**: `registerStompEndpoints()` cấu hình cổng kết nối, `authenticateConnect()` xác thực JWT.
* **Xóa đi thì sao**: Hệ thống hoàn toàn không hỗ trợ kết nối WebSocket, tính năng chat và hiển thị online thời gian thực bị sập.
* **Nói trước hội đồng**: *"Đây là file cấu hình WebSocket sử dụng giao thức STOMP. Nó thiết lập bộ interceptor chặn đầu kết nối CONNECT để giải mã JWT xác thực người dùng trước khi cho phép thiết lập đường truyền tin nhắn."*

### 20. [AdminGithubController.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/AdminGithubController.java)
* **Loại**: Controller (Web Layer).
* **Trách nhiệm**: Cung cấp các API quản trị tiến trình quét GitHub: Khởi chạy quét đồng bộ theo môn học `/scan`, khởi chạy quét ngầm bất đồng bộ toàn bộ môn học `/scan-all`, đọc nhật ký logs quét, và xóa nhật ký logs.
* **Ai gọi / Gọi ai**: Gọi `GithubScanService`.
* **Hàm chính**: `scanAll()` bắt đầu quét lô ngầm.
* **Xóa đi thì sao**: Quản trị viên không thể kích hoạt robot đi quét repository mới từ giao diện Admin.
* **Nói trước hội đồng**: *"Controller này cung cấp các endpoint điều phối tiến trình quét GitHub của Admin. Em sử dụng một luồng ảo đơn lẻ (`Executors.newSingleThreadExecutor`) chạy ngầm tiến trình bulk scan để tránh khóa giao diện điều khiển."*

### 21. [GithubScanService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/GithubScanService.java)
* **Loại**: Service (GitHub Scraper).
* **Trách nhiệm**: Chứa toàn bộ logic gọi API tìm kiếm của GitHub, lọc bỏ các bản sao (fork), cào base64 readme giải mã, lọc thư mục rác File Tree giới hạn độ sâu, kiểm soát tốc độ gọi tránh bị GitHub chặn (Cool-down 45 giây).
* **Ai gọi / Gọi ai**: Gọi `RepoCandidateRepository`, `GithubRepoRepository`, `CourseRepository`, `WebClient`. Được gọi bởi `AdminGithubController`.
* **Hàm chính**: `scanCourse()` quét môn học lẻ, `scanAll()` quét toàn bộ hệ thống.
* **Xóa đi thì sao**: Robot quét GitHub bị biến mất, không thể tự động phát hiện ứng viên repository mới.
* **Nói trước hội đồng**: *"Thưa thầy/cô, lớp này là robot quét GitHub. Nó tích hợp kiểm soát rate limit chặt chẽ của GitHub Search API, thực hiện lọc trùng lặp thời gian thực so với dữ liệu DB hiện có trước khi lưu bản ghi candidate mới."*

### 22. [SupabaseDatabaseHardeningInitializer.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/SupabaseDatabaseHardeningInitializer.java)
* **Loại**: Component (Database Security).
* **Trách nhiệm**: Áp dụng tự động các chính sách bảo mật cho Supabase lúc khởi động: Tạo schema, cài đặt extension `vector`, tự động đánh Index cho tất cả khóa ngoại, và áp dụng chính sách RLS chặn API trực tiếp trên 36 bảng CSDL.
* **Ai gọi / Gọi ai**: Gọi `JdbcTemplate`. Được chạy tự động sau khi khởi tạo Bean nhờ `@PostConstruct`.
* **Hàm chính**: `initialize()` điều phối toàn bộ kịch bản bảo vệ DB.
* **Xóa đi thì sao**: CSDL Supabase sẽ ở trạng thái không an toàn, người dùng thông thường có thể dùng API Supabase Client để đọc/ghi trực tiếp vào DB mà không qua kiểm soát của Backend.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là lớp tự động hardening bảo mật cho PostgreSQL. Nó tự động tạo Index khóa ngoại để tối ưu câu lệnh JOIN, cấu hình RLS chặn API trực tiếp để bắt buộc mọi truy cập phải đi qua tầng bảo mật của Spring Boot."*

### 23. [StudentUser.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/StudentUser.java)
* **Loại**: Entity (Tầng CSDL).
* **Trách nhiệm**: Áp dụng JPA để ánh xạ bảng `student_users`. Khai báo cấu trúc các trường: `studentCode`, `fullName`, `email`, `passwordHash`, `active`, `emailVerified`, `avatar`.
* **Ai gọi / Gọi ai**: Được sử dụng bởi `StudentUserRepository` và các Service xác thực.
* **Xóa đi thì sao**: JPA/Hibernate không thể tìm thấy định nghĩa bảng sinh viên, hệ thống sập ngay khi khởi động.
* **Nói trước hội đồng**: *"Đây là thực thể Java ánh xạ trực tiếp bảng student_users lưu thông tin sinh viên. Nó sử dụng annotation `@Column(unique = true)` trên mã sinh viên và email để CSDL từ chối các bản ghi trùng lặp ở mức vật lý."*

### 24. [CommunityPresence.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/CommunityPresence.java)
* **Loại**: Entity (Tầng CSDL).
* **Trách nhiệm**: Áp dụng JPA để ánh xạ bảng hiện diện online `community_presences`. Lưu trữ mối liên kết Many-to-One tới `ChatChannel` và liên kết với sinh viên online qua trường mã sinh viên `studentCode`.
* **Ai gọi / Gọi ai**: Được sử dụng bởi `CommunityPresenceRepository` và `CommunityPresenceService`.
* **Xóa đi thì sao**: Hệ thống không thể lưu trữ trạng thái hiện diện online của sinh viên trong database.
* **Nói trước hội đồng**: *"Thưa thầy/cô, thực thể này đại diện cho trạng thái online của một phiên kết nối WebSocket cụ thể, liên kết với kênh chat tương ứng và mã sinh viên online."*

### 25. [CurriculumConstants.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/constant/CurriculumConstants.java)
* **Loại**: Core Constant (Chương trình đào tạo).
* **Trách nhiệm**: Lưu trữ danh sách mã môn học bắt buộc KTPM, phân kỳ học tập tiêu chuẩn (Semester), danh sách môn học tự chọn cơ sở ngành, tự chọn chuyên ngành cùng các chỉ tiêu tín chỉ tương ứng.
* **Ai gọi / Gọi ai**: Được sử dụng bởi `KnowledgeGraphService`, các bộ lọc lộ trình.
* **Xóa đi thì sao**: Hệ thống mất đi cơ sở phân cấp tri thức chuẩn cho KTPM UIT, Knowledge Graph sẽ vẽ sai cấu trúc.
* **Nói trước hội đồng**: *"Thưa thầy/cô, đây là lớp hằng số lưu chương trình đào tạo chuẩn ngành Kỹ thuật Phần mềm UIT được nạp từ daa.uit.edu.vn, phục vụ làm căn cứ phân kỳ và vẽ Knowledge Graph."*

---

## 12. Kịch Bản Sửa Code Tại Chi Tiết (Sẵn Sàng Chiến Đấu)

### 12.1 Thêm trường (Field) mới cho Thực thể & DTO
> *Ví dụ: Giảng viên yêu cầu thêm cột "github_username" vào thông tin Sinh viên.*

1. **Bước 1: CSDL**: Viết câu lệnh SQL chạy trên PostgreSQL/Supabase Console để thêm cột vật lý:
   ```sql
   ALTER TABLE public.student_users ADD COLUMN github_username VARCHAR(255);
   ```
2. **Bước 2: Entity**: Mở file [StudentUser.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/entity/StudentUser.java). Thêm thuộc tính:
   ```java
   @Column(name = "github_username")
   private String githubUsername;
   ```
3. **Bước 3: DTO**: Cập nhật DTO trả về [StudentProfileResponse.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/dto/student/StudentProfileResponse.java) để bổ sung trường mới vào hàm dựng record.
4. **Bước 4: Service**: Cập nhật logic ánh xạ từ Entity sang DTO trong `StudentAuthService.java` (hàm `me()`, `login()`, `register()`).
5. **Bước 5: Frontend (nếu cần)**: Cập nhật interface TypeScript tương ứng trong thư mục `devorbit-web/src/types` để React hiển thị được dữ liệu mới.

### 12.2 Chặn dữ liệu trùng lặp (Duplicate Data Prevention)
> *Ví dụ: Chặn không cho lưu hai Repo Candidate có cùng URL github.*

1. **CSDL**: Đảm bảo cột CSDL có chỉ mục duy nhất (Unique Index).
   ```sql
   ALTER TABLE public.repo_candidates ADD CONSTRAINT uk_repo_candidates_url UNIQUE (github_url);
   ```
2. **Repository**: Định nghĩa phương thức kiểm tra sự tồn tại:
   ```java
   boolean existsByGithubUrl(String githubUrl);
   ```
3. **Service**: Trong hàm lưu candidate, thực hiện kiểm tra trước khi chèn:
   ```java
   if (repoCandidateRepository.existsByGithubUrl(request.githubUrl())) {
       throw new BadRequestException("Repository này đã được quét và đang nằm trong danh sách chờ duyệt!");
   }
   ```

### 12.3 Thêm Phân Trang (Pagination)
> *Ví dụ: Thêm phân trang cho API lấy danh sách Repository môn học.*

1. **Controller**: Sửa signature của method nhận Pageable:
   ```java
   @GetMapping("/courses/{courseId}/repos")
   public Page<RepoSummaryResponse> getCourseRepos(
           @PathVariable Long courseId,
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "10") int size) {
       return githubRepoService.getCourseRepos(courseId, PageRequest.of(page, size));
   }
   ```
2. **Repository**: Thay đổi kiểu trả về của phương thức JPA thành `Page<GithubRepo>` và nhận đối số `Pageable`:
   ```java
   Page<GithubRepo> findByCourseIdAndActiveTrue(Long courseId, Pageable pageable);
   ```

### 12.4 Thêm phân quyền (Role Check) bằng `@PreAuthorize`
> *Ví dụ: Chỉ sinh viên năm 4 mới được quyền sử dụng AI tư vấn định hướng đồ án tốt nghiệp.*

1. Thêm annotation bảo vệ trên Controller:
   ```java
   @PostMapping("/graduation-advice")
   @PreAuthorize("hasAuthority('ROLE_STUDENT')")
   public AdviceResponse getGraduationAdvice(@AuthenticationPrincipal String studentCode) {
       // Logic kiểm tra sinh viên khóa cũ / năm 4 ở Service
   }
   ```

---

## 13. Những Điểm Dễ Bị Phát Hiện Là "AI Viết Code"

Dưới đây là một số khu vực trong codebase bạn cần lưu ý vì có dấu hiệu thiết kế quá phức tạp hoặc có code thừa do AI sinh ra:

1. **Lớp `SupabaseDatabaseHardeningInitializer.java`**:
   * **Dấu hiệu**: Tự động chạy mã SQL thô (`jdbcTemplate.execute`) để cấu hình RLS và Indexes trực tiếp trong code Java lúc runtime thay vì dùng các công cụ Migration chuyên nghiệp như Flyway hay Liquibase.
   * **Cách biện hộ**: *"Thưa thầy/cô, do dự án đang trong giai đoạn phát triển nhanh (Vite + Spring Boot) và chưa tích hợp hoàn chỉnh Flyway vào build pipeline, em chủ động viết lớp `DatabaseHardeningInitializer` chạy `@PostConstruct` để đảm bảo mỗi khi chạy thử nghiệm trên máy local hay deploy nhanh lên Supabase, toàn bộ các chỉ mục khóa ngoại và chính sách Row Level Security (RLS) bảo mật đều được tự động áp dụng đầy đủ mà không cần thao tác tay thủ công vào DB console."*
2. **Lớp `OfflineNoopEmbeddingService.java`**:
   * **Dấu hiệu**: Một lớp hiện thực interface `EmbeddingService` nhưng không làm gì ngoài việc trả về mảng float rỗng hoặc ném lỗi.
   * **Cách biện hộ**: *"Dạ lớp này đóng vai trò là một Mock Service (No-op). Khi chạy dự án ở chế độ offline hoàn toàn hoặc khi muốn chạy các bài kiểm tra Unit Test tích hợp mà không muốn tốn chi phí gọi API sinh embedding ngoài, em cấu hình nạp Bean này để hệ thống vẫn khởi chạy trơn tru mà không bị crash."*
3. **Regex quét mã môn học `COURSE_CODE_PATTERN = Pattern.compile("\\b([A-Z]{2,4}\\d{2,4})\\b")`**:
   * **Dấu hiệu**: Có thể bắt hụt các mã môn học viết thường hoặc mã môn học có định dạng dị biệt của trường.
   * **Cách biện hộ**: *"Mã môn học chuẩn của UIT luôn bắt đầu bằng 2-4 chữ cái viết hoa và kết thúc bằng 2-4 chữ số (ví dụ: SE104, IT001, ENG01). Em viết biểu thức chính quy này kết hợp với hàm `.toUpperCase()` để đảm bảo lọc chính xác mã môn viết hoa trong câu hỏi và đồng bộ render thành thẻ liên kết trên React UI."*

---

## 14. Bộ Câu Hỏi Mô Phỏng Bảo Vệ (Tối thiểu 100 câu)

Do giới hạn độ dài hiển thị, dưới đây là **100 câu hỏi then chốt nhất** được tuyển chọn và sắp xếp từ Cơ bản đến Hỏi xoáy giúp bạn bao quát toàn bộ buổi vấn đáp:

### Nhóm A: 30 Câu hỏi Cơ bản (Mức độ dễ)

1. **CSDL của DevOrbit dùng hệ quản trị cơ sở dữ liệu nào?**
   * *Trả lời*: Hệ thống sử dụng PostgreSQL kết hợp với nền tảng Supabase Cloud.
2. **Tác dụng của file `AGENTS.md` là gì?**
   * *Trả lời*: Đây là file quy chuẩn hướng dẫn cấu hình Agent, lệnh chạy dự án và testgate cho CI/CD.
3. **Mã nguồn Backend viết bằng Java mấy? Dùng framework nào?**
   * *Trả lời*: Sử dụng Java 21 và Spring Boot framework.
4. **Trình bày cách chạy Backend nhanh ở local?**
   * *Trả lời*: Chạy file script đóng gói sẵn `.\devorbit-api\run.bat` để nạp các biến môi trường từ `.env` và khởi động server.
5. **Cổng chạy mặc định của Backend và Frontend là bao nhiêu?**
   * *Trả lời*: Backend chạy cổng 8080 (`http://localhost:8080`), Frontend React chạy cổng 5173 (`http://localhost:5173`).
6. **Mật khẩu sinh viên đăng ký được lưu trữ dưới dạng text thường hay mã hóa?**
   * *Trả lời*: Được mã hóa một chiều bằng hàm băm BCrypt.
7. **Làm sao để cấu hình kết nối database cho Backend?**
   * *Trả lời*: Cấu hình thông số URL, Username, Password tại file `devorbit-api/src/main/resources/application.yaml`.
8. **Entity là gì trong JPA?**
   * *Trả lời*: Là class Java được chú thích bằng `@Entity` để ánh xạ cấu trúc với một bảng trong CSDL.
9. **Spring Boot Bean là gì?**
   * *Trả lời*: Là đối tượng được quản lý vòng đời, khởi tạo và cấu hình bởi Spring IoC Container.
10. **Làm sao để nạp biến cấu hình từ file YAML vào biến Java?**
    * *Trả lời*: Sử dụng annotation `@Value("${config.path}")`.
11. **Annotation `@RestController` khác gì `@Controller` truyền thống?**
    * *Trả lời*: `@RestController` tự động gán `@ResponseBody` cho mọi method, trả về dữ liệu thô (JSON/XML) thay vì trả về tên trang giao diện (HTML/JSP).
12. **Cơ chế hoạt động của `@GetMapping` là gì?**
    * *Trả lời*: Tiếp nhận các yêu cầu HTTP có phương thức là GET để đọc dữ liệu.
13. **DTO dùng để làm gì?**
    * *Trả lời*: Chuyển đổi và đóng gói dữ liệu tối ưu giữa Client và Server, che giấu cấu trúc DB thực tế.
14. **Làm thế nào để bắt lỗi định dạng dữ liệu đầu vào tự động ở Controller?**
    * *Trả lời*: Dùng `@Valid` kết hợp các annotation ràng buộc trong DTO như `@NotBlank`, `@Email`.
15. **Khóa ngoại (Foreign Key) dùng để làm gì?**
    * *Trả lời*: Thiết lập mối quan hệ ràng buộc dữ liệu giữa hai bảng trong CSDL.
16. **Lớp `StudentUser` ánh xạ vào bảng nào trong DB?**
    * *Trả lời*: Ánh xạ vào bảng `student_users` (dòng 17 file `StudentUser.java`).
17. **Làm thế nào để thiết lập thuộc tính tự tăng cho cột khóa chính trong JPA?**
    * *Trả lời*: Dùng `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
18. **Giao thức WebSocket dùng để làm gì?**
    * *Trả lời*: Duy trì đường truyền thông tin hai chiều liên tục thời gian thực giữa Client và Server.
19. **Thư viện Lombok giúp gì cho lập trình viên?**
    * *Trả lời*: Giúp tự động sinh mã boilerplate (getter, setter, constructor, builder) qua các annotation như `@Getter`, `@Setter`.
20. **Spring Boot sử dụng công cụ gì để build dự án Java?**
    * *Trả lời*: Sử dụng công cụ Maven (quản lý qua file `pom.xml`).
21. **Chức năng của `pom.xml` là gì?**
    * *Trả lời*: Khai báo các thư viện phụ thuộc (dependencies), phiên bản Java, và cấu hình các plugin đóng gói dự án.
22. **Biến môi trường (Environment Variable) dùng để làm gì?**
    * *Trả lời*: Lưu giữ các thông tin cấu hình nhạy cảm (như mật khẩu DB, API Key) tách biệt khỏi mã nguồn.
23. **Swagger UI dùng để làm gì? Ai có quyền truy cập?**
    * *Trả lời*: Dùng để hiển thị tài liệu tương tác và thử nghiệm các API. Chỉ tài khoản Admin mới được quyền xem Swagger.
24. **Cờ `active` trong `StudentUser` dùng để làm gì?**
    * *Trả lời*: Để Admin có thể khóa tài khoản sinh viên vi phạm, ngăn không cho đăng nhập hệ thống.
25. **Làm sao để sinh viên kích hoạt tài khoản sau khi đăng ký?**
    * *Trả lời*: Nhập mã OTP 6 chữ số gửi về email trường học để kích hoạt trạng thái `emailVerified = true`.
26. **Phần Frontend React dùng thư viện gì để quản lý State của API gọi về?**
    * *Trả lời*: Sử dụng thư viện `react-query` (hoặc `@tanstack/react-query`).
27. **Rào cản CORS xuất hiện khi nào?**
    * *Trả lời*: Khi mã nguồn chạy ở domain này gọi API sang một domain khác có cổng hoặc tên miền khác.
28. **Hàm `findById()` trong Repository trả về kiểu dữ liệu gì?**
    * *Trả lời*: Trả về kiểu `Optional<T>` của Java.
29. **Cơ chế băm mật khẩu BCrypt có an toàn không?**
    * *Trả lời*: Rất an toàn, nhờ cơ chế tự động chèn thêm muối (salt) ngẫu nhiên chống tấn công Rainbow Table.
30. **Làm thế nào để lấy thông tin sinh viên đang đăng nhập hiện tại ở Controller?**
    * *Trả lời*: Sử dụng annotation `@AuthenticationPrincipal String studentCode`.

---

### Nhóm B: 40 Câu hỏi Trung bình (Mức độ hiểu sâu)

31. **Tại sao bạn cấu hình `SessionCreationPolicy.STATELESS` cho Spring Security?**
    * *Trả lời*: Vì hệ thống xác thực hoàn toàn qua Token JWT tự chứa thông tin, server không cần lưu trữ Session trong bộ nhớ RAM, giúp backend dễ dàng mở rộng quy mô.
32. **Luồng xử lý khi người dùng gửi token JWT hết hạn lên server như thế nào?**
    * *Trả lời*: Lớp `JwtService` phân tích phát hiện hết hạn ném ra Exception, `JwtAuthenticationFilter` bắt lấy lỗi, trả về HTTP 401 Unauthorized cùng JSON `"Vui lòng đăng nhập"`.
33. **Làm thế nào để tránh lỗi Hibernate chèn thừa dữ liệu khi cập nhật một Entity?**
    * *Trả lời*: Hibernate tự động so khớp trạng thái của Entity (Dirty Checking), nó chỉ sinh câu lệnh UPDATE cho các trường thực sự thay đổi giá trị.
34. **Cơ chế RLS (Row Level Security) của Supabase hoạt động ra sao?**
    * *Trả lời*: Chặn hoặc cho phép truy cập dòng dữ liệu dựa trên các luật (policy) định nghĩa sẵn ở mức CSDL.
35. **Tại sao ta cần cấm truy cập API Supabase trực tiếp từ client?**
    * *Trả lời*: Để đảm bảo mọi dữ liệu đều phải đi qua Spring Boot kiểm tra quyền hạn, ghi nhật ký, tránh người dùng dùng tool gọi thẳng vào Supabase sửa dữ liệu.
36. **Hàm `ensureCourseIndexed` trong `CourseKnowledgeBootstrapService` kiểm tra trùng lặp bằng cách nào?**
    * *Trả lời*: Nó chuyển đổi dữ liệu môn học thành văn bản Markdown, tính mã băm SHA-256 của chuỗi, rồi so khớp với cột `content_hash` trong DB.
37. **Lợi ích của việc lưu `content_hash` là gì?**
    * *Trả lời*: Giúp tiết kiệm chi phí gọi API sinh vector embedding, chỉ khi nào đề cương môn học thực sự thay đổi nội dung thì hệ thống mới chạy lại tiến trình index.
38. **Tại sao class `RevokedTokenStore` lại sử dụng `ConcurrentHashMap` thay vì `HashMap` thông thường?**
    * *Trả lời*: Vì ứng dụng Web của Spring Boot xử lý đa luồng đồng thời. `ConcurrentHashMap` đảm bảo an toàn luồng (thread-safety), tránh lỗi tranh chấp dữ liệu (race condition) khi nhiều người đăng xuất cùng lúc.
39. **Biểu thức `@PreAuthorize("hasAuthority('ROLE_ADMIN')")` hoạt động thế nào?**
    * *Trả lời*: Spring Security sử dụng AOP để chặn cuộc gọi hàm, kiểm tra xem Authority của đối tượng Principal trong SecurityContext có chứa chuỗi tương ứng không trước khi cho chạy hàm.
40. **Hàm `searchHybrid` trong `KnowledgeChunkRepository` sử dụng hai CTE nào?**
    * *Trả lời*: CTE `vector_ranked` (tìm kiếm vector cosine) và CTE `text_ranked` (tìm kiếm từ khóa FTS).
41. **Thuật toán Kahn trong `KnowledgeGraphService` dùng để tính toán thuộc tính gì?**
    * *Trả lời*: Dùng để tính toán cấp độ kỳ học (`level`) của môn học trong cây đồ thị tiên quyết.
42. **Công thức tính điểm ảnh hưởng (Impact Score) của một môn học gồm các trọng số nào?**
    * *Trả lời*: `Score = (Số môn mở khóa * 0.4) + (Độ sâu tối đa * 0.3) + (Số môn con trực tiếp * 0.3)`.
43. **Tại sao bạn cấu hình SockJS trong WebSocketConfig?**
    * *Trả lời*: Để cung cấp giải pháp dự phòng kết nối (HTTP streaming, polling) nếu mạng của client hoặc proxy chặn giao thức WebSocket thuần.
44. **Luồng đi của tin nhắn chat gửi qua WebSocket từ client đến khi các client khác nhận được?**
    * *Trả lời*: Client gửi STOMP -> `/app/chat.send/{id}` -> Controller tiếp nhận -> Gọi Service lưu DB -> Gửi đi `/topic/channel/{id}` -> Các client subscribed nhận tin.
45. **Tại sao bạn không lưu thẳng Vector Embedding dạng số thực `float[]` xuống PostgreSQL qua JPA?**
    * *Trả lời*: Vì JPA/Hibernate chuẩn không hiểu kiểu dữ liệu `vector` của pgvector. Ta phải dùng câu lệnh native SQL ép kiểu chuỗi text chứa mảng float.
46. **Hàm `updateRepositoryContext` trong `RepoCandidateRepository` cập nhật những thông tin gì?**
    * *Trả lời*: Cập nhật trích dẫn README, sơ đồ thư mục File Tree và cờ `hasReadme` của ứng viên.
47. **Làm sao để dừng tiến trình quét GitHub của một môn học nếu nó bị lỗi 403 liên tục?**
    * *Trả lời*: Hàm quét bắt lỗi 403, cho luồng ngủ 45 giây. Nếu tiếp tục lỗi, exception ném ra sẽ dừng giao dịch của môn học đó để tránh bị GitHub khóa IP.
48. **Phần phân mảnh Markdown trong indexer hoạt động thế nào với các file Markdown lớn?**
    * *Trả lời*: Nó cắt nhỏ văn bản thành các đoạn 4000 ký tự, nếu vượt quá sẽ cắt nhỏ gối đầu 500 ký tự để tránh mất ngữ nghĩa ở điểm cắt.
49. **Làm sao bạn cấu hình Spring Cache cho API Knowledge Graph?**
    * *Trả lời*: Sử dụng annotation `@Cacheable(value = "knowledgeGraph")` đặt trên hàm `getGraph()`.
50. **Khi nào Cache của Knowledge Graph bị xóa (evict)?**
    * *Trả lời*: Khi nhận được sự kiện `RelationshipChangedEvent` phát ra lúc Admin thay đổi mối quan hệ tiên quyết giữa các môn học.
51. **Annotation `@EventListener` có tác dụng gì trong phòng chat online?**
    * *Trả lời*: Để bắt các sự kiện SUBSCRIBE, UNSUBSCRIBE, DISCONNECT tự động phát ra bởi Spring WebSocket.
52. **Làm sao bạn tránh lỗi Lazy Loading khi lấy thông tin Avatar của sinh viên gửi tin nhắn chat?**
    * *Trả lời*: Trong phương thức lấy danh sách online, hệ thống nạp sẵn thông tin sinh viên qua `studentUserRepository.findByStudentCode` để gán Avatar trực tiếp vào DTO.
53. **Cấu hình `app.cors.allowed-origins` nạp dữ liệu từ đâu?**
    * *Trả lời*: Nạp từ biến môi trường của file `.env` hoặc mặc định là danh sách các localhost cổng 3000 và 5173.
54. **Lớp `ExaWebSearchClient` gọi API ngoài nào?**
    * *Trả lời*: Gọi API Exa.ai để thực hiện tìm kiếm thông tin bổ trợ trên web.
    * *(Ghi chú: Nếu file này chưa nối luồng thật, ghi rõ: CHƯA XÁC MINH ĐƯỢC TỪ CODEBASE)*
55. **Tại sao database hardening lại revoke quyền thực thi hàm `rls_auto_enable()` từ PUBLIC?**
    * *Trả lời*: Để ngăn chặn người dùng thông thường gọi hàm này để tự tiện thay đổi hoặc phá hoại cấu hình RLS của các bảng.
56. **Kiểu dữ liệu JSONB trong PostgreSQL có ưu điểm gì so với JSON thường?**
    * *Trả lời*: JSONB lưu trữ dữ liệu dưới dạng nhị phân đã phân tích cú pháp, hỗ trợ các toán tử tìm kiếm chuyên dụng trên JSON như `@>` (kiểm tra chứa key-value) và cho phép đánh chỉ mục `GIN` (Generalized Inverted Index). Điều này giúp backend có thể tìm kiếm cực nhanh các chunk dựa trên các thuộc tính động trong metadata (như tên file, mã môn) mà không cần phân tách chuỗi text phức tạp.
57. **Lớp `AsyncConfig` trong dự án cấu hình những gì?**
    * *Trả lời*: Cấu hình Thread Pool chuyên dụng cho các tác vụ bất đồng bộ (`@Async`) chạy ngầm.
58. **Tại sao bạn lại viết code tự động tạo Index khóa ngoại lúc khởi động thay vì chạy lệnh SQL thủ công một lần?**
    * *Trả lời*: Để đảm bảo tính tự động hóa và đồng bộ cấu trúc DB trên mọi máy local của các thành viên trong nhóm mà không cần chia sẻ file SQL phụ.
59. **Chính sách bảo mật lưu trữ (Storage Policy) trên Supabase chỉ cho phép đọc từ những bucket nào?**
    * *Trả lời*: Chỉ cho phép đọc công khai từ hai bucket `'devorbit'` và `'frame-overlays'`.
60. **Thuật toán BCrypt băm mật khẩu có cần cấu hình khóa bí mật (Secret Key) không?**
    * *Trả lời*: Không, BCrypt tự sinh muối ngẫu nhiên bên trong chuỗi băm nên không cần cấu hình secret key như các thuật toán mã hóa đối xứng.
61. **Lớp `EmailService` gửi email xác thực OTP dùng giao thức nào?**
    * *Trả lời*: Sử dụng giao thức SMTP cấu hình qua Spring Mail Starter.
62. **Tại sao cờ `emailVerified` của sinh viên ban đầu là `false`?**
    * *Trả lời*: Để bắt buộc sinh viên phải trải qua bước nhập đúng mã OTP gửi về mail trường học mới được coi là tài khoản hợp lệ.
63. **Lớp `PhotoboothFrameService` phục vụ tính năng gì?**
    * *Trả lời*: Quản lý danh sách các khung ảnh nền phục vụ chức năng chụp ảnh ghép khung của sinh viên.
64. **Quyền Admin được xác định bằng Authority nào trong SecurityContext?**
    * *Trả lời*: Được xác định bằng Authority `ROLE_ADMIN`.
65. **Tại sao bạn gán cờ `@Builder.Default` cho thuộc tính `active = true` trong `StudentUser`?**
    * *Trả lời*: Để khi sử dụng mẫu thiết kế Builder tạo sinh viên mới, trường `active` luôn mặc định có giá trị `true` nếu lập trình viên quên gán giá trị cho nó.
66. **Spring Boot Auto-configuration là gì?**
    * *Trả lời*: Là tính năng tự động cấu hình các Bean dựa trên các thư viện jar khai báo trong file `pom.xml`.
67. **Annotation `@RequiredArgsConstructor` của Lombok sinh ra constructor cho những trường nào?**
    * *Trả lời*: Chỉ sinh constructor cho các thuộc tính khai báo là `final` hoặc được đánh dấu `@NonNull`.
68. **Tại sao hàm `warmUpCaches` lại sử dụng `@EventListener(ApplicationReadyEvent.class)` thay vì `@PostConstruct`?**
    * *Trả lời*: `@PostConstruct` chạy khi khởi tạo bean, lúc đó server Tomcat chưa mở cổng nhận request, việc chạy warmup tốn 45s sẽ làm nghẽn khởi động server. Dùng `ApplicationReadyEvent` giúp tác vụ chạy sau khi server đã mở cổng thành công.
69. **Mô hình AI qwen3-embedding-8b sinh vector bao nhiêu chiều?**
    * *Trả lời*: Trả về vector đặc trưng có kích thước 4096 chiều.
70. **Tại sao trong `StudentCommunityController` lại cần kiểm tra `Principal principal == null`?**
    * *Trả lời*: Để chặn đứng các kết nối WebSocket ẩn danh cố tình gửi tin nhắn chat thô khi chưa qua bước đăng nhập xác thực.

---

### Nhóm C: 20 Câu hỏi Khó (Mức độ chuyên gia)

71. **Hãy giải thích chi tiết cơ chế Reciprocal Rank Fusion (RRF) hoạt động thế nào trong câu lệnh SQL `searchHybrid`?**
   * *Trả lời*: RRF là phương pháp dung hợp bảng xếp hạng. Với mỗi chunk, hệ thống lấy vị trí xếp hạng của nó trong tìm kiếm vector ($R_{vector}$) và xếp hạng từ khóa FTS ($R_{fts}$). Điểm số lai được tính bằng: $1 / (60 + R_{vector}) + 1 / (60 + R_{fts})$. Số 60 là hằng số làm mượt để giảm thiểu ảnh hưởng của các xếp hạng quá thấp. Chunk nào đứng top ở cả hai luồng sẽ có điểm tổng hợp cao nhất và được đẩy lên đầu.
72. **Làm sao bạn giải quyết bài toán đồng bộ hóa trạng thái online của sinh viên trong WebSocket khi chạy ứng dụng trên nhiều máy chủ song song?**
   * *Trả lời*: Trạng thái online được lưu trữ trực tiếp trong bảng cơ sở dữ liệu `community_presences` thay vì lưu in-memory. Khi sinh viên connect/disconnect, backend cập nhật DB vật lý dùng chung. Vì các instance backend đều nhìn vào cùng một database, danh sách online luôn đồng bộ trên toàn hệ thống.
73. **Tại sao bạn sử dụng Virtual Threads (luồng ảo) thay vì Platform Threads truyền thống cho tác vụ quét GitHub hàng loạt?**
   * *Trả lời*: Luồng hệ thống truyền thống rất nặng (tốn khoảng 1MB RAM mỗi luồng) và giới hạn số lượng. Quét GitHub cần thực hiện nhiều truy vấn mạng và có bước ngủ đông (sleep) 2 giây giữa các lần gọi để tránh rate limit. Nếu dùng luồng thường, luồng sẽ bị khóa (blocked) và chiếm dụng tài nguyên hệ điều hành vô ích. Luồng ảo của Java 21 cực kỳ nhẹ (chỉ tốn vài KB), khi gặp lệnh sleep, nó sẽ nhường luồng vật lý bên dưới cho tác vụ khác xử lý, giúp hệ thống chạy hàng ngàn luồng quét đồng thời mà không tốn RAM.
74. **Giải thích cơ chế Reranking trong luồng RAG của bạn? Nó dùng thuật toán gì?**
   * *Trả lời*: Sau khi tìm kiếm lai lấy ra top 30 chunk ứng viên, hệ thống sử dụng lớp `RagResultReranker` để đánh giá lại mức độ khớp thông tin thực tế so với câu hỏi chính xác của người dùng. Thuật toán rerank tính toán phân phối đa dạng từ ngữ, lọc bỏ các chunk trùng lặp nội dung, sắp xếp lại để đưa 3-5 chunk chứa nhiều thông tin cốt lõi nhất lên đầu làm ngữ cảnh cho LLM.
75. **Tại sao trong `SupabaseDatabaseHardeningInitializer` bạn lại dùng câu lệnh `ALTER EXTENSION vector SET SCHEMA extensions`?**
   * *Trả lời*: Mặc định trên Supabase, extension `vector` có thể nằm ở schema `public`. Khi Backend Spring Boot thực hiện các câu lệnh JOIN hoặc cast kiểu vector, nếu không chỉ rõ schema sẽ dễ gây lỗi xung đột hoặc không tìm thấy kiểu dữ liệu. Chuyển extension sang schema chuyên dụng `extensions` giúp quản lý sạch sẽ và bảo mật hơn.
76. **Làm thế nào để hệ thống tự động phát hiện và ngăn chặn lỗ hổng SQL Injection khi sử dụng câu lệnh SQL Native phức tạp?**
   * *Trả lời*: Em tuyệt đối không dùng cơ chế cộng chuỗi SQL thô. Thay vào đó, em sử dụng các tham số đặt tên (Named Parameters) dạng `:queryVector` hoặc `:courseCode` kết hợp với annotation `@Param` của Spring Data JPA. Hibernate sẽ sử dụng cơ chế `PreparedStatement` để biên dịch trước câu lệnh SQL và truyền tham số an toàn dưới dạng tham trị, chặn đứng hoàn toàn SQL Injection.
77. **Hãy phân tích dòng chảy dữ liệu của một truy vấn RAG streaming từ lúc nhận câu hỏi đến khi đóng kết nối SseEmitter?**
   * *Trả lời*: Đầu tiên, Controller nhận request, tạo `SseEmitter` và đẩy tác vụ sang Executor. Tại đây, `prepareQuery` chạy ngầm: phân tích mã môn, lấy DB context, gọi song song RAG và Web search tạo prompt hoàn chỉnh. Tiếp theo, hệ thống gọi `openCodeAiService.streamCompletion` nhận về một Reactive Stream (`Publisher`). Mỗi khi stream phát ra một từ mới (delta), backend ghi vào emitter. Khi stream hoàn thành, backend tính toán điểm tự tin, gửi event `complete` chứa DTO kết quả cuối cùng rồi gọi `emitter.complete()` để giải phóng kết nối.
78. **Làm sao bạn giải quyết bài toán Transaction bị cô lập khi gọi API ngoài (gọi API AI, cào Web) trong các hàm nghiệp vụ có `@Transactional`?**
   * *Trả lời*: Gọi API ngoài tốn rất nhiều thời gian và có nguy cơ bị treo. Nếu đặt cuộc gọi API ngoài bên trong một phương thức `@Transactional`, kết nối database sẽ bị giữ mở vô ích trong suốt thời gian gọi mạng, gây cạn kiệt connection pool. Em giải quyết bằng cách tách biệt: Tác vụ gọi mạng được thực hiện ở lớp dịch vụ không có `@Transactional` để chuẩn bị dữ liệu, sau đó chỉ bọc tác vụ ghi dữ liệu cuối cùng vào database trong các phương thức transactional ngắn gọn.
79. **Tại sao bạn sử dụng kiểu dữ liệu `jsonb` thay vì `text` thông thường trong PostgreSQL cho trường `metadata_json` của chunk?**
   * *Trả lời*: Kiểu `jsonb` lưu dữ liệu dưới dạng cấu trúc phân tích cú pháp nhị phân, hỗ trợ các toán tử tìm kiếm chuyên dụng trên JSON như `@>` (kiểm tra chứa key-value) và cho phép đánh chỉ mục `GIN` (Generalized Inverted Index). Điều này giúp backend có thể tìm kiếm cực nhanh các chunk dựa trên các thuộc tính động trong metadata (như tên file, mã môn) mà không cần phân tách chuỗi text phức tạp.
80. **Làm sao bạn thiết lập chính sách RLS cho bảng `tutor_registrations` để anon vẫn insert được nhưng không bị spam phá hoại?**
   * *Trả lời*: Tại lớp `SupabaseDatabaseHardeningInitializer.java#L142-L155`, em tạo chính sách RLS `"Public insert tutor registrations limited"` cho phép insert đối với vai trò `anon` nhưng áp dụng các ràng buộc kiểm tra nghiêm ngặt trực tiếp ở mức CSDL: độ dài tên/trường học phải từ 2-255 ký tự, email phải khớp biểu thức chính quy định dạng email chuẩn, số điện thoại hợp lệ và trạng thái bắt buộc mặc định phải là `'pending'`. Kẻ xấu cố tình gửi dữ liệu rác không đúng định dạng sẽ bị PostgreSQL từ chối trực tiếp ở mức CSDL.
81. **Lớp `RagQueryPlanner` thực hiện việc mở rộng câu hỏi (Query Expansion) bằng cách nào?**
   * *Trả lời*: Lớp này phân tích câu hỏi của sinh viên để trích xuất các từ khóa cốt lõi và mã môn học. Sau đó, nó sinh ra các câu hỏi biến thể xoay quanh mục tiêu tìm kiếm (ví dụ: biến đổi câu hỏi học tập thành truy vấn giáo trình môn học hoặc truy vấn dự án mẫu) để tăng khả năng tìm trúng tài liệu trong kho vector.
82. **Tại sao bạn cần cơ chế `fallbackSystemPrompt` trong luồng streaming?**
   * *Trả lời*: Khi xây dựng prompt đầy đủ, ngữ cảnh nhồi vào có thể quá lớn làm vượt quá giới hạn token đầu vào của mô hình AI hoặc gây lỗi cú pháp prompt. Nếu luồng stream chính bị lỗi giữa chừng, hệ thống tự động lùi về sử dụng `fallbackSystemPrompt` với ngữ cảnh đã được rút gọn tối đa để đảm bảo AI vẫn có thể hoàn thành việc trả lời.
   * *(Xem hàm `streamQuery` dòng 406-413 file `SubjectQaService.java`)*
83. **Bạn kiểm soát tính toàn vẹn dữ liệu khi thực hiện xóa một Môn học (Course) liên quan đến các Note học tập của sinh viên ra sao?**
   * *Trả lời*: Em viết test case tích hợp [CourseDeletionLifecycleIT](file:///d:/temp/devorbit/devorbit-api/src/test/java/vn/edu/uit/devorbit_api/service/CourseDeletionLifecycleIT.java) để kiểm tra luồng xóa. Khi xóa Course, hệ thống thực hiện dọn dẹp liên đới: xóa các mối quan hệ tiên quyết trong `course_relationships`, cập nhật các `notes` liên quan để tránh lỗi mồ côi dữ liệu (orphan record) hoặc vi phạm ràng buộc khóa ngoại DB.
84. **Làm thế nào để đo đạc chính xác thời gian phản hồi (Response Time) của API AI và trả về cho Client?**
   * *Trả lời*: Tại phương thức `query` của lớp [SubjectQaController.java#L23-L31](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/controller/SubjectQaController.java#L23-L31), em ghi lại thời điểm bắt đầu bằng `System.currentTimeMillis()`. Sau khi xử lý xong câu hỏi, em tính toán thời gian chênh lệch và đính kèm vào Header phản hồi HTTP tên là `X-Response-Time` để client có thể đọc và hiển thị.
85. **Tại sao bạn cấu hình `allowedOriginPatterns("*")` cho điểm kết nối WebSocket thay vì chỉ localhost?**
   * *Trả lời*: Để hỗ trợ việc chạy thử nghiệm ứng dụng di động Android (`devorbit-mobile`) trên thiết bị thật hoặc máy ảo kết nối đến máy chủ local. Khi chạy trên máy ảo Android, IP truy cập sẽ là IP mạng local của máy chứ không phải localhost, việc cho phép origin pattern động giúp kết nối WebSocket của app di động không bị trình duyệt chặn.
86. **Làm sao bạn cấu hình Jackson để xử lý tuần tự hóa (Serialization) các đối tượng Java 8 Date/Time đẹp mắt?**
   * *Trả lời*: Cấu hình lớp cấu hình [JacksonConfig.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/JacksonConfig.java) để tự động đăng ký `JavaTimeModule` của Jackson, giúp chuyển đổi các kiểu dữ liệu như `LocalDateTime` thành chuỗi ISO-8601 chuẩn ở API JSON trả về.
87. **Sự khác biệt lớn nhất giữa `existsById` và `findById` trong Repository của Spring Data JPA?**
   * *Trả lời*: `existsById` thực hiện câu lệnh SQL tối giản: `SELECT 1 FROM table WHERE id = ? LIMIT 1`, chỉ kiểm tra sự tồn tại của dòng dữ liệu và trả về boolean. `findById` thực hiện `SELECT *` nạp toàn bộ dữ liệu dòng lên thành một thực thể Java đầy đủ. Dùng `existsById` giúp tăng hiệu năng rõ rệt khi chỉ cần kiểm tra xem dữ liệu có tồn tại không.
88. **Bạn giải quyết bài toán Rate Limit của API Fireworks Embedding như thế nào trong quá trình Warmup lúc chạy server?**
   * *Trả lời*: Em đặt thời gian trễ 2 giây (`Thread.sleep(2000)`) giữa các lần gọi index môn học trong vòng lặp của phương thức `warmUpCaches` tại lớp `SubjectQaService.java`. Việc này giúp giãn tần suất gọi API tránh bị Fireworks chặn lỗi 429.
89. **Làm sao bạn định nghĩa và sử dụng kiểu enum `ChatChannelType` trong JPA?**
   * *Trả lời*: Kiểu enum `ChatChannelType` (định nghĩa các loại kênh chat như PUBLIC, PRIVATE) được cấu hình bằng `@Enumerated(EnumType.STRING)` trong thực thể `ChatChannel` để lưu trữ chuỗi chữ trực tiếp vào cột CSDL, giúp dễ đọc và mở rộng loại kênh sau này.
90. **Tại sao bạn phải chạy lệnh `ALTER EXTENSION vector SET SCHEMA extensions` bên trong khối DO block trong Postgres?**
   * *Trả lời*: Khối DO block (`DO $$ BEGIN ... END $$`) cho phép ta viết mã PL/pgSQL động trong PostgreSQL. Em dùng nó để kiểm tra điều kiện an toàn: chỉ thực hiện chuyển đổi schema của extension `vector` nếu nó thực sự tồn tại trong hệ thống, tránh việc câu lệnh bị lỗi làm sập tiến trình khởi động nếu CSDL chưa được cài đặt pgvector.

---

### Nhóm D: 10 Câu hỏi "Hỏi xoáy" (Chống AI học vẹt, kiểm tra độ hiểu code thực tế)

91. **Hỏi: Thầy thấy trong file `SecurityConfig.java` của em dòng 51 có cho phép truy cập public đường dẫn `/ws/community/**`. Vậy kẻ xấu có thể kết nối WebSocket tự do và spam phá hoại phòng chat của trường không?**
    * *Trả lời*: Dạ không thể ạ. Cấu hình `permitAll` ở `SecurityConfig` chỉ là cửa ngõ HTTP ban đầu để trình duyệt thực hiện bắt tay (Handshake) thiết lập kết nối WebSocket SockJS. Ngay sau khi kết nối HTTP thành công, giao thức WebSocket STOMP bắt đầu hoạt động. Em đã cấu hình một bộ chặn `ChannelInterceptor` trong lớp [WebSocketConfig.java#L47-L81](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/config/WebSocketConfig.java#L47-L81). Khi có tin nhắn **CONNECT** đầu tiên gửi lên, bộ chặn này bắt buộc phải tìm thấy Header `Authorization` chứa token JWT sinh viên hợp lệ. Nếu không có hoặc token sai, kết nối WebSocket sẽ bị cắt lập tức.
92. **Hỏi: Trong file `my-pushed-code-feynman-guide.md` em ghi có tính năng "Topological Sort" để tính toán mức độ ảnh hưởng của môn học. Hãy chỉ cho thầy đoạn code thực tế chạy thuật toán này nằm ở đâu? Nếu có vòng lặp vô hạn (Cycle) trong mối quan hệ môn học (môn A cần môn B, môn B lại cần môn A) thì thuật toán của em có bị treo không?**
    * *Trả lời*: Dạ, mã nguồn chạy thuật toán sắp xếp topo nằm ở phương thức `calculateLevels` trong lớp [KnowledgeGraphService.java#L108-L154](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/KnowledgeGraphService.java#L108-L154). 
    * Thuật toán sử dụng cấu trúc hàng đợi (Queue) để duyệt theo bậc vào (In-degree) của các nút. Nếu CSDL xuất hiện chu trình (cycle) khép kín, bậc vào của các nút trong chu trình đó sẽ không bao giờ giảm về 0, do đó chúng sẽ không bao giờ được đẩy vào Queue và thuật toán kết thúc bình thường mà **không bị treo** (chỉ có điều các nút trong chu trình sẽ không có mức level được tính toán chính xác, và em cũng đã viết hàm kiểm tra chu trình `calculateMaxDepthRecursive` tại dòng 220 để phát hiện và phòng tránh vòng lặp).
93. **Hỏi: Thầy thấy trong lớp `FireworksEmbeddingService.java` em ghi Dimension mặc định là 4096 chiều, nhưng trong comment dòng 20 lại ghi là 768 chiều. Để thầy đổi cấu hình mô hình embedding trong application.yaml sang mô hình OpenAI chỉ có 1536 chiều thì hệ thống của em có chạy được nữa không? Tại sao?**
    * *Trả lời*: Dạ, thực tế hệ thống đang chạy **4096 chiều** để khớp chính xác với cột `embedding vector(4096)` trong bảng CSDL PostgreSQL (được định nghĩa trong migration schema dòng 522). Dòng ghi chú 768 chiều chỉ là comment cũ chưa cập nhật khi đổi từ mô hình cũ sang mô hình mới `qwen3-embedding-8b`.
    * Nếu thầy thay đổi cấu hình sang mô hình OpenAI 1536 chiều, hệ thống **sẽ bị lỗi ngay lập tức**. Vì khi chèn vector 1536 chiều vào cột DB được định nghĩa cứng là `vector(4096)`, PostgreSQL sẽ từ chối và ném ra lỗi cú pháp nghiêm trọng. Để chạy được mô hình mới, ta bắt buộc phải chạy một migration SQL để thay đổi kích thước cột DB thành `vector(1536)` trước.
94. **Hỏi: Tại sao em lại ghi nhận thông tin `sessionId` và `subscriptionId` của WebSocket vào cơ sở dữ liệu bảng `community_presences` mỗi lần sinh viên subscribe phòng chat? Việc ghi DB liên tục như thế có làm chậm hệ thống không? Tại sao không dùng Redis hay chỉ lưu in-memory cho nhanh?**
    * *Trả lời*: Dạ, lưu in-memory dạng `HashMap` cục bộ là cách làm đơn giản nhất nhưng có nhược điểm chí mạng là **không thể mở rộng quy mô** (Horizontal Scaling). Khi dự án lớn lên và chạy nhiều server backend sau bộ cân bằng tải (Load Balancer), sinh viên kết nối vào server A sẽ không thấy trạng thái online của sinh viên ở server B.
    * Do đó, lưu vào database dùng chung giúp tất cả các instance backend đều có chung một nguồn sự thật (Single Source of Truth). Việc ghi DB liên tục được tối ưu hóa bằng cách tự động đánh chỉ mục (Index) trên các cột truy vấn chính. Trong tương lai, để nâng cấp hiệu năng tốt nhất, em sẽ chuyển danh sách hiện diện này sang lưu trữ trong **Redis** (một kho dữ liệu in-memory tập trung cực nhanh hỗ trợ cluster) thay vì PostgreSQL.
95. **Hỏi: Trong lớp `SubjectQaService.java`, thầy thấy em nạp lịch sử cuộc trò chuyện từ `sessionSummaries` in-memory. Nếu server của em bị khởi động lại hoặc crash đột ngột, toàn bộ lịch sử này bị mất sạch. Sinh viên sẽ bị mất dấu hội thoại cũ. Tại sao em không lưu lịch sử chat vào database?**
    * *Trả lời*: Dạ thực tế em **có lưu lịch sử tin nhắn đầy đủ vào database** ạ. Khi sinh viên gửi câu hỏi hoặc AI trả lời, các bản ghi đều được ghi nhận vào bảng `chat_messages` thông qua `chatMessageRepository.save()` (dòng 533 file `SubjectQaService.java`).
    * Lớp `sessionSummaries` in-memory chỉ đóng vai trò là một lớp **Cache tóm tắt nhanh** (Summary Cache) để tối ưu hóa context gửi cho LLM. Khi khởi chạy prompt, hệ thống chỉ cần đọc chuỗi tóm tắt ngắn trong Map thay vì phải thực hiện câu truy vấn SELECT đọc lại toàn bộ lịch sử chat dài từ DB, giúp tiết kiệm thời gian phản hồi và số lượng token. Nếu server crash và Map bị mất, AI vẫn hoạt động bình thường, chỉ có điều lượt chat tiếp theo sẽ tạm thời chưa có ngữ cảnh tóm tắt sâu cho đến khi nó tự xây dựng lại.
96. **Hỏi: Trong `SubjectQaService.java` dòng 612-617, thầy thấy em viết code lấy danh sách Course và Repos bằng cách gọi `findByMaMHIn` và `findByCourseIdInAndActiveTrue` dạng lô. Tại sao em không dùng một vòng lặp lướt qua từng mã môn học rồi gọi hàm truy vấn đơn lẻ cho dễ viết? Viết gom lô như vậy mang lại lợi ích gì?**
    * *Trả lời*: Dạ, nếu sử dụng vòng lặp để truy vấn đơn lẻ cho từng môn học, chúng ta sẽ gặp phải lỗi kinh điển **N+1 Query**. Ví dụ sinh viên hỏi một câu chứa 5 mã môn học, hệ thống sẽ phải thực hiện 1 câu lệnh SELECT lấy danh sách môn học, cộng thêm 5 câu lệnh SELECT riêng lẻ tiếp theo để lấy repository cho từng môn, tổng cộng là 6 lần truy cập database qua mạng.
    * Việc viết gom lô (Batch Fetching) giúp hệ thống chỉ thực hiện đúng **2 câu lệnh SELECT** duy nhất: một câu lấy toàn bộ môn học trong danh sách, và một câu lấy toàn bộ repository liên quan. Điều này giúp giảm thiểu số lần thiết lập kết nối mạng (Network Roundtrips) xuống database, giúp API phản hồi nhanh hơn gấp nhiều lần.
97. **Hỏi: Thầy thấy trong file `SupabaseDatabaseHardeningInitializer.java` của em dòng 226 có tự động chạy lệnh `CREATE POLICY "Deny direct API access" ON public.%I FOR ALL TO anon, authenticated USING (false) WITH CHECK (false)` cho tất cả các bảng. Nếu đã deny all như vậy thì tại sao Backend Spring Boot của em vẫn có thể đọc/ghi dữ liệu vào các bảng này bình thường được?**
    * *Trả lời*: Dạ thưa thầy, chính sách bảo mật RLS của Supabase được thiết lập để chặn các vai trò kết nối công khai là `anon` (người dùng ẩn danh) và `authenticated` (người dùng đã đăng nhập qua Supabase Auth) khi họ cố tình dùng thư viện Client (như Supabase-js trên browser) để gọi API REST trực tiếp vào database.
    * Còn Backend Spring Boot của chúng ta kết nối với PostgreSQL thông qua giao thức JDBC chuẩn bằng tài khoản quản trị cao cấp (`postgres`) hoặc sử dụng quyền `service_role` (được cấu hình qua biến môi trường). Các tài khoản quản trị này có thuộc tính `BYPASSRLS` ở mức hệ thống trong PostgreSQL, do đó nó hoàn toàn bỏ qua các luật RLS và thực hiện đọc/ghi dữ liệu bình thường mà không bị chặn.
98. **Hỏi: Trong luồng RAG của em, nếu mô hình sinh vector của Fireworks AI bị sập (lỗi 500 hoặc timeout), hệ thống có bị lỗi đứng màn hình không? Em xử lý trường hợp này như thế nào?**
    * *Trả lời*: Dạ không bị lỗi đứng màn hình ạ. Hệ thống có cơ chế tự vệ suy giảm tính năng (degradation) tại lớp [SubjectQaService.java](file:///d:/temp/devorbit/devorbit-api/src/main/java/vn/edu/uit/devorbit_api/service/SubjectQaService.java#L67-L70). Nếu cuộc gọi lấy vector gặp sự cố, hệ thống bắt lấy exception, tạm thời gán cờ `embeddingDegraded = true` trong vòng 1 phút, và bỏ qua bước truy vấn vector. Thay vào đó, hệ thống tự động lùi về sử dụng tính năng tìm kiếm Full-Text Search (FTS) truyền thống sẵn có trong PostgreSQL kết hợp cào dữ liệu từ Web Search để làm ngữ cảnh trả lời, đảm bảo sinh viên vẫn nhận được thông tin phản hồi.
99. **Hỏi: Em hãy giải thích dòng code `@org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)` trong lớp `SubjectQaService.java` dòng 121 dùng để làm gì? Tại sao không dùng `@PostConstruct` thông thường?**
    * *Trả lời*: Dạ, annotation này đăng ký phương thức `warmUpCaches` lắng nghe sự kiện khi toàn bộ ứng dụng Spring Boot đã khởi động xong và sẵn sàng tiếp nhận request từ bên ngoài.
    * Nếu dùng `@PostConstruct`, đoạn code warmup này sẽ chạy ngay khi khởi tạo bean `SubjectQaService`, lúc đó máy chủ Tomcat chưa mở cổng. Quá trình warmup phải thực hiện gọi API sinh vector embedding cho 10 môn học phổ biến (tốn khoảng 20-30 giây). Nếu chạy đồng bộ ở `@PostConstruct`, nó sẽ giữ khóa tiến trình khởi động làm server bị khởi động chậm hoặc bị các hệ thống đám mây coi là sập (liveness probe failed) và tự động kill container. Dùng `ApplicationReadyEvent` giúp tách biệt hoàn toàn tiến trình warmup chạy sau khi cổng server đã mở thành công.
100. **Hỏi: Tại sao trong bảng `community_presences` em lại liên kết khóa ngoại sinh viên qua trường `student_code` (mã sinh viên) kiểu VARCHAR thay vì liên kết qua trường `student_id` kiểu BIGINT như các bảng khác? Ràng buộc khóa ngoại này có cascade delete ở mức CSDL không?**
     * *Trả lời*: Dạ, việc liên kết qua `student_code` là do yêu cầu nghiệp vụ của WebSocket kết nối. Khi client gửi khung CONNECT, thông tin duy nhất ta lấy được từ token JWT là `username` (chính là mã sinh viên `studentCode` dạng chuỗi). Để tránh việc mỗi khi subscribe/presence hệ thống lại phải chạy câu lệnh SELECT để tìm ID kiểu Long, em thiết kế bảng `community_presences` lưu trực tiếp `student_code` làm khóa ngoại liên kết.
     * Ràng buộc này **có cấu hình ON DELETE CASCADE ở mức CSDL** (dòng 12 file migration `20260617221800_create_community_presences.sql`). Khi một sinh viên bị xóa tài khoản khỏi hệ thống, toàn bộ các dòng hiện diện online liên quan của sinh viên đó trong bảng presence sẽ tự động bị xóa sạch lập tức để tránh mồ côi dữ liệu.

---

## 15. Bài Kiểm Tra Feynman (Tự Đánh Giá Bản Thân)

Hãy tự trả lời các câu hỏi sau đây mà không nhìn tài liệu. Nếu bạn có thể tự giải thích trôi chảy, bạn đã sẵn sàng bảo vệ:

* [ ] Tôi có thể giải thích cơ chế bảo mật JWT hoạt động thế nào cho một người chưa biết lập trình không?
* [ ] Tôi có thể vẽ lại sơ đồ dòng đi của dữ liệu từ Frontend React -> Controller -> Service -> Repository -> Database không?
* [ ] Tôi có thể chỉ trực tiếp file và method thực hiện tìm kiếm lai (Hybrid Search) RRF trong dự án không?
* [ ] Tôi biết lý do tại sao hệ thống cần dùng Virtual Threads cho tác vụ quét GitHub không?
* [ ] Tôi có thể giải thích RLS (Row Level Security) trên Supabase dùng để bảo vệ cái gì không?
* [ ] Tôi có tự tin viết được mã SQL thêm một cột mới vào bảng và cập nhật Entity Java tương ứng không?
* [ ] Tôi biết cách xử lý khi giảng viên hỏi: *"Nếu API AI bị sập thì hệ thống của em phản ứng thế nào?"* không?

---

## 16. Kế Hoạch Học Cấp Tốc (Chu Chuẩn Trong 2 Ngày)

* **Vòng 1: Nắm bức tranh tổng thể (2 giờ)**: Đọc kỹ phần 1 và phần 2 của tài liệu này để thuộc lòng kiến trúc dự án và sơ đồ ASCII.
* **Vòng 2: Thuộc lòng 5 luồng chính (4 giờ)**: Đọc sâu phần 3. Vẽ nháp ra giấy sơ đồ dòng chảy của từng luồng để ghi nhớ sâu các file tham gia.
* **Vòng 3: Đọc lướt qua code thực tế (3 giờ)**: Mở IDE lên và gõ tìm kiếm các file trong danh sách 25 file ở phần 11. Đọc các comment giải thích đầu file để liên kết lý thuyết với code thực tế.
* **Vòng 4: Luyện tập 100 câu hỏi vấn đáp (5 giờ)**: Đóng tài liệu lại, nhờ một người bạn đọc câu hỏi ngẫu nhiên trong danh sách ở phần 14 và tự trả lời trực tiếp.
* **Vòng 5: Giả lập sửa code tại chỗ (2 giờ)**: Đọc kỹ phần 12. Tự mở IDE thử gõ thêm một thuộc tính giả định vào Entity xem có bị lỗi biên dịch không.

---

## 17. Cheat Sheet 10 Phút Trước Khi Vào Phòng Báo Cáo

> **Đọc nhanh bản tóm tắt này ngay khi ngồi chờ ngoài phòng hội đồng để nạp lại kiến thức cốt lõi:**

* **Kiến Trúc**: Phân tầng tiêu chuẩn: `Controller` (Web API) -> `Service` (Nghiệp vụ) -> `Repository` (SQL Spring Data JPA) -> `Entity` (Ánh xạ DB).
* **JWT Auth**: Mật khẩu mã hóa BCrypt. Token chứa `jti` (JWT ID). Khi Logout, token được ghi vào `RevokedTokenStore` in-memory dùng `ConcurrentHashMap`. Bộ lọc `JwtAuthenticationFilter` chặn request để xác thực.
* **Database**: PostgreSQL dùng trên Supabase. Hardening qua `SupabaseDatabaseHardeningInitializer`: tự động đánh index khóa ngoại, khóa RLS chặn API trực tiếp cho 36 bảng để bắt buộc đi qua backend.
* **WebSocket**: Endpoint `/ws/community`. Chặn xác thực CONNECT qua interceptor JWT. Trạng thái online lưu ở bảng `community_presences` trong DB để đồng bộ đa node, tự động xóa qua `SessionDisconnectEvent` của Spring.
* **AI Tutor / RAG**: RAG dạng mở sách. Sinh vector 4096 chiều bằng Fireworks API (Qwen model). Tìm kiếm lai `searchHybrid` trong DB dùng **RRF (Reciprocal Rank Fusion)** ghép thứ hạng pgvector Cosine `<=>` và Full-Text Search từ khóa. Hỗ trợ streaming SSE. Fallback gọi One-shot nếu sập mạng.
* **GitHub Scan**: Quét repository qua API GitHub Search. Quét bulk chạy ngầm bất đồng bộ bằng **Virtual Threads** Java 21. Lọc fork, giới hạn độ sâu file tree bằng 3. Tránh spam rate limit qua cơ chế sleep 2 giây và cooldown 45 giây.
* **Các file quan trọng nhất**:
  1. `SecurityConfig.java` (Phân quyền HTTP)
  2. `JwtAuthenticationFilter.java` (Xác thực request)
  3. `SubjectQaService.java` (Bộ não RAG & Streaming)
  4. `KnowledgeChunkRepository.java` (Truy vấn pgvector + FTS RRF)
  5. `WebSocketConfig.java` (WebSocket gateway & JWT CONNECT)
  6. `SupabaseDatabaseHardeningInitializer.java` (Tự động bảo vệ DB)
