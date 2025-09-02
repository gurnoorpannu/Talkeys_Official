package com.example.talkeysapk.screensUI.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.example.talkeys_new.screens.common.HomeTopBar
import com.example.talkeys_new.R
import com.example.talkeys_new.screens.common.Footer
import android.util.Log

@Composable
fun ContactUsScreen(navController: NavController) {
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

                // Contact Us Text
                Text(
                    text = "Contact Us",
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
                    text = "We'd love to hear from you! Fill out the form below and we'll get back to you as soon as possible.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight(500),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                    )
                )

                Spacer(modifier = Modifier.height(34.dp))

                // Contact Form Box - Using Box with background image instead of border
                Box(
                    modifier = Modifier
                        .width(363.dp)
                ) {
                    // Background image for the form
                    Image(
                        painter = painterResource(id = R.drawable.contactus_textbg),
                        contentDescription = "Form background",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.matchParentSize()
                    )

                    // Content inside the form
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
                    ) {
                        // Header and Image Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Send Us a Message Text
                            Text(
                                text = "Send Us a Message\n \nFill out the form below to get in touch with our team.",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    fontWeight = FontWeight(700),
                                    color = Color(0xFFF5F5F5),
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            // Computer Guy Meme Image
                            Image(
                                painter = painterResource(id = R.drawable.contactus_sticker),
                                contentDescription = "Contact illustration",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .shadow(
                                        elevation = 17.dp,
                                        spotColor = Color(0x40000000),
                                        ambientColor = Color(0x40000000)
                                    )
                                    .padding(1.dp)
                                    .width(100.dp)
                                    .height(100.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Form inputs
                        ContactFormFields()

                        Spacer(modifier = Modifier.height(24.dp))

                        // Send Message Button
                        Button(
                            onClick = { /* TODO: Implement send functionality */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7A2EC0)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Send Message",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                    fontWeight = FontWeight(600),
                                    color = Color(0xFFFFFFFF),
                                    textAlign = TextAlign.Center,
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer
                Footer(navController = navController)

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFormFields() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Name Field
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Your Name",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF),
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = {
                Text(
                    "Enter your full name",
                    color = Color(0x80FFFFFF) // Setting placeholder text color here
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color(0xFF7A2EC0),
                focusedBorderColor = Color(0xFF7A2EC0),
                unfocusedBorderColor = Color(0xFF4A0E82)
            )
            ,
            textStyle = TextStyle(color = Color.White), // Set text color here
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Email Field
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Your Email",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF),
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(
                    "Enter your email address",
                    color = Color(0x80FFFFFF) // Setting placeholder text color here
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color(0xFF7A2EC0),
                focusedBorderColor = Color(0xFF7A2EC0),
                unfocusedBorderColor = Color(0xFF4A0E82)
            )
            ,
            textStyle = TextStyle(color = Color.White), // Set text color here
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Message Field
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Your Message",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF),
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = {
                Text(
                    "Type your message here",
                    color = Color(0x80FFFFFF) // Setting placeholder text color here
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color(0xFF7A2EC0),
                focusedBorderColor = Color(0xFF7A2EC0),
                unfocusedBorderColor = Color(0xFF4A0E82)
            )
            ,
            textStyle = TextStyle(color = Color.White), // Set text color here
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp) // Set height here
        )
    }
}