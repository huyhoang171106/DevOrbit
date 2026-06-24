package vn.edu.uit.devorbit.admin.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.core.designsystem.AdminPrimaryButton
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoggedIn: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoggedIn()
    }

    // ── Session restore loading ────────────────────────────────────────────
    if (!state.sessionChecked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = UITBlue,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp),
            )
        }
        return
    }

    // ── Login form ─────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(24.dp),
            shape = ObsidianShape.lg,
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // ── Brand icon ───────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(ObsidianShape.md)
                        .background(UITBlueSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = null,
                        tint = UITBlue,
                        modifier = Modifier.size(28.dp),
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ── Title ────────────────────────────────────────────────
                Text(
                    text = "Quản trị DevOrbit",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(32.dp))

                // ── Username field ───────────────────────────────────────
                OutlinedTextField(
                    value = state.username,
                    onValueChange = viewModel::updateUsername,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Tên đăng nhập") },
                    placeholder = { Text("Nhập tên đăng nhập") },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    singleLine = true,
                    shape = ObsidianShape.sm,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Border,
                        focusedBorderColor = UITBlue,
                        unfocusedContainerColor = Surface,
                        focusedContainerColor = Surface,
                        focusedLabelColor = UITBlue,
                        cursorColor = UITBlue,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    ),
                    enabled = !state.isLoading,
                )

                Spacer(Modifier.height(16.dp))

                // ── Password field ───────────────────────────────────────
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::updatePassword,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Mật khẩu") },
                    placeholder = { Text("Nhập mật khẩu") },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = viewModel::togglePasswordVisibility) {
                            Icon(
                                imageVector = if (state.isPasswordVisible)
                                    Icons.Rounded.VisibilityOff
                                else
                                    Icons.Rounded.Visibility,
                                contentDescription = if (state.isPasswordVisible)
                                    "Ẩn mật khẩu"
                                else
                                    "Hiện mật khẩu",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    },
                    singleLine = true,
                    shape = ObsidianShape.sm,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Border,
                        focusedBorderColor = UITBlue,
                        unfocusedContainerColor = Surface,
                        focusedContainerColor = Surface,
                        focusedLabelColor = UITBlue,
                        cursorColor = UITBlue,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    visualTransformation = if (state.isPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.login()
                        },
                    ),
                    enabled = !state.isLoading,
                )

                // ── Error message ────────────────────────────────────────
                AnimatedVisibility(visible = state.error != null) {
                    Text(
                        text = state.error.orEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Danger,
                        textAlign = TextAlign.Start,
                    )
                }

                Spacer(Modifier.height(24.dp))

                // ── Login button ─────────────────────────────────────────
                AdminPrimaryButton(
                    text = "Đăng nhập",
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.login()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    loading = state.isLoading,
                )
            }
        }
    }
}
