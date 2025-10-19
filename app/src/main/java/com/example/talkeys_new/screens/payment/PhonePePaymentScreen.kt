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
import com.example.talkeys_new.MainActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhonePePaymentScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var amount by remember { mutableStateOf("") }
    var orderId by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
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
        
        Button(
            onClick = {
                if (orderId.isNotBlank() && token.isNotBlank()) {
                    isLoading = true
                    // Call the payment method from MainActivity
                    (context as? MainActivity)?.initiatePhonePePayment(token, orderId)
                    isLoading = false
                }
            },
            enabled = orderId.isNotBlank() && token.isNotBlank() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Pay Now with PhonePe")
            }
        }
        
        // Sample values for testing
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "For Testing:",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Replace 'DOC' values with actual Order ID and Token from your backend",
                    fontSize = 14.sp
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            orderId = "TEST_ORDER_${System.currentTimeMillis()}"
                            token = "DOC" // Replace with actual token
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
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }
                }
            }
        }
    }
}