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
    modifier: Modifier = Modifier,
    onWebViewCreated: (WebView) -> Unit = {}
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

                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                settings.setSupportZoom(true)


                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        view.post {
                            val mapW = 2850
                            val mapH = 3449

                            val dx = mapW / 2 + 200
                            val dy = mapH / 2 + 600

                            view.scrollTo(dx, dy)
                        }
                    }
                }
                webChromeClient = WebChromeClient()

                addJavascriptInterface(
                    MapInterface(
                        webView = this,
                        onStationClicked = onStationClicked,
                        onStationClickedWithResult = onStationClickedWithResult,
                    ),
                    "androidObj"
                )

                onWebViewCreated(this)
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
