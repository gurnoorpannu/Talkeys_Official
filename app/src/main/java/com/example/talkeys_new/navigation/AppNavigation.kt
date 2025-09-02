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
import com.example.talkeys_new.screens.profile.OrganizerDashboardScreen
import com.example.talkeys_new.avatar.AvatarCustomizerScreen
import com.example.talkeys_new.screens.events.createEvent.CreateEvent1Screen
import com.example.talkeys_new.screens.events.createEvent.CreateEvent2Screen
import com.example.talkeys_new.screens.events.createEvent.CreateEvent3Screen
import com.example.talkeys_new.screens.events.createEvent.CreateEvent4Screen
import com.example.talkeys_new.screens.events.createEvent.CreateEvent5Screen
import com.example.talkeys_new.screens.events.createEvent.CreateEvent6Screen
import com.example.talkeysapk.screens.events.RegistrationSuccessScreen
import com.example.talkeysapk.screensUI.home.AboutUsScreen
import com.example.talkeysapk.screensUI.home.ContactUsScreen
import com.example.talkeysapk.screensUI.home.TermsAndConditionsScreen
import com.example.talkeysapk.screensUI.home.privacyPolicy

@Composable
fun AppNavigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        //composable("splash") { SplashScreen(navController) }
        composable("landingpage") { LandingPage(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("registered_events") { RegisteredEventsScreen(navController) }
        composable("liked_events") { LikedEventsScreen(navController) }
        composable("hosted_events") { HostedEventsScreen(navController) }
        composable("organizer_dashboard") {
            OrganizerDashboardScreen(
                onBackClick = { navController.popBackStack() },
                onHostEventClick = { navController.navigate("create_event_1") }
            )
        }
        composable("events") { ExploreEventsScreen(navController) }
        composable("contact_us") { ContactUsScreen(navController) }
        composable("about_us") { AboutUsScreen(navController) }
        composable("privacy_policy") { privacyPolicy(navController) }
        composable("tas") { TermsAndConditionsScreen(navController) }
        composable("avatar_customizer") { AvatarCustomizerScreen(navController) }
        composable("screen_not_found"){ScreenNotFound(navController)}
        
        // Create Event Flow
        composable("create_event_1") { CreateEvent1Screen(navController) }
        composable("create_event_2") { CreateEvent2Screen(navController) }
        composable("create_event_3") { CreateEvent3Screen(navController) }
        composable("create_event_4") { CreateEvent4Screen(navController) }
        composable("create_event_5") { CreateEvent5Screen(navController) }
        composable("create_event_6") { CreateEvent6Screen(navController) }
        composable("registration_success") { RegistrationSuccessScreen(navController) }

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

        // Payment functionality disabled for now
        // You can uncomment this when you need payment integration
        /*
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
            
            com.example.talkeys_new.screens.events.payment.EventPaymentScreen(
                eventId = eventId,
                eventName = eventName,
                eventPrice = eventPrice,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        */

    }
}
