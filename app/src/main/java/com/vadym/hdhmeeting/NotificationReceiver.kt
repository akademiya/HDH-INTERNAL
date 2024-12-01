package com.vadym.hdhmeeting

import OpenUrlService
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onReceive(context: Context, intent: Intent?) {
        sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val soundSwitchState = sharedPreferences.getBoolean("soundSwitchState", false)

        @Suppress("DEPRECATION")
        val item = intent?.getSerializableExtra("linkItem") as? ItemLinkEntity
        item?.let {
            verifyAndHandleLink(context.applicationContext, it, soundSwitchState)
        }

//        if (item != null) {
//            context.startActivity(
//                Intent(Intent.ACTION_VIEW, Uri.parse(item.linkUrl)).apply {
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                }
//            )
//        }

        val serviceIntent = Intent(context, OpenUrlService::class.java).apply {
            putExtra("url", item?.linkUrl)
        }
        context.startForegroundService(serviceIntent)
//        context.startService(serviceIntent)


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

//    @SuppressLint("UnspecifiedImmutableFlag", "ObsoleteSdkInt")
private fun verifyAndHandleLink(context: Context, item: ItemLinkEntity, isSoundMessage: Boolean) {
//        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//        val isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//            powerManager.isInteractive
//        } else {
//            @Suppress("DEPRECATION")
//            powerManager.isScreenOn
//        }

//        val wakeLock = powerManager.newWakeLock(
//            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE, // or PowerManager.ACQUIRE_CAUSES_WAKEUP
//            "OpenNotificationReceiver:WakeLock"
//        )
//        wakeLock.acquire(3000)


        acquireWakeLock(context).use {
            sendNotification(context, item, isSoundMessage)
//            openLinkInBrowser(context.applicationContext, item.linkUrl)
        }


//        if (!isScreenOn) {
//            context.applicationContext.startService(Intent(context, OpenUrlService::class.java).apply {
//                putExtra("url", item.linkUrl)
//            })
////            openUrlByHandle(context, item.linkUrl.toString())
//        } else {
//            sendNotification(context, item)
//        }
//        wakeLock.release()
    }

    private inline fun PowerManager.WakeLock.use(block: () -> Unit) {
        try {
            block()
        } finally {
            release()
        }
    }


    private fun acquireWakeLock(context: Context): PowerManager.WakeLock {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "OpenNotificationReceiver:WakeLock"
        ).apply { acquire(3000) }
    }

    private fun openLinkInBrowser(context: Context, url: String?) {
        try {
            url?.let {
                Log.d("NotificationReceiver", "Opening URL: $it")
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(it)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            }
        } catch (e: ActivityNotFoundException) {
            Log.e("NotificationReceiver", "No activity found to handle the intent for URL: $url", e)
        }

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
    private fun sendNotification(context: Context, item: ItemLinkEntity, isSoundMessage: Boolean) {
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

//       ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED


        with(NotificationManagerCompat.from(context)) {
            notify(item.linkID, notification)
        }
//        notificationManager.notify(item.linkID, notification)
        if (isSoundMessage) {
            MediaPlayer.create(context, R.raw.beep_alarm3).apply {
                start()
                setOnCompletionListener { release() }
            }
        }

    }

}