package com.example.talkeys_new.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.talkeys_new.api.DashboardApiService
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.screens.authentication.TokenManager
import com.example.talkeys_new.screens.common.ErrorCard
import com.example.talkeys_new.screens.dashboard.DashboardViewModel
import com.example.talkeys_new.screens.events.exploreEvents.EventCard
import com.example.talkeys_new.screens.events.exploreEvents.SkeletonEventCard
import com.example.talkeys_new.utils.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisteredEventsScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current)
    )
) {
    val scope = rememberCoroutineScope()
    
    // State collection
    val events by viewModel.userEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Fetch registered events when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchUserEvents("registered")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Registered Events",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when {
                isLoading -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(6) {
                            SkeletonEventCard()
                        }
                    }
                }

                error != null -> {
                    ErrorCard(
                        message = error ?: "An unknown error occurred",
                        onRetry = {
                            viewModel.fetchUserEvents("registered")
                        }
                    )
                }

                events.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No registered events found",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Register for events to see them here",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(events) { event ->
                            EventCard(
                                event = event,
                                onClick = {
                                    navController.navigate("eventDetail/${event._id}")
                                },
                                isFocused = true
                            )
                        }
                    }
                }
            }
        }
    }
}