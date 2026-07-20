package com.example.mybrowser

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class HistoryItem(
    val id: Long = 0,
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HistoryDbHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "history.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_HISTORY = "history"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_URL = "url"
        private const val KEY_TIMESTAMP = "timestamp"

        @Volatile
        private var instance: HistoryDbHelper? = null

        fun getInstance(context: Context): HistoryDbHelper {
            return instance ?: synchronized(this) {
                instance ?: HistoryDbHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_HISTORY (" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$KEY_TITLE TEXT," +
                "$KEY_URL TEXT," +
                "$KEY_TIMESTAMP INTEGER)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db)
    }

    fun addHistory(title: String, url: String) {
        if (url == "about:blank") return
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_TITLE, title)
            put(KEY_URL, url)
            put(KEY_TIMESTAMP, System.currentTimeMillis())
        }
        db.insert(TABLE_HISTORY, null, values)
        // db.close() kaldırıldı - Singleton yönetimi için açık tutulur
    }

    fun getAllHistory(): List<HistoryItem> {
        val historyList = mutableListOf<HistoryItem>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_HISTORY, null, null, null, null, null, "$KEY_TIMESTAMP DESC")
        
        if (cursor.moveToFirst()) {
            do {
                val item = HistoryItem(
                    cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_URL)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TIMESTAMP))
                )
                historyList.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close() // Cursor kapatılmaya devam edilmeli
        // db.close() kaldırıldı
        return historyList
    }

    fun clearHistory() {
        val db = this.writableDatabase
        db.delete(TABLE_HISTORY, null, null)
        // db.close() kaldırıldı
    }
}