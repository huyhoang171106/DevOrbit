package vn.edu.uit.devorbit.admin.ui.github

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoCandidateResponse
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*
import vn.edu.uit.devorbit.admin.ui.theme.repoStatusLabel

@Composable
fun GithubScreen(
    viewModel: GithubViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var courseIdInput by remember { mutableStateOf("") }
    var queryInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Quét GitHub",
            subtitle = "Tìm & quét kho lưu trữ từ GitHub"
        )

        ObsidianDivider()

        // Scan form
        ObsidianSectionHeader(
            title = "Quét theo môn học",
            action = {
                Button(
                    onClick = {
                        val courseId = courseIdInput.toLongOrNull() ?: return@Button
                        if (queryInput.isNotBlank()) {
                            viewModel.scan(courseId, queryInput.trim())
                        }
                    },
                    enabled = !state.isScanning &&
                        courseIdInput.isNotBlank() &&
                        queryInput.isNotBlank(),
                    shape = ObsidianShape.sm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (state.isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Quét", style = ObsidianType.labelMedium)
                    }
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = courseIdInput,
                onValueChange = { courseIdInput = it.filter { c -> c.isDigit() } },
                label = { Text("Mã môn học") },
                placeholder = { Text("123") },
                modifier = Modifier.width(120.dp),
                shape = ObsidianShape.sm,
                textStyle = ObsidianType.bodyMedium,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = queryInput,
                onValueChange = { queryInput = it },
                label = { Text("Truy vấn") },
                placeholder = { Text("kotlin compose") },
                modifier = Modifier.weight(1f),
                shape = ObsidianShape.sm,
                textStyle = ObsidianType.bodyMedium,
                singleLine = true
            )
        }

        Spacer(Modifier.height(12.dp))
        ObsidianDivider()

        // Primary action: Scan All
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.scanAll() },
                enabled = !state.isScanning,
                modifier = Modifier.weight(1f),
                shape = ObsidianShape.sm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ObsidianPalette.Blue500
                )
            ) {
                if (state.isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Đang quét...", style = ObsidianType.labelMedium)
                } else {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Quét tất cả", style = ObsidianType.labelMedium)
                }
            }

            OutlinedButton(
                onClick = { viewModel.clearLogs() },
                enabled = state.scanLogs.isNotEmpty(),
                shape = ObsidianShape.sm,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ObsidianPalette.Gray500
                )
            ) {
                Icon(Icons.Rounded.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Xoá log", style = ObsidianType.labelMedium)
            }
        }

        // Error banner
        state.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = ObsidianShape.sm,
                colors = CardDefaults.cardColors(
                    containerColor = ObsidianPalette.Red50.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = ObsidianPalette.Red500,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = error,
                        style = ObsidianType.bodySmall,
                        color = ObsidianPalette.Red700,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // Success message banner
        state.scanMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = ObsidianShape.sm,
                colors = CardDefaults.cardColors(
                    containerColor = ObsidianPalette.Green50.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = ObsidianPalette.Green500,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = message,
                        style = ObsidianType.bodySmall,
                        color = ObsidianPalette.Green700,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.clearScanMessage() },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Đóng",
                            tint = ObsidianPalette.Green500.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // Scan result cards
        state.scanResult?.let { results ->
            if (results.isNotEmpty()) {
                ObsidianSectionHeader(
                    title = "Kết quả quét (${results.size})",
                    action = {
                        ObsidianBadge(
                            text = "${results.size} kho",
                            color = ObsidianPalette.Green500
                        )
                    }
                )
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(results, key = { it.id }) { candidate ->
                        ScanResultCard(candidate = candidate)
                    }
                }
            } else {
                ObsidianEmptyState(
                    message = "Không tìm thấy kết quả phù hợp",
                    subtitle = "Thử truy vấn khác hoặc quét tất cả",
                    icon = Icons.Rounded.SearchOff
                )
            }
        }

        // Scan logs
        if (state.scanResult == null) {
            ObsidianSectionHeader(
                title = "Nhật ký quét",
                action = {
                    if (state.scanLogs.isNotEmpty()) {
                        ObsidianBadge(
                            text = "${state.scanLogs.size}",
                            color = ObsidianPalette.Amber500
                        )
                    }
                }
            )

            if (state.isLoading) {
                ObsidianLoadingBox()
            } else if (state.scanLogs.isEmpty()) {
                ObsidianEmptyState(
                    message = "Chưa có log nào",
                    subtitle = "Quét kho lưu trữ để xem nhật ký",
                    icon = Icons.Rounded.List
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(state.scanLogs, key = { index, log -> "$index:$log" }) { _, log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = ObsidianShape.xs,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = log,
                                style = ObsidianType.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Scan Result Card ───────────────────────────────────────────────────────
@Composable
private fun ScanResultCard(candidate: RepoCandidateResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: avatar + name + status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ObsidianAvatar(name = candidate.githubOwner ?: "?")

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = candidate.githubName ?: "unknown",
                        style = ObsidianType.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = candidate.githubOwner ?: "",
                        style = ObsidianType.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status badge
                ObsidianBadge(
                    text = repoStatusLabel(candidate.status),
                    color = when (candidate.status?.uppercase()) {
                        "APPROVED" -> ObsidianPalette.Green500
                        "REJECTED" -> ObsidianPalette.Red500
                        "PENDING", null -> ObsidianPalette.Amber500
                        else -> ObsidianPalette.Gray500
                    }
                )
            }

            // Description
            candidate.description?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    style = ObsidianType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            // Meta: language, stars, forks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                candidate.primaryLanguage?.let { lang ->
                    ObsidianBadge(text = lang, color = ObsidianPalette.Blue500)
                }
                if (candidate.stars > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Star,
                            contentDescription = null,
                            tint = ObsidianPalette.Amber500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${candidate.stars}",
                            style = ObsidianType.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (candidate.forks > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.CallSplit,
                            contentDescription = null,
                            tint = ObsidianPalette.Gray500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${candidate.forks}",
                            style = ObsidianType.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Topics
            candidate.topics?.takeIf { it.isNotBlank() }?.let { topics ->
                Spacer(Modifier.height(6.dp))
                Text(
                    text = topics,
                    style = ObsidianType.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // README excerpt
            candidate.readmeExcerpt?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    style = ObsidianType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
