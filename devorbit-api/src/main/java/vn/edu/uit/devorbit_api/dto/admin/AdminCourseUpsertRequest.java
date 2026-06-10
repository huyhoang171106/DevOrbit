package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ADMIN COURSE UPSERT REQUEST = create or update a course.
 *
 * Used by:
 *   POST /api/admin/courses      (create)
 *   PUT  /api/admin/courses/{id} (update)
 *
 * Required fields: code, name, credits, subjectType.
 *
 * Fields map to Vietnamese entity fields:
 *   code        → maMH (Mã môn học = course code)
 *   name        → tenMH (Tên môn học = course name)
 *   credits     → soTC (Số tín chỉ = credit count)
 *   subjectType → loaiMonHoc (Loại môn học = subject type)
 */
public record AdminCourseUpsertRequest(
    @NotBlank String code,              // Course code, e.g., "SE101"
    @NotBlank String name,              // Course name in Vietnamese
    String nameEn,                       // Course name in English (optional)
    @NotNull Integer credits,            // Number of credits (soTC)
    Integer lectureHours,                // Theory hours (lt) — null = 0
    Integer practiceHours,               // Practice hours (th) — null = 0
    @NotBlank String subjectType,        // "DAI_CUONG" / "CHUYEN_NGANH" / "CO_SO"
    Boolean isOpen,                      // Is the course active? (default: true)
    String managementUnit,               // Managing department, e.g., "CNPM"
    String codeOld,                      // Previous course code (maMH_Old)
    String equivalentMH,                 // Equivalent course for credit transfer
    String prerequisiteMH,               // Prerequisite course (must take first)
    String previousMH,                   // Recommended prior course
    String description,                  // Course description (optional)
    String learningObjectives,
    String gradingCriteria,
    com.fasterxml.jackson.databind.JsonNode topics
) {}
