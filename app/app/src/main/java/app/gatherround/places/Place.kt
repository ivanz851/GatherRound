package app.gatherround.places

import kotlinx.serialization.Serializable

/**
 * DTO для описания места встречи.
 *
 * @property id       уникальный идентификатор места
 * @property title    название
 * @property address  полный адрес
 * @property coords   географические координаты (широта и долгота) или `null`, если неизвестны
 * @property subway   название ближайшей станции метро на русском либо "",
 *                    если не задано
 */
@Serializable
data class Place(
    val id: Int,
    val title: String,
    val address: String,
    val coords: Coordinates? = null,
    val subway: String,
) {
    @Serializable
    data class Coordinates(
        val lat: Double?,
        val lon: Double?
    )
}