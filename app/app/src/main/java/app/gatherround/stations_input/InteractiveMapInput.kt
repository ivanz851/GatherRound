package app.gatherround.stations_input

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun InteractiveMapInput(
    htmlContent: String?,
    onStationClicked: (String, String) -> Unit,
    onStationClickedWithResult: (String, String) -> Boolean,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.allowUniversalAccessFromFileURLs = true
                settings.setSupportZoom(false)
                settings.builtInZoomControls = false
                settings.displayZoomControls = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true

                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()

                addJavascriptInterface(
                    MapInterface(
                        webView = this,
                        onStationClicked = onStationClicked,
                        onStationClickedWithResult = onStationClickedWithResult
                    ),
                    "androidObj"
                )
            }
        },
        update = { webView ->
            htmlContent?.let {
                webView.loadDataWithBaseURL(
                    "file:///android_asset/web/",
                    it,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        modifier = modifier
    )
}
