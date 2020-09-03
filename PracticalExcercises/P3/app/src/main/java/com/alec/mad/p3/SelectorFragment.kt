package com.alec.mad.p3

import Structure
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_selector.*
import kotlinx.android.synthetic.main.list_selection.*
import kotlinx.android.synthetic.main.list_selection.view.*

class SelectorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_selector, container, false)



        return view
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
            this@SelectorFragment.label?.text = structure.label
            this@SelectorFragment.image?.setImageResource(structure.drawableId)
        }
    }
}