-- ============================================================
-- cleanup_courses.sql
-- Xoá các môn học không nằm trong danh sách giữ lại,
-- kèm repo và candidate thuộc các môn đó.
--
-- Cách chạy:
--   psql -d devorbit_db -f cleanup_courses.sql
--
-- CÁC MÔN GIỮ LẠI (KHÔNG ĐỘNG VÀO):
--   SE      : tất cả mã 2 chữ + 3 số (^SE\d{3}$)
--   IT      : IT001, IT002, IT003, IT004, IT005, IT006, IT007, IT008
--   SS      : SS003, SS004, SS006, SS007, SS008, SS009, SS010
--   MA      : MA003, MA004, MA005, MA006
--   ENG     : ENG01, ENG02, ENG03
-- ============================================================

-- === 0. PREVIEW: xem những môn sẽ bị xoá và số lượng liên quan ===

DO $$
DECLARE
  delete_count INT;
BEGIN
  RAISE NOTICE '=== PREVIEW: Các môn sẽ bị xOÁ ===';

  SELECT COUNT(*) INTO delete_count FROM courses WHERE NOT (
    mamh ~ '^SE\d{3}$'
    OR mamh IN ('IT001','IT002','IT003','IT004','IT005','IT006','IT007','IT008')
    OR mamh IN ('SS003','SS004','SS006','SS007','SS008','SS009','SS010')
    OR mamh IN ('MA003','MA004','MA005','MA006')
    OR mamh IN ('ENG01','ENG02','ENG03')
  );
  RAISE NOTICE 'Số môn sẽ bị xoá: %', delete_count;

  SELECT COUNT(*) INTO delete_count FROM github_repos
  WHERE course_id IN (
    SELECT stt FROM courses WHERE NOT (
      mamh ~ '^SE\d{3}$'
      OR mamh IN ('IT001','IT002','IT003','IT004','IT005','IT006','IT007','IT008')
      OR mamh IN ('SS003','SS004','SS006','SS007','SS008','SS009','SS010')
      OR mamh IN ('MA003','MA004','MA005','MA006')
      OR mamh IN ('ENG01','ENG02','ENG03')
    )
  );
  RAISE NOTICE 'Số repo sẽ bị xoá: %', delete_count;

  SELECT COUNT(*) INTO delete_count FROM repo_candidates
  WHERE course_id IN (
    SELECT stt FROM courses WHERE NOT (
      mamh ~ '^SE\d{3}$'
      OR mamh IN ('IT001','IT002','IT003','IT004','IT005','IT006','IT007','IT008')
      OR mamh IN ('SS003','SS004','SS006','SS007','SS008','SS009','SS010')
      OR mamh IN ('MA003','MA004','MA005','MA006')
      OR mamh IN ('ENG01','ENG02','ENG03')
    )
  );
  RAISE NOTICE 'Số repo_candidates sẽ bị xoá: %', delete_count;
END $$;

-- === 1. XOÁ GITHUB_REPOS thuộc môn bị xoá ===
-- (repo_tech_stacks tự động cascade theo github_repos.id)

DELETE FROM github_repos
WHERE course_id IN (
  SELECT stt FROM courses WHERE NOT (
    mamh ~ '^SE\d{3}$'
    OR mamh IN ('IT001','IT002','IT003','IT004','IT005','IT006','IT007','IT008')
    OR mamh IN ('SS003','SS004','SS006','SS007','SS008','SS009','SS010')
    OR mamh IN ('MA003','MA004','MA005','MA006')
    OR mamh IN ('ENG01','ENG02','ENG03')
  )
);

-- === 2. XOÁ REPO_CANDIDATES thuộc môn bị xoá ===

DELETE FROM repo_candidates
WHERE course_id IN (
  SELECT stt FROM courses WHERE NOT (
    mamh ~ '^SE\d{3}$'
    OR mamh IN ('IT001','IT002','IT003','IT004','IT005','IT006','IT007','IT008')
    OR mamh IN ('SS003','SS004','SS006','SS007','SS008','SS009','SS010')
    OR mamh IN ('MA003','MA004','MA005','MA006')
    OR mamh IN ('ENG01','ENG02','ENG03')
  )
);

-- === 3. XOÁ COURSES ===
-- (course_relationships, course_youtube_playlists, course_articles,
--  course_tutorials tự động cascade theo courses.stt)

DELETE FROM courses WHERE NOT (
  mamh ~ '^SE\d{3}$'
  OR mamh IN ('IT001','IT002','IT003','IT004','IT005','IT006','IT007','IT008')
  OR mamh IN ('SS003','SS004','SS006','SS007','SS008','SS009','SS010')
  OR mamh IN ('MA003','MA004','MA005','MA006')
  OR mamh IN ('ENG01','ENG02','ENG03')
);

-- === KẾT THÚC ===
