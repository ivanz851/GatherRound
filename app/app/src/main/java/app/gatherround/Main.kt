package app.gatherround

import kotlinx.serialization.json.Json
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import java.io.File
import java.io.FileNotFoundException

fun loadMetroDataFromFile(filePath: String): MetroData {
    val file = File(filePath)
    if (!file.exists()) {
        throw FileNotFoundException("File not found: $filePath")
    }

    val jsonContent = file.readText()
    val json = Json {
        ignoreUnknownKeys = true
    }

    return json.decodeFromString<MetroData>(jsonContent)
}

fun main() {
    val metroData: MetroData = loadMetroDataFromFile("C:\\\\Users\\test\\user\\ProjectSeminar2024-25\\GatherRound\\app\\app\\src\\main\\resources\\get-scheme-metadata.json")

    /*
    metroData.stations.forEach { (id, station) ->
        println("Station ID: $id, Name: ${station.name}, Line: ${station.lineId}, Label: ${station.labelId}")
    }
    */

    val graph = MetroGraph(metroData)
    val (timeInSecs, path) = graph.findShortestPath("Lermontovsky Prospekt", "Novoyasenevskaya")

    val timeInMins: Int = timeInSecs / 60
    println("Время в пути (мин): $timeInMins")
    println("Путь:\n ${path?.joinToString(" ->\n") { it.name }}")
}
