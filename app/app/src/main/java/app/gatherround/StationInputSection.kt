package app.gatherround.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.gatherround.StationInputField
import app.gatherround.metro.*

@Composable
fun StationInputSection(
    stationsNames: List<Pair<String, String>>,
    metroData: MetroData,
    graph: MetroGraph,
    stations: SnapshotStateList<Station>,
    onSubmit: (Set<Station>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stations.forEachIndexed { index, station ->
            StationInputField(
                station = station,
                stationsNames = stationsNames,
                metroData = metroData,
                onValueChange = { newStation ->
                    stations[index] = newStation
                },
                onClose = {
                    stations.removeAt(index)
                }
            )
        }

        if (stations.size < 6) {
            Button(onClick = {
                stations.add(
                    Station(
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
                )
            }) {
                Text("Добавить станцию")
            }
        }

        Button(
            onClick = {
                val chosen = stations.filter { it.id != -1 }.toSet()
                if (chosen.isNotEmpty()) {
                    onSubmit(chosen)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Найти мероприятия", color = Color.White)
        }
    }
}
