package com.example.talkeys_new.screens.events.exploreEvents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.talkeys_new.dataModels.EventResponse

@Composable
fun EventRow(events: List<EventResponse>, navController: NavController) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { event ->
            EventCard(
                event = event,
                onClick = {
                    // TODO: Navigate to event detail screen
                    // navController.navigate("event_detail/${event._id}")
                },
                modifier = Modifier
            )
        }
    }
}