package com.alec.mad.p4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.alec.mad.p4.FactionSchema.FactionTable

/**
 * Maintains the overall data set; specifically of course all the different factions.
 */
class FactionList(context: Context) {
    private val factions: MutableList<Faction> = mutableListOf()

    private val db: SQLiteDatabase = FactionListDbHelper(context.applicationContext).writableDatabase

    val size: Int get() = this.factions.size

    fun load() {
        // Get SELECT query
        val cursor = FactionCursor(db.query(
            FactionTable.NAME,
            null,
            null,
            null,
            null,
            null,
            null
        ))

        // Clear the list if needed
        if (this.factions.size != 0) {
            this.factions.clear()
        }

        // Iterate through the cursor, filling the list
        cursor.use {
            it.moveToFirst()
            while (!it.isAfterLast) {
                this.factions.add(it.faction)
                it.moveToNext()
            }
        }
    }

    operator fun get(i: Int): Faction = this.factions[i]

    fun add(newFaction: Faction): Int {
        factions.add(newFaction)
        db.insert(
            FactionTable.NAME,
            null,
            ContentValues().apply {
                put(FactionTable.Cols.ID, newFaction.id)
                put(FactionTable.Cols.NAME, newFaction.name)
                put(FactionTable.Cols.STRENGTH, newFaction.strength)
                put(FactionTable.Cols.RELATIONSHIP, newFaction.relationship.idx)
            }
        )
        return factions.size - 1 // Return insertion point
    }

    fun edit(newFaction: Faction) {
        db.update(
            FactionTable.NAME,
            ContentValues().apply {
                put(FactionTable.Cols.ID, newFaction.id)
                put(FactionTable.Cols.NAME, newFaction.name)
                put(FactionTable.Cols.STRENGTH, newFaction.strength)
                put(FactionTable.Cols.RELATIONSHIP, newFaction.relationship.idx)
            },
            "${FactionTable.Cols.ID} = ?",
            arrayOf(newFaction.id.toString())
        )
    }

    fun remove(rmFaction: Faction) {
        // Remove from list
        factions.remove(rmFaction)

        // Update DB
        db.delete(
            FactionTable.NAME,
            "${FactionTable.Cols.ID} = ?",
            arrayOf(rmFaction.id.toString())
        )
    }
}