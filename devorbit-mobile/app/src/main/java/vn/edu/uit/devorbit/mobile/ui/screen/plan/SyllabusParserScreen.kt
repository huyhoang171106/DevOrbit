package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

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
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input field
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Dán nội dung syllabus vào ô bên dưới",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp, max = 280.dp),
                    placeholder = {
                        Text(
                            text = "VD: Môn: Cơ sở dữ liệu\nTuần 1: Giới thiệu\nTuần 2: Mô hình ER\nGiữa kỳ: 20/03\n...",
                            color = TextSecondary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CosmicNebulaPurple,
                        unfocusedBorderColor = GlassBorder,
                        cursorColor = CosmicNebulaPurple
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onParse,
                    enabled = text.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicNebulaPurple,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Phân tích")
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
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Môn học",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = parsedResult.courseName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                // Deadlines
                if (parsedResult.deadlines.isNotEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Hạn nộp",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CosmicGlowBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        parsedResult.deadlines.forEach { d ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = CosmicGlowBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = d, fontSize = 13.sp, color = TextPrimary)
                            }
                        }
                    }
                }

                // Topics
                if (parsedResult.topics.isNotEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Chủ đề",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CosmicGlowPurple
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        parsedResult.topics.forEach { t ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.List,
                                    contentDescription = null,
                                    tint = CosmicGlowPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = t, fontSize = 13.sp, color = TextPrimary)
                            }
                        }
                    }
                }

                // Assignments
                if (parsedResult.assignments.isNotEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Bài tập / Kiểm tra",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CosmicGlowPurple
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        parsedResult.assignments.forEach { a ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(text = a, fontSize = 13.sp, color = TextPrimary)
                            }
                        }
                    }
                }

                // Create tasks button
                Button(
                    onClick = onCreateTasks,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicGlowBlue.copy(alpha = 0.15f),
                        contentColor = CosmicGlowBlue
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tạo task từ syllabus")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
