package vn.edu.uit.devorbit_api.entity;

/**
 * TYPES OF RELATIONSHIPS between two courses.
 *
 * Used by CourseRelationship to define how courses connect
 * in the knowledge graph and prerequisite chain.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ PREREQUISITE  Course A MUST be completed before Course B    │
 * │               Example: SE101 → SE201 → SE301                │
 * │               → You CANNOT take SE201 without SE101 first   │
 * ├──────────────────────────────────────────────────────────────┤
 * │ COMPLEMENTARY Courses share knowledge but no order needed   │
 * │               Example: Discrete Math + Data Structures      │
 * │               → Can be taken in any semester                │
 * ├──────────────────────────────────────────────────────────────┤
 * │ COREQUISITE   Courses should be taken TOGETHER same term    │
 * │               Example: Graph Theory + Graph Theory Lab      │
 * │               → Theory reinforces practice in parallel      │
 * └──────────────────────────────────────────────────────────────┘
 *
 * Each course pair has ONE relationship type. The unique constraint
 * (course_id + related_course_id + relation_type) enforces this.
 */
public enum CourseRelationType {

    /** Course A is a PREREQUISITE for Course B. Must be completed first. */
    PREREQUISITE,

    /** Courses are RELATED but neither depends on the other. */
    COMPLEMENTARY,

    /** Courses should be taken CONCURRENTLY in the same semester. */
    COREQUISITE
}
