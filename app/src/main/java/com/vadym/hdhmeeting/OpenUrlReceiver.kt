package com.vadym.hdhmeeting

import OpenUrlService
import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class OpenUrlReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

//        val intent = Intent(context, OpenUrlService::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//
//
//// Build the notification
//        val notification = NotificationCompat.Builder(context, "openUrlChannel")
//            .setSmallIcon(R.drawable.hdh) // Replace with your app's drawable
//            .setContentTitle("HDH-MEETING OPEN URL")
//            .setContentText("Tap to open the link from HDH-MEETING app")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(pendingIntent) // Intent for notification action
//            .setAutoCancel(true) // Dismiss notification on tap
////            .build()
//
//        val notificationManager = NotificationManagerCompat.from(context)
//        notificationManager.notify(123, notification.build())


















//        val mediaPlayer = MediaPlayer.create(context, R.raw.beep_alarm)
//        mediaPlayer.start()
//        val url = intent.getStringExtra("url")
//
//        if (url != null) {
//            try {
////                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
////                val wakeLock = powerManager.newWakeLock(
////                    PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
////                    "OpenUrlReceiver:WakeLock"
////                )
////                wakeLock.acquire(3000)
////                sendNotification(context, url.toString())
//
//                val channelId = "openUrlChannel"
//                val notificationId = 1960
//
//                // Create a notification channel for displaying notifications
//                val channel = NotificationChannel(
//                    channelId,
//                    "Open URL Notifications",
//                    NotificationManager.IMPORTANCE_HIGH
//                ).apply {
//                    description = "Notifications to open URLs"
//                }
//
//
//
//
//                // Check and request notification permission
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                    // Request the notification permission
//                    ActivityCompat.requestPermissions(
//                        (context as? Activity) ?: return,
//                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                        1
//                    )
//                    return
//                }
//
//                // Display the notification
//                notificationManager?.notify(notificationId, notification)
//
//                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                })
////                wakeLock.release()
//            } catch (e: Exception) {
//                Toast.makeText(context, "No application found to open this link", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

//    fun sendNotification(context: Context, url: String) {
//        val channelId = "open_url_channel"
//        val notificationId = 1001
//
//        // Create an Intent to trigger the BroadcastReceiver
//        val intent = Intent(context, OpenUrlReceiver::class.java).apply {
//            putExtra("url", url)
//        }
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // Create a NotificationChannel (Required for Android 8.0 and above)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Open URL Notifications",
//                NotificationManager.IMPORTANCE_DEFAULT
//            ).apply {
//                description = "Notifications to open URLs"
//            }
//            val notificationManager = context.getSystemService(NotificationManager::class.java)
//            notificationManager?.createNotificationChannel(channel)
//        }
//
//        // Build the notification
//        val notification = NotificationCompat.Builder(context, channelId)
//            .setSmallIcon(R.drawable.hdh) // Replace with your notification icon
//            .setContentTitle("Open URL")
//            .setContentText("Tap to open the link in your browser.")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent) // Set the intent for the notification
//            .setAutoCancel(true) // Auto-dismiss notification when tapped
//            .build()
//
//        // Show the notification
//        val notificationManager = NotificationManagerCompat.from(context)
//        notificationManager.notify(notificationId, notification)
//    }

}