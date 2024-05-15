package com.example.exampleapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "items.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "items"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ITEM = "item"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_ITEM TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TEMPORARY TABLE items_backup AS SELECT * FROM $TABLE_NAME")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
            db.execSQL("INSERT INTO $TABLE_NAME ($COLUMN_ITEM) SELECT $COLUMN_ITEM FROM items_backup")
            db.execSQL("DROP TABLE items_backup")
        }
    }

    fun addItem(item: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM, item)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllItems(): List<String> {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, arrayOf(COLUMN_ITEM), null, null, null, null, null)
        val items = mutableListOf<String>()
        with(cursor) {
            while (moveToNext()) {
                val item = getString(getColumnIndexOrThrow(COLUMN_ITEM))
                items.add(item)
            }
            close()
        }
        return items
    }

    fun updateItem(id: Long, newItem: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM, newItem)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun deleteItem(id: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }
}