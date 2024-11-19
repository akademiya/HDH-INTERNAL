package com.vadym.hdhmeeting

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout

abstract class BaseActivity : AppCompatActivity() {

//    lateinit var toolbar: Toolbar

    override fun setContentView(layoutResID: Int) {
        val fullView = layoutInflater.inflate(R.layout.app_bar_main, null)
        val activityContainer = fullView.findViewById<View>(R.id.content_base) as FrameLayout

        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(fullView)

//        toolbar = findViewById<View>(R.id.toolbar) as Toolbar

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
    }

    abstract fun init(savedInstanceState: Bundle?)

}