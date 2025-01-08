package app.gatherround

import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.MetroStation
import app.gatherround.places.Place
import app.gatherround.places.PlacesData

fun findOptimalPlaces(
    metroGraph: MetroGraph,
    selectedStations: Set<MetroStation>,
    placesData: PlacesData,
): List<Place> {
    val optimalStation = metroGraph.findOptimalVertex(selectedStations).first

    println("OK, ${optimalStation}\n")

    return placesData.getPlacesByStation(optimalStation!!)
}

fun main() {
    val metroData = MetroData().loadMetroDataFromFile()

    /*
    println("MetroData: ${metroData.stations.size}")
    metroData.stations.forEach { (id, station) ->
        println("${station.name}, ${station.lineId}")
    }
    */

    val graph = MetroGraph(metroData)

    val (optimalStataion, time) = graph.findOptimalVertex(setOf(metroData.getStationByNameAndLineId("Tretyakovskaya", 6),
        metroData.getStationByNameAndLineId("Delovoy Tsentr", 15),
        metroData.getStationByNameAndLineId("Tsaritsyno", 2))
        .filterNotNull().toSet())

    if (optimalStataion == null) {
        println("Ошибка! Оптимальная станция не найдена.")
    } else {
        println("Оптимальная станция: ${optimalStataion.name}, ${optimalStataion.lineId}, " +
                "максимальное время в пути: $time")
    }
    val placesData = PlacesData()

    var places = placesData.fetchPlacesInMoscow()

    places = findOptimalPlaces(graph,
        setOf(metroData.getStationByNameAndLineId("Tretyakovskaya", 6),
            metroData.getStationByNameAndLineId("Delovoy Tsentr", 15),
            metroData.getStationByNameAndLineId("Tsaritsyno", 2))
            .filterNotNull().toSet(), placesData)

    if (places.isNotEmpty()) {
        println("Полученные места: ${places.size}\n")
        places.forEach { place ->
            println(place)
        }
    } else {
        println("Не удалось получить места.")
    }
}