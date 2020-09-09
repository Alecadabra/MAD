package com.alec.mad.p4

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.alec.mad.p4.FactionSchema.FactionTable

class FactionListDbHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, VERSION) {
    companion object {
        const val VERSION = 1
        const val DB_NAME = "factions.db"
    }


    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("""
                    |CREATE TABLE ${FactionTable.NAME} (
                    |    ${FactionTable.Cols.ID} INTEGER,
                    |    ${FactionTable.Cols.NAME} TEXT,
                    |    ${FactionTable.Cols.STRENGTH} INTEGER,
                    |    ${FactionTable.Cols.RELATIONSHIP} INTEGER
                    |)
                    """.trimMargin("|")
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        // Database on first version
    }
}