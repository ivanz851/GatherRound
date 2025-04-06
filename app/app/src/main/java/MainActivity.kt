/*
 * Copyright (c) 2019.
 * Bismillahir Rahmanir Rahim,
 * Developer : Saadat Sayem
 */

package app.gatherround

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.gatherround.R
import com.sam43.svginteractiondemo.FileDownloaderVM
import app.gatherround.databinding.ActivityMainBinding
import com.sam43.svginteractiondemo.getHTMLBody
import com.sam43.svginteractiondemo.toast

class MainActivity : AppCompatActivity() {

    companion object {
        const val JAVASCRIPT_OBJ = "javascript_obj"
        const val BASE_URL = "file:///android_asset/web/"
    }

    private val fileDownloaderVM: FileDownloaderVM by viewModels()
    private lateinit var pd: ProgressDialog
    private lateinit var binding: ActivityMainBinding

    private fun initProgressDialog() {
        pd = ProgressDialog(this)
        pd.setCancelable(false)
        pd.isIndeterminate = true
        pd.setTitle(getString(R.string.rendering_svg))
        pd.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButtonActions()
        setupWebLayout()
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun setupButtonActions() {
        initProgressDialog()
        binding.btnZoomIn.setOnClickListener {
            binding.webView.zoomIn()
        }

        binding.btnZoomOut.setOnClickListener {
            binding.webView.zoomOut()
        }

        binding.btnSendToWeb.setOnClickListener {
            val text = binding.etSendDataField.text.toString()
            binding.webView.evaluateJavascript(
                "javascript: updateFromAndroid(\"$text\")",
                null
            )
        }
    }

    override fun onResume() {
        super.onResume()
        callVM()
    }

    private fun callVM() {
        val url = "https://raw.githubusercontent.com/ivanz851/GatherRound/refs/heads/implement_interactve_metro_map/app/app/src/main/assets/initial.svg"
        try {
            fileDownloaderVM.downloadFileFromServer(url)
                .observe(this, Observer { responseBody ->
                    val svgString = responseBody.string()
                    binding.webView.loadDataWithBaseURL(
                        BASE_URL,
                        getHTMLBody(svgString),
                        "text/html",
                        "UTF-8",
                        null
                    )
                    pd.dismiss()
                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("AddJavascriptInterface", "SetJavaScriptEnabled")
    private fun setupWebLayout() {
        val webView = binding.webView

        webView.setInitialScale(150)
        webView.settings.apply {
            builtInZoomControls = true
            displayZoomControls = false
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        webView.addJavascriptInterface(
            JavaScriptInterface(),
            JAVASCRIPT_OBJ
        )
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                injectJavaScriptFunction()
            }
        }
        webView.webChromeClient = WebChromeClient()
    }

    private fun injectJavaScriptFunction() {
        val textToAndroid = "javascript: window.androidObj.textToAndroid = function(message) { " +
                JAVASCRIPT_OBJ + ".textFromWeb(message) }"
        binding.webView.loadUrl(textToAndroid)
    }


    inner class JavaScriptInterface {
        @SuppressLint("SetTextI18n")
        @JavascriptInterface
        fun textFromWeb(fromWeb: String) {
            runOnUiThread {
                binding.tvStateName.text = fromWeb
            }
            toast(fromWeb)
        }
    }


    override fun onDestroy() {
        binding.webView.removeJavascriptInterface(JAVASCRIPT_OBJ)
        super.onDestroy()
    }
}