package app.gatherround.metro

import app.gatherround.graph.Dijkstra
import app.gatherround.graph.Graph

class MetroGraph(private val metroData: MetroData) : Graph<Station>() {
    init {
        metroData.stations.forEach { station ->
            addStation(station)
        }

        metroData.connections.forEach { connection ->
            addConnection(connection)
        }
    }

    private fun addStation(station: Station) {
        addVertex(station)
    }

    private fun addConnection(link: Connection) {
        val start: Station? = metroData.getStationById(link.stationFromId)
        val finish: Station? = metroData.getStationById(link.stationToId)

        if (start != null && finish != null) {
            addEdge(start, finish, link.pathLength)
        }
    }

    fun findShortestPath(startStationName: String,
                         startStationLineId: Int,
                         finishStationName: String,
                         finishStationLineId: Int): Pair<Int, List<Station>?> {
        val start: Station? =
            metroData.getStationByNameAndLineId(startStationName, startStationLineId)
        val finish: Station? =
            metroData.getStationByNameAndLineId(finishStationName, finishStationLineId)

        return if (start != null && finish != null) {
            val (distance, path) = Dijkstra<Station>().getShortestPath(this, start, finish)
            Pair(distance, path)
        } else {
            Pair(-1, emptyList())
        }
    }

    fun printAllConnections() {
        metroData.connections.forEach { link ->
            val startStation = metroData.getStationById(link.stationFromId)
            val finishStation = metroData.getStationById(link.stationToId)
            val timeInSecs = link.pathLength

            if (startStation != null && finishStation != null) {
                println("${startStation.name}, ${startStation.lineId} -> ${finishStation.name}, ${finishStation.lineId}, время в пути: ${timeInSecs.toDouble()/60} мин")
            }
        }
    }


}