package app.gatherround.EptaActivity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.gatherround.StationInputBlock
import app.gatherround.WebMapBlock
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.Station

@Composable
fun InteractiveMapScreen(
    metroData: MetroData,
    stationsNames: List<Pair<String, String>>,
    graph: MetroGraph,
    htmlContent: String?
) {
    val selectedStations = remember { mutableStateListOf<Station>() }

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
        StationInputBlock(
            stations = selectedStations,
            stationsNames = stationsNames,
            metroData = metroData,
            graph = graph,
            modifier = Modifier.weight(0.4f)
        )

        Divider()

        WebMapBlock(
            htmlContent = htmlContent,
            onStationClicked = onStationClicked,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}
