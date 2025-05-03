package app.gatherround.metro

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

const val SECS_IN_MIN = 60
const val MINS_IN_HOUR = 60
const val SECS_IN_HOUR = SECS_IN_MIN * MINS_IN_HOUR
const val MAX_ROUTE_TIME = 5 * SECS_IN_HOUR

const val metroDataJsonPath = "C:\\\\Users\\\\test\\\\user\\\\study\\\\hse_3_course\\\\course_project\\\\gather_round\\\\app\\\\app\\\\src\\\\main\\\\assets\\\\get-scheme-metadata.json"

/**
 * Класс для данные схемы метро.
 * После десериализации **строит вспомогательные словари** для быстрого доступа
 * к станциям и линиям по ключам.
 *
 * @property lines        список линий метро
 * @property stations     список станций
 * @property connections  список перегонов между станциями
 * @property transitions  спсок пересадок пересадки
 */
@Serializable
data class MetroData(
    val lines: List<Line> = emptyList(),
    val stations: List<Station> = emptyList(),
    val connections: List<Connection> = emptyList(),
    val transitions: List<Transition> = emptyList()
) {
    private var stationNameIdMap: Map<Pair<String, Int>, Station> = emptyMap()
    private var stationIdMap: Map<Int, Station> = emptyMap()
    private var lineNameMap: Map<Int, Line> = emptyMap()
    private var lineIndexMap: Map<String, Line> = emptyMap()
    var stationsNames: List<Pair<String, String>> = emptyList()

    init {
        stationIdMap = fillStationIdMap()
        stationNameIdMap = fillStationNameIdMap()
        lineNameMap = fillLineNameMap()
        lineIndexMap = fillLineIndexMap()
        stationsNames = fillStationsNames()
    }

    companion object {
        fun loadFromAssets(context: Context, fileName: String): MetroData {
            val jsonContent = context.assets.open(fileName).use { inputStream ->
                InputStreamReader(inputStream).readText()
            }

            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString(serializer(), jsonContent)
        }
    }

    fun loadMetroDataFromFile(filePath: String = metroDataJsonPath): MetroData {
        val file = File(filePath)
        if (!file.exists()) {
            throw FileNotFoundException("File not found: $filePath")
        }

        val jsonContent = file.readText()
        val json = Json {
            ignoreUnknownKeys = true
        }

        return json.decodeFromString<MetroData>(jsonContent)
    }

    private fun fillStationNameIdMap(): Map<Pair<String, Int>, Station> {
        val result = mutableMapOf<Pair<String, Int>, Station>()

        for (station in stations) {
            val key = Pair(station.name[RUSSIAN]!!, station.lineId)
            result[key] = station
        }

        return result
    }

    private fun fillStationIdMap(): Map<Int, Station> {
        val result = mutableMapOf<Int, Station>()

        for (station in stations) {
            result[station.id] = station
        }

        return result
    }

    private fun fillLineNameMap(): Map<Int, Line> {
        val result = mutableMapOf<Int, Line>()

        for (line in lines) {
            result[line.id] = line
        }

        return result
    }

    private fun fillLineIndexMap(): Map<String, Line> {
        val result = mutableMapOf<String, Line>()

        for (line in lines) {
            result[line.name[RUSSIAN]!!] = line
        }

        return result
    }

    private fun fillStationsNames(): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()

        for (station in stations) {
            result.add(Pair(station.name[RUSSIAN]!!,
                lineNameMap[station.lineId]!!.name[RUSSIAN]!!))
        }

        return result
    }

    fun getStationById(stationId: Int): Station? {
        return stationIdMap[stationId]
    }

    fun getStationByNameAndLineId(stationName: String, stationLineId: Int): Station? {
        return stationNameIdMap[Pair(stationName, stationLineId)]
    }

    fun getLineIndexByName(lineName: String): Line? {
        return lineIndexMap[lineName]
    }

    fun getLineIndexById(lineId: Int): Line? {
        return lineNameMap[lineId]
    }


    fun printStationNameIdMap() {
        stationNameIdMap.forEach { (key, value) ->
            val (stationName, lineId) = key
            println("Станция: $stationName, Линия: $lineId, ID: $value")
        }
    }

    fun printStations() {
        stations.forEach {  station ->
            println("Станция: ${station.name[RUSSIAN]!!}, Линия: ${station.lineId}, Label ID: ${station.id}")
        }
    }
}
