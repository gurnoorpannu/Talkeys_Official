package com.example.talkeys_new.screens.common


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.talkeys_new.R
import com.example.talkeys_new.screens.authentication.GoogleSignInManager
import com.example.talkeys_new.screens.authentication.UserProfile
import com.example.talkeys_new.avatar.SimpleUserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavController) {
    val context = LocalContext.current
    val googleSignInManager = remember { GoogleSignInManager(context) }
    var userProfile by remember { mutableStateOf(UserProfile()) }
    
    // Load user profile
    LaunchedEffect(Unit) {
        userProfile = googleSignInManager.getUserProfile()
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

            // Custom Avatar Profile Image
            SimpleUserAvatar(
                userName = userProfile.name,
                size = 44.dp,
                modifier = Modifier.clickable { navController.navigate("profile") }
            )
        },
        modifier = Modifier.fillMaxWidth()
    )

}

