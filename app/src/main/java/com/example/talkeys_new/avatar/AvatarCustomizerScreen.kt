package com.example.talkeys_new.avatar

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.talkeys_new.R
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow

// Helper function to generate refreshed avatar URL
private fun generateRefreshedAvatarUrl(avatarConfig: AvatarConfig, refreshKey: Int): String {
    val baseSeed = avatarConfig.userName.lowercase().replace(Regex("[^a-z0-9]"), "")
    val variedSeed = if (refreshKey > 0) "${baseSeed}${refreshKey}" else {
        // If no refresh key, use the saved seed modifier or base seed
        if (avatarConfig.seedModifier.isNotEmpty()) "${baseSeed}${avatarConfig.seedModifier}" else baseSeed
    }
    return "${AvatarConstants.DICEBEAR_BASE_URL}/${avatarConfig.style}/png?seed=$variedSeed&backgroundColor=${avatarConfig.backgroundColor}&size=200"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarCustomizerScreen(navController: NavController) {
    val context = LocalContext.current
    val avatarManager = remember { AvatarManager.getInstance(context) }
    val avatarConfig by avatarManager.avatarConfig.collectAsState()
    
    // Get screen dimensions for responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isSmallScreen = screenWidth < 360.dp

    // Refresh state to trigger avatar regeneration
    var refreshKey by remember { mutableStateOf(0) }
    
    // Simple loading state for 3D rotation animation
    var isAvatarLoading by remember { mutableStateOf(false) }
    var imageLoadingState by remember { mutableStateOf<AvatarLoadingState?>(null) }
    
    // Refresh button animation state
    var isRefreshClicked by remember { mutableStateOf(false) }
    
    // 3D rotation animation for loading with faster speed
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_rotation")
    val rotationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_y"
    )
    
    // Jumping/bouncing effect animation
    val jumpOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jump_offset"
    )
    
    // Scale effect for more dynamic feel
    val scaleEffect by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_effect"
    )
    
    // Helper function to start controlled loading with minimum duration
    fun startAvatarLoading() {
        isAvatarLoading = true
        imageLoadingState = null
    }
    
    // Simple loading management: fixed short duration for better UX
    LaunchedEffect(isAvatarLoading) {
        if (isAvatarLoading) {
            // Fixed loading time of 1.2 seconds (4 rotations at 300ms each)
            kotlinx.coroutines.delay(1200L)
            isAvatarLoading = false
        }
    }
    
    // Refresh button rotation animation
    val refreshRotation by animateFloatAsState(
        targetValue = if (isRefreshClicked) 360f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "refresh_rotation",
        finishedListener = {
            isRefreshClicked = false
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Customize Avatar",
                        style = TextStyle(
                            fontSize = if (isSmallScreen) 16.sp else 20.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF262626)
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (isSmallScreen) 12.dp else 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar Preview with 3D Loading Animation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (isSmallScreen) 8.dp else 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF171717).copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(if (isSmallScreen) 12.dp else 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(if (isSmallScreen) 16.dp else 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Avatar",
                            style = TextStyle(
                                fontSize = if (isSmallScreen) 16.sp else 18.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(bottom = if (isSmallScreen) 12.dp else 16.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Avatar with 3D rotation, jumping, and scaling effects
                        val avatarSize = if (isSmallScreen) 100.dp else 120.dp
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(avatarSize)
                                .offset(y = if (isAvatarLoading) jumpOffset.dp else 0.dp)
                                .graphicsLayer {
                                    if (isAvatarLoading) {
                                        this.rotationY = rotationY
                                        this.scaleX = scaleEffect
                                        this.scaleY = scaleEffect
                                        cameraDistance = 12f * density
                                    }
                                }
                        ) {
                            // Show loading placeholder during loading
                            if (isAvatarLoading) {
                                // Empty rotating placeholder - just the border and background
                                val borderWidth = if (isSmallScreen) 2.dp else 3.dp
                                Box(
                                    modifier = Modifier
                                        .size(avatarSize)
                                        .background(
                                            Color(android.graphics.Color.parseColor("#${avatarConfig.backgroundColor}")),
                                            RoundedCornerShape(avatarSize / 2)
                                        )
                                        .padding(borderWidth)
                                        .background(
                                            Color(0xFF8A44CB),
                                            RoundedCornerShape((avatarSize - borderWidth) / 2)
                                        )
                                        .padding(borderWidth)
                                        .background(
                                            Color(android.graphics.Color.parseColor("#${avatarConfig.backgroundColor}")),
                                            RoundedCornerShape((avatarSize - borderWidth * 2) / 2)
                                        )
                                )
                            } else {
                                // Show actual avatar when not loading
                                AvatarImageWithFallback(
                                    avatarUrl = generateRefreshedAvatarUrl(avatarConfig, refreshKey),
                                    size = avatarSize,
                                    borderColor = Color(0xFF8A44CB),
                                    borderWidth = if (isSmallScreen) 2.dp else 3.dp,
                                    backgroundColor = Color(android.graphics.Color.parseColor("#${avatarConfig.backgroundColor}")),
                                    showLoadingIndicator = false,
                                    onLoadingStateChange = { loadingState ->
                                        // Only update loading state if we're actually in a loading animation
                                        if (isAvatarLoading) {
                                            imageLoadingState = loadingState
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Avatar Style Selection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (isSmallScreen) 4.dp else 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF171717).copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(if (isSmallScreen) 12.dp else 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(if (isSmallScreen) 12.dp else 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Avatar Style",
                                style = TextStyle(
                                    fontSize = if (isSmallScreen) 14.sp else 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 8.dp else 12.dp)
                            ) {
                                Text(
                                    text = "Random",
                                    style = TextStyle(
                                        fontSize = if (isSmallScreen) 12.sp else 14.sp,
                                        fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF8A44CB)
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            startAvatarLoading()
                                            // Select random avatar style
                                            val randomStyle = AvatarConstants.AVATAR_STYLES.random()
                                            avatarManager.updateAvatarStyle(randomStyle)
                                            
                                            // Select random background color
                                            val randomColor = AvatarConstants.BACKGROUND_COLORS.random()
                                            avatarManager.updateBackgroundColor(randomColor.value)
                                            
                                            // Add some randomness to the avatar generation
                                            refreshKey = (1..999).random()
                                        }
                                        .background(
                                            Color(0xFF8A44CB).copy(alpha = 0.1f),
                                            RoundedCornerShape(if (isSmallScreen) 6.dp else 8.dp)
                                        )
                                        .padding(
                                            horizontal = if (isSmallScreen) 8.dp else 12.dp,
                                            vertical = if (isSmallScreen) 4.dp else 6.dp
                                        ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                IconButton(
                                    onClick = {
                                        isRefreshClicked = true
                                        refreshKey++
                                    },
                                    modifier = Modifier
                                        .size(if (isSmallScreen) 36.dp else 40.dp)
                                        .background(
                                            Color(0xFF8A44CB).copy(alpha = 0.1f),
                                            RoundedCornerShape(50)
                                        )
                                        .graphicsLayer {
                                            rotationZ = refreshRotation
                                        }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh Current Avatar",
                                        tint = Color(0xFF8A44CB),
                                        modifier = Modifier.size(if (isSmallScreen) 16.dp else 20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(if (isSmallScreen) 8.dp else 12.dp))

                        val gridColumns = if (isSmallScreen) 4 else 5
                        val gridHeight = if (isSmallScreen) 120.dp else 160.dp
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(gridColumns),
                            horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 6.dp else 8.dp),
                            verticalArrangement = Arrangement.spacedBy(if (isSmallScreen) 6.dp else 8.dp),
                            modifier = Modifier.height(gridHeight)
                        ) {
                            items(AvatarConstants.AVATAR_STYLES) { style ->
                                AvatarPreview(
                                    style = style,
                                    isSelected = style == avatarConfig.style,
                                    onClick = { 
                                        startAvatarLoading()
                                        avatarManager.updateAvatarStyle(style)
                                    },
                                    refreshKey = refreshKey,
                                    avatarConfig = avatarConfig
                                )
                            }
                        }
                    }
                }

                // Background Color Selection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (isSmallScreen) 4.dp else 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF171717).copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(if (isSmallScreen) 12.dp else 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(if (isSmallScreen) 12.dp else 16.dp)
                    ) {
                        Text(
                            text = "Background Color",
                            style = TextStyle(
                                fontSize = if (isSmallScreen) 14.sp else 16.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(bottom = if (isSmallScreen) 8.dp else 12.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        var expanded by remember { mutableStateOf(false) }
                        val selectedColor = AvatarConstants.BACKGROUND_COLORS.find {
                            it.value == avatarConfig.backgroundColor
                        } ?: AvatarConstants.BACKGROUND_COLORS[0]

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedColor.name,
                                onValueChange = { },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF8A44CB),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                textStyle = TextStyle(fontSize = if (isSmallScreen) 14.sp else 16.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color(0xFF262626))
                            ) {
                                AvatarConstants.BACKGROUND_COLORS.forEach { color ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(if (isSmallScreen) 16.dp else 20.dp)
                                                        .background(
                                                            Color(android.graphics.Color.parseColor("#${color.value}")),
                                                            RoundedCornerShape(4.dp)
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(if (isSmallScreen) 8.dp else 12.dp))
                                                Text(
                                                    text = color.name,
                                                    color = Color.White,
                                                    fontSize = if (isSmallScreen) 14.sp else 16.sp
                                                )
                                            }
                                        },
                                        onClick = {
                                            avatarManager.updateBackgroundColor(color.value)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (isSmallScreen) 12.dp else 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(if (isSmallScreen) 8.dp else 12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            avatarManager.resetToDefaults()
                            refreshKey = 0 // Reset the refresh key as well
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isSmallScreen) 40.dp else 48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Reset",
                            fontSize = if (isSmallScreen) 14.sp else 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = {
                            // Save the current refreshed variation as the permanent avatar
                            if (refreshKey > 0) {
                                avatarManager.updateSeedModifier(refreshKey.toString())
                            } else {
                                avatarManager.updateSeedModifier("") // Clear seed modifier if no refresh
                            }
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isSmallScreen) 40.dp else 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8A44CB)
                        )
                    ) {
                        Text(
                            "Save", 
                            color = Color.White,
                            fontSize = if (isSmallScreen) 14.sp else 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isSmallScreen) 16.dp else 32.dp))
            }
        }
    }
}