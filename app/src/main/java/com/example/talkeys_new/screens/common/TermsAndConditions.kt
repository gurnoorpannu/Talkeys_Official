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
fun TermsAndConditionsScreen(navController: NavController) {
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
                    text = "Terms Of Service",
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

                // Terms sections
                ExpandableTermsSection(
                    title = "1. Introduction",
                    content = "Talkeys is a platform that connects people through communities and events. Our mission is to help users discover, engage, and participate in various communities while offering a space for event creators to host their events with a global audience. These Terms of Service govern your use of our platform. By using Talkeys, you agree to comply with these terms. If you don't agree, please refrain from using our services.\n\nNote: Talkeys is solely a ticketing platform. We do not organize or manage events listed on our platform."
                )

                ExpandableTermsSection(
                    title = "2. Eligibility",
                    content = "To use Talkeys, you must be at least 13 years old (or older, depending on your local laws). By using the platform, you represent that you are legally permitted to use Talkeys and are responsible for complying with all applicable laws and regulations.\n\nTo access certain features of Talkeys, you'll need to create an account. When you sign up, please provide accurate information and keep your account secure. You are responsible for all activity that happens through your account, so please choose a strong password and keep it private.\n\nWe reserve the right to suspend or terminate your account if we detect any breach of these terms."
                )

                ExpandableTermsSection(
                    title = "3. Community Guidelines",
                    content = "We believe in fostering a positive and respectful environment for all users. As such, when participating in community chats, forums, or events, please abide by the following guidelines:\n\n- Respect others. No harassment, hate speech, or abusive behavior will be tolerated.\n- Keep it legal. Don't share any illegal content or engage in illegal activities.\n- No spamming. Refrain from unsolicited promotions, advertising, or irrelevant content.\n\nAny violation of these guidelines may result in a suspension or ban from Talkeys."
                )

                ExpandableTermsSection(
                    title = "4. User-Generated Content",
                    content = "At Talkeys, you have the opportunity to create and share content (e.g., messages, event listings, posts). By doing so, you retain ownership of your content, but you also grant a license to use, display, and promote it on our platform. We are not responsible for the content shared by users, but we reserve the right to remove any content that violates these terms or our community guidelines."
                )

                ExpandableTermsSection(
                    title = "5. Event Hosting",
                    content = "One of the most exciting features of Talkeys is the ability to create and host events. As an event host, you are responsible for the accuracy and content of your event listing. Please ensure your event follows our guidelines and complies with all applicable laws.\n\nImportant: Talkeys does not promote alcohol consumption or endorse any specific activities at events. We solely provide a ticketing platform to connect users with events."
                )

                ExpandableTermsSection(
                    title = "6. Intellectual Property",
                    content = "All content, design, branding, and features on Talkeys are the intellectual property of Talkeys or our licensors. You may not copy, reproduce, or distribute any part of Talkeys without our permission.\n\nThat said, any content you create on Talkeys remains yours, and we're excited to see what you build!"
                )

                ExpandableTermsSection(
                    title = "7. Privacy",
                    content = "Your privacy is incredibly important to us. Please refer to our Privacy Policy for details on how we collect, store, and use your personal data."
                )

                ExpandableTermsSection(
                    title = "8. Prohibited Activities",
                    content = "We are committed to creating a safe, enjoyable experience for everyone. Certain behaviors are strictly prohibited on Talkeys, including:\n\n- Engaging in unlawful activities or promoting illegal content.\n- Using the platform for fraudulent purposes.\n- Sharing offensive, obscene, or otherwise inappropriate content.\n- Impersonating others or misrepresenting your identity.\n\nFailure to comply may result in account suspension or termination."
                )

                ExpandableTermsSection(
                    title = "9. Liability Disclaimer",
                    content = "While we strive to provide the best possible experience on Talkeys, we do not guarantee that everything will always function perfectly. We are not responsible for:\n\n- Downtime, interruptions, or data loss.\n- Any issues arising from user-generated content or third-party events.\n- The success or outcome of any event hosted on our platform.\n\nYou use Talkeys at your own risk."
                )

                ExpandableTermsSection(
                    title = "10. Termination of Services",
                    content = "You may close your account at any time. We also reserve the right to terminate or suspend accounts that violate our terms or pose risks to the community. We will notify you if your account is at risk of termination unless you've engaged in serious violations that require immediate action."
                )

                ExpandableTermsSection(
                    title = "11. Changes to the Terms",
                    content = "We may update these terms periodically. If we make changes, we will notify you through the platform or via email. Your continued use of Talkeys following any changes means that you accept the new terms."
                )

                ExpandableTermsSection(
                    title = "12. Refund Policy",
                    content = "Talkeys serves solely as a ticketing platform and does not directly manage or organize the events listed on the platform. As such, the refund policy for each event is determined by the individual event organizer. Talkeys does not guarantee refunds for ticket purchases.\n\nUsers are advised to review the specific event's refund terms prior to completing a booking. If an event is canceled or rescheduled, any refund (partial or full) will be subject to the event organizer’s refund guidelines.\n\nTalkeys will facilitate the refund process only as per instructions received from the event organizer and is not liable for the outcome of refund claims.\n\nFor refund-related inquiries, please contact the respective event organizer directly. In case of technical issues related to payment processing, users can reach out to Talkeys at:\n\ntalkeys11@gmail.com."
                )

                ExpandableTermsSection(
                    title = "13. Contact Us",
                    content = "If you have any questions about these Terms of Service, please contact us at: Talkeys@gmail.com"
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
                            text = "Thank you for being a part of the Talkeys community. We can't wait to see what you create, and we're thrilled to have you on board!",
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
fun ExpandableTermsSection(title: String, content: String) {
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
                        // Choose icon based on title
                        when {
                            title.contains("Introduction") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.introduction),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Eligibility") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.eligibility),
                                    contentDescription = "Shield Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Community Guidelines") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.community_guidelines),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("User-Generated Content") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.user_generated),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Event Hosting") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.event_hosting),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Intellectual Property") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.intellectiual_property),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Privacy") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.privacy),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Prohibited Activities") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.prohibited_activities),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Liability Disclaimer") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.liability_disclaimer),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Termination of Services") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.termination_of_services),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Changes to the Terms") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.changes_to_the_terms),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Refund Policy") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.refund_policy),
                                    contentDescription = "Document Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            title.contains("Contact Us") -> {
                                Image(
                                    painter = painterResource(id = R.drawable.contact_us),
                                    contentDescription = "Document Icon",
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
                            title.contains("Introduction") -> "Platform overview and terms acceptance"
                            title.contains("Eligibility") -> "Age requirements and account responsibilities"
                            title.contains("Community Guidelines") -> "Rules for positive community interaction"
                            title.contains("User-Generated Content") -> "Content ownership and platform rights"
                            title.contains("Event Hosting") -> "Event creation and hosting responsibilities"
                            title.contains("Intellectual Property") -> "Copyright and intellectual property rights"
                            title.contains("Privacy") -> "Data collection and privacy practices"
                            title.contains("Prohibited Activities") -> "Activities not allowed on the platform"
                            title.contains("Liability Disclaimer") -> "Platform limitations and user responsibilities"
                            title.contains("Termination of Services") -> "Account closure and termination policies"
                            title.contains("Changes to the Terms") -> "Updates to terms and notification process"
                            title.contains("Refund Policy") -> "Our refund policy"
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
                                color = if (expanded) Color(0xFF6923AA) else Color(0xFF2E2E2E),
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