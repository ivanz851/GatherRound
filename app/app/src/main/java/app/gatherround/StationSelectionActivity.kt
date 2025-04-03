package app.gatherround

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.places.PlacesData
import kotlinx.coroutines.*

class StationSelectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val metroData = MetroData.loadFromAssets(applicationContext, "get-scheme-metadata.json")
        val stationsNames = metroData.stationsNames
        val graph = MetroGraph(metroData)

        WebView.setWebContentsDebuggingEnabled(true)

        setContent {
            MaterialTheme {
                StationSelectionScreen(
                    metroData = metroData,
                    stationsNames = stationsNames,
                    graph = graph
                )
            }
        }
    }
}

class MapInterface(
    private val onStationClicked: (String) -> Unit
) {

    @JavascriptInterface
    fun onStationClick(stationId: String) {
        Log.d("WebView", "Станция выбрана: $stationId")

        onStationClicked(stationId)
    }
}


@Composable
fun StationSelectionScreen(
    metroData: MetroData,
    stationsNames: List<Pair<String, String>>,
    graph: MetroGraph
) {
    val stations = remember { mutableStateListOf<app.gatherround.metro.Station>() }

    val onStationClicked = fun(stationStr: String) {
        val numericId = stationStr.removePrefix("station-").toIntOrNull()
        if (numericId == null) return

        val station = metroData.stations.find { it.id == numericId }
        if (station != null && stations.size < 6) {
            stations.add(station)
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        // Вставляем блок с TextField’ами
        StationInputBlock(
            stations = stations,
            stationsNames = stationsNames,
            metroData = metroData,
            graph = graph,
            modifier = Modifier
                .weight(0.4f)
        )

        // Добавляем карту
        // Можно сделать разделитель (Divider) или Spacer, если нужно
        Divider()
        WebMapBlock(
            onStationClicked = onStationClicked,
            modifier = Modifier
                .weight(1f) // 👈 растягивается на всё оставшееся
                .fillMaxWidth()
        )
    }
}


@Composable
fun StationInputBlock(
    stations: MutableList<app.gatherround.metro.Station>,
    stationsNames: List<Pair<String, String>>,
    metroData: MetroData,
    graph: MetroGraph,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        stations.forEachIndexed { stationIndex, station ->
            StationInputField(
                metroData = metroData,
                station = station,
                stationsNames = stationsNames,
                onValueChange = { newStation ->
                    stations[stationIndex] = newStation
                },
                onClose = {
                    stations.removeAt(stationIndex)
                }
            )
        }

        if (stations.size < 6) {
            Button(onClick = {
                val newStation = app.gatherround.metro.Station(
                    id = -1,
                    name = mapOf(app.gatherround.metro.RUSSIAN to ""),
                    lineId = -1,
                    location = null,
                    exits = emptyList(),
                    scheduleTrains = emptyMap(),
                    workTime = emptyList(),
                    services = emptyList(),
                    enterTime = null,
                    exitTime = null,
                    ordering = 0,
                    mcd = false,
                    outside = false,
                    mcc = false,
                    history = null,
                    audios = emptyList(),
                    accessibilityImages = emptyList(),
                    buildingImages = emptyList(),
                    stationSvg = null,
                    textSvg = null,
                    tapSvg = null
                )
                stations.add(newStation)
            }) {
                Text("Добавить станцию")
            }
        }

        Button(onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val chosenStations = stations.filter { it.id != -1 }.toSet()
                    val placesData = PlacesData()

                    val eventsJson = withContext(Dispatchers.IO) {
                        findOptimalPlaces(graph, chosenStations, placesData)
                    } ?: return@launch

                    val intent = Intent(context, EventListActivity::class.java).apply {
                        putExtra("places_json", eventsJson)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }) {
            Text("Найти мероприятия")
        }
    }
}


@Composable
fun WebMapBlock(
    onStationClicked: (String) -> Unit,
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
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage): Boolean {
                        Log.d("WebViewConsole", consoleMessage.message())
                        return true
                    }
                }

                addJavascriptInterface(
                    MapInterface { stationId ->
                        // MapInterface вызовет этот код (уже на UI-потоке)
                        Log.d("WebView", "onStationClicked вызван с ID = $stationId")

                        post {
                            onStationClicked(stationId)
                        }
                    },
                    "Android"  // это имя объекта, под которым JS будет вызывать методы
                )
                loadUrl("file:///android_asset/map/index.html")

            }
        },
        modifier = modifier
    )
}
