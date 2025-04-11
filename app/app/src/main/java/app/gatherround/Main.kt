package app.gatherround

import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.RUSSIAN
import app.gatherround.metro.Station
import app.gatherround.places.Place
import app.gatherround.places.PlacesData

fun findOptimalPlaces(
    metroGraph: MetroGraph,
    selectedStations: Set<Station>,
    placesData: PlacesData,
): String? {
    println("👉 Выбранные станции (${selectedStations.size}):")
    selectedStations.forEachIndexed { index, station ->
        val name = station.name[RUSSIAN] ?: "Без названия"
        println("  ${index + 1}. $name (ID: ${station.id}, Линия: ${station.lineId})")
    }

    val optimalStation = metroGraph.findOptimalVertex(selectedStations).first

    println("optimal station = ${optimalStation!!}")
    return placesData.getPlacesByStation(optimalStation!!)
}

fun main() {
    val metroData = MetroData().loadMetroDataFromFile()

    val graph = MetroGraph(metroData)

    graph.processLinesTraversal()

    return





    graph.printAllConnections()

    /*
    val chosenStations = setOf(metroData.getStationByNameAndLineId("Третьяковская", 5),
        metroData.getStationByNameAndLineId("Деловой центр", 13),
        metroData.getStationByNameAndLineId("Царицыно", 2))
        .filterNotNull().toSet()
     */
    val chosenStations = setOf(metroData.getStationByNameAndLineId("Академическая", 5),
        metroData.getStationByNameAndLineId("Петровско-Разумовская", 7))
        .filterNotNull().toSet()

    val (optimalStataion, time) = graph.findOptimalVertex(chosenStations)

    if (optimalStataion == null) {
        println("Ошибка! Оптимальная станция не найдена.")
    } else {
        println("Оптимальная станция: ${optimalStataion.name}, ${optimalStataion.lineId}, " +
                "максимальное время в пути: $time")
    }

    val placesData = PlacesData()

    var places = placesData.fetchPlacesInMoscow()

    places = PlacesData().parsePlaces(findOptimalPlaces(graph, chosenStations, placesData)!!)

    if (places.isNotEmpty()) {
        println("Полученные места: ${places.size}\n")
        places.forEach { place ->
            println(place)
        }
    } else {
        println("Не удалось получить места.")
    }
}