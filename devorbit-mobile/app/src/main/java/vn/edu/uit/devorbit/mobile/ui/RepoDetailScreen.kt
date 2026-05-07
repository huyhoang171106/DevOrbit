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
import vn.edu.uit.devorbit.mobile.model.RepoSummary

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicGlowPurple
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AiTutorViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

@Composable
fun RepoDetailScreen(
    repo: RepoSummary,
    onBack: () -> Unit,
    aiViewModel: AiTutorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val aiSummary by aiViewModel.summary
    val aiAdvice by aiViewModel.advice
    val isAiLoading by aiViewModel.isLoading

    LaunchedEffect(repo.id) {
        aiViewModel.loadAiInsights(repo.id)
    }

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

        Spacer(Modifier.height(24.dp))

        // AI SMART INSIGHTS SECTION
        Text(
            "AI SMART INSIGHTS",
            style = MaterialTheme.typography.labelLarge,
            color = CosmicGlowPurple,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CosmicGlowPurple.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        ) {
            if (isAiLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = CosmicGlowPurple
                )
            } else {
                Column {
                    aiSummary?.let {
                        Text(
                            text = "💡 Summary",
                            style = MaterialTheme.typography.titleSmall,
                            color = CosmicGlowPurple
                        )
                        Text(
                            text = it.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    
                    aiAdvice?.let {
                        Text(
                            text = "🎓 Tutor's Advice",
                            style = MaterialTheme.typography.titleSmall,
                            color = CosmicGlowPurple
                        )
                        Text(
                            text = it.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
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
