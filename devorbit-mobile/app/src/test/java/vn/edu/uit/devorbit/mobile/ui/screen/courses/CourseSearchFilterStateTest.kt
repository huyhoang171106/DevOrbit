package vn.edu.uit.devorbit.mobile.ui.screen.courses

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CourseSearchFilterStateTest {

    @Test
    fun `normalizedQuery trims blank search text`() {
        assertNull(CourseSearchFilterState(query = "   ").normalizedQuery)
        assertEquals("SE101", CourseSearchFilterState(query = "  SE101 ").normalizedQuery)
    }

    @Test
    fun `selectSubjectType toggles active type off`() {
        val state = CourseSearchFilterState(subjectType = "DAI_CUONG")

        assertNull(state.selectSubjectType("DAI_CUONG").subjectType)
        assertEquals("CO_SO", state.selectSubjectType("CO_SO").subjectType)
    }
}
