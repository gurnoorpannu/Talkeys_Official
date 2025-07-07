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
                .padding(0.dp)
                .width(364.00549.dp)
                .height(0.8.dp)
                .background(color = Color(0xFFFFFFFF))
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Talkeys Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "image description",
            modifier = Modifier
                .padding(0.5.dp)
                .width(87.27267.dp)
                .height(80.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Social Media Icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SocialMediaIcon(R.drawable.ic_facebook_icon)
            SocialMediaIcon(R.drawable.ic_instagram_icon, "https://www.instagram.com/talkeys_/")
            SocialMediaIcon(R.drawable.ic_x_icon)
            SocialMediaIcon(R.drawable.ic_linkedin_icon,"https://www.linkedin.com/company/talkeys/posts/?feedView=all")
            SocialMediaIcon(R.drawable.ic_yt_icon)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Copyright Text
        Text(
            text = "© 2024  Talkeys. All rights reserved.",
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.4.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight(400),
                color = Color(0xFFF5F5F5),
            )
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


