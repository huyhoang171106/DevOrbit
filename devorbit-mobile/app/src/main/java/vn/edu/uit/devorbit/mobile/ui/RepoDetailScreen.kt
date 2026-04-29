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
    ) {
        TextButton(onClick = onBack) {
            Text("< Back")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = repo.displayName,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(8.dp))

        if (repo.description.isNotBlank()) {
            Text(
                text = repo.description,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(16.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (repo.primaryLanguage.isNotBlank()) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(repo.primaryLanguage) }
                )
            }
            Text(
                text = "${repo.stars} ★",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        if (repo.techStacks.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Tech Stacks: ${repo.techStacks.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.githubUrl))
                context.startActivity(intent)
            }
        ) {
            Text("Open in GitHub")
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = repo.githubUrl,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )
    }
}
