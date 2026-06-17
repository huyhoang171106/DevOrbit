package vn.edu.uit.devorbit.mobile.ui.screen.repos

import org.junit.Assert.assertEquals
import org.junit.Test
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity

class ReposCourseRowModelTest {

    @Test
    fun `fromCourse formats compact course metadata`() {
        val model = ReposCourseRowModel.fromCourse(
            CourseEntity(
                id = 7L,
                maMH = "SE104",
                tenMH = "Nhap mon cong nghe phan mem",
                credits = 4,
                description = null
            )
        )

        assertEquals("SE104", model.code)
        assertEquals("Nhap mon cong nghe phan mem", model.name)
        assertEquals("4 TC", model.meta)
    }

    @Test
    fun `fromCourse hides zero credit metadata`() {
        val model = ReposCourseRowModel.fromCourse(
            CourseEntity(
                id = 8L,
                maMH = "ME001",
                tenMH = "Giao duc quoc phong",
                credits = 0,
                description = null
            )
        )

        assertEquals("", model.meta)
    }
}
