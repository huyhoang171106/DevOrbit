package vn.edu.uit.devorbit.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.admin.design.OrbitColors

/**
 * A semantic section within an AI tutor response.
 *
 * Types: Explanation, KeyConcept, Example, Warning, NextAction
 */
enum class ResponseSectionType(val label: String, val color: Color) {
    Explanation("EXPLANATION", OrbitColors.PrimaryElectricBlue),
    KeyConcept("KEY CONCEPT", OrbitColors.CyanSignal),
    Example("EXAMPLE", OrbitColors.Success),
    Warning("WARNING", OrbitColors.Warning),
    NextAction("NEXT ACTION", OrbitColors.PrimaryBright),
}

@Composable
fun TutorResponseSection(
    type: ResponseSectionType,
    title: String,
    content: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(OrbitColors.SurfaceBase)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Section header
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(type.color),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = type.label,
                style = MaterialTheme.typography.labelSmall,
                color = type.color,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
            )
        }

        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = OrbitColors.TextPrimary,
            )
        }

        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = OrbitColors.TextSecondary,
        )
    }
}

/**
 * Response action bar — copy, save, listen.
 */
@Composable
fun ResponseActions(
    modifier: Modifier = Modifier,
    onCopy: () -> Unit = {},
    onSave: () -> Unit = {},
    onListen: () -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onCopy()
        }) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = OrbitColors.TextMuted,
                modifier = Modifier.size(18.dp),
            )
        }
        IconButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onSave()
        }) {
            Icon(
                imageVector = Icons.Outlined.BookmarkBorder,
                contentDescription = "Save",
                tint = OrbitColors.TextMuted,
                modifier = Modifier.size(18.dp),
            )
        }
        IconButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onListen()
        }) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Listen",
                tint = OrbitColors.TextMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
