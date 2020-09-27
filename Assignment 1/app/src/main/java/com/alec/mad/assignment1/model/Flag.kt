package com.alec.mad.assignment1.model

import com.alec.mad.assignment1.fragment.selector.SelectorCellModel

/**
 * A flag for a country, holds questions about it and it's image drawable ID. Implements
 * [SelectorCellModel] so that it can be held in an AbstractSelectorFragment.
 */
class Flag(
    val questions: List<Question>,
    override val imageSrc: Int,
) : SelectorCellModel {

    /**
     * A flag is enabled if it has any questions that are enabled.
     */
    val enabled: Boolean
        get() = questions.any { it.enabled }
}