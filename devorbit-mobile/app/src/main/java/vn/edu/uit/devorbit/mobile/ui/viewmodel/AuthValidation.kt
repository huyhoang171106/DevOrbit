package vn.edu.uit.devorbit.mobile.ui.viewmodel

object AuthValidation {
    private const val OtpLength = 6

    fun registerError(state: RegisterUiState): String? {
        if (state.studentCode.isBlank() || state.fullName.isBlank() || state.email.isBlank()) {
            return "Vui long nhap day du thong tin"
        }
        if (state.password.length < 6) {
            return "Mat khau phai co it nhat 6 ky tu"
        }
        if (state.password != state.confirmPassword) {
            return "Mat khau xac nhan khong khop"
        }
        return null
    }

    fun sanitizeOtp(value: String): String = value.filter(Char::isDigit).take(OtpLength)

    fun otpError(value: String): String? {
        return if (sanitizeOtp(value).length == OtpLength) {
            null
        } else {
            "Vui long nhap ma OTP gom 6 chu so"
        }
    }
}
