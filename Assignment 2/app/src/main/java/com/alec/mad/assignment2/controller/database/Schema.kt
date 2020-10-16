package com.alec.mad.assignment2.controller.database

object Schema {
    object SettingsTable {
        const val NAME = "settings"
        object Cols {
            const val MAP_WIDTH = "mapWidth"
            const val MAP_HEIGHT = "mapHeight"
            const val INITIAL_MONEY = "initialMoney"
        }
    }
    object StructuresTable {
        const val NAME = "structures"
        object Cols {
            const val I = "i"
            const val J = "j"
            const val STRUCTURE_TYPE_ORDINAL = "structureTypeOrdinal"
            const val DRAWABLE_ID = "drawableId"
            const val STRUCTURE_NAME = "structureName"
        }
    }
}