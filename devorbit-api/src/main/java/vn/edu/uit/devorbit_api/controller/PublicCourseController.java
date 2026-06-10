package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseDetailResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.KnowledgeGraphResponse;
import vn.edu.uit.devorbit_api.service.CourseService;
import vn.edu.uit.devorbit_api.service.CourseTutorialService;
import vn.edu.uit.devorbit_api.service.CourseYoutubePlaylistService;
import vn.edu.uit.devorbit_api.service.CourseArticleService;
import vn.edu.uit.devorbit_api.service.KnowledgeGraphService;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * PUBLIC COURSE CONTROLLER = the HTTP layer for course-related endpoints
 * that ANYONE can access (no authentication required).
 *
 * WHAT IS A CONTROLLER?
 * A Controller is the ENTRY POINT for HTTP requests. It:
 * 1. Receives HTTP requests from the browser/mobile app
 * 2. Routes them to the right Service method
 * 3. Returns the response (Spring auto-serializes it to JSON)
 *
 * ENDPOINT CONVENTION:
 * /api/courses       — public endpoints (this controller)
 * /api/admin/courses — admin-only endpoints (AdminCourseController)
 *
 * REQUEST FLOW EXAMPLE (GET /api/courses):
 *   1. Browser sends GET /api/courses
 *   2. Spring maps it to getCourses() method
 *   3. Controller calls courseService.getActiveCourseSummaries()
 *   4. Service talks to Repository
 *   5. Repository runs database query
 *   6. Response flows back to browser as JSON
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class PublicCourseController {
    private final CourseService courseService;
    private final CourseTutorialService tutorialService;
    private final CourseYoutubePlaylistService playlistService;
    private final CourseArticleService articleService;
    private final KnowledgeGraphService knowledgeGraphService;

    /**
     * GET /api/courses
     * Returns a LIST of all courses (basic info: id, code, name, repo count).
     * Supports optional search/filter query parameters:
     *   ?q=keyword      — filter by name or code (case-insensitive)
     *   ?subjectType=X  — filter by subject type (DAI_CUONG, CHUYEN_NGANH, CO_SO, etc.)
     *   ?semester=N     — filter by semester
     *   ?managementUnit=X — filter by management unit (CNPM, HTTT, etc.)
     * Results are sorted by repo count (most repos first).
     */
    @GetMapping
    public List<CourseSummaryResponse> getCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String subjectType,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String managementUnit) {
        boolean hasFilters = q != null || subjectType != null || semester != null || managementUnit != null;
        if (!hasFilters) {
            return courseService.getActiveCourseSummaries();
        }
        return courseService.searchCourses(q, subjectType, semester, managementUnit);
    }

    /**
     * GET /api/courses/graph
     * Returns the KNOWLEDGE GRAPH — a visual map of how courses relate.
     * Shows:
     *   - Nodes: each course (with position, size, semester info)
     *   - Links: relationships like "Prerequisite" or "Complementary"
     * Used by the interactive knowledge graph visualization on the frontend.
     */
    @GetMapping("/graph")
    public KnowledgeGraphResponse getGraph() {
        log.info("Fetching knowledge graph data...");
        KnowledgeGraphResponse response = knowledgeGraphService.getGraph();
        log.info("Knowledge graph data fetched: {} nodes, {} links",
            response.nodes().size(), response.links().size());
        return response;
    }

    /**
     * GET /api/courses/{id}
     * Returns DETAILED info about a single course, including:
     * - Full course metadata
     * - A list of approved GitHub repos linked to this course
     *
     * Path variable {id} is the course's primary key (database ID).
     * Example: /api/courses/1 returns course with id=1
     */
    @GetMapping("/{id}")
    public CourseDetailResponse getCourseDetail(@PathVariable Long id) {
        return courseService.getCourseDetail(id);
    }

    /**
     * GET /api/courses/{id}/tutorials
     * Returns tutorials (written guides) linked to this course.
     */
    @GetMapping("/{id}/tutorials")
    public List<?> getTutorials(@PathVariable Long id) {
        return tutorialService.getByCourse(id);
    }

    /**
     * GET /api/courses/{id}/videos
     * Returns YouTube playlists/videos linked to this course.
     */
    @GetMapping("/{id}/videos")
    public List<?> getVideos(@PathVariable Long id) {
        return playlistService.getByCourse(id);
    }

    /**
     * GET /api/courses/{id}/articles
     * Returns articles/references linked to this course.
     */
    @GetMapping("/{id}/articles")
    public List<?> getArticles(@PathVariable Long id) {
        return articleService.getByCourse(id);
    }
}
