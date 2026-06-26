package vn.edu.uit.devorbit.mobile.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.data.remote.dto.AiResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSocialInfoResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.data.remote.dto.ReviewResponse
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import java.text.SimpleDateFormat
import java.util.Locale

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
    isBookmarked: Boolean = false,
    onVote: (Int) -> Unit = {},
    onBookmark: () -> Unit = {},
    onSubmitReview: (Int, String?) -> Unit = { _, _ -> },
    onDeleteReview: () -> Unit = {}
) {
    val context = LocalContext.current
    var showReviewForm by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableIntStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Filter out techStacks that match primaryLanguage to avoid duplication
    val filteredStacks = remember(repo) {
        repo.techStacks.filter { it.name != repo.primaryLanguage }
    }

    val lastPushedFormatted = remember(repo.lastPushedAt) {
        repo.lastPushedAt?.let { raw ->
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = parser.parse(raw.take(19))
                val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                fmt.format(date!!)
            } catch (_: Exception) { null }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding() + 24.dp)
    ) {
        // ─── Header ───
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Quay lại", tint = CosmicTheme.colors.textSecondary)
                }
                Text("Repository", style = CosmicTheme.typography.body, color = CosmicTheme.colors.textSecondary)
            }
        }

        // ─── Title + description ───
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = repo.displayName.orEmpty(),
                        style = CosmicTheme.typography.display,
                        color = CosmicTheme.colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onBookmark) {
                        Icon(
                            if (isBookmarked) Icons.Filled.BookmarkRemove else Icons.Filled.BookmarkAdd,
                            contentDescription = if (isBookmarked) "Bỏ lưu" else "Lưu",
                            tint = if (isBookmarked) CosmicTheme.colors.plasma else CosmicTheme.colors.textTertiary
                        )
                    }
                }
                if (!repo.description.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = repo.description,
                        style = CosmicTheme.typography.body,
                        color = CosmicTheme.colors.textSecondary
                    )
                }
            }
        }

        // ─── Info chips (language, stars, tech stacks, last pushed, rating) ───
        item {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!repo.primaryLanguage.isNullOrBlank()) {
                    InfoChip(repo.primaryLanguage, CosmicTheme.colors.aurora)
                }
                repo.stars?.let { s ->
                    InfoChip("\u2605 $s", CosmicTheme.colors.supernova)
                }
                filteredStacks.forEach { tech ->
                    if (!tech.name.isNullOrBlank()) {
                        InfoChip(tech.name, CosmicTheme.colors.plasma)
                    }
                }
                lastPushedFormatted?.let {
                    InfoChip("Cập nhật $it", CosmicTheme.colors.textTertiary)
                }
                val avgRating = socialInfo?.averageRating ?: 0.0
                if (avgRating > 0) {
                    InfoChip("\u2605 ${"%.1f".format(avgRating)}", CosmicTheme.colors.supernova)
                }
                val rCount = socialInfo?.reviews?.size ?: 0
                if (rCount > 0) {
                    InfoChip("$rCount reviews", CosmicTheme.colors.plasma)
                }
            }
        }

        // ─── GitHub button ───
        item {
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    repo.githubUrl?.let { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it))) }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
            ) {
                Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Mở trên GitHub")
            }
        }

        // ─── README excerpt ───
        item {
            Spacer(Modifier.height(16.dp))
            SectionCard(title = "README") {
                if (!repo.readmeExcerpt.isNullOrBlank()) {
                    Text(text = repo.readmeExcerpt, style = CosmicTheme.typography.body, color = CosmicTheme.colors.textSecondary)
                } else {
                    Text(text = "Chưa có README", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                }
            }
        }

        // ─── File tree / directory structure ───
        item {
            Spacer(Modifier.height(12.dp))
            SectionCard(title = "Cấu trúc thư mục") {
                if (!repo.fileTree.isNullOrBlank()) {
                    val lines = repo.fileTree.lines()
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        lines.forEach { line ->
                            Text(
                                text = line,
                                style = CosmicTheme.typography.label.copy(fontSize = 12.sp),
                                color = CosmicTheme.colors.textSecondary,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    Text(text = "Chưa có thông tin cấu trúc", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                }
            }
        }

        // ─── AI loading ───
        if (aiLoading) {
            item {
                Spacer(Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CosmicTheme.colors.plasma, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("AI đang phân tích...", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    }
                }
            }
        }

        // ─── AI Summary ───
        item {
            Spacer(Modifier.height(12.dp))
            AiSection(title = "Tóm tắt", content = aiSummary?.content, type = aiSummary?.type)
        }

        // ─── AI Advice ───
        item {
            Spacer(Modifier.height(12.dp))
            AiSection(title = "Lời khuyên", content = aiAdvice?.content, type = aiAdvice?.type)
        }

        // ─── Vote buttons + Reviews header ───
        item {
            Spacer(Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                color = CosmicTheme.colors.nebula,
                border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onVote(if (userVote == 1) 0 else 1) }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Rounded.ThumbUp, contentDescription = "Upvote",
                                tint = if (userVote == 1) CosmicTheme.colors.aurora else CosmicTheme.colors.textTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "${socialInfo?.voteScore ?: 0}",
                            style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                            color = CosmicTheme.colors.textPrimary
                        )
                        IconButton(onClick = { onVote(if (userVote == -1) 0 else -1) }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Rounded.ThumbDown, contentDescription = "Downvote",
                                tint = if (userVote == -1) CosmicTheme.colors.supernova else CosmicTheme.colors.textTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Đánh giá (${socialInfo?.reviews?.size ?: 0})",
                            style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                            color = CosmicTheme.colors.textPrimary
                        )
                        TextButton(onClick = { showReviewForm = !showReviewForm }) {
                            Text(if (showReviewForm) "Huỷ" else "Viết đánh giá", color = CosmicTheme.colors.plasma)
                        }
                    }
                }
            }
        }

        if (showReviewForm) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = CosmicTheme.colors.nebula,
                    border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Điểm:", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                            Spacer(Modifier.width(8.dp))
                            (1..5).forEach { r ->
                                IconButton(onClick = { reviewRating = r }, modifier = Modifier.size(28.dp)) {
                                    Icon(
                                        Icons.Filled.Star,
                                        contentDescription = "$r sao",
                                        tint = if (r <= reviewRating) CosmicTheme.colors.supernova else CosmicTheme.colors.glassBorder,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = reviewComment,
                            onValueChange = { reviewComment = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Nhận xét...") },
                            minLines = 2, maxLines = 4,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onSubmitReview(reviewRating, reviewComment.ifBlank { null })
                                showReviewForm = false
                                reviewComment = ""
                                reviewRating = 5
                                scope.launch {
                                    snackbarHostState.showSnackbar("Đã gửi đánh giá!", duration = SnackbarDuration.Short)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
                        ) { Text("Gửi đánh giá") }
                    }
                }
            }
        }

        // ─── Review items ───
        socialInfo?.let { info ->
            if (info.reviews.isNotEmpty()) {
                items(info.reviews) { review ->
                    ReviewItem(review)
                }
            }
        }
    }
    }
}

// ─── Reusable components ───

@Composable
private fun InfoChip(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
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
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                color = CosmicTheme.colors.textPrimary
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun AiSection(title: String, content: String?, type: String?) {
    SectionCard(title = title) {
        if (content != null && type != null) {
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
            Spacer(Modifier.height(12.dp))
            MarkdownContent(content)
        } else {
            Text(
                text = "Đang tải phân tích AI...",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
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
                trimmed.isBlank() -> Spacer(Modifier.height(4.dp))
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
private fun ReviewItem(review: ReviewResponse) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
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
                InfoChip("\u2605".repeat(review.rating), CosmicTheme.colors.supernova)
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
