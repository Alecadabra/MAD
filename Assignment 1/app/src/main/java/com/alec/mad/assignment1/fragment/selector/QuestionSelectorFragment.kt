package com.alec.mad.assignment1.fragment.selector

import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.model.Question

class QuestionSelectorFragment(override val values: List<Question>) :
    AbstractSelectorFragment<Question>(
        useBackButton = true,
        useDynamicLayout = true
    ) {
    override val title: String = "Choose a question"

    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: Question) {
        super.bindViewHolder(holder, item)

        // Handle enabled
        if (!item.enabled) {
            holder.imageButton.isClickable = false
            holder.imageButton.isEnabled = false
        }
        else holder.imageButton.setOnClickListener {
            // Change to question fragment for this flag
            fragmentManager?.beginTransaction()?.also { transaction ->
                // Replace the activity's fragment frame with the question selector
                transaction.replace(
                    R.id.gameFragmentFrame,
                    AnswerSelectorFragment(item)
                )

                // The back button will go back to the flag selection screen
                transaction.addToBackStack(null)

                // Commit changes
                transaction.commit()
            } ?: throw IllegalStateException("Fragment manager not present")
        }
    }
}