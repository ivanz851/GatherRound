package app.gatherround.stations_input

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.Station

/**
 * Точка входа в приложение.
 *
 * 1. Загружает JSON-метаданные схемы метро из *assets*-папки.
 * 2. На их основе строит [MetroGraph] и список имён станций (для автодополнения).
 * 3. Асинхронно подтягивает initial.svg с GitHub — результат передаёт на [InputScreen].
 * 4. Держит список выбранных пользователем станций в `mutableStateListOf`, чтобы
 *    `InputScreen` реагировал на изменения.
 *
 * @property fileDownloaderVM ViewModel, отвечающий за скачивание файлов (SVG)
 * @property selectedStations `mutableStateListOf` — список выбраных станций
 */
class MainActivity : ComponentActivity() {

    private val fileDownloaderVM: FileDownloaderVM by viewModels()
    private val selectedStations = mutableStateListOf<Station>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val metroData = MetroData.loadFromAssets(applicationContext, "get-scheme-metadata.json")
        val stationsNames = metroData.stationsNames
        val graph = MetroGraph(metroData)

        setContent {
            MaterialTheme {
                val htmlContent = remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val url = "https://raw.githubusercontent.com/ivanz851/GatherRound/refs/heads/main/app/app/src/main/assets/initial.svg"
                    fileDownloaderVM.downloadFileFromServer(url).observe(this@MainActivity) { response ->
                        val svg = response.string()
                        htmlContent.value = getHTMLBody(svg)
                    }
                }

                InputScreen(
                    metroData = metroData,
                    stationsNames = stationsNames,
                    graph = graph,
                    htmlContent = htmlContent.value,
                    selectedStations = selectedStations
                )
            }
        }
    }
}
