package com.example.talkeys_new.screens.events.createEvent

// This file now serves as the unified create event screen with all steps combined
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.talkeys_new.screens.common.HomeTopBar
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent1Screen(navController: NavController) {
    // State variables for form fields
    var organizerName by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var organizationName by remember { mutableStateOf("") }
    var cityState by remember { mutableStateOf("") }
    var socialMediaLinks by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("No file selected") }
    
    // Error state variables
    var organizerNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var contactNumberError by remember { mutableStateOf("") }
    var organizationNameError by remember { mutableStateOf("") }
    var cityStateError by remember { mutableStateOf("") }
    var socialMediaError by remember { mutableStateOf("") }
    var fileError by remember { mutableStateOf("") }
    
    // Validation functions
    fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
        )
        return emailPattern.matcher(email).matches()
    }
    
    fun isValidPhoneNumber(phone: String): Boolean {
        val phonePattern = Pattern.compile("^[+]?[0-9]{10,15}$")
        return phonePattern.matcher(phone.replace("\\s".toRegex(), "")).matches()
    }
    
    fun isValidUrl(url: String): Boolean {
        val urlPattern = Pattern.compile(
            "^(https?://)?" +
            "([\\da-z\\.-]+)\\.([a-z\\.]{2,6})" +
            "([/\\w \\.-]*)*/?$"
        )
        return urlPattern.matcher(url).matches()
    }
    
    // Validation function
    fun validateFields(): Boolean {
        var isValid = true
        
        // Validate organizer name
        if (organizerName.isBlank()) {
            organizerNameError = "Organizer name is required"
            isValid = false
        } else {
            organizerNameError = ""
        }
        
        // Validate email
        if (emailAddress.isBlank()) {
            emailError = "Email address is required"
            isValid = false
        } else if (!isValidEmail(emailAddress)) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = ""
        }
        
        // Validate contact number
        if (contactNumber.isBlank()) {
            contactNumberError = "Contact number is required"
            isValid = false
        } else if (!isValidPhoneNumber(contactNumber)) {
            contactNumberError = "Please enter a valid phone number (10-15 digits)"
            isValid = false
        } else {
            contactNumberError = ""
        }
        
        // Validate organization name
        if (organizationName.isBlank()) {
            organizationNameError = "Organization name is required"
            isValid = false
        } else {
            organizationNameError = ""
        }
        
        // Validate city & state
        if (cityState.isBlank()) {
            cityStateError = "City & State is required"
            isValid = false
        } else {
            cityStateError = ""
        }
        
        // Validate social media links
        if (socialMediaLinks.isBlank()) {
            socialMediaError = "Social media link is required"
            isValid = false
        } else if (!isValidUrl(socialMediaLinks)) {
            socialMediaError = "Please enter a valid URL (e.g., https://instagram.com/...)"
            isValid = false
        } else {
            socialMediaError = ""
        }
        
        // Validate file upload
        if (selectedFileUri == null) {
            fileError = "Please upload a verification document"
            isValid = false
        } else {
            fileError = ""
        }
        
        return isValid
    }

    val context = LocalContext.current

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        fileName = if (uri != null) {
            // Extract file name from URI (simplified)
            uri.lastPathSegment ?: "File selected"
        } else {
            "No file selected"
        }
    }

    // Check if all fields are filled
    val areAllFieldsFilled = organizerName.isNotBlank() &&
            emailAddress.isNotBlank() &&
            contactNumber.isNotBlank() &&
            organizationName.isNotBlank() &&
            cityState.isNotBlank() &&
            socialMediaLinks.isNotBlank() &&
            selectedFileUri != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Black,        // Black at top left
                        Color(0xFF4A0E4E)   // Purple at bottom right
                    ),
                    center = androidx.compose.ui.geometry.Offset(0.2f, 0.2f),
                    radius = 1200f
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            HomeTopBar(navController = navController)

            // Main Content Card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.6f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header
                        Text(
                            text = "Create Your Event",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE91E63), // Pink color matching the image
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Step 1 of 6",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Section Title
                        Text(
                            text = "1. Basic Organizer Information",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Organizer Name Field
                        Text(
                            text = "Organizer Name *",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = organizerName,
                            onValueChange = { 
                                organizerName = it
                                if (organizerNameError.isNotEmpty()) organizerNameError = ""
                            },
                            placeholder = {
                                Text(
                                    "Enter organizer name",
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            isError = organizerNameError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (organizerNameError.isNotEmpty()) Color.Red else Color(0xFFE91E63),
                                unfocusedBorderColor = if (organizerNameError.isNotEmpty()) Color.Red else Color.White.copy(alpha = 0.3f),
                                errorBorderColor = Color.Red,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (organizerNameError.isNotEmpty()) {
                            Text(
                                text = organizerNameError,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Email Address Field
                        Text(
                            text = "📧 Email Address *",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = emailAddress,
                            onValueChange = { 
                                emailAddress = it
                                if (emailError.isNotEmpty()) emailError = ""
                            },
                            placeholder = {
                                Text(
                                    "Enter email address",
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = emailError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (emailError.isNotEmpty()) Color.Red else Color(0xFFE91E63),
                                unfocusedBorderColor = if (emailError.isNotEmpty()) Color.Red else Color.White.copy(alpha = 0.3f),
                                errorBorderColor = Color.Red,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (emailError.isNotEmpty()) {
                            Text(
                                text = emailError,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Contact Number Field
                        Text(
                            text = "📞 Contact Number *",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = contactNumber,
                            onValueChange = { 
                                contactNumber = it
                                if (contactNumberError.isNotEmpty()) contactNumberError = ""
                            },
                            placeholder = {
                                Text(
                                    "Enter contact number",
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            isError = contactNumberError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (contactNumberError.isNotEmpty()) Color.Red else Color(0xFFE91E63),
                                unfocusedBorderColor = if (contactNumberError.isNotEmpty()) Color.Red else Color.White.copy(alpha = 0.3f),
                                errorBorderColor = Color.Red,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (contactNumberError.isNotEmpty()) {
                            Text(
                                text = contactNumberError,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Organization Name Field
                        Text(
                            text = "Organization / Society Name *",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = organizationName,
                            onValueChange = { 
                                organizationName = it
                                if (organizationNameError.isNotEmpty()) organizationNameError = ""
                            },
                            placeholder = {
                                Text(
                                    "Enter organization name",
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            isError = organizationNameError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (organizationNameError.isNotEmpty()) Color.Red else Color(0xFFE91E63),
                                unfocusedBorderColor = if (organizationNameError.isNotEmpty()) Color.Red else Color.White.copy(alpha = 0.3f),
                                errorBorderColor = Color.Red,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (organizationNameError.isNotEmpty()) {
                            Text(
                                text = organizationNameError,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // City & State Field
                        Text(
                            text = "City & State *",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = cityState,
                            onValueChange = { 
                                cityState = it
                                if (cityStateError.isNotEmpty()) cityStateError = ""
                            },
                            placeholder = {
                                Text(
                                    "e.g Patiala, Punjab",
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            isError = cityStateError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (cityStateError.isNotEmpty()) Color.Red else Color(0xFFE91E63),
                                unfocusedBorderColor = if (cityStateError.isNotEmpty()) Color.Red else Color.White.copy(alpha = 0.3f),
                                errorBorderColor = Color.Red,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (cityStateError.isNotEmpty()) {
                            Text(
                                text = cityStateError,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Social Media Links Field
                        Text(
                            text = "Social Media Links *",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = socialMediaLinks,
                            onValueChange = { 
                                socialMediaLinks = it
                                if (socialMediaError.isNotEmpty()) socialMediaError = ""
                            },
                            placeholder = {
                                Text(
                                    "https://instagram.com/...",
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            isError = socialMediaError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (socialMediaError.isNotEmpty()) Color.Red else Color(0xFFE91E63),
                                unfocusedBorderColor = if (socialMediaError.isNotEmpty()) Color.Red else Color.White.copy(alpha = 0.3f),
                                errorBorderColor = Color.Red,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFE91E63),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (socialMediaError.isNotEmpty()) {
                            Text(
                                text = socialMediaError,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // File Upload Section
                        Text(
                            text = "📎 Upload Verification Document *",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // File Picker Button
                        OutlinedButton(
                            onClick = {
                                filePickerLauncher.launch("*/*")
                                if (fileError.isNotEmpty()) fileError = ""
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (fileError.isNotEmpty()) Color.Red else Color.White.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Choose File",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Choose File", fontSize = 16.sp)
                        }

                        // File Status
                        Text(
                            text = fileName,
                            fontSize = 14.sp,
                            color = if (selectedFileUri != null) Color.Green else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        
                        if (fileError.isNotEmpty()) {
                            Text(
                                text = fileError,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Next Button
                        Button(
                            onClick = {
                                if (validateFields()) {
                                    navController.navigate("create_event_2") // Fixed route name
                                }
                            },
                            enabled = validateFields(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (validateFields())
                                    Color(0xFFE91E63) else Color.Gray.copy(alpha = 0.3f),
                                contentColor = Color.White,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
                                disabledContentColor = Color.White.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                "Next ›",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}