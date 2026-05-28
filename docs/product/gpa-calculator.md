# GPA Calculator

## Purpose

DevOrbit includes a student-facing GPA calculator so students can quickly estimate their current 10-point GPA from course credits and grades.

## Public Route

- `/gpa-calculator`

## Product Contract

- Students can enter multiple course rows.
- Each row accepts course name, credits, and grade on the 10-point scale.
- Students can add or remove course rows.
- Students can duplicate a course row, clear all rows, add five blank rows at once, and reset to the default two-row template.
- The calculator autosaves draft rows, mode, semester, GPA inputs, and goal what-if grades to browser `localStorage` and restores them after reload.
- The calculator uses the same shared DevOrbit student-page background atmosphere pattern as the course list screen.
- Students can switch between semester GPA mode and cumulative GPA estimate mode.
- Students can load a semester preset from the DevOrbit course catalogue.
- Semester preset controls show the selected semester's course count and total credits before applying.
- Students can either replace the current course rows with a semester preset or merge the preset into the existing rows.
- Semester presets use `/api/courses`, include only matching semester courses with credits greater than 0, and leave grade cells empty for student input.
- If the student has saved a learning roadmap, semester presets use `devorbit_kanban_semester_map` before falling back to the catalogue semester.
- The calculator ignores invalid rows and shows guidance when no valid credits exist.
- Invalid rows show row-level reasons for missing credits, invalid credits, missing grades, or grades outside the 0-10 scale.
- When invalid rows are ignored, the summary panel shows how many rows are excluded from the current calculation.
- Semester GPA mode calculates only the rows currently entered in the calculator.
- Cumulative GPA estimate mode accepts current GPA and completed credits, then estimates the new cumulative GPA from current transcript data plus this term's rows.
- Cumulative GPA formula: `(current GPA * completed credits + semester GPA * semester credits) / (completed credits + semester credits)`.
- Goal planning mode accepts current GPA, completed credits, target GPA, and current-term course rows.
- Goal planning formula: `(target GPA * (completed credits + semester credits) - current GPA * completed credits) / semester credits`.
- Goal planning results include the required current-term GPA, feasibility status, total credits after the term, and approximate per-course target grades.
- Goal planning includes what-if inputs for each valid current-term course so students can enter projected grades, see the projected term GPA, see the projected cumulative GPA when all current-term grades are entered, and see the remaining average needed for unfilled courses when only some projected grades are entered.
- If the required current-term GPA is above 10, the target is marked not feasible for the current term and the result displays the raw required GPA above 10 instead of capping it to 10.
- If the required current-term GPA is below 0, the student is already safely above the target.
- Results include semester credits, weighted 10-point semester GPA, a simple academic classification, cumulative GPA projection when that mode is active, and GPA goal planning when that mode is active.
- The calculator does not display or convert to a 4-point scale.
- Classification uses the 10-point result: 9.0+ `Xuất sắc`, 8.0+ `Giỏi`, 7.0+ `Khá`, 5.0+ `Trung bình`, below 5.0 `Cần cải thiện`.

## Scope Boundaries

- This page is a client-side planning calculator.
- It reads course catalogue data for presets and saves draft calculator state only in the current browser.
- If catalogue loading fails, manual entry remains available.
- It does not replace official UIT transcript or academic office results.
