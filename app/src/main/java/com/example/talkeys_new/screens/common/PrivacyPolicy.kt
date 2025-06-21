package com.example.talkeysapk.screensUI.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.example.talkeys_new.screens.common.HomeTopBar
import com.example.talkeys_new.R
import com.example.talkeys_new.screens.common.Footer


@Composable
fun privacyPolicy(navController: NavController) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // TopBar that stays fixed
            HomeTopBar(navController = navController)

            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Terms Of Service Text
                Text(
                    text = "Privacy Policy",
                    style = TextStyle(
                        fontSize = 34.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF7A2EC0),
                        textAlign = TextAlign.Center,
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Line Divider
                Image(
                    painter = painterResource(id = R.drawable.line_divider),
                    contentDescription = "Divider",
                    modifier = Modifier
                        .width(137.86559.dp)
                        .height(4.dp)
                )

                Spacer(modifier = Modifier.height(23.dp))

                // Description Text
                Text(
                    text = "Welcome to Talkeys! Before you dive in, it's important to understand the terms and conditions under which we operate. By using Talkeys, you agree to the following terms. Let's work together to make this platform a great space for everyone.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight(500),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                    )
                )

                Spacer(modifier = Modifier.height(34.dp))

                // Terms sections - CHANGED FROM ExpandableTermsSection TO ExpandablePrivacyPolicy
                ExpandablePrivacyPolicy(
                    title = "1. Information Collection",
                    content = "At Talkeys, we collect information such as your name, email address, contact number, and other relevant details when you sign up, register for events, or interact with our platform. This information is solely used to provide services such as event registration, ticketing, user support, and to enhance your overall experience on Talkeys."
                )

                ExpandablePrivacyPolicy(
                    title = "2. Information Usage",
                    content = "We do not sell or rent your personal data to third parties. However, your information may be shared with trusted partners or event organizers strictly for event-related purposes. To improve our website's functionality, we may use cookies and analytics tools, which help us understand user behavior and enhance platform performance."
                )

                ExpandablePrivacyPolicy(
                    title = "3. Data Security",
                    content = "Talkeys follows industry-standard security protocols to safeguard your information against unauthorized access or misuse. We implement appropriate data collection, storage, and processing practices and security measures to protect against unauthorized access, alteration, disclosure, or destruction of your personal information."
                )

                ExpandablePrivacyPolicy(
                    title = "4. User Rights",
                    content = " As a user, you have the right to access, update, or request the deletion of your personal data at any time by contacting our support team. You can also choose to opt out of certain communications or data collection practices by adjusting your account settings or contacting us directly."
                )

                ExpandablePrivacyPolicy(
                    title = "5. Data Retention",
                    content = "We retain your personal information for as long as it is required to deliver our services and comply with legal obligations. Once the purpose for which the information was collected has been fulfilled, we will securely delete or anonymize your data unless retention is necessary for legal or regulatory reasons."
                )

                ExpandablePrivacyPolicy(
                    title = "6. Cookies & Tracking",
                    content = "Our website uses cookies and similar tracking technologies to enhance your browsing experience, analyze site traffic, and personalize content. You can control cookie settings through your browser preferences, although disabling certain cookies may limit your ability to use some features of our platform."
                )

                ExpandablePrivacyPolicy(
                    title = "7. Third-Party Services",
                    content = "We may employ third-party companies and individuals to facilitate our service, provide the service on our behalf, perform service-related tasks, or assist us in analyzing how our service is used. These third parties have access to your personal information only to perform these tasks on our behalf and are obligated not to disclose or use it for any other purpose."
                )

                ExpandablePrivacyPolicy(
                    title = "8. Policy Changes",
                    content = "This privacy policy may be updated periodically, and we will notify users of significant changes through our platform or via email. We encourage you to review this policy regularly to stay informed about how we are protecting your information."
                )

                ExpandablePrivacyPolicy(
                    title = "9. Contact Us",
                    content = "If you have any questions or concerns regarding our privacy policy, please feel free to contact us at: talkeys11@gmail.com"
                )

                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.message_bg), // Replace with your PNG resource
                        contentDescription = "Thank You Background",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp) // Adjust height if needed
                    )
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Thank you for trusting Talkeys with your personal information. We are committed to maintaining the highest standards of privacy and data protection.",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight(500),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Sincerely,",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight(400),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Team Talkeys",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight(600),
                                color = Color(0xFF7A2EC0),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                // Footer
                Footer(navController = navController)

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ExpandablePrivacyPolicy(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )
    val iconBackgroundColor = if (expanded) Color(0xFF6923AA) else Color(0xFF2E2E2E)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Main container with conditional background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            // Apply different backgrounds based on expanded state
            if (expanded) {
                // PNG background when expanded
                Image(
                    painter = painterResource(id = R.drawable.termsandservice_bg),
                    contentDescription = "Background",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                // Regular background when collapsed
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = Color(0xFF1E1E1E),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFF2E2E2E),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                        )
                )
            }

            // Content column
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header row (always visible)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon container (circular background)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = iconBackgroundColor,
                                shape = CircleShape
                            )
                    ) {
                        // Choose icon based on title - Fixed the conditions
                        when {
                            title.contains("Information Collection") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.information_collection),
                                    contentDescription = "Information Collection Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Information Usage") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.information_usage),
                                    contentDescription = "Information Usage Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Data Security") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.data_security),
                                    contentDescription = "Data Security Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("User Rights") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.user_rights),
                                    contentDescription = "User Rights Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Data Retention") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.data_retention),
                                    contentDescription = "Data Retention Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Cookies & Tracking") -> { // Fixed typo from "Cooking" to "Cookies"
                                Image(
                                    painter = painterResource(id = R.drawable.cookies),
                                    contentDescription = "Cookies Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Third-Party Services") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.third_party_services),
                                    contentDescription = "Third-Party Services Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Policy Changes") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.policy_changes),
                                    contentDescription = "Policy Changes Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Contact Us") -> { // Fixed condition for Contact Us
                                Image(
                                    painter = painterResource(id = R.drawable.contact_us),
                                    contentDescription = "Contact Us Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            else -> {
                                Text(
                                    text = title.substringBefore("."),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Title and subtitle
                    Column(modifier = Modifier.weight(1f)) {
                        // Title
                        Text(
                            text = title,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight(600),
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Subtitle (extract from content or use default based on title)
                        val subtitle = when {
                            title.contains("Information Collection") -> "What data we collect and how"
                            title.contains("Information Usage") -> "How we use your information"
                            title.contains("Data Security") -> "How we protect your information"
                            title.contains("User Rights") -> "Your rights regarding your data"
                            title.contains("Data Retention") -> "How long we keep your information"
                            title.contains("Cookies & Tracking") -> "Our use of cookies and similar technologies"
                            title.contains("Third-Party Services") -> "How we work with third parties"
                            title.contains("Policy Changes") -> "How we handle policy updates"
                            title.contains("Contact Us") -> "How to reach our support team"
                            else -> "Section details"
                        }

                        Text(
                            text = subtitle,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFFAAAAAA)
                            )
                        )
                    }

                    // Arrow icon with correct color change
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = iconBackgroundColor, // Using the shared variable here
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(rotationState)
                        )
                    }
                }

                // Expandable content
                AnimatedVisibility(visible = expanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Content text
                        Text(
                            text = content,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight(400),
                                color = Color.White,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}