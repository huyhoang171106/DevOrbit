package vn.edu.uit.devorbit.mobile.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenTest {

    @Test
    fun `bottom navigation uses subjects gpa and profile without explore`() {
        val labels = Screen.navItems.map { it.label }

        assertEquals(
            listOf("Tổng quan", "Môn học", "Kế hoạch", "GPA", "Cá nhân"),
            labels
        )
        assertTrue(Screen.navItems.any { it.route == "repos" })
        assertTrue(Screen.navItems.any { it.route == "gpa" })
        assertFalse(Screen.navItems.any { it.route == "explore" })
        assertFalse(Screen.navItems.any { it.route == "courses" })
        assertFalse(Screen.navItems.any { it.route == "knowledge" })
    }
}
