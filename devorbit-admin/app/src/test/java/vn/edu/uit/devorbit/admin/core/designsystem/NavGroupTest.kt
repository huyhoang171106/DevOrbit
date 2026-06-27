package vn.edu.uit.devorbit.admin.core.designsystem

import org.junit.Assert.assertEquals
import org.junit.Test

class NavGroupTest {

    @Test
    fun items_areAvailableAfterFirstGroupInitialization() {
        val initialGroup: NavGroup = NavGroup.Command

        assertEquals("command", initialGroup.route)
        assertEquals(
            listOf("command", "content", "moderation", "users", "more"),
            navGroupItems.map { it.route },
        )
    }
}
