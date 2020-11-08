package com.alec.mad.assignment2.singleton

import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.controller.database.DatabaseManager
import com.alec.mad.assignment2.model.*

/**
 * Globally accessed singleton that holds the game state reference when the game is running.
 * Also handles initialising said game state for a new game or resumed game
 */
object State {
    lateinit var gameData: GameData

    fun initialiseNewGame(settings: Settings, database: DatabaseManager) {
        // Reset saved game state
        database.clearGameDataTable()
        database.clearStructuresTable()

        // Generate blank map with no pre-existing structures
        val gameMap = generateGameMap(emptyMap(), settings)

        // Build the game data object
        val localGameData = GameData(settings = settings, gameMap = gameMap)

        // Update the singleton's game data reference
        this.gameData = localGameData

        // Have database start listening to changes
        database.observe()
    }

    fun initialiseContinueGame(settings: Settings, database: DatabaseManager) {
        // Map adapter positions to pre-existing structures from the database
        val existingStructures = mutableMapOf<Int, Structure>()
        database.structuresCursor.use { cursor ->
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                existingStructures[cursor.adapterPosition] = ImageIDStructure(
                    structureType = cursor.structureType,
                    imageId = cursor.drawableId,
                    name = cursor.structureName
                )
                cursor.moveToNext()
            }
        }
        // Generate the map with resolved structures
        val gameMap = generateGameMap(existingStructures, settings)

        // Build the game data object
        val localGameData = GameData(
            settings = settings,
            gameMap = gameMap
        )
        // Update game state from the database
        database.gameDataCursor.use { cursor ->
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                if (cursor.count > 1) {
                    error("Game data cursor contains multiple tuples (${cursor.count})")
                }
                localGameData.gameTime = cursor.gameTime
                localGameData.money = cursor.money
                localGameData.income = cursor.income
                cursor.moveToNext()
            }
        }

        // Update the singleton's game data reference
        this.gameData = localGameData

        // Have database start listening to changes
        database.observe()
    }

    private fun generateGameMap(structures: Map<Int, Structure>, settings: Settings): GameMap {
        // Background grass drawables
        val bgDrawables = setOf(
            R.drawable.ic_grass1,
            R.drawable.ic_grass2,
            R.drawable.ic_grass3,
            R.drawable.ic_grass4
        )
        // Build map, adding structures from database, or null if no entry for that tile
        return GameMap(
            List(settings.mapHeight) { i ->
                List(settings.mapWidth) { j ->
                    GameMap.MapElement(
                        structure = structures[
                                GameMap.getAdapterPosition(i, j, settings.mapHeight)
                        ],
                        bgImage = bgDrawables.random()
                    )
                }
            }
        )
    }
}