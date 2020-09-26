package com.alec.mad.assignment1.model

import com.alec.mad.assignment1.fragment.selector.SelectorCellModel

class Flag(
    name: String /* Not used */,
    val questions: List<Question>,
    override val imageSrc: Int,
) : SelectorCellModel {

    /**
     * A flag is enabled if it has any questions that are enabled.
     */
    val enabled: Boolean
        get() = questions.any { it.enabled }
}