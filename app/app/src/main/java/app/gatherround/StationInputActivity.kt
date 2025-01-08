package app.gatherround

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.TextStyle


val metroStations = listOf(
    "Краснопресненская", "Киевская", "Баррикадная", "Китай-город", "Таганская", "Чистые пруды",
    "Маяковская", "Кольцевая", "Бауманская", "Площадь Революции", "Арбатская", "Новослободская"
)

class StationInputActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    MaterialTheme {
        StationInputScreen()
    }
}

@Composable
fun StationInputScreen() {
    var stations by remember { mutableStateOf(listOf("")) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stations.forEachIndexed { index, station ->
            StationInputField(
                index = index,
                station = station,
                onValueChange = { newStation, ind ->
                    stations = stations.toMutableList().apply { set(ind, newStation) }
                }
            )
        }

        if (stations.size < 6) {
            Button(onClick = {
                stations = stations + ""
            }) {
                Text("Добавить станцию")
            }
        }
    }
}

@Composable
fun StationInputField(index: Int, station: String, onValueChange: (String, Int) -> Unit) {
    var text by remember { mutableStateOf(station) }
    var expanded by remember { mutableStateOf(false) }
    var filteredStations by remember { mutableStateOf(metroStations) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = text,
                onValueChange = { newText: String ->
                    text = newText
                    filteredStations = metroStations.filter { it.startsWith(newText, ignoreCase = true) }
                    expanded = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.Gray),
                textStyle = TextStyle(color = Color.Black)
            )

            if (expanded && filteredStations.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(filteredStations) { station ->
                        Text(
                            text = station,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    text = station
                                    onValueChange(station, index)
                                    expanded = false
                                }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp()
}
