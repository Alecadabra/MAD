package com.alec.mad.assignment2.model.observer

import com.alec.mad.assignment2.model.GameMap

interface GameMapObserver {
    fun onUpdateMapElement(i: Int, j: Int, mapElement: GameMap.MapElement) {}
}