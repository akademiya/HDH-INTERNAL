package com.vadym.hdhmeeting

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.ArrayList

class SqliteDatabase private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_PERSON_TABLE = ("CREATE TABLE $TABLE_LINKS($KEY_ID INTEGER PRIMARY KEY,$KEY_LINK_TITLE TEXT,$KEY_LINK_URL TEXT,$KEY_LIST_DAYS TEXT,$KEY_TIME TEXT,$KEY_POSITION INTEGER)")
        db?.execSQL(CREATE_PERSON_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun addLink(link: ItemLinkEntity) {
        val values = ContentValues()
        values.put(KEY_LINK_TITLE, link.linkTitle)
        values.put(KEY_LINK_URL, link.linkUrl)
        values.put(KEY_LIST_DAYS, link.days!!.joinToString(","))
        values.put(KEY_TIME, link.time)
        values.put(KEY_POSITION, link.position)
        val db = this.writableDatabase

        db.insert(TABLE_LINKS, null, values)
        db.close()
    }

    fun listLinks(): List<ItemLinkEntity> {
        val sql = "select * from $TABLE_LINKS"
        val db = this.readableDatabase
        val storePersons = ArrayList<ItemLinkEntity>()
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToFirst()) {
            do {
                val id = Integer.parseInt(cursor.getString(0))
                val title = cursor.getString(1)
                val url = cursor.getString(2)
                val days = cursor.getString(3)?.split(",") ?: emptyList()
                val time = cursor.getString(4)
                val position = Integer.parseInt(cursor.getString(5))
                storePersons.add(ItemLinkEntity(id, title, url, days, time, position))

            } while (cursor.moveToNext())
        }
        cursor.close()
        return storePersons.sortedBy { it.position }
    }

    fun updateLink(link: ItemLinkEntity) {
        val values = ContentValues()
        values.put(KEY_LINK_TITLE, link.linkTitle)
        values.put(KEY_LINK_URL, link.linkUrl)
        values.put(KEY_LIST_DAYS, link.days!!.joinToString(","))
        values.put(KEY_TIME, link.time)
        val db = this.readableDatabase
        db.update(TABLE_LINKS, values, "$KEY_ID=?", arrayOf(link.linkID.toString()))
    }

    fun updateSortPosition(link: ItemLinkEntity) {
        val db = this.readableDatabase
        val values = ContentValues()
        val columns = arrayOf(KEY_ID, KEY_LINK_TITLE, KEY_LINK_URL, KEY_LIST_DAYS, KEY_TIME, KEY_POSITION)

        values.put(KEY_POSITION, link.position)
        db.query(TABLE_LINKS, columns, null, null, null, null, KEY_POSITION).close()
        db.update(TABLE_LINKS, values, "$KEY_ID=?", arrayOf(link.linkID.toString()))
    }

    fun deleteLink(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_LINKS, "$KEY_ID =?", arrayOf(id.toString()))
    }

    companion object {
        private val DATABASE_VERSION = 2
        private val DATABASE_NAME = "person"
        val TABLE_LINKS = "links"

        val KEY_ID = "_id"
        val KEY_LINK_TITLE = "linktitle"
        val KEY_LINK_URL = "linkurl"
        val KEY_LIST_DAYS = "days"
        val KEY_TIME = "time"
        val KEY_POSITION = "position"

        private var instance: SqliteDatabase? = null
        fun getInstance(context: Context): SqliteDatabase {
            if (instance == null) {
                instance = SqliteDatabase(context)
            }
            return instance!!
        }

    }
}