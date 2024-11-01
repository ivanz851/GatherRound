package app.gatherround

import kotlinx.serialization.json.Json
import app.gatherround.metro.MetroData
import java.io.File
import java.io.FileNotFoundException

fun loadMetroDataFromFile(filePath: String): MetroData {
    // Check if the file exists before trying to read it
    val file = File(filePath)
    if (!file.exists()) {
        throw FileNotFoundException("File not found: $filePath")
    }

    // Read the JSON content from the file
    val jsonContent = file.readText()

    // Deserialize the JSON content into a MetroData object
    return Json.decodeFromString<MetroData>(jsonContent)
}

fun main() {
    val metroData: MetroData = loadMetroDataFromFile("C:\\\\Users\\test\\user\\ProjectSeminar2024-25\\GatherRound\\app\\app\\src\\main\\resources\\get-scheme-metadata.json")

    // Вывод информации
    metroData.stations.forEach { (id, station) ->
        println("Station ID: $id, Name: ${station.name}, Line: ${station.lineId}, Label: ${station.labelId}")
    }
}
