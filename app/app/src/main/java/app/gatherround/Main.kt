package app.gatherround

import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.places.PlacesData
import java.io.File
import java.io.FileNotFoundException

fun main() {
    val placesData = PlacesData()

    val places = placesData.fetchPlacesInMoscow()

    if (places.isNotEmpty()) {
        println("Полученные места:")
        places.forEach { place ->
            println(place)
        }
    } else {
        println("Не удалось получить места.")
    }
}