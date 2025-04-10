package app.gatherround.stations_input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.RUSSIAN
import app.gatherround.metro.Station

@Composable
fun InputScreen(
    metroData: MetroData,
    stationsNames: List<Pair<String, String>>,
    graph: MetroGraph,
    htmlContent: String?
) {
    val selectedStations = remember { mutableStateListOf<Station>() }

    if (selectedStations.isEmpty()) {
        selectedStations.add(
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
    }

    val onStationClicked = fun(stationId: String, status: String) {
        val id = stationId.removePrefix("station-").toIntOrNull() ?: return
        val station = metroData.stations.find { it.id == id } ?: return

        if (status == "select" && station !in selectedStations && selectedStations.size < 6) {
            selectedStations.add(station)
        } else if (status == "deselect") {
            selectedStations.removeIf { it.id == id }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        InteractiveMapInput(
            htmlContent = htmlContent,
            onStationClicked = onStationClicked,
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
    }
}
