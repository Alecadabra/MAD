package com.alec.mad.assignment1.fragment.selector

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.gameState
import com.alec.mad.assignment1.model.Flag

sealed class AbstractFlagSelectorFragment(
    useBackButton: Boolean, useDynamicLayout: Boolean
) : AbstractSelectorFragment<Flag>(useBackButton, useDynamicLayout) {

    override val values: List<Flag> = this.gameState.flags
}

class FlagQuestionSelectorFragment : AbstractFlagSelectorFragment(
    useBackButton = false,
    useDynamicLayout = true
) {
    override val title: String = "Select a flag to view questions"

    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: Flag) {
        super.bindViewHolder(holder, item)

        // Handle being disabled
        if (!item.enabled) {
            // Make greyscale
            holder.imageButton.colorFilter = ColorMatrixColorFilter(
                ColorMatrix().also { it.setSaturation(0F) }
            )

            holder.imageButton.isClickable = false
            holder.imageButton.isEnabled = false
        }
        else holder.imageButton.setOnClickListener {
            // Change to question fragment for this flag
            activity?.supportFragmentManager?.beginTransaction()?.also { transaction ->
                // Replace the activity's fragment frame with the question selector
                transaction.replace(
                    R.id.gameFragmentFrame,
                    QuestionSelectorFragment(item.questions)
                )

                // The back button will go back to the flag selection screen
                transaction.addToBackStack(null)

                // Commit changes
                transaction.commit()
            } ?: throw IllegalStateException("Fragment manager not present")
        }
    }
}

class FlagSpecialSelectorFragment : AbstractFlagSelectorFragment(
    useBackButton = false,
    useDynamicLayout = true
) {

    companion object {
        const val SPECIAL_POINT_INCREASE_AMOUNT = 10
    }

    override val title: String =
        "You activated a special question! Choose a flag to add 10 points to all of it's questions"

    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: Flag) {
        super.bindViewHolder(holder, item)
        holder.imageButton.setOnClickListener {
            // Increase the points earned for each question in this flag
            item.questions.forEach { it.points += SPECIAL_POINT_INCREASE_AMOUNT }

            fragmentManager?.popBackStack()
        }
    }
}