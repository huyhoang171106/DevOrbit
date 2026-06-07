package vn.edu.uit.devorbit.mobile.ui.screen.courses

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CourseHubNavigationStateTest {

    @Test
    fun `openCourse selects course and clears selected repo`() {
        val state = CourseHubNavigationState(selectedRepoId = 9L)

        val next = state.openCourse(4L)

        assertEquals(4L, next.selectedCourseId)
        assertNull(next.selectedRepoId)
    }

    @Test
    fun `openRepo keeps selected course and selects repo`() {
        val state = CourseHubNavigationState(selectedCourseId = 4L)

        val next = state.openRepo(12L)

        assertEquals(4L, next.selectedCourseId)
        assertEquals(12L, next.selectedRepoId)
    }

    @Test
    fun `back from repo returns to course detail`() {
        val state = CourseHubNavigationState(selectedCourseId = 4L, selectedRepoId = 12L)

        val next = state.back()

        assertEquals(4L, next.selectedCourseId)
        assertNull(next.selectedRepoId)
    }

    @Test
    fun `back from course detail returns to course list`() {
        val state = CourseHubNavigationState(selectedCourseId = 4L)

        val next = state.back()

        assertNull(next.selectedCourseId)
        assertNull(next.selectedRepoId)
    }
}
