package app.gatherround

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MetroMapActivity: AppCompatActivity() {

    private lateinit var webView: WebView
    private val chosenStations = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(true)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = WebViewClient()

        webView.addJavascriptInterface(JSBridge(), "Android")

        webView.loadUrl("file:///android_asset/map/index.html")
    }

    inner class JSBridge {
        @JavascriptInterface
        fun onStationClick(stationId: String) {
            Log.d("MetroMapActivity", "Станция нажата: $stationId")

            runOnUiThread {
                if (!chosenStations.contains(stationId)) {
                    chosenStations.add(stationId)
                    Toast.makeText(
                        this@MetroMapActivity,
                        "Добавлена станция: $stationId",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MetroMapActivity,
                        "Станция уже выбрана",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (chosenStations.size >= 2) {
                    goToInputScreen()
                }
            }
        }
    }

    private fun goToInputScreen() {
        val intent = Intent(this, StationInputActivity::class.java)
        intent.putStringArrayListExtra("selected_station_ids", ArrayList(chosenStations))
        startActivity(intent)
    }
}
