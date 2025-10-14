package com.example.talkeys_new.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log

object ConsentDialogHelper {
    private const val TAG = "ConsentDialog"

    /**
     * Show a consent dialog for FCM notifications
     * This is an example implementation - customize based on your app's design
     */
    fun showFCMConsentDialog(
        context: Context,
        onConsent: (granted: Boolean) -> Unit
    ) {
        Log.d(TAG, "📋 Showing FCM consent dialog")
        
        AlertDialog.Builder(context)
            .setTitle("Enable Notifications?")
            .setMessage(
                "This app would like to send you notifications about:\n\n" +
                "• Important updates\n" +
                "• New messages\n" +
                "• App announcements\n\n" +
                "You can change this setting anytime in app settings."
            )
            .setPositiveButton("Allow") { dialog, _ ->
                Log.d(TAG, "✅ User granted notification consent")
                dialog.dismiss()
                onConsent(true)
            }
            .setNegativeButton("Not Now") { dialog, _ ->
                Log.d(TAG, "❌ User denied notification consent")
                dialog.dismiss()
                onConsent(false)
            }
            .setCancelable(false) // Force user to make a choice
            .show()
    }

    /**
     * Show a dialog explaining why notifications were disabled
     */
    fun showNotificationsDisabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Notifications Disabled")
            .setMessage(
                "Notifications are currently disabled. You can enable them anytime in:\n\n" +
                "Settings → Notifications → Enable"
            )
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Show analytics consent dialog (separate from notifications)
     */
    fun showAnalyticsConsentDialog(
        context: Context,
        onConsent: (granted: Boolean) -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle("Help Improve Our App?")
            .setMessage(
                "We'd like to collect anonymous usage data to improve the app experience.\n\n" +
                "This includes:\n" +
                "• App performance metrics\n" +
                "• Feature usage statistics\n" +
                "• Crash reports\n\n" +
                "No personal information is collected."
            )
            .setPositiveButton("Allow") { dialog, _ ->
                Log.d(TAG, "✅ User granted analytics consent")
                dialog.dismiss()
                onConsent(true)
            }
            .setNegativeButton("No Thanks") { dialog, _ ->
                Log.d(TAG, "❌ User denied analytics consent")
                dialog.dismiss()
                onConsent(false)
            }
            .setCancelable(true)
            .show()
    }
}