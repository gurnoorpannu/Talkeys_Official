package com.example.talkeys_new.screens.common

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.example.talkeys_new.R
import com.example.talkeys_new.screens.authentication.GoogleAuthClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavController) {
    var showProfileDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Initialize GoogleAuthClient
    val googleAuthClient = remember {
        GoogleAuthClient(
            context = context,
            clientId = "563385258779-75kq583ov98fk7h3dqp5em0639769a61.apps.googleusercontent.com"
        )
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(75.dp)
                    .fillMaxHeight()
                    .clickable {
                        navController.navigate("home")
                    })
        },
        title = { },
        actions = {
            IconButton(onClick = { /* Handle chat click */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chat),
                    contentDescription = "Chat",
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.profile_image),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable { showProfileDialog = true }
            )
        },
        modifier = Modifier.fillMaxWidth()
    )

    if (showProfileDialog) {
        Box(modifier = Modifier.fillMaxSize()) {
            Popup(
                onDismissRequest = { showProfileDialog = false },
                alignment = Alignment.TopEnd,
                offset = IntOffset(x = -8, y = 0)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 64.dp)
                        .wrapContentSize()
                ) {
                    Column(
                        modifier = Modifier
                            .shadow(
                                elevation = 12.dp,
                                spotColor = Color(0x40000000),
                                ambientColor = Color(0x40000000)
                            )
                            .width(191.dp)
                            .background(
                                color = Color(0xFF171717),
                                shape = RoundedCornerShape(size = 12.dp)
                            )
                            .padding(vertical = 8.dp)
                    ) {
                        // Profile Option
                        MenuOption(
                            icon = R.drawable.dialogbox_profile_icon,
                            text = "Profile",
                            onClick = { /* Handle Profile click */ }
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Notifications Option
                        MenuOption(
                            icon = R.drawable.dialogbox_notification_icon,
                            text = "Notifications",
                            onClick = { /* Handle Notifications click */ }
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Host Event Option
                        MenuOption(
                            icon = R.drawable.dropdown_hostevent_icon,
                            text = "Host Event",
                            onClick = { /* Handle Host Event click */ }
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Settings Option
                        MenuOption(
                            icon = R.drawable.dropdown_settings_icon,
                            text = "Settings",
                            onClick = { /* Handle Settings click */ }
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Logout Option with proper implementation
                        MenuOption(
                            icon = R.drawable.dropdown_logout_icon,
                            text = "Logout",
                            onClick = {
                                scope.launch {
                                    try {
                                        // Sign out from Google
                                        googleAuthClient.signOut().addOnCompleteListener {
                                            // Clear any stored user data (SharedPreferences, etc.)
                                            clearUserSession(context)

                                            // Close the dialog
                                            showProfileDialog = false

                                            // Navigate to landing page and clear backstack
                                            navController.navigate("landingpage") {
                                                popUpTo(0) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        // Even if Google sign out fails, still navigate to landing page
                                        clearUserSession(context)
                                        showProfileDialog = false
                                        navController.navigate("landingpage") {
                                            popUpTo(0) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// Helper function to clear user session data
private fun clearUserSession(context: android.content.Context) {
    // Clear SharedPreferences if you're storing user data there
    val sharedPreferences = context.getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
    sharedPreferences.edit().clear().apply()

    // Clear any other user-related data you might have stored
    // For example, if you're using Room database, clear user tables
    // If you're using any other storage mechanism, clear it here
}

@Composable
fun MenuOption(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Start,
            )
        )
    }
}