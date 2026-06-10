package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a university course stored in the "courses" database table.
 *
 * HOW THIS FITS IN THE ARCHITECTURE:
 * - This is a JPA Entity = a Java class that maps to a database row.
 * - The CourseController receives HTTP requests, calls CourseService,
 *   which uses CourseRepository to read/write Course entities.
 * -
 * FIELD NAMING NOTE:
 * Several fields use Vietnamese abbreviations (e.g., maMH, tenMH, soTC)
 * because they come from the university's database schema.
 * English translations are provided in the JavaDoc for each field.
 *
 * REQUEST FLOW EXAMPLE (GET /api/courses):
 *   1. Browser sends GET /api/courses
 *   2. PublicCourseController receives request
 *   3. Calls CourseService.getActiveCourseSummaries()
 *   4. Service calls CourseRepository.findAllWithRepoCountSortedByRepoCount()
 *   5. Repository runs a JOIN query → returns CourseSummaryResponse list
 *   6. Response flows back: Repository → Service → Controller → Browser
 */
@Entity
@Table(name = "courses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    // =====================================================================
    // PRIMARY KEY
    // =====================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stt")
    private Long id;

    // =====================================================================
    // VIETNAMESE FIELD: maMH = "Mã môn học" = Course Code
    // Example: "SE101" for "Nhập môn Công nghệ phần mềm"
    // =====================================================================

    @Column(name = "mamh", unique = true, nullable = false, length = 20)
    private String maMH;

    // =====================================================================
    // VIETNAMESE FIELD: tenMH = "Tên môn học" = Course Name (in Vietnamese)
    // =====================================================================

    @Column(name = "tenmh", nullable = false, length = 255)
    private String tenMH;

    // =====================================================================
    // VIETNAMESE FIELD: tenMH_EN = "Tên môn học (tiếng Anh)" = Course Name (English)
    // =====================================================================

    @Column(name = "tenmh_en", length = 255)
    private String tenMH_EN;

    // =====================================================================
    // VIETNAMESE FIELD: soTC = "Số tín chỉ" = Number of Credits
    // Example: 3 means this course gives 3 credits
    // =====================================================================

    @Column(name = "sotc")
    private int soTC;

    // =====================================================================
    // VIETNAMESE FIELD: lt = "Lý thuyết" = Theory/Lecture hours
    // =====================================================================

    private int lt;

    // =====================================================================
    // VIETNAMESE FIELD: th = "Thực hành" = Practice/Lab hours
    // =====================================================================

    private int th;

    // =====================================================================
    // VIETNAMESE FIELD: loaiMonHoc = "Loại môn học" = Subject Type
    // Examples: "DAI_CUONG" (general), "CHUYEN_NGANH" (major), "CO_SO" (foundation)
    // =====================================================================

    @Column(name = "loaimonhoc", nullable = false, length = 50)
    private String loaiMonHoc;

    // =====================================================================
    // The semester in which this course is typically taken
    // (e.g., 1 = first semester, 2 = second semester)
    // =====================================================================

    private Integer semester;

    // =====================================================================
    // Whether this course is currently active/open for enrollment
    // =====================================================================

    @Column(name = "is_open")
    private boolean isOpen;

    // =====================================================================
    // The department or faculty that manages this course
    // Example: "CNPM" (Công nghệ phần mềm = Software Engineering)
    // =====================================================================

    @Column(name = "management_unit", length = 100)
    private String managementUnit;

    // =====================================================================
    // A longer description of what this course covers
    // =====================================================================

    @Column(columnDefinition = "TEXT")
    private String description;

    // =====================================================================
    // VIETNAMESE FIELD: maMH_Old = "Mã môn học cũ" = Previous/Old course code
    // Used when the university renumbers course codes
    // =====================================================================

    @Column(name = "mamh_old", length = 50)
    private String maMH_Old;

    // =====================================================================
    // Code of an equivalent course (used for credit transfer)
    // =====================================================================

    @Column(name = "equivalent_mh")
    private String equivalentMH;

    // =====================================================================
    // Code of the prerequisite course (must be completed BEFORE this course)
    // Example: "SE101" must be done before "SE201"
    // =====================================================================

    @Column(name = "prerequisite_mh")
    private String prerequisiteMH;

    // =====================================================================
    // Code of a previous/recommended course (should be done before this one)
    // Soft requirement — unlike prerequisite, not strictly enforced
    // =====================================================================

    @Column(name = "previous_mh")
    private String previousMH;

    // =====================================================================
    // SYLLABUS FIELDS
    // =====================================================================

    @Column(name = "learning_objectives", columnDefinition = "TEXT")
    private String learningObjectives;

    @Column(name = "grading_criteria", columnDefinition = "TEXT")
    private String gradingCriteria;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "topics")
    private com.fasterxml.jackson.databind.JsonNode topics;
}

