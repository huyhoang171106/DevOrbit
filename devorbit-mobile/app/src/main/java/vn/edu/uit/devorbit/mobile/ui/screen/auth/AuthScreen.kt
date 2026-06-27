package vn.edu.uit.devorbit.mobile.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.ui.components.DevOrbitLogo
import vn.edu.uit.devorbit.mobile.ui.components.DevOrbitMark
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthMode
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthUiState
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ForgotPasswordUiState
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ForgotStep
import vn.edu.uit.devorbit.mobile.ui.viewmodel.RegisterUiState

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoggedIn: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    val forgotState by viewModel.forgotState.collectAsStateWithLifecycle()
    val authMode by viewModel.authMode.collectAsStateWithLifecycle()
    val isChecking by viewModel.isCheckingAuth.collectAsStateWithLifecycle()

    LaunchedEffect(loginState.isLoggedIn) {
        if (loginState.isLoggedIn) onLoggedIn()
    }

    if (isChecking) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CosmicTheme.colors.plasma)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthCard {
            when (authMode) {
                AuthMode.LOGIN -> LoginForm(
                    state = loginState,
                    onStudentCodeChange = viewModel::updateStudentCode,
                    onPasswordChange = viewModel::updatePassword,
                    onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                    onLogin = viewModel::login,
                    onSwitchToRegister = viewModel::switchToRegister,
                    onSwitchToForgot = viewModel::switchToForgot
                )
                AuthMode.REGISTER -> {
                    if (registerState.isAwaitingOtp) {
                        RegisterOtpForm(
                            state = registerState,
                            onDigitChange = viewModel::updateRegisterOtpDigit,
                            onVerify = viewModel::verifyOtp,
                            onResend = viewModel::resendOtpForRegister,
                            onBack = viewModel::switchToLogin
                        )
                    } else if (registerState.isSuccess) {
                        RegisterSuccess(onSwitchToLogin = viewModel::switchToLogin)
                    } else {
                        RegisterForm(
                            state = registerState,
                            onFieldChange = viewModel::updateRegisterField,
                            onTogglePasswordVisibility = viewModel::toggleRegisterPasswordVisibility,
                            onToggleConfirmPasswordVisibility = viewModel::toggleRegisterConfirmPasswordVisibility,
                            onRegister = viewModel::register,
                            onSwitchToLogin = viewModel::switchToLogin
                        )
                    }
                }
                AuthMode.FORGOT -> {
                    if (forgotState.step == ForgotStep.SEND_OTP) {
                        ForgotForm(
                            state = forgotState,
                            onStudentCodeChange = viewModel::updateForgotStudentCode,
                            onSendOtp = viewModel::forgotPassword,
                            onBack = viewModel::switchToLogin
                        )
                    } else {
                        ResetForm(
                            state = forgotState,
                            onDigitChange = viewModel::updateForgotOtpDigit,
                            onNewPasswordChange = viewModel::updateForgotNewPassword,
                            onConfirmPasswordChange = viewModel::updateForgotConfirmPassword,
                            onToggleNewPasswordVisibility = viewModel::toggleForgotNewPasswordVisibility,
                            onToggleConfirmPasswordVisibility = viewModel::toggleForgotConfirmPasswordVisibility,
                            onReset = viewModel::resetPassword,
                            onResend = viewModel::resendOtpForForgot,
                            onBack = viewModel::switchToLogin
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CosmicTheme.colors.nebula.copy(alpha = 0.7f))
            .border(1.dp, CosmicTheme.colors.glassBorder, RoundedCornerShape(16.dp))
            .padding(28.dp)
    ) {
        content()
    }
}

// ── Login ──

@Composable
private fun LoginForm(
    state: AuthUiState,
    onStudentCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLogin: () -> Unit,
    onSwitchToRegister: () -> Unit,
    onSwitchToForgot: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DevOrbitLogo(withText = true)
        Spacer(modifier = Modifier.height(16.dp))
        Heading("Đăng nhập")
        Spacer(modifier = Modifier.height(24.dp))

        state.error?.let { ErrorBanner(it) }

        FormField(
            value = state.studentCode,
            onValueChange = onStudentCodeChange,
            label = "Tên đăng nhập",
            imeAction = ImeAction.Next
        )
        Spacer(modifier = Modifier.height(14.dp))

        PasswordField(
            value = state.password,
            onValueChange = onPasswordChange,
            visible = state.passwordVisible,
            onToggleVisibility = onTogglePasswordVisibility,
            onDone = onLogin
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLogin,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = CosmicTheme.colors.void,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Đăng nhập", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Quên mật khẩu?",
            color = CosmicTheme.colors.plasma,
            style = CosmicTheme.typography.body,
            modifier = Modifier.clickable(enabled = !state.isLoading) { onSwitchToForgot() }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = CosmicTheme.colors.glassBorder, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Chưa có tài khoản?",
                color = CosmicTheme.colors.textSecondary,
                style = CosmicTheme.typography.body
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Đăng ký",
                color = CosmicTheme.colors.plasma,
                fontWeight = FontWeight.Medium,
                style = CosmicTheme.typography.body,
                modifier = Modifier.clickable(enabled = !state.isLoading) { onSwitchToRegister() }
            )
        }
    }
}

