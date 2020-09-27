package com.alec.mad.assignment1.state

/**
 * The observer pattern; implementing classes can subscribe to changes to playerPoints and/or
 * playerCondition.
 */
interface GameStateObserver {
    /**
     * Called when the value for playerPoints is changed.
     */
    fun onUpdatePlayerPoints(playerPoints: Int) {}

    /**
     * Called when the player condition changes.
     */
    fun onUpdatePlayerCondition(playerCondition: GameState.PlayerCondition) {}
}