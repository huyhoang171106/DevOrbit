package vn.edu.uit.devorbit.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun CosmicBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicTheme.gradients.voidDepth)
    ) {
        content()
    }
}
