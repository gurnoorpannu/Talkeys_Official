package com.example.talkeys_new

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        // Create notification channel early to ensure proper permission handling
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title, it.body)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        // Handle data payload of FCM messages here
        // You can extract custom data and perform actions
        Log.d(TAG, "Handling data message: $data")
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server
        Log.d(TAG, "sendTokenToServer($token)")
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.default_notification_channel_id)
            val channel = NotificationChannel(
                channelId,
                "Push Notifications",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Notifications from Talkeys app"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}