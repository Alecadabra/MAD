package com.alec.mad.assignment2.model

import androidx.annotation.DrawableRes
import com.alec.mad.assignment2.model.observer.GameMapObserver
import com.alec.mad.assignment2.model.observer.ObservableState
import com.alec.mad.assignment2.singleton.State
import kotlin.properties.Delegates.observable

class GameMap(
    private val map: List<List<MapElement>>
) : ObservableState<GameMapObserver>(), List<List<GameMap.MapElement>> by map {

    val lastRowIndex: Int
        get() = this.map.lastIndex

    val lastColIndex: Int
        get() = this.map[0].lastIndex

    operator fun get(i: Int, j: Int) = this.map[i][j].also { mapElement ->
        mapElement.observerCallback = {
            notifyObservers { it.onUpdateMapElement(i, j, mapElement) }
        }
    }

    class MapElement(
        structure: Structure?,
        @DrawableRes val bgImage: Int
    ) {
        var observerCallback: (() -> Unit)? = null

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
        fun getAdapterPosition(
            i: Int,
            j: Int,
            mapHeight: Int = State.gameData.settings.mapHeight
        ) = j * mapHeight + i

        fun getI(
            adapterPosition: Int,
            mapHeight: Int = State.gameData.settings.mapHeight
        ) = adapterPosition % mapHeight

        fun getJ(
            adapterPosition: Int,
            mapHeight: Int = State.gameData.settings.mapHeight
        ) = adapterPosition / mapHeight
    }
}