package app.gatherround

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.RUSSIAN
import app.gatherround.metro.Station
import app.gatherround.places.PlacesData
import kotlinx.coroutines.*

class StationInputActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val metroData = MetroData.loadFromAssets(applicationContext, "get-scheme-metadata.json")
        val stationsNames = metroData.stationsNames
        val graph = MetroGraph(metroData)

        setContent {
            MyApp(
                stationsNames,
                metroData,
                graph
            )
        }
    }
}

@Composable
fun MyApp(stationsNames: List<Pair<String, String>>,
          metroData: MetroData,
          graph: MetroGraph) {
    MaterialTheme {
        StationInputScreen(
            stationsNames,
            metroData,
            graph
        )
    }
}

@Composable
fun StationInputScreen(
    stationsNames: List<Pair<String, String>>,
    metroData: MetroData,
    graph: MetroGraph
) {
    val stations = remember { mutableStateListOf<Station>() }
    // val selectedStations = remember { mutableStateOf(mutableSetOf<Station>()) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stations.forEachIndexed { stationIndex, station ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StationInputField(
                    metroData = metroData,
                    station = station,
                    stationsNames = stationsNames,
                    onValueChange = { newStation ->
                        stations.removeAt(stationIndex)
                        stations.add(stationIndex, newStation)
                    },
                    onClose = {
                        stations.removeAt(stationIndex)
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        if (stations.size < 6) {
            Button(onClick = {
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
            }) {
                Text("Добавить станцию")
            }
        }

        Button(
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val chosenStations: Set<Station> = stations
                            .filter { it.id != -1 } // Removing empty input fields
                            .toSet()
                        val placesData = PlacesData()

                        val eventsJson = withContext(Dispatchers.IO) {
                            findOptimalPlaces(graph, chosenStations, placesData)
                        }!!

                        val intent = Intent(context, EventListActivity::class.java).apply {
                            putExtra("places_json", eventsJson)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Найти мероприятия", color = Color.White)
        }

        // Debug output:
        /*
        Spacer(modifier = Modifier.height(16.dp))
        Text("Выбранные станции:")
        val chosenStations: Set<Station> = stations
            .filter { it.id != -1 } // Removing empty input fields
            .toSet()
        chosenStations.forEach { station ->
            Text("- ${station.id} ${station.name[RUSSIAN]!!}")
        }
         */
    }
}

@Composable
fun StationInputField(
    metroData: MetroData,
    station: Station,
    stationsNames: List<Pair<String, String>>,
    onValueChange: (Station) -> Unit,
    onClose: (Station) -> Unit
) {
    var curStationName by remember(station) { mutableStateOf(station.name[RUSSIAN]!!) }
    var expanded by remember { mutableStateOf(false) }
    var filteredStations by remember { mutableStateOf(stationsNames) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
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
                                curStationName = ""
                                onValueChange(station.copy(name = station.name.toMutableMap().apply { put(RUSSIAN, "") },
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

            Spacer(modifier = Modifier.width(8.dp))

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