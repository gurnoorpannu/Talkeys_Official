package com.example.talkeys_new.avatar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.talkeys_new.R
import com.example.talkeys_new.screens.authentication.UserProfile
import androidx.core.graphics.toColorInt

/**
 * Enhanced profile avatar section that integrates with the avatar system
 * This can be used in your ProfileScreen to replace the existing profile image
 */
@Composable
fun ProfileAvatarSection(
    userProfile: UserProfile,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val avatarManager = remember { AvatarManager.getInstance(context) }
    val avatarConfig by avatarManager.avatarConfig.collectAsState()

    // Update avatar manager with user's name when profile changes
    LaunchedEffect(userProfile.name) {
        if (userProfile.name.isNotEmpty()) {
            avatarManager.updateUserName(userProfile.name)
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Avatar with edit functionality
        Box {
            AvatarImageWithFallback(
                avatarUrl = avatarConfig.generateAvatarUrl(),
                size = 100.dp, // Increased from 80dp to 100dp
                borderColor = Color(0xFF171717),
                borderWidth = 3.dp,
                backgroundColor = Color(android.graphics.Color.parseColor("#${avatarConfig.backgroundColor}")),
                showLoadingIndicator = false // Disable loading indicator completely
            )

            // Edit button overlay
            IconButton(
                onClick = { navController.navigate("avatar_customizer") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF8A44CB)
                    ),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }


    }
}

/**
 * Simple avatar display for use in navigation bars, comments, etc.
 */
@Composable
fun SimpleUserAvatar(
    userName: String = "",
    size: androidx.compose.ui.unit.Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val avatarManager = remember { AvatarManager.getInstance(context) }
    val avatarConfig by avatarManager.avatarConfig.collectAsState()

    // Update name if provided
    LaunchedEffect(userName) {
        if (userName.isNotEmpty() && userName != avatarConfig.userName) {
            avatarManager.updateUserName(userName)
        }
    }

    AvatarImageWithFallback(
        avatarUrl = avatarConfig.generateAvatarUrl(),
        size = size,
        borderColor = Color(0xFF8A44CB),
        borderWidth = 1.dp,
        backgroundColor = Color("#${avatarConfig.backgroundColor}".toColorInt()),
        modifier = modifier,
        showLoadingIndicator = false // Disable loading indicator
    )
}

/**
 * Menu item for avatar customization - can be added to your profile menu
 */
@Composable
fun AvatarCustomizationMenuItem(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { navController.navigate("avatar_customizer") },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171717)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Customize Avatar",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Customize Avatar",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}