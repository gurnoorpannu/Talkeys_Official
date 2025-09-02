package com.example.talkeys_new.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.talkeys_new.R

/**
 * A full-screen error component that displays an error message and provides a retry button
 * @param message The error message to display
 * @param onRetry Callback when the retry button is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val urbanistFont = FontFamily(Font(R.font.urbanist_regular))
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.screen_not_found),
                contentDescription = "Error Illustration",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 24.dp)
            )
            
            Text(
                text = "Oops!",
                color = Color.White,
                fontFamily = urbanistFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = urbanistFont,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE0A96D)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            ) {
                Text(
                    text = "Try Again",
                    color = Color.Black,
                    fontFamily = urbanistFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * A smaller error component that can be embedded within other UI elements
 * @param message The error message to display
 * @param onRetry Callback when the retry button is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val urbanistFont = FontFamily(Font(R.font.urbanist_regular))
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF222222)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = Color.White,
                fontFamily = urbanistFont,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            TextButton(
                onClick = onRetry,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFE0A96D)
                )
            ) {
                Text(
                    text = "Retry",
                    fontFamily = urbanistFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * A loading indicator component
 * @param modifier Modifier for the component
 */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFE0A96D),
            modifier = Modifier.size(48.dp)
        )
    }
}

/**
 * A toast-like error message that appears at the bottom of the screen
 * @param message The error message to display
 * @param onDismiss Callback when the error is dismissed
 */
@Composable
fun ErrorToast(
    message: String,
    onDismiss: () -> Unit
) {
    val urbanistFont = FontFamily(Font(R.font.urbanist_regular))
    
    Snackbar(
        modifier = Modifier
            .padding(16.dp),
        containerColor = Color(0xFF442220),
        contentColor = Color.White,
        action = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Dismiss",
                    color = Color(0xFFE0A96D),
                    fontFamily = urbanistFont
                )
            }
        }
    ) {
        Text(
            text = message,
            fontFamily = urbanistFont,
            fontSize = 14.sp
        )
    }
}