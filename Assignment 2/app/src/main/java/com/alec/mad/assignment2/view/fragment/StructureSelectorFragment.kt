package com.alec.mad.assignment2.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.Structure
import com.alec.mad.assignment2.model.StructureType
import com.alec.mad.assignment2.singleton.StructureData

class StructureSelectorFragment(private val structureType: StructureType) : Fragment() {

    val structures = StructureData.filter { it.type == this.structureType }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_structure_selector_list, container, false)

        // Set up recyclerview
        val rv = view as? RecyclerView ?: throw IllegalStateException("View not RV")
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = StructureSelectorAdapter()
        return view
    }

    inner class StructureSelectorAdapter
        : RecyclerView.Adapter<StructureSelectorAdapter.StructureSelectorViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StructureSelectorViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_structure_selector, parent, false)
            return StructureSelectorViewHolder(view)
        }

        override fun onBindViewHolder(holder: StructureSelectorViewHolder, position: Int) {
            holder.bindViewHolder(this@StructureSelectorFragment.structures[position])
        }

        override fun getItemCount(): Int = this@StructureSelectorFragment.structures.size

        inner class StructureSelectorViewHolder(
            view: View
        ) : RecyclerView.ViewHolder(view) {

            private val structureImageButton: ImageButton = view.findViewById(
                R.id.structureSelectorFragmentImageButton
            )

            fun bindViewHolder(structure: Structure) {
                structure.drawImageTo(this.structureImageButton)
            }
        }
    }
}