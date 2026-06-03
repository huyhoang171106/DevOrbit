package vn.edu.uit.devorbit.mobile.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class SubjectQaStateTest {

    @Test
    fun `appendAnswer keeps session id and appends assistant message`() {
        val state = SubjectQaUiState(
            messages = listOf(SubjectQaMessage(role = "user", text = "SE101 la gi?")),
            sessionId = null
        )

        val next = state.appendAnswer(answer = "Day la mon nhap mon.", sessionId = "abc")

        assertEquals("abc", next.sessionId)
        assertEquals(2, next.messages.size)
        assertEquals("assistant", next.messages.last().role)
        assertEquals("Day la mon nhap mon.", next.messages.last().text)
    }
}
