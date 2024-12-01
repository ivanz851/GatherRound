package app.gatherround

import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import java.io.File
import java.io.FileNotFoundException



fun main() {
    val metroData = MetroData().loadMetroDataFromFile()
    val metroGraph = MetroGraph(metroData)

    val stations = metroGraph.getVertices()

    val (actualTimeInSecs, actualPath) =metroGraph.findShortestPath(
        startStationName = "Shosse Entuziastov",
        startStationLineId = 14,
        finishStationName = "Tulskaya",
        finishStationLineId = 9
    )
    println(actualTimeInSecs.toDouble() / 60)
    println(metroData.stations.size)
}
