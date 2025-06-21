package com.example.talkeys_new.screens.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.talkeys_new.R

@Composable
fun Footer(modifier: Modifier = Modifier,navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Horizontal Line
        Box(
            modifier = Modifier
                .width(364.87216.dp)
                .height(0.8.dp)
                .background(color = Color.White)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween // Ensures equal spacing
        ) {
            // Left Side (Team Talkeys + Address)
            Column(
                modifier = Modifier.weight(1f), // Both sides share equal weight
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Team Talkeys",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.W400,
                        color = Color(0xFFF5F5F5),
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Text(
                    text = "Address:Thapar University,\n Patiala, Punjab, India",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.W500,
                        color = Color(0xFFF5F5F5),
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Get in Touch:+91 98882 30798\ntalkeys11@gmail.com",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.W500,
                        color = Color(0xFFF5F5F5),
                    )
                )
            }

            Spacer(modifier = Modifier.width(32.dp)) // Keeps space consistent between columns

            // Right Side (Buttons)
            Column(
                modifier = Modifier.weight(1f), // Equal weight as the left side
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FooterTextButton("Contact us") { navController.navigate("contact_us") }
                Spacer(modifier = Modifier.height(18.dp))

                FooterTextButton("About us") { navController.navigate("about_us") }
                Spacer(modifier = Modifier.height(18.dp))

                FooterTextButton("Privacy Policy") { navController.navigate("privacy_policy")  }
                Spacer(modifier = Modifier.height(18.dp))

                FooterTextButton("Terms of Service") { navController.navigate("tas") }

            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Center Talkeys Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Talkeys Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Social Media Icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            SocialMediaIcon(R.drawable.ic_facebook_icon)
            SocialMediaIcon(R.drawable.ic_instagram_icon, "https://www.instagram.com/talkeys_/")
            SocialMediaIcon(R.drawable.ic_x_icon)
            SocialMediaIcon(R.drawable.ic_linkedin_icon)
            SocialMediaIcon(R.drawable.ic_yt_icon)
        }

        Spacer(modifier = Modifier.height(19.dp))

        // Copyright Text
        Text(
            text = "© 2024  Talkeys. All rights reserved.",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                fontWeight = FontWeight.W400,
                color = Color(0xFFF5F5F5),
            ),
            modifier = Modifier.width(230.dp)
            //.height(22.dp)
        )
    }
}
// ✅ Footer Text Button with Clickable Action (Kept as is)
@Composable
fun FooterTextButton(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.W500,
            color = Color(0xFFF5F5F5),
        ),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun SocialMediaIcon(@DrawableRes iconRes: Int, url: String? = null) {
    val context = LocalContext.current

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        modifier = Modifier
            .size(24.dp)
            .clickable {
                url?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    context.startActivity(intent)
                }
            }
    )
}


