package vn.edu.uit.devorbit.admin.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val sessionChecked: Boolean = false
)

@HiltViewModel
class AdminLoginViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun updateUsername(value: String) {
        _state.value = _state.value.copy(username = value, error = null)
    }

    fun updatePassword(value: String) {
        _state.value = _state.value.copy(password = value, error = null)
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
                onSuccess = { _state.value = _state.value.copy(isLoading = false, isLoggedIn = true) },
                onFailure = {
                    _state.value = _state.value.copy(isLoading = false, error = it.message ?: "Đăng nhập thất bại")
                }
            )
        }
    }
    fun checkSession() {
        viewModelScope.launch {
            val token = adminRepository.getToken()
            _state.value = _state.value.copy(
                sessionChecked = true,
                isLoggedIn = !token.isNullOrBlank()
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
