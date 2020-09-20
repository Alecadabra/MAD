package com.alec.mad.assignment1.fragment.selector

/**
 * Implementing classes can be represented in a AbstractSelectorFragment
 */
interface SelectorCellModel {
    val imageSrc: Int get() = android.R.color.transparent
    val attr1: String get() = ""
    val attr2: String get() = ""
    val attr3: String get() = ""
    val enabled: Boolean get() = true
}