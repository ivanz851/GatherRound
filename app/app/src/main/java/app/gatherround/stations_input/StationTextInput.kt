package app.gatherround.stations_input

import android.content.Intent
import android.location.Location
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.RUSSIAN
import app.gatherround.metro.Station
import app.gatherround.places.PlacesData
import app.gatherround.places_output.PlacesOutputMap
import android.net.Uri
import androidx.compose.material.icons.filled.DirectionsWalk
import app.gatherround.places.findOptimalPlaces
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Вертикальный список полей ввода станций (до 6), а также кнопка «Найти мероприятия».
 *
 * @param stations           список выбранных / пустых станций
 * @param stationsNames      пары **(название станции, название линии)** — для автодополнения
 * @param metroData          данные схемы метро (для поиска станций по названию)
 * @param graph              [MetroGraph] — нужен, чтобы вычислить оптимальную станцию
 * @param webView            ссылка на SVG-`WebView`, чтобы подсвечивать/снимать станции
 * @param modifier           внешний `Modifier`
 * @param onStationClicked   callback для прямых кликов на интерактивной карте (может быть `null`)
 * @param userLocation       GPS-координаты пользователя (или `null`, если нет разрешения)
 */
@Composable
fun StationInputBlock(
    stations: MutableList<Station>,
    stationsNames: List<Pair<String, String>>,
    metroData: MetroData,
    graph: MetroGraph,
    webView: WebView?,
    modifier: Modifier = Modifier,
    onStationClicked: ((String, String) -> Unit)? = null,
    userLocation: Location?,
) {
    val context = LocalContext.current

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {

        /* ----------- динамический список полей ввода ------------------------ */
        stations.forEachIndexed { stationIndex, station ->
            StationInputField(
                metroData = metroData,
                station = station,
                stationsNames = stationsNames,
                onValueChange = { newStation ->
                    stations[stationIndex] = newStation

                    if (newStation.id != -1) {
                        webView?.evaluateJavascript("selectStation('station-${newStation.id}')", null)
                    }
                },
                onClose = {
                    stations.removeAt(stationIndex)
                },
                onAddClick = {
                    val newStation = Station(
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
                    stations.add(newStation)
                },
                showDelete = stations.size > 1,
                showAdd = stationIndex == stations.lastIndex && stations.size < 6,
                webView = webView,
                userLocation    = userLocation
            )

            if (stationIndex < stations.lastIndex) {
                Spacer(modifier = Modifier.height(3.dp))
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        /* ----------- кнопка «Найти места» ----------------------------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    val chosenStations = stations.filter { it.id != -1 }.toSet()
                    val placesData = PlacesData()

                    val optimaPlacesData = withContext(Dispatchers.IO) {
                        findOptimalPlaces(graph, chosenStations, placesData)
                    }

                    val optimalStation = optimaPlacesData.first!!
                    val eventsJson = optimaPlacesData.second!!

                    val intent = Intent(context, PlacesOutputMap::class.java).apply {
                        putExtra("station_lat", optimalStation.location!!.lat)
                        putExtra("station_lon", optimalStation.location.lon)
                        putExtra("places_json", eventsJson)
                    }
                    context.startActivity(intent)
                }
            }) {
                Text("Найти места")
            }
        }
    }
}

/* -------------------------------------------------------------------------- */

/**
 * Одно поле ввода станции с автодополнением, и иконками
 * "добавить новую станцию", "удалить текущую станцию", "очистить ввод".
 *
 * @param station           текущая станция (или «пустая», если id = -1)
 * @param onValueChange     вызывается при выборе новой станции из выпадающего списка
 * @param onClose           вызывается при клике по корзине ("удалить текущую станцию"), удаляет поле
 * @param onAddClick        вызывается при клике на плюс (добавить новую станцию"), добавляет поле
 * @param showDelete        отображать ли иконку с корзиной
 * @param showAdd           отображать ли иконку с плюсом
 * @param webView           SVG-`WebView` для подсветки станций на интерактивной карте
 * @param userLocation      текущие координаты пользователя (для кнопки "построить маршрут")
 */
@Composable
fun StationInputField(
    metroData: MetroData,
    station: Station,
    stationsNames: List<Pair<String, String>>,
    onValueChange: (Station) -> Unit,
    onClose: (Station) -> Unit,
    onAddClick: () -> Unit,
    showDelete: Boolean,
    showAdd: Boolean,
    webView: WebView?,
    userLocation: Location?,
) {
    val context = LocalContext.current
    var curStationName by remember(station) { mutableStateOf(station.name[RUSSIAN]!!) }
    var expanded by remember { mutableStateOf(false) }
    var filteredStations by remember { mutableStateOf(stationsNames) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (showDelete) {
                IconButton(
                    onClick = { onClose(station) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = "Удалить поле",
                        tint = Color.Red
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            /* ------ поле ввода ----------------------------------------------------- */
            OutlinedTextField(
                value = curStationName,
                onValueChange = { newStationName: String ->
                    curStationName = newStationName
                    filteredStations = stationsNames.filter {
                        it.first.startsWith(newStationName, ignoreCase = true)
                    }
                    expanded = filteredStations.isNotEmpty()
                },
                modifier = Modifier.weight(1f),
                label = { Text("Введите станцию") },
                singleLine = true,
                trailingIcon = {
                    Row {
                        if (curStationName.isNotEmpty()) {
                            IconButton(onClick = {
                                if (station.id != -1) {
                                    webView?.evaluateJavascript("deselectStation('station-${station.id}')", null)
                                }

                                curStationName = ""
                                onValueChange(station.copy(name = station.name.toMutableMap().apply { put(
                                    RUSSIAN, "") },
                                    id = -1))
                                expanded = false
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Очистить",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(3.dp))

            /* ------ кнопка "построить маршрут от текущей геопозиции пользователя
            до выбранной станции" --------------------------- */
            if (station.id != -1 && station.location != null && userLocation != null) {
                IconButton(
                    onClick = {
                        val fromLat = userLocation.latitude
                        val fromLon = userLocation.longitude
                        val dest = station.location
                        val appUri = Uri.parse(
                            "yandexmaps://build_route_on_map/?" +
                                    "lat_from=$fromLat&lon_from=$fromLon&" +
                                    "lat_to=${dest.lat}&lon_to=${dest.lon}&" +
                                    "route_type=pd"
                        )
                        val appIntent = Intent(Intent.ACTION_VIEW, appUri).apply {
                            setPackage("ru.yandex.yandexmaps")
                        }

                        val browserUri = Uri.parse(
                            "https://yandex.ru/maps/?" +
                                    "rtext=$fromLat,$fromLon~${dest.lat},${dest.lon}&rtt=pd"
                        )
                        val finalIntent =
                            if (appIntent.resolveActivity(context.packageManager) != null)
                                appIntent else Intent(Intent.ACTION_VIEW, browserUri)

                        context.startActivity(finalIntent)
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsWalk,
                        contentDescription = "Маршрут из текущего положения",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Spacer(Modifier.size(34.dp))
            }

            /* ------ кнопка "добавить поле ввода" --------------------------------------- */
            Box(modifier = Modifier.size(48.dp)) {
                if (showAdd) {
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить станцию",
                            tint = Color.Green
                        )
                    }
                }
            }
        }

        /* ----------------------- выпадающий список для автодополнения ------------------ */
        AnimatedVisibility(visible = expanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                LazyColumn {
                    items(filteredStations) { (newStationName, newLineName) ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = newStationName,
                                        style = MaterialTheme.typography.bodyLarge)
                                    Text(text = newLineName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray)
                                }
                            },
                            onClick = onClick@{
                                val newLineId =
                                    metroData.getLineIndexByName(newLineName)?.id ?: return@onClick
                                val selectedStation =
                                    metroData.getStationByNameAndLineId(newStationName, newLineId) ?: return@onClick

                                curStationName = newStationName
                                expanded = false
                                onValueChange(selectedStation)
                            }
                        )
                    }
                }
            }
        }
    }
}