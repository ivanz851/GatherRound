package app.gatherround

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.gatherround.places.Place
import app.gatherround.places.PlacesData

class EventListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jsonString = intent.getStringExtra("places_json") ?: ""

        val places = PlacesData().parsePlaces(jsonString)

        setContent {
            EventListScreen(places)
        }
    }
}

@Composable
fun EventListScreen(places: List<Place>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Список мест для встречи:", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        places.forEach { place ->
            EventItem(place = place)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun EventItem(place: Place) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Название: ${place.title}")
        Text("Адрес: ${place.address}")

        /*
        place.coords?.let {
            Text("Координаты: ${it.lat}, ${it.lon}")
        }
        Text("Линии метро: ${place.subway}")
         */
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEventListScreen() {
    EventListScreen(
        places = listOf(
            Place(
                id = 1,
                title = "Place 1",
                address = "Address 1",
                coords = Place.Coordinates(40.7128, -74.0060),
                subway = "Line 1, Line 2"
            ),
            Place(
                id = 2,
                title = "Place 2",
                address = "Address 2",
                coords = Place.Coordinates(48.8566, 2.3522),
                subway = "Line A, Line B"
            )
        )
    )
}