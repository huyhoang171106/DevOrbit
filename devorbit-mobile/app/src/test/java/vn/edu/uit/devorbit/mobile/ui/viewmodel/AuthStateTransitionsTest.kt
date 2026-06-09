package vn.edu.uit.devorbit.mobile.ui.viewmodel

import org.junit.Assert.assertNull
import org.junit.Test

class AuthStateTransitionsTest {

    @Test
    fun `switching to login clears register error and message`() {
        val state = RegisterUiState(
            isAwaitingOtp = true,
            error = "Invalid OTP",
            message = "Da gui lai ma OTP"
        )

        val next = AuthStateTransitions.registerStateAfterSwitchToLogin(state)

        assertNull(next.error)
        assertNull(next.message)
    }
}
