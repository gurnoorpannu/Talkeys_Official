package com.example.talkeys_new.screens.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.talkeys_new.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(navController: NavController, scrollState: ScrollState, modifier: Modifier = Modifier) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val backgroundColor by animateColorAsState(
        targetValue = when (currentRoute) {
            "home" -> when {
                scrollState.value > 100 -> Color(0xFF000000).copy(alpha = 0.95f)
                else -> Color(0xFF000000).copy(alpha = 0.92f)
            }
            "events", "communities" -> when {
                scrollState.value > 100 -> Color(0xFF000000).copy(alpha = 0.95f)
                else -> Color(0xFF000000).copy(alpha = 0.92f)
            }
            "explore" -> when {
                scrollState.value > 100 -> Color(0xFF000000).copy(alpha = 0.94f)
                else -> Color(0xFF000000).copy(alpha = 0.90f)
            }
            else -> Color(0xFF000000).copy(alpha = 0.92f)
        },
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(bottom = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Background layer 
        Box(
            modifier = Modifier
                .width(330.dp)
                .height(45.dp)
                .shadow(
                    elevation = 20.dp,
                    spotColor = Color.Black.copy(alpha = 0.15f),
                    ambientColor = Color.Black.copy(alpha = 0.1f)
                )
                .graphicsLayer {
                    shadowElevation = 10f
                    shape = RoundedCornerShape(25.dp)
                    clip = true
                }
                .background(
                    color = backgroundColor.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(25.dp)
                )
                .blur(radius = 45.dp)
        )

        // Content layer with icons (no blur applied)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(340.dp)
                .height(40.dp)
                .padding(horizontal = 28.dp)
        ) {
            val navigationItems = listOf(
                NavigationItem(
                    route = "screen_not_found",
                    normalIcon = R.drawable.ic_community_icon,
                    selectedIcon = R.drawable.ic_globe_selected,
                    contentDescription = "Communities"
                ),
                NavigationItem(
                    route = "events",
                    normalIcon = R.drawable.ic_events_icon,
                    selectedIcon = R.drawable.ic_events_selected,
                    contentDescription = "Events"
                ),
                NavigationItem(
                    route = "home",
                    normalIcon = R.drawable.ic_home_icon,
                    selectedIcon = R.drawable.ic_home_selected,
                    contentDescription = "Home"
                ),
                NavigationItem(
                    route = "screen_not_found",
                    normalIcon = R.drawable.ic_search_icon,
                    selectedIcon = R.drawable.ic_search_selected,
                    contentDescription = "Explore"
                ),
                NavigationItem(
                    route = "screen_not_found",
                    normalIcon = R.drawable.ic_globe_icon,
                    selectedIcon = R.drawable.ic_community_selected,
                    contentDescription = "Globe"
                )
            )

            navigationItems.forEach { navItem ->
                val isSelected = navItem.route == currentRoute
                val iconToDisplay = if (isSelected) navItem.selectedIcon else navItem.normalIcon

                Icon(
                    painter = painterResource(id = iconToDisplay),
                    contentDescription = navItem.contentDescription,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                    tint = Color.Unspecified
                )
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val normalIcon: Int,
    val selectedIcon: Int,
    val contentDescription: String
)

