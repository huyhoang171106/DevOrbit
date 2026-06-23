package vn.edu.uit.devorbit.admin.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.design.OrbitColors

@Composable
fun MorphingContainer(
    targetState: Any,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(14.dp),
    content: @Composable (Any) -> Unit,
) {
    val transition = ContentTransform(
        targetContentEnter = fadeIn(animationSpec = tween(280)) +
            slideInVertically(animationSpec = tween(280)) { it / 10 },
        initialContentExit = fadeOut(animationSpec = tween(200)) +
            slideOutVertically(animationSpec = tween(200)) { it / 10 },
        sizeTransform = SizeTransform(
            clip = false,
            sizeAnimationSpec = { _, _ -> tween(360) },
        ),
    )

    AnimatedContent(
        targetState = targetState,
        transitionSpec = { transition },
        modifier = modifier,
        label = "morphContainer",
    ) { state ->
        Box(
            modifier = Modifier
                .clip(shape)
                .background(OrbitColors.SurfaceBase),
        ) {
            content(state)
        }
    }
}
