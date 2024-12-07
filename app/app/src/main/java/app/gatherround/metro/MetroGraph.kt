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

    fun findShortestPath(startStationName: String,
                         startStationLineId: Int,
                         finishStationName: String,
                         finishStationLineId: Int): Pair<Int, List<MetroStation>?> {
        val start: MetroStation? =
            metroData.getStationByNameAndLineId(startStationName, startStationLineId)
        val finish: MetroStation? =
            metroData.getStationByNameAndLineId(finishStationName, finishStationLineId)

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

    private fun hasCommonStation(sortedDistances: List<List<Pair<Int, Int>>>,
                                 t: Int): Pair<Boolean, Int?> {
        val pointers = IntArray(sortedDistances.size) { 0 }
        var maxValue: Int = -1

        while (true) {
            var newMinValue: Int = Int.MAX_VALUE
            var newMaxValue: Int = -1

            for (i in sortedDistances.indices) {
                while (pointers[i] < sortedDistances[i].size &&
                    sortedDistances[i][pointers[i]].first < t &&
                    sortedDistances[i][pointers[i]].first < maxValue) {
                    pointers[i]++
                }

                if (!(pointers[i] < sortedDistances[i].size &&
                            sortedDistances[i][pointers[i]].first < t)) {
                    return Pair(false, null)
                }

                val currentValue: Int = sortedDistances[i][pointers[i]].first
                if (currentValue < newMinValue) {
                    newMinValue = currentValue
                }
                newMaxValue = maxOf(newMaxValue, currentValue)
            }

            if (newMinValue == newMaxValue) {
                return Pair(true, sortedDistances[0][pointers[0]].second)
            }

            maxValue = newMaxValue
        }
    }

    fun findOptimalStation(
        selectedStations: List<Pair<String, Int>>,
    ): MetroStation? {
        val stationIds = selectedStations.mapNotNull { (name, lineId) ->
            this.metroData.getStationByNameAndLineId(name, lineId)?.stationUniqueId
        }

        if (stationIds.isEmpty()) {
            return null
        }

        val sortedDistances = stationIds.map { stationId ->
            val distances = Dijkstra<MetroStation>().getDistances(
                this,
                metroData.getStationById(stationId)!!
            )
            distances
                .map { (station, distance) -> distance to station.stationUniqueId!! }
                .sortedBy { it.first }
        }
        var left = 0
        var right: Int = MAX_ROUTE_TIME
        var optimalStationId: Int? = null

        while (left + 1 < right) {
            val mid: Int = (left + right) / 2
            val (exists, stationId) = hasCommonStation(sortedDistances, mid)
            if (exists) {
                optimalStationId = stationId
                right = mid
            } else {
                left = mid
            }
        }

        return if (optimalStationId != null) {
            metroData.getStationById(optimalStationId)
        } else null
    }
}