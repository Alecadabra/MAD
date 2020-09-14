package com.alec.mad.p4

object FactionSchema {
    object FactionTable {
        const val NAME = "factions"
        object Cols {
            const val ID = "faction_id"
            const val NAME = "name"
            const val STRENGTH = "strength"
            const val RELATIONSHIP = "relationship"
        }
    }
}