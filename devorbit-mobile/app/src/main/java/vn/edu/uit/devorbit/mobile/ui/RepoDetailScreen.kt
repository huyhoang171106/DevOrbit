package vn.edu.uit.devorbit.mobile.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.data.remote.dto.AiResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSocialInfoResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.data.remote.dto.ReviewResponse
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun RepoDetailScreen(
    repo: RepoSummary,
    onBack: () -> Unit,
    aiSummary: AiResponse? = null,
    aiAdvice: AiResponse? = null,
    aiLoading: Boolean = false,
    socialInfo: RepoSocialInfoResponse? = null,
    userVote: Int = 0,
    socialLoading: Boolean = false,
    onVote: (Int) -> Unit = {},
    onSubmitReview: (Int, String?) -> Unit = { _, _ -> },
    onDeleteReview: () -> Unit = {}
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "Quay lai",
                        tint = CosmicTheme.colors.textSecondary
                    )
                }
                Text(
                    text = "Repository",
                    style = CosmicTheme.typography.body,
                    color = CosmicTheme.colors.textSecondary
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = repo.displayName,
                    style = CosmicTheme.typography.display,
                    color = CosmicTheme.colors.textPrimary
                )
                if (repo.description.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = repo.description,
                        style = CosmicTheme.typography.body,
                        color = CosmicTheme.colors.textSecondary
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            RepoInfoChips(repo)
        }

        item {
            Spacer(Modifier.height(16.dp))
            SocialBar(
                voteScore = socialInfo?.voteScore ?: 0,
                averageRating = socialInfo?.averageRating ?: 0.0,
                reviewCount = socialInfo?.reviews?.size ?: 0,
                userVote = userVote,
                onVote = onVote,
                repoId = repo.id
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(repo.githubUrl)))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma
                )
            ) {
                Icon(
                    Icons.Rounded.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Mo tren GitHub")
            }
        }

        if (aiLoading) {
            item {
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = CosmicTheme.colors.plasma,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "AI dang phan tiet...",
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textTertiary
                        )
                    }
                }
            }
        }

        aiSummary?.let { summary ->
            item {
                Spacer(Modifier.height(24.dp))
                AiSection(
                    title = "AI Summary",
                    type = summary.type,
                    content = summary.content
                )
            }
        }

        aiAdvice?.let { advice ->
            item {
                Spacer(Modifier.height(16.dp))
                AiSection(
                    title = "AI Tutor Advice",
                    type = advice.type,
                    content = advice.content
                )
            }
        }

        socialInfo?.let { info ->
            if (info.reviews.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Reviews (${info.reviews.size})",
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                        color = CosmicTheme.colors.textPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items(info.reviews) { review ->
                    ReviewItem(review)
                }
            }
        }
    }
}

@Composable
private fun RepoInfoChips(repo: RepoSummary) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (repo.primaryLanguage.isNotBlank()) {
            InfoChip(label = repo.primaryLanguage, color = CosmicTheme.colors.aurora)
        }
        repo.stars?.let { stars ->
            if (stars > 0) {
                InfoChip(label = "$stars stars", color = CosmicTheme.colors.supernova)
            }
        }
        repo.techStacks.forEach { tech ->
            InfoChip(label = tech.name, color = CosmicTheme.colors.plasma)
        }
    }
}

@Composable
private fun InfoChip(label: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Medium),
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun AiSection(title: String, type: String, content: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                    color = CosmicTheme.colors.textPrimary
                )
                val typeLabel = when (type) {
                    "LLM_SUMMARY", "LLM_TUTOR_ADVICE" -> "AI"
                    else -> "Rules"
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CosmicTheme.colors.plasma.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = typeLabel,
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.plasma,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            MarkdownContent(content)
        }
    }
}

@Composable
private fun MarkdownContent(text: String) {
    val lines = text.lines()
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        lines.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("**") && trimmed.endsWith("**") -> {
                    Text(
                        text = trimmed.removeSurrounding("**"),
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                        color = CosmicTheme.colors.textPrimary
                    )
                }
                trimmed.startsWith("- ") -> {
                    Text(
                        text = trimmed.removePrefix("- "),
                        style = CosmicTheme.typography.body,
                        color = CosmicTheme.colors.textSecondary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                trimmed.isBlank() -> {
                    Spacer(Modifier.height(4.dp))
                }
                else -> {
                    Text(
                        text = trimmed,
                        style = CosmicTheme.typography.body,
                        color = CosmicTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SocialBar(
    voteScore: Int,
    averageRating: Double,
    reviewCount: Int,
    userVote: Int,
    onVote: (Int) -> Unit,
    repoId: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onVote(if (userVote == 1) 0 else 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.ThumbUp,
                    contentDescription = "Upvote",
                    tint = if (userVote == 1) CosmicTheme.colors.aurora else CosmicTheme.colors.textTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = "$voteScore",
                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                color = CosmicTheme.colors.textPrimary
            )
            IconButton(
                onClick = { onVote(if (userVote == -1) 0 else -1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.ThumbDown,
                    contentDescription = "Downvote",
                    tint = if (userVote == -1) CosmicTheme.colors.supernova else CosmicTheme.colors.textTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (averageRating > 0) {
            InfoChip(
                label = "%.1f".format(averageRating),
                color = CosmicTheme.colors.supernova
            )
        }
        if (reviewCount > 0) {
            InfoChip(
                label = "$reviewCount reviews",
                color = CosmicTheme.colors.plasma
            )
        }
    }
}

@Composable
private fun ReviewItem(review: ReviewResponse) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.studentName,
                    style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textPrimary
                )
                InfoChip(
                    label = "${review.rating}",
                    color = CosmicTheme.colors.supernova
                )
            }
            if (!review.comment.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = review.comment,
                    style = CosmicTheme.typography.body,
                    color = CosmicTheme.colors.textSecondary
                )
            }
        }
    }
}
