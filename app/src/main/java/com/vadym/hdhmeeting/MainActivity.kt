package com.vadym.hdhmeeting

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

//           val listLinks: List<ItemLinkEntity> = listOf(
//            ItemLinkEntity("Central Europe", "https://us06web.zoom.us/j/94352399169", listOf("Monday"), "7:00"),
//            ItemLinkEntity("Ukraine HDH", "https://us02web.zoom.us/j/89702900897", listOf("Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"), "6:00")
//        )

        fab.setOnClickListener {
            startActivity(Intent(this, CreateItemActivity::class.java))
        }

        adapter = ItemLinkAdapter(listLinks, db)
        rvListLinks.layoutManager = LinearLayoutManager(this)
        rvListLinks.adapter = adapter

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