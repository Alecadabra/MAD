package com.alec.mad.assignment1.singleton

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView

/**
 * Handles recyclerview orientation and span count changes.
 */
class LayoutController(
    spanCount: Int = DEFAULT_SPAN_COUNT,
    @RecyclerView.Orientation orientation: Int = DEFAULT_ORIENTATION
) : Parcelable {

    companion object {
        const val HORIZONTAL = RecyclerView.HORIZONTAL
        const val VERTICAL = RecyclerView.VERTICAL

        // Defaults as defined by assignment spec
        private const val DEFAULT_ORIENTATION = VERTICAL
        private const val DEFAULT_SPAN_COUNT = 2

        @JvmField
        val CREATOR = object : Parcelable.Creator<LayoutController> {
            override fun createFromParcel(parcel: Parcel): LayoutController = LayoutController(parcel)
            override fun newArray(size: Int) = arrayOfNulls<LayoutController>(size)
        }
    }

    // Callbacks to the fragments
    val observers: MutableSet<LayoutControllerObserver> = mutableSetOf()

    var spanCount = spanCount
        set(value) {
            if (this.spanCount != value) {
                field = value
                this.observers.forEach { it.onUpdateSpanCount(value) }
            }
        }

    @RecyclerView.Orientation
    var orientation = orientation
        set(@RecyclerView.Orientation value) {
            if (value != HORIZONTAL && value != VERTICAL) {
                throw IllegalArgumentException("Invalid orientation '$value'")
            }
            if (this.orientation != value) {
                field = value
                this.observers.forEach { it.onUpdateOrientation(value) }
            }
        }

    val orientationEnum get() = when (this.orientation) {
        HORIZONTAL -> Orientation.HORIZONTAL
        VERTICAL -> Orientation.VERTICAL
        else -> throw IllegalStateException("Invalid orientation '${this.orientation}'")
    }

    private constructor(parcel: Parcel) : this(
        spanCount = parcel.readInt(),
        orientation = parcel.readInt()
    )

    fun swapOrientation() {
        this.orientation = when (this.orientation) {
            HORIZONTAL -> VERTICAL
            VERTICAL -> HORIZONTAL
            else -> throw IllegalStateException("Invalid orientation '$orientation'")
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.let {
        it.writeInt(this.spanCount)
        it.writeInt(this.orientation)
    }

    override fun describeContents() = 0
}

enum class Orientation {
    HORIZONTAL, VERTICAL
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