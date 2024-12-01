package com.vadym.hdhmeeting

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat

class InfoActivity: BaseActivity() {
    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var soundMessageSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_info)

        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        notificationSwitch = findViewById(R.id.notification_switch)
        soundMessageSwitch = findViewById(R.id.sound_switch)

        val version: TextView = findViewById(R.id.version)
        version.text = "Привіт, я Вадим - розробник даного додатка v. " + packageManager.getPackageInfo(packageName, 0).versionName

        val site = findViewById<Button>(R.id.site)
        site.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.site_link))))
        }

        val googlePlay = findViewById<TextView>(R.id.text_info)
        googlePlay.movementMethod = LinkMovementMethod.getInstance()

        notificationSwitch.isChecked = sharedPreferences.getBoolean("notificationSwitchState", false)
        soundMessageSwitch.isChecked = sharedPreferences.getBoolean("soundSwitchState", false)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        with(sharedPreferences.edit()) {
            putBoolean("notificationSwitchState", notificationSwitch.isChecked)
            putBoolean("soundSwitchState", soundMessageSwitch.isChecked)
            apply()
        }
    }
}