package app.gatherround.metro

import kotlinx.serialization.Serializable
import java.io.FileInputStream
import java.util.Properties


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
    private val stationNameIdMap: Map<Pair<String, Int>, Int> = fillStationNameIdMap()

    private fun fillStationNameIdMap(): Map<Pair<String, Int>, Int> {
        return stations.values.associate { station ->
            Pair(station.name, station.lineId) to station.labelId
        }
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
}
