package vn.edu.uit.devorbit_api.dto.publicapi;

/**
 * COURSE SUMMARY = a compact representation of a course for list views.
 *
 * Used by GET /api/courses and GET /api/admin/courses.
 * Contains only the fields needed for a course card/row display.
 *
 * NOTE: The field "loaiMonHoc" (Vietnamese) corresponds to "subjectType"
 * in CourseDetailResponse. Same concept, different name for historical reasons.
 * loaiMonHoc values: "DAI_CUONG" (general), "CHUYEN_NGANH" (major), "CO_SO" (foundation)
 */
public record CourseSummaryResponse(
    Long id,
    String code,
    String name,
    String description,
    Long repoCount,
    Integer semester,
    int credits,
    String loaiMonHoc,
    String managementUnit
) {}
