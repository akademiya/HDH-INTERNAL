package com.vadym.hdhmeeting

import android.os.Bundle
import android.widget.TextView

class InfoActivity: BaseActivity() {
    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_info)

        val version: TextView = findViewById(R.id.version)
        version.text = "v. " + packageManager.getPackageInfo(packageName, 0).versionName

    }
}