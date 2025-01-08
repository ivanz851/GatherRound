package app.gatherround.places

import app.gatherround.metro.MetroStation
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URLEncoder


class PlacesData {
    private val client = OkHttpClient()
    private val jsonParser = Json {
        ignoreUnknownKeys = true
    }

    @Serializable
    data class PlacesResponse(
        val count: Int,
        val next: String?,
        val previous: String?,
        val results: List<Place>
    )

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

    private fun parsePlaces(json: String): List<Place> {
        return try {
            val response = jsonParser.decodeFromString<PlacesResponse>(json)
            response.results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun fetchPlacesInMoscow(): List<Place> {
        val url = "https://kudago.com/public-api/v1.4/places/?lang=ru&location=msk&fields=id,title,address,coords,subway"
        val json = performRequest(url)
        return if (json != null) {
            parsePlaces(json)
        } else {
            emptyList()
        }
    }


    fun getPlacesByStation(metroStation: MetroStation): List<Place> {
        val stationName = URLEncoder.encode(metroStation.name, "UTF-8")
        val url = "https://kudago.com/public-api/v1.4/places/?lang=ru&location=msk&fields=id,title,address,coords,subway&subway=${stationName}"
        val json = performRequest(url)
        return if (json != null) {
            parsePlaces(json)
        } else {
            emptyList()
        }
    }
}