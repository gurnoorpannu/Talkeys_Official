package com.example.talkeys_new

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.talkeys_new.navigation.AppNavigation
import com.example.talkeys_new.screens.VideoSplashScreen
import com.example.talkeys_new.screens.authentication.TokenManager
import com.example.talkeys_new.ui.theme.Talkeys_NewTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            Talkeys_NewTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = "video_splash"
                ) {
                    composable("video_splash") {
                        VideoSplashScreen { destination ->
                            navController.navigate(destination) {
                                popUpTo("video_splash") { inclusive = true }
                            }
                        }
                    }
                    
                    composable("main_app") {
                        var startDestination by remember { mutableStateOf("landingpage") }
                        
                        LaunchedEffect(Unit) {
                            try {
                                val tokenManager = TokenManager(this@MainActivity)
                                val savedToken = tokenManager.token.first()
                                startDestination = if (!savedToken.isNullOrEmpty()) "home" else "landingpage"
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Error checking auth", e)
                                startDestination = "landingpage"
                            }
                        }
                        
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            AppNavigation(
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}