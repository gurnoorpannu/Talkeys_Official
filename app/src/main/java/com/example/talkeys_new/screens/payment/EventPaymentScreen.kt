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
import com.example.talkeys_new.utils.PhonePePaymentManager
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope

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
    var paymentStatus by remember { mutableStateOf<String?>(null) }
    
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
            
            // Payment Status Display
            paymentStatus?.let { status ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            status.contains("success", ignoreCase = true) -> Color.Green.copy(alpha = 0.2f)
                            status.contains("failed", ignoreCase = true) -> Color.Red.copy(alpha = 0.2f)
                            status.contains("pending", ignoreCase = true) -> Color.Yellow.copy(alpha = 0.2f)
                            else -> Color.Gray.copy(alpha = 0.2f)
                        }
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = status,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
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
                            text = "UPI • Cards • Net Banking • Wallets",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            // Payment Instructions
            Text(
                text = "• All UPI apps (GPay, Paytm, BHIM, etc.)\n• Credit/Debit Cards (Visa, Mastercard, RuPay)\n• Net Banking (All major banks)\n• Digital Wallets & BNPL options",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Pay Now Button
            Button(
                onClick = {
                    isLoading = true
                    paymentStatus = null
                    onPaymentInitiated()
                    
                    // Production: Prepare payment data
                    val passType = determinePassType(amount)
                    val friends = getUserSelectedFriends()
                    
                    // Get auth token using the same method as other APIs
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        try {
                            // Use the same TokenManager approach as AuthInterceptor
                            val tokenManager = com.example.talkeys_new.screens.authentication.TokenManager(context)
                            val tokenResult = tokenManager.getToken()
                            
                            val authToken = when (tokenResult) {
                                is com.example.talkeys_new.utils.Result.Success -> {
                                    val token = tokenResult.data?.takeIf { it.isNotEmpty() }
                                    if (token != null) {
                                        Log.d("PaymentAuth", "Token retrieved - length: ${token.length}")
                                        Log.d("PaymentAuth", "Token preview: ${token.take(20)}...")
                                    } else {
                                        Log.e("PaymentAuth", "Token is null or empty!")
                                    }
                                    token
                                }
                                is com.example.talkeys_new.utils.Result.Error -> {
                                    Log.e("PaymentAuth", "Authentication failed: ${tokenResult.message}")
                                    null
                                }
                                is com.example.talkeys_new.utils.Result.Loading -> {
                                    Log.w("PaymentAuth", "Token still loading")
                                    null
                                }
                            }
                            
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                (context as? MainActivity)?.initiateIntegratedPayment(
                                    eventId = eventId,
                                    passType = passType,
                                    friends = friends,
                                    authToken = authToken
                                ) { result ->
                                    isLoading = false
                                    when (result) {
                                        is PhonePePaymentManager.PaymentResult.Success -> {
                                            paymentStatus = "Payment successful! Pass ID: ${result.passId}"
                                        }
                                        is PhonePePaymentManager.PaymentResult.Failed -> {
                                            paymentStatus = "Payment failed: ${result.message}"
                                        }
                                        is PhonePePaymentManager.PaymentResult.Pending -> {
                                            paymentStatus = "Payment pending: ${result.message}"
                                        }
                                        is PhonePePaymentManager.PaymentResult.Cancelled -> {
                                            paymentStatus = "Payment cancelled: ${result.message}"
                                        }
                                        is PhonePePaymentManager.PaymentResult.Error -> {
                                            paymentStatus = "Error: ${result.message}"
                                        }
                                        is PhonePePaymentManager.PaymentResult.Completed -> {
                                            paymentStatus = "${result.message}"
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("PaymentAuth", "Authentication error: ${e.message}")
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                isLoading = false
                                paymentStatus = "Authentication error occurred"
                            }
                        }
                    }
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

/**
 * PRODUCTION HELPER FUNCTIONS
 */

/**
 * Determine pass type based on amount or user selection
 * Backend accepts: "VIP", "General", "Staff" (default: "General")
 */
private fun determinePassType(amount: Double): String {
    return when {
        amount >= 500 -> "VIP"      // Higher amounts get VIP
        amount > 0 -> "General"     // Regular paid events get General
        else -> "General"           // Default to General
    }
}

/**
 * Get user selected friends for the event
 * For now, returns empty list - user can register alone
 */
private fun getUserSelectedFriends(): List<com.talkeys.shared.data.payment.Friend> {
    // PRODUCTION: Return empty list for solo registration
    // You can add friend selection UI later if needed
    return emptyList()
    
    // FUTURE: Implement friend selection UI
    // return friendSelectionViewModel.getSelectedFriends().map { friend ->
    //     com.talkeys.shared.data.payment.Friend(
    //         name = friend.name,
    //         email = friend.email
    //     )
    // }
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