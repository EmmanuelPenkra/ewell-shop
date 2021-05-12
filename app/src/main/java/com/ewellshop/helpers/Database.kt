package com.ewellshop.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class Database (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_ORDERS_TABLE = "CREATE TABLE $ORDERS_TABLE (${BaseColumns._ID} INTEGER " +
                "PRIMARY KEY, id INTEGER, name TEXT, icon TEXT, rank INTEGER DEFAULT 0, position " +
                "INTEGER DEFAULT 0);"

        db.execSQL(CREATE_ORDERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        val DROP_ORDERS_TABLE = "DROP TABLE IF EXISTS $ORDERS_TABLE"

        db.execSQL(DROP_ORDERS_TABLE)

        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "self_destruct.db"

        const val ORDERS_TABLE = "orders"
    }
}