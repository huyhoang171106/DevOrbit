package vn.edu.uit.devorbit.mobile.ui.screen.repos

import org.junit.Assert.assertEquals
import org.junit.Test
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity

class ReposCourseCatalogTest {

    @Test
    fun `visibleCourses keeps only courses with repos and sorts like web table`() {
        val courses = listOf(
            course(id = 1L, code = "MA006", repoCount = 0),
            course(id = 2L, code = "SE104", repoCount = 6),
            course(id = 3L, code = "IT001", repoCount = 12),
            course(id = 4L, code = "SS004", repoCount = 1)
        )

        val visible = ReposCourseCatalog.visibleCourses(courses)

        assertEquals(listOf("IT001", "SE104", "SS004"), visible.map { it.maMH })
    }

    private fun course(id: Long, code: String, repoCount: Int): CourseEntity {
        return CourseEntity(
            id = id,
            maMH = code,
            tenMH = "Môn $code",
            credits = 4,
            repoCount = repoCount,
            description = null
        )
    }
}
