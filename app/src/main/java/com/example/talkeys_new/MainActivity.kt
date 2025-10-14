package com.example.talkeys_new

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.talkeys_new.navigation.AppNavigation
import com.example.talkeys_new.screens.authentication.TokenManager
import com.example.talkeys_new.ui.theme.Talkeys_NewTheme
import com.example.talkeys_new.utils.FCMInitializationManager
import com.example.talkeys_new.utils.FCMTokenManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import com.talkeys.shared.Greeting
import com.talkeys.shared.initKoin

class MainActivity : ComponentActivity() {
    
    // Declare the launcher at the top of your Activity
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            Log.d("FCM_PERMISSION", "Notification permission granted")
        } else {
            // Inform user that your app will not show notifications.
            Log.d("FCM_PERMISSION", "Notification permission denied")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize KMP shared module
        initKoin()
        
        // Test shared module
        val greeting = Greeting().greet()
        Log.d("KMP_TEST", greeting)
        
        // Check Google Play Services availability
        if (checkGooglePlayServices()) {
            // Log FCM initialization status
            FCMInitializationManager.logInitializationStatus(this)
            
            // Ensure FCM is enabled (you can modify this based on your needs)
            if (!FCMInitializationManager.isAutoInitEnabled()) {
                Log.d(TAG, "FCM auto-init is disabled, enabling it...")
                FCMInitializationManager.enableAutoInit()
            }
            
            // Ask for notification permission
            askNotificationPermission()
            
            // Log current token status
            FCMTokenManager.logTokenStatus(this)
            
            // Retrieve FCM registration token
            retrieveFCMToken()
        } else {
            Log.e(TAG, "Google Play Services not available - FCM will not work")
        }
        
        setContent {
            Talkeys_NewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
                Log.d("FCM_PERMISSION", "Notification permission already granted")
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
                Log.d("FCM_PERMISSION", "Should show permission rationale")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Directly ask for the permission
                Log.d("FCM_PERMISSION", "Requesting notification permission")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // For Android 12L and below, permission is handled automatically
            Log.d("FCM_PERMISSION", "Android version < 33, no runtime permission needed")
        }
    }

    override fun onResume() {
        super.onResume()
        // Check Google Play Services again when app resumes
        checkGooglePlayServices()
    }
    
    private fun checkGooglePlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        
        return when (resultCode) {
            ConnectionResult.SUCCESS -> {
                Log.d(TAG, "✅ Google Play Services is available")
                true
            }
            ConnectionResult.SERVICE_MISSING,
            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,
            ConnectionResult.SERVICE_DISABLED -> {
                Log.w(TAG, "⚠️ Google Play Services issue: $resultCode")
                
                if (googleApiAvailability.isUserResolvableError(resultCode)) {
                    // Show dialog to user to resolve the issue
                    googleApiAvailability.makeGooglePlayServicesAvailable(this)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "✅ Google Play Services resolved successfully")
                                // Retry FCM initialization
                                retrieveFCMToken()
                            } else {
                                Log.e(TAG, "❌ Failed to resolve Google Play Services")
                            }
                        }
                } else {
                    Log.e(TAG, "❌ Google Play Services error cannot be resolved")
                }
                false
            }
            else -> {
                Log.e(TAG, "❌ Unknown Google Play Services error: $resultCode")
                false
            }
        }
    }
    
    private fun retrieveFCMToken() {
        FCMTokenManager.getCurrentToken { token ->
            if (token != null) {
                Log.d(TAG, "FCM Registration Token: $token")
                
                // Store token locally
                FCMTokenManager.storeToken(this, token)
                
                // Send token to your app server for targeting this device
                sendTokenToServer(token)
            } else {
                Log.e(TAG, "Failed to retrieve FCM token")
            }
        }
    }
    
    private fun sendTokenToServer(token: String) {
        // TODO: Implement this method to send token to your app server
        // This is where you would typically make an API call to your backend
        // to associate this token with the current user
        Log.d(TAG, "TODO: Send token to server: $token")
    }
    
    // Example methods for controlling FCM initialization
    // Call these based on user preferences or app requirements
    
    private fun enableFCMForUser() {
        Log.d(TAG, "User opted in to notifications")
        FCMInitializationManager.enableAll(this)
        retrieveFCMToken()
    }
    
    private fun disableFCMForUser() {
        Log.d(TAG, "User opted out of notifications")
        FCMInitializationManager.disableAll(this)
        FCMTokenManager.clearToken(this)
    }
    
    private fun enableOnlyFCM() {
        Log.d(TAG, "Enabling FCM without Analytics")
        FCMInitializationManager.enableAutoInit()
        FCMInitializationManager.disableAnalytics(this)
        retrieveFCMToken()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}