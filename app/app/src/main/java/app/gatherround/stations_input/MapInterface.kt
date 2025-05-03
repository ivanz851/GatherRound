package app.gatherround.stations_input

import android.webkit.JavascriptInterface
import android.webkit.WebView

/**
 * JavaScript-bridge между интерактивной SVG-картой (работающей внутри `WebView`)
 * и слоем Compose-UI.
 *
 * SVG-скрипт вызывает методы `androidObj.onStationClick(data)` и
 * `androidObj.onStationClickedWithResult(id, status, callbackName)`; ответы
 * обрабатываются *Kotlin-коллбэками* и при необходимости передаются обратно в JS.
 *
 * @param webView                      хост-`WebView`, через который исполняется JS-код
 * @param onStationClicked             коллбэк «без подтверждения» — просто сообщает
 *                                     о клике ( `stationId`, `status` )
 * @param onStationClickedWithResult   коллбэк, который должен *сообщить, удался ли
 *                                     выбор* (true/false). Результат синхронно
 *                                     возвращается в JS методом-обраткой.
 */
class MapInterface(
    private val webView: WebView,
    private val onStationClicked: (stationId: String, status: String) -> Unit,
    private val onStationClickedWithResult: (stationId: String, status: String) -> Boolean
) {
    /**
     * Принимает строку формата `"station-123:select"` / `"station-123:deselect"`,
     * разбивает её на id и статус и передаёт в [onStationClicked].
     */
    @JavascriptInterface
    fun onStationClick(data: String) {
        val parts = data.split(":")
        if (parts.size == 2) {
            onStationClicked(parts[0], parts[1])
        }
    }

    /**
     * Вызывается из JS, когда скрипту нужен «ответ» (успех/не-успех выбора).
     *
     * После получения булевого результата формирует строку
     * `callbackName('true'|'false')` и исполняет её в том же `WebView`.
     */
    @JavascriptInterface
    fun onStationClickedWithResult(stationId: String, status: String, callbackName: String) {
        val result = onStationClickedWithResult(stationId, status)
        val js = "$callbackName('${result.toString()}')"
        webView.post {
            webView.evaluateJavascript(js, null)
        }
    }
}
