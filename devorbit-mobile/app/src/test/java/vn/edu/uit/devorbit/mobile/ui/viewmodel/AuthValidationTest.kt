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

    @Test
    fun `otp accepts only six numeric characters`() {
        assertEquals("123456", AuthValidation.sanitizeOtp("123456"))
        assertEquals("123456", AuthValidation.sanitizeOtp("12a34 56 extra"))
        assertEquals("123456", AuthValidation.sanitizeOtp("1234567"))
    }

    @Test
    fun `otp rejects values that are not six digits`() {
        assertEquals("Vui long nhap ma OTP gom 6 chu so", AuthValidation.otpError(""))
        assertEquals("Vui long nhap ma OTP gom 6 chu so", AuthValidation.otpError("12345"))
        assertEquals("Vui long nhap ma OTP gom 6 chu so", AuthValidation.otpError("abcdef"))
        assertNull(AuthValidation.otpError("123456"))
    }
}
