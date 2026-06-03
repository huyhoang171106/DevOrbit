package vn.edu.uit.devorbit.mobile.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidationTest {

    @Test
    fun `register rejects password shorter than six characters`() {
        val state = RegisterUiState(
            studentCode = "22520001",
            fullName = "Nguyen Van A",
            email = "a@uit.edu.vn",
            password = "12345",
            confirmPassword = "12345"
        )

        assertEquals("Mat khau phai co it nhat 6 ky tu", AuthValidation.registerError(state))
    }

    @Test
    fun `register accepts valid required fields`() {
        val state = RegisterUiState(
            studentCode = "22520001",
            fullName = "Nguyen Van A",
            email = "a@uit.edu.vn",
            password = "123456",
            confirmPassword = "123456"
        )

        assertNull(AuthValidation.registerError(state))
    }
}
