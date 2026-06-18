package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * COURSE SESSION = one teaching session/lecture within a course.
 *
 * Maps to the "course_sessions" table.
 * Extracted from the course syllabus or imported from a knowledge source.
 * Each session describes what happens in ONE class meeting:
 * - Topic covered
 * - Activities during the session
 * - Any assessment component tied to this session
 *
 * sessionType distinguishes:
 *   THEORY  — lecture / theoretical content
 *   PRACTICE — lab work / hands-on exercises
 *
 * Part of the "Course Syllabus" knowledge model. Each session belongs
 * to one KnowledgeSource (the syllabus document it was extracted from).
 */
@Entity
@Table(name = "course_sessions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The course this session belongs to (e.g., "SE101"). */
    @Column(name = "course_code", nullable = false, length = 50)
    private String courseCode;

    /** The syllabus document this session was extracted from. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private KnowledgeSource source;

    /**
     * Session number within the course.
     * Examples: "1", "2", "Buổi 1", "Week 1"
     */
    @Column(name = "session_no", length = 50)
    private String sessionNo;

    /**
     * Type of session.
     * Values: "THEORY", "PRACTICE", "SEMINAR", etc.
     */
    @Column(name = "session_type", nullable = false, length = 50)
    private String sessionType;

    /** The topic/lesson content for this session. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String topic;

    /** What students will do during this session. */
    @Column(columnDefinition = "TEXT")
    private String activities;

    /**
     * Any graded component tied to this session.
     * Example: "Homework 1", "Lab Report"
     */
    @Column(name = "assessment_component", columnDefinition = "TEXT")
    private String assessmentComponent;
}
