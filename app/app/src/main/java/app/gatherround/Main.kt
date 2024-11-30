package app.gatherround

import kotlinx.serialization.json.Json
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.getSchemeMetadataPath
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
    val metroData: MetroData = loadMetroDataFromFile(getSchemeMetadataPath())
    val metroGraph = MetroGraph(metroData)

    val stations = metroGraph.getVertices()

    stations.forEach { station ->
        println("${station.name}, ${station.lineId}")
    }

}
