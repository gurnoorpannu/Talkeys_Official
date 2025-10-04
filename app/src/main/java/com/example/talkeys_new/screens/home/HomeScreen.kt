package com.example.talkeys_new.screens.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.talkeys_new.screens.common.BottomBar
import com.example.talkeys_new.screens.common.Footer
import com.example.talkeys_new.screens.common.HomeTopBar
import com.example.talkeys_new.screens.events.exploreEvents.EventCard
import com.example.talkeys_new.screens.events.EventViewModel
import com.example.talkeys_new.screens.events.EventsRepository
import com.example.talkeys_new.screens.events.provideEventApiService
import com.example.talkeys_new.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current

    // Initialize EventViewModel with proper factory
    val eventViewModel: EventViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val apiService = provideEventApiService(context)
                val repository = EventsRepository(apiService)
                return EventViewModel(repository) as T
            }
        }
    )

    // Collect events state
    val events by eventViewModel.eventList.collectAsState()
    val isLoading by eventViewModel.isLoading.collectAsState()

    // Fetch events when screen loads
    LaunchedEffect(Unit) {
        eventViewModel.fetchAllEvents()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image (fixed, not inside scrolling area)
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            topBar = { HomeTopBar(navController = navController) },
            containerColor = Color.Transparent, // Make scaffold background transparent
            contentColor = Color.White,
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.navigationBars,
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // LazyColumn with LazyListState for scroll tracking
                val lazyListState = rememberLazyListState()

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp) // Padding for BottomBar
                ) {
                    item { BannerSection(navController) }
                    item { CategoryTitle("Live Events") }
                    item {
                        if (isLoading) {
                            LoadingEventRow()
                        } else {
                            EventRow(events.filter { it.isLive }, navController)
                        }
                    }
                    item { CategoryTitle("Featured Communities") }
                    item { CommunityRow(navController) }
                    item { CategoryTitle("Influencers Shaping the Community") }
                    item { InfluencerRow() }
                    item { HostYourOwnEvent(navController) }
                    item {
                        Footer(
                            modifier = Modifier,
                            navController = navController
                        )
                    }
                }

                // Bottom navigation
                BottomBar(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    scrollState = rememberScrollState()
                )
            }
        }
    }
}

@Composable
fun HostYourOwnEvent(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Host your own EVENT!!!",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFCFCFC),
                ),
                fontSize = 22.sp,
                color = Color(0xFFFCFCFC),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Create an event, invite your community, and manage everything in one place.",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                ),
                modifier = Modifier.width(237.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Updated button with click handler for unified create event
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(45.dp)
                    .background(color = Color(0xFF8A44CB), shape = RoundedCornerShape(8.dp))
                    .clickable {
                        // Navigate to create event screen
                        navController.navigate("create_event_1")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Host Event",
                    fontSize = 16.sp,
                    color = Color.White,
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.hostevent_sticker),
            contentDescription = "Host Event Sticker",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(200.dp)
        )
    }
}