// ── Register ──

@Composable
private fun RegisterForm(
    state: RegisterUiState,
    onFieldChange: (String, String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onRegister: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DevOrbitMark()
        Spacer(modifier = Modifier.height(16.dp))
        Heading("Tạo tài khoản")
        Spacer(modifier = Modifier.height(24.dp))

        state.error?.let { ErrorBanner(it) }

        FormField(
            value = state.studentCode,
            onValueChange = { onFieldChange("studentCode", it) },
            label = "Tên đăng nhập",
            imeAction = ImeAction.Next
        )
        Spacer(modifier = Modifier.height(14.dp))

        FormField(
            value = state.fullName,
            onValueChange = { onFieldChange("fullName", it) },
            label = "Họ và tên",
            imeAction = ImeAction.Next
        )
        Spacer(modifier = Modifier.height(14.dp))

        FormField(
            value = state.email,
            onValueChange = { onFieldChange("email", it) },
            label = "Email",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        )
        Spacer(modifier = Modifier.height(14.dp))

        PasswordField(
            value = state.password,
            onValueChange = { onFieldChange("password", it) },
            visible = state.passwordVisible,
            onToggleVisibility = onTogglePasswordVisibility,
            onDone = { focusManager.moveFocus(FocusDirection.Down) }
        )
        Spacer(modifier = Modifier.height(14.dp))

        PasswordField(
            value = state.confirmPassword,
            onValueChange = { onFieldChange("confirmPassword", it) },
            visible = state.confirmPasswordVisible,
            onToggleVisibility = onToggleConfirmPasswordVisibility,
            label = "Nhập lại mật khẩu",
            onDone = onRegister
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRegister,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = CosmicTheme.colors.void,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Đăng ký", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = CosmicTheme.colors.glassBorder, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Đã có tài khoản?",
                color = CosmicTheme.colors.textSecondary,
                style = CosmicTheme.typography.body
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Đăng nhập",
                color = CosmicTheme.colors.plasma,
                fontWeight = FontWeight.Medium,
                style = CosmicTheme.typography.body,
                modifier = Modifier.clickable(enabled = !state.isLoading) { onSwitchToLogin() }
            )
        }
    }
}

// ── Register OTP ──

