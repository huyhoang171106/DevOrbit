package vn.edu.uit.devorbit_api.dto.publicapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * COURSE DETAIL = full information about a single course.
 *
 * Used by GET /api/courses/{id} and GET /api/admin/courses/{id}.
 * Contains all course metadata + a list of linked GitHub repos.
 *
 * NOTE: Fields equivalentMH, prerequisiteMH, previousMH use course CODES
 * (e.g., "SE101"), not IDs. They reference courses by their maMH value.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourseDetailResponse(
    Long id,
    String code,
    String name,
    String nameEn,
    String description,
    Integer theoryHours,
    Integer practiceHours,
    Integer credits,
    String subjectType,    // "DAI_CUONG", "CHUYEN_NGANH", "CO_SO", etc.
    Boolean isOpen,
    String managementUnit, // Department name, e.g., "CNPM"
    String codeOld,        // Previous course code (from before renumbering)
    String equivalentMH,   // Code of equivalent course for credit transfer
    String prerequisiteMH, // Code of prerequisite course (must be taken first)
    String previousMH,     // Code of recommended prior course (soft prerequisite)
    String learningObjectives,
    String gradingCriteria,
    com.fasterxml.jackson.databind.JsonNode topics,
    List<RepoSummaryResponse> repos  // Active GitHub repos linked to this course
) {}
