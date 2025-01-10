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

        metroData.transitions.forEach { transition ->
            addTransition(transition)
        }
    }

    private fun addStation(station: Station) {
        addVertex(station)
    }

    private fun addConnection(connection: Connection) {
        val start: Station? = metroData.getStationById(connection.stationFromId)
        val finish: Station? = metroData.getStationById(connection.stationToId)

        if (start != null && finish != null) {
            addEdge(start, finish, connection.pathLength)
        }
    }

    private fun addTransition(transition: Transition) {
        val start: Station? = metroData.getStationById(transition.stationFromId)
        val finish: Station? = metroData.getStationById(transition.stationToId)

        if (start != null && finish != null) {
            addEdge(start, finish, transition.pathLength)
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
        val edges = this.getEdges()

        for (edge in edges) {
            val startStation = edge.first
            val finishStation = edge.second.finish
            val transferTime = edge.second.weight

            println("${startStation.name[RUSSIAN]}, ${startStation.lineId} -> " +
                    "${finishStation.name[RUSSIAN]}, ${finishStation.lineId}, " +
                    "время в пути: ${transferTime.toDouble()/60} мин")
        }
    }


}