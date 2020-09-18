package com.alec.mad.assignment1.singleton

import androidx.recyclerview.widget.RecyclerView

/**
 * Handles recyclerview orientation and span count changes.
 */
object LayoutController {
    const val HORIZONTAL = RecyclerView.HORIZONTAL
    const val VERTICAL = RecyclerView.VERTICAL

    private const val DEFAULT_ORIENTATION = VERTICAL
    private const val DEFAULT_SPAN_COUNT = 3

    // Callbacks to the fragments
    val observers: MutableSet<LayoutControllerObserver> = mutableSetOf()

    var spanCount = DEFAULT_SPAN_COUNT
        set(value) {
            if (this.spanCount != value) {
                field = value
                this.observers.forEach { it.onUpdateSpanCount(value) }
            }
        }

    @RecyclerView.Orientation
    var orientation = DEFAULT_ORIENTATION
        set(@RecyclerView.Orientation value) {
            if (value != HORIZONTAL && value != VERTICAL) {
                throw IllegalArgumentException("Invalid orientation '$value'")
            }
            if (this.orientation != value) {
                field = value
                this.observers.forEach { it.onUpdateOrientation(value) }
            }
        }

    fun swapOrientation() {
        this.orientation = when (this.orientation) {
            HORIZONTAL -> VERTICAL
            VERTICAL -> HORIZONTAL
            else -> throw IllegalStateException("Invalid orientation '$orientation'")
        }
    }
}

interface LayoutControllerObserver {
    /**
     * Called when the orientation of the layout changes.
     *
     * @param orientation The new orientation
     */
    fun onUpdateOrientation(@RecyclerView.Orientation orientation: Int) { }

    /**
     * Called when the span count of the layout changes.
     *
     * @param spanCount The new span count
     */
    fun onUpdateSpanCount(spanCount: Int) { }
}