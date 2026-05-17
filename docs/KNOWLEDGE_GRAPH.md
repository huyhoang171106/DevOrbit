# Knowledge Graph - Trạng thái hiện tại (v2.0)

Tài liệu này mô tả chi tiết trạng thái hiện tại của tính năng **Sơ đồ kiến thức (Knowledge Graph)** dành cho chương trình đào tạo **Kỹ thuật Phần mềm (SE) 2025**.

## 1. Tổng quan (Overview)
Sơ đồ kiến thức đã được chuyển đổi từ dạng tự do (Force-directed) sang dạng **Lưới (Blueprint Grid)** để tối ưu hóa việc theo dõi lộ trình học tập theo từng học kỳ.

- **Chương trình áp dụng**: Kỹ thuật Phần mềm (SE) Khóa 2025 (UIT).
- **Tổng số tín chỉ**: 130 TC (Tối thiểu).
- **Cấu trúc**: Phân tách theo 8 học kỳ chính quy.

## 2. Giao diện & Trải nghiệm (UI/UX)

### Hệ thống Card (Thẻ môn học)
- Các nút (nodes) đã được thay thế bằng các thẻ hình chữ nhật chuyên nghiệp.
- **Hiển thị**: Bao gồm Mã môn học (Font Black, Bold) và Tên môn học (Font Regular, Truncated).
- **Phân loại màu sắc**:
  - `Trắng/Xám`: Trạng thái mặc định.
  - `Xanh dương`: Đang được chọn (Môn tự chọn).
  - `Đỏ (Failed)`: Môn bị đánh dấu trượt (Trong chế độ giả lập).
  - `Hồng (Blocked)`: Môn bị ảnh hưởng/không thể học do môn tiên quyết bị trượt.

### Bố cục Lưới (Grid Layout)
- **Trục ngang (X)**: Chia theo 8 học kỳ (Học kỳ 1 -> Học kỳ 8).
- **Trục dọc (Y)**: Tự động căn chỉnh các môn học trong cùng một kỳ để tránh chồng lấp.
- **Headers**: Nhãn "HỌC KỲ 1 - 8" được vẽ trực tiếp trong Canvas, đồng bộ hóa tuyệt đối khi Phóng to/Thu nhỏ (Zoom/Pan).
- **Phân cách**: Sử dụng các đường kẻ đứt quãng (Dashed lines) để phân định ranh giới giữa các học kỳ.

## 3. Tính năng cốt lõi (Core Features)

### Chế độ Giả lập (Simulation Mode)
- Cho phép người dùng click vào một môn học để giả định việc "Trượt môn".
- **Logic Lan truyền (Cascade)**: Hệ thống tự động tính toán và đánh dấu tất cả các môn "hậu bối" (downstream) bị ảnh hưởng theo quan hệ Tiền đề (Prerequisite).

### Hệ thống môn Tự chọn (Elective System)
- **Placeholders**: Các ô đặc biệt "Tự chọn Cơ sở ngành", "Tự chọn Chuyên ngành" xuất hiện tại các học kỳ tương ứng (4, 5, 6, 7, 8).
- **Panel tương tác**: Khi click vào ô Tự chọn, một bảng danh sách (Overlay) sẽ hiện ra bên dưới với các môn học thực tế (Phát triển Game, Microservices, UI/UX...).
- **Lọc dữ liệu**: Tự động lọc dựa trên trường `loaiMonHoc` từ Backend.

## 4. Cấu trúc Dữ liệu & Backend

### Database (PostgreSQL)
- **Bảng `courses`**: Đã bổ sung cột `semester` để xác định vị trí trên grid.
- **Bảng `course_relationships`**: Lưu trữ các mối quan hệ `PREREQUISITE` để vẽ các mũi tên liên kết.
- **Seeding**: File `data.sql` chứa đầy đủ 100% lộ trình SE 2025 và hơn 40 môn tự chọn chuyên sâu.

### API & DTO
- **Endpoint**: `/api/courses/graph` trả về toàn bộ cấu trúc đồ thị.
- **DTO**: `CourseSummaryResponse` đã được nâng cấp để bao gồm thông tin `semester` và `loaiMonHoc`.

## 5. Các thành phần kỹ thuật
- **Frontend**: `React`, `react-force-graph-2d`, `TailwindCSS`.
- **State Management**: `Zustand` (GalaxyStore) dùng cho simulation mode.
- **Backend**: `Spring Boot`, `Hibernate/JPA`, `PostgreSQL`.

---
**Cap nhat lan cuoi**: 11/05/2026.
