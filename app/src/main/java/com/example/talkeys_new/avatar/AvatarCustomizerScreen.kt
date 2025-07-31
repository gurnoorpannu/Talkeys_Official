package com.example.talkeys_new.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.talkeys_new.R

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

    // Refresh state to trigger avatar regeneration
    var refreshKey by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Customize Avatar",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
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
            // Background image
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Current Avatar Preview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF171717).copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Avatar",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        AvatarImageWithFallback(
                            avatarUrl = generateRefreshedAvatarUrl(avatarConfig, refreshKey),
                            size = 120.dp,
                            borderColor = Color(0xFF8A44CB),
                            borderWidth = 3.dp,
                            backgroundColor = Color(android.graphics.Color.parseColor("#${avatarConfig.backgroundColor}"))
                        )
                    }
                }

                // Avatar Style Selection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF171717).copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Avatar Style",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            )

                            // Refresh button - now updates the main avatar preview
                            IconButton(
                                onClick = {
                                    // Force refresh of the main avatar by updating the config
                                    refreshKey++
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Current Avatar",
                                    tint = Color(0xFF8A44CB),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(200.dp)
                        ) {
                            items(AvatarConstants.AVATAR_STYLES) { style ->
                                AvatarPreview(
                                    style = style,
                                    isSelected = style == avatarConfig.style,
                                    onClick = { avatarManager.updateAvatarStyle(style) },
                                    refreshKey = refreshKey,
                                    avatarConfig = avatarConfig // Pass current config for consistent preview
                                )
                            }
                        }
                    }
                }

                // Background Color Selection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF171717).copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Background Color",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
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
                                    .fillMaxWidth()
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
                                                        .size(20.dp)
                                                        .background(
                                                            Color(android.graphics.Color.parseColor("#${color.value}")),
                                                            RoundedCornerShape(4.dp)
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = color.name,
                                                    color = Color.White
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
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            avatarManager.resetToDefaults()
                            refreshKey = 0 // Reset the refresh key as well
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Reset")
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
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8A44CB)
                        )
                    ) {
                        Text("Save", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}