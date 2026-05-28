-- Seed admin user for integration tests
INSERT INTO admin_users (username, password_hash, active)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true);

-- Seed courses for integration tests
INSERT INTO courses (mamh, tenmh, tenmh_en, sotc, lt, th, loaimonhoc, semester, management_unit, description)
VALUES ('SE101', 'Nhập môn Công nghệ phần mềm', 'Introduction to Software Engineering', 4, 3, 1, 'CSN', 4, 'CNPM', 'Môn học nhập môn về công nghệ phần mềm');

-- Seed photobooth_frames for integration tests (used by PhotoboothFrameService.seedDefaults)
INSERT INTO photobooth_frames (id, frame_id, name, display_name, photo_count, slots, filter, background_color)
VALUES ('a0000000-0000-0000-0000-000000000001', 'classic-4', 'Classic', 'Classic 4-Hình', 4, '[]', 'normal', '#1a1a2e');
