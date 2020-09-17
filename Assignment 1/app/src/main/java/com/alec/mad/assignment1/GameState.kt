package com.alec.mad.assignment1

import com.alec.mad.assignment1.model.Flag
import kotlin.properties.Delegates.observable
import kotlin.random.Random
import kotlin.reflect.KProperty

object GameState {
    /**
     * Immutable storage for flags.
     *
     * @see com.alec.mad.assignment1.model.Flag
     */
    val flags: List<Flag> = Init.initFlags()

    /**
     * The player's mutable point count. Changes are observed and used to update playerCondition.
     *
     * @see com.alec.mad.assignment1.GameState.playerCondition
     */
    var playerPoints: Int = Init.initPlayerPoints()
        set(value) {
            // Update player condition
            this.playerCondition = when {
                value <= 0                 -> PlayerCondition.LOST
                value >= this.targetPoints -> PlayerCondition.WON
                else                       -> PlayerCondition.PLAYING
            }

            field = value
        }

    /**
     * No. of points to reach to win.
     *
     * @see com.alec.mad.assignment1.GameState.playerPoints
     */
    val targetPoints: Int = Init.initTargetPoints(playerPoints)

    /**
     * The player's state. Automatically set when playerPoints is changed.
     *
     * @see com.alec.mad.assignment1.GameState.PlayerCondition
     * @see com.alec.mad.assignment1.GameState.playerPoints
     */
    var playerCondition: PlayerCondition = PlayerCondition.PLAYING
        private set

    /**
     * The player's state, either PLAYING (If normally playing the game), WON (If reached the
     * targetPoints) or LOST (If decreased points to 0). This is automatically updated when
     * playerPoints is updated.
     */
    enum class PlayerCondition {
        PLAYING,
        WON,
        LOST;

        override fun toString(): String = when (this) {
            PLAYING -> "Game in progress"
            WON -> "You have won!"
            LOST -> "You lost :("
        }
    }

    /**
     * Used to initialise the game state
     */
    private object Init {
        private const val STARTING_POINTS_LOWER = 0
        private const val STARTING_POINTS_UPPER = 10
        private const val TARGET_POINTS_UPPER = 20

        fun initFlags(): List<Flag> = listOf(
            Flag(listOf(), R.drawable.flag_au),
            Flag(listOf(), R.drawable.flag_bg),
            Flag(listOf(), R.drawable.flag_ca),
            Flag()
        )

        fun initPlayerPoints(): Int = Random.nextInt(
            STARTING_POINTS_LOWER, STARTING_POINTS_UPPER
        )

        fun initTargetPoints(playerPoints: Int): Int = Random.nextInt(
            playerPoints, TARGET_POINTS_UPPER
        )
    }
}