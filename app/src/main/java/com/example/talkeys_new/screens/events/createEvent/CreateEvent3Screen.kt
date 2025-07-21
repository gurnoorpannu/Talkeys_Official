package com.example.talkeys_new.screens.events.createEvent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.talkeys_new.R
import com.example.talkeys_new.screens.common.HomeTopBar

// Font family definition - using only urbanist_regular for all weights

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent3Screen(navController: NavController) {
    // State variables for form fields
    var eventDates by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var registrationDeadline by remember { mutableStateOf("") }
    var maxAttendees by remember { mutableStateOf("") }
    var platformUsed by remember { mutableStateOf("") }
    var willBeRecorded by remember { mutableStateOf("") }
    var recordingDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Recording options
    val recordingOptions = listOf("Yes", "No")

    // Check if all fields are filled
    val areAllFieldsFilled = eventDates.isNotBlank() &&
            startTime.isNotBlank() &&
            endTime.isNotBlank() &&
            registrationDeadline.isNotBlank() &&
            maxAttendees.isNotBlank() &&
            platformUsed.isNotBlank() &&
            willBeRecorded.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Black,        // Black at top left
                        Color(0xFF4A0E4E)   // Purple at bottom right
                    ),
                    center = androidx.compose.ui.geometry.Offset(0.2f, 0.2f),
                    radius = 1200f
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            HomeTopBar(navController = navController)

            // Main Content Card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.6f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header
                        Text(
                            text = "Create Your Event",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Normal, // Changed from Bold to Normal
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color(0xFFE91E63), // Pink color matching the image
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Step 3 of 6",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Section Title
                        Text(
                            text = "3. Event Timing & Logistics",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal, // Changed from SemiBold to Normal
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Event Date(s) Field
                        Text(
                            text = "📅 Event Date(s)",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = eventDates,
                            onValueChange = { eventDates = it },
                            placeholder = {
                                Text(
                                    "2023-12-25 or comma separated",
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE91E63),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily(Font(R.font.urbanist_regular))
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Start Time Field
                        Text(
                            text = "🕒 Start Time",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            placeholder = {
                                Text(
                                    "HH:MM AM/PM",
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE91E63),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily(Font(R.font.urbanist_regular))
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // End Time Field
                        Text(
                            text = "🕐 End Time",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            placeholder = {
                                Text(
                                    "HH:MM AM/PM",
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE91E63),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily(Font(R.font.urbanist_regular))
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Registration Deadline Field
                        Text(
                            text = "Registration Deadline",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = registrationDeadline,
                            onValueChange = { registrationDeadline = it },
                            placeholder = {
                                Text(
                                    "YYYY-MM-DD",
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE91E63),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily(Font(R.font.urbanist_regular))
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Max No. of Attendees Field
                        Text(
                            text = "Max No. of Attendees",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = maxAttendees,
                            onValueChange = { maxAttendees = it },
                            placeholder = {
                                Text(
                                    "100",
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE91E63),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily(Font(R.font.urbanist_regular))
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Online Details Section
                        Text(
                            text = "Online Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal, // Changed from SemiBold to Normal
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Platform Used Field
                        Text(
                            text = "Platform Used",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = platformUsed,
                            onValueChange = { platformUsed = it },
                            placeholder = {
                                Text(
                                    "e.g Zoom, Google Meet, Teams",
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFE91E63),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily(Font(R.font.urbanist_regular))
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Will this be recorded? Dropdown
                        Text(
                            text = "Will this be recorded?",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = recordingDropdownExpanded,
                            onExpandedChange = { recordingDropdownExpanded = !recordingDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = willBeRecorded.ifEmpty { "-- Select an option --" },
                                onValueChange = { },
                                readOnly = true,
                                placeholder = {
                                    Text(
                                        "-- Select an option --",
                                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Dropdown",
                                        tint = Color.White.copy(alpha = 0.7f)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE91E63),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular))
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = recordingDropdownExpanded,
                                onDismissRequest = { recordingDropdownExpanded = false },
                                modifier = Modifier.background(Color(0xFF2A2A2A))
                            ) {
                                recordingOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option,
                                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                                color = Color.White
                                            )
                                        },
                                        onClick = {
                                            willBeRecorded = option
                                            recordingDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Navigation Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Previous Button
                            OutlinedButton(
                                onClick = {
                                    navController.navigate("create_event_2")
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color.White.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(0.4f)
                                    .height(56.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Previous",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Previous",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    fontWeight = FontWeight.Normal // Changed from Medium to Normal
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Next Button
                            Button(
                                onClick = {
                                    if (areAllFieldsFilled) {
                                        navController.navigate("create_event_4")
                                    }
                                },
                                enabled = areAllFieldsFilled,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (areAllFieldsFilled)
                                        Color(0xFFE91E63) else Color.Gray.copy(alpha = 0.3f),
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
                                    disabledContentColor = Color.White.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(0.4f)
                                    .height(56.dp)
                            ) {
                                Text(
                                    "Next ›",
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    fontWeight = FontWeight.Normal // Changed from SemiBold to Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}