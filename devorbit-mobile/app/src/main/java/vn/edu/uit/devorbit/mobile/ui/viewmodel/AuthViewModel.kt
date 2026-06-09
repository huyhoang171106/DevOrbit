package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.domain.repository.AuthRepository
import javax.inject.Inject

data class AuthUiState(
    val studentCode: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

data class RegisterUiState(
    val studentCode: String = "",
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val otpCode: String = "",
    val registeredEmail: String = "",
    val isAwaitingOtp: Boolean = false,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _loginState = MutableStateFlow(AuthUiState())
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _isCheckingAuth = MutableStateFlow(true)
    val isCheckingAuth: StateFlow<Boolean> = _isCheckingAuth.asStateFlow()

    init {
        observeAuthToken()
    }

    private fun observeAuthToken() {
        viewModelScope.launch {
            settingsDataStore.token.collect { token ->
                val loggedIn = !token.isNullOrBlank()
                _loginState.value = _loginState.value.copy(isLoggedIn = loggedIn)
                if (_isCheckingAuth.value) {
                    _isCheckingAuth.value = false
                }
            }
        }
    }

    fun updateStudentCode(code: String) {
        _loginState.value = _loginState.value.copy(studentCode = code, error = null)
    }

    fun updatePassword(pw: String) {
        _loginState.value = _loginState.value.copy(password = pw, error = null)
    }

    fun login() {
        val state = _loginState.value
        if (state.studentCode.isBlank() || state.password.isBlank()) {
            _loginState.value = _loginState.value.copy(error = "Vui long nhap day du MSSV va mat khau")
            return
        }
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, error = null)
            authRepository.login(state.studentCode, state.password)
                .onSuccess {
                    _loginState.value = _loginState.value.copy(isLoading = false, isLoggedIn = true)
                }
                .onFailure { e ->
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Dang nhap that bai"
                    )
                }
        }
    }

    fun updateRegisterField(field: String, value: String) {
        _registerState.value = when (field) {
            "studentCode" -> _registerState.value.copy(studentCode = value, error = null, message = null)
            "fullName" -> _registerState.value.copy(fullName = value, error = null, message = null)
            "email" -> _registerState.value.copy(email = value, error = null, message = null)
            "password" -> _registerState.value.copy(password = value, error = null, message = null)
            "confirmPassword" -> _registerState.value.copy(confirmPassword = value, error = null, message = null)
            "otpCode" -> _registerState.value.copy(otpCode = value, error = null)
            else -> _registerState.value
        }
    }

    fun register() {
        val state = _registerState.value
        val validationError = AuthValidation.registerError(state)
        if (validationError != null) {
            _registerState.value = _registerState.value.copy(error = validationError)
            return
        }
        viewModelScope.launch {
            _registerState.value = _registerState.value.copy(isLoading = true, error = null, message = null)
            authRepository.register(
                studentCode = state.studentCode,
                fullName = state.fullName,
                email = state.email,
                password = state.password
            ).onSuccess {
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    isAwaitingOtp = true,
                    registeredEmail = it.email,
                    isSuccess = true
                )
                _loginState.value = _loginState.value.copy(
                    isLoggedIn = AuthSessionPolicy.isAuthenticatedAfterRegister()
                )
            }.onFailure { e ->
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Dang ky that bai"
                )
            }
        }
    }

    fun verifyOtp() {
        val state = _registerState.value
        val email = state.registeredEmail.ifBlank { state.email }
        if (email.isBlank() || state.otpCode.isBlank()) {
            _registerState.value = state.copy(error = "Vui long nhap ma OTP")
            return
        }
        viewModelScope.launch {
            _registerState.value = state.copy(isLoading = true, error = null)
            authRepository.verifyOtp(email, state.otpCode)
                .onSuccess { result ->
                    _registerState.value = _registerState.value.copy(isLoading = false)
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        isLoggedIn = AuthSessionPolicy.isAuthenticatedWithToken(result.token)
                    )
                }
                .onFailure { e ->
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Xac thuc OTP that bai"
                    )
                }
        }
    }

    fun resendOtp() {
        val state = _registerState.value
        val email = state.registeredEmail.ifBlank { state.email }
        if (email.isBlank()) {
            _registerState.value = state.copy(error = "Khong tim thay email de gui lai OTP", message = null)
            return
        }
        viewModelScope.launch {
            _registerState.value = state.copy(isLoading = true, error = null, message = null)
            authRepository.resendOtp(email)
                .onSuccess {
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        message = "Da gui lai ma OTP"
                    )
                }
                .onFailure { e ->
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Gui lai OTP that bai"
                    )
                }
        }
    }

    fun switchToRegister() {
        _registerState.value = RegisterUiState()
    }

    fun switchToLogin() {
        _loginState.value = _loginState.value.copy(error = null)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _loginState.value = AuthUiState(isLoggedIn = false)
            _registerState.value = RegisterUiState()
        }
    }
}
