package vn.edu.uit.devorbit.admin.design

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Orbital Intelligence motion tokens.
 *
 * Durations:
 *  - Instant feedback:      80-120ms
 *  - Small state change:   160-220ms
 *  - Container transform:  280-420ms
 *  - Full-screen transit:  350-500ms
 */
object OrbitMotion {
    val instant = tween<Float>(durationMillis = 100, easing = FastOutSlowInEasing)
    val quick = tween<Float>(durationMillis = 200, easing = FastOutSlowInEasing)
    val container = tween<Float>(durationMillis = 360, easing = FastOutSlowInEasing)
    val screen = tween<Float>(durationMillis = 420, easing = FastOutSlowInEasing)

    val springQuick = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    val springSoft = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow,
    )

    val springStiff = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh,
    )
}
