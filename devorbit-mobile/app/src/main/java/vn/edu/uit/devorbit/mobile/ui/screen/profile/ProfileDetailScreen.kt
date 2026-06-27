package vn.edu.uit.devorbit.mobile.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun ProfileDetailScreen(onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CosmicTheme.colors.void
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = "Quay lai",
                    tint = CosmicTheme.colors.textSecondary
                )
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Profile Detail (coming soon)",
                    style = CosmicTheme.typography.body,
                    color = CosmicTheme.colors.textTertiary
                )
            }
        }
    }
}
