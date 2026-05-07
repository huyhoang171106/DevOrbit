package vn.edu.uit.devorbit.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicDeepSpace

@Composable
fun CosmicBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1B25),
                        CosmicDeepSpace,
                        Color(0xFF000000)
                    )
                )
            )
    ) {
        content()
    }
}
