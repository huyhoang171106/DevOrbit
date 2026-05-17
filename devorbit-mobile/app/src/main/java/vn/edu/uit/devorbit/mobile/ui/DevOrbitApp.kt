package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AcademicViewModel
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun DevOrbitApp() {
    val academicVm: AcademicViewModel = hiltViewModel()
    val isBurnedOut = academicVm.burnout.value.riskLevel != vn.edu.uit.devorbit.mobile.domain.model.BurnoutRisk.NONE

    DevOrbitTheme(isBurnedOut = isBurnedOut) {
        MainScreen(academicVm = academicVm)
    }
}
