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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent4Screen(navController: NavController) {
    // State variables for form fields
    var eventType by remember { mutableStateOf("") }
    var eventTypeDropdownExpanded by remember { mutableStateOf(false) }
    var discounts by remember { mutableStateOf("") }
    var discountsDropdownExpanded by remember { mutableStateOf(false) }
    var qrCheckIn by remember { mutableStateOf("") }
    var qrCheckInDropdownExpanded by remember { mutableStateOf(false) }
    var refundPolicy by remember { mutableStateOf("") }
    var refundPolicyDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Dropdown options
    val eventTypeOptions = listOf("Free", "Paid")
    val discountOptions = listOf("Yes", "No")
    val qrCheckInOptions = listOf("Yes", "No")
    val refundPolicyOptions = listOf(
        "No Refunds",
        "Full Refund (24 hours before)",
        "Partial Refund (48 hours before)",
        "Custom Policy"
    )

    // Check if all fields are filled
    val areAllFieldsFilled = eventType.isNotBlank() &&
            discounts.isNotBlank() &&
            qrCheckIn.isNotBlank() &&
            refundPolicy.isNotBlank()

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
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                            color = Color(0xFFE91E63), // Pink color matching the image
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Step 4 of 6",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Section Title
                        Text(
                            text = "4. Ticketing & Access",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Is the Event Free or Paid? Dropdown
                        Text(
                            text = "Is the Event Free or Paid?",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = eventTypeDropdownExpanded,
                            onExpandedChange = { eventTypeDropdownExpanded = !eventTypeDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = eventType.ifEmpty { "Free" },
                                onValueChange = { },
                                readOnly = true,
                                placeholder = {
                                    Text(
                                        "Free",
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
                                expanded = eventTypeDropdownExpanded,
                                onDismissRequest = { eventTypeDropdownExpanded = false },
                                modifier = Modifier.background(Color(0xFF2A2A2A))
                            ) {
                                eventTypeOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option,
                                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                                color = Color.White
                                            )
                                        },
                                        onClick = {
                                            eventType = option
                                            eventTypeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Any Discounts? Dropdown
                        Text(
                            text = "Any Discounts?",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = discountsDropdownExpanded,
                            onExpandedChange = { discountsDropdownExpanded = !discountsDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = discounts.ifEmpty { "-- Select an option --" },
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
                                expanded = discountsDropdownExpanded,
                                onDismissRequest = { discountsDropdownExpanded = false },
                                modifier = Modifier.background(Color(0xFF2A2A2A))
                            ) {
                                discountOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option,
                                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                                color = Color.White
                                            )
                                        },
                                        onClick = {
                                            discounts = option
                                            discountsDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Enable QR-based check-in? Dropdown
                        Text(
                            text = "Enable QR-based check-in?",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = qrCheckInDropdownExpanded,
                            onExpandedChange = { qrCheckInDropdownExpanded = !qrCheckInDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = qrCheckIn.ifEmpty { "-- Select an option --" },
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
                                expanded = qrCheckInDropdownExpanded,
                                onDismissRequest = { qrCheckInDropdownExpanded = false },
                                modifier = Modifier.background(Color(0xFF2A2A2A))
                            ) {
                                qrCheckInOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option,
                                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                                color = Color.White
                                            )
                                        },
                                        onClick = {
                                            qrCheckIn = option
                                            qrCheckInDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Refund Policy Dropdown
                        Text(
                            text = "Refund Policy",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = refundPolicyDropdownExpanded,
                            onExpandedChange = { refundPolicyDropdownExpanded = !refundPolicyDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = refundPolicy.ifEmpty { "-- Select an option --" },
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
                                expanded = refundPolicyDropdownExpanded,
                                onDismissRequest = { refundPolicyDropdownExpanded = false },
                                modifier = Modifier.background(Color(0xFF2A2A2A))
                            ) {
                                refundPolicyOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option,
                                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                                color = Color.White
                                            )
                                        },
                                        onClick = {
                                            refundPolicy = option
                                            refundPolicyDropdownExpanded = false
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
                                    navController.navigate("create_event_3")
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
                                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Next Button
                            Button(
                                onClick = {
                                    if (areAllFieldsFilled) {
                                        navController.navigate("create_event_5")
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
                                    fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                                    fontWeight = FontWeight.SemiBold
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