package com.alec.mad.p4

import android.database.Cursor
import android.database.CursorWrapper
import com.alec.mad.p4.FactionSchema.FactionTable

class FactionCursor(cursor: Cursor) : CursorWrapper(cursor) {
    val faction: Faction
        get() = Faction(
            id = getInt(getColumnIndex(FactionTable.Cols.ID)),
            name = getString(getColumnIndex(FactionTable.Cols.NAME)),
            strength = getInt(getColumnIndex(FactionTable.Cols.STRENGTH)),
            relationship = getInt(getColumnIndex(FactionTable.Cols.RELATIONSHIP))
        )
}