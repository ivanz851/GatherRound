package app.gatherround.metro

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

const val RUSSIAN = "ru"

@Serializable
data class Station(
    var id: Int,
    val name: Map<String, String>,
    val lineId: Int,
    val location: Location?,
    val exits: List<Exit>,
    val scheduleTrains: Map<String, List<TrainSchedule>>,
    val workTime: List<WorkTime>,
    val services: List<String>,
    val enterTime: Int?,
    val exitTime: Int?,
    val ordering: Int,
    val mcd: Boolean?,
    val outside: Boolean?,
    val mcc: Boolean?,
    val history: String?,
    val audios: List<String>,
    val accessibilityImages: List<String>,
    val buildingImages: List<String>,
    val stationSvg: SvgData?,
    val textSvg: SvgText?,
    val tapSvg: SvgTap?
) : Comparable<Station> {
    override fun compareTo(other: Station): Int {
        val thisNameEn = this.name[RUSSIAN]!!
        val otherNameEn = other.name[RUSSIAN]!!

        val nameComparison = thisNameEn.compareTo(otherNameEn)
        if (nameComparison != 0) return nameComparison

        return this.lineId.compareTo(other.lineId)
    }

    override fun toString(): String {
        return "Station(name=$name, lineId=$lineId)"
    }
}

@Serializable
data class Location(val lat: Double, val lon: Double)

@Serializable
data class Exit(
    val title: Map<String, String>,
    val exitNumber: Int,
    val location: Location?,
    val bus: String?,
    val trolleybus: String?,
    val tram: String?
)

@Serializable
data class TrainSchedule(
    val stationToId: Int,
    val stationToName: String,
    val first: String,
    val last: String?,
    val dayType: String,
    val weekend: Boolean
)

@Serializable
data class WorkTime(val open: String?, val close: String?)

@Serializable
data class SvgData(val svg: String, val x: Double, val y: Double)

@Serializable
data class SvgText(val svg: String, val x: Double, val y: Double, val h: Double, val w: Double)

@Serializable
data class SvgTap(val x: Double, val y: Double, val h: Double, val w: Double)