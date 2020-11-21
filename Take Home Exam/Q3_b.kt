// i)

fun createTable(db: SQLiteDatabase) {
    val cols = GameScoreInfoSchema.GameScoreInfoTable.Cols
    db.execSQL(
        """
            CREATE TABLE ${GameScoreInfoSchema.GameScoreInfoTable.NAME} (
                ${cols.ID} INTEGER,
                ${cols.NAME} TEXT,
                ${cols.SCORE} INTEGER
            )
        """.trimIndent()
    )
}

// ii)

fun insert(info: GameScoreInfo) {
    val cols = GameScoreInfoSchema.GameScoreInfoTable.Cols
    this.writableDatabase.insert(
        GameScoreInfoSchema.GameScoreInfoTable.NAME,
        null,
        ContentValues().also { cv ->
            cv.put(cols.ID, info.id)
            cv.put(cols.NAME, info.name)
            cv.put(cols.SCORE, info.score)
        }
    )
}

// iii)

// Essentially the kotlin Pair class, just holds a score and a name in an
// easily accessible way.
data class ScoreAndName(val score: Int, val name: String)

// Returns a collection of the ScoreAndName data class.
fun getScoresAndNames(): Collection<ScoreAndName> {

    val cols = GameScoreInfoSchema.GameScoreInfoTable.Cols

    // Cursor class
    class GameScoreInfoCursor(cursor: Cursor) : CursorWrapper(cursor) {
        val scoreAndName: ScoreAndName
            get() = ScoreAndName(
                score = getInt(getColumnIndex(cols.SCORE)),
                name = getString(getColumnIndex(cols.NAME))
            )
    }

    val scoreAndNames = mutableSetOf<ScoreAndName>()

    GameScoreInfoCursor(
        this.writableDatabase.query(
            GameScoreInfoSchema.GameScoreInfoTable.NAME,
            null, null, null, null, null, null
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