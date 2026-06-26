-- Backfill semester column for courses based on KTPM curriculum (Khoá 20-2025).
-- Source: CurriculumConstants.CURRICULUM_SEMESTERS in devorbit-api.

UPDATE courses SET semester = 1 WHERE maMH IN ('IT001','IT007','MA003','MA006','ENG01','SE005');
UPDATE courses SET semester = 2 WHERE maMH IN ('IT002','IT003','MA004','MA005','ENG02');
UPDATE courses SET semester = 3 WHERE maMH IN ('IT008','IT004','IT005','IT012','ENG03');
UPDATE courses SET semester = 4 WHERE maMH IN ('SE104','SS004','SS007','SS008');
UPDATE courses SET semester = 5 WHERE maMH IN ('SE100','SS009','SS010');
UPDATE courses SET semester = 6 WHERE maMH IN ('SE503','SS003','SS006');
UPDATE courses SET semester = 7 WHERE maMH IN ('SE502');
UPDATE courses SET semester = 8 WHERE maMH IN ('SE505','SE506','SE507');
