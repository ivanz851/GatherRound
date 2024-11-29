package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class MetroStation(
    val name: String,
    val lineId: Int,
    val labelId: Int,
    val boardInfo: BoardInfo? = null,
    val linkIds: List<Int>,
    val isTransferStation: Boolean = false
) : Comparable<MetroStation> {
    override fun compareTo(other: MetroStation): Int {
        val nameComparison = this.name.compareTo(other.name)
        if (nameComparison != 0) return nameComparison
        return this.lineId.compareTo(other.lineId)
    }

    override fun toString(): String {
        return "Station(name=$name, lineId=$lineId)"
    }
}
