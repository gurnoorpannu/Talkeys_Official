package com.example.talkeys_new.screens.events.eventDetailScreen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.talkeys_new.R
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.screens.common.BottomBar
import com.example.talkeys_new.screens.common.Footer
import com.example.talkeys_new.screens.common.HomeTopBar
import com.example.talkeys_new.screens.events.EventViewModel
import com.example.talkeys_new.screens.events.EventsRepository
import com.example.talkeys_new.screens.events.provideEventApiService
import java.text.SimpleDateFormat
import java.util.*

// Constants
private object EventDetailConstants {
     val CARD_WIDTH = 418.dp
     val CARD_HEIGHT = 266.66501.dp
     val EVENT_CARD_WIDTH = 150.dp
     val INFO_CARD_WIDTH = 370.dp
     val INFO_CARD_HEIGHT = 241.dp
     val CONTENT_CARD_WIDTH = 340.dp
     val REGISTER_BUTTON_WIDTH = 133.dp
    val REGISTER_BUTTON_HEIGHT = 39.dp
     val ANIMATION_DURATION = 500
     val HIGHLIGHT_DURATION = 2000L
     val INITIAL_LIKE_COUNT = 183
     val DEFAULT_ICON_SIZE = 24.dp
     val SMALL_ICON_SIZE = 16.dp
     val MEDIUM_ICON_SIZE = 20.dp
}

// UI State
private data class EventDetailUiState(
    val selectedItem: String = "Details",
    val isLiked: Boolean = false,
    val likeCount: Int = EventDetailConstants.INITIAL_LIKE_COUNT
)

@Composable
fun EventDetailScreen(
    eventId: String,
    navController: NavController
) {
    // Input validation
    if (eventId.isBlank()) {
        Log.e("EventDetailScreen", "Invalid eventId: $eventId")
        ErrorScreen(
            message = "Invalid event ID",
            onRetry = { navController.popBackStack() }
        )
        return
    }

    Log.d("EventDetailScreen", "Screen opened with eventId: $eventId")

    val context = LocalContext.current
    val repository = remember { EventsRepository(provideEventApiService(context)) }
    val viewModel = remember { EventViewModel(repository) }

    // State management
    val eventState by viewModel.selectedEvent.collectAsState()
    val isLoading by viewModel.eventLoading.collectAsState()
    val errorMessage by viewModel.eventError.collectAsState()

    var uiState by remember { mutableStateOf(EventDetailUiState()) }

    // Fetch event details
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
            // Background image
            BackgroundImage()

            // Content based on state
            when {
                isLoading -> {
                    LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    ErrorScreen(
                        message = errorMessage!!,
                        onRetry = { viewModel.fetchEventById(eventId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                eventState != null -> {
                    EventContent(
                        event = eventState!!,
                        navController = navController,
                        uiState = uiState,
                        onUiStateChange = { uiState = it }
                    )
                }

                else -> {
                    ErrorScreen(
                        message = "Event not found",
                        onRetry = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Bottom navigation
            BottomBar(
                navController = navController,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                scrollState = rememberScrollState()
            )
        }
    }
}

@Composable
private fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.background),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier,
        color = Color.White
    )
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A44CB))
        ) {
            Text("Retry", color = Color.White)
        }
    }
}

@Composable
private fun EventContent(
    event: EventResponse,
    navController: NavController,
    uiState: EventDetailUiState,
    onUiStateChange: (EventDetailUiState) -> Unit
) {
    val tabItems = listOf("Details", "Dates & Deadlines", "Prizes", "Link to Community")
    val reorderedItems = remember(uiState.selectedItem) {
        listOf(uiState.selectedItem) + tabItems.filter { it != uiState.selectedItem }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Event header
        item {
            EventHeader(event = event)
        }

        item {
            Spacer(modifier = Modifier.height(5.dp))
        }

        // Event info box
        item {
            EventInfoBox(
                navController = navController,
                event = event,
                uiState = uiState,
                onUiStateChange = onUiStateChange
            )
        }

        // Tab selection
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SelectionBar(
                items = tabItems,
                selectedItem = uiState.selectedItem
            ) { selectedItem ->
                onUiStateChange(uiState.copy(selectedItem = selectedItem))
            }
            Spacer(modifier = Modifier.height(33.dp))
        }

        // Tab content
        items(reorderedItems) { item ->
            TabContentItem(
                item = item,
                event = event,
                isSelected = item == uiState.selectedItem
            )
            Spacer(modifier = Modifier.height(30.dp))
        }

        // Footer
        item {
            Footer(navController = navController)
        }
    }
}

@Composable
private fun EventHeader(event: EventResponse) {
    Card(
        modifier = Modifier
            .width(EventDetailConstants.CARD_WIDTH)
            .height(EventDetailConstants.CARD_HEIGHT)
            .padding(top = 34.dp, start = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Event image
            EventCard(
                title = event.name.orEmpty(),
                location = event.location.orEmpty(),
                imageUrl = event.photographs?.firstOrNull()
            )

            // Event details
            EventDetailsColumn(event = event)
        }
    }
}

