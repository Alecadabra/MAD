package com.alec.mad.assignment1.fragment.selector

import android.graphics.Color

/**
 * Implementing classes can be represented in a AbstractSelectorFragment.
 */
interface SelectorCellModel {
    /**
     * Drawable id for the image button. Transparent by default
     */
    val imageSrc: Int
        get() = android.R.color.transparent

    /**
     * Background color int for the button. Use in conjunction with [android.graphics.Color].
     * Default button color is #d8d8d8.
     */
    val bgColor: Int
        get() = Color.parseColor("#d8d8d8")

    /**
     * Text to display in front of button. Empty string by default.
     */
    val text: String
        get() = ""

    /**
     * Color of [text]. [android.graphics.Color.DKGRAY] by default.
     */
    val textColor: Int
        get() = Color.DKGRAY
}