# PRODUCT.md — Tích hợp Chat Cộng đồng thời gian thực và các chức năng cộng đồng

> **Last Updated:** 2026-06-07
> **Status:** DRAFT — Requirements gathered through GSD Interview (/grill-me)

## 1. Vấn đề & Bối cảnh

DevOrbit hiện tại đã phát triển khá hoàn chỉnh các tính năng cốt lõi cho sinh viên UIT (Course lists, knowledge graphs, learning roadmaps, note editors, and photobooth). Tuy nhiên, nền tảng đang thiếu sự tương tác xã hội và cộng đồng:
- Sinh viên không có nơi trao đổi trực tiếp, thời gian thực về các môn học hay đồ án.
- Không thể đóng góp phản hồi về chất lượng môn học hoặc xếp hạng, đánh giá các legacy repositories đã được duyệt để các khóa sau tham khảo.
- Đã có nền tảng xác thực sinh viên (Student register, email verification qua OTP, student login) nhưng chưa được tích hợp chặt chẽ vào các tính năng xã hội.

## 2. Giải pháp: Không gian Cộng đồng & Tính năng Tương tác

Xây dựng hệ thống **Community Hub** kết hợp:
1. **Chat cộng đồng thời gian thực (Real-time Chat):** Kiến trúc WebSocket/STOMP.
2. **Kênh Chat phân cấp (Channels):** Phân chia theo Kênh chung, Môn học, và Tech Stack.
3. **Bình chọn & Đánh giá Repository:** Đánh giá sao, viết nhận xét, upvote/downvote repo.
4. **Đánh giá & Review Môn học:** Giúp khóa trước để lại kinh nghiệm học tập cho khóa sau.
5. **Ràng buộc Auth chặt chẽ:** Chỉ sinh viên UIT (đã active qua email OTP) mới được tham gia chat/vote/comment.

---

## 3. Chi tiết Tính năng & Trải nghiệm Người dùng

### 3.1. Xác thực & Phân quyền (Auth Control)
- **Tài khoản Khách (Guest):**
  - Chỉ xem danh sách môn học, repo, đánh giá môn học, đánh giá repo công khai.
  - Không được gửi tin nhắn chat, không được viết đánh giá, không được vote.
  - Khi cố gắng tương tác, hiển thị prompt chuyển hướng đến trang Đăng nhập / Đăng ký.
- **Tài khoản Sinh viên (Đã Login & Active):**
  - Toàn quyền tham gia các phòng chat cộng đồng.
  - Được vote (up/down) và review (đánh giá sao + bình luận) các repo và môn học.

### 3.2. Chat Cộng đồng Thời gian thực (Real-time Channels)
- **Kiến trúc:** Dùng WebSockets + STOMP Broker ở backend và StompJS/SockJS ở frontend.
- **Tổ chức Phòng chat (Kênh):**
  - **Kênh chung (Cố định):** Kênh `#general` (Chung), `#study` (Học tập), `#relax` (Giải trí).
  - **Kênh môn học (Course-specific):** Tự động ánh xạ từ bảng `courses`. Khi sinh viên truy cập chi tiết một môn học, có thể nhấn nút "Tham gia chat môn học" để vào phòng tương ứng (ví dụ: `#course-se104` cho Nhập môn CNPM).
  - **Kênh công nghệ (Tech Stack-specific):** Tự động ánh xạ từ bảng `tech_stacks` (ví dụ: `#tech-react`, `#tech-springboot`).
- **Lưu trữ tin nhắn:**
  - Tin nhắn chat được lưu vào database để sinh viên khi vào phòng có thể xem lại lịch sử (phân trang tải thêm tin nhắn cũ khi cuộn lên).
  - Giới hạn lưu trữ hoặc phân trang mặc định 50 tin nhắn gần nhất mỗi khi vào phòng.

### 3.3. Tương tác Repository (Legacy Repo Socials)
- **Upvote/Downvote:** Giúp lọc ra các đồ án/mã nguồn chất lượng cao. Hiển thị tổng số vote score (Upvote - Downvote) trên từng card repo.
- **Đánh giá & Nhận xét:** Cho phép đánh giá từ 1 - 5 sao kèm bình luận chi tiết về code quality, kiến trúc dự án, mức độ dễ hiểu của mã nguồn.

### 3.4. Đánh giá Môn học (Course Review & Rating)
- **Mục tiêu:** Cung cấp thông tin tham khảo quý giá cho sinh viên khóa dưới.
- **Dữ liệu đánh giá:**
  - Điểm số đánh giá tổng hợp (trung bình cộng số sao).
  - Các bình luận/kinh nghiệm học tập: tài liệu nên đọc, thầy cô hướng dẫn, mẹo qua môn/đạt điểm cao.

---

## 4. Ràng buộc & Invariants (Behavior Invariants)

1. **Strict Auth Handshake:** Mọi kết nối WebSocket kết nối đến cổng chat phải mang theo JWT Token hợp lệ của Student.
2. **Vietnamese Language:** Toàn bộ giao diện chat, đánh giá, các thông báo lỗi và phản hồi hệ thống sử dụng tiếng Việt.
3. **No Guest Write:** Database và API bắt buộc kiểm tra quyền `ROLE_STUDENT` trước khi ghi dữ liệu chat/vote/review.
4. **Unique Vote/Review:** Mỗi sinh viên chỉ được vote 1 lần (up hoặc down hoặc cancel) cho mỗi repo, và viết tối đa 1 bài đánh giá cho mỗi repo/môn học (có quyền sửa/xóa bài đánh giá cũ).

---

## 5. Non-goals (Các mục tiêu ngoài phạm vi)

- Không làm chat trực tiếp giữa 2 cá nhân (Direct Message / PM).
- Không làm tính năng chia sẻ tệp tin (File transfer/image upload) trong chat cộng đồng để tránh quá tải lưu trữ (chỉ hỗ trợ gửi văn bản thuần túy và link).
- Không làm các phòng chat thoại (Voice/Video channels).
- Không làm hệ thống kiểm duyệt tin nhắn tự động bằng AI trong phase này (sẽ sử dụng công cụ ẩn/xóa tin nhắn của Admin nếu cần).

---

## 6. Trạng thái Hiện tại & Việc cần làm

| Thành phần | Trạng thái | Mô tả |
|---|---|---|
| Đăng nhập / Đăng ký | ⚠️ Đã có API & UI cơ bản | Cần tối ưu giao diện, sửa lỗi OTP nếu có và tích hợp vào Layout chính |
| WebSocket Infra | ❌ Chưa có | Cần cấu hình Spring Boot WebSocket STOMP Broker |
| Database Tables | ❌ Chưa có | Cần tạo migration SQL cho các bảng chat, vote, review |
| Giao diện Chat | ❌ Chưa có | Thiết kế layout Slack/Discord (Kênh trái, Chat giữa, User online bên phải) |
| API Reviews/Votes | ❌ Chưa có | Viết các REST Controller quản lý đánh giá môn học và repo |
| UI Reviews/Votes | ❌ Chưa có | Thêm phần đánh giá sao, bình luận vào CourseDetailPage và RepoDetailPage |
