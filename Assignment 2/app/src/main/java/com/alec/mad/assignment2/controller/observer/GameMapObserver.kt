package com.alec.mad.assignment2.controller.observer

import com.alec.mad.assignment2.model.GameMap

/**
 * Implementing classes observe changes to a [GameMap] object.
 */
interface GameMapObserver {
    fun onUpdateMapElement(i: Int, j: Int, mapElement: GameMap.MapElement) {}
}