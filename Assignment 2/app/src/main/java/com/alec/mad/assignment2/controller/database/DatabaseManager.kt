package com.alec.mad.assignment2.controller.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.alec.mad.assignment2.controller.database.Schema.SettingsTable
import com.alec.mad.assignment2.controller.database.Schema.StructuresTable
import java.lang.IllegalStateException

class DatabaseManager {

    inner class DatabaseHelper(
        context: Context?
    ) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        override fun onCreate(database: SQLiteDatabase?) {
            // Assert non null
            database ?: throw IllegalStateException("Null database reference")
            database.execSQL(
                """
                    CREATE TABLE ${SettingsTable.NAME} (
                        ${SettingsTable.Cols.MAP_WIDTH} INTEGER,
                        ${SettingsTable.Cols.MAP_HEIGHT} INTEGER,
                        ${SettingsTable.Cols.INITIAL_MONEY} INTEGER
                    )
                """.trimIndent()
            )
            database.execSQL(
                """
                    CREATE TABLE ${StructuresTable.NAME} (
                        ${StructuresTable.Cols.I} INTEGER,
                        ${StructuresTable.Cols.J} INTEGER,
                        ${StructuresTable.Cols.STRUCTURE_TYPE_ORDINAL} INTEGER,
                        ${StructuresTable.Cols.DRAWABLE_ID} INTEGER,
                        ${StructuresTable.Cols.STRUCTURE_NAME} TEXT
                    )
                """.trimIndent()
            )
        }

        override fun onUpgrade(database: SQLiteDatabase?, p1: Int, p2: Int) {
            // No upgrades
        }
    }

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "citySim.db"
    }
}