package vn.edu.uit.devorbit_api.entity;

/**
 * How two courses relate to each other in the knowledge graph.
 *
 * PREREQUISITE  — Course A must be completed BEFORE Course B
 *                  (e.g., SE101 → SE201 → SE301)
 * COMPLEMENTARY — Courses are related but neither depends on the other
 *                  (e.g., they share topics but can be taken in any order)
 * COREQUISITE   — Courses should be taken IN THE SAME semester
 *                  (they complement each other during parallel study)
 */
public enum CourseRelationType {
    PREREQUISITE,
    COMPLEMENTARY,
    COREQUISITE
}
