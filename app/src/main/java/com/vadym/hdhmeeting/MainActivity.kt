package com.vadym.hdhmeeting

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : BaseActivity() {
    lateinit var db: SqliteDatabase
    private lateinit var adapter: ItemLinkAdapter

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_link_list)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val rvListLinks = findViewById<RecyclerView>(R.id.rv_list_links)
        db = SqliteDatabase.getInstance(this)
        val listLinks: List<ItemLinkEntity> = db.listLinks()
//        val adContainer: AdView = findViewById(R.id.adView)

        fab.setOnClickListener {
            startActivity(Intent(this, CreateItemActivity::class.java))
        }

        adapter = ItemLinkAdapter(listLinks, db)
        rvListLinks.layoutManager = LinearLayoutManager(this)
        rvListLinks.adapter = adapter

//        if (isNetworkAvailable(this)) {
//            adContainer.visibility = View.VISIBLE
//            Admob.initializeAdmob(this, adContainer)
//        } else {
//            adContainer.visibility = View.GONE
//        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                startActivity(Intent(this, InfoActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}