package com.example.talkeys_new.screens.events.exploreEvents

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import kotlinx.coroutines.delay

/**
 * Screen for exploring events with filtering and categorization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreEventsScreen(navController: NavController) {
    val context = LocalContext.current

    // Create ViewModel with proper error handling
    val viewModel: EventViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return try {
                    val api = provideEventApiService(context)
                    EventViewModel(EventsRepository(api)) as T
                } catch (e: Exception) {
                    Log.e("ExploreEventsScreen", "Error creating ViewModel", e)
                    throw e
                }
            }
        }
    )

    // Collect state with proper error handling
    val eventList by viewModel.eventList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val showLiveEvents by viewModel.showLiveEvents.collectAsState()

    // Group events by category with null safety
    val groupedEvents = remember(eventList) {
        eventList.groupBy { event ->
            event.category?.takeIf { it.isNotBlank() } ?: "Uncategorized"
        }.filterValues { it.isNotEmpty() } // Remove empty categories
    }

    // Load data when screen starts with retry mechanism
    LaunchedEffect(Unit) {
        Log.d("ExploreEventsScreen", "Screen launched, fetching events...")
        viewModel.fetchAllEvents()
    }

    // Auto-refresh mechanism (optional - can be removed if not needed)
    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(300000) // 5 minutes
            if (!isLoading && errorMessage == null) {
                viewModel.refreshEvents()
            }
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar(navController = navController)
        },
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
            // Background image
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Main content with pull-to-refresh
            ExploreEventsContent(
                groupedEvents = groupedEvents,
                isLoading = isLoading,
                errorMessage = errorMessage,
                showLiveEvents = showLiveEvents,
                onToggleFilter = { viewModel.toggleEventFilter() },
                onRetry = { viewModel.fetchAllEvents() },
                onRefresh = { viewModel.fetchAllEvents() },
                onEventClick = { event ->
                    handleEventClick(event, navController)
                },
                onClearError = { viewModel.clearErrors() },
                navController = navController
            )

            // Bottom navigation
            BottomBar(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                scrollState = rememberScrollState()
            )
        }
    }
}

/**
 * Main content composable for the explore events screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreEventsContent(
    groupedEvents: Map<String, List<EventResponse>>,
    isLoading: Boolean,
    errorMessage: String?,
    showLiveEvents: Boolean,
    onToggleFilter: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onEventClick: (EventResponse) -> Unit,
    onClearError: () -> Unit,
    navController: NavController
) {
    val pullToRefreshState = rememberPullToRefreshState()
    
    // Handle pull to refresh
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            onRefresh()
        }
    }
    
    // Reset refresh state when loading completes
    LaunchedEffect(isLoading) {
        if (!isLoading && pullToRefreshState.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        when {
            errorMessage != null -> {
                ErrorContent(
                    errorMessage = errorMessage,
                    onRetry = onRetry,
                    onClearError = onClearError
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                // Header section
                item {
                    HeaderSection(
                        showLiveEvents = showLiveEvents,
                        onToggleFilter = onToggleFilter
                    )
                }

                // Content based on loading state
                if (isLoading) {
                    items(3) {
                        LoadingCategorySection()
                    }
                } else if (groupedEvents.isEmpty()) {
                    item {
                        EmptyStateContent(showLiveEvents = showLiveEvents)
                    }
                } else {
                    // Display categorized events
                    groupedEvents.forEach { (category, events) ->
                        item {
                            CategorySection(
                                category = category,
                                events = events,
                                onEventClick = onEventClick
                            )
                        }
                    }
                }

                    // Footer
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Footer(navController = navController)
                    }
                }
            }
        }
        
        // Pull to refresh indicator
        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * Header section with title and filter buttons
 */
@Composable
private fun HeaderSection(
    showLiveEvents: Boolean,
    onToggleFilter: () -> Unit
) {
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

        // Filter buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterButton(
                text = "Live Events",
                isSelected = showLiveEvents,
                onClick = { if (!showLiveEvents) onToggleFilter() },
                modifier = Modifier.weight(1f)
            )

            FilterButton(
                text = "Past Events",
                isSelected = !showLiveEvents,
                onClick = { if (showLiveEvents) onToggleFilter() },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Filter button component
 */
@Composable
private fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                color = if (isSelected) Color(0xFF8A44CB) else Color(0x40FFFFFF),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

/**
 * Error content display
 */
@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    onClearError: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Unable to load events",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onClearError,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("Dismiss")
            }

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8A44CB)
                )
            ) {
                Text("Retry")
            }
        }
    }
}

/**
 * Empty state content
 */
@Composable
private fun EmptyStateContent(showLiveEvents: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (showLiveEvents) "No live events available" else "No past events available",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Check back later for new events",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Category section with horizontal scrollable event list
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategorySection(
    category: String,
    events: List<EventResponse>,
    onEventClick: (EventResponse) -> Unit
) {
    if (events.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Category header
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

        // Horizontal scrollable list
        val lazyListState = rememberLazyListState()

        LazyRow(
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 0.dp, end = 80.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(
                items = events,
                key = { _, event -> event._id ?: event.hashCode() }
            ) { index, event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) },
                    modifier = Modifier
                        .animateItemPlacement() // Smooth animations
                )
            }
        }
    }
}

/**
 * Loading shimmer for category sections
 */
@OptIn(ExperimentalFoundationApi::class)
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
                .height(20.dp)
                .background(
                    Color(0xFF404040).copy(alpha = 0.6f),
                    RoundedCornerShape(4.dp)
                )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Loading cards row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 0.dp, end = 80.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(3) { index ->
                SkeletonEventCard(
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}
/**
 * Handles event click with proper error handling and navigation
 */
private fun handleEventClick(event: EventResponse, navController: NavController) {
    try {
        val eventId = event._id
        if (eventId.isNullOrBlank()) {
            Log.e("ExploreEventsScreen", "Event ID is null or blank for event: ${event.name}")
            return
        }

        Log.d("ExploreEventsScreen", "Event clicked: ${event.name}, ID: $eventId")
        Log.d("ExploreEventsScreen", "Navigating to: eventDetail/$eventId")

        navController.navigate("eventDetail/$eventId") {
            // Add proper navigation options
            launchSingleTop = true
            restoreState = true
        }

        Log.d("ExploreEventsScreen", "Navigation called successfully")
    } catch (e: Exception) {
        Log.e("ExploreEventsScreen", "Error handling event click", e)
    }
}