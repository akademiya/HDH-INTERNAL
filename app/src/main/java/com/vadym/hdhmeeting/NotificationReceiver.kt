package com.vadym.hdhmeeting

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        val item = intent?.getSerializableExtra("linkItem") as? ItemLinkEntity
        item?.let {
            verifyAndHandleLink(context, it)
        }


//        val url = intent?.getStringExtra("url")
//        val title = intent?.getStringExtra("title")
//
//        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item?.linkUrl))
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification = NotificationCompat.Builder(context, "LINK_NOTIFICATION_CHANNEL")
//            .setContentTitle(item?.linkTitle ?: "Open Link")
//            .setContentText("Tap to open: $url")
//            .setSmallIcon(R.drawable.hdh)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Create the notification channel if on API 26+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "LINK_NOTIFICATION_CHANNEL",
//                "Link Notifications",
//                NotificationManager.IMPORTANCE_HIGH
//            ).apply {
//                description = "Notifications for scheduled links"
//            }
//            notificationManager.createNotificationChannel(channel)
//        }
//        val mediaPlayer = MediaPlayer.create(context, R.raw.beep_alarm)
//
//        notificationManager.notify(item?.linkID ?: System.currentTimeMillis().toInt(), notification)
//        mediaPlayer.start()
//        mediaPlayer.setOnCompletionListener { it.release() }


    }

    @SuppressLint("UnspecifiedImmutableFlag", "ObsoleteSdkInt")
    fun verifyAndHandleLink(context: Context, item: ItemLinkEntity) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//        val isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//            powerManager.isInteractive
//        } else {
//            @Suppress("DEPRECATION")
//            powerManager.isScreenOn
//        }

        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE, // or PowerManager.ACQUIRE_CAUSES_WAKEUP
            "OpenNotificationReceiver:WakeLock"
        )
        wakeLock.acquire(3000)
        sendNotification(context, item)

//        if (!isScreenOn) {
//            context.applicationContext.startService(Intent(context, OpenUrlService::class.java).apply {
//                putExtra("url", item.linkUrl)
//            })
////            openUrlByHandle(context, item.linkUrl.toString())
//        } else {
//            sendNotification(context, item)
//        }
        wakeLock.release()
    }

//    private fun openUrlByHandle(context: Context, url: String) {
//        try {
//            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            })
//        } catch (e: Exception) {
//            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
//        }
//    }


    @SuppressLint("ObsoleteSdkInt")
    private fun sendNotification(context: Context, item: ItemLinkEntity) {
        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.linkUrl))
        val pendingIntent = PendingIntent.getActivity(
            context,
            item.linkID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "LINK_NOTIFICATION_CHANNEL")
            .setContentTitle(item.linkTitle ?: "Open Link")
            .setContentText("Tap to open: ${item.linkUrl}")
            .setSmallIcon(R.drawable.hdh)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel if on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "LINK_NOTIFICATION_CHANNEL",
                "Link Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for scheduled links"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(item.linkID, notification)
//        MediaPlayer.create(context, R.raw.beep_alarm5).apply {
//            start()
//            setOnCompletionListener { release() }
//        }
    }
}