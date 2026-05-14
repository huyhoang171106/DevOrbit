package vn.edu.uit.devorbit.mobile.ui.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoggedIn: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
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

    var showRegister by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showRegister) {
            RegisterContent(
                state = registerState,
                onFieldChange = viewModel::updateRegisterField,
                onRegister = viewModel::register,
                onSwitchToLogin = { showRegister = false; viewModel.switchToLogin() }
            )
        } else {
            LoginContent(
                state = loginState,
                onStudentCodeChange = viewModel::updateStudentCode,
                onPasswordChange = viewModel::updatePassword,
                onLogin = viewModel::login,
                onSwitchToRegister = { showRegister = true; viewModel.switchToRegister() }
            )
        }
    }
}

@Composable
private fun LoginContent(
    state: vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthUiState,
    onStudentCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Text(
        "DevOrbit",
        style = CosmicTheme.typography.display,
        color = CosmicTheme.colors.plasma,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        "Nền tảng khám phá mã nguồn học thuật UIT",
        style = CosmicTheme.typography.body,
        color = CosmicTheme.colors.textSecondary,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(48.dp))

    OutlinedTextField(
        value = state.studentCode,
        onValueChange = onStudentCodeChange,
        label = { Text("MSSV") },
        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = CosmicTheme.colors.plasma) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CosmicTheme.colors.plasma,
            unfocusedBorderColor = CosmicTheme.colors.textSecondary,
            focusedTextColor = CosmicTheme.colors.textPrimary,
            unfocusedTextColor = CosmicTheme.colors.textPrimary,
            cursorColor = CosmicTheme.colors.plasma
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = state.password,
        onValueChange = onPasswordChange,
        label = { Text("Mật khẩu") },
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = CosmicTheme.colors.plasma) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); onLogin() }),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CosmicTheme.colors.plasma,
            unfocusedBorderColor = CosmicTheme.colors.textSecondary,
            focusedTextColor = CosmicTheme.colors.textPrimary,
            unfocusedTextColor = CosmicTheme.colors.textPrimary,
            cursorColor = CosmicTheme.colors.plasma
        )
    )

    if (state.error != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(state.error, color = CosmicTheme.colors.supernova, style = CosmicTheme.typography.label)
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onLogin,
        enabled = !state.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = CosmicTheme.colors.void,
                strokeWidth = 2.dp
            )
        } else {
            Text("Đăng nhập", style = CosmicTheme.typography.body, fontWeight = FontWeight.Bold)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    TextButton(onClick = onSwitchToRegister) {
        Text("Chưa có tài khoản? Đăng ký", color = CosmicTheme.colors.plasma)
    }
}

@Composable
private fun RegisterContent(
    state: vn.edu.uit.devorbit.mobile.ui.viewmodel.RegisterUiState,
    onFieldChange: (String, String) -> Unit,
    onRegister: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    if (state.isSuccess) {
        Text(
            "Đăng ký thành công! 🎉",
            style = CosmicTheme.typography.display,
            color = CosmicTheme.colors.plasma
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onSwitchToLogin) {
            Text("Đã có tài khoản? Đăng nhập", color = CosmicTheme.colors.plasma)
        }
        return
    }

    Text(
        "Tạo tài khoản",
        style = CosmicTheme.typography.display,
        color = CosmicTheme.colors.plasma,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(32.dp))

    OutlinedTextField(
        value = state.studentCode,
        onValueChange = { onFieldChange("studentCode", it) },
        label = { Text("MSSV") },
        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = CosmicTheme.colors.plasma) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        colors = authFieldColors()
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = state.fullName,
        onValueChange = { onFieldChange("fullName", it) },
        label = { Text("Họ và tên") },
        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = CosmicTheme.colors.plasma) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        colors = authFieldColors()
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = state.email,
        onValueChange = { onFieldChange("email", it) },
        label = { Text("Email") },
        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = CosmicTheme.colors.plasma) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        colors = authFieldColors()
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = state.password,
        onValueChange = { onFieldChange("password", it) },
        label = { Text("Mật khẩu") },
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = CosmicTheme.colors.plasma) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        colors = authFieldColors()
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = state.confirmPassword,
        onValueChange = { onFieldChange("confirmPassword", it) },
        label = { Text("Xác nhận mật khẩu") },
        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = CosmicTheme.colors.plasma) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); onRegister() }),
        colors = authFieldColors()
    )

    if (state.error != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(state.error, color = CosmicTheme.colors.supernova, style = CosmicTheme.typography.label)
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onRegister,
        enabled = !state.isLoading,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = CosmicTheme.colors.void,
                strokeWidth = 2.dp
            )
        } else {
            Text("Đăng ký", style = CosmicTheme.typography.body, fontWeight = FontWeight.Bold)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    TextButton(onClick = onSwitchToLogin) {
        Text("Đã có tài khoản? Đăng nhập", color = CosmicTheme.colors.plasma)
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CosmicTheme.colors.plasma,
    unfocusedBorderColor = CosmicTheme.colors.textSecondary,
    focusedTextColor = CosmicTheme.colors.textPrimary,
    unfocusedTextColor = CosmicTheme.colors.textPrimary,
    cursorColor = CosmicTheme.colors.plasma
)
