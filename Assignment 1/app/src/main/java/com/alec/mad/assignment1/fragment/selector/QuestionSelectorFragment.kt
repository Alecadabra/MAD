package com.alec.mad.assignment1.fragment.selector

import com.alec.mad.assignment1.model.Question

class QuestionSelectorFragment(override val values: List<Question>) :
    AbstractSelectorFragment<Question>() {

    override fun bindViewHolder(holder: SelectorFragmentAdapter.ViewHolder, item: Question) {
        super.bindViewHolder(holder, item)
        holder.imageButton.setOnClickListener {
            // TODO Swap to question screen for this item
        }
    }
}