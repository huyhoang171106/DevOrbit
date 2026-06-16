package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.edu.uit.devorbit_api.dto.admin.AdminCourseUpsertRequest;
import vn.edu.uit.devorbit_api.dto.admin.CourseDeleteResult;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseDetailResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.*;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * COURSE SERVICE = the BUSINESS LOGIC layer for course operations.
 *
 * WHAT IS A SERVICE?
 * A Service is where we put "business logic" — the rules and operations
 * that aren't just "save this" or "read that." The Service:
 * 1. Receives requests from Controllers
 * 2. Applies business rules
 * 3. Talks to Repositories for data access
 * 4. Returns DTOs (Data Transfer Objects) to Controllers
 *
 * REQUEST FLOW for "get all courses":
 *   Browser → GET /api/courses
 *   → PublicCourseController.getCourses()
 *   → CourseService.getActiveCourseSummaries()
 *   → CourseRepository.findAllWithRepoCountSortedByRepoCount()
 *   → PostgreSQL (JOIN query)
 *   → CourseSummaryResponse list back to browser
 *
 * LAYER DIAGRAM:
 *   ┌─────────────────────────────────────┐
 *   │  Controller (HTTP layer)            │
 *   │  - Parses request parameters        │
 *   │  - Calls service methods            │
 *   ├─────────────────────────────────────┤
 *   │  Service (Business logic layer)     │ ← YOU ARE HERE
 *   │  - Validates data                   │
 *   │  - Applies rules                    │
 *   │  - Coordinates multiple repos       │
 *   ├─────────────────────────────────────┤
 *   │  Repository (Data access layer)     │
 *   │  - Reads/writes database            │
 *   └─────────────────────────────────────┘
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    private final CourseRepository courseRepository;
    private final GithubRepoService githubRepoService;
    private final CourseTutorialRepository courseTutorialRepository;
    private final CourseYoutubePlaylistRepository courseYoutubePlaylistRepository;
    private final CourseArticleRepository courseArticleRepository;
    private final CourseRelationshipRepository courseRelationshipRepository;
    private final GithubRepoRepository githubRepoRepository;
    private final RepoCandidateRepository repoCandidateRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final StudentBookmarkRepository studentBookmarkRepository;
    private final NoteRepository noteRepository;
    private final RepoVoteRepository repoVoteRepository;
    private final RepoReviewRepository repoReviewRepository;
    private final ChatChannelRepository chatChannelRepository;
    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final KnowledgeSourceRepository knowledgeSourceRepository;
    private final CourseSyllabusRepository courseSyllabusRepository;
    private final CourseObjectiveRepository courseObjectiveRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;
    private final CourseSessionRepository courseSessionRepository;
    private final CourseAssessmentRepository courseAssessmentRepository;
    private final CourseReferenceRepository courseReferenceRepository;
    private final CourseToolRepository courseToolRepository;
    private final CommunityChatService communityChatService;
    private final CommunityMessageRepository communityMessageRepository;

    // =====================================================================
    // LIST METHODS
    // =====================================================================

    /**
     * Get course summaries for the PUBLIC API (anyone can access).
     * Cached for 5 minutes to avoid repeated DB queries.
     */
    @Cacheable(value = "courses", key = "'all'", unless = "#result.isEmpty()")
    public List<CourseSummaryResponse> getActiveCourseSummaries() {
        return courseRepository.findAllWithRepoCountSortedByRepoCount();
    }

    /**
     * Evict course cache when courses are modified.
     */
    @CacheEvict(value = "courses", allEntries = true)
    public void evictCourseCache() {
        // Called after create/update/delete operations
    }

    /**
     * Search/filter course summaries with optional parameters.
     * All filters are case-insensitive and applied client-side on the full list.
     * For large datasets, consider adding native SQL query with WHERE clauses.
     */
    public List<CourseSummaryResponse> searchCourses(
            String q, String subjectType, Integer semester, String managementUnit) {
        return courseRepository.findAllWithRepoCountSortedByRepoCount().stream()
            .filter(c -> q == null || q.isBlank() ||
                c.name().toLowerCase().contains(q.toLowerCase()) ||
                c.code().toLowerCase().contains(q.toLowerCase()))
            .filter(c -> subjectType == null || subjectType.isBlank() ||
                subjectType.equalsIgnoreCase(c.loaiMonHoc()))
            .filter(c -> semester == null || semester.equals(c.semester()))
            .filter(c -> managementUnit == null || managementUnit.isBlank() ||
                managementUnit.equalsIgnoreCase(c.managementUnit()))
            .toList();
    }

    /**
     * Get course summaries for the ADMIN API (restricted access).
     *
     * Shows ALL courses (both active and inactive) so admins can
     * manage the full course catalog. Same query as getActiveCourseSummaries()
     * for now, but will add filtering logic later.
     */
    public List<CourseSummaryResponse> getAllCourseSummaries() {
        return courseRepository.findAllWithRepoCountSortedByRepoCount();
    }

    // =====================================================================
    // DETAIL METHODS
    // =====================================================================

    /**
     * Get full course details + approved GitHub repos for the public API.
     *
     * Internally calls findCourseById() which:
     * 1. Looks up the Course entity by ID
     * 2. Maps it to a CourseDetailResponse DTO
     * 3. Includes all repos linked to this course
     *
     * If the course doesn't exist, throws NotFoundException
     * (the ApiExceptionHandler catches this and returns HTTP 404).
     */
    public CourseDetailResponse getCourseDetail(Long id) {
        return findCourseById(id);
    }

    // =====================================================================
    // CREATE / UPDATE / DELETE METHODS
    // =====================================================================

    /**
     * Create a new course from admin request data.
     *
     * Step-by-step:
     * 1. Build a Course entity from the incoming request DTO
     * 2. Save it to the database via courseRepository.save()
     * 3. Convert the saved entity back to a CourseDetailResponse DTO
     * 4. Return the DTO (Spring serializes it to JSON)
     */
    @CacheEvict(value = "courses", allEntries = true)
    public CourseDetailResponse createCourse(AdminCourseUpsertRequest request) {
        Course course = Course.builder()
                .maMH(request.code())
                .tenMH(request.name())
                .tenMH_EN(request.nameEn())
                .soTC(defaultZero(request.credits()))
                .lt(defaultZero(request.lectureHours()))
                .th(defaultZero(request.practiceHours()))
                .loaiMonHoc(request.subjectType())
                .isOpen(request.isOpen() != null ? request.isOpen() : true)
                .managementUnit(request.managementUnit())
                .description(request.description())
                .maMH_Old(request.codeOld())
                .equivalentMH(request.equivalentMH())
                .prerequisiteMH(request.prerequisiteMH())
                .previousMH(request.previousMH())
                .learningObjectives(request.learningObjectives())
                .gradingCriteria(request.gradingCriteria())
                .topics(request.topics())
                .build();
        Course saved = courseRepository.save(course);
        communityChatService.createChannel(ChatChannelType.COURSE, String.valueOf(saved.getId()), saved.getTenMH());
        return mapToDetail(saved);
    }

    /**
     * Update an existing course.
     *
     * Step-by-step:
     * 1. Find the existing course in the database
     * 2. If not found, throw NotFoundException (→ HTTP 404)
     * 3. Apply the new field values from the request
     * 4. Save the updated entity
     * 5. Return the updated detail DTO
     */
    @CacheEvict(value = "courses", allEntries = true)
    public CourseDetailResponse updateCourse(Long id, AdminCourseUpsertRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));
        applyRequestFields(course, request);
        return mapToDetail(courseRepository.save(course));
    }

    /**
     * Delete a course and all owned dependents atomically.
     *
     * Cleanup order respects FK constraints:
     *   1. Non-FK / business-key dependents (bookmarks, notes, RAG, channels)
     *   2. Child entities with FK to course
     *   3. Repo-scoped dependents (votes, reviews, bookmarks, notes)
     *   4. GithubRepos (including ManyToMany join rows)
     *   5. The Course itself
     *
     * @Transactional ensures atomicity — any failure rolls back everything.
     */
    @Transactional
    @CacheEvict(value = "courses", allEntries = true)
    public CourseDeleteResult deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));

        String courseCode = course.getMaMH();

        // 1. Clean up non-FK / business-key dependents that reference the course
        // 1a. StudentBookmarks for this course
        studentBookmarkRepository.deleteByTargetTypeAndTargetId("COURSE", id);

        // 1b. Notes for this course
        noteRepository.deleteByTargetTypeAndTargetId(NoteTargetType.COURSE, id);

        // Note: 1c. Chat channel cleanup is deferred to step 5 (end of method)
        //       to support soft-delete when messages exist

        // 1d. RAG / knowledge data keyed by business key (courseCode / maMH)
        try {
            knowledgeChunkRepository.deleteByCourseCode(courseCode);
        } catch (InvalidDataAccessResourceUsageException e) {
            log.warn("deleteCourse: knowledge_chunks table not available (expected in some test envs): {}", e.getMessage());
        }
        courseSyllabusRepository.deleteByCourseCode(courseCode);
        courseObjectiveRepository.deleteByCourseCode(courseCode);
        courseOutcomeRepository.deleteByCourseCode(courseCode);
        courseSessionRepository.deleteByCourseCode(courseCode);
        courseAssessmentRepository.deleteByCourseCode(courseCode);
        courseReferenceRepository.deleteByCourseCode(courseCode);
        courseToolRepository.deleteByCourseCode(courseCode);

        // 1e. Remove orphaned KnowledgeSources that have no remaining chunks
        try {
            List<UUID> orphanedSourceIds = knowledgeSourceRepository.findAll().stream()
                    .filter(ks -> knowledgeChunkRepository.findBySourceIdOrderByChunkIndexAsc(ks.getId()).isEmpty())
                    .map(KnowledgeSource::getId)
                    .toList();
            if (!orphanedSourceIds.isEmpty()) {
                knowledgeSourceRepository.deleteAllById(orphanedSourceIds);
                log.info("deleteCourse: removed {} orphaned knowledge sources for course {}", orphanedSourceIds.size(), courseCode);
            }
        } catch (InvalidDataAccessResourceUsageException e) {
            log.warn("deleteCourse: knowledge_chunks table not available for orphan check (expected in some test envs): {}", e.getMessage());
        }

        // 2. Clean up child entities with FK to course
        courseTutorialRepository.deleteByCourseId(id);
        courseYoutubePlaylistRepository.deleteByCourseId(id);
        courseArticleRepository.deleteByCourseId(id);
        courseRelationshipRepository.deleteByCourseIdOrRelatedCourseId(id, id);
        courseReviewRepository.deleteByCourseId(id);
        repoCandidateRepository.deleteByCourseId(id);

        // 3. Clean up GithubRepos and their scoped dependents
        List<GithubRepo> repos = githubRepoRepository.findByCourseId(id);
        if (!repos.isEmpty()) {
            List<Long> repoIds = repos.stream().map(GithubRepo::getId).toList();

            // 3a. Repo-scoped dependents (non-FK relationships)
            studentBookmarkRepository.deleteByTargetTypeAndTargetIdIn("REPO", repoIds);
            noteRepository.deleteByTargetTypeAndTargetIdIn(NoteTargetType.REPO, repoIds);

            // 3b. Repo-scoped dependents with DB FK (ON DELETE CASCADE exists, clean explicitly for consistency)
            repoVoteRepository.deleteByRepoIdIn(repoIds);
            repoReviewRepository.deleteByRepoIdIn(repoIds);

            // 3c. Remove ManyToMany join rows then delete repos
            for (GithubRepo repo : repos) {
                repo.getTechStacks().clear();
                githubRepoRepository.delete(repo);
            }
        }

        // 4. Delete the course itself
        courseRepository.delete(course);

        // 5. Soft-delete chat channels with messages, hard-delete empty ones
        var channels = chatChannelRepository.findByTypeAndReferenceId(ChatChannelType.COURSE, String.valueOf(id));
        boolean channelDeactivated = false;
        if (!channels.isEmpty()) {
            var channel = channels.get(0);
            if (communityMessageRepository.existsByChannelId(channel.getId())) {
                channel.setActive(false);
                chatChannelRepository.save(channel);
                channelDeactivated = true;
            } else {
                chatChannelRepository.delete(channel);
            }
        }

        log.info("deleteCourse: course id={} code={} deleted with all dependents", id, courseCode);
        return new CourseDeleteResult(channelDeactivated);
    }

    // =====================================================================
    // PRIVATE HELPER METHODS
    // =====================================================================

    /**
     * Find a course by ID, convert it to a detail DTO.
     * If not found, return HTTP 404 via NotFoundException.
     */
    private CourseDetailResponse findCourseById(Long id) {
        return courseRepository.findById(id)
                .map(this::mapToDetail)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));
    }

    /**
     * Convert null to 0 for numeric fields (credits, hours, etc.).
     * This prevents NullPointerException when the database has null values
     * but the entity uses primitive int (which can't be null).
     */
    private int defaultZero(Integer value) {
        return value != null ? value : 0;
    }

    /**
     * Copy all fields from the request DTO to the Course entity.
     * Used by updateCourse() to avoid setting each field manually.
     */
    private void applyRequestFields(Course course, AdminCourseUpsertRequest r) {
        course.setTenMH(r.name());
        course.setTenMH_EN(r.nameEn());
        course.setSoTC(defaultZero(r.credits()));
        course.setLt(defaultZero(r.lectureHours()));
        course.setTh(defaultZero(r.practiceHours()));
        course.setLoaiMonHoc(r.subjectType());
        course.setOpen(r.isOpen() != null ? r.isOpen() : true);
        course.setManagementUnit(r.managementUnit());
        course.setDescription(r.description());
        course.setMaMH_Old(r.codeOld());
        course.setEquivalentMH(r.equivalentMH());
        course.setPrerequisiteMH(r.prerequisiteMH());
        course.setPreviousMH(r.previousMH());
        course.setLearningObjectives(r.learningObjectives());
        course.setGradingCriteria(r.gradingCriteria());
        course.setTopics(r.topics());
    }

    /**
     * Convert a Course entity (database representation)
     * to a CourseDetailResponse DTO (API response representation).
     *
     * WHY DO WE NEED THIS?
     * The Course entity contains ALL database fields.
     * The DTO contains only the fields we want to SEND TO THE CLIENT.
     * This separation:
     * - Hides internal database details
     * - Lets us add calculated fields (like repos list)
     * - Decouples API contract from database schema
     */
    private CourseDetailResponse mapToDetail(Course course) {
        return new CourseDetailResponse(
                course.getId(), course.getMaMH(),
                course.getTenMH(), course.getTenMH_EN(),
                course.getDescription(),
                course.getLt(), course.getTh(), course.getSoTC(),
                course.getLoaiMonHoc(), course.isOpen(),
                course.getManagementUnit(), course.getMaMH_Old(),
                course.getEquivalentMH(), course.getPrerequisiteMH(),
                course.getPreviousMH(),
                course.getLearningObjectives(),
                course.getGradingCriteria(),
                course.getTopics(),
                githubRepoService.getApprovedReposByCourse(course.getId())
        );
    }
}
