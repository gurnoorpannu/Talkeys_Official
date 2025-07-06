package com.example.talkeys_new.screens.events.exploreEvents

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.talkeys_new.screens.events.EventViewModel
import com.example.talkeys_new.screens.events.provideEventApiService
import com.example.talkeys_new.screens.events.EventsRepository
import com.example.talkeys_new.screens.common.HomeTopBar
import com.example.talkeys_new.R
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.screens.common.BottomBar
import com.example.talkeys_new.screens.common.Footer
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ExploreEventsScreen(navController: NavController) {
    val context = LocalContext.current

    // Create ViewModel manually since it has dependencies
    val viewModel: EventViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val api = provideEventApiService(context)
                return EventViewModel(EventsRepository(api)) as T
            }
        }
    )

    val eventList by viewModel.eventList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val showLiveEvents by viewModel.showLiveEvents.collectAsState()

    // Group events by category
    val groupedEvents = remember(eventList) {
        eventList.groupBy { it.category ?: "Uncategorized" }
    }

    // Load data when screen starts
    LaunchedEffect(Unit) {
        Log.d("ExploreEventsScreen", "Fetching events...")
        viewModel.fetchAllEvents()
    }

    Scaffold(
        topBar = { HomeTopBar(navController = navController) },
        containerColor = Color.Transparent,
        contentColor = Color.White,
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            when {
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading events",
                            color = Color.Red,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = errorMessage ?: "Unknown error",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { viewModel.fetchAllEvents() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        item {
                            Column {
                                Text(
                                    text = "Explore Events",
                                    style = TextStyle(
                                        fontSize = 22.sp,
                                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.padding(top = 12.dp, start = 19.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Toggle buttons for Live/Past events
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Live Events Button
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .background(
                                                color = if (showLiveEvents) Color(0xFF8A44CB) else Color(
                                                    0x40FFFFFF
                                                ),
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .clickable { viewModel.toggleEventFilter() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Live Events",
                                            color = Color.White,
                                            fontWeight = if (showLiveEvents) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 14.sp
                                        )
                                    }
                                    // Past Events Button
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .background(
                                                color = if (!showLiveEvents) Color(0xFF8A44CB) else Color(
                                                    0x40FFFFFF
                                                ),
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .clickable { viewModel.toggleEventFilter() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Past Events",
                                            color = Color.White,
                                            fontWeight = if (!showLiveEvents) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Show loading shimmer categories or actual content
                        if (isLoading) {
                            // Show loading shimmer categories
                            items(3) { index ->
                                LoadingCategorySection()
                            }
                        } else if (groupedEvents.isEmpty()) {
                            item {
                                Text(
                                    text = if (showLiveEvents) "No live events available" else "No past events available",
                                    color = Color.White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            groupedEvents.forEach { (category, events) ->
                                item {
                                    CategorySection(
                                        category = category,
                                        events = events,
                                        onEventClick = { event ->
                                            Log.d(
                                                "ExploreEventsScreen",
                                                "Event clicked: ${event.name}, ID: ${event._id}"
                                            )
                                            Log.d(
                                                "ExploreEventsScreen",
                                                "Navigating to: eventDetail/${event._id}"
                                            )
                                            // Navigate to event detail screen
                                            navController.navigate("eventDetail/${event._id}")
                                            Log.d(
                                                "ExploreEventsScreen",
                                                "Navigation called successfully"
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(5.dp)) }
                        item { Footer(navController = navController) }
                    }
                }
            }
            BottomBar(
                navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                scrollState = rememberScrollState()
            )
        }
    }
}

@Composable
fun CategorySection(
    category: String,
    events: List<EventResponse>,
    onEventClick: (EventResponse) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Category Header
        Text(
            text = category,
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Horizontal scrollable list of events with enhanced animations
        val lazyListState = rememberLazyListState()

        LazyRow(
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 0.dp, end = 80.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(events) { index, event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) },
                    isCenter = true, // All cards get center effects
                    isFocused = true, // All cards are focused (large size)
                    modifier = Modifier // Remove alpha transparency
                )
            }
        }
    }
}

// Helper data class for multiple return values
data class Triple<A, B, C>(val first: A, val second: B, val third: C)
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// Loading state shimmer categories
@Composable
fun LoadingCategorySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Loading category title
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(18.dp)
                .background(
                    Color(0xFF404040).copy(alpha = 0.6f),
                    RoundedCornerShape(4.dp)
                )
                .padding(bottom = 12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Loading cards row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 0.dp, end = 80.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(3) {
                ShimmerEventCard()
            }
        }
    }
}

