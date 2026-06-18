package vn.edu.uit.devorbit_api.dto.publicapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * COURSE DETAIL = full response for a single course page.
 *
 * Used by: GET /api/courses/{id} (public) and admin endpoints.
 *
 * Contains ALL course fields plus linked GitHub repos.
 * @JsonInclude(NON_NULL) skips null fields from the JSON response,
 * keeping the payload small — optional fields like nameEn, codeOld
 * only appear if they have values.
 *
 * prerequisiteMH, previousMH, equivalentMH use course CODES (e.g., "SE101"),
 * NOT database IDs. This makes them human-readable directly in the API response.
 *
 * The `topics` field is a JSON structure that can store course topics/syllabus
 * in flexible format (not rigid columns).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourseDetailResponse(
    Long id,
    String code,                    // "SE101"
    String name,                    // Vietnamese name
    String nameEn,                  // English name (optional)
    String description,
    Integer theoryHours,            // lt (lý thuyết)
    Integer practiceHours,          // th (thực hành)
    Integer credits,                // soTC
    String subjectType,             // "DAI_CUONG" / "CHUYEN_NGANH" / "CO_SO"
    Boolean isOpen,                 // Is the course active?
    String managementUnit,          // Department, e.g., "CNPM"
    String codeOld,                 // Previous course code (maMH_Old)
    String equivalentMH,            // Course code for credit transfer
    String prerequisiteMH,          // Must be taken first
    String previousMH,              // Recommended prior course
    String learningObjectives,
    String gradingCriteria,
    com.fasterxml.jackson.databind.JsonNode topics,  // Flexible JSON syllabus
    List<RepoSummaryResponse> repos  // Approved repos for this course
) {}
