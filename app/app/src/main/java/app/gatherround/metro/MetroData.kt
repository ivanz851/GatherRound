package app.gatherround.metro

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

const val SECS_IN_MIN = 60
const val MINS_IN_HOUR = 60
const val SECS_IN_HOUR = SECS_IN_MIN * MINS_IN_HOUR
const val MAX_ROUTE_TIME = SECS_IN_HOUR

const val metroDataJsonPath = "C:\\\\Users\\\\test\\\\user\\\\ProjectSeminar2024-25\\\\GatherRound\\\\app\\\\app\\\\src\\\\main\\\\resources\\\\get-scheme-metadata.json"

@Serializable
data class MetroData(
    val lines: Map<String, MetroLine> = emptyMap(),
    val stations: Map<Int, MetroStation> = emptyMap(),
    val links: Map<String, Link> = emptyMap(),
    val transfers: Map<String, Transfer> = emptyMap()
) {
    private var stationNameIdMap: Map<Pair<String, Int>, Int> = emptyMap()

    init {
        stations.forEach { (key, station) ->
            station.stationUniqueId = key
        }

        stationNameIdMap = fillStationNameIdMap()
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

    private fun fillStationNameIdMap(): Map<Pair<String, Int>, Int> {
        val result = mutableMapOf<Pair<String, Int>, Int>()

        for ((_, station) in stations) {
            val key = Pair(station.name, station.lineId)
            result[key] = station.stationUniqueId!!
        }

        return result
    }


    fun getStationById(stationId: Int): MetroStation? {
        return stations[stationId]
    }

    fun getStationByNameAndLineId(stationName: String, stationLineId: Int): MetroStation? {
        val stationId: Int? = stationNameIdMap[Pair(stationName, stationLineId)]
        return if (stationId == null) {
            null
        } else {
            stations[stationId]
        }
    }


    fun printStationNameIdMap() {
        stationNameIdMap.forEach { (key, value) ->
            val (stationName, lineId) = key
            println("Станция: $stationName, Линия: $lineId, ID: $value")
        }
    }



    fun printStations() {
        stations.forEach { (id, station) ->
            println("ID: $id, Станция: ${station.name}, Линия: ${station.lineId}, Label ID: ${station.labelId}, Переходная: ${station.isTransferStation}")
        }
    }

}