@Composable
private fun RegisterOtpForm(
    state: RegisterUiState,
    onDigitChange: (Int, String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HeaderIcon(Icons.Filled.Lock)
        Spacer(modifier = Modifier.height(16.dp))
        Heading("Xác thực email")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Nhập mã OTP đã gửi đến ${state.registeredEmail.ifBlank { state.email }}",
            color = CosmicTheme.colors.textSecondary,
            style = CosmicTheme.typography.body,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        state.error?.let { ErrorBanner(it) }

        OtpRow(
            digits = state.otpDigits,
            onDigitChange = onDigitChange,
            onComplete = onVerify
        )
        Spacer(modifier = Modifier.height(16.dp))

        CountdownOrResend(
            countdown = state.otpCountdown,
            onResend = onResend
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerify,
            enabled = !state.isLoading && state.otpDigits.joinToString("").length == 6,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = CosmicTheme.colors.void,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Xác thực", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BackToLoginLink(onClick = onBack)
    }
}

// ── Forgot Password ──

@Composable
private fun ForgotForm(
    state: ForgotPasswordUiState,
    onStudentCodeChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HeaderIcon(Icons.Filled.Lock)
        Spacer(modifier = Modifier.height(16.dp))
        Heading("Quên mật khẩu")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Nhập tên đăng nhập để nhận mã OTP qua email",
            color = CosmicTheme.colors.textSecondary,
            style = CosmicTheme.typography.body,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        state.error?.let { ErrorBanner(it) }

        FormField(
            value = state.studentCode,
            onValueChange = onStudentCodeChange,
            label = "Tên đăng nhập",
            imeAction = ImeAction.Done,
            onDone = { focusManager.clearFocus(); onSendOtp() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onSendOtp,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = CosmicTheme.colors.void,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Gửi mã OTP", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BackToLoginLink(onClick = onBack)
    }
}

// ── Reset Password ──

@Composable
private fun ResetForm(
    state: ForgotPasswordUiState,
    onDigitChange: (Int, String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onReset: () -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HeaderIcon(Icons.Filled.Lock)
        Spacer(modifier = Modifier.height(16.dp))
        Heading("Đặt lại mật khẩu")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Nhập mã OTP và mật khẩu mới",
            color = CosmicTheme.colors.textSecondary,
            style = CosmicTheme.typography.body,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        state.error?.let { ErrorBanner(it) }

        Text(
            text = "MÃ OTP",
            color = CosmicTheme.colors.textSecondary,
            style = CosmicTheme.typography.label
        )
        Spacer(modifier = Modifier.height(8.dp))
        OtpRow(
            digits = state.otpDigits,
            onDigitChange = onDigitChange,
            onComplete = {}
        )
        Spacer(modifier = Modifier.height(12.dp))

        CountdownOrResend(
            countdown = state.countdown,
            onResend = onResend
        )

        Spacer(modifier = Modifier.height(20.dp))

        PasswordField(
            value = state.newPassword,
            onValueChange = onNewPasswordChange,
            visible = state.newPasswordVisible,
            onToggleVisibility = onToggleNewPasswordVisibility,
            label = "Mật khẩu mới",
            onDone = { onReset() }
        )
        Spacer(modifier = Modifier.height(14.dp))

        PasswordField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            visible = state.confirmPasswordVisible,
            onToggleVisibility = onToggleConfirmPasswordVisibility,
            label = "Xác nhận mật khẩu",
            onDone = onReset
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onReset,
            enabled = !state.isLoading && state.otpDigits.joinToString("").length == 6,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = CosmicTheme.colors.void,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Đặt lại mật khẩu", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        BackToLoginLink(onClick = onBack)
    }
}

// ── Register Success ──

@Composable
private fun RegisterSuccess(onSwitchToLogin: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HeaderIcon(Icons.Filled.CheckCircle, tint = CosmicTheme.colors.aurora)
        Spacer(modifier = Modifier.height(16.dp))
        Heading("Đăng ký thành công!")
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSwitchToLogin,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
        ) {
            Text("Đăng nhập", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// ── Shared Components ──

@Composable
private fun HeaderIcon(icon: ImageVector, tint: Color = CosmicTheme.colors.plasma) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tint.copy(alpha = 0.1f))
            .border(1.dp, tint.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun Heading(text: String) {
    Text(
        text = text,
        color = CosmicTheme.colors.textPrimary,
        style = CosmicTheme.typography.display,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x1AD45B5B))
            .border(1.dp, Color(0x33D45B5B), RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = CosmicTheme.colors.supernova,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = message,
            color = CosmicTheme.colors.supernova,
            style = CosmicTheme.typography.body
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onDone: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current

    Text(
        text = label,
        color = CosmicTheme.colors.textSecondary,
        style = CosmicTheme.typography.label,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = CosmicTheme.typography.body.copy(color = CosmicTheme.colors.textPrimary),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus(); onDone?.invoke() }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            cursorColor = CosmicTheme.colors.plasma,
            focusedTextColor = CosmicTheme.colors.textPrimary,
            unfocusedTextColor = CosmicTheme.colors.textPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    label: String = "Mật khẩu",
    onDone: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current

    Text(
        text = label,
        color = CosmicTheme.colors.textSecondary,
        style = CosmicTheme.typography.label,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = if (visible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                    tint = if (visible) CosmicTheme.colors.plasma else CosmicTheme.colors.textSecondary
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = CosmicTheme.typography.body.copy(color = CosmicTheme.colors.textPrimary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); onDone?.invoke() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            cursorColor = CosmicTheme.colors.plasma,
            focusedTextColor = CosmicTheme.colors.textPrimary,
            unfocusedTextColor = CosmicTheme.colors.textPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun OtpRow(
    digits: List<String>,
    onDigitChange: (Int, String) -> Unit,
    onComplete: () -> Unit
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        digits.forEachIndexed { index, digit ->
            DigitInput(
                digit = digit,
                focusRequester = focusRequesters[index],
                onValueChange = { newDigit ->
                    onDigitChange(index, newDigit)
                    if (newDigit.isNotEmpty() && index < 5) {
                        focusRequesters[index + 1].requestFocus()
                    } else if (newDigit.isEmpty() && digit.isNotEmpty() && index > 0) {
                        focusRequesters[index - 1].requestFocus()
                    }
                    if (index == 5 && newDigit.isNotEmpty()) {
                        onComplete()
                    }
                }
            )
        }
    }
}

@Composable
private fun DigitInput(
    digit: String,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val filled = digit.isNotEmpty()

    val borderColor = when {
        filled -> CosmicTheme.colors.plasma.copy(alpha = 0.5f)
        isFocused -> CosmicTheme.colors.plasma.copy(alpha = 0.5f)
        else -> Color.White.copy(alpha = 0.1f)
    }

    val bgColor = when {
        filled -> CosmicTheme.colors.plasma.copy(alpha = 0.05f)
        else -> Color.White.copy(alpha = 0.05f)
    }

    Box(
        modifier = Modifier
            .width(44.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused },
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = digit,
            onValueChange = { newValue ->
                val clean = newValue.filter { it.isDigit() }.takeLast(1)
                if (clean.isEmpty() && digit.isNotEmpty() && !isFocused) return@BasicTextField
                onValueChange(clean)
            },
            modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle(
                color = if (filled) CosmicTheme.colors.plasma else CosmicTheme.colors.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(CosmicTheme.colors.plasma),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun CountdownOrResend(
    countdown: Int,
    onResend: () -> Unit
) {
    if (countdown > 0) {
        Text(
            text = "Mã OTP còn hiệu lực trong $countdown giây",
            color = CosmicTheme.colors.textSecondary,
            style = CosmicTheme.typography.body
        )
    } else {
        Text(
            text = "Gửi lại mã OTP",
            color = CosmicTheme.colors.plasma,
            fontWeight = FontWeight.Medium,
            style = CosmicTheme.typography.body,
            modifier = Modifier.clickable { onResend() }
        )
    }
}

@Composable
private fun BackToLoginLink(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quay lại",
            color = CosmicTheme.colors.textSecondary,
            style = CosmicTheme.typography.body
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "đăng nhập",
            color = CosmicTheme.colors.plasma,
            fontWeight = FontWeight.Medium,
            style = CosmicTheme.typography.body,
            modifier = Modifier.clickable { onClick() }
        )
    }
}
