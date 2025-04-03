package app.gatherround.ui

import android.webkit.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MetroMapScreenSimple() {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), // Карта на весь экран
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = true
                    settings.domStorageEnabled = true

                    // Выводим консоль браузера в Logcat (полезно для отладки)
                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            android.util.Log.d(
                                "WebViewLog",
                                "${consoleMessage?.message()} -- line ${consoleMessage?.lineNumber()}"
                            )
                            return true
                        }
                    }

                    // Обычный WebViewClient (важен, иначе некоторые загрузки могут не работать)
                    webViewClient = WebViewClient()

                    // Добавляем интерфейс (если нужен в будущем)
                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onStationClick(stationId: String) {
                            android.util.Log.d("MetroMapScreen", "Clicked: $stationId")
                        }
                    }, "Android")

                    // Загружаем карту из assets
                    loadUrl("file:///android_asset/map/index.html")
                }
            }
        )
    }
}
