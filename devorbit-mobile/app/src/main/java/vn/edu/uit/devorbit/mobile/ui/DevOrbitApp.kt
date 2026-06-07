package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.mobile.ui.screen.auth.AuthScreen
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AcademicViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthViewModel
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun DevOrbitApp() {
    val authVm: AuthViewModel = hiltViewModel()
    val loginState by authVm.loginState.collectAsState()
    val isChecking by authVm.isCheckingAuth.collectAsState()

    if (isChecking) {
        return
    }

    if (loginState.isLoggedIn) {
        val academicVm: AcademicViewModel = hiltViewModel()
        val isBurnedOut = academicVm.burnout.value.riskLevel != vn.edu.uit.devorbit.mobile.domain.model.BurnoutRisk.NONE

        DevOrbitTheme(isBurnedOut = isBurnedOut) {
            MainScreen(academicVm = academicVm)
        }
    } else {
        DevOrbitTheme {
            AuthScreen(
                viewModel = authVm,
                onLoggedIn = { /* State change handled by observeAuthToken */ }
            )
        }
    }
}
