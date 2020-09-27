package com.alec.mad.assignment1.model

import android.graphics.Color
import com.alec.mad.assignment1.fragment.selector.SelectorCellModel

/**
 * A question for a [Flag].
 */
class Question(
    private val qNum: Int,
    var points: Int, // Var to support isSpecial, which can modify a question's points value
    val penalty: Int,
    val isSpecial: Boolean,
    val questionText: String,
    val answers: List<String>,
    val correctAnswer: String
) : SelectorCellModel {

    companion object {
        val COLOR_CORRECT = Color.parseColor("#7bdbc3")
        val COLOR_INCORRECT = Color.parseColor("#e09999")
        val COLOR_SPECIAL = Color.parseColor("#f5ec8c")
        const val COLOR_TEXT_DISABLED = Color.GRAY
    }

    var state = QuestionState.UNANSWERED

    val enabled: Boolean
        get() = this.state == QuestionState.UNANSWERED

    override val bgColor: Int
        get() = when (this.state) {
            QuestionState.UNANSWERED -> when (this.isSpecial) {
                true -> COLOR_SPECIAL // Enabled, is special
                false -> super.bgColor // Enabled, isn't special
            }
            QuestionState.CORRECT -> COLOR_CORRECT
            QuestionState.INCORRECT -> COLOR_INCORRECT
        }

    override val text: String
        get() = StringBuilder().let { builder ->
            builder.appendLine("Q$qNum")
            if (this.isSpecial) {
                builder.appendLine("(Special)")
            }
            builder.appendLine(
                when (this.state) {
                    QuestionState.UNANSWERED -> "Points: ${this.points}"
                    else -> "Answered"
                }
            )
            builder.appendLine(
                when (this.state) {
                    QuestionState.UNANSWERED -> "Penalty: ${this.penalty}"
                    QuestionState.CORRECT -> "Correctly"
                    QuestionState.INCORRECT -> "Incorrectly"
                }
            )
            builder.toString()
        }

    override val textColor: Int
        get() = when (this.state) {
            QuestionState.UNANSWERED -> super.textColor
            else -> COLOR_TEXT_DISABLED
        }

    enum class QuestionState {
        UNANSWERED,
        CORRECT,
        INCORRECT
    }
}