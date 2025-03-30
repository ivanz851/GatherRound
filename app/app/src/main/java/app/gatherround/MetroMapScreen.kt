package app.gatherround.ui

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.ConsoleMessage
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MetroMapScreen() {
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), // Используем всю высоту
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = true

                    webViewClient = WebViewClient()

                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            android.util.Log.d(
                                "WebViewLog",
                                "${consoleMessage?.message()} -- line ${consoleMessage?.lineNumber()}"
                            )
                            return true
                        }
                    }

                    loadUrl("file:///android_asset/map/index.html")
                    webViewRef.value = this
                }
            }
        )
    }
}
