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
        
        // Ask for notification permission
        askNotificationPermission()
        
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
}