package vn.edu.uit.devorbit.mobile.ui.screen.notification

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun NotificationScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Thông báo",
                color = CosmicTheme.colors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Chưa có thông báo nào",
                color = CosmicTheme.colors.textSecondary,
                fontSize = 14.sp
            )
        }
    }
}
