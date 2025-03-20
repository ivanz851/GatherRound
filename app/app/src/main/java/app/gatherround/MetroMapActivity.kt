package app.gatherround

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class MetroMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)

        // Загружаем SVG из assets
        webView.settings.allowFileAccess = true
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/metromap.svg")
    }
}