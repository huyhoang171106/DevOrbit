package vn.edu.uit.devorbit.mobile.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Update
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
import androidx.compose.ui.graphics.vector.ImageVector
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

@OptIn(ExperimentalLayoutApi::class)
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

    fun formatRelativeTime(isoDate: String?): String {
        if (isoDate == null) return "Vừa xong"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = parser.parse(isoDate.take(19))
            val now = System.currentTimeMillis()
            val diff = now - date!!.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val weeks = days / 7
            val months = days / 30
            val years = days / 365
            when {
                years > 0 -> "${years} năm trước"
                months > 0 -> "${months} tháng trước"
                weeks > 0 -> "${weeks} tuần trước"
                days > 0 -> "${days} ngày trước"
                hours > 0 -> "${hours} giờ trước"
                minutes > 0 -> "${minutes} phút trước"
                else -> "Vừa xong"
            }
        } catch (_: Exception) { "Vừa xong" }
    }

    val relativeTime = remember(repo.lastPushedAt, repo.approvedAt) {
        formatRelativeTime(repo.lastPushedAt ?: repo.approvedAt)
    }

    // ─── Analysis computations (computed from repo data, no API needed) ───
    val repoTypeLabel = repo.repoType?.let { rt ->
        when (rt) {
            "programming_exercise" -> "Bài tập lập trình"
            "project_practice" -> "Dự án thực hành"
            "study_material" -> "Tài liệu học tập"
            "exam_review" -> "Ôn tập thi"
            "mixed_resource" -> "Tài liệu tổng hợp"
            "fullstack" -> "Fullstack"
            "backend" -> "Backend"
            "frontend" -> "Frontend"
            "mobile" -> "Mobile"
            "data_science" -> "Khoa học dữ liệu"
            "devops" -> "DevOps"
            "unknown", "other" -> "Chưa phân loại"
            else -> "Chưa phân loại"
        }
    }
    val usefulnessLabel = repo.usefulnessRating?.let { ur ->
        when (ur) {
            "excellent", "highly_recommended" -> "Rất nên xem"
            "good", "recommended" -> "Nên xem"
            "average", "selective" -> "Có chọn lọc"
            "limited", "quick_reference" -> "Tham khảo"
            "low_priority" -> "Không ưu tiên"
            "insufficient_data" -> "Chưa đủ dữ liệu"
            else -> "Chưa phân loại"
        }
    }
    val readyLabel = repo.readyToUseLevel?.let { rl ->
        when (rl) {
            "very_ready" -> "Sẵn sàng"
            "ready" -> "Có thể dùng"
            "needs_check" -> "Cần kiểm tra"
            "quick_reference" -> "Tham khảo nhanh"
            "insufficient_data" -> "Chưa đủ dữ liệu"
            else -> "Chưa xác định"
        }
    }
    val hasReadme = repo.hasReadme ?: false
    val hasDesc = !repo.description.isNullOrBlank()
    val hasFileTree = !repo.fileTree.isNullOrBlank()
    val starCount = repo.stars ?: 0
    val lang = repo.primaryLanguage.orEmpty()

    // Parse owner from githubUrl: https://github.com/owner/repo-name
    val repoOwner = remember(repo.githubUrl) {
        try {
            val parts = repo.githubUrl?.trimEnd('/')?.split("/")
            if (parts != null && parts.size >= 4) parts[3] else null
        } catch (_: Exception) { null }
    }

    val techTools = remember(repo) {
        buildList {
            if (lang.isNotBlank()) add(lang)
            repo.techStacks.filter { it.name != lang }.forEach { add(it.name) }
        }
    }
    val quickSummary = remember(repo) {
        buildString {
            append("Repository **${repo.displayName}**")
            if (repo.courseName != null) append(" thuộc môn **${repo.courseName}**")
            append(". Ngôn ngữ chính **$lang**.")
            if (starCount > 0) append(" Có **$starCount sao** GitHub.")
            if (hasDesc) append(" Mô tả đầy đủ.")
            if (!hasDesc) append(" Chưa có mô tả chi tiết.")
        }
    }
    val strengths = remember(repo) {
        buildList {
            if (hasReadme) add("Có README hướng dẫn")
            if (hasDesc) add("Mô tả chi tiết nội dung")
            if (hasFileTree) add("Cấu trúc thư mục rõ ràng")
            if (starCount > 10) add("Cộng đồng đánh giá cao ($starCount sao)")
            if (techTools.size > 1) add("Nhiều công nghệ (${techTools.take(3).joinToString(", ")})")
            if (repo.courseName != null) add("Liên kết với môn ${repo.courseCode.orEmpty()}")
            if (isEmpty()) add("Repository mã nguồn tham khảo")
        }
    }
    val weaknesses = remember(repo) {
        buildList {
            if (!hasReadme) add("Thiếu README hướng dẫn")
            if (!hasDesc) add("Thiếu mô tả chi tiết")
            if (!hasFileTree) add("Không có cấu trúc thư mục")
            if (starCount == 0) add("Chưa có sao từ cộng đồng")
        }
    }
    val coreTopics = remember(repo) {
        buildList {
            repo.courseName?.let { add(it) }
            repoTypeLabel?.let { if (it != repo.courseName) add(it) }
        }
    }
    val nextActions = remember(repo) { listOf("Clone repo", "Cài đặt dependencies", "Chạy thử", "Phân tích mã nguồn") }

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

        // ─── Info chips (language, stars, tech stacks, last pushed) ───
        item {
            Spacer(Modifier.height(8.dp))
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
            }
        }

        // ─── GitHub button (under title) ───
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

        // ─── Thông tin repo ───
        item { Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            color = CosmicTheme.colors.nebula,
            border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Thông tin repo", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold), color = CosmicTheme.colors.textPrimary)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        MetaRow(Icons.Filled.Person, repoOwner ?: "—")
                        Spacer(Modifier.height(10.dp))
                        MetaRow(Icons.Filled.Code, lang.ifBlank { "—" })
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        MetaRow(Icons.Filled.Star, "$starCount")
                        Spacer(Modifier.height(10.dp))
                        MetaRow(Icons.Filled.CallSplit, repo.forkCount?.toString() ?: "—")
                        Spacer(Modifier.height(10.dp))
                        MetaRow(Icons.Filled.Description, repo.license ?: "—")
                    }
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = CosmicTheme.colors.glassBorder.copy(alpha = 0.3f))
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Update, contentDescription = null, tint = CosmicTheme.colors.textTertiary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cập nhật lần cuối: ", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    Text(relativeTime, style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Medium), color = CosmicTheme.colors.textPrimary)
                }
            }
        } }

        // ─── Công nghệ / công cụ ───
        if (techTools.isNotEmpty()) {
            item { Spacer(Modifier.height(8.dp))
            SectionCard(title = "Công nghệ / công cụ") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    techTools.forEach { InfoChip(it, CosmicTheme.colors.plasma) }
                }
            } }
        }

        // ─── Chủ đề chính ───
        if (coreTopics.isNotEmpty()) {
            item { Spacer(Modifier.height(8.dp))
            SectionCard(title = "Chủ đề chính") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    coreTopics.forEach { InfoChip(it, CosmicTheme.colors.aurora) }
                }
            } }
        }

        // ─── Điểm mạnh / Điểm yếu ───
        if (strengths.isNotEmpty() || weaknesses.isNotEmpty()) {
            item { Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                color = CosmicTheme.colors.nebula,
                border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (strengths.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(CosmicTheme.colors.aurora, RoundedCornerShape(4.dp)))
                            Spacer(Modifier.width(8.dp))
                            Text("Điểm mạnh", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold), color = CosmicTheme.colors.aurora)
                        }
                        Spacer(Modifier.height(6.dp))
                        strengths.forEach { Text("• $it", style = CosmicTheme.typography.body, color = CosmicTheme.colors.textSecondary) }
                    }
                    if (weaknesses.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(CosmicTheme.colors.supernova, RoundedCornerShape(4.dp)))
                            Spacer(Modifier.width(8.dp))
                            Text("Điểm yếu", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold), color = CosmicTheme.colors.supernova)
                        }
                        Spacer(Modifier.height(6.dp))
                        weaknesses.forEach { Text("• $it", style = CosmicTheme.typography.body, color = CosmicTheme.colors.textSecondary) }
                    }
                }
            } }
        }

        // ─── Hành động tiếp theo ───
        item { Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            color = CosmicTheme.colors.nebula,
            border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Hành động tiếp theo", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold), color = CosmicTheme.colors.textPrimary)
                Spacer(Modifier.height(8.dp))
                nextActions.forEachIndexed { i, action ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("${i + 1}.", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold), color = CosmicTheme.colors.plasma, modifier = Modifier.width(20.dp))
                        Text(action, style = CosmicTheme.typography.body, color = CosmicTheme.colors.textSecondary)
                    }
                }
            }
        } }

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
                text = "Chưa có dữ liệu",
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

@Composable
private fun MetaRow(icon: ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = CosmicTheme.colors.textTertiary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(value, style = CosmicTheme.typography.body, color = CosmicTheme.colors.textPrimary)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(bottom = 6.dp)) {
        Text(text = "$label: ", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
        Text(text = value, style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Medium), color = CosmicTheme.colors.textPrimary)
    }
}

@Composable
private fun ScoreBadge(label: String, score: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$score",
                style = CosmicTheme.typography.metric.copy(fontSize = 22.sp, fontWeight = FontWeight.Black),
                color = color
            )
            Text(
                text = label,
                style = CosmicTheme.typography.label.copy(fontSize = 10.sp),
                color = CosmicTheme.colors.textTertiary
            )
        }
    }
}
