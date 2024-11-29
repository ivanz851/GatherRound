package app.gatherround.metro

import app.gatherround.graph.Dijkstra
import app.gatherround.graph.Graph

class MetroGraph(private val metroData: MetroData) : Graph<MetroStation>() {
    init {
        metroData.stations.values.forEach { station ->
            addStation(station)
        }

        metroData.links.values.forEach { link ->
            addLink(link)
        }
    }

    private fun addStation(station: MetroStation) {
        addVertex(station)
    }

    private fun addLink(link: Link) {
        val start: MetroStation? = metroData.getStationById(link.fromStationId)
        val finish: MetroStation? = metroData.getStationById(link.toStationId)

        if (start != null && finish != null) {
            addEdge(start, finish, link.weightTime)
        }
    }

    fun findShortestPath(startStationName: String, finishStationName: String): Pair<Int, List<MetroStation>?> {
        val start: MetroStation? = metroData.getStationByName(startStationName)
        val finish: MetroStation? = metroData.getStationByName(finishStationName)

        return if (start != null && finish != null) {
            val (distance, path) = Dijkstra<MetroStation>().getShortestPath(this, start, finish)
            Pair(distance, path)
        } else {
            Pair(-1, emptyList())
        }
    }

    fun printAllConnections() {
        metroData.links.values.forEach { link ->
            val startStation = metroData.getStationById(link.fromStationId)
            val finishStation = metroData.getStationById(link.toStationId)
            val timeInSecs = link.weightTime

            if (startStation != null && finishStation != null) {
                println("${startStation.name}, ${startStation.lineId} -> ${finishStation.name}, ${finishStation.lineId}, время в пути: ${timeInSecs.toDouble()/60} мин")
            }
        }
    }
}