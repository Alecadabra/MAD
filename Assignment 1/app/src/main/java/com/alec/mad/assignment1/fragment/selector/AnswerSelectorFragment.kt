package com.alec.mad.assignment1.fragment.selector

import com.alec.mad.assignment1.GameStateSingleton
import com.alec.mad.assignment1.gameState
import com.alec.mad.assignment1.model.Question
import com.alec.mad.assignment1.model.Question.State
import com.alec.mad.assignment1.state.GameState

class AnswerSelectorFragment(private val question: Question) :
    AbstractSelectorFragment<AnswerSelectorFragment.AnswerWrapper>() {

    override val values: List<AnswerWrapper> = this.question.answers.map { AnswerWrapper(it) }
    override val title: String = this.question.questionText

    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: AnswerWrapper) {
        super.bindViewHolder(holder, item)

        holder.imageButton.setOnClickListener {
            // True iff the answer was correct
            val correct = item.attr1 == this.question.correctAnswer

            // Make the button green or red
            holder.imageButton.setImageResource(when (correct) {
                    true -> android.R.color.holo_green_light
                    false -> android.R.color.holo_red_light
                }
            )

            // Adjust player's points accordingly
            this.gameState.also { gameState ->
                gameState.playerPoints = when (correct) {
                    true -> gameState.playerPoints + this.question.points
                    false -> gameState.playerPoints - this.question.penalty
                }
            }

            // Set this question's state accordingly
            this.question.state = if (correct) State.CORRECT else State.INCORRECT

            if (correct && this.question.isSpecial) {
                // TODO isSpecial logic
            }

            // If the player hasn't won or lost, pop the back stack to go back to the question
            // selection screen
            if (this.gameState.playerCondition == GameState.PlayerCondition.PLAYING) {
                fragmentManager?.popBackStack()
            }
        }
    }

    /**
     * Adapts a string representing a question's possible answer to be compatible with an
     * AbstractSelectorFragment list.
     */
    class AnswerWrapper(override val attr1: String) : SelectorCellModel
}