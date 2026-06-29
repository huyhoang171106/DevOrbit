package vn.edu.uit.devorbit.admin.ui.github

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun AutoApprovalScreen(viewModel: AutoApprovalViewModel = hiltViewModel()) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val uriHandler = LocalUriHandler.current

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            ObsidianPageHeader(
                title = "Tự động duyệt kho",
                subtitle = "Chỉ duyệt khi repo, repo khác của chủ sở hữu hoặc bio GitHub có UIT"
            )
            ObsidianDivider()
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = ObsidianShape.md
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = ObsidianPalette.Blue500)
                        Spacer(Modifier.width(8.dp))
                        Text("Lịch tự động", style = ObsidianType.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Text("03:00 UTC · Thứ Bảy hàng tuần", style = ObsidianType.bodyMedium)
                    Text("Repo không có bằng chứng UIT vẫn nằm ở Duyệt kho để xử lý thủ công.", style = ObsidianType.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(
                        onClick = viewModel::runNow,
                        enabled = !state.running,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (state.running) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        else ObsidianButtonText("Chạy duyệt tự động ngay", style = ObsidianType.labelMedium)
                    }
                    state.lastRun?.let {
                        Text(
                            "Đã kiểm tra ${it.checked}, duyệt ${it.approved}, chờ tay ${it.leftForManualReview}",
                            style = ObsidianType.labelMedium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
        state.error?.let { error ->
            item { Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp)) }
        }
        item { ObsidianSectionHeader(title = "Lịch sử tự động duyệt (${state.repos.size})") }
        if (state.loading) {
            item { ObsidianLoadingBox(modifier = Modifier.padding(16.dp)) }
        } else if (state.repos.isEmpty()) {
            item { Text("Chưa có repo nào được tự động duyệt.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(state.repos, key = { it.id }) { repo ->
                ListItem(
                    headlineContent = { Text(repo.githubName ?: repo.githubUrl ?: "Repository", fontWeight = FontWeight.SemiBold) },
                    supportingContent = {
                        Column {
                            Text(repo.reviewNote?.removePrefix("AUTO_APPROVED_UIT: ") ?: "UIT verified")
                            Text(listOfNotNull(repo.courseCode, repo.approvedAt).joinToString(" · "), style = ObsidianType.labelSmall)
                        }
                    },
                    trailingContent = {
                        repo.githubUrl?.let { url ->
                            IconButton(onClick = { uriHandler.openUri(url) }) {
                                Icon(Icons.Rounded.OpenInNew, contentDescription = "Mở GitHub")
                            }
                        }
                    }
                )
                ObsidianDivider()
            }
        }
    }
}
