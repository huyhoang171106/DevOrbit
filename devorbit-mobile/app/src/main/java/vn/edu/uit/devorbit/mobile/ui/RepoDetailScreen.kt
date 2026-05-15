package vn.edu.uit.devorbit.mobile.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicGlowPurple
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextButton(onClick = onBack) {
            Text("< Back", color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = repo.displayName,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        if (repo.description.isNotBlank()) {
            Text(
                text = repo.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(16.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (repo.primaryLanguage.isNotBlank()) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(repo.primaryLanguage) },
                    colors = SuggestionChipDefaults.suggestionChipColors(labelColor = Color.White)
                )
            }
            Text(
                text = "${repo.stars ?: 0} ★",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Spacer(Modifier.height(16.dp))

        // TECH STACKS SECTION
        if (repo.techStacks.isNotEmpty()) {
            Text(
                "Tech Stacks",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repo.techStacks.forEach { techStack ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(techStack.name) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color.White.copy(alpha = 0.1f),
                            labelColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        OutlinedButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.githubUrl))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
        ) {
            Text("OPEN IN GITHUB", color = Color.White)
        }
    }
}