@Composable
private fun EventDetailsColumn(event: EventResponse) {
    Column {
        // Event name
        Text(
            text = event.name.orEmpty(),
            style = TextStyle(
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Event details rows
        event.category?.let { category ->
            EventDetailRow(R.drawable.college, category)
            Spacer(modifier = Modifier.height(10.dp))
        }

        event.location?.let { location ->
            EventDetailRow(R.drawable.ic_location, location)
            Spacer(modifier = Modifier.height(10.dp))
        }

        EventDetailRow(R.drawable.event_date, formatEventDate(event.startDate))
        Spacer(modifier = Modifier.height(10.dp))

        event.organizerName?.takeIf { it.isNotEmpty() }?.let { organizer ->
            EventDetailRow(R.drawable.trophy, organizer)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun TabContentItem(
    item: String,
    event: EventResponse,
    isSelected: Boolean
) {
    val highlightColor = Color(0xFF8A44CB)
    val normalColor = Color(0xFF171717)

    var showHighlight by remember { mutableStateOf(isSelected) }

    val backgroundColor by animateColorAsState(
        targetValue = if (showHighlight) highlightColor else normalColor,
        animationSpec = tween(durationMillis = EventDetailConstants.ANIMATION_DURATION),
        label = "highlight"
    )

    LaunchedEffect(isSelected) {
        if (isSelected) {
            showHighlight = true
            kotlinx.coroutines.delay(EventDetailConstants.HIGHLIGHT_DURATION)
            showHighlight = false
        }
    }

    InfoCard(
        title = item,
        content = getTabContent(item, event),
        backgroundColor = backgroundColor
    )
}

private fun getTabContent(item: String, event: EventResponse): String {
    return when (item) {
        "Details" -> event.eventDescription?.takeIf { it.isNotEmpty() }
            ?: "No details available."

        "Dates & Deadlines" -> buildString {
            append("Start Date: ${formatEventDate(event.startDate)}\n")
            append("Start Time: ${event.startTime?.takeIf { it.isNotEmpty() } ?: "Not specified"}\n")
            append("Duration: ${event.duration?.takeIf { it.isNotEmpty() } ?: "Not specified"}\n")
            append("Registration Ends: ${event.endRegistrationDate?.takeIf { it.isNotEmpty() } ?: "Not specified"}")
        }

        "Prizes" -> event.prizes?.takeIf { it.isNotEmpty() }
            ?: "No prizes available."

        "Link to Community" -> "No information available"

        else -> "No information available"
    }
}

@Composable
private fun SelectionBar(
    items: List<String>,
    selectedItem: String,
    onSelect: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFF2E2E2E), shape = RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
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
private fun SelectionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color(0xFF8A44CB) else Color(0xFF444444),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun InfoCard(
    title: String,
    content: String,
    backgroundColor: Color
) {
    Column(
        modifier = Modifier
            .width(EventDetailConstants.CONTENT_CARD_WIDTH)
            .wrapContentHeight()
            .padding(start = 32.dp)
            .background(Color(0xFF171717), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VerticalIndicator()
            Spacer(modifier = Modifier.width    (8.dp))

            Text(
                text = title,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
private fun EventDetailRow(icon: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(EventDetailConstants.SMALL_ICON_SIZE)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                color = Color.White
            ),
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EventCard(
    title: String,
    location: String,
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .width(EventDetailConstants.EVENT_CARD_WIDTH)
            .height(EventDetailConstants.CARD_HEIGHT)
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Event image for $title",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = R.drawable.ic_location),
                error = painterResource(id = R.drawable.ic_location)
            )
        } else {
            // Fallback image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF404040).copy(alpha = 0.6f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = "Default event image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.5f
                )
            }
        }
    }
}

@Composable
private fun VerticalIndicator() {
    Box(
        modifier = Modifier
            .width(5.dp)
            .height(39.dp)
            .background(
                color = Color(0xFF8A44CB),
                shape = RoundedCornerShape(50.dp)
            )
    )
}

@Composable
private fun EventInfoBox(
    navController: NavController,
    event: EventResponse,
    uiState: EventDetailUiState,
    onUiStateChange: (EventDetailUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .width(EventDetailConstants.INFO_CARD_WIDTH)
            .height(EventDetailConstants.INFO_CARD_HEIGHT)
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
                // Cost and actions row
                CostAndActionsRow(
                    event = event,
                    context = context,
                    uiState = uiState,
                    onUiStateChange = onUiStateChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Register button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    RegisterButton(navController = navController, event = event)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                Image(
                    painter = painterResource(id = R.drawable.divider_line),
                    contentDescription = "Divider",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(320.dp)
                        .height(2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Event tags
                EventTags(event = event)
            }
        }
    }
}

@Composable
private fun CostAndActionsRow(
    event: EventResponse,
    context: Context,
    uiState: EventDetailUiState,
    onUiStateChange: (EventDetailUiState) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cost information
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
                text = formatEventPrice(event.ticketPrice),
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8A44CB)
                )
            )
        }

        // Action buttons
        ActionButtons(
            event = event,
            context = context,
            uiState = uiState,
            onUiStateChange = onUiStateChange
        )
    }
}

@Composable
private fun ActionButtons(
    event: EventResponse,
    context: Context,
    uiState: EventDetailUiState,
    onUiStateChange: (EventDetailUiState) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Like button
        ActionButton(
            iconRes = R.drawable.ic_like,
            text = "${uiState.likeCount} likes",
            contentDescription = "Like event",
            tint = if (uiState.isLiked) Color.Red else Color.White,
            onClick = {
                onUiStateChange(
                    uiState.copy(
                        isLiked = !uiState.isLiked,
                        likeCount = if (uiState.isLiked) uiState.likeCount - 1 else uiState.likeCount + 1
                    )
                )
            }
        )

        // Comment button
        ActionButton(
            iconRes = R.drawable.ic_comment,
            contentDescription = "Comment on event",
            onClick = { /* TODO: Implement comment functionality */ }
        )

        // Share button
        ActionButton(
            iconRes = R.drawable.ic_share,
            contentDescription = "Share event",
            onClick = { shareEvent(context, event) }
        )
    }
}

@Composable
private fun ActionButton(
    iconRes: Int,
    contentDescription: String,
    text: String? = null,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(EventDetailConstants.DEFAULT_ICON_SIZE),
            colorFilter = ColorFilter.tint(tint)
        )
        text?.let {
            Text(
                text = it,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EventTags(event: EventResponse) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // First row - Category and Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            event.category?.let { category ->
                EventTag(category)
                Spacer(modifier = Modifier.width(12.dp))
            }
            event.mode?.let { mode ->
                EventTag(mode)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Second row - Price and Team status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EventTag(
                text = if (isEventFree(event.ticketPrice)) "Free Event" else "Paid Event",
                isBigger = true
            )
            Spacer(modifier = Modifier.width(12.dp))
            EventTag(if (event.isTeamEvent) "Team Event" else "Solo Event")
        }
    }
}

@Composable
private fun EventTag(
    text: String,
    isBigger: Boolean = false
) {
    val width = if (isBigger) 149.dp else 104.dp

    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(0xFFCCA1F4),
                shape = RoundedCornerShape(27.dp)
            )
            .width(width)
            .height(25.dp)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCCA1F4)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RegisterButton(
    navController: NavController,
    event: EventResponse
) {
    Box(
        modifier = Modifier
            .width(EventDetailConstants.REGISTER_BUTTON_WIDTH)
            .height(EventDetailConstants.REGISTER_BUTTON_HEIGHT)
            .background(Color(0xFF8A44CB), shape = RoundedCornerShape(8.dp))
            .clickable {
                // Safe navigation with null checks
                val eventId = event._id?.takeIf { it.isNotEmpty() } ?: return@clickable
                val eventName = event.name?.takeIf { it.isNotEmpty() } ?: "Unknown Event"
                val ticketPrice = event.ticketPrice ?: 0

                try {
                    navController.navigate("payment/$eventId/$eventName/$ticketPrice")
                } catch (e: Exception) {
                    Log.e("RegisterButton", "Navigation failed", e)
                }
            }
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

// Utility functions
private fun formatEventDate(date: String?): String {
    if (date.isNullOrEmpty()) return "Not specified"

    return try {
        val dateOnly = if (date.contains("T")) {
            date.split("T")[0]
        } else {
            date
        }

        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val parsedDate = parser.parse(dateOnly)

        parsedDate?.let { formatter.format(it) } ?: "Not specified"
    } catch (e: Exception) {
        Log.e("EventDetailScreen", "Date parsing failed for: $date", e)
        "Not specified"
    }
}

private fun formatEventPrice(price: Any?): String {
    return when {
        price == null -> "Free"
        price.toString().isEmpty() -> "Free"
        price.toString() == "0" -> "Free"
        price.toString() == "0.0" -> "Free"
        else -> {
            try {
                val numericPrice = price.toString().toDoubleOrNull()
                if (numericPrice != null && numericPrice > 0) {
                    "₹${numericPrice.toInt()}"
                } else {
                    "Free"
                }
            } catch (e: Exception) {
                "Free"
            }
        }
    }
}

private fun isEventFree(price: Any?): Boolean {
    return when {
        price == null -> true
        price.toString().isEmpty() -> true
        price.toString() == "0" -> true
        price.toString() == "0.0" -> true
        else -> {
            try {
                val numericPrice = price.toString().toDoubleOrNull()
                numericPrice == null || numericPrice <= 0
            } catch (e: Exception) {
                true
            }
        }
    }
}

private fun shareEvent(context: Context, event: EventResponse) {
    try {
        val shareText = buildString {
            append("Check out this event: ${event.name ?: "Unknown Event"}\n")
            event.location?.let { append("Location: $it\n") }
            append("Date: ${formatEventDate(event.startDate)}")
            event.startTime?.let { append(" at $it") }
            append("\nPrice: ${formatEventPrice(event.ticketPrice)}")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share Event")
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Log.e("EventDetailScreen", "Share failed", e)
    }
}