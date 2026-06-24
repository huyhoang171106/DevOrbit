package vn.edu.uit.devorbit.admin.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.ui.theme.*

enum class StatusType {
    SUCCESS, WARNING, DANGER, INFO, NEUTRAL
}

data class StatusConfig(
    val label: String,
    val type: StatusType,
    val filled: Boolean = false,
)

@Composable
fun StatusBadge(
    label: String,
    type: StatusType,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
) {
    val (bgColor, textColor) = when (type) {
        StatusType.SUCCESS -> if (filled) Success to Color.White else SuccessSoft to Success
        StatusType.WARNING -> if (filled) Warning to Color.White else WarningSoft to Warning
        StatusType.DANGER -> if (filled) Danger to Color.White else DangerSoft to Danger
        StatusType.INFO -> if (filled) Info to Color.White else InfoSoft to Info
        StatusType.NEUTRAL -> if (filled) SurfaceTertiary to TextSecondary else SurfaceTertiary to TextSecondary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor,
        )
    }
}

@Composable
fun SeverityIndicator(
    severity: String,
    modifier: Modifier = Modifier,
) {
    val type = when (severity.uppercase()) {
        "HIGH", "CRITICAL", "ERROR" -> StatusType.DANGER
        "MEDIUM", "WARNING" -> StatusType.WARNING
        "LOW", "INFO" -> StatusType.INFO
        else -> StatusType.NEUTRAL
    }
    StatusBadge(label = severity, type = type, modifier = modifier)
}

@Composable
fun StatusDot(
    type: StatusType,
    modifier: Modifier = Modifier,
) {
    val color = when (type) {
        StatusType.SUCCESS -> Success
        StatusType.WARNING -> Warning
        StatusType.DANGER -> Danger
        StatusType.INFO -> Info
        StatusType.NEUTRAL -> TextMuted
    }
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color),
    )
}
