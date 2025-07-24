package com.vidz.metroll_mobile.presentation

import MetrollApp
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.WorkManager
import com.vidz.theme.MetrollTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var workManager: WorkManager
    
    private var shouldNavigateToCart = false
    private var isFromNotification = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetrollApp(
                shouldNavigateToCart = shouldNavigateToCart,
                isFromNotification = isFromNotification
            )
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Important: update the intent

        // Update the compose content with new navigation state
        setContent {
            MetrollApp(
                shouldNavigateToCart = shouldNavigateToCart,
                isFromNotification = isFromNotification
            )
        }
    }
    

}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MetrollTheme {
        Greeting("Android")
    }
}
