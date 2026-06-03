package vn.edu.uit.devorbit.mobile.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseRecommendationResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RoadmapRecommendationResponse

class StudyPlanMappersTest {

    @Test
    fun `RoadmapRecommendationResponse maps recommended courses into study plan`() {
        val response = RoadmapRecommendationResponse(
            summary = "Focus on mobile",
            recommendedCourses = listOf(
                CourseRecommendationResponse(
                    courseId = 12,
                    courseCode = "SE114",
                    courseName = "Mobile Development",
                    reasoning = "Build Android skills",
                    description = "Compose and Android",
                    isMandatory = false,
                    semester = 5,
                    credits = 3
                )
            )
        )

        val plan = response.toStudyPlan("Android developer")

        assertEquals("Android developer", plan.title)
        assertEquals(1, plan.phases.size)
        assertEquals("Hoc ky 5", plan.phases[0].title)
        assertEquals("SE114 - Mobile Development", plan.phases[0].items[0].title)
        assertEquals(12, plan.phases[0].items[0].subjectId)
        assertEquals(9.0, plan.totalHours, 0.01)
    }
}
