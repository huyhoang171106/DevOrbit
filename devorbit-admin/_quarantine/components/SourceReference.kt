package vn.edu.uit.devorbit.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.design.OrbitColors
import vn.edu.uit.devorbit.admin.design.OrbitShapes

/**
 * Data for a trusted reference source.
 */
data class SourceData(
    val title: String,
    val type: String,       // "Paper", "Documentation", "Article", "Repository"
    val source: String,     // "arXiv:2401.12345", "developer.android.com", "github.com/..."
    val relevance: Float,   // 0.0–1.0
    val url: String = "",
)

/**
 * Reference source card — looks like a trustworthy reference object.
 */
@Composable
fun SourceReference(
    source: SourceData,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(OrbitColors.SurfaceBase)
            .border(0.5.dp, OrbitColors.BorderSubtle.copy(alpha = 0.5f), shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClick()
                        },
                    )
                } else Modifier
            )
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Outlined.Description,
            contentDescription = null,
            tint = OrbitColors.PrimaryElectricBlue,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = source.title,
                style = MaterialTheme.typography.bodyMedium,
                color = OrbitColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = source.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = OrbitColors.PrimaryElectricBlue,
                    fontFamily = FontFamily.Monospace,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = source.source,
                    style = MaterialTheme.typography.bodySmall,
                    color = OrbitColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.Outlined.OpenInNew,
                contentDescription = "Open source",
                tint = OrbitColors.TextMuted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
