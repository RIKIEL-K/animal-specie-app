package com.example.projetfinsession

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BddHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "species.DB"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "specie"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_STATUS = "status"
        const val COLUMN_TEMP_MAX = "temperature_max"
        const val COLUMN_TEMP_MIN = "temperature_min"
        const val COLUMN_HUMIDITY_MIN = "humidity_min"
        const val COLUMN_HUMIDITY_MAX = "humidity_max"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_POPULATION = "population"

        private const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME ("+
                "$COLUMN_ID TEXT PRIMARY KEY,"+
                "$COLUMN_NAME TEXT,"+
                "$COLUMN_DESCRIPTION TEXT,"+
                "$COLUMN_STATUS TEXT,"+
                "$COLUMN_TEMP_MAX REAL,"+
                "$COLUMN_TEMP_MIN REAL,"+
                "$COLUMN_HUMIDITY_MIN REAL,"+
                "$COLUMN_HUMIDITY_MAX REAL,"+
                "$COLUMN_LONGITUDE TEXT,"+
                "$COLUMN_POPULATION REAL,"+
                "$COLUMN_LATITUDE TEXT)"
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

}