package vn.edu.uit.devorbit.admin.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.admin.design.OrbitColors
import vn.edu.uit.devorbit.admin.design.metricLabelStyle
import vn.edu.uit.devorbit.admin.design.metricLargeStyle
import vn.edu.uit.devorbit.admin.design.metricTextStyle

/**
 * Single focused metric display — large value + label.
 * Used for streak, hours, completion rate, etc.
 */
@Composable
fun MetricDisplay(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = OrbitColors.TextPrimary,
    labelColor: Color = OrbitColors.TextMuted,
    large: Boolean = false,
) {
    Column(modifier = modifier) {
        Text(
            text = value,
            style = if (large) metricLargeStyle else metricTextStyle,
            color = valueColor,
            fontFamily = FontFamily.Monospace,
        )
        Text(
            text = label,
            style = metricLabelStyle,
            color = labelColor,
        )
    }
}

/**
 * Horizontal row of metrics, evenly distributed.
 */
@Composable
fun MetricRow(
    metrics: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        metrics.forEachIndexed { index, (value, label) ->
            MetricDisplay(value = value, label = label)
            if (index < metrics.size - 1) {
                TechnicalVerticalDivider(
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
        }
    }
}
