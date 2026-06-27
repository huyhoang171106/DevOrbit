package vn.edu.uit.devorbit.mobile.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseTutorial
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun TutorialDetailScreen(
    tutorial: CourseTutorial,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val typeLabel = when (tutorial.type) {
        "video" -> "Video"
        "lab" -> "Lab"
        "guide" -> "Hướng dẫn"
        "interactive" -> "Tương tác"
        else -> "Bài viết"
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Quay lại", tint = CosmicTheme.colors.textSecondary)
            }
            Text("Hướng dẫn", style = CosmicTheme.typography.body, color = CosmicTheme.colors.textSecondary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Title
            Text(
                text = tutorial.title,
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary
            )

            Spacer(Modifier.height(12.dp))

            // Type badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)
            ) {
                Text(
                    text = typeLabel,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Medium),
                    color = CosmicTheme.colors.plasma
                )
            }

            // Description
            if (!tutorial.description.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = CosmicTheme.colors.nebula,
                    border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mô tả", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold), color = CosmicTheme.colors.textPrimary)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = tutorial.description,
                            style = CosmicTheme.typography.body,
                            color = CosmicTheme.colors.textSecondary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Open button
            Button(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(tutorial.url)))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
            ) {
                Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Mở hướng dẫn")
            }
        }
    }
}
