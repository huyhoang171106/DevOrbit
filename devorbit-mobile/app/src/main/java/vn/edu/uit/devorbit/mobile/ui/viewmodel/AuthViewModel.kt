package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.domain.repository.AuthRepository
import javax.inject.Inject

enum class AuthMode { LOGIN, REGISTER, FORGOT }

enum class ForgotStep { SEND_OTP, RESET }

data class AuthUiState(
    val studentCode: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
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
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val otpDigits: List<String> = List(6) { "" },
    val registeredEmail: String = "",
    val isAwaitingOtp: Boolean = false,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val otpCountdown: Int = 0,
    val error: String? = null
)

data class ForgotPasswordUiState(
    val step: ForgotStep = ForgotStep.SEND_OTP,
    val studentCode: String = "",
    val otpDigits: List<String> = List(6) { "" },
    val newPassword: String = "",
    val confirmPassword: String = "",
    val newPasswordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val countdown: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _authMode = MutableStateFlow(AuthMode.LOGIN)
    val authMode: StateFlow<AuthMode> = _authMode.asStateFlow()

    private val _loginState = MutableStateFlow(AuthUiState())
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _forgotState = MutableStateFlow(ForgotPasswordUiState())
    val forgotState: StateFlow<ForgotPasswordUiState> = _forgotState.asStateFlow()

    private val _isCheckingAuth = MutableStateFlow(true)
    val isCheckingAuth: StateFlow<Boolean> = _isCheckingAuth.asStateFlow()

    private var countdownJob: Job? = null

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

    // ── Login ──

    fun updateStudentCode(code: String) {
        _loginState.value = _loginState.value.copy(studentCode = code, error = null)
    }

    fun updatePassword(pw: String) {
        _loginState.value = _loginState.value.copy(password = pw, error = null)
    }

    fun togglePasswordVisibility() {
        _loginState.value = _loginState.value.copy(passwordVisible = !_loginState.value.passwordVisible)
    }

    fun login() {
        val state = _loginState.value
        if (state.studentCode.isBlank() || state.password.isBlank()) {
            _loginState.value = _loginState.value.copy(error = "Vui lòng nhập đầy đủ MSSV và mật khẩu")
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
                        error = e.message ?: "Đăng nhập thất bại"
                    )
                }
        }
    }

    // ── Register ──

    fun updateRegisterField(field: String, value: String) {
        _registerState.value = when (field) {
            "studentCode" -> _registerState.value.copy(studentCode = value, error = null)
            "fullName" -> _registerState.value.copy(fullName = value, error = null)
            "email" -> _registerState.value.copy(email = value, error = null)
            "password" -> _registerState.value.copy(password = value, error = null)
            "confirmPassword" -> _registerState.value.copy(confirmPassword = value, error = null)
            else -> _registerState.value
        }
    }

    fun toggleRegisterPasswordVisibility() {
        _registerState.value = _registerState.value.copy(passwordVisible = !_registerState.value.passwordVisible)
    }

    fun toggleRegisterConfirmPasswordVisibility() {
        _registerState.value = _registerState.value.copy(confirmPasswordVisible = !_registerState.value.confirmPasswordVisible)
    }

    fun updateRegisterOtpDigit(index: Int, digit: String) {
        val current = _registerState.value.otpDigits.toMutableList()
        if (index in 0..5) {
            val clean = digit.takeLast(1).filter { it.isDigit() }
            current[index] = clean
            _registerState.value = _registerState.value.copy(otpDigits = current, error = null)
        }
    }

    fun register() {
        val state = _registerState.value
        val error = when {
            state.studentCode.isBlank() -> "Vui lòng nhập MSSV"
            state.fullName.isBlank() -> "Vui lòng nhập họ và tên"
            state.email.isBlank() -> "Vui lòng nhập email"
            !state.email.contains("@") -> "Email không hợp lệ"
            state.password.isBlank() -> "Vui lòng nhập mật khẩu"
            state.password.length < 6 -> "Mật khẩu phải có ít nhất 6 ký tự"
            state.password != state.confirmPassword -> "Mật khẩu xác nhận không khớp"
            else -> null
        }
        if (error != null) {
            _registerState.value = _registerState.value.copy(error = error)
            return
        }
        viewModelScope.launch {
            _registerState.value = _registerState.value.copy(isLoading = true, error = null)
            authRepository.register(
                studentCode = state.studentCode,
                fullName = state.fullName,
                email = state.email,
                password = state.password
            ).onSuccess {
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    isAwaitingOtp = true,
                    registeredEmail = it.email
                )
                startRegisterOtpCountdown()
            }.onFailure { e ->
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Đăng ký thất bại"
                )
            }
        }
    }

    fun verifyOtp() {
        val state = _registerState.value
        val email = state.registeredEmail.ifBlank { state.email }
        val otpCode = state.otpDigits.joinToString("")
        if (email.isBlank() || otpCode.length < 6) {
            _registerState.value = state.copy(error = "Vui lòng nhập đầy đủ mã OTP")
            return
        }
        viewModelScope.launch {
            _registerState.value = state.copy(isLoading = true, error = null)
            authRepository.verifyOtp(email, otpCode)
                .onSuccess {
                    countdownJob?.cancel()
                    _registerState.value = _registerState.value.copy(isLoading = false)
                    _loginState.value = _loginState.value.copy(isLoading = false, isLoggedIn = true)
                }
                .onFailure { e ->
                    _registerState.value = _registerState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Xác thực OTP thất bại"
                    )
                }
        }
    }

    fun resendOtpForRegister() {
        val email = _registerState.value.registeredEmail.ifBlank { _registerState.value.email }
        if (email.isBlank()) return
        viewModelScope.launch {
            authRepository.resendOtp(email, "registration")
                .onSuccess { startRegisterOtpCountdown() }
                .onFailure { e ->
                    _registerState.value = _registerState.value.copy(
                        error = e.message ?: "Gửi lại mã OTP thất bại"
                    )
                }
        }
    }

    private fun startRegisterOtpCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var sec = 60
            _registerState.value = _registerState.value.copy(otpCountdown = sec)
            while (sec > 0) {
                delay(1000)
                sec--
                _registerState.value = _registerState.value.copy(otpCountdown = sec)
            }
        }
    }

    // ── Forgot Password ──

    fun updateForgotStudentCode(code: String) {
        _forgotState.value = _forgotState.value.copy(studentCode = code, error = null)
    }

    fun updateForgotOtpDigit(index: Int, digit: String) {
        val current = _forgotState.value.otpDigits.toMutableList()
        if (index in 0..5) {
            val clean = digit.takeLast(1).filter { it.isDigit() }
            current[index] = clean
            _forgotState.value = _forgotState.value.copy(otpDigits = current, error = null)
        }
    }

    fun updateForgotNewPassword(pw: String) {
        _forgotState.value = _forgotState.value.copy(newPassword = pw, error = null)
    }

    fun updateForgotConfirmPassword(pw: String) {
        _forgotState.value = _forgotState.value.copy(confirmPassword = pw, error = null)
    }

    fun toggleForgotNewPasswordVisibility() {
        _forgotState.value = _forgotState.value.copy(newPasswordVisible = !_forgotState.value.newPasswordVisible)
    }

    fun toggleForgotConfirmPasswordVisibility() {
        _forgotState.value = _forgotState.value.copy(confirmPasswordVisible = !_forgotState.value.confirmPasswordVisible)
    }

    fun forgotPassword() {
        val code = _forgotState.value.studentCode
        if (code.isBlank()) {
            _forgotState.value = _forgotState.value.copy(error = "Vui lòng nhập MSSV")
            return
        }
        viewModelScope.launch {
            _forgotState.value = _forgotState.value.copy(isLoading = true, error = null)
            authRepository.forgotPassword(code)
                .onSuccess {
                    _forgotState.value = _forgotState.value.copy(
                        isLoading = false,
                        step = ForgotStep.RESET
                    )
                    startForgotOtpCountdown()
                }
                .onFailure { e ->
                    _forgotState.value = _forgotState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Gửi yêu cầu thất bại"
                    )
                }
        }
    }

    fun resetPassword() {
        val state = _forgotState.value
        val otpCode = state.otpDigits.joinToString("")
        val error = when {
            otpCode.length < 6 -> "Vui lòng nhập đầy đủ mã OTP"
            state.newPassword.isBlank() -> "Vui lòng nhập mật khẩu mới"
            state.newPassword.length < 6 -> "Mật khẩu phải có ít nhất 6 ký tự"
            state.newPassword != state.confirmPassword -> "Mật khẩu xác nhận không khớp"
            else -> null
        }
        if (error != null) {
            _forgotState.value = state.copy(error = error)
            return
        }
        viewModelScope.launch {
            _forgotState.value = state.copy(isLoading = true, error = null)
            authRepository.resetPassword(state.studentCode, otpCode, state.newPassword)
                .onSuccess {
                    countdownJob?.cancel()
                    _forgotState.value = _forgotState.value.copy(isLoading = false)
                    _loginState.value = _loginState.value.copy(isLoading = false, isLoggedIn = true)
                }
                .onFailure { e ->
                    _forgotState.value = _forgotState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Đặt lại mật khẩu thất bại"
                    )
                }
        }
    }

    fun resendOtpForForgot() {
        val code = _forgotState.value.studentCode
        if (code.isBlank()) return
        viewModelScope.launch {
            authRepository.resendOtp(code, "PASSWORD_RESET")
                .onSuccess { startForgotOtpCountdown() }
                .onFailure { e ->
                    _forgotState.value = _forgotState.value.copy(
                        error = e.message ?: "Gửi lại mã OTP thất bại"
                    )
                }
        }
    }

    private fun startForgotOtpCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var sec = 60
            _forgotState.value = _forgotState.value.copy(countdown = sec)
            while (sec > 0) {
                delay(1000)
                sec--
                _forgotState.value = _forgotState.value.copy(countdown = sec)
            }
        }
    }

    // ── Mode Switching ──

    fun switchMode(mode: AuthMode) {
        countdownJob?.cancel()
        _authMode.value = mode
        _loginState.value = _loginState.value.copy(error = null)
        _registerState.value = RegisterUiState()
        _forgotState.value = ForgotPasswordUiState()
    }

    fun switchToRegister() {
        switchMode(AuthMode.REGISTER)
    }

    fun switchToLogin() {
        switchMode(AuthMode.LOGIN)
    }

    fun switchToForgot() {
        switchMode(AuthMode.FORGOT)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _loginState.value = AuthUiState(isLoggedIn = false)
            _registerState.value = RegisterUiState()
            _forgotState.value = ForgotPasswordUiState()
            _authMode.value = AuthMode.LOGIN
        }
    }
}
