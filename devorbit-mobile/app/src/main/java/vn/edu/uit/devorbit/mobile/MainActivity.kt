package vn.edu.uit.devorbit.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import vn.edu.uit.devorbit.mobile.ui.DevOrbitApp
import vn.edu.uit.devorbit.mobile.ui.theme.DevOrbitTheme
import vn.edu.uit.devorbit.mobile.ui.components.CosmicBackground

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DevOrbitTheme {
                CosmicBackground {
                    DevOrbitApp()
                }
            }
        }
    }
}
