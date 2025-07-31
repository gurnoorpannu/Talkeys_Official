package com.example.talkeys_new.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.delay

// Note: AvatarConfig is defined in AvatarManager.kt to avoid conflicts

/**
 * Sealed class representing different avatar loading states
 */
sealed class AvatarLoadingState {
    object Loading : AvatarLoadingState()
    object Success : AvatarLoadingState()
    data class Error(val throwable: Throwable?) : AvatarLoadingState()
}

// Note: AvatarConstants is defined in AvatarConstants.kt to avoid conflicts

/**
 * A professional, production-ready avatar image component with comprehensive error handling
 * and accessibility support.
 *
 * @param avatarUrl The URL of the avatar image to display
 * @param modifier Modifier to be applied to the component
 * @param size The size of the avatar (width and height)
 * @param isCircular Whether the avatar should be circular or rounded rectangle
 * @param borderColor Color of the border around the avatar
 * @param borderWidth Width of the border
 * @param backgroundColor Background color shown while loading or on error
 * @param contentDescription Accessibility content description
 * @param onLoadingStateChange Callback for loading state changes
 */
@Composable
fun AvatarImage(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    isCircular: Boolean = true,
    borderColor: Color = Color.White,
    borderWidth: Dp = 2.dp,
    backgroundColor: Color = Color.Gray.copy(alpha = 0.3f),
    contentDescription: String? = null,
    onLoadingStateChange: ((AvatarLoadingState) -> Unit)? = null
) {
    // Input validation
    require(size >= 24.dp) {
        "Avatar size must be at least 24dp"
    }
    require(size <= 512.dp) {
        "Avatar size must not exceed 512dp"
    }
    require(borderWidth >= 0.dp) { "Border width cannot be negative" }

    val shape = getAvatarShape(isCircular)
    var loadingState by remember { mutableStateOf<AvatarLoadingState>(AvatarLoadingState.Loading) }
    var timeoutExceeded by remember { mutableStateOf(false) }

    // Handle loading timeout
    LaunchedEffect(avatarUrl) {
        timeoutExceeded = false
        delay(10000L) // 10 seconds timeout
        if (loadingState is AvatarLoadingState.Loading) {
            timeoutExceeded = true
            loadingState = AvatarLoadingState.Error(Exception("Loading timeout exceeded"))
        }
    }

    // Notify parent of loading state changes
    LaunchedEffect(loadingState) {
        onLoadingStateChange?.invoke(loadingState)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .semantics {
                this.contentDescription = contentDescription ?: "User avatar"
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            avatarUrl.isNullOrBlank() -> {
                FallbackIcon(size = size)
            }
            timeoutExceeded -> {
                FallbackIcon(size = size)
            }
            else -> {
                AsyncImage(
                    model = createImageRequest(avatarUrl),
                    contentDescription = contentDescription ?: "User avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onState = { state ->
                        loadingState = when (state) {
                            is AsyncImagePainter.State.Loading -> AvatarLoadingState.Loading
                            is AsyncImagePainter.State.Success -> AvatarLoadingState.Success
                            is AsyncImagePainter.State.Error -> AvatarLoadingState.Error(state.result.throwable)
                            is AsyncImagePainter.State.Empty -> AvatarLoadingState.Loading
                        }
                    }
                )

                // Show loading indicator
                if (loadingState is AvatarLoadingState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size * 0.3f),
                        strokeWidth = 2.dp,
                        color = borderColor
                    )
                }

                // Show fallback on error
                if (loadingState is AvatarLoadingState.Error) {
                    FallbackIcon(size = size)
                }
            }
        }
    }
}

