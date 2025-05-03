package app.gatherround.stations_input

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Оборачивает **SVG-карту метро** (HTML) во `WebView` и встраивает её в Compose-дерево.
 *
 * @param htmlContent      готовая HTML-строка (или `null`, если ещё не загружена).
 *                         Загружается через `loadDataWithBaseURL`.
 * @param onStationClicked callback из JS (вызов androidObj.click(id, status)),
 *                         который меняет состояние выбранных станций *без* ожидания
 *                         подтверждения (для подсветки в реальном времени).
 * @param onStationClickedWithResult аналогичный callback, но возвращает `Boolean`,
 *                         чтобы JS понял, удалось ли добавить станцию (лимит = 6).
 * @param modifier         обычный `Modifier` (занять высоту/ширину, отступы...)
 * @param onWebViewCreated даёт внешний доступ к созданному `WebView`
 *                         (нужно, чтобы позднее вызвать JS-функции `selectStation()`
 *                         или `deselectStation()` из других composable’ов).
 *
 *
 * * После полной загрузки страницы `onPageFinished` искусственно «скроллит»
 *   WebView к центру схемы (dx, dy), чтобы пользователю сразу была видна
 *   центральная часть карты.
 * * Зум вкл/выкл оставлен вручную: pinch-zoom разрешён, а кнопки-лупы скрыты.
 */
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
                /* --- базовые настройки WebView --- */
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.allowUniversalAccessFromFileURLs = true
                settings.setSupportZoom(false)
                settings.builtInZoomControls = false
                settings.displayZoomControls = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true

                /* --- жестовый зум ON, кнопки зума OFF --- */
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                settings.setSupportZoom(true)

                /* --- авто-скролл к центру карты после полной загрузки --- */
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        super.onPageFinished(view, url)
                        view.post {
                            val mapW = 2850
                            val mapH = 3449

                            val dx = mapW / 2 + 200
                            val dy = mapH / 2 + 200

                            view.scrollTo(dx, dy)
                        }
                    }
                }
                webChromeClient = WebChromeClient()

                /* --- подключаем JS-bridge --- */
                addJavascriptInterface(
                    MapInterface(
                        webView = this,
                        onStationClicked = onStationClicked,
                        onStationClickedWithResult = onStationClickedWithResult,
                    ),
                    "androidObj"
                )

                /* отдаём ссылку наружу (нужно другим composable’ам) */
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
