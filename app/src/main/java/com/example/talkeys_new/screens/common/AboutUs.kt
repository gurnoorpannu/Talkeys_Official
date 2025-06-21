package com.example.talkeysapk.screensUI.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.talkeys_new.screens.common.HomeTopBar
import com.example.talkeys_new.R
import com.example.talkeys_new.screens.common.Footer

@Composable
fun AboutUsScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Use the existing HomeTopBar component
            HomeTopBar(navController)

            Spacer(modifier = Modifier.height(4.dp))

            // Centered "About Us" title with underline
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "About Us",
                    // H3/Bold
                    style = TextStyle(
                        fontSize = 34.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_bold)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF7A2EC0),
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .width(325.dp)
                        .height(41.dp)
                )

                // Underline image
                Image(
                    painter = painterResource(id = R.drawable.underline),
                    contentDescription = "Underline",
                    modifier = Modifier
                        .padding(0.dp)
                        .width(137.86559.dp)
                        .height(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(31.dp))

            // Welcome text
            Text(
                text = "Welcome to Talkeys, the ultimate meeting ground for fandoms, communities, and creators!",
                // Caption/Medium
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_light)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .width(306.dp)
                    .height(34.dp)
                    .align (Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(31.dp))

            // Outer card with border
            Surface(
                modifier = Modifier
                    .border(
                        width = 0.5.dp,
                        color = Color(0xFF4A0E82),
                        shape = RoundedCornerShape(size = 20.dp)
                    )
                    .width(363.dp)
                    .height(495.dp),
                shape = RoundedCornerShape(size = 20.dp),
                color = Color(0xFF120227) // Dark purple background matching the image
            ) {
                // Container for inner content with padding
                Box(
                    modifier = Modifier
                        .padding(start = 24.dp, top = 18.dp, end = 23.dp, bottom = 18.dp)
                ) {
                    // Inner text container
                    Surface(
                        modifier = Modifier
                            .width(316.dp)
                            .height(445.dp),
                        shape = RoundedCornerShape(size = 16.dp),
                        color = Color(0xFF120227) // Same dark purple background
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Welcome to Talkeys, the ultimate meeting ground for fandoms, communities, and creators! Whether you're a die-hard anime lover, a formula racing enthusiast, a desi hip-hop fan, or part of any niche community, Talkeys is where your world gets louder, more connected, and more exciting.",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    color = Color.White,
                                    lineHeight = 20.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Talkeys is a unique blend of a booking platform and anonymous chatroom designed to unite passionate people and give underrated artists the space they deserve. Dive into anonymous chatrooms curated for different interests and connect with others who share your passion. No matter how niche or mainstream, your community awaits you here.",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    color = Color.White,
                                    lineHeight = 20.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "But we're not just about conversations—we're about action! Artists can post their gigs and events, and fans can easily book tickets right through our platform. Discover hidden talent, support emerging creators, and be part of something bigger.",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    color = Color.White,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(21.dp))


            // Design me a card section
            MissionVisionScreen(363.dp)

            Spacer(modifier = Modifier.height(21.dp))

            // Outer card with border and specific layout to match the reference image
            Card(
                modifier = Modifier
                    .width(363.dp)
                    .height(329.dp)
                    .border(
                        width = 0.5.dp,
                        color = Color(0xFF4A0E82),
                        shape = RoundedCornerShape(size = 20.dp)
                    )
                    .padding(0.5.dp),
                shape = RoundedCornerShape(size = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF120227) // Dark purple background
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp), // Push content down
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Reduced size image and pushed it down
                    Image(
                        painter = painterResource(id = R.drawable.about_us_sticker),
                        contentDescription = "Journey Illustration",
                        modifier = Modifier
                            .fillMaxWidth(1.0f) // Adjust size of image
                            .height(220.dp), // Adjust height accordingly
                        contentScale = ContentScale.Fit // Keep the original proportions
                    )

                    // Spacer to create some gap
                    Spacer(modifier = Modifier.height(8.dp))

                    // Text positioned at the bottom
                    Text(
                        text = "Journey through the depths to\nfind your next adventure",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                            fontWeight = FontWeight(700),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }



            Spacer(modifier = Modifier.height(24.dp))

            // Use the existing Footer component
            Footer(navController = navController)
        }
    }
}


@Composable
fun MissionVisionValuesCard(cardWidth: Dp) {
    val darkPurple = Color(0xFF1A0A2E)
    val darkerPurple = Color(0xFF21103A)
    val white = Color.White

    var selectedTab by remember { mutableStateOf("Mission") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(darkPurple, shape = RoundedCornerShape(16.dp))
            .border(
                width = 0.5.dp,
                color = Color(0xFF4A0E82),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        // Tab Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(darkerPurple, shape = RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { TabButton("Mission", R.drawable.our_mission_icon, selectedTab == "Mission") { selectedTab = "Mission" } }
            item { TabButton("Vision", R.drawable.our_vision_icon, selectedTab == "Vision") { selectedTab = "Vision" } }
            item { TabButton("Values", R.drawable.our_values_icon, selectedTab == "Values") { selectedTab = "Values" } }
            item { TabButton("Community", R.drawable.our_communities_icon, selectedTab == "Community") { selectedTab = "Community" } }
        }

        // Content Based on Selected Tab
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .animateContentSize()
        ) {
            Column {
                // Title with Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = getTabIcon(selectedTab)),
                        contentDescription = selectedTab,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Our $selectedTab",
                        color = white,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content text
                Text(
                    text = getTabContent(selectedTab),
                    color = white,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun TabButton(text: String, iconRes: Int, selected: Boolean, onClick: () -> Unit) {
    val textColor = if (selected) Color.White else Color.Gray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 12.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Our $text",
                color = textColor,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (selected) {
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .width(70.dp)
                    .background(Color.White)
            )
        }
    }
}

// Helper functions to get the appropriate resources and content
fun getTabIcon(tab: String): Int {
    return when (tab) {
        "Mission" -> R.drawable.our_mission_icon
        "Vision" -> R.drawable.our_vision_icon
        "Values" -> R.drawable.our_values_icon
        "Community" -> R.drawable.our_communities_icon
        else -> R.drawable.our_mission_icon
    }
}

fun getTabContent(tab: String): String {
    return when (tab) {
        "Mission" -> "Our mission is to create a space where the voices of emerging artists are amplified, giving them equal footing to showcase their craft. We want to build a world where artistic discovery is powered by real communities, where both creators and fans can thrive together."
        "Vision" -> "Our vision is to establish a globally recognized platform that fosters creativity and innovation, empowering artists to turn their passion into a sustainable career."
        "Values" -> "We believe in authenticity, collaboration, and inclusivity, ensuring that every artist has an opportunity to shine and grow within a supportive community."
        "Community" -> "Our community is the heart of our platform, where artists and enthusiasts connect, share, and inspire one another. We foster an inclusive environment that nurtures creativity and growth."
        else -> ""
    }
}

@Composable
fun MissionVisionScreen(cardWidth: Dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        MissionVisionValuesCard(cardWidth)
    }
}