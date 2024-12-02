package com.vadym.hdhmeeting

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder

class OpenUrlService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreferences = this.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isSoundMessage = sharedPreferences.getBoolean("soundSwitchState", false)
        val url = intent?.getStringExtra("url")
        if (url != null) {
            openUrl(url)
            if (isSoundMessage) {
                MediaPlayer.create(this, R.raw.beep_alarm6).apply {
                    start()
                    setOnCompletionListener { release() }
                }
            }

        }
        stopSelf() /** Stop service after task completion */
        return START_NOT_STICKY
    }

    private fun openUrl(url: String) {
        try {
            val openUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(openUrlIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
    }
}
