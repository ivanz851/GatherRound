package app.gatherround.stations_input

import android.content.Intent
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
import app.gatherround.findOptimalPlaces
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.RUSSIAN
import app.gatherround.metro.Station
import app.gatherround.places.PlacesData
import app.gatherround.places_output.PlacesOutputActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun StationInputBlock(
    stations: MutableList<Station>,
    stationsNames: List<Pair<String, String>>,
    metroData: MetroData,
    graph: MetroGraph,
    webView: WebView?,
    modifier: Modifier = Modifier,
    onStationClicked: ((String, String) -> Unit)? = null
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
            )

            if (stationIndex < stations.lastIndex) {
                Spacer(modifier = Modifier.height(3.dp))
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val chosenStations = stations.filter { it.id != -1 }.toSet()
                        val placesData = PlacesData()

                        val eventsJson = withContext(Dispatchers.IO) {
                            findOptimalPlaces(graph, chosenStations, placesData)
                        } ?: return@launch

                        val intent = Intent(context, PlacesOutputActivity::class.java).apply {
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
}

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
) {
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