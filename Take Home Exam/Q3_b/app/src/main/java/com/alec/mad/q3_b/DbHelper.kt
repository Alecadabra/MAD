package com.alec.mad.q3_b

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.CursorWrapper
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        createTable()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun createTable() {
        val cols = GameScoreInfoSchema.GameScoreInfoTable.Cols
        writableDatabase.execSQL(
            """
                CREATE TABLE ${GameScoreInfoSchema.GameScoreInfoTable.NAME} (
                    ${cols.ID} INTEGER,
                    ${cols.NAME} TEXT,
                    ${cols.SCORE} INTEGER
                )
            """.trimIndent()
        )
    }

    fun insert(info: GameScoreInfo) {
        val cols = GameScoreInfoSchema.GameScoreInfoTable.Cols
        writableDatabase.insert(
            GameScoreInfoSchema.GameScoreInfoTable.NAME,
            null,
            ContentValues().also { cv ->
                cv.put(cols.ID, info.id)
                cv.put(cols.NAME, info.name)
                cv.put(cols.SCORE, info.score)
            }
        )
    }

    data class ScoreAndName(val score: Int, val name: String)

    fun getScoresAndNames(): Collection<ScoreAndName> {
        val cols = GameScoreInfoSchema.GameScoreInfoTable.Cols
        class GameScoreInfoCursor(cursor: Cursor) : CursorWrapper(cursor) {
            val scoreAndName: ScoreAndName
                get() = ScoreAndName(
                    score = getInt(getColumnIndex(cols.SCORE)),
                    name = getString(getColumnIndex(cols.NAME))
                )
        }
        val scoreAndNames = mutableSetOf<ScoreAndName>()

        GameScoreInfoCursor(
            writableDatabase.query(
                GameScoreInfoSchema.GameScoreInfoTable.NAME, null, null, null, null, null, null
            )
        ).use { cur ->
            cur.moveToFirst()
            while (!cur.isAfterLast) {
                scoreAndNames.add(cur.scoreAndName)
                cur.moveToNext()
            }
        }

        return scoreAndNames
    }

    companion object {
        const val VERSION = 1
        const val DATABASE_NAME = "gamescoreinfo.db"
    }
}

class GameScoreInfo(val id: Int, val name: String, val score: Int = 0)