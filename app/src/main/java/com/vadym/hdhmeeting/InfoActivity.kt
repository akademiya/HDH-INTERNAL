package com.vadym.hdhmeeting

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.gms.ads.AdView

class InfoActivity: BaseActivity() {
    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_info)
//        val adContainer: AdView = findViewById(R.id.adView)
        val version: TextView = findViewById(R.id.version)

        version.text = "v. " + packageManager.getPackageInfo(packageName, 0).versionName

//        if (isNetworkAvailable(this)) {
//            adContainer.visibility = View.VISIBLE
//            Admob.initializeAdmob(this, adContainer)
//        } else {
//            adContainer.visibility = View.GONE
//        }
    }
}