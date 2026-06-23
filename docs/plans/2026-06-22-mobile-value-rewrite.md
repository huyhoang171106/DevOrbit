# Plan: Hệ thống khám phá môn học và tạo lộ trình học tập

## 1. Mục tiêu

Xây dựng hệ thống giúp sinh viên:

* Khám phá môn học và tài nguyên học tập phù hợp.
* Đánh giá nhanh chất lượng repository, tutorial, video và bài viết.
* Tạo lộ trình học cá nhân theo thời gian và kiến thức hiện tại.
* Theo dõi nhiệm vụ học tập hằng ngày thông qua checklist.

---

## 2. Mô-đun Môn học

### 2.1. Danh sách môn học

Hiển thị danh sách các môn học với các chức năng:

* Tìm kiếm theo tên hoặc mã môn.
* Lọc theo loại môn học.
* Lọc theo học kỳ.
* Đánh dấu môn học yêu thích bằng bookmark.
* Xem nhanh mô tả, số tín chỉ và mức độ khó của môn.

### 2.2. Chi tiết môn học

Mỗi môn học có một trang tổng hợp tài nguyên gồm:

* Repository GitHub.
* Tutorial.
* Video.
* Bài viết.
* Tài liệu học tập liên quan.

Hệ thống sử dụng AI để:

* Tóm tắt nội dung và giá trị của từng tài nguyên.
* Đánh giá tài nguyên phù hợp với người mới hay người đã có nền tảng.
* Đề xuất thứ tự học và cách sử dụng tài nguyên.

Một số môn phù hợp như Đại số tuyến tính, Giải tích, OOP và DSA sẽ có nút:

**“Tạo kế hoạch học môn này”**

Khi người dùng bấm nút, hệ thống chuyển sang tab **Kế hoạch** và tự động chọn môn học tương ứng.

---

## 3. Mô-đun Repository

Khi người dùng chọn một repository, hệ thống hiển thị:

* Thông tin cơ bản của repository.
* Ngôn ngữ và công nghệ sử dụng.
* Cấu trúc thư mục.
* Hướng dẫn cài đặt và chạy dự án.
* Mức độ cập nhật và hoạt động của repository.
* Số sao, fork và các chỉ số GitHub liên quan.

AI hỗ trợ:

* Tóm tắt mục đích và nội dung repository.
* Đánh giá mức độ phù hợp với môn học.
* Đưa ra lời khuyên về phần nên đọc hoặc thực hành trước.
* Cảnh báo khi repository thiếu tài liệu, khó chạy hoặc có cấu trúc phức tạp.

Người dùng có thể:

* Đánh giá và viết review.
* Vote tài nguyên hữu ích hoặc không hữu ích.
* Xem điểm cộng đồng và mức độ uy tín của repository.
* Bookmark repository để xem lại sau.

---

## 4. Mô-đun Kế hoạch học tập

### 4.1. Tạo kế hoạch

Người dùng cung cấp:

* Môn học muốn học.
* Ngày bắt đầu.
* Ngày kết thúc.
* Số ngày hoặc số giờ có thể học mỗi tuần.
* Kiến thức đã học.
* Mục tiêu học tập.
* Tài nguyên muốn sử dụng.

Hệ thống dựa trên:

* Đề cương môn học.
* Nội dung và prerequisite của môn.
* Tiến độ hiện tại của người dùng.
* Các course hoặc chủ đề người dùng đã hoàn thành.
* Thời gian học còn lại.

Từ đó, AI tạo một roadmap phù hợp.

Ví dụ:

* **Phase 1 — Tuần 1–2:** Học kiến thức nền tảng.
* **Phase 2 — Tuần 3–5:** Học các nội dung trọng tâm.
* **Phase 3 — Tuần 6–8:** Thực hành bài tập và xây dựng project.
* **Phase 4 — Tuần 9:** Ôn tập và tự đánh giá.

### 4.2. Nội dung của từng phase

Mỗi phase gồm:

* Mục tiêu cần đạt.
* Chủ đề cần học.
* Tài liệu được đề xuất.
* Video, bài viết hoặc repository liên quan.
* Bài tập cần hoàn thành.
* Kết quả đầu ra dự kiến.
* Tiêu chí để chuyển sang phase tiếp theo.

---

## 5. Lịch học và nhiệm vụ hằng ngày

Roadmap được tự động chia thành nhiệm vụ theo từng ngày.

Tại mục **Hôm nay**, người dùng có thể xem:

* Nội dung cần học trong ngày.
* Tài liệu cần đọc hoặc xem.
* Bài tập cần thực hiện.
* Thời gian học dự kiến.
* Mức độ ưu tiên.
* Tiến độ hiện tại của kế hoạch.

Ví dụ:

* Đọc phần giới thiệu về linked list.
* Xem video về singly linked list.
* Cài đặt các thao tác insert và delete.
* Hoàn thành ba bài tập thực hành.
* Tự kiểm tra bằng năm câu hỏi ngắn.

Mỗi nhiệm vụ được hiển thị dưới dạng todo để người dùng đánh dấu khi hoàn thành.

---

## 6. Theo dõi và điều chỉnh tiến độ

Hệ thống theo dõi:

* Tỷ lệ hoàn thành toàn bộ roadmap.
* Tiến độ của từng phase.
* Số nhiệm vụ đã hoàn thành.
* Số ngày học liên tục.
* Các nhiệm vụ bị trễ.
* Thời gian học thực tế so với kế hoạch.

Khi người dùng học chậm hoặc bỏ lỡ nhiệm vụ, hệ thống có thể:

* Dời nhiệm vụ sang ngày khác.
* Tự động phân bổ lại lịch học.
* Giảm hoặc tăng khối lượng công việc.
* Đề xuất kéo dài thời gian hoàn thành.
* Tạo nhiệm vụ ôn tập cho nội dung chưa nắm chắc.

---

## 7. Luồng người dùng chính

1. Người dùng mở danh sách môn học.
2. Tìm kiếm hoặc lọc môn học phù hợp.
3. Xem tài nguyên của môn học.
4. Đọc AI summary và đánh giá cộng đồng.
5. Chọn tài nguyên phù hợp.
6. Bấm **“Tạo kế hoạch học môn này”**.
7. Nhập thời gian, trình độ và mục tiêu.
8. Hệ thống sinh roadmap theo từng phase.
9. Roadmap được chia thành nhiệm vụ hằng ngày.
10. Người dùng hoàn thành và đánh dấu todo.
11. Hệ thống cập nhật tiến độ và điều chỉnh kế hoạch khi cần.

---

## 8. Phạm vi triển khai ưu tiên

### Giai đoạn 1

* Danh sách và filter môn học.
* Bookmark môn học.
* Trang chi tiết môn và danh sách tài nguyên.
* Trang chi tiết repository.
* AI summary cơ bản.
* Review, vote và social score.

### Giai đoạn 2

* Tạo roadmap theo ngày bắt đầu và kết thúc.
* Chia roadmap thành phase.
* Sinh todo hằng ngày.
* Đánh dấu nhiệm vụ hoàn thành.
* Hiển thị tiến độ kế hoạch.

### Giai đoạn 3

* Cá nhân hóa roadmap theo kiến thức đã học.
* Tự động điều chỉnh lịch khi nhiệm vụ bị trễ.
* Đề xuất tài nguyên theo từng phase.
* Sinh bài kiểm tra và nhiệm vụ ôn tập.
* Phân tích thói quen và hiệu quả học tập.
