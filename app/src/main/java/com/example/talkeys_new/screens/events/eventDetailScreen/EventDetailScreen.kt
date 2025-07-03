package com.example.talkeys_new.screens.events.eventDetailScreen

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.navigation.NavController
import com.example.talkeys_new.R
import com.example.talkeys_new.screens.common.BottomBar
import com.example.talkeys_new.screens.common.Footer
import com.example.talkeys_new.screens.common.HomeTopBar
import com.example.talkeys_new.screens.events.EventViewModel
import com.example.talkeys_new.screens.events.EventsRepository
import com.example.talkeys_new.screens.events.provideEventApiService
import coil.compose.AsyncImage
import com.example.talkeys_new.dataModels.EventResponse
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventDetailScreen(eventId: String, navController: NavController) {
    Log.d("EventDetailScreen", "Screen opened with eventId: $eventId")

    val context = LocalContext.current
    val repository = remember { EventsRepository(provideEventApiService(context)) }
    val viewModel = remember { EventViewModel(repository) }

    val eventState by viewModel.selectedEvent.collectAsState()
    val isLoading by viewModel.eventLoading.collectAsState()
    val errorMessage by viewModel.eventError.collectAsState()

    Log.d(
        "EventDetailScreen",
        "isLoading: $isLoading, errorMessage: $errorMessage, eventState: $eventState"
    )

    var selectedItem by remember { mutableStateOf("Details") }

    val items = listOf(
        "Details", "Dates & Deadlines", "Prizes", "Link to Community"
    )
    val reorderedItems = remember(selectedItem) {
        listOf(selectedItem) + items.filter { it != selectedItem }
    }

    // Fetch event details on first composition
    LaunchedEffect(eventId) {
        viewModel.fetchEventById(eventId)
    }

    Scaffold(
        topBar = { HomeTopBar(navController = navController) }
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
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Something went wrong",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                eventState != null -> {
                    val event = eventState!!

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .width(418.dp)
                                    .height(266.66501.dp)
                                    .padding(top = 34.dp, start = 20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    EventCard1(
                                        title = event.name ?: "",
                                        location = event.location ?: "",
                                        imageUrl = event.photographs?.firstOrNull(),
                                        modifier = Modifier
                                    )
                                    Column {
                                        Text(
                                            text = event.name ?: "",
                                            style = TextStyle(
                                                fontSize = 22.sp,
                                                fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))

                                        EventDetailRow(R.drawable.college, event.category ?: "")
                                        Spacer(modifier = Modifier.height(10.dp))
                                        EventDetailRow(R.drawable.ic_location, event.location ?: "")
                                        Spacer(modifier = Modifier.height(10.dp))
                                        EventDetailRow(
                                            R.drawable.event_date,
                                            formatEventDate(event.startDate)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        // Show organizer name only if available
                                        if (!event.organizerName.isNullOrEmpty()) {
                                            EventDetailRow(R.drawable.trophy, event.organizerName)
                                            Spacer(modifier = Modifier.height(10.dp))
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(5.33.dp)) }

                        item { EventInfoBox(navController = navController, event = event) }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            SelectionBar(items, selectedItem) { selectedItem = it }
                            Spacer(modifier = Modifier.height(33.dp))
                        }

                        val highlightColor = Color(0xFF8A44CB)
                        val normalColor = Color(0xFF171717)

                        items(reorderedItems) { item ->
                            val isHighlighted = item == selectedItem
                            var showHighlight by remember { mutableStateOf(isHighlighted) }

                            val backgroundColor by animateColorAsState(
                                targetValue = if (showHighlight) highlightColor else normalColor,
                                animationSpec = tween(durationMillis = 500),
                                label = "highlight"
                            )

                            LaunchedEffect(selectedItem) {
                                if (isHighlighted) {
                                    showHighlight = true
                                    kotlinx.coroutines.delay(2000)
                                    showHighlight = false
                                }
                            }

                            InfoCard1(
                                title = item,
                                content = when (item) {
                                    "Details" -> event.eventDescription ?: "No details available."
                                    "Dates & Deadlines" -> buildString {
                                        append("Start Date: ${formatEventDate(event.startDate)}\n")
                                        append("Start Time: ${event.startTime ?: "Not specified"}\n")
                                        append("Duration: ${event.duration ?: "Not specified"}\n")
                                        append("Registration Ends: ${event.endRegistrationDate ?: "Not specified"}")
                                    }

                                    "Prizes" -> event.prizes ?: "No prizes available."
                                    "Link to Community" -> "No information available"
                                    else -> "No information available"
                                },
                                backgroundColor = backgroundColor
                            )

                            Spacer(modifier = Modifier.height(30.dp))
                        }

                        item {
                            Footer(navController = navController)
                        }
                    }
                }
            }

            BottomBar(
                navController,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                scrollState = rememberScrollState()
            )
        }
    }
}

@Composable
fun SelectionBar(items: List<String>, selectedItem: String, onSelect: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFF2E2E2E), shape = RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { item ->
                SelectionItem(
                    text = item,
                    isSelected = item == selectedItem,
                    onClick = { onSelect(item) }
                )
            }
        }
    }
}

@Composable
fun SelectionItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) Color(0xFF8A44CB) else Color(0xFF444444),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun InfoCard1(title: String, content: String, backgroundColor: Color) {
    Column(
        modifier = Modifier
            .width(340.dp)
            .wrapContentHeight()
            .padding(start = 32.dp)
            .background(Color(0xFF171717), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VerticalIndicator1()
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = content,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Light,
                color = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun EventDetailText(text: String) {
    Text(
        text = text,
        style = TextStyle(fontSize = 22.sp, color = Color.White),
        modifier = Modifier
            .width(121.dp)
            .height(26.dp)
    )
}

@Composable
fun EventDetailRow(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                color = Color.White
            ),
            modifier = Modifier
                .width(234.dp)
                .height(19.dp)
        )
    }
}

