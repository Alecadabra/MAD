package com.alec.mad.assignment1.model

import com.alec.mad.assignment1.fragment.selector.SelectorCellModel

class Question(
    private val qNum: Int,
    var points: Int, // Var to support isSpecial, which can modify a question's points value
    val penalty: Int,
    val isSpecial: Boolean,
    val questionText: String,
    val answers: List<String>,
    val correctAnswer: String
) : SelectorCellModel {

    var state = QuestionState.UNANSWERED

    val enabled: Boolean
        get() = this.state == QuestionState.UNANSWERED

    override val imageSrc: Int
        get() = when (this.enabled) {
            false -> android.R.color.background_light // Disabled
            true -> when (this.isSpecial) {
                true -> android.R.color.holo_orange_light // Enabled, is special
                false -> android.R.color.transparent // Enabled, isn't special
            }
        }

    override val attr1: String
        get() = "Q$qNum${if (this.isSpecial) "*" else ""}"

    override val attr2: String
        get() = when (this.state) {
            QuestionState.UNANSWERED -> "Points: $points"
            else -> "Answered"
        }

    override val attr3: String
        get() = when (this.state) {
            QuestionState.UNANSWERED -> "Penalty: $penalty"
            QuestionState.CORRECT -> "Correctly"
            QuestionState.INCORRECT -> "Incorrectly"
        }

    enum class QuestionState {
        UNANSWERED,
        CORRECT,
        INCORRECT
    }
}