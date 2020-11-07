package com.alec.mad.assignment2.controller.database

object DatabaseSchema {
    object GameDataTable {
        const val NAME = "gameData"
        object Cols {
            const val GAME_TIME = "gameTime"
            const val MONEY = "money"
            const val INCOME = "income"
        }
    }
    object StructuresTable {
        const val NAME = "structures"
        object Cols {
            const val ADAPTER_POSITION = "adapterPosition"
            const val STRUCTURE_TYPE_ORDINAL = "structureTypeOrdinal"
            const val DRAWABLE_ID = "drawableId"
            const val STRUCTURE_NAME = "structureName"
        }
    }
    object SettingsTable {
        const val NAME = "settings"
        object Cols {
            const val MAP_WIDTH = "mapWidth"
            const val MAP_HEIGHT = "mapHeight"
            const val INITIAL_MONEY = "initialMoney"
        }
    }
}