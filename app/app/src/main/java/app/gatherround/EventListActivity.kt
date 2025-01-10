package app.gatherround

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class EventListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получаем список мероприятий из Intent
        val events = intent.getStringArrayListExtra("events") ?: arrayListOf()

        setContent {
            EventListScreen(events)
        }
    }
}

@Composable
fun EventListScreen(events: List<String>) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Список мероприятий",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(events) { event ->
                    Text(
                        text = event,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