/**
 * Enhanced avatar component with explicit fallback handling and customizable fallback content.
 *
 * @param avatarUrl The URL of the avatar image to display
 * @param modifier Modifier to be applied to the component
 * @param size The size of the avatar
 * @param isCircular Whether the avatar should be circular
 * @param borderColor Color of the border
 * @param borderWidth Width of the border
 * @param backgroundColor Background color
 * @param contentDescription Accessibility content description
 * @param fallbackContent Custom fallback content to show on error
 * @param showLoadingIndicator Whether to show loading indicator
 * @param onLoadingStateChange Callback for loading state changes
 */
@Composable
fun AvatarImageWithFallback(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    isCircular: Boolean = true,
    borderColor: Color = Color.White,
    borderWidth: Dp = 2.dp,
    backgroundColor: Color = Color.Gray.copy(alpha = 0.3f),
    contentDescription: String? = null,
    fallbackContent: @Composable () -> Unit = { FallbackIcon(size = size) },
    showLoadingIndicator: Boolean = true,
    onLoadingStateChange: ((AvatarLoadingState) -> Unit)? = null
) {
    // Input validation
    validateAvatarParameters(size, borderWidth)

    val shape = getAvatarShape(isCircular)
    var loadingState by remember(avatarUrl) { mutableStateOf<AvatarLoadingState>(AvatarLoadingState.Loading) }
    var shouldShowFallback by remember(avatarUrl) { mutableStateOf(false) }

    // Reset state when URL changes
    LaunchedEffect(avatarUrl) {
        shouldShowFallback = avatarUrl.isNullOrBlank()
        if (!shouldShowFallback) {
            loadingState = AvatarLoadingState.Loading
        }
    }

    // Notify parent of loading state changes
    LaunchedEffect(loadingState) {
        onLoadingStateChange?.invoke(loadingState)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .semantics {
                this.contentDescription = contentDescription ?: "User avatar"
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            shouldShowFallback -> {
                fallbackContent()
            }
            else -> {
                AsyncImage(
                    model = createImageRequest(avatarUrl!!),
                    contentDescription = contentDescription ?: "User avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onState = { state ->
                        when (state) {
                            is AsyncImagePainter.State.Loading -> {
                                loadingState = AvatarLoadingState.Loading
                            }
                            is AsyncImagePainter.State.Success -> {
                                loadingState = AvatarLoadingState.Success
                                shouldShowFallback = false
                            }
                            is AsyncImagePainter.State.Error -> {
                                loadingState = AvatarLoadingState.Error(state.result.throwable)
                                shouldShowFallback = true
                            }
                            is AsyncImagePainter.State.Empty -> {
                                loadingState = AvatarLoadingState.Loading
                            }
                        }
                    }
                )

                // Show loading indicator
                if (showLoadingIndicator && loadingState is AvatarLoadingState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size * 0.3f),
                        strokeWidth = 2.dp,
                        color = borderColor
                    )
                }
            }
        }
    }
}

/**
 * Avatar preview component for style selection with enhanced error handling and accessibility.
 *
 * @param style The DiceBear style to preview
 * @param modifier Modifier to be applied to the component
 * @param size The size of the preview
 * @param isSelected Whether this preview is currently selected
 * @param onClick Click handler for selection
 * @param refreshKey Key to force refresh of the preview
 * @param avatarConfig Configuration for avatar generation
 * @param contentDescription Accessibility content description
 */
