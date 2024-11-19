package com.vadym.hdhmeeting

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}