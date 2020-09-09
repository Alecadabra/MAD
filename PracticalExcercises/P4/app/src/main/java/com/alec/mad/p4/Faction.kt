package com.alec.mad.p4

/**
 * Represents one of the various factions.
 */
class Faction(
    val id: Int = nextId,
    var name: String,
    var strength: Int = DEFAULT_STRENGTH,
    var relationship: Relationship = Relationship.DEFAULT
) {

    constructor(id: Int, name: String, strength: Int, relationship: Int) : this(
        id,
        name,
        strength,
        Relationship.from(relationship)
    )

    companion object {
        const val DEFAULT_STRENGTH = 100
        private var nextId = 0
    }

    /**
     * Increments the next ID
     */
    init {
        nextId = this.id + 1
    }

    enum class Relationship(val idx: Int) {
        ENEMY(0),
        NEUTRAL(1),
        ALLY(2),
        DEFAULT(NEUTRAL.idx);

        companion object {
            fun from(num: Int = -1) = when (num) {
                0 -> ENEMY
                1 -> NEUTRAL
                2 -> ALLY
                else -> DEFAULT
            }
        }
    }
}