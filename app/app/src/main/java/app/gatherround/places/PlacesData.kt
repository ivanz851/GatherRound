package app.gatherround.places

import android.util.Pair
import app.gatherround.metro.Location
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.Station
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Клиент KudaGo-API (v1.4) + набор утилит для поиска птимальных мест для встречи рядом со
 * станцией метро.
 */
class PlacesData {

    /* ------------------------------ HTTP & JSON ------------------------------ */
    private val client = OkHttpClient()
    private val jsonParser = Json {
        ignoreUnknownKeys = true
    }

    /* ----------------------------- DTO ответа -------------------------------- */
    @Serializable
    data class PlacesResponse(
        val count: Int,
        val next: String?,
        val previous: String?,
        val results: List<Place>
    )

    /** Выполняет GET-запрос и возвращает тело ответа как строку или `null` при неудаче. */
    private fun performRequest(url: String): String? {
        return try {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Превращает JSON KudaGo в список [Place]; в случае ошибки — пустой список. */
    fun parsePlaces(json: String): List<Place> {
        return try {
            val response = jsonParser.decodeFromString<PlacesResponse>(json)
            response.results
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Загружает все места из KudaGo для Москвы. */
    fun fetchPlacesInMoscow(): List<Place> {
        val url = "https://kudago.com/public-api/v1.4/places/?lang=ru&location=msk&fields=id,title,address,coords,subway"
        val json = performRequest(url)
        return if (json != null) {
            parsePlaces(json)
        } else {
            emptyList()
        }
    }

    /** Формирует URL для запроса мест в заданном радиусе (в метрах) от заданных координат. */
    private fun getPlacesByLocationAndRadius(location: Location, radius: Int): String? {
        val url = "https://kudago.com/public-api/v1.4/places/" +
                "?lang=ru" +
                "&location=msk" +
                "&fields=id,title,address,coords,subway" +
                "&lon=${location.lon}" +
                "&lat=${location.lat}" +
                "&radius=$radius"
        return performRequest(url)
    }

    /**
     * Возвращает JSON с списком мест в радиусе ~ 1,2 км от станции.
     */
    fun getPlacesByStation(station: Station): String? {
        return getPlacesByLocationAndRadius(station.location!!, 1200)
    }
}


/**
 * @param metroGraph       граф метро
 * @param selectedStations набор станций, с которых выезжают люди
 * @param placesData       информация о всех местах встречи
 *
 * @return `Pair(оптимальная станция, JSON с информацией о подобранных местах рядом с оптимальной станцией)`
 */
fun findOptimalPlaces(
    metroGraph: MetroGraph,
    selectedStations: Set<Station>,
    placesData: PlacesData,
): Pair<Station?, String?> {
    val optimalStation = metroGraph.findOptimalVertex(selectedStations).first
    return Pair(optimalStation, placesData.getPlacesByStation(optimalStation!!))
}