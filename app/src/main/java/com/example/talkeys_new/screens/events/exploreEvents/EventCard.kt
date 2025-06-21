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
            .width(280.dp)
            .height(450.dp) // Increased height to accommodate all content
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
                // Image Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
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
                        .padding(16.dp)
                ) {
                    // Title Section with fixed height
                    Box(
                        modifier = Modifier.height(75.dp) // Fixed height for title area
                    ) {
                        Text(
                            text = event.name,
                            style = TextStyle(
                                fontSize = 29.47.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight(600),
                                color = Color(0xFFFCFCFC),
                            ),
                            color = Color.White,
                            maxLines = 3, // Allow up to 3 lines
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                   // Spacer(modifier = Modifier.height(12.dp))

                    // Location Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.location ?: "Location not available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                   Spacer(modifier = Modifier.height(8.dp))

                    // Date Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${formatDate(event.startDate)} | ${event.startTime}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom Tags Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Free/Price Tag
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFDCB6FF),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (event.ticketPrice == null || event.ticketPrice == 0.0) "Free" else "₹${event.ticketPrice}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp
                            )
                        }

                        // Category Tag
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFDCB6FF),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = event.category ?: "Event",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp
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