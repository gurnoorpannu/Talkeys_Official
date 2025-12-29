package com.example.talkeys_new.screens.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import android.app.Activity
import com.example.talkeys_new.MainActivity
import com.example.talkeys_new.utils.PhonePePaymentManager
import com.example.talkeys_new.utils.PhonePeIntegrationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhonePePaymentScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var amount by remember { mutableStateOf("") }
    var orderId by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var paymentStatus by remember { mutableStateOf("Ready to pay") }
    var showDebugInfo by remember { mutableStateOf(false) }
    
    // Payment result launcher
    val paymentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        PhonePePaymentManager.handlePaymentResult(
            resultCode = result.resultCode,
            data = result.data
        ) { paymentResult ->
            isLoading = false
            when (paymentResult) {
                is PhonePePaymentManager.PaymentResult.Success -> {
                    paymentStatus = "✅ Payment Successful! Pass ID: ${paymentResult.passId}"
                }
                is PhonePePaymentManager.PaymentResult.Failed -> {
                    paymentStatus = "❌ Payment Failed: ${paymentResult.message}"
                }
                is PhonePePaymentManager.PaymentResult.Pending -> {
                    paymentStatus = "⏳ Payment Pending: ${paymentResult.message}"
                }
                is PhonePePaymentManager.PaymentResult.Error -> {
                    paymentStatus = "🚨 Error: ${paymentResult.message}"
                }
                else -> {
                    paymentStatus = "Unknown result: $paymentResult"
                }
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "PhonePe Payment",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Payment Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = orderId,
                    onValueChange = { orderId = it },
                    label = { Text("Order ID") },
                    placeholder = { Text("Enter order ID from backend") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Payment Token") },
                    placeholder = { Text("Enter token from backend") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Instructions:",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "1. Get Order ID and Token from your backend's Create Order API",
                    fontSize = 14.sp
                )
                Text(
                    text = "2. Enter the values above and click Pay Now",
                    fontSize = 14.sp
                )
                Text(
                    text = "3. After payment, check Order Status API for final result",
                    fontSize = 14.sp
                )
            }
        }
        
        // Payment Status Display
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    paymentStatus.contains("✅") -> MaterialTheme.colorScheme.primaryContainer
                    paymentStatus.contains("❌") -> MaterialTheme.colorScheme.errorContainer
                    paymentStatus.contains("⏳") -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Payment Status:",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = paymentStatus,
                    fontSize = 14.sp
                )
            }
        }
        
        // Payment Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Complete Payment Flow Button (Recommended)
            Button(
                onClick = {
                    activity?.let { act ->
                        isLoading = true
                        paymentStatus = "Starting complete payment flow..."
                        
                        PhonePeIntegrationHelper.initiateCompletePaymentFlow(
                            activity = act,
                            eventId = "EVENT_123", // Replace with actual event ID
                            passType = "standard",
                            friends = emptyList(),
                            launcher = paymentLauncher,
                            authToken = null, // Add your auth token here
                            onResult = { result ->
                                // Result will be handled by paymentLauncher
                            }
                        )
                    } ?: run {
                        paymentStatus = "Error: Activity not available"
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Complete Flow")
                }
            }
            
            // Legacy Token Payment Button
            Button(
                onClick = {
                    if (orderId.isNotBlank() && token.isNotBlank()) {
                        activity?.let { act ->
                            isLoading = true
                            paymentStatus = "Starting token-based payment..."
                            
                            PhonePePaymentManager.startCheckout(
                                activity = act,
                                token = token,
                                orderId = orderId,
                                activityResultLauncher = paymentLauncher
                            )
                        } ?: run {
                            paymentStatus = "Error: Activity not available"
                        }
                    }
                },
                enabled = orderId.isNotBlank() && token.isNotBlank() && !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text("Token Payment")
            }
        }
        
        // Debug and Testing Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Debug & Testing:",
                        fontWeight = FontWeight.Medium
                    )
                    Button(
                        onClick = { showDebugInfo = !showDebugInfo }
                    ) {
                        Text(if (showDebugInfo) "Hide" else "Show")
                    }
                }
                
                if (showDebugInfo) {
                    Text(
                        text = PhonePeIntegrationHelper.getSDKStatus(),
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            orderId = "TEST_ORDER_${System.currentTimeMillis()}"
                            token = "DOC" // Replace with actual token from backend
                            amount = "100.00"
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fill Sample")
                    }
                    
                    Button(
                        onClick = {
                            orderId = ""
                            token = ""
                            amount = ""
                            paymentStatus = "Ready to pay"
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }
                }
                
                // WebView Error Handler
                Button(
                    onClick = {
                        paymentStatus = "Checking WebView errors..."
                        PhonePeIntegrationHelper.handleWebViewErrors { result ->
                            paymentStatus = "WebView check result: $result"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Handle WebView Errors")
                }
                
                // Manual Verification
                Button(
                    onClick = {
                        if (orderId.isNotBlank()) {
                            paymentStatus = "Manual verification..."
                            PhonePeIntegrationHelper.verifyPaymentManually(
                                merchantOrderId = orderId,
                                authToken = null
                            ) { result ->
                                paymentStatus = "Manual verification: $result"
                            }
                        }
                    },
                    enabled = orderId.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Manual Verify Payment")
                }
            }
        }
    }
}