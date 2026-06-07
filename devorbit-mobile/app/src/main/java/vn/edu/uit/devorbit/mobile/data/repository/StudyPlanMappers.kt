package vn.edu.uit.devorbit.mobile.data.repository

import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseRecommendationResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RoadmapRecommendationResponse
import vn.edu.uit.devorbit.mobile.domain.model.StudyItem
import vn.edu.uit.devorbit.mobile.domain.model.StudyPhase
import vn.edu.uit.devorbit.mobile.domain.model.StudyPlan

internal fun RoadmapRecommendationResponse.toStudyPlan(title: String): StudyPlan {
    val courses = recommendedCourses.ifEmpty {
        electivePools.flatMap { pool -> pool.candidates.filter { it.isSelected } }
            .map {
                CourseRecommendationResponse(
                    courseId = it.courseId,
                    courseCode = it.courseCode,
                    courseName = it.courseName,
                    reasoning = it.reasoning,
                    description = it.description,
                    isMandatory = false,
                    semester = it.semester,
                    credits = it.credits
                )
            }
    }

    val phases = courses
        .groupBy { it.semester ?: 0 }
        .toSortedMap()
        .map { (semester, semesterCourses) ->
            val phaseTitle = if (semester > 0) "Hoc ky $semester" else "Goi y hoc tap"
            StudyPhase(
                title = phaseTitle,
                startDay = 1,
                endDay = 14,
                items = semesterCourses.mapIndexed { index, course ->
                    StudyItem(
                        id = listOfNotNull(course.courseCode, semester.toString(), index.toString()).joinToString("-"),
                        title = listOfNotNull(course.courseCode, course.courseName).joinToString(" - "),
                        subjectId = course.courseId ?: 0,
                        estimatedHours = (course.credits.coerceAtLeast(1) * 3).toDouble(),
                        difficulty = if (course.isMandatory) "hard" else "medium"
                    )
                }
            )
        }

    return StudyPlan(
        id = "roadmap-${System.currentTimeMillis()}",
        title = title,
        phases = phases,
        totalHours = phases.flatMap { it.items }.sumOf { it.estimatedHours }
    )
}
