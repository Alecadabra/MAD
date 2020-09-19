package com.alec.mad.assignment1.model

import com.alec.mad.assignment1.fragment.selector.SelectorCellModel

class Question(
    val qNum: Int,
    var points: Int, // Var to support isSpecial, which can modify a question's points value
    val penalty: Int,
    val isSpecial: Boolean,
    val questionText: String,
    val answers: List<String>,
    val correctAnswer: String,
    override var enabled: Boolean = true
) : SelectorCellModel {
    override val imageSrc: Int get() = when (enabled) {
        false -> android.R.color.background_dark // Disabled
        true -> when (isSpecial) {
            true -> android.R.color.holo_orange_light // Enabled, is special
            false -> android.R.color.background_light // Enabled, isn't special
        }
    }
    override val attr1: String get() = "Q$qNum"
    override val attr2: String get() = "Points: $points"
    override val attr3: String get() = "Penalty: $penalty"
}