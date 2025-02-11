package app.gatherround

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
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
    var stations by remember { mutableStateOf(listOf(StationInput())) }
    val selectedStations = remember { mutableStateOf(mutableSetOf<Pair<String, String>>()) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stations.forEach { stationInput ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StationInputField(
                    stationInput = stationInput,
                    stationsNames = stationsNames,
                    onValueChange = { newStation ->
                        stations = stations.map {
                            if (it.id == stationInput.id) it.copy(name = newStation) else it
                        }
                    },
                    onSelect = { station, line ->
                        selectedStations.value.add(station to line)
                    },
                    onClose = { stationId ->
                        // Удаляем станцию по уникальному ID
                        stations = stations.filter { it.id != stationId }
                        selectedStations.value.removeIf { it.first == stationInput.name }
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        // Кнопка для добавления новой станции, если их меньше 6
        if (stations.size < 6) {
            Button(onClick = {
                stations = stations + StationInput() // Добавляем новую станцию
            }) {
                Text("Добавить станцию")
            }
        }

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

        /*
        Spacer(modifier = Modifier.height(16.dp))
        Text("Выбранные станции:")
        selectedStations.value.forEach { (station, line) ->
            Text("- $station ($line)")
        }
        */
    }
}

@Composable
fun StationInputField(
    stationInput: StationInput,
    stationsNames: List<Pair<String, String>>,
    onValueChange: (String) -> Unit,
    onSelect: (String, String) -> Unit,
    onClose: (String) -> Unit
) {
    var text by remember { mutableStateOf(stationInput.name) }
    var expanded by remember { mutableStateOf(false) }
    var filteredStations by remember { mutableStateOf(stationsNames) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { newText: String ->
                    text = newText
                    filteredStations = stationsNames.filter {
                        it.first.startsWith(newText, ignoreCase = true)
                    }
                    expanded = filteredStations.isNotEmpty()
                    onValueChange(newText)
                },
                modifier = Modifier.weight(1f),
                label = { Text("Введите станцию") },
                singleLine = true,
                trailingIcon = {
                    Row {
                        if (text.isNotEmpty()) {
                            IconButton(onClick = {
                                text = ""
                                onValueChange("")
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
                onClick = { onClose(stationInput.id) },
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
                    items(filteredStations) { (stationName, lineName) ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = stationName, style = MaterialTheme.typography.bodyLarge)
                                    Text(text = lineName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            },
                            onClick = {
                                text = stationName
                                onValueChange(stationName)
                                onSelect(stationName, lineName)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

data class StationInput(
    val id: String = java.util.UUID.randomUUID().toString(),
    var name: String = ""
)

