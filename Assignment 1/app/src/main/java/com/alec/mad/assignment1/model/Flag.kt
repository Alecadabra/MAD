package com.alec.mad.assignment1.model

import com.alec.mad.assignment1.fragment.selector.SelectorCellModel

class Flag(
    val name: String,
    val questions: List<Question>,
    override val imageSrc: Int,
) : SelectorCellModel {
    override val enabled: Boolean get() = questions.any { it.enabled }
}