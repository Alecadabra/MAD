package com.alec.mad.assignment1.fragment.selector

import com.alec.mad.assignment1.model.Question
import com.alec.mad.assignment1.model.Question.State
import com.alec.mad.assignment1.singleton.GameState

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
            holder.imageButton.setImageResource(
                if (correct) android.R.color.holo_green_light else android.R.color.holo_red_light
            )

            // Adjust player's points accordingly
            GameState.playerPoints += if (correct) this.question.points else -this.question.penalty

            // Set this question's state accordingly
            this.question.state = if (correct) State.CORRECT else State.INCORRECT

            if (correct && this.question.isSpecial) {

            }
        }
    }

    class AnswerWrapper(override val attr1: String) : SelectorCellModel
}