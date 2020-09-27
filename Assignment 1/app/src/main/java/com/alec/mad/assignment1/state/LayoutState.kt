package com.alec.mad.assignment1.state

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView

/**
 * Handles recyclerview orientation and span count changes. Uses the observer pattern
 * to allow classes to be notified on changes.
 */
class LayoutState(
    spanCount: Int = DEFAULT_SPAN_COUNT,
    @RecyclerView.Orientation orientation: Int = DEFAULT_ORIENTATION
) : ObservableState<LayoutStateObserver>(), Parcelable {

    companion object {
        const val HORIZONTAL = RecyclerView.HORIZONTAL
        const val VERTICAL = RecyclerView.VERTICAL

        // Defaults as defined by assignment spec
        private const val DEFAULT_ORIENTATION = VERTICAL
        private const val DEFAULT_SPAN_COUNT = 2

        // Parcelable implementation
        @JvmField
        val CREATOR = object : Parcelable.Creator<LayoutState> {
            override fun createFromParcel(parcel: Parcel): LayoutState {
                return LayoutState(parcel)
            }

            override fun newArray(size: Int): Array<LayoutState?> {
                return arrayOfNulls(size)
            }
        }
    }

    /**
     * The span count of the layout. If the layout is horizontal, this is the number of rows, and
     * if it is vertical, this is the number of columns.
     */
    var spanCount = spanCount
        set(value) {
            if (this.spanCount != value) {
                // Only apply changes
                field = value
                notifyObservers { it.onUpdateSpanCount(value) }
            }
        }

    /**
     * The orientation of the layout, either horizontal (left/right scrollable) or vertical
     * (up/down scrollable).
     */
    @RecyclerView.Orientation
    var orientation = orientation
        set(@RecyclerView.Orientation value) {
            if (value != HORIZONTAL && value != VERTICAL) {
                throw IllegalArgumentException("Invalid orientation '$value'")
            }
            if (this.orientation != value) {
                // Only apply changes
                field = value
                notifyObservers { it.onUpdateOrientation(value) }
            }
        }

    /**
     * The orientation of the layout from [orientation], but as an easily workable enum.
     */
    val orientationEnum
        get() = when (this.orientation) {
            HORIZONTAL -> Orientation.HORIZONTAL
            VERTICAL -> Orientation.VERTICAL
            else -> throw IllegalStateException("Invalid orientation '${this.orientation}'")
        }

    // Parcelable implementation
    private constructor(parcel: Parcel) : this(
        spanCount = parcel.readInt(),
        orientation = parcel.readInt()
    )

    /**
     * Swap the orientation.
     */
    fun swapOrientation() {
        this.orientation = when (this.orientation) {
            HORIZONTAL -> VERTICAL
            VERTICAL -> HORIZONTAL
            else -> throw IllegalStateException("Invalid orientation '$orientation'")
        }
    }

    // Parcelable implementation
    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.let {
        it.writeInt(this.spanCount)
        it.writeInt(this.orientation)
    }

    // Parcelable implementation
    override fun describeContents() = 0

    enum class Orientation {
        HORIZONTAL, VERTICAL
    }
}