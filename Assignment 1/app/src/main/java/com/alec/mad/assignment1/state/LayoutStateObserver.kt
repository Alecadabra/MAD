package com.alec.mad.assignment1.state

import androidx.recyclerview.widget.RecyclerView

interface LayoutStateObserver {
    /**
     * Called when the orientation of the layout changes.
     */
    fun onUpdateOrientation(@RecyclerView.Orientation orientation: Int) {}

    /**
     * Called when the span count of the layout changes.
     */
    fun onUpdateSpanCount(spanCount: Int) {}
}