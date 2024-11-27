import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import com.vadym.hdhmeeting.R

class OpenUrlService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url")
        if (url != null) {
            MediaPlayer.create(this, R.raw.beep_alarm).apply {
                start()
                setOnCompletionListener { release() }
            }
            openUrl(url)
        }
        stopSelf() // Stop service after task completion
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
//        createNotificationChannel()
//        val notification = NotificationCompat.Builder(this, "UrlOpenChannel")
//            .setContentTitle("Opening URL")
//            .setContentText("Opening URL in the background...")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .build()
//        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "UrlOpenChannel",
//                "URL Open Service",
//                NotificationManager.IMPORTANCE_LOW
//            )
//            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            manager.createNotificationChannel(channel)
//        }
    }
}
