package app.gatherround.stations_input

import android.webkit.JavascriptInterface
import android.webkit.WebView

class MapInterface(
    private val webView: WebView,
    private val onStationClicked: (stationId: String, status: String) -> Unit,
    private val onStationClickedWithResult: (stationId: String, status: String) -> Boolean
) {
    @JavascriptInterface
    fun onStationClick(data: String) {
        val parts = data.split(":")
        if (parts.size == 2) {
            onStationClicked(parts[0], parts[1])
        }
    }

    @JavascriptInterface
    fun onStationClickedWithResult(stationId: String, status: String, callbackName: String) {
        val result = onStationClickedWithResult(stationId, status)
        val js = "$callbackName('${result.toString()}')"
        webView.post {
            webView.evaluateJavascript(js, null)
        }
    }
}
