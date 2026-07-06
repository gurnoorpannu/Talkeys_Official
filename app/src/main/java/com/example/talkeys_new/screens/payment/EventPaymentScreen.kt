package com.example.talkeys_new.screens.payment

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val ScreenBlack = Color(0xFF050507)
private val CardBlack = Color(0xFF111115)
private val Stroke = Color.White.copy(alpha = 0.09f)
private val Muted = Color.White.copy(alpha = 0.68f)
private val PrimaryPurple = Color(0xFF8D45D5)
private val BrightPurple = Color(0xFFC084FC)
private val PhonePePurple = Color(0xFF5F259F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventPaymentScreen(
    eventId: String,
    eventName: String,
    eventPrice: String,
    navController: NavController
) {
    val priceAmount = eventPrice.toDoubleOrNull() ?: 0.0
    val isEventFree = priceAmount <= 0

    Scaffold(
        containerColor = ScreenBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Register", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text("Complete your booking", color = Muted, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBlack)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ScreenBlack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1B1024),
                                ScreenBlack,
                                ScreenBlack
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OrderSummaryCard(
                    eventName = eventName,
                    eventPrice = priceAmount,
                    isEventFree = isEventFree
                )

                if (isEventFree) {
                    FreeEventRegistration(onRegister = {
                        navController.navigate("registration_success")
                    })
                } else {
                    PhonePePaymentSection(
                        eventId = eventId,
                        amount = priceAmount,
                        navController = navController
                    )
                }

                PaymentSecurityInfo()
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    eventName: String,
    eventPrice: Double,
    isEventFree: Boolean
) {
    SectionSurface {
        Text(
            text = "Order summary",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Event", color = Muted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = eventName.ifBlank { "Selected event" },
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text("Total", color = Muted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEventFree) "Free" else "₹${eventPrice.toInt()}",
                    color = if (isEventFree) Color(0xFF51D88A) else BrightPurple,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FreeEventRegistration(onRegister: () -> Unit) {
    SectionSurface {
        Text(
            text = "Free registration",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No payment is needed for this event. Confirm once and your registration will be completed.",
            color = Muted,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        CheckoutButton(
            text = "Confirm Registration",
            isLoading = false,
            onClick = onRegister
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PhonePePaymentSection(
    eventId: String,
    amount: Double,
    navController: NavController
) {
    var isLoading by remember { mutableStateOf(false) }
    val paymentViewModel = sharedPaymentCheckoutViewModel()
    val checkoutState by paymentViewModel.checkoutState.collectAsState()
    val errorMessage = checkoutState.errorMessage

    LaunchedEffect(checkoutState.checkoutData) {
        checkoutState.checkoutData?.let { checkout ->
            isLoading = false
            val encodedUrl = Uri.encode(checkout.paymentUrl)
            navController.navigate(
                "webview_payment/$encodedUrl/${checkout.merchantOrderId}/${checkout.passId}"
            )
            paymentViewModel.clearCheckout()
        }
    }

    LaunchedEffect(checkoutState.isLoading) {
        isLoading = checkoutState.isLoading
    }

    SectionSurface {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pay with",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Fast checkout",
                color = BrightPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .border(1.dp, BrightPurple.copy(alpha = 0.35f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let { error ->
            ErrorBanner(error)
            Spacer(modifier = Modifier.height(14.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.055f), RoundedCornerShape(10.dp))
                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Pe", color = PhonePePurple, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("PhonePe", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("UPI, cards, net banking, wallets", color = Muted, fontSize = 12.sp)
                }

                Text(
                    text = "Selected",
                    color = BrightPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.035f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentLine("Amount", "₹${amount.toInt()}")
                PaymentLine("Convenience fee", "Included")
                Divider(color = Stroke)
                PaymentLine("Total", "₹${amount.toInt()}", highlight = true)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Supported methods",
            color = Muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PaymentChip("UPI")
            PaymentChip("Cards")
            PaymentChip("Net banking")
            PaymentChip("Wallets")
        }

        Spacer(modifier = Modifier.height(18.dp))

        CheckoutButton(
            text = "Pay ₹${amount.toInt()}",
            isLoading = isLoading,
            onClick = {
                isLoading = true
                paymentViewModel.startCheckout(
                    eventId = eventId,
                    passType = determinePassType(amount),
                    friends = getUserSelectedFriends(),
                    teamCode = null,
                    clientPlatform = "android",
                    authToken = null
                )
            }
        )
    }
}

@Composable
private fun PaymentLine(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (highlight) Color.White else Muted,
            fontSize = if (highlight) 15.sp else 13.sp,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = value,
            color = if (highlight) Color.White else Color.White.copy(alpha = 0.82f),
            fontSize = if (highlight) 17.sp else 13.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun PaymentChip(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.86f),
        fontSize = 12.sp,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(50))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(50))
            .padding(horizontal = 11.dp, vertical = 7.dp)
    )
}

@Composable
private fun ErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3A161B), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFFF667A).copy(alpha = 0.28f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Text(
            text = message,
            color = Color(0xFFFFCCD3),
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun CheckoutButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryPurple,
            disabledContainerColor = PrimaryPurple.copy(alpha = 0.55f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun PaymentSecurityInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101014).copy(alpha = 0.86f)),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Stroke)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Secure checkout",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Text(
                text = "Your payment is processed through PhonePe. Talkeys does not store card, UPI, or net banking details.",
                color = Muted,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Text(
                text = "After payment, return to Talkeys for confirmation.",
                color = BrightPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SectionSurface(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBlack.copy(alpha = 0.92f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Stroke),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

private fun determinePassType(amount: Double): String = "General"

private fun getUserSelectedFriends(): List<com.talkeys.shared.data.payment.Friend> = emptyList()
