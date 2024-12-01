package com.vadym.hdhmeeting

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Collections

class MainActivity : BaseActivity() {
    lateinit var db: SqliteDatabase
    private lateinit var adapter: ItemLinkAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var listLinks: List<ItemLinkEntity>
    private lateinit var fab: FloatingActionButton
    private lateinit var rvListLinks: RecyclerView

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_link_list)
        fab = findViewById(R.id.fab)
        rvListLinks = findViewById(R.id.rv_list_links)
        db = SqliteDatabase.getInstance(this)
        listLinks = db.listLinks()

        showOrHideFab()
        fab.setOnClickListener {
            startActivity(Intent(this, CreateItemActivity::class.java))
        }

        adapter = ItemLinkAdapter(listLinks, db) { viewHolder -> onStartDrag(viewHolder) }
        rvListLinks.layoutManager = LinearLayoutManager(this)
        rvListLinks.adapter = adapter

        itemTouchHelper = ItemTouchHelper(touchHelperCallback()).apply {
            attachToRecyclerView(rvListLinks)
        }

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

    private fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    private fun touchHelperCallback() = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags: Int = ItemTouchHelper.UP.or(ItemTouchHelper.DOWN)
            val swipeFlags: Int = ItemTouchHelper.ACTION_STATE_DRAG
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            drop(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            listLinks.forEachIndexed { index, current ->
                db.deleteLink(current.linkID)
                current.position = index
                db.updateSortPosition(current)
            }
        }
    }

    fun drop(from: Int, to: Int) {
        if (from < to) {
            for (i in from until to) {
                Collections.swap(listLinks, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(listLinks, i, i - 1)
            }
        }

        listLinks.forEachIndexed { index, current ->
            current.position = index
            db.updateSortPosition(current)
        }
    }

    private fun showOrHideFab() {
        rvListLinks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.visibility == View.VISIBLE) {
                    fab.hide()
                } else if (dy < 0 && fab.visibility != View.VISIBLE) {
                    fab.show()
                }
            }
        })
    }

}