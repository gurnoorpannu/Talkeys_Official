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
import androidx.navigation.NavController
import com.example.talkeys_new.api.DashboardApiService
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.screens.authentication.TokenManager
import com.example.talkeys_new.screens.events.exploreEvents.EventCard
import com.example.talkeys_new.screens.events.exploreEvents.ShimmerEventCard
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedEventsScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val scope = rememberCoroutineScope()

    var events by remember { mutableStateOf<List<EventResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Create API service
    val apiService = remember {
        Retrofit.Builder()
            .baseUrl("https://talkeys-backend.onrender.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DashboardApiService::class.java)
    }

    // Load liked events
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val token = tokenManager.getToken()
                if (token != null) {
                    val response = apiService.getUserEvents("Bearer $token", "bookmarked")
                    if (response.isSuccessful) {
                        events = response.body()?.events ?: emptyList()
                    } else {
                        errorMessage = "Failed to load liked events"
                    }
                } else {
                    errorMessage = "Authentication required"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
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
                text = "Liked Events",
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
                            ShimmerEventCard()
                        }
                    }
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    try {
                                        val token = tokenManager.getToken()
                                        if (token != null) {
                                            val response = apiService.getUserEvents("Bearer $token", "bookmarked")
                                            if (response.isSuccessful) {
                                                events = response.body()?.events ?: emptyList()
                                            } else {
                                                errorMessage = "Failed to load liked events"
                                            }
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Network error: ${e.message}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }

                events.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No liked events found",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Like events to see them here",
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