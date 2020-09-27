package com.alec.mad.assignment1.fragment.selector.derived

import android.widget.Toast
import com.alec.mad.assignment1.GameSingleton
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.fragment.selector.AbstractSelectorFragment
import com.alec.mad.assignment1.fragment.selector.SelectorCellModel
import com.alec.mad.assignment1.model.Question
import com.alec.mad.assignment1.model.Question.QuestionState
import com.alec.mad.assignment1.state.GameState

/**
 * Uses a AbstractSelectorFragment to show possible answers to a given question.
 */
class AnswerSelectorFragment(
    private val question: Question,
    useBackButton: Boolean = true,
    useDynamicLayout: Boolean = false,
    /** Callback to the question selector for the landscape tablet special case */
    private val landTabletCaseCallback: (() -> Unit)? = null
) : AbstractSelectorFragment<AnswerSelectorFragment.AnswerWrapper>(
    useBackButton,
    useDynamicLayout
) {

    init {
        // Because the default constructor has parameters, which android does not like.
        this.retainInstance = true
    }

    override val values: List<AnswerWrapper> = this.question.answers.map { AnswerWrapper(it) }
    override val title: String = this.question.questionText // Display the question text

    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: AnswerWrapper) {
        super.bindViewHolder(holder, item)

        holder.imageButton.setOnClickListener {
            // Only trigger if the question isn't already answered
            if (this.question.state == QuestionState.UNANSWERED) {
                // True iff the answer was correct
                val correct = item.text == this.question.correctAnswer

                val toastText = when (correct) {
                    true -> "Correct! You earned ${this.question.points} points!"
                    false -> "Incorrect, You lost ${this.question.penalty} points :("
                }
                Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()

                // Set this view's state
                item.answerState = if (correct) AnswerState.CORRECT else AnswerState.INCORRECT

                // Set this question's state accordingly
                this.question.state =
                    if (correct) QuestionState.CORRECT else QuestionState.INCORRECT

                // Make the button green or red
                holder.imageButton.setBackgroundColor(item.bgColor)

                // Adjust player's points accordingly
                GameSingleton.state.playerPoints = when (correct) {
                    true -> GameSingleton.state.playerPoints + this.question.points
                    false -> GameSingleton.state.playerPoints - this.question.penalty
                }

                // Handle landscape tablet special case
                if (resources.getBoolean(R.bool.isLandscapeTablet)) {
                    // Callback to the question selector to update it's views
                    this.landTabletCaseCallback?.invoke()
                }

                // Handle isSpecial
                if (
                    correct &&
                    this.question.isSpecial &&
                    // If they haven't already won from this question
                    GameSingleton.state.playerCondition == GameState.PlayerCondition.PLAYING
                ) {
                    fragmentManager?.beginTransaction()?.also { transaction ->
                        transaction.replace(
                            R.id.gameFragmentFrame,
                            FlagSpecialSelectorFragment()
                        )

                        transaction.addToBackStack(null)

                        transaction.commit()
                    }
                }
            }
        }
    }

    /**
     * Adapts a string representing a question's possible answer to be compatible with an
     * AbstractSelectorFragment list. ALso holds information on the state of the answer.
     */
    class AnswerWrapper(answerText: String) : SelectorCellModel {

        override val text = answerText

        var answerState = AnswerState.UNSELECTED

        override val textColor: Int
            get() = when (this.answerState) {
                AnswerState.UNSELECTED -> super.textColor
                else -> Question.COLOR_TEXT_DISABLED
            }

        override val bgColor: Int
            get() = when (this.answerState) {
                AnswerState.UNSELECTED -> super.bgColor
                AnswerState.CORRECT -> Question.COLOR_CORRECT
                AnswerState.INCORRECT -> Question.COLOR_INCORRECT
            }
    }

    enum class AnswerState {
        UNSELECTED,
        CORRECT,
        INCORRECT
    }
}