package com.alec.mad.assignment2.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.Structure
import com.alec.mad.assignment2.model.StructureType
import com.alec.mad.assignment2.singleton.Settings
import com.alec.mad.assignment2.singleton.StructureData

class BuildToolFragment(private var structureType: StructureType? = null) : Fragment() {

    val structures: List<Structure> by lazy {
        StructureData.filter { it.type == this.structureType }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.also { bundle ->
            this.structureType ?: run {
                val ordinal = bundle.getInt(BUNDLE_STRUCTURE_TYPE_ORDINAL, -1)
                if (ordinal != -1) {
                    this.structureType = StructureType.values()[ordinal]
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_build_tool, container, false)

        // Set up recyclerview
        val rv = view.findViewById<RecyclerView>(R.id.buildToolRecyclerView)
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = StructureSelectorAdapter()

        // Display title
        val text = when (this.structureType) {
            StructureType.RESIDENTIAL -> {
                "Build a residential building. Cost: ${Settings.houseBuildCost}"
            }
            StructureType.COMMERCIAL -> {
                "Build a commercial building. Cost: ${Settings.commBuildCost}"
            }
            StructureType.ROAD -> {
                "Build a road. Cost: ${Settings.roadBuildCost}"
            }
            else -> {
                throw IllegalStateException("Structure type not set")
            }
        }
        view.findViewById<TextView>(R.id.buildToolTitle).text = text

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.also { bundle ->
            this.structureType?.also {
                bundle.putInt(BUNDLE_STRUCTURE_TYPE_ORDINAL, it.ordinal)
            }
        }
    }

    inner class StructureSelectorAdapter
        : RecyclerView.Adapter<StructureSelectorAdapter.StructureSelectorViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): StructureSelectorViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_build_tool_element,
                parent,
                false
            )
            return StructureSelectorViewHolder(view)
        }

        override fun onBindViewHolder(holder: StructureSelectorViewHolder, position: Int) {
            holder.bindViewHolder(this@BuildToolFragment.structures[position])
        }

        override fun getItemCount(): Int = this@BuildToolFragment.structures.size

        inner class StructureSelectorViewHolder(
            view: View
        ) : RecyclerView.ViewHolder(view) {

            private val structureImageButton: ImageButton = view.findViewById(
                R.id.buildToolElementStructureBtn
            )

            fun bindViewHolder(structure: Structure) {
                structure.drawImageTo(this.structureImageButton)
            }
        }
    }

    companion object {
        private const val PACKAGE = "com.alec.mad.assignment2.view.fragment"
        const val BUNDLE_STRUCTURE_TYPE_ORDINAL = "$PACKAGE.structureTypeOrdinal"
    }
}