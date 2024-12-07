package app.gatherround

import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.MetroStation
import app.gatherround.places.Place
import app.gatherround.places.PlacesData
import java.io.File
import java.io.FileNotFoundException

fun findOptimalPlaces(
    metroGraph: MetroGraph,
    selectedStations: List<Pair<String, Int>>,
    placesData: PlacesData,
): List<Place> {
    val optimalStation = metroGraph.findOptimalStation(selectedStations)!!
    val places = placesData.getPlacesByStation(optimalStation)

    return emptyList()
}

fun main() {
    val placesData = PlacesData()

    val places = placesData.fetchPlacesInMoscow()

    if (places.isNotEmpty()) {
        println("Полученные места: ${places.size}\n")
        places.forEach { place ->
            println(place)
        }
    } else {
        println("Не удалось получить места.")
    }
}