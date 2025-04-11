package app.gatherround.stations_input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.RUSSIAN
import app.gatherround.metro.Station

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






    Column(modifier = Modifier.fillMaxSize()) {
        InteractiveMapInput(
            htmlContent = htmlContent,
            onStationClicked = onStationClicked,
            onStationClickedWithResult = onStationClickedWithResult,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )


        HorizontalDivider()

        StationInputBlock(
            stations = selectedStations,
            stationsNames = stationsNames,
            metroData = metroData,
            graph = graph,
            modifier = Modifier.weight(0.4f)
        )











/*

        Text(
            text = "Выбранные станции:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )

// Список станций
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)) {

            selectedStations
                .filter { it.id != -1 }
                .forEachIndexed { index, station ->
                    val name = station.name[RUSSIAN] ?: "Без названия"
                    val lineId = station.lineId

                    Text(
                        text = "${index + 1}. $name (линия $lineId)",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
        }

        */
    }
}
