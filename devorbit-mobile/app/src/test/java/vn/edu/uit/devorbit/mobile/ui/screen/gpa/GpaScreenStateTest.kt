package vn.edu.uit.devorbit.mobile.ui.screen.gpa

import org.junit.Assert.assertEquals
import org.junit.Test

class GpaScreenStateTest {

    @Test
    fun `initial state starts with two course rows`() {
        val state = GpaScreenState.initial()

        assertEquals(2, state.courses.size)
        assertEquals("1", state.courses[0].id)
        assertEquals("2", state.courses[1].id)
    }

    @Test
    fun `addCourse appends a stable next row id`() {
        val state = GpaScreenState.initial().addCourse()

        assertEquals(3, state.courses.size)
        assertEquals("3", state.courses.last().id)
    }

    @Test
    fun `removeCourse keeps at least one row`() {
        val state = GpaScreenState.initial()
            .removeCourse("1")
            .removeCourse("2")

        assertEquals(1, state.courses.size)
    }

    @Test
    fun `updateCourse changes only selected row`() {
        val state = GpaScreenState.initial()
            .updateCourse("2") { it.copy(name = "Cau truc du lieu", credits = "3", grade = "8") }

        assertEquals("", state.courses[0].name)
        assertEquals("Cau truc du lieu", state.courses[1].name)
        assertEquals("3", state.courses[1].credits)
        assertEquals("8", state.courses[1].grade)
    }
}
