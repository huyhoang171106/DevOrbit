package vn.edu.uit.devorbit.mobile.ui.components

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ShaderBrush
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

private const val COSMIC_SHADER = """
    uniform float2 iResolution;
    uniform float iTime;
    
    float noise(float2 p) {
        return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        
        // Base cosmic void - very deep indigo/black
        half3 color = half3(0.02, 0.02, 0.04); 
        
        // Nebula Pulse - slow, atmospheric color shifts
        float pulse = sin(iTime * 0.3 + uv.x * 1.5 + uv.y * 1.5) * 0.5 + 0.5;
        color += half3(0.04, 0.02, 0.08) * pulse;
        
        // Cosmic Grain - subtle noise to prevent banding and add texture
        float n = noise(uv + iTime * 0.01);
        color += half3(n * 0.02);
        
        return half4(color, 1.0);
    }
"""

@Composable
fun CosmicBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            CosmicShaderBackground()
        } else {
            // Fallback for older Android versions using the design system's radial depth
            Box(modifier = Modifier.fillMaxSize().background(CosmicTheme.gradients.voidDepth))
        }
        content()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun CosmicShaderBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "CosmicPulse")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Time"
    )

    val shader = remember { RuntimeShader(COSMIC_SHADER) }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        shader.setFloatUniform("iResolution", size.width, size.height)
        shader.setFloatUniform("iTime", time)
        drawRect(brush = ShaderBrush(shader))
    }
}
