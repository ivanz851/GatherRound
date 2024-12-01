package app.gatherround.metro

import kotlinx.serialization.Serializable
import java.io.FileInputStream
import java.util.Properties

const val SECS_IN_MIN = 60

fun getSchemeMetadataPath(): String {
    val properties = Properties()
    FileInputStream("config.properties").use { inputStream ->
        properties.load(inputStream)
    }
    return properties.getProperty("metroSchemeMetadataPath")
}

@Serializable
data class MetroData(
    val lines: Map<String, MetroLine>,
    val stations: Map<Int, MetroStation>,
    val links: Map<String, Link>,
    val transfers: Map<String, Transfer>
) {
    private var stationNameIdMap: Map<Pair<String, Int>, Int> = emptyMap()

    init {
        stations.forEach { (key, station) ->
            station.stationUniqueId = key
        }

        stationNameIdMap = fillStationNameIdMap()
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
