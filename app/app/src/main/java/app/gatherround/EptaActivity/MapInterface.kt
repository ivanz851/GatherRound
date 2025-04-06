package app.gatherround.EptaActivity

import android.webkit.JavascriptInterface

class MapInterface(
    private val onStationClicked: (stationId: String, status: String) -> Unit
) {
    @JavascriptInterface
    fun onStationClick(data: String) {
        val parts = data.split(":")
        if (parts.size == 2) {
            onStationClicked(parts[0], parts[1])
        }
    }
}
