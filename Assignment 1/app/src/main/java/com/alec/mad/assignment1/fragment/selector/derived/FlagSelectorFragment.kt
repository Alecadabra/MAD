package com.alec.mad.assignment1.fragment.selector.derived

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import com.alec.mad.assignment1.GameSingleton
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.fragment.selector.AbstractSelectorFragment
import com.alec.mad.assignment1.model.Flag
import com.alec.mad.assignment1.state.GameState

/**
 * An [AbstractSelectorFragment] for [Flag]s, either a selector for a flag to view questions of,
 * or a selector for a flag to increase the points of after an isSpecial question is correctly
 * answered.
 */
sealed class AbstractFlagSelectorFragment(
    useBackButton: Boolean,
    useDynamicLayout: Boolean
) : AbstractSelectorFragment<Flag>(useBackButton, useDynamicLayout) {

    override val values: List<Flag> = GameSingleton.state.flags
}

/**
 * Selector for a flag to view the questions of.
 */
class FlagQuestionSelectorFragment(
    useBackButton: Boolean = false,
    useDynamicLayout: Boolean = true
) : AbstractFlagSelectorFragment(useBackButton, useDynamicLayout) {
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
        } else {
            // Reset color filter
            holder.imageButton.colorFilter = ColorMatrixColorFilter(ColorMatrix())

            holder.imageButton.setOnClickListener {
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
                }
            }
        }
    }
}

/**
 * Selector for a flag after an isSpecial question is correctly answered.
 */
class FlagSpecialSelectorFragment(
    useBackButton: Boolean = false,
    useDynamicLayout: Boolean = true
) : AbstractFlagSelectorFragment(useBackButton, useDynamicLayout) {

    /* A hidden feature of this class is in MainActivity, where back button presses are ignored
 * if this class is currently active. */

    companion object {
        const val SPECIAL_POINT_INCREASE_AMOUNT = 10
    }

    override val title: String = "You activated a special question! Choose a flag to add " +
            "$SPECIAL_POINT_INCREASE_AMOUNT points to all of it's questions"

    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: Flag) {
        super.bindViewHolder(holder, item)

        // Sometimes this gets triggered after the game is already over, which we don't want
        if (GameSingleton.state.playerCondition != GameState.PlayerCondition.PLAYING) {
            fragmentManager?.popBackStack()
        }

        // Handle being disabled
        if (!item.enabled) {
            // Make greyscale
            holder.imageButton.colorFilter = ColorMatrixColorFilter(
                ColorMatrix().also { it.setSaturation(0F) }
            )

            holder.imageButton.isClickable = false
            holder.imageButton.isEnabled = false
        } else {
            // Reset color filter
            holder.imageButton.colorFilter = ColorMatrixColorFilter(ColorMatrix())

            holder.imageButton.setOnClickListener {
                // Increase the points earned for each question in this flag
                item.questions.forEach { it.points += SPECIAL_POINT_INCREASE_AMOUNT }

                // Pop a back stack, it's a skateboarding thing
                fragmentManager?.popBackStack()
            }
        }
    }
}