# Milestones: Tích hợp Chat Cộng đồng thời gian thực và các chức năng cộng đồng

Free-form implementation log. Record meaningful phase changes, successful milestones, failed attempts, setbacks, fixes, validation notes, and decisions.

**Current Status:** DRAFT — Execution plan formulated

---

## Các mốc phát triển dự kiến (Milestones)

### Mốc 1: Hạ tầng Database & JPA Entities (Dự kiến)
- [ ] Thiết lập migration script `V006__create_community_features.sql` tạo đầy đủ 5 bảng: `chat_channels`, `community_messages`, `repo_reviews`, `repo_votes`, `course_reviews`.
- [ ] Định nghĩa các Entity Java tương ứng và thiết lập quan hệ JPA `@ManyToOne` thích hợp với `StudentUser`, `Course`, `GithubRepo`.
- [ ] Tạo các interface `Repository` kế thừa `JpaRepository`.

### Mốc 2: Cấu hình WebSocket & Bảo mật STOMP Connection (Dự kiến)
- [ ] Viết `WebSocketConfig` kích hoạt message broker, định nghĩa endpoint `/ws/community` (hỗ trợ SockJS fallback).
- [ ] Viết `ChannelInterceptor` trong WebSocketConfig để đón nhận frame `CONNECT`, giải mã JWT từ header `Authorization`, xác thực thông qua `JwtService` và gắn `Principal` vào session.
- [ ] Cập nhật `SecurityConfig` cho phép tất cả các yêu cầu đến `/ws/community/**` vượt qua HTTP Filter Chain.

### Mốc 3: REST API & WebSocket Message Endpoints (Dự kiến)
- [ ] Viết endpoints lấy danh sách kênh chat (`/api/student/community/channels`) và lịch sử tin nhắn của kênh có phân trang.
- [ ] Viết logic đồng bộ tự động tạo các kênh chat dựa trên Môn học và Tech Stack đang có trong database.
- [ ] Xây dựng REST API cho các tác vụ:
  - Viết/sửa/xóa đánh giá sao và bình luận cho môn học (`/api/student/courses/{courseId}/review`).
  - Viết/sửa/xóa đánh giá sao và bình luận cho repository (`/api/student/repos/{repoId}/review`).
  - Thực hiện Upvote/Downvote repository (`/api/student/repos/{repoId}/vote`).
- [ ] Viết `@MessageMapping` controller đón nhận tin nhắn gửi lên từ WebSocket client, lưu trữ vào cơ sở dữ liệu, và broadcast tin nhắn đến kênh chat.

### Mốc 4: Giao diện Chat Cộng đồng thời gian thực (Dự kiến)
- [ ] Cài đặt gói thư viện `@stomp/stompjs` và `sockjs-client` trên `devorbit-web`.
- [ ] Hoàn thiện `CommunityPage.tsx` với cấu trúc layout 3 cột:
  - Cột 1: Danh sách kênh (General, Môn học, Tech Stack).
  - Cột 2: Nội dung tin nhắn chat cuộn mượt mà + Infinite Scroll khi cuộn lên trên đầu + Ô nhập tin nhắn.
  - Cột 3: Danh sách các thành viên online hiện tại.
- [ ] Thiết lập logic kết nối tự động, gửi JWT Token qua header, đăng ký nhận tin nhắn thời gian thực và tự động scroll xuống dưới khi có tin nhắn mới.

### Mốc 5: Tương tác Đánh giá & Bình chọn trên Web (Dự kiến)
- [ ] Thiết kế và nhúng component viết đánh giá sao & nhận xét vào `CourseDetailPage.tsx`.
- [ ] Thiết kế và tích hợp component đánh giá sao, bình luận cùng cụm Upvote/Downvote trực quan vào `RepoDetailPage.tsx`.
- [ ] Ràng buộc kiểm tra xác thực ở frontend: Nếu là Guest, ẩn ô nhập bình luận/chat/vote và thay thế bằng nút "Đăng nhập để tương tác".