@Composable
fun BannerSection(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.banner),
            contentDescription = "Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .align(Alignment.Center)
                .background(
                    Color.Black.copy(alpha = 0.8f),
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Explore Shows and \nevents with ease.",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Connect with fellow enthusiasts in our \nchat rooms. Share experiences and ideas\nanonymously.",
                    color = Color(0xFF8A44CB),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { navController.navigate("events") },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A44CB))
                    ) {
                        Text(
                            text = "Explore Events",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = { navController.navigate("communities") },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A44CB))
                    ) {
                        Text(
                            text = "Explore Communities",
                            fontSize = 13.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Visible
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_bold)),
            fontWeight = FontWeight.Bold,
            color = Color.White
        ),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun EventRow(events: List<com.example.talkeys_new.dataModels.EventResponse>, navController: NavController) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(events) { event ->
            EventCard(
                event = event,
                onClick = {
                    navController.navigate("eventDetail/${event._id}")
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadingEventRow() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(4) { index ->
            com.example.talkeys_new.screens.events.exploreEvents.SkeletonEventCard(
                modifier = Modifier.animateItemPlacement(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            )
        }
    }
}

// Sample data classes for Communities and Influencers
data class CommunityItem(
    val name: String,
    val description: String,
    val imageRes: Int,
    val memberCount: String
)

data class InfluencerItem(
    val name: String,
    val profession: String,
    val imageRes: Int,
    val followers: String
)

@Composable
fun CommunityRow(navController: NavController) {
    val communities = listOf(
        CommunityItem("Tech Enthusiasts", "Join fellow developers and tech lovers", R.drawable.community_banner, "1.2K"),
        CommunityItem("Gaming Community", "Connect with gamers worldwide", R.drawable.community_banner, "850"),
        CommunityItem("Art & Design", "Creative minds unite", R.drawable.community_banner, "2.1K"),
        CommunityItem("Music Lovers", "Share your passion for music", R.drawable.community_banner, "1.8K")
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(communities) { community ->
            CommunityCard(
                name = community.name,
                imageRes = community.imageRes,
                description = community.description,
                navController = navController
            )
        }
    }
}

@Composable
fun CommunityCard(name: String, imageRes: Int, description: String, navController: NavController) {
    // ✅ Wrapper Box with Background and Shadows
    Box(
        modifier = Modifier
            .shadow(elevation = 4.dp,
                ambientColor = Color(0xFF20201F)
            )
            .width(148.dp)
            .clickable { navController.navigate("screen_not_found") }
            .height(200.dp) // Slightly increased height to accommodate extra text
            .background(color = Color(0xFF212020), shape = RoundedCornerShape(size = 15.dp))
            .padding(start = 6.dp, top = 7.dp, end = 6.dp, bottom = 7.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            // ✅ Community Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Community Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(136.dp)
                    .height(136.dp)
                    .background(
                        color = Color(0xFFD9D9D9),
                        shape = RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp))
            )

            // ✅ Community Name - Changed to "Coming Soon"
            Text(
                text = "Coming Soon",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                    color = Color.White
                ),
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )

            // ✅ Additional Description Text
            Text(
                text = description,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)), // Less bold
                    color = Color.White
                ),
                modifier = Modifier.padding(start = 6.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun InfluencerRow() {
    val influencers = listOf(
        InfluencerItem("Arijit Sharma", "F1 Racer", R.drawable.ic_influencer_banner, "125K"),
        InfluencerItem("Arsh Chatrath", "Cricketer", R.drawable.ic_influencer_banner, "89K"),
        InfluencerItem("Rohan Mehta", "Tech Reviewer", R.drawable.ic_influencer_banner, "234K"),
        InfluencerItem("Priya Singh", "Content Creator", R.drawable.ic_influencer_banner, "156K")
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(influencers) { influencer ->
            InfluencerCard(
                name = influencer.name,
                profession = influencer.profession,
                imageRes = influencer.imageRes
            )
        }
    }
}

@Composable
fun InfluencerCard(name: String, profession: String, imageRes: Int) {
    Box(
        modifier = Modifier
            .shadow(elevation = 4.dp, spotColor = Color(0xFF000000), ambientColor = Color(0xFF000000))
            .shadow(elevation = 27.dp, spotColor = Color(0x40FFFFFF), ambientColor = Color(0x40FFFFFF))
            .width(131.dp)
            .height(158.dp)
            .background(color = Color(0x1AFFFFFF), shape = RoundedCornerShape(size = 15.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Start, // Changed alignment to Start for left alignment
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(horizontal = 8.dp) // Optional: Adjust padding for better alignment
        ) {
            // Influencer Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Influencer Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(103.dp)
                    .background(
                        color = Color(0xFFD9D9D9),
                        shape = RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp)
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Influencer Name
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight(400),
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Influencer Profession
            Text(
                text = profession,
                modifier = Modifier
                    .fillMaxWidth() // Ensures proper alignment within the Column
                    .padding(start = 4.dp), // Adds padding for consistent left alignment
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Start
                )
            )
        }
    }
}