package com.example.talkeys_new.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.talkeys_new.screens.LandingPage
import com.example.talkeys_new.screens.authentication.loginScreen.LoginScreen
import com.example.talkeys_new.screens.authentication.signupScreen.SignUpScreen
import com.example.talkeys_new.screens.home.HomeScreen

@Composable
fun AppNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "landingpage") {
        composable("landingpage") { LandingPage(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
    }
}