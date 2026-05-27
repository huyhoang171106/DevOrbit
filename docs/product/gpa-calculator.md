# GPA Calculator

## Purpose

DevOrbit includes a student-facing GPA calculator so students can quickly estimate their current 10-point GPA from course credits and grades.

## Public Route

- `/gpa-calculator`

## Product Contract

- Students can enter multiple course rows.
- Each row accepts course name, credits, and grade on the 10-point scale.
- Students can add or remove course rows.
- Students can switch between semester GPA mode and cumulative GPA estimate mode.
- Students can load a semester preset from the DevOrbit course catalogue.
- Semester presets use `/api/courses`, include only matching semester courses with credits greater than 0, and leave grade cells empty for student input.
- If the student has saved a learning roadmap, semester presets use `devorbit_kanban_semester_map` before falling back to the catalogue semester.
- The calculator ignores invalid rows and shows guidance when no valid credits exist.
- Semester GPA mode calculates only the rows currently entered in the calculator.
- Cumulative GPA estimate mode accepts current GPA and completed credits, then estimates the new cumulative GPA from current transcript data plus this term's rows.
- Cumulative GPA formula: `(current GPA * completed credits + semester GPA * semester credits) / (completed credits + semester credits)`.
- Goal planning mode accepts current GPA, completed credits, target GPA, and current-term course rows.
- Goal planning formula: `(target GPA * (completed credits + semester credits) - current GPA * completed credits) / semester credits`.
- Goal planning results include the required current-term GPA, feasibility status, total credits after the term, and approximate per-course target grades.
- If the required current-term GPA is above 10, the target is marked not feasible for the current term.
- If the required current-term GPA is below 0, the student is already safely above the target.
- Results include semester credits, weighted 10-point semester GPA, a simple academic classification, cumulative GPA projection when that mode is active, and GPA goal planning when that mode is active.
- The calculator does not display or convert to a 4-point scale.
- Classification uses the 10-point result: 9.0+ `Xuất sắc`, 8.0+ `Giỏi`, 7.0+ `Khá`, 5.0+ `Trung bình`, below 5.0 `Cần cải thiện`.

## Scope Boundaries

- This page is a client-side planning calculator.
- It reads course catalogue data for presets but does not save grades or calculator rows.
- If catalogue loading fails, manual entry remains available.
- It does not replace official UIT transcript or academic office results.
