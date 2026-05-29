package vn.edu.uit.devorbit.mobile.ui.screen.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.domain.model.LearningTask
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun FocusModeScreen(
    focusTask: LearningTask,
    onComplete: () -> Unit,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Thoát focus",
                    tint = CosmicTheme.colors.textTertiary
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Circular progress
            val progress = if (focusTask.estimatedMinutes > 0) {
                focusTask.achievedMinutes.toFloat() / focusTask.estimatedMinutes.toFloat()
            } else 0f

            val glassBorderColor = CosmicTheme.colors.glassBorder
            val plasmaColor = CosmicTheme.colors.plasma

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    val inset = strokeWidth
                    val arcWidth = size.width - strokeWidth * 2
                    val arcHeight = size.height - strokeWidth * 2
                    // Track
                    drawArc(
                        color = glassBorderColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = Size(arcWidth, arcHeight),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Progress
                    drawArc(
                        color = plasmaColor,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = Size(arcWidth, arcHeight),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${focusTask.achievedMinutes}",
                        color = CosmicTheme.colors.plasma,
                        style = CosmicTheme.typography.metric.copy(fontSize = 40.sp)
                    )
                    Text(
                        text = "/ ${focusTask.estimatedMinutes} phút",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Task title
            Text(
                text = focusTask.title,
                color = CosmicTheme.colors.textPrimary,
                style = CosmicTheme.typography.body.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 28.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tập trung vào nhiệm vụ này",
                style = CosmicTheme.typography.body,
                color = CosmicTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Complete button
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma,
                    contentColor = CosmicTheme.colors.void
                )
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hoàn thành", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Exit button
            TextButton(onClick = onBack) {
                Text("Thoát focus", color = CosmicTheme.colors.textTertiary)
            }
        }
    }
}
