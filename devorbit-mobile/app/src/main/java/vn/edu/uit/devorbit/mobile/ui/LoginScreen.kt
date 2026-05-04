package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.repository.DevOrbitRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { DevOrbitRepository(context) }
    val scope = rememberCoroutineScope()
    var studentCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var registering by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (registering) "Student Registration" else "Student Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(studentCode, { studentCode = it }, label = { Text("Student code") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        if (registering) {
            OutlinedTextField(fullName, { fullName = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }
        OutlinedTextField(password, { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    loading = true
                    error = null
                    val result = if (registering) {
                        repository.registerStudent(studentCode, fullName, email, password)
                    } else {
                        repository.loginStudent(studentCode, password)
                    }
                    result
                        .onSuccess { onLoginSuccess() }
                        .onFailure { error = it.message ?: if (registering) "Registration failed" else "Login failed" }
                    loading = false
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (loading) "Please wait..." else if (registering) "Create account" else "Log in") }
        TextButton(onClick = { registering = !registering }, modifier = Modifier.fillMaxWidth()) {
            Text(if (registering) "Already have an account? Log in" else "Create a student account")
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
    }
}
