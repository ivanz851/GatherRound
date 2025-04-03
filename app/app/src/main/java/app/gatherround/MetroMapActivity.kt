package app.gatherround

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

class MetroMapActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(true)

        setContent {
            WebMapScreen()
        }
    }
}

@Composable
fun WebMapScreen() {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.allowUniversalAccessFromFileURLs = true

                settings.setSupportZoom(false) // отключи zoomControls WebView
                settings.builtInZoomControls = false
                settings.displayZoomControls = false

                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true


                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()

                val html = context.assets.open("map/index.html")
                    .bufferedReader()
                    .use { it.readText() }

                loadDataWithBaseURL(
                    "file:///android_asset/map/",
                    html,
                    "text/html",
                    "utf-8",
                    null
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
