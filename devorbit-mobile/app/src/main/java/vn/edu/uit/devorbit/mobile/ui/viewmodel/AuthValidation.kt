package vn.edu.uit.devorbit.mobile.ui.viewmodel

object AuthValidation {
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
}
