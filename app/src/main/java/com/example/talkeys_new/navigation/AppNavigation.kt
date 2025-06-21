package com.example.talkeys_new.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.talkeys_new.screens.LandingPage
import com.example.talkeys_new.screens.authentication.loginScreen.LoginScreen
import com.example.talkeys_new.screens.authentication.signupScreen.SignUpScreen
import com.example.talkeys_new.screens.common.ScreenNotFound
import com.example.talkeys_new.screens.home.HomeScreen
import com.example.talkeys_new.screens.events.exploreEvents.ExploreEventsScreen
import com.example.talkeysapk.screensUI.home.AboutUsScreen
import com.example.talkeysapk.screensUI.home.ContactUsScreen
import com.example.talkeysapk.screensUI.home.TermsAndConditionsScreen
import com.example.talkeysapk.screensUI.home.privacyPolicy

@Composable
fun AppNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "events") {
        composable("landingpage") { LandingPage(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("events") { ExploreEventsScreen(navController) }
        composable("contact_us") { ContactUsScreen(navController) }
        composable("about_us") { AboutUsScreen(navController) }
        composable("privacy_policy") { privacyPolicy(navController) }
        composable("tas") { TermsAndConditionsScreen(navController) }
        composable("screen_not_found"){ScreenNotFound(navController)}
    }
}
