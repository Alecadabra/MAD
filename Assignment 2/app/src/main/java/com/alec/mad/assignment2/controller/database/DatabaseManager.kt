package com.alec.mad.assignment2.controller.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.CursorWrapper
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.alec.mad.assignment2.controller.database.DatabaseSchema.GameDataTable
import com.alec.mad.assignment2.controller.database.DatabaseSchema.SettingsTable
import com.alec.mad.assignment2.controller.database.DatabaseSchema.StructuresTable
import com.alec.mad.assignment2.model.GameMap
import com.alec.mad.assignment2.model.StructureType
import com.alec.mad.assignment2.controller.observer.GameDataObserver
import com.alec.mad.assignment2.controller.observer.GameMapObserver
import com.alec.mad.assignment2.controller.observer.ObservableState
import com.alec.mad.assignment2.controller.observer.SettingsObserver
import com.alec.mad.assignment2.singleton.State

/**
 * Handles database queries, observes changes to the overall game state and updates the database
 * for changes to mutable settings, structure placements on the map and persistable game data
 * changes.
 */
class DatabaseManager(context: Context?) : GameDataObserver, GameMapObserver, SettingsObserver {

    private val dbHelper = DatabaseHelper(context)
    private val db = this.dbHelper.writableDatabase

    val gameDataCursor: GameDataCursor
        get() = GameDataCursor(db.query(GameDataTable.NAME, null, null, null, null, null, null))

    val structuresCursor: StructuresCursor
        get() = StructuresCursor(db.query(StructuresTable.NAME, null, null, null, null, null, null))

    val settingsCursor: SettingsCursor
        get() = SettingsCursor(db.query(SettingsTable.NAME, null, null, null, null, null, null))

    fun observe() {
        // Register to observers, and look cool doing it
        setOf<ObservableState<in DatabaseManager>>(
            State.gameData, State.gameData.gameMap, State.gameData.settings
        ).forEach { observableState ->
            observableState.observers.add(this@DatabaseManager)
            observableState.notifyMe(this@DatabaseManager)
        }
    }

    fun clearGameDataTable() {
        this.db.delete(GameDataTable.NAME, null, null)
    }

    fun clearStructuresTable() {
        this.db.delete(StructuresTable.NAME, null, null)
    }

    private fun clearSettingsTable() {
        this.db.delete(SettingsTable.NAME, null, null)
    }

    override fun onUpdateMapElement(i: Int, j: Int, mapElement: GameMap.MapElement) {
        val structure = mapElement.structure
        if (structure != null) {
            // Add structure to database at this adapter position
            this@DatabaseManager.db.replace(
                StructuresTable.NAME,
                null,
                ContentValues().also { cv ->
                    cv.put(
                        StructuresTable.Cols.ADAPTER_POSITION,
                        GameMap.getAdapterPosition(i, j)
                    )
                    cv.put(StructuresTable.Cols.DRAWABLE_ID, structure.imageId)
                    cv.put(
                        StructuresTable.Cols.STRUCTURE_TYPE_ORDINAL,
                        structure.structureType.ordinal
                    )
                    cv.put(StructuresTable.Cols.STRUCTURE_NAME, structure.name)
                }
            )
        } else {
            // Delete this structure in the database
            this@DatabaseManager.db.delete(
                StructuresTable.NAME,
                "${StructuresTable.Cols.ADAPTER_POSITION} = ?",
                arrayOf(GameMap.getAdapterPosition(i, j).toString())
            )
        }
    }

    override fun onUpdateIslandName(islandName: String) { updateSettingsTable() }
    override fun onUpdateMapWidth(mapWidth: Int) { updateSettingsTable() }
    override fun onUpdateMapHeight(mapHeight: Int) { updateSettingsTable() }
    override fun onUpdateInitialMoney(initialMoney: Int) { updateSettingsTable() }

    /**
     * Updates the database settings table, called when changes are made to the settings.
     */
    private fun updateSettingsTable() {
        val settings = State.gameData.settings
        setOf(
            SettingsTable.Cols.MAP_WIDTH to settings.mapWidth,
            SettingsTable.Cols.MAP_HEIGHT to settings.mapHeight,
            SettingsTable.Cols.INITIAL_MONEY to settings.initialMoney
        )
        clearSettingsTable()
        this@DatabaseManager.db.insert(
            SettingsTable.NAME,
            null,
            ContentValues().also { cv ->
                cv.put(SettingsTable.Cols.ISLAND_NAME, settings.islandName)
                cv.put(SettingsTable.Cols.MAP_WIDTH, settings.mapWidth)
                cv.put(SettingsTable.Cols.MAP_HEIGHT, settings.mapHeight)
                cv.put(SettingsTable.Cols.INITIAL_MONEY, settings.initialMoney)
            }
        )
    }