@Composable
fun AvatarPreview(
    style: String,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    refreshKey: Int = 0,
    avatarConfig: AvatarConfig? = null,
    contentDescription: String? = null
) {
    // Input validation
    require(style.isNotBlank()) { "Avatar style cannot be blank" }
    require(size >= 24.dp) {
        "Preview size must be at least 24dp"
    }

    val previewUrl = generatePreviewUrl(style, refreshKey, avatarConfig)
    val borderColor = if (isSelected) Color(0xFF8A44CB) else Color.Gray
    val borderWidth = if (isSelected) 3.dp else 1.dp
    val shape = RoundedCornerShape(8.dp)

    var loadingState by remember(previewUrl) { mutableStateOf<AvatarLoadingState>(AvatarLoadingState.Loading) }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Color.White)
            .border(borderWidth, borderColor, shape)
            .let { baseModifier ->
                onClick?.let { clickHandler ->
                    baseModifier
                        .clickable(
                            onClickLabel = contentDescription ?: "Select $style avatar style"
                        ) { clickHandler() }
                        .semantics {
                            role = Role.Button
                        }
                } ?: baseModifier
            }
            .semantics {
                this.contentDescription = contentDescription ?: "$style avatar style preview"
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = createImageRequest(previewUrl),
            contentDescription = contentDescription ?: "$style avatar style",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            onState = { state ->
                loadingState = when (state) {
                    is AsyncImagePainter.State.Loading -> AvatarLoadingState.Loading
                    is AsyncImagePainter.State.Success -> AvatarLoadingState.Success
                    is AsyncImagePainter.State.Error -> AvatarLoadingState.Error(state.result.throwable)
                    is AsyncImagePainter.State.Empty -> AvatarLoadingState.Loading
                }
            }
        )

        // Show loading indicator
        if (loadingState is AvatarLoadingState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(size * 0.4f),
                strokeWidth = 1.dp,
                color = borderColor
            )
        }

        // Show fallback on error
        if (loadingState is AvatarLoadingState.Error) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Preview unavailable",
                tint = Color.Gray,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}

/**
 * Creates a properly configured ImageRequest with error handling and caching
 */
@Composable
private fun createImageRequest(url: String): ImageRequest {
    return ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(true)
        .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
        .diskCachePolicy(coil.request.CachePolicy.ENABLED)
        .networkCachePolicy(coil.request.CachePolicy.ENABLED)
        .build()
}

/**
 * Generates a preview URL for DiceBear avatars with proper validation
 */
private fun generatePreviewUrl(
    style: String,
    refreshKey: Int,
    avatarConfig: AvatarConfig?
): String {
    // Validate style against our existing avatar styles
    if (style !in AvatarConstants.AVATAR_STYLES) {
        throw IllegalArgumentException("Invalid avatar style: $style")
    }

    return if (avatarConfig != null) {
        val baseSeed = avatarConfig.userName.lowercase().replace(Regex("[^a-z0-9]"), "")
        val variedSeed = if (refreshKey > 0) "${baseSeed}${refreshKey}" else {
            // If no refresh key, use the saved seed modifier or base seed
            if (avatarConfig.seedModifier.isNotEmpty()) "${baseSeed}${avatarConfig.seedModifier}" else baseSeed
        }
        "${AvatarConstants.DICEBEAR_BASE_URL}/$style/png?seed=$variedSeed&backgroundColor=${avatarConfig.backgroundColor}&size=100"
    } else {
        "${AvatarConstants.DICEBEAR_BASE_URL}/$style/png?seed=preview$refreshKey&size=100"
    }
}

/**
 * Returns the appropriate shape based on circular preference
 */
private fun getAvatarShape(isCircular: Boolean): Shape {
    return if (isCircular) CircleShape else RoundedCornerShape(8.dp)
}

/**
 * Validates avatar parameters and throws appropriate exceptions
 */
private fun validateAvatarParameters(size: Dp, borderWidth: Dp) {
    require(size >= 24.dp) {
        "Avatar size must be at least 24dp, got $size"
    }
    require(size <= 512.dp) {
        "Avatar size must not exceed 512dp, got $size"
    }
    require(borderWidth >= 0.dp) {
        "Border width cannot be negative, got $borderWidth"
    }
}

/**
 * Default fallback icon component
 */
@Composable
private fun FallbackIcon(
    size: Dp,
    icon: ImageVector = Icons.Default.Person,
    tint: Color = Color.White
) {
    Icon(
        imageVector = icon,
        contentDescription = "Default avatar",
        tint = tint,
        modifier = Modifier.size(size * 0.6f)
    )
}