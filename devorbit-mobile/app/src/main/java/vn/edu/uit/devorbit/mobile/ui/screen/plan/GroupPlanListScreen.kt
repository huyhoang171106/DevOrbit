package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.data.remote.dto.GroupPlanResponse
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.GroupPlanViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPlanListScreen(
    onNavigateToPlan: (Long) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: GroupPlanViewModel = hiltViewModel()
) {
    val plans by viewModel.myPlans.collectAsStateWithLifecycle()
    val loading by viewModel.plansLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMyPlans()
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kế hoạch nhóm",
                color = CosmicTheme.colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onNavigateBack) {
                Text("Đóng", color = CosmicTheme.colors.plasma)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CosmicTheme.colors.plasma, strokeWidth = 2.dp)
            }
        } else if (plans.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Chưa có kế hoạch nhóm nào",
                    color = CosmicTheme.colors.textTertiary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(plans, key = { it.id }) { plan ->
                    PlanListItem(plan = plan, onClick = { onNavigateToPlan(plan.id) })
                }
            }
        }
    }
}

@Composable
private fun PlanListItem(plan: GroupPlanResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicTheme.colors.nebula)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Group,
                contentDescription = null,
                tint = CosmicTheme.colors.plasma,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.title,
                    color = CosmicTheme.colors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (plan.deadline != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hạn: ${plan.deadline}",
                        color = CosmicTheme.colors.textTertiary,
                        fontSize = 12.sp
                    )
                }
            }
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = CosmicTheme.colors.textTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