    override fun onUpdateGameTime(gameTime: Int) {
        updateGameDataTable(gameTime = gameTime)
    }

    override fun onUpdateIncome(income: Int) {
        updateGameDataTable(income = income)
    }

    override fun onUpdateMoney(money: Int) {
        updateGameDataTable(money = money)
        if (money < 0) {
            // Lose condition
            clearGameDataTable()
            clearStructuresTable()
        }
    }

    /**
     * Updates the game data table, called when changes are made to persistent parts of the
     * game data.
     */
    private fun updateGameDataTable(
        gameTime: Int = State.gameData.gameTime,
        money: Int = State.gameData.money,
        income: Int = State.gameData.income
    ) {
        // Values to store in content values
        val cols = setOf(
            GameDataTable.Cols.GAME_TIME to gameTime,
            GameDataTable.Cols.MONEY to money,
            GameDataTable.Cols.INCOME to income
        )
        clearGameDataTable()
        this@DatabaseManager.db.insert(
            GameDataTable.NAME,
            null,
            ContentValues().also { cv -> cols.forEach { cv.put(it.first, it.second) } }
        )
    }

    /**
     * DatabaseHelper Implementation - creates tables
     */
    private inner class DatabaseHelper(
        context: Context?
    ) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        override fun onCreate(database: SQLiteDatabase?) {
            // Assert non null
            database ?: error("Null database reference")

            database.execSQL(
                """
                    CREATE TABLE ${StructuresTable.NAME} (
                        ${StructuresTable.Cols.ADAPTER_POSITION} INTEGER PRIMARY KEY,
                        ${StructuresTable.Cols.STRUCTURE_TYPE_ORDINAL} INTEGER,
                        ${StructuresTable.Cols.DRAWABLE_ID} INTEGER,
                        ${StructuresTable.Cols.STRUCTURE_NAME} TEXT
                    )
                """.trimIndent()
            )

            database.execSQL(
                """
                    CREATE TABLE ${SettingsTable.NAME} (
                        ${SettingsTable.Cols.ISLAND_NAME} TEXT,
                        ${SettingsTable.Cols.MAP_WIDTH} INTEGER,
                        ${SettingsTable.Cols.MAP_HEIGHT} INTEGER,
                        ${SettingsTable.Cols.INITIAL_MONEY} INTEGER
                    )
                """.trimIndent()
            )

            database.execSQL(
                """
                    CREATE TABLE ${GameDataTable.NAME} (
                        ${GameDataTable.Cols.GAME_TIME} INTEGER,
                        ${GameDataTable.Cols.MONEY} INTEGER,
                        ${GameDataTable.Cols.INCOME} INTEGER
                    )
                """.trimIndent()
            )
        }

        override fun onUpgrade(database: SQLiteDatabase?, p1: Int, p2: Int) {}
    }

    /**
     * Interface to retrieve the persistent game data from the database
     */
    class GameDataCursor(cursor: Cursor) : CursorWrapper(cursor) {
        val gameTime: Int
            get() = getInt(getColumnIndex(GameDataTable.Cols.GAME_TIME))

        val money: Int
            get() = getInt(getColumnIndex(GameDataTable.Cols.MONEY))

        val income: Int
            get() = getInt(getColumnIndex(GameDataTable.Cols.INCOME))
    }

    /**
     * Interface to retrieve the built structures info from the database
     */
    class StructuresCursor(cursor: Cursor) : CursorWrapper(cursor) {
        val adapterPosition: Int
            get() = getInt(getColumnIndex(StructuresTable.Cols.ADAPTER_POSITION))

        val structureType: StructureType
            get() = StructureType.values()[getInt(getColumnIndex(StructuresTable.Cols.STRUCTURE_TYPE_ORDINAL))]

        val drawableId: Int
            get() = getInt(getColumnIndex(StructuresTable.Cols.DRAWABLE_ID))

        val structureName: String
            get() = getString(getColumnIndex(StructuresTable.Cols.STRUCTURE_NAME))
    }

    /**
     * Interface to retrieve the persistent settings from the database
     */
    class SettingsCursor(cursor: Cursor) : CursorWrapper(cursor) {
        val islandName: String
            get() = getString(getColumnIndex(SettingsTable.Cols.ISLAND_NAME))

        val mapWidth: Int
            get() = getInt(getColumnIndex(SettingsTable.Cols.MAP_WIDTH))

        val mapHeight: Int
            get() = getInt(getColumnIndex(SettingsTable.Cols.MAP_HEIGHT))

        val initialMoney: Int
            get() = getInt(getColumnIndex(SettingsTable.Cols.INITIAL_MONEY))
    }

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "citySim.db"
    }
}