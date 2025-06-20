package com.example.talkeys_new.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.talkeys_new.R
import com.example.talkeys_new.screens.authentication.AuthService.RetrofitClient
import com.example.talkeys_new.screens.authentication.GoogleAuthClient
import com.example.talkeys_new.screens.authentication.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
@Composable
fun LandingPage(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }

    var isCheckingToken by remember { mutableStateOf(true) }

    // ✅ Token auto-login logic with flicker prevention
    LaunchedEffect(Unit) {
        val token = tokenManager.token.first()
        if (!token.isNullOrEmpty()) {
            navController.navigate("home") {
                popUpTo("landing") { inclusive = true }
                launchSingleTop = true
            }
        } else {
            isCheckingToken = false
        }
    }

    if (isCheckingToken) {
        // Optional: loading spinner or just blank while checking token
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val googleAuthClient = remember {
        GoogleAuthClient(
            context = context,
            clientId = "563385258779-75kq583ov98fk7h3dqp5em0639769a61.apps.googleusercontent.com"
        )
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val token = googleAuthClient.getIdTokenFromIntent(result.data)

        if (token != null) {
            coroutineScope.launch {
                try {
                    val response = RetrofitClient.instance.verifyToken("Bearer $token")
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.accessToken?.let {
                            tokenManager.saveToken(it)
                        }
                        Toast.makeText(context, "Welcome ${body?.name}", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("landing") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        Toast.makeText(context, "Login failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Google sign-in failed", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = "Splash Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Unlimited\nentertainment,\nall in one place",
                color = Color(0xFFFFFCFC),
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your Stage, Your Voice\nEvents Reimagined",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    launcher.launch(googleAuthClient.getSignInIntent())
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB768FF)
                )
            ) {
                Text(
                    text = "LOGIN WITH GOOGLE",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}