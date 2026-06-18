package vn.edu.uit.devorbit_api.entity;

/**
 * WHAT a roadmap recommendation item points to.
 *
 * When the AI generates a learning roadmap, each step is either:
 *
 * COURSE — "You should take this course"
 *          targetId = Course.id
 *          Example: "Start with SE101 - Intro to Programming"
 *
 * REPO   — "You should study this GitHub repository"
 *          targetId = GithubRepo.id
 *          Example: "Practice with this Spring Boot project"
 */
public enum RoadmapItemTargetType {
    COURSE,
    REPO
}
