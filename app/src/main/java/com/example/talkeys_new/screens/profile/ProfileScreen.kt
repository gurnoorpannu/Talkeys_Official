package com.example.talkeys_new.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.talkeys_new.R
import com.example.talkeys_new.screens.authentication.GoogleSignInManager
import com.example.talkeys_new.screens.authentication.UserProfile
import com.example.talkeys_new.avatar.AvatarManager
import com.example.talkeys_new.avatar.AvatarImageWithFallback
import com.example.talkeys_new.avatar.ProfileAvatarSection
import kotlinx.coroutines.launch
import android.util.Log
import com.example.talkeys_new.screens.authentication.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val googleSignInManager = remember { GoogleSignInManager(context) }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // User profile state with proper error handling
    val userProfile by googleSignInManager.userProfile.collectAsState(initial = UserProfile())
    var mutualCommunities by remember { mutableStateOf(2) }
    var userBio by remember { mutableStateOf("this is how your card will look like to others and this is your sample bio") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Check for signed-in Google account with comprehensive error handling
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            error = null

            val lastSignedInAccount =
                com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount(context)

            if (lastSignedInAccount != null) {
                // Validate account data before saving
                val accountId = lastSignedInAccount.id
                val displayName = lastSignedInAccount.displayName
                val email = lastSignedInAccount.email

                if (accountId != null && displayName != null && email != null) {
                    if (userProfile.name.isEmpty()) {
                        val profile = UserProfile(
                            id = accountId,
                            name = displayName,
                            email = email,
                            profileImageUrl = lastSignedInAccount.photoUrl?.toString(),
                            givenName = lastSignedInAccount.givenName ?: displayName.split(" ").firstOrNull() ?: "",
                            familyName = lastSignedInAccount.familyName ?: displayName.split(" ").drop(1).joinToString(" ")
                        )
                        googleSignInManager.saveUserProfile(profile)
                    }
                } else {
                    error = "Incomplete account information"
                }
            } else {
                error = "No signed-in account found"
            }
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error checking Google account", e)
            error = "Failed to load account information"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Dashboard",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            try {
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Log.e("ProfileScreen", "Navigation error", e)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF262626),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            // Enhanced Profile Section with rounded corners (removed spacing above)
            ProfileSection(
                userProfile = userProfile,
                userBio = userBio,
                mutualCommunities = mutualCommunities,
                isLoading = isLoading,
                error = error,
                clipboardManager = clipboardManager,
                navController = navController
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Items Section
            MenuItemsSection(navController = navController)

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Section
            LogoutSection(
                tokenManager = tokenManager,
                googleSignInManager = googleSignInManager,
                navController = navController,
                scope = scope
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ProfileSection(
    userProfile: UserProfile,
    userBio: String,
    mutualCommunities: Int,
    isLoading: Boolean,
    error: String?,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171717)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                LoadingProfileContent()
            } else if (error != null) {
                ErrorProfileContent(error = error)
            } else {
                ProfileContent(
                    userProfile = userProfile,
                    userBio = userBio,
                    mutualCommunities = mutualCommunities,
                    clipboardManager = clipboardManager,
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun LoadingProfileContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color(0xFF6A4C93),
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading profile...",
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                color = Color.White
            )
        )
    }
}

@Composable
private fun ErrorProfileContent(error: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = Color.Red,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun ProfileContent(
    userProfile: UserProfile,
    userBio: String,
    mutualCommunities: Int,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Purple Strip at top - Increased height for larger avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp) // Increased from 43.dp to 53.dp for larger avatar
                    .background(
                        color = Color(0xFF6A4C93), // Purple color
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 15.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Friend request icon
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Friend Request",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Three dots icon
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Rest of the profile content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF171717),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(40.dp)) // Increased space for larger profile image overlap (100.dp avatar)

                    // User Name with Copy Icon (right next to name)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getUserDisplayName(userProfile),
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Username",
                            tint = Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    val username = getUserDisplayName(userProfile)
                                    clipboardManager.setText(AnnotatedString(username))
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email with Pronouns (right next to email)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getUserEmail(userProfile),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFA3A3A3),
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "•",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "he/him",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFA3A3A3),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Communities Information
                    CommunitiesInfo(mutualCommunities = mutualCommunities)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bio Section with responsive sizing
                    BioSection(userBio = userBio)
                }
            }
        }

        // Profile Avatar (overlapping the purple strip) - Using Avatar System
        ProfileAvatarSection(
            userProfile = userProfile,
            navController = navController,
            modifier = Modifier.offset(x = 20.dp, y = 20.dp) // Adjusted from y = 15.dp to y = 20.dp for better positioning with larger avatar
        )
    }
}

