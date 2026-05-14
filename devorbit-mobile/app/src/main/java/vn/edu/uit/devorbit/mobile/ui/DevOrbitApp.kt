package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.ui.screen.auth.AuthScreen
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AcademicViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AuthViewModel
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun DevOrbitApp() {
    val academicVm: AcademicViewModel = hiltViewModel()
    val authVm: AuthViewModel = hiltViewModel()
    val loginState by authVm.loginState.collectAsStateWithLifecycle()
    val isChecking by authVm.isCheckingAuth.collectAsStateWithLifecycle()
    val isBurnedOut = academicVm.burnout.value.riskLevel != vn.edu.uit.devorbit.mobile.domain.model.BurnoutRisk.NONE

    DevOrbitTheme(isBurnedOut = isBurnedOut) {
        if (isChecking) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CosmicTheme.colors.plasma)
            }
        } else if (loginState.isLoggedIn) {
            MainScreen(academicVm = academicVm)
        } else {
            AuthScreen(viewModel = authVm, onLoggedIn = { /* state changes reactively */ })
        }
    }
}
