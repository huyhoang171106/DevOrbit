package vn.edu.uit.devorbit.mobile.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepoDetailScreen(
    repo: RepoSummary,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onBack, modifier = Modifier.padding(start = 0.dp)) {
            Text("← Quay lại", color = CosmicTheme.colors.textSecondary)
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = repo.displayName,
            style = CosmicTheme.typography.display,
            color = CosmicTheme.colors.textPrimary
        )

        Spacer(Modifier.height(12.dp))

        if (repo.description.isNotBlank()) {
            Text(
                text = repo.description,
                style = CosmicTheme.typography.body,
                color = CosmicTheme.colors.textSecondary
            )
            Spacer(Modifier.height(20.dp))
        }

        // Meta row: language + stars
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (repo.primaryLanguage.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = repo.primaryLanguage,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.plasma
                    )
                }
            }
            Text(
                text = "${repo.stars ?: 0} ★",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
        }

        // Tech stacks
        if (repo.techStacks.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(
                "Tech stacks",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.textTertiary,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repo.techStacks.forEach { techStack ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CosmicTheme.colors.nebula,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                    ) {
                        Text(
                            text = techStack.name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(36.dp))

        // GitHub button
        Button(
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(repo.githubUrl)))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CosmicTheme.colors.plasma,
                contentColor = CosmicTheme.colors.void
            ),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text("Mở trên GitHub", fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(24.dp))
    }
}
