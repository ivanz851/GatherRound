package app.gatherround

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.Station
import app.gatherround.places.Place
import app.gatherround.places.PlacesData
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
    var stations by remember { mutableStateOf(listOf("")) }
    val selectedStations = remember { mutableStateOf(mutableSetOf<Pair<String, String>>()) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stations.forEachIndexed { index, station ->
            StationInputField(
                index = index,
                station = station,
                stationsNames = stationsNames,
                onValueChange = { newStation, ind ->
                    stations = stations.toMutableList().apply { set(ind, newStation) }
                },
                onSelect = { station, line ->
                    selectedStations.value.add(station to line)
                }
            )
        }

        if (stations.size < 6) {
            Button(onClick = {
                stations = stations + ""
            }) {
                Text("Добавить станцию")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val chosenStations = selectedStations.value.map { (station, line) ->
                            metroData.getStationByNameAndLineId(
                                station,
                                metroData.getLineIndexByName(line)!!.id
                            )!!
                        }.toSet()

                        val placesData = PlacesData()

                        // Фоновая обработка поиска мероприятий
                        val eventsJson = withContext(Dispatchers.IO) {
                            findOptimalPlaces(graph, chosenStations, placesData)
                        }!!

                        //val eventsJson = Json.encodeToString(events)

                        val intent = Intent(context, EventListActivity::class.java).apply {
                            putExtra("places_json", eventsJson)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Показываем сообщение об ошибке или обрабатываем исключение
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Найти мероприятия", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Выбранные станции:")
        selectedStations.value.forEach { (station, line) ->
            Text("- $station ($line)")
        }
    }
}

@Composable
fun StationInputField(
    index: Int,
    station: String,
    stationsNames: List<Pair<String, String>>,
    onValueChange: (String, Int) -> Unit,
    onSelect: (String, String) -> Unit
) {
    var text by remember { mutableStateOf(station) }
    var expanded by remember { mutableStateOf(false) }
    var filteredStations by remember { mutableStateOf(stationsNames) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = text,
                onValueChange = { newText: String ->
                    text = newText
                    filteredStations =
                        stationsNames.filter { it.first.startsWith(newText, ignoreCase = true) }
                    expanded = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.Gray),
                textStyle = TextStyle(color = Color.Black)
            )

            if (expanded && filteredStations.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(filteredStations) { (station, line) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    text = station
                                    onValueChange(station, index)
                                    onSelect(station, line)
                                    expanded = false
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = station,
                                modifier = Modifier.weight(1f),
                                color = Color.Black
                            )
                            Text(
                                text = line,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
