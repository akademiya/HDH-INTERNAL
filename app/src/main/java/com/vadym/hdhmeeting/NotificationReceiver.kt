package com.vadym.hdhmeeting

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

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

//        val serviceIntent = Intent(context, OpenUrlService::class.java).apply {
//            putExtra("url", item?.linkUrl)
//        }
//        context.startForegroundService(serviceIntent)
    }

private fun verifyAndHandleLink(context: Context, item: ItemLinkEntity, isSoundMessage: Boolean) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            powerManager.isInteractive
        } else {
            @Suppress("DEPRECATION")
            powerManager.isScreenOn
        }

//        val wakeLock = powerManager.newWakeLock(
//            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE, // or PowerManager.ACQUIRE_CAUSES_WAKEUP
//            "OpenNotificationReceiver:WakeLock"
//        )
//        wakeLock.acquire(3000)


//        acquireWakeLock(context).use {
//            sendNotification(context, item, isSoundMessage)
//        }


        if (isScreenOn) {
            val serviceIntent = Intent(context, OpenUrlService::class.java).apply {
                putExtra("url", item.linkUrl)
            }
            context.startForegroundService(serviceIntent)
        } else {
            acquireWakeLock(context).use {
                sendNotification(context, item, isSoundMessage)
//            openLinkInBrowser(context.applicationContext, item.linkUrl)
            }
        }
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

//    private fun openLinkInBrowser(context: Context, url: String?) {
//        try {
//            url?.let {
//                context.startActivity(
//                    Intent(Intent.ACTION_VIEW, Uri.parse(it)).apply {
//                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    }
//                )
//            }
//        } catch (e: ActivityNotFoundException) {
//            Log.e(context, "Something wrong with URL: $url", Toast.LENGTH_LONG)
//        }
//
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

        /** Create the notification channel if on API 26+ */
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            with(NotificationManagerCompat.from(context)) {
                notify(item.linkID, notification)
            }
        } else {
            Toast.makeText(context, "Notifications are disabled due to missing permission", Toast.LENGTH_LONG).show()
        }

        if (isSoundMessage) {
            MediaPlayer.create(context, R.raw.beep_alarm3).apply {
                start()
                setOnCompletionListener { release() }
            }
        }

    }

}