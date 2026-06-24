package vn.edu.uit.devorbit.admin.core.designsystem

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun ShimmerSkeleton(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp = 16.dp,
    shapeRadius: Dp = 8.dp,
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_translate",
    )

    val shimmerColors = listOf(
        SurfaceTertiary,
        SurfaceSecondary,
        SurfaceTertiary,
    )

    Box(
        modifier = modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .clip(RoundedCornerShape(shapeRadius))
            .background(
                Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(translateAnim - 200f, 0f),
                    end = Offset(translateAnim + 200f, 0f),
                )
            ),
    )
}

@Composable
fun CardSkeleton(
    modifier: Modifier = Modifier,
    lines: Int = 3,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ShimmerSkeleton(width = 120.dp, height = 18.dp)
        repeat(lines) {
            ShimmerSkeleton(height = 14.dp)
        }
        ShimmerSkeleton(width = 80.dp, height = 14.dp)
    }
}

@Composable
fun ListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 5,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        repeat(itemCount) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerSkeleton(width = 40.dp, height = 40.dp, shapeRadius = 20.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShimmerSkeleton(width = 160.dp, height = 15.dp)
                    ShimmerSkeleton(width = 100.dp, height = 12.dp)
                }
            }
        }
    }
}

@Composable
fun MetricSkeleton(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(4) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Surface, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ShimmerSkeleton(width = 36.dp, height = 28.dp)
                ShimmerSkeleton(width = 56.dp, height = 10.dp)
            }
        }
    }
}
