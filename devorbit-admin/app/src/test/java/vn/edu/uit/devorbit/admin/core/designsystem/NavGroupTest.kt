package vn.edu.uit.devorbit.admin.core.designsystem

import org.junit.Assert.assertEquals
import org.junit.Test

import vn.edu.uit.devorbit.admin.ui.navigation.AdminScreen

class NavGroupTest {

    @Test
    fun primaryTabs_haveCorrectRoutes() {
        val tabs = AdminScreen.primaryTabs
        assertEquals(4, tabs.size)
        assertEquals("dashboard", tabs[0].route)
        assertEquals("students", tabs[1].route)
        assertEquals("courses", tabs[2].route)
        assertEquals("repos", tabs[3].route)
    }

    @Test
    fun secondaryScreens_haveCorrectCategories() {
        val screens = AdminScreen.secondaryScreens
        assertEquals(9, screens.size)
        screens.forEach { screen ->
            assert(screen.category in listOf("Quản lý", "Công cụ", "Hệ thống"))
        }
    }

    @Test
    fun allScreens_haveNonEmptyLabels() {
        val allScreens = AdminScreen.primaryTabs + AdminScreen.secondaryScreens
        allScreens.forEach { screen ->
            assert(screen.label.isNotBlank())
            assert(screen.route.isNotBlank())
        }
    }
}
