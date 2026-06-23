package vn.edu.uit.devorbit.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import vn.edu.uit.devorbit.admin.ui.AdminRoot
import vn.edu.uit.devorbit.admin.ui.theme.DevOrbitAdminTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DevOrbitAdminTheme {
                AdminRoot()
            }
        }
    }
}