@Composable
fun EventCard1(title: String, location: String, imageUrl: String?, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_transition")

    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    val animatedRotationY by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation_y"
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .width(150.dp)
            .height(266.66501.dp)
            .graphicsLayer {
                rotationY = animatedRotationY
            }
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Shimmer overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.6f),
                                Color.Transparent
                            ),
                            start = Offset(shimmerTranslateAnim, 0f),
                            end = Offset(shimmerTranslateAnim + 150f, size.height)
                        )
                        onDrawBehind {
                            drawRect(brush = brush)
                        }
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF404040).copy(alpha = 0.6f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.5f
                )

                // Shimmer overlay for placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithCache {
                            val brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.4f),
                                    Color.Transparent
                                ),
                                start = Offset(shimmerTranslateAnim, 0f),
                                end = Offset(shimmerTranslateAnim + 150f, size.height)
                            )
                            onDrawBehind {
                                drawRect(brush = brush)
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun VerticalIndicator1() {
    Box(
        modifier = Modifier
            .width(5.dp)
            .height(39.dp)
            .background(color = Color(0xFF8A44CB), shape = RoundedCornerShape(50.dp))
    )
}

@Composable
fun EventInfoBox(
    modifier: Modifier = Modifier,
    navController: NavController,
    event: EventResponse
) {
    val context = LocalContext.current
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(183) } // Initial like count

    Card(
        modifier = modifier
            .width(370.dp)
            .height(241.dp)
            .padding(start = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF171717), shape = RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Row containing "Cost for Event" and icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Cost for Event" text with actual cost
                    Column {
                        Text(
                            text = "Cost for Event",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (event.ticketPrice.toString() == "0" || event.ticketPrice.toString()
                                    .isEmpty()
                            ) "Free" else "₹${event.ticketPrice}",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8A44CB)
                            )
                        )
                    }

                    // Icons Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        // Like Button with Like Count
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_like),
                                contentDescription = "Like",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        isLiked = !isLiked
                                        likeCount += if (isLiked) 1 else -1
                                    },
                                colorFilter = ColorFilter.tint(
                                    if (isLiked) Color.Red else Color.White
                                )
                            )
                            Text(
                                text = "$likeCount likes",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }

                        // Comment Icon
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_comment),
                                contentDescription = "Comment",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }

                        // Share Button
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_share),
                                contentDescription = "Share",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        // Create share intent
                                        val shareText = "Check out this event: ${event.name}\n" +
                                                "Location: ${event.location}\n" +
                                                "Date: ${formatEventDate(event.startDate)} at ${event.startTime}\n" +
                                                "Price: ${if (event.ticketPrice.toString() == "0") "Free" else "₹${event.ticketPrice}"}"

                                        val sendIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        val shareIntent = android.content.Intent.createChooser(
                                            sendIntent,
                                            "Share Event"
                                        )
                                        context.startActivity(shareIntent)
                                    },
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Register Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    RegisterButton(navController = navController)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Dashed Line Divider
                Image(
                    painter = painterResource(id = R.drawable.divider_line),
                    contentDescription = "Divider Line",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(320.dp)
                        .height(2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Event Tags
                EventTags(event = event)
            }
        }
    }
}

@Composable
fun EventTags(event: EventResponse) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // First Row (Aligned Right) - Category and Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EventTag(event.category ?: "Event")
            Spacer(modifier = Modifier.width(12.dp))
            EventTag(event.mode ?: "Mode")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Second Row (Aligned Left) - Price and Team Event status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EventTag(
                if (event.ticketPrice.toString() == "0" || event.ticketPrice.toString()
                        .isEmpty()
                ) "Free Event" else "Paid Event",
                isBigger = true
            )
            Spacer(modifier = Modifier.width(12.dp))
            EventTag(if (event.isTeamEvent) "Team Event" else "Solo Event")
        }
    }
}

@Composable
fun IconWithText(iconRes: Int, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Center icon and text
        verticalArrangement = Arrangement.spacedBy(2.dp) // Space between icon and text
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "Icon",
            modifier = Modifier.size(20.dp)
        )
        if (text.isNotBlank()) { // Avoids unnecessary empty space
            Text(
                text = text,
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_light)),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EventTag(text: String, isBigger: Boolean = false) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(0xFFCCA1F4),
                shape = RoundedCornerShape(size = 27.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .then(
                if (isBigger) Modifier
                    .width(149.dp)
                    .height(25.dp)
                else Modifier
                    .width(104.dp)
                    .height(25.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCCA1F4)
            )
        )
    }
}

@Composable
fun RegisterButton(navController: NavController) {
    Box(
        modifier = Modifier
            .width(133.dp)
            .height(39.dp)
            .background(Color(0xFF8A44CB), shape = RoundedCornerShape(8.dp))
            .clickable { navController.navigate("event_registration") }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Register Now",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

fun formatEventDate(date: String?): String {
    if (date.isNullOrEmpty()) return "Not specified"

    return try {
        // Handle ISO date format (2025-02-12T18:00:00.000Z)
        val dateOnly = if (date.contains("T")) {
            date.split("T")[0] // Extract just the date part before 'T'
        } else {
            date
        }

        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val parsedDate = parser.parse(dateOnly)

        if (parsedDate != null) {
            formatter.format(parsedDate)
        } else {
            date
        }
    } catch (e: Exception) {
        date
    }
}