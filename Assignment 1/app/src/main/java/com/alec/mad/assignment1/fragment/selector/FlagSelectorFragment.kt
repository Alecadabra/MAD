package com.alec.mad.assignment1.fragment.selector

import androidx.fragment.app.FragmentManager
import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.model.Flag
import com.alec.mad.assignment1.singleton.GameState

sealed class AbstractFlagSelectorFragment : AbstractSelectorFragment<Flag>() {
    override val values: List<Flag> = GameState.flags
}

class FlagQuestionSelectorFragment : AbstractFlagSelectorFragment() {
    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: Flag) {
        super.bindViewHolder(holder, item)
        holder.imageButton.setOnClickListener {
            // Change to question fragment for this flag

            val fm: FragmentManager = activity?.supportFragmentManager
                ?: throw IllegalStateException("Activity not present")
            var fragTransaction = fm.beginTransaction()

            // Remove the layout changer from it's frame if it's there
            fragTransaction = fragTransaction.remove(this)
            fm.findFragmentById(R.id.selectorLayoutChangerFrame)?.also {
                fragTransaction = fragTransaction.remove(it)
            }

            // Replace the activity's fragment frame with the question selector
            fragTransaction = fragTransaction.replace(
                R.id.gameFragmentFrame,
                QuestionSelectorFragment(item.questions)
            )

            // The back button will go back to the flag selection screen
            fragTransaction = fragTransaction.addToBackStack(null)

            // Commit changes
            fragTransaction.commit()
        }
    }
}

class FlagSpecialSelectorFragment : AbstractFlagSelectorFragment() {
    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: Flag) {
        super.bindViewHolder(holder, item)
        holder.imageButton.setOnClickListener {
            // Increase the points earned for each question in this flag by 10.
            item.questions.forEach { it.points += 10 }
        }
    }
}