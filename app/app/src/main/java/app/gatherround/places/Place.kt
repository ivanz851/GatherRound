package app.gatherround.places

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: Int,
    val title: String,
    val address: String,
    val location: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val description: String? = null,
    val categories: List<String> = emptyList(),
    val rating: Double? = null,
    val imageUrl: String? = null
)