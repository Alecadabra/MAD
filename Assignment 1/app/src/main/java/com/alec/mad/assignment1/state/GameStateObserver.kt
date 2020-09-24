package com.alec.mad.assignment1.state

interface GameStateObserver {
    fun onUpdatePlayerPoints(playerPoints: Int) { }

    fun onUpdatePlayerCondition(playerCondition: GameState.PlayerCondition) { }
}