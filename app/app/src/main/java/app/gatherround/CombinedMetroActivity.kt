package app.gatherround

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

class CombinedMetroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WebView.setWebContentsDebuggingEnabled(true)
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "CombinedMetroActivity запущена", Toast.LENGTH_SHORT).show()
        Log.d("MyApp", "CombinedMetroActivity запущена")

        setContent {
            CombinedMetroScreen()
        }
    }
}

@Composable
fun CombinedMetroScreen() {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        val webView = remember {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                settings.domStorageEnabled = true

                webViewClient = WebViewClient()

                loadUrl("file:///android_asset/map/index.html")
            }
        }
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}
