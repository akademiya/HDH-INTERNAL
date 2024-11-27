package com.vadym.hdhmeeting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class InfoActivity: BaseActivity() {
    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_info)

        val version: TextView = findViewById(R.id.version)
        version.text = "Привіт, я Вадим - розробник даного додатка v. " + packageManager.getPackageInfo(packageName, 0).versionName

        val site = findViewById<Button>(R.id.site)
        site.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.site_link))))
        }
    }
}