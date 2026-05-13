-- Update Courses to SE 2025 Curriculum
INSERT INTO courses (mamh, tenmh, sotc, lt, th, loaimonhoc) VALUES
('SE503', 'Đồ án', 2, 2, 0, 'BAT_BUOC'),
('SE502', 'Thực tập', 2, 2, 0, 'TOT_NGHIEP'),
('PE0231', 'Giáo dục thể chất 1', 0, 0, 0, 'DAI_CUONG'),
('PE0232', 'Giáo dục thể chất 2', 0, 0, 0, 'DAI_CUONG'),
('ME001', 'Giáo dục quốc phòng', 0, 0, 0, 'DAI_CUONG')
ON CONFLICT (mamh) DO UPDATE SET 
    tenmh = EXCLUDED.tenmh, 
    sotc = EXCLUDED.sotc,
    loaimonhoc = EXCLUDED.loaimonhoc;

-- Clear old relationships to rebuild from the 2025 graph
DELETE FROM course_relationships;

-- Insert relationships from the 2025 curriculum graph
-- Helper: (source_code, target_code, type)
WITH rels(src, tgt, typ) AS (
    VALUES 
    ('IT001', 'IT002', 'PREREQUISITE'),
    ('IT001', 'IT003', 'PREREQUISITE'),
    ('IT002', 'IT008', 'PREREQUISITE'),
    ('IT003', 'SE104', 'PREREQUISITE'),
    ('IT004', 'SE104', 'PREREQUISITE'),
    ('SE104', 'SE100', 'PREREQUISITE'),
    ('SE100', 'SE503', 'PREREQUISITE'),
    ('SE503', 'SE505', 'PREREQUISITE'),
    ('SE503', 'SE506', 'PREREQUISITE'),
    ('SE503', 'SE507', 'PREREQUISITE'),
    ('MA003', 'MA004', 'PREREQUISITE'),
    ('MA003', 'MA005', 'PREREQUISITE'),
    ('ENG01', 'ENG02', 'PREREQUISITE'),
    ('ENG02', 'ENG03', 'PREREQUISITE')
)
INSERT INTO course_relationships (course_id, related_course_id, relation_type)
SELECT s.stt, t.stt, r.typ::course_relation_type
FROM rels r
JOIN courses s ON s.mamh = r.src
JOIN courses t ON t.mamh = r.tgt;
