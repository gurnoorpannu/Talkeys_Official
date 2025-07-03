package com.example.talkeys_new.screens.events.exploreEvents

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
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
import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState

@Composable
fun EventCard(
    event: EventResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCenter: Boolean = false,
    isFocused: Boolean = true
) {
    // Calculate dimensions based on focus state
    val cardWidth = if (isFocused) 165.dp else 130.dp
    val cardHeight = if (isFocused) 286.dp else 247.dp
    val imageHeight =
        if (isFocused) 165.dp else 130.dp // Use same width as height for square aspect ratio

    // 1. TAP ANIMATION using interaction source
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tap_scale"
    )

    // 2. 3D ROTATION (only for center card)
    val infiniteTransition = rememberInfiniteTransition(label = "rotation_transition")
    val animatedRotationY by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation_y"
    )

    // 3. ENTRANCE ANIMATION
    var isVisible by remember { mutableStateOf(false) }
    val slideOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 100f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "entrance_slide"
    )

    val entranceAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "entrance_alpha"
    )

    // Trigger entrance animation
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .graphicsLayer {
                // Tap animation
                scaleX = animatedScale
                scaleY = animatedScale

                // 3D rotation for center card
                if (isCenter) {
                    rotationY = animatedRotationY
                }

                // Entrance animation
                translationY = slideOffset
                alpha = entranceAlpha
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    Log.d("EventCard", "Card clicked for event: ${event.name}")
                    onClick()
                    Log.d("EventCard", "onClick called")
                }
            ),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = Color(0xFF703CA0),
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(2.dp)
                .background(
                    color = Color(0xFF262626),
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Image Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .background(
                            color = Color(0xFFA7A7A7),
                            shape = RoundedCornerShape(
                                topStart = 8.18715.dp,
                                topEnd = 8.18715.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .clip(
                            RoundedCornerShape(
                                topStart = 8.18715.dp,
                                topEnd = 8.18715.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
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

                // Content Section
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Title Section
                    Text(
                        text = event.name,
                        style = TextStyle(
                            fontSize = if (isFocused) 14.sp else 12.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            fontWeight = FontWeight(600),
                            color = Color(0xFFFCFCFC),
                            lineHeight = if (isFocused) 16.sp else 14.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

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
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.location ?: "Location not available",
                            style = TextStyle(
                                fontSize = if (isFocused) 10.sp else 9.sp,
                                color = Color.White
                            ),
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
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${formatDate(event.startDate)} | ${event.startTime}",
                            style = TextStyle(
                                fontSize = if (isFocused) 10.sp else 9.sp,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Bottom Tags Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Free/Price Tag
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF703CA0).copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFDCB6FF),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .width(63.dp)
                                .height(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (event.ticketPrice == null || event.ticketPrice == 0.0) "Free" else "₹${event.ticketPrice}",
                                color = Color.White,
                                fontSize = if (isFocused) 9.sp else 8.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Category Tag
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF703CA0).copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFDCB6FF),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .width(63.dp)
                                .height(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = event.category ?: "Event",
                                color = Color.White,
                                fontSize = if (isFocused) 9.sp else 8.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// SHIVERING LOADING EFFECT
@Composable
fun ShimmerEventCard(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_transition")

    // Shivering effect
    val shiver by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiver"
    )

    // Shimmer effect
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // Pulsing effect
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = modifier
            .height(281.87.dp)
            .graphicsLayer {
                translationX = shiver
                scaleX = pulse
                scaleY = pulse
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = Color(0xFFDCB6FF).copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = Color(0xFF262626).copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                )
                .drawWithCache {
                    val brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        start = Offset(shimmerTranslateAnim - 200f, 0f),
                        end = Offset(shimmerTranslateAnim, size.height)
                    )
                    onDrawBehind {
                        drawRoundRect(
                            brush = brush,
                            size = size,
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    }
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Shimmer image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(165.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color(0xFF404040).copy(alpha = 0.6f))
                )

                // Shimmer content placeholders
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Title placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(20.dp)
                            .background(
                                Color(0xFF404040).copy(alpha = 0.6f),
                                RoundedCornerShape(4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Location placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(14.dp)
                            .background(
                                Color(0xFF404040).copy(alpha = 0.6f),
                                RoundedCornerShape(4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Date placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(14.dp)
                            .background(
                                Color(0xFF404040).copy(alpha = 0.6f),
                                RoundedCornerShape(4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tags placeholder
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(24.dp)
                                .background(
                                    Color(0xFF404040).copy(alpha = 0.6f),
                                    RoundedCornerShape(12.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .height(24.dp)
                                .background(
                                    Color(0xFF404040).copy(alpha = 0.6f),
                                    RoundedCornerShape(12.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

// Helper function to format date (unchanged)
private fun formatDate(dateString: String): String {
    return try {
        // Handle ISO date format (2025-02-12T18:00:00.000Z)
        val datePart = if (dateString.contains("T")) {
            dateString.split("T")[0] // Extract just the date part before 'T'
        } else {
            dateString
        }

        val parts = datePart.split("-")
        if (parts.size >= 3) {
            val year = parts[0]
            val month = when (parts[1]) {
                "01" -> "Jan"
                "02" -> "Feb"
                "03" -> "Mar"
                "04" -> "Apr"
                "05" -> "May"
                "06" -> "Jun"
                "07" -> "Jul"
                "08" -> "Aug"
                "09" -> "Sep"
                "10" -> "Oct"
                "11" -> "Nov"
                "12" -> "Dec"
                else -> "Month"
            }
            // Extract day and remove leading zeros
            val day = parts[2].toIntOrNull()?.toString() ?: parts[2]

            "$day $month $year"
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

// Alternative simpler version (if you prefer without ordinal suffixes)
private fun formatDateSimple(dateString: String): String {
    return try {
        val parts = dateString.split("-")
        if (parts.size >= 3) {
            val year = parts[0]
            val month = when (parts[1]) {
                "01" -> "Jan"
                "02" -> "Feb"
                "03" -> "Mar"
                "04" -> "Apr"
                "05" -> "May"
                "06" -> "Jun"
                "07" -> "Jul"
                "08" -> "Aug"
                "09" -> "Sep"
                "10" -> "Oct"
                "11" -> "Nov"
                "12" -> "Dec"
                else -> "Month"
            }
            // Simply convert to int to remove leading zero, then back to string
            val day = parts[2].toIntOrNull()?.toString() ?: parts[2]

            "$day $month $year"  // Changed order: day first, then month, then year
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}