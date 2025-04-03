package app.gatherround

import android.webkit.*
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun MetroMapScreenCompose(
    onStationsSelected: (List<String>) -> Unit
) {
    val context = LocalContext.current
    val selectedStations = rememberSaveable { mutableStateListOf<String>() }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        factory = { ctx ->
            WebView(ctx).apply {
                WebView.setWebContentsDebuggingEnabled(true)

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true

                webViewClient = WebViewClient()
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        android.util.Log.d(
                            "WebViewConsole",
                            "${consoleMessage?.message()} (line ${consoleMessage?.lineNumber()})"
                        )
                        return true
                    }
                }

                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onStationClick(stationId: String) {
                        android.util.Log.d("WebViewJS", "Станция нажата: $stationId")

                        post {
                            if (!selectedStations.contains(stationId)) {
                                selectedStations.add(stationId)
                                Toast.makeText(ctx, "Добавлена станция: $stationId", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(ctx, "Станция уже выбрана", Toast.LENGTH_SHORT).show()
                            }

                            if (selectedStations.size >= 2) {
                                onStationsSelected(selectedStations)
                            }
                        }
                    }
                }, "Android")

                loadUrl("file:///android_asset/map/index.html")
            }
        }
    )
}