@Composable
private fun ProfileImage(userProfile: UserProfile) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .border(
                width = 3.dp,
                color = Color(0xFF6A4C93),
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(Color(0xFFD9D9D9)),
        contentAlignment = Alignment.Center
    ) {
        if (!userProfile.profileImageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(userProfile.profileImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default Profile",
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun UserInformation(
    userProfile: UserProfile,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager
) {
    Column {
        // Username with copy functionality
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = getUserDisplayName(userProfile),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val username = getUserDisplayName(userProfile)
                    clipboardManager.setText(AnnotatedString(username))
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Username",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Email and pronouns
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = getUserEmail(userProfile),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFA3A3A3),
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = " • ",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            )

            Text(
                text = "he/him",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFA3A3A3),
                )
            )
        }
    }
}

@Composable
private fun CommunitiesInfo(mutualCommunities: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Group,
            contentDescription = "Communities",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "$mutualCommunities mutual communities joined",
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                color = Color.White,
            )
        )
    }
}

@Composable
private fun BioSection(userBio: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp) // Add horizontal padding to make it wider within the container
            .wrapContentHeight(), // Responsive height
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        SelectionContainer {
            Text(
                text = userBio,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFA3A3A3),
                    lineHeight = 20.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp) // Increased padding for better readability and more width
            )
        }
    }
}

@Composable
private fun MenuItemsSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Menu items with icons
        val menuItemsWithIcons = listOf(
            Triple(R.drawable.dialogbox_profile_icon, "Profile") { /* Navigate to profile edit */ },
            Triple(R.drawable.registered_events, "Registered events") {
                safeNavigate(navController, "registered_events")
            },
            Triple(R.drawable.ic_liked_events, "Liked events") {
                safeNavigate(navController, "liked_events")
            },
            Triple(R.drawable.hosted_events, "Hosted events") {
                safeNavigate(navController, "Organizer Dashboard")
            },
            Triple(R.drawable.dropdown_hostevent_icon, "Host new event") {
                /* Navigate to create event */
            },
            Triple(R.drawable.dialogbox_notification_icon, "Notifications") {
                /* Navigate to notifications */
            }
        )

        menuItemsWithIcons.forEach { (iconRes, title, onClick) ->
            EnhancedMenuItem(
                iconRes = iconRes,
                title = title,
                onClick = onClick
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Menu items without icons
        val menuItemsNoIcons = listOf(
            "About us" to { safeNavigate(navController, "about_us") },
            "Contact us" to { safeNavigate(navController, "contact_us") },
            "Privacy policy" to { safeNavigate(navController, "privacy_policy") },
            "Terms of Service" to { safeNavigate(navController, "tas") }
        )

        menuItemsNoIcons.forEach { (title, onClick) ->
            EnhancedMenuItemNoIcon(
                title = title,
                onClick = onClick
            )
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
private fun LogoutSection(
    tokenManager: TokenManager,
    googleSignInManager: GoogleSignInManager,
    navController: NavController,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val context = LocalContext.current
    var isLoggingOut by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (!isLoggingOut) {
                isLoggingOut = true
                scope.launch {
                    try {
                        // Clear app tokens and user profile
                        tokenManager.clearToken()
                        googleSignInManager.clearUserProfile()
                        
                        // Sign out from Google to force account picker on next login
                        val googleAuthClient = com.example.talkeys_new.screens.authentication.GoogleAuthClient(
                            context = context,
                            clientId = "563385258779-75kq583ov98fk7h3dqp5em0639769a61.apps.googleusercontent.com"
                        )
                        
                        // Revoke access to force account selection on next login
                        googleAuthClient.revokeAccess().addOnCompleteListener {
                            Log.d("ProfileScreen", "Google access revoked successfully")
                        }.addOnFailureListener { exception ->
                            Log.e("ProfileScreen", "Failed to revoke Google access", exception)
                            // Even if revoke fails, still sign out
                            googleAuthClient.signOut().addOnCompleteListener {
                                Log.d("ProfileScreen", "Google sign out completed")
                            }
                        }
                        
                        navController.navigate("landingpage") {
                            popUpTo(0) { inclusive = true }
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileScreen", "Logout error", e)
                    } finally {
                        isLoggingOut = false
                    }
                }
            }
        },
        enabled = !isLoggingOut,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4C4C4C),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoggingOut) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = "Logout",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun EnhancedMenuItem(
    iconRes: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171717)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
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

@Composable
private fun EnhancedMenuItemNoIcon(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171717)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
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

// Helper functions
private fun getUserDisplayName(userProfile: UserProfile): String {
    return when {
        userProfile.givenName.isNotEmpty() -> userProfile.givenName
        userProfile.name.isNotEmpty() -> userProfile.name
        else -> "User"
    }
}

private fun getUserEmail(userProfile: UserProfile): String {
    return if (userProfile.email.isNotEmpty()) {
        "@${userProfile.email.substringBefore("@")}"
    } else {
        "@user"
    }
}

private fun safeNavigate(navController: NavController, route: String) {
    try {
        navController.navigate(route)
    } catch (e: Exception) {
        Log.e("ProfileScreen", "Navigation error to $route", e)
    }
}