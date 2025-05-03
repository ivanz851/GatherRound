package app.gatherround.stations_input

import android.content.pm.PackageManager
import android.location.Location
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.RUSSIAN
import app.gatherround.metro.Station
import app.gatherround.places.PlacesData
import com.google.android.gms.location.LocationServices
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import app.gatherround.places.findOptimalPlaces


/**
 * Экран ввода станций.
 *
 * 1. Позволяет пользователю выбирать до 6 станций на интерактивной SVG-схеме
 *    (см. [InteractiveMapInput]).
 * 2. Выводит конечную (оптимальную) станцию, минимизирующую
 *    максимум времени в пути до выбранных станций (через [MetroGraph]).
 * 3. Запрашивает разрешение на геопозицию и передаёт координаты в [StationInputBlock]
 *     для перенаправления в Яндекс Карты для построения пешего маршрута к выбранной станции метро.
 *
 * @param metroData        десериализованные данные схемы метро
 * @param stationsNames    пары «название станции, название линии» — источник для автодополнения
 * @param graph            граф метро, построенный на основе [metroData]
 * @param htmlContent      SVG-разметка схемы (или `null`, если ещё не загружена)
 * @param selectedStations список выбранных станций;
 *
 */
@Composable
fun InputScreen(
    metroData: MetroData,
    stationsNames: List<Pair<String, String>>,
    graph: MetroGraph,
    htmlContent: String?,
    selectedStations: MutableList<Station>
) {
    fun emptyStation() = Station(
        id = -1,
        name = mapOf(RUSSIAN to ""),
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

    if (selectedStations.isEmpty()) {
        selectedStations.add(emptyStation())
    }

    /** Call-back для клика по станции в SVG. Изменяет [selectedStations] напрямую. */
    val onStationClicked = fun(stationId: String, status: String) {
        val id = stationId.removePrefix("station-").toIntOrNull() ?: return
        val station = metroData.stations.find { it.id == id } ?: return

        if (status == "select") {
            if (selectedStations.any { it.id == id }) return
            val emptyIndex = selectedStations.indexOfFirst { it.id == -1 }

            if (emptyIndex != -1) {
                selectedStations[emptyIndex] = station
            } else if (selectedStations.size < 6) {
                selectedStations.add(station)
            }
        }
        else if (status == "deselect") {
            val index = selectedStations.indexOfFirst { it.id == id }
            if (index != -1) {
                selectedStations[index] = emptyStation()
            }
        }
    }

    /** Call-back для клика по станции в SVG.
     *  Обработчик, возвращающий `true`, если выбор удался, — для SVG-скрипта.
     *  При «deselect» возвращает `false`, чтобы JS мог снять подсветку. */
    val onStationClickedWithResult: (String, String) -> Boolean = onStationClickedWithResult@ { stationId, status ->
        val id = stationId.removePrefix("station-").toIntOrNull() ?: return@onStationClickedWithResult false
        val station = metroData.stations.find { it.id == id } ?: return@onStationClickedWithResult false

        if (status == "select") {
            if (selectedStations.any { it.id == id }) return@onStationClickedWithResult false

            val emptyIndex = selectedStations.indexOfFirst { it.id == -1 }
            if (emptyIndex == -1) return@onStationClickedWithResult false

            selectedStations[emptyIndex] = station
            return@onStationClickedWithResult true
        }

        if (status == "deselect") {
            val index = selectedStations.indexOfFirst { it.id == id }
            if (index != -1) {
                selectedStations[index] = emptyStation()
            }
            return@onStationClickedWithResult false
        }

        return@onStationClickedWithResult false
    }

    /* ---------- UI ---------------------------------------------------------------- */

    Column(modifier = Modifier.fillMaxSize()) {
        /* WebView со схемой для передачи коллбэков кликов -------------------------------- */
        val webViewRef = remember { mutableStateOf<WebView?>(null) }

        InteractiveMapInput(
            htmlContent = htmlContent,
            onStationClicked = onStationClicked,
            onStationClickedWithResult = onStationClickedWithResult,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onWebViewCreated = { webView -> webViewRef.value = webView }
        )

        HorizontalDivider()


        val endStation by remember(selectedStations) {
            derivedStateOf {
                val chosen = selectedStations.filter { it.id != -1 }
                if (chosen.isEmpty()) {
                    null
                } else {
                    val placesData = PlacesData()
                    findOptimalPlaces(graph, chosen.toSet(), placesData).first
                }
            }
        }
        /* --- Вывод конечной станции --- */
        Text(
            text = buildString {
                append("Конечная станция: ")
                append(endStation?.name?.get(RUSSIAN) ?: "—")
            },
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        val context = LocalContext.current

        val fusedLocationClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }
        var userLocation by remember { mutableStateOf<Location?>(null) }
        val locationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation = location
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            val permission = Manifest.permission.ACCESS_FINE_LOCATION
            when {
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            userLocation = location
                        }
                    }
                }
                else -> {
                    locationPermissionLauncher.launch(permission)
                }
            }
        }

        /* ---------- блок ввода + список выбранных станций ------------------------- */
        StationInputBlock(
            stations = selectedStations,
            stationsNames = stationsNames,
            metroData = metroData,
            graph = graph,
            webView = webViewRef.value,
            modifier = Modifier.weight(0.4f),
            onStationClicked = onStationClicked,
            userLocation = userLocation
        )

    }
}
