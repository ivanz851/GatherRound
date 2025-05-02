package app.gatherround.metro

import app.gatherround.graph.Dijkstra
import app.gatherround.graph.Graph
import java.io.File
import java.nio.charset.Charset

/**
 * Обёртка-граф над данными метро. Наследует [Graph] с типом вершины — [Station].
 *
 * Алгоритм поиска кратчайшего пути — Дейкстра из модуля `graph`.
 *
 * @constructor сразу после создания наполняет граф всеми станциями, рёбрами-соединениями
 *              и рёбрами-переходами из [metroData].
 *
 * @property metroData данные схемы метро
 */
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

    /** Добавляет станцию как вершину. */
    private fun addStation(station: Station) {
        addVertex(station)
    }

    /** Создаёт ребро-перегон между двумя станциями, если обе найдены. */
    private fun addConnection(connection: Connection) {
        val start: Station? = metroData.getStationById(connection.stationFromId)
        val finish: Station? = metroData.getStationById(connection.stationToId)

        if (start != null && finish != null) {
            addEdge(start, finish, connection.pathLength)
        }
    }

    /** Создаёт ребро-переход между двумя станциями, если обе найдены. */
    private fun addTransition(transition: Transition) {
        val start: Station? = metroData.getStationById(transition.stationFromId)
        val finish: Station? = metroData.getStationById(transition.stationToId)

        if (start != null && finish != null) {
            addEdge(start, finish, transition.pathLength)
        }
    }

    /**
     * Находит кратчайший путь между двумя станциями по их названию и id лини1.
     *
     * @return `Pair(время в секундах, список станций)`; если хотя бы одна станция
     *         не найдена, результат — `(-1, emptyList())`.
     */
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

    /** Печатает все рёбра графа в читаемом виде (для отладки). */
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

    /**
     * Вспомогательная функция для вывода списка станций, принадлежащих каждой линии (не используется, нужна для отладки).
     */
    fun processLinesTraversal() {
        /*
        Функция, которая записывает в csv таблицу основную информацию о станциях:
        название станции, название линии, названия соседей, координаты.
        */
        val linesTraversals: MutableMap<String, MutableList<String>> = mutableMapOf()

        for (line in metroData.lines) {
            linesTraversals[line.name["ru"]!!] = mutableListOf()

            var prevId = -1
            var startId = line.stationStartId
            var finishId = line.stationEndId
            var swapFlag = false

            var curId = startId

            while (curId != finishId) {
                val curStation = metroData.getStationById(curId)!!

                for (edge in this.getAdjacentEdges(curStation)) {
                    if (edge.finish.id != prevId &&
                        edge.finish.lineId == curStation.lineId) {

                        if (prevId == -1 && curId == startId &&
                            edge.finish.id == finishId &&
                            !swapFlag) {
                            curId = finishId
                            finishId = startId
                            startId = curId
                            swapFlag = true
                            break
                        }

                        linesTraversals[line.name["ru"]!!]!!.add(curStation.name["ru"]!!)

                        prevId = curId
                        curId = edge.finish.id

                        break
                    }
                }
            }

            val curStation = metroData.getStationById(curId)!!
            linesTraversals[line.name["ru"]!!]!!.add(curStation.name["ru"]!!)
        }

        val csvBuilderMetro = StringBuilder()
        val csvBuilderMCD = StringBuilder()
        csvBuilderMetro.append("station_name,line_name,prev_station_name,next_station_name,latitude,longitude\n")
        csvBuilderMCD.append("station_name,line_name,prev_station_name,next_station_name,latitude,longitude\n")

        for ((lineName, stations) in linesTraversals) {
            for (i in stations.indices) {
                val stationName = stations[i]
                var prevStation = if (i > 0) stations[i - 1] else ""
                var nextStation = if (i < stations.size - 1) stations[i + 1] else ""

                if (lineName == "Большая кольцевая линия" ||
                    lineName == "МЦК" ||
                    lineName == "Кольцевая линия") {
                    if (prevStation == "") {
                        prevStation = stations.last()
                    }

                    if (nextStation == "") {
                        nextStation = stations.first()
                    }
                }

                val location = metroData
                    .getStationByNameAndLineId(
                        stationName,
                        metroData.getLineIndexByName(lineName)!!.id
                    )!!
                    .location!!

                val latitude = location.lat
                val longitude = location.lon

                if (lineName == "МЦД-1" ||
                    lineName == "МЦД-2" ||
                    lineName == "МЦД-3" ||
                    lineName == "МЦД-4" ||
                    lineName == "МЦД-4А") {
                    csvBuilderMCD.append("$stationName,$lineName,$prevStation,$nextStation,$latitude,$longitude\n")
                } else {
                    csvBuilderMetro.append("$stationName,$lineName,$prevStation,$nextStation,$latitude,$longitude\n")
                }
            }
        }

        File("metro_stations_neighbours.csv")
            .writeText(csvBuilderMetro.toString(), Charset.forName("utf-8"))
        File("mcd_stations_neighbours.csv")
            .writeText(csvBuilderMCD.toString(), Charset.forName("utf-8"))
    }

}