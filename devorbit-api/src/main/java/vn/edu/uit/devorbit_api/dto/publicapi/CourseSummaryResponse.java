package vn.edu.uit.devorbit_api.dto.publicapi;

/**
 * COURSE SUMMARY = lightweight course info for list views.
 *
 * Used in: GET /api/courses (public) and GET /api/admin/courses (admin).
 * Contains ONLY fields needed to render a course card/row — no heavy joins.
 *
 * The repoCount field is computed via LEFT JOIN COUNT in the repository,
 * not stored in the database. This avoids an extra column that needs syncing.
 *
 * loaiMonHoc values:
 *   DAI_CUONG     — General education (e.g., English, Math)
 *   CHUYEN_NGANH  — Major/specialization course
 *   CO_SO         — Foundation course
 *
 * Note: loaiMonHoc here = subjectType in CourseDetailResponse.
 * Same field, different name for historical consistency.
 */
public record CourseSummaryResponse(
    Long id,
    String code,              // Course code (maMH), e.g., "SE101"
    String name,              // Course name in Vietnamese (tenMH)
    String description,       // Brief course description
    Long repoCount,           // Number of approved GitHub repos linked
    Integer semester,         // Recommended semester (1-9)
    int credits,              // Credit hours (soTC)
    String loaiMonHoc,        // Subject type classification
    String managementUnit     // Department: "CNPM", "HTTT", etc.
) {}
