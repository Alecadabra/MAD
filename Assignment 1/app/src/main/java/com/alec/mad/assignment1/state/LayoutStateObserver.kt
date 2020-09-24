package com.alec.mad.assignment1.state

import androidx.recyclerview.widget.RecyclerView

interface LayoutStateObserver {
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