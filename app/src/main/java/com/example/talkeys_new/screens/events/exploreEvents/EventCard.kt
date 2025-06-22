package com.example.talkeys_new.screens.events.exploreEvents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.talkeys_new.dataModels.EventResponse
import com.example.talkeys_new.R

@Composable
fun EventCard(
    event: EventResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(380.dp) // Increased from 320.dp to 380.dp
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = Color(0xFFDCB6FF),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = Color(0xFF262626).copy(alpha = 0.7f),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Image Section - Made more square
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Increased from 160.dp to 200.dp to make more square
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(event.photographs?.firstOrNull())
                            .crossfade(true)
                            .build(),
                        contentDescription = event.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Content Section with fixed layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp) // Reduced padding
                ) {
                    // Title Section with proper height for multi-line text
                    Box(
                        modifier = Modifier.height(48.dp), // Reduced height
                        contentAlignment = Alignment.TopStart
                    ) {
                        Text(
                            text = event.name,
                            style = TextStyle(
                                fontSize = 18.sp, // Reduced font size
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight(600),
                                color = Color(0xFFFCFCFC),
                                lineHeight = 20.sp // Adjusted line height
                            ),
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Location Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp) // Reduced icon size
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = event.location ?: "Location not available",
                            style = MaterialTheme.typography.bodySmall, // Smaller text
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Date Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp) // Reduced icon size
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${formatDate(event.startDate)} | ${event.startTime}",
                            style = MaterialTheme.typography.bodySmall, // Smaller text
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom Tags Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Free/Price Tag
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFDCB6FF),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp) // Reduced padding
                        ) {
                            Text(
                                text = if (event.ticketPrice == null || event.ticketPrice == 0.0) "Free" else "₹${event.ticketPrice}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp // Reduced font size
                            )
                        }

                        // Category Tag
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFDCB6FF),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp) // Reduced padding
                        ) {
                            Text(
                                text = event.category ?: "Event",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp // Reduced font size
                            )
                        }
                    }
                }
            }
        }
    }
}
// Helper function to format date
private fun formatDate(dateString: String): String {
    return try {
        // Assuming the date comes in format "YYYY-MM-DD", convert to "Month DD, YYYY"
        val parts = dateString.split("-")
        if (parts.size >= 3) {
            val year = parts[0]
            val month = when (parts[1]) {
                "01" -> "January"
                "02" -> "February"
                "03" -> "March"
                "04" -> "April"
                "05" -> "May"
                "06" -> "June"
                "07" -> "July"
                "08" -> "August"
                "09" -> "September"
                "10" -> "October"
                "11" -> "November"
                "12" -> "December"
                else -> "Month"
            }
            val day = parts[2].toIntOrNull()?.toString() ?: parts[2]
            "$month $day, $year"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}