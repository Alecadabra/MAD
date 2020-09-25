package com.alec.mad.assignment1.fragment.selector

import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.gameState
import com.alec.mad.assignment1.model.Question
import com.alec.mad.assignment1.model.Question.State
import com.alec.mad.assignment1.state.GameState

class AnswerSelectorFragment(private val question: Question) :
    AbstractSelectorFragment<AnswerSelectorFragment.AnswerWrapper>(
        useBackButton = true,
        useDynamicLayout = false
    ) {

    override val values: List<AnswerWrapper> = this.question.answers.map { AnswerWrapper(it) }
    override val title: String = this.question.questionText

    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: AnswerWrapper) {
        super.bindViewHolder(holder, item)

        fun determineImageSrc() = when (item.state) {
            State.UNANSWERED -> item.imageSrc
            State.CORRECT -> android.R.color.holo_green_light
            State.INCORRECT -> android.R.color.holo_red_light
        }

        holder.imageButton.setImageResource(determineImageSrc())

        holder.imageButton.setOnClickListener {
            // Only trigger if the question isn't answered
            if (this.question.state == State.UNANSWERED) {
                // True iff the answer was correct
                val correct = item.attr1 == this.question.correctAnswer

                // Set this view's state
                item.state = if (correct) State.CORRECT else State.INCORRECT

                // Set this question's state accordingly
                this.question.state = if (correct) State.CORRECT else State.INCORRECT

                // Make the button green or red
                holder.imageButton.setImageResource(determineImageSrc())

                // Adjust player's points accordingly
                this.gameState.also { gameState ->
                    gameState.playerPoints = when (correct) {
                        true -> gameState.playerPoints + this.question.points
                        false -> gameState.playerPoints - this.question.penalty
                    }
                }

                // Handle isSpecial
                if (
                    correct &&
                    this.question.isSpecial &&
                    this.gameState.playerCondition == GameState.PlayerCondition.PLAYING
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
     * AbstractSelectorFragment list. ALso holds information on if the answer was
     */
    class AnswerWrapper(
        override val attr1: String, var state: State = State.UNANSWERED
    ) : SelectorCellModel
}