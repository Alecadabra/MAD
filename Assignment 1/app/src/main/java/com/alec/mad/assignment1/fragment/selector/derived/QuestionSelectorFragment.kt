package com.alec.mad.assignment1.fragment.selector.derived

import com.alec.mad.assignment1.R
import com.alec.mad.assignment1.fragment.selector.AbstractSelectorFragment
import com.alec.mad.assignment1.model.Question

/**
 * [AbstractSelectorFragment] for questions of a Flag.
 */
class QuestionSelectorFragment(
    override val values: List<Question>,
    useBackButton: Boolean = true,
    useDynamicLayout: Boolean = true
) : AbstractSelectorFragment<Question>(useBackButton, useDynamicLayout) {

    init {
        // Because the default constructor has parameters, which android does not like.
        this.retainInstance = true
    }

    override val title: String = "Choose a question"

    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: Question) {
        super.bindViewHolder(holder, item)

        // Handle enabled
        if (!item.enabled) {
            holder.imageButton.isClickable = false
            holder.imageButton.isEnabled = false
        } else holder.imageButton.setOnClickListener {
            // Change to question fragment for this flag
            fragmentManager?.beginTransaction()?.also { transaction ->

                // Handle the landscape tablet special case
                if (resources.getBoolean(R.bool.isLandscapeTablet)) {
                    // Callback to invoke bindViewHolder so that the view is updated to show
                    // correct/incorrect colours and be un-clickable if answered
                    val landTabletCaseCallback: () -> Unit = {
                        this.bindViewHolder(holder, item)
                    }
                    // Put the answer selector in the side frame
                    transaction.replace(
                        R.id.gameFragmentLandTabletFrame,
                        AnswerSelectorFragment(
                            item, useBackButton = false,
                            landTabletCaseCallback = landTabletCaseCallback
                        )
                    )
                } else {
                    // Replace the activity's fragment frame with the answer selector
                    transaction.replace(
                        R.id.gameFragmentFrame,
                        AnswerSelectorFragment(item)
                    )

                    // The back button will go back to the flag selection screen
                    transaction.addToBackStack(null)
                }

                // Commit changes
                transaction.commit()
            }
        }
    }
}