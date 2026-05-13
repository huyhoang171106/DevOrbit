-- BỔ SUNG CÁC MÔN HỌC THIẾU TỪ KHÓA 19-2024 SO VỚI 20-2025
INSERT INTO courses (mamh, tenmh, tenmh_en, sotc, lt, th, loaimonhoc, semester, management_unit, description, is_open) VALUES
('ME001', 'Giáo dục Quốc phòng', NULL, 0, 0, 0, 'ĐC', NULL, 'CNPM', 'Môn học Giáo dục Quốc phòng và An ninh.', true),
('PE231', 'Giáo dục thể chất 1', NULL, 0, 0, 0, 'ĐC', 5, 'CNPM', 'Môn học Giáo dục thể chất phần 1.', true),
('PE232', 'Giáo dục thể chất 2', NULL, 0, 0, 0, 'ĐC', 6, 'CNPM', 'Môn học Giáo dục thể chất phần 2.', true),
('SE121', 'Đồ án 1', NULL, 2, 2, 0, 'TT', 6, 'CNPM', 'Môn học Đồ án 1 trong lộ trình đào tạo Kỹ thuật Phần mềm.', true),
('SE122', 'Đồ án 2', NULL, 2, 2, 0, 'TT', 7, 'CNPM', 'Môn học Đồ án 2 trong lộ trình đào tạo Kỹ thuật Phần mềm.', true),
('SE351', 'Xử lý song song', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Môn học cung cấp kiến thức về lập trình và xử lý song song trên các hệ thống tính toán hiệu năng cao.', true),
('SE329', 'Thiết kế 3D game engine', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Môn học đi sâu vào việc thiết kế và xây dựng các thành phần cốt lõi của một Game Engine 3D.', true),
('SE501', 'Thực tập doanh nghiệp', NULL, 2, 2, 0, 'TT', 7, 'CNPM', 'Sinh viên thực tập tại các doanh nghiệp để làm quen với môi trường làm việc thực tế.', true)
ON CONFLICT (mamh) DO UPDATE SET 
    tenmh = EXCLUDED.tenmh,
    sotc = EXCLUDED.sotc,
    semester = COALESCE(EXCLUDED.semester, courses.semester);

-- CẬP NHẬT MỐI QUAN HỆ (NẾU CÓ)
-- Ví dụ: Đồ án 2 cần học sau Đồ án 1
INSERT INTO course_relationships (course_id, related_course_id, relation_type)
SELECT 
    (SELECT stt FROM courses WHERE mamh = 'SE122'),
    (SELECT stt FROM courses WHERE mamh = 'SE121'),
    'PREREQUISITE'
ON CONFLICT DO NOTHING;
