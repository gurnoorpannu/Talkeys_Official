package com.example.talkeys_new.screens.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.talkeys_new.screens.common.BottomBar
import com.example.talkeys_new.screens.common.HomeTopBar


@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar
        HomeTopBar(
            navController = navController,
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 56.dp, bottom = 80.dp), // Adjust these values based on your TopBar and BottomBar heights
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to Talkeys \n This screen is under development currently")
        }

        // Bottom Bar
        BottomBar(
            navController = navController,
            scrollState = ScrollState(0),
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}