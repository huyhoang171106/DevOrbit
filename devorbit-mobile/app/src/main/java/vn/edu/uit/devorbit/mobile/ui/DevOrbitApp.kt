package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.mobile.model.*
import vn.edu.uit.devorbit.mobile.model.domain.*
import vn.edu.uit.devorbit.mobile.ui.screen.dashboard.*
import vn.edu.uit.devorbit.mobile.ui.screen.knowledge.*
import vn.edu.uit.devorbit.mobile.ui.screen.plan.*
import vn.edu.uit.devorbit.mobile.ui.screen.analytics.*
import vn.edu.uit.devorbit.mobile.ui.screen.risk.BurnoutScreen
import vn.edu.uit.devorbit.mobile.ui.screen.risk.DigitalTwinScreen
import vn.edu.uit.devorbit.mobile.ui.screen.risk.RecommendationsScreen
import vn.edu.uit.devorbit.mobile.ui.screen.risk.RiskCenterScreen
import vn.edu.uit.devorbit.mobile.ui.screen.risk.SimulationScreen
import vn.edu.uit.devorbit.mobile.ui.screen.analytics.WorkloadScreen
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AcademicViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import vn.edu.uit.devorbit.mobile.ui.theme.*

enum class Screen { COURSES, DETAIL, REPO_DETAIL, FOCUS_MODE, BREAKDOWN, SIMULATE, TIMELINE, GPA, BURNOUT, TWIN, RECOMMENDATIONS, SYLLABUS, KNOWLEDGE_DETAIL, WORKLOAD }

@Composable
fun DevOrbitApp() {
    val academicVm: AcademicViewModel = hiltViewModel()
    val isBurnedOut = academicVm.burnout.value.riskLevel != vn.edu.uit.devorbit.mobile.model.domain.BurnoutRisk.NONE

    DevOrbitTheme(isBurnedOut = isBurnedOut) {
        MainScreen(academicVm = academicVm)
    }
}

data class NavItem(val icon: ImageVector, val label: String, val screen: Screen)
