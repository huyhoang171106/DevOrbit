package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

data class ParsedSyllabus(
    val courseName: String = "",
    val deadlines: List<String> = emptyList(),
    val topics: List<String> = emptyList(),
    val assignments: List<String> = emptyList()
)

@Composable
fun SyllabusParserScreen(
    text: String,
    onTextChange: (String) -> Unit,
    onParse: () -> Unit,
    parsedResult: ParsedSyllabus?,
    onCreateTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Nhập syllabus",
            style = CosmicTheme.typography.display,
            color = CosmicTheme.colors.textPrimary,
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = CosmicTheme.colors.nebula,
            border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Dán nội dung syllabus vào ô bên dưới",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp, max = 280.dp),
                    placeholder = {
                        Text(
                            text = "VD: Môn: Cơ sở dữ liệu\nTuần 1: Giới thiệu\nTuần 2: Mô hình ER\nGiữa kỳ: 20/03\n...",
                            color = CosmicTheme.colors.textTertiary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CosmicTheme.colors.textPrimary,
                        unfocusedTextColor = CosmicTheme.colors.textPrimary,
                        focusedBorderColor = CosmicTheme.colors.plasma,
                        unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                        cursorColor = CosmicTheme.colors.plasma
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onParse,
                    enabled = text.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicTheme.colors.plasma,
                        contentColor = CosmicTheme.colors.void
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text("Phân tích", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (parsedResult != null) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Course name
                if (parsedResult.courseName.isNotBlank()) {
                    ResultCard(label = "Môn học", value = parsedResult.courseName, accent = CosmicTheme.colors.plasma)
                }

                // Deadlines
                if (parsedResult.deadlines.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CosmicTheme.colors.nebula,
                        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Hạn nộp",
                                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                                color = CosmicTheme.colors.aurora
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            parsedResult.deadlines.forEach { d ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = CosmicTheme.colors.aurora,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = d,
                                        style = CosmicTheme.typography.body,
                                        color = CosmicTheme.colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Topics
                if (parsedResult.topics.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CosmicTheme.colors.nebula,
                        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Chủ đề",
                                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                                color = CosmicTheme.colors.plasma
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            parsedResult.topics.forEach { t ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.List,
                                        contentDescription = null,
                                        tint = CosmicTheme.colors.plasma,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = t,
                                        style = CosmicTheme.typography.body,
                                        color = CosmicTheme.colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Assignments
                if (parsedResult.assignments.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CosmicTheme.colors.nebula,
                        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Bài tập / Kiểm tra",
                                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                                color = CosmicTheme.colors.plasma
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            parsedResult.assignments.forEach { a ->
                                Text(
                                    text = a,
                                    style = CosmicTheme.typography.body,
                                    color = CosmicTheme.colors.textPrimary,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Create tasks button
                OutlinedButton(
                    onClick = onCreateTasks,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, CosmicTheme.colors.plasma.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = CosmicTheme.colors.plasma
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Tạo task từ syllabus",
                        color = CosmicTheme.colors.plasma,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ResultCard(label: String, value: String, accent: androidx.compose.ui.graphics.Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                color = accent
            )
        }
    }
}
