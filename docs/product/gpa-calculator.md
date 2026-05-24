# GPA Calculator

## Purpose

DevOrbit includes a student-facing GPA calculator so students can quickly estimate their current 10-point GPA from course credits and grades.

## Public Route

- `/gpa-calculator`

## Product Contract

- Students can enter multiple course rows.
- Each row accepts course name, credits, and grade on the 10-point scale.
- Students can add or remove course rows.
- The calculator ignores invalid rows and shows guidance when no valid credits exist.
- Results include total credits, weighted 10-point GPA, and a simple academic classification.
- The calculator does not display or convert to a 4-point scale.
- Classification uses the 10-point result: 9.0+ `Xuất sắc`, 8.0+ `Giỏi`, 7.0+ `Khá`, 5.0+ `Trung bình`, below 5.0 `Cần cải thiện`.

## Scope Boundaries

- This page is a client-only planning calculator.
- It does not save grades or connect to backend APIs.
- It does not replace official UIT transcript or academic office results.
