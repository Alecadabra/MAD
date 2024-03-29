package com.alec.mad.assignment1

import com.alec.mad.assignment1.state.GameState

/**
 * Simply holds a static reference to the current game state for simplicity.
 */
object GameSingleton {
    /**
     * The current game state.
     */
    var state = GameState()
        private set

    /**
     * Reset the game.
     */
    fun reset() {
        // Maintain observers
        val observers = state.observers
        this.state = GameState(observers)
    }
}