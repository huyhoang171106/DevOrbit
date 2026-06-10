-- UPDATE ACTUAL ELECTIVE SUBJECTS TO 'TU_CHON'
-- This allows the frontend to filter them into the selection overlay
UPDATE courses 
SET loaimonhoc = 'TU_CHON' 
WHERE mamh IN (
    -- CSN Electives
    'SE359', 'SE102', 'SE115', 'SE116', 'SE114', 'SE360', 'SE361', 'SE301', 'SE215', 'SE113', 'SE358', 'SE117',
    -- CN Electives
    'SE330', 'SE332', 'SE334', 'SE347', 'SE350', 'SE343', 'SE355', 'SE310', 'SE346', 'SE404', 'SE331', 'SE313', 'SE352', 
    'SE109', 'SE356', 'SE357', 'SE325', 'SE101', 'SE106', 'SE214', 'SE362', 'SE363', 'SE364', 'SE365', 
    'SE221', 'SE220', 'SE320', 'SE327', 'SE328', 'SE344', 'SE314', 'SE315', 'SE316', 'SE317'
);

-- INSERT PLACEHOLDER NODES INTO THE ROADMAP
-- These nodes have mamh starting with 'TC_' to trigger the elective overlay in GalaxyPage.tsx
INSERT INTO courses (mamh, tenmh, sotc, lt, th, loaimonhoc, semester, management_unit, description, is_open) VALUES
('TC_CSN_4',   'Tự chọn Cơ sở ngành', 4, 3, 1, 'CSN', 4, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Cơ sở ngành.', true),
('TC_CSN_5_1', 'Tự chọn Cơ sở ngành', 4, 3, 1, 'CSN', 5, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Cơ sở ngành.', true),
('TC_CSN_5_2', 'Tự chọn Cơ sở ngành', 4, 3, 1, 'CSN', 5, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Cơ sở ngành.', true),
('TC_CN_6_1',  'Tự chọn Chuyên ngành', 4, 3, 1, 'CN',  6, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Chuyên ngành.', true),
('TC_CN_6_2',  'Tự chọn Chuyên ngành', 4, 3, 1, 'CN',  6, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Chuyên ngành.', true),
('TC_CN_7_1',  'Tự chọn Chuyên ngành', 4, 3, 1, 'CN',  7, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Chuyên ngành.', true),
('TC_CN_7_2',  'Tự chọn Chuyên ngành', 4, 3, 1, 'CN',  7, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Chuyên ngành.', true),
('TC_TD_7',    'Tự chọn tự do',        6, 6, 0, 'ĐC',  7, 'CNPM', 'Sinh viên chọn môn tự chọn tự do.', true)
ON CONFLICT (mamh) DO UPDATE SET 
    tenmh = EXCLUDED.tenmh,
    semester = EXCLUDED.semester,
    loaimonhoc = EXCLUDED.loaimonhoc;
