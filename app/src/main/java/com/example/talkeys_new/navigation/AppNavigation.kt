package com.example.talkeys_new.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.talkeys_new.screens.LandingPage
import com.example.talkeys_new.screens.authentication.loginScreen.LoginScreen
import com.example.talkeys_new.screens.authentication.signupScreen.SignUpScreen
import com.example.talkeys_new.screens.common.ScreenNotFound
import com.example.talkeys_new.screens.events.eventDetailScreen.EventDetailScreen
import com.example.talkeys_new.screens.home.HomeScreen
import com.example.talkeys_new.screens.events.exploreEvents.ExploreEventsScreen
import com.example.talkeys_new.screens.profile.ProfileScreen
import com.example.talkeys_new.screens.profile.RegisteredEventsScreen
import com.example.talkeys_new.screens.profile.LikedEventsScreen
import com.example.talkeys_new.screens.profile.HostedEventsScreen
import com.example.talkeysapk.screensUI.home.AboutUsScreen
import com.example.talkeysapk.screensUI.home.ContactUsScreen
import com.example.talkeysapk.screensUI.home.TermsAndConditionsScreen
import com.example.talkeysapk.screensUI.home.privacyPolicy

@Composable
fun AppNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "landingpage") {
        composable("landingpage") { LandingPage(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("registered_events") { RegisteredEventsScreen(navController) }
        composable("liked_events") { LikedEventsScreen(navController) }
        composable("hosted_events") { HostedEventsScreen(navController) }
        composable("events") { ExploreEventsScreen(navController) }
        composable("contact_us") { ContactUsScreen(navController) }
        composable("about_us") { AboutUsScreen(navController) }
        composable("privacy_policy") { privacyPolicy(navController) }
        composable("tas") { TermsAndConditionsScreen(navController) }
        composable("screen_not_found"){ScreenNotFound(navController)}

        // Event Detail Screen with eventId parameter
        composable(
            route = "eventDetail/{eventId}",
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                navController = navController
            )
        }

        composable("exploreEvents") {
            ExploreEventsScreen(navController = navController)
        }

        // Event Registration Screen (placeholder - you'll need to create this)
        composable("event_registration") {
            // EventRegistrationScreen(navController = navController)
            // For now, you can just show a placeholder or navigate back
        }

        // PhonePe Payment Screen
      /*  composable("payment") {
            PaymentScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }*/

        // PhonePe Payment Screen with Event Details
        composable(
            route = "payment/{eventId}/{eventName}/{eventPrice}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("eventName") { type = NavType.StringType },
                navArgument("eventPrice") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val eventName = backStackEntry.arguments?.getString("eventName") ?: ""
            val eventPrice = backStackEntry.arguments?.getString("eventPrice") ?: "0"
            
       /*     EventPaymentScreen(
                eventId = eventId,
                eventName = eventName,
                eventPrice = eventPrice,
                onNavigateBack = { navController.popBackStack() }
            )*/
        }

    }
}
