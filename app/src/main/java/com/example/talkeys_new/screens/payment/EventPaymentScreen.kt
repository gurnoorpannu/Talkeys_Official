package com.example.talkeys_new.screens.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.talkeys_new.MainActivity
import com.example.talkeys_new.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventPaymentScreen(
    eventId: String,
    eventName: String,
    eventPrice: String,
    navController: NavController
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Convert price to double for calculations
    val priceAmount = eventPrice.toDoubleOrNull() ?: 0.0
    val isEventFree = priceAmount <= 0
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Registration") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8A44CB),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Event Details Card
                EventDetailsCard(
                    eventName = eventName,
                    eventPrice = priceAmount,
                    isEventFree = isEventFree
                )
                
                if (isEventFree) {
                    // Free Event Registration
                    FreeEventRegistration(
                        eventName = eventName,
                        onRegister = {
                            // Handle free event registration
                            // You can call your backend API here
                            navController.navigate("registration_success")
                        }
                    )
                } else {
                    // Paid Event - PhonePe Payment
                    PhonePePaymentSection(
                        eventId = eventId,
                        eventName = eventName,
                        amount = priceAmount,
                        onPaymentInitiated = {
                            // This will be called when payment is initiated
                            // The actual payment result will be handled in MainActivity
                        }
                    )
                }
                
                // Payment Security Info
                PaymentSecurityInfo()
            }
        }
    }
}

@Composable
private fun EventDetailsCard(
    eventName: String,
    eventPrice: Double,
    isEventFree: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171717)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Event Registration",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Divider(color = Color(0xFF333333))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Event:",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = eventName,
                    color = Color(0xFF8A44CB),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Registration Fee:",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = if (isEventFree) "FREE" else "₹${eventPrice.toInt()}",
                    color = if (isEventFree) Color.Green else Color(0xFF8A44CB),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FreeEventRegistration(
    eventName: String,
    onRegister: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171717)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🎉 Free Event Registration",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "This is a free event! Click below to complete your registration.",
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            
            Button(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Register for Free",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PhonePePaymentSection(
    eventId: String,
    eventName: String,
    amount: Double,
    onPaymentInitiated: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171717)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Payment Method",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // PhonePe Payment Option
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF5F259F)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // PhonePe logo placeholder
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pe",
                            color = Color(0xFF5F259F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PhonePe",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "UPI, Cards, Wallets & More",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            // Payment Instructions
            Text(
                text = "• Secure payment powered by PhonePe\n• Supports UPI, Cards, and Wallets\n• Instant confirmation",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Pay Now Button
            Button(
                onClick = {
                    isLoading = true
                    onPaymentInitiated()
                    
                    // TODO: Replace with actual backend API call to create order
                    // For now, using dummy values for testing
                    val dummyToken = "DUMMY_TOKEN_${System.currentTimeMillis()}"
                    val dummyOrderId = "ORDER_${eventId}_${System.currentTimeMillis()}"
                    
                    // Important: In production, you need to:
                    // 1. Call your backend API to create an order
                    // 2. Get the actual token and orderId from the response
                    // 3. Then call PhonePe payment with real values
                    
                    // Call PhonePe payment
                    (context as? MainActivity)?.initiatePhonePePayment(dummyToken, dummyOrderId)
                    
                    isLoading = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A44CB)),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Pay ₹${amount.toInt()} with PhonePe",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentSecurityInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171717).copy(alpha = 0.8f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🔒 Secure Payment",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            Text(
                text = "Your payment is secured with industry-standard encryption. We don't store your payment information.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            
            Text(
                text = "Note: After payment completion, please check the Order Status for final confirmation.",
                color = Color.Yellow.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}