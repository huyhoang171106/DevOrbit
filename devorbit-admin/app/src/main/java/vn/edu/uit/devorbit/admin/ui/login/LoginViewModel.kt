package vn.edu.uit.devorbit.admin.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import java.io.IOException
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val sessionChecked: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    init {
        checkSession()
    }

    fun updateUsername(value: String) {
        _state.value = _state.value.copy(username = value, error = null)
    }

    fun updatePassword(value: String) {
        _state.value = _state.value.copy(password = value, error = null)
    }

    fun togglePasswordVisibility() {
        _state.value = _state.value.copy(
            isPasswordVisible = !_state.value.isPasswordVisible,
        )
    }

    fun login() {
        val s = _state.value
        if (s.username.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(error = "Vui lòng nhập đầy đủ thông tin")
            return
        }
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.value = s.copy(isLoading = true, error = null)
            adminRepository.login(s.username, s.password).fold(
                onSuccess = {
                    _state.value = _state.value.copy(isLoading = false, isLoggedIn = true)
                },
                onFailure = { e ->
                    val errorMessage = when (e) {
                        is IOException -> "Không thể kết nối đến máy chủ. Vui lòng thử lại sau."
                        else -> "Tên đăng nhập hoặc mật khẩu không đúng"
                    }
                    _state.value = _state.value.copy(isLoading = false, error = errorMessage)
                },
            )
        }
    }

    fun checkSession() {
        viewModelScope.launch {
            val token = adminRepository.getToken()
            _state.value = _state.value.copy(
                sessionChecked = true,
                isLoggedIn = !token.isNullOrBlank(),
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            adminRepository.logout()
            _state.value = LoginUiState(sessionChecked = true)
        }
    }
}
