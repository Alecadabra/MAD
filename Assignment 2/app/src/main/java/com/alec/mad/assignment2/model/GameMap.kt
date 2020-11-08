package com.alec.mad.assignment2.model

import androidx.annotation.DrawableRes
import com.alec.mad.assignment2.controller.observer.GameMapObserver
import com.alec.mad.assignment2.controller.observer.ObservableState
import com.alec.mad.assignment2.singleton.State
import kotlin.properties.Delegates.observable

/**
 * Wrapper for a 2D list of [MapElement] to hold the game map.
 */
class GameMap(
    private val map: List<List<MapElement>>
) : ObservableState<GameMapObserver>(), List<List<GameMap.MapElement>> by map {

    val lastRowIndex: Int
        get() = this.map.lastIndex

    val lastColIndex: Int
        get() = this.map[0].lastIndex

    operator fun get(i: Int, j: Int) = this.map[i][j].also { mapElement ->
        // Also give the map element a callback to update it's observers if it is modified
        mapElement.observerCallback = {
            notifyObservers { it.onUpdateMapElement(i, j, mapElement) }
        }
    }

    /**
     * An element of the [GameMap]. Holds zero to one [Structure] and a background drawable.
     */
    class MapElement(
        structure: Structure?,
        @DrawableRes val bgImage: Int
    ) {
        /**
         * Callback to notify observers if changed.
         */
        var observerCallback: (() -> Unit)? = null

        /**
         * Structure placed at this map element, or null.
         */
        var structure: Structure? by observable(initialValue = structure) { _, _, _ ->
            observerCallback?.invoke()
        }
    }

    override fun notifyMe(them: GameMapObserver) {
        repeat(this.map.lastIndex) { i ->
            repeat(this.map[i].lastIndex) { j ->
                them.onUpdateMapElement(i, j, this[i, j])
            }
        }
    }

    companion object {
        /**
         * Get the adapter position equivalent value at a given i, j value pair. Based on the
         * map's height.
         */
        fun getAdapterPosition(
            i: Int,
            j: Int,
            mapHeight: Int = State.gameData.settings.mapHeight
        ) = j * mapHeight + i

        /**
         * Gets the 1st-dimension index equivalent value to a given adapter position value. Based
         * on the map's height.
         */
        fun getI(
            adapterPosition: Int,
            mapHeight: Int = State.gameData.settings.mapHeight
        ) = adapterPosition % mapHeight

        /**
         * Gets the 2nd-dimension index equivalent value to a given adapter position value. Based
         * on the map's height.
         */
        fun getJ(
            adapterPosition: Int,
            mapHeight: Int = State.gameData.settings.mapHeight
        ) = adapterPosition / mapHeight
    }
}