package com.alec.mad.p3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_selection.*
import kotlin.RuntimeException

class SelectorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selector, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val rv: RecyclerView? = activity?.findViewById(R.id.selectorRecyclerView)

        rv?.apply {
            layoutManager = LinearLayoutManager(
                activity, RecyclerView.HORIZONTAL, false
            )

            adapter = SelectorAdapter(StructureData.structureList)
        }
    }

    /**
     * Adapter implementation
     */
    private inner class SelectorAdapter(val structureList: MutableList<Structure>) :
        RecyclerView.Adapter<StructureViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StructureViewHolder =
            StructureViewHolder(LayoutInflater.from(this@SelectorFragment.activity), parent)

        override fun onBindViewHolder(holder: StructureViewHolder, position: Int) {
            holder.bind(this.structureList[position])
        }

        override fun getItemCount(): Int = this.structureList.size
    }

    /**
     * ViewHolder implementation
     */
    private inner class StructureViewHolder(li: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(li.inflate(R.layout.list_selection, parent, false)) {

        lateinit var structure: Structure

        fun bind(structure: Structure) {
            this.structure = structure
            this@SelectorFragment.label?.setText(structure.label)
                //?: throw RuntimeException("Could not set label text: ${this@SelectorFragment.label}")
            this@SelectorFragment.image?.setImageResource(structure.drawableId)
                //?: throw RuntimeException("Could not set image : ${this@SelectorFragment.image}")
        }
    }
}