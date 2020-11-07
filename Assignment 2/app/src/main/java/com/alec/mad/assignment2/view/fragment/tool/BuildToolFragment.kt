package com.alec.mad.assignment2.view.fragment.tool

import android.graphics.LightingColorFilter
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
import com.alec.mad.assignment2.controller.BuildIntent
import com.alec.mad.assignment2.model.Structure
import com.alec.mad.assignment2.model.StructureType
import com.alec.mad.assignment2.model.Settings
import com.alec.mad.assignment2.singleton.State
import com.alec.mad.assignment2.singleton.StructureData

class BuildToolFragment(private var structureType: StructureType? = null) : Fragment() {

    val structures: List<Structure> by lazy {
        // Resolve the structure list if the type has been set
        this.structureType?.let { nullSafeType ->
            StructureData.filter { it.structureType == nullSafeType }
        } ?: error("Structure type null")
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
                "Build a residential building. Cost: ${State.gameData.settings.houseBuildCost}"
            }
            StructureType.COMMERCIAL -> {
                "Build a commercial building. Cost: ${State.gameData.settings.commBuildCost}"
            }
            StructureType.ROAD -> {
                "Build a road. Cost: ${State.gameData.settings.roadBuildCost}"
            }
            else -> {
                error("Structure type not set")
            }
        }
        view.findViewById<TextView>(R.id.buildToolTitle).text = text

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        this.structureType?.also {
            outState.putInt(BUNDLE_STRUCTURE_TYPE_ORDINAL, it.ordinal)
        }
    }

    inner class StructureSelectorAdapter
        : RecyclerView.Adapter<StructureSelectorAdapter.StructureSelectorViewHolder>() {

        private var selectedItemPosition: Int? = null

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
            val view: View
        ) : RecyclerView.ViewHolder(view) {

            private val structureImageButton: ImageButton = view.findViewById(
                R.id.buildToolElementStructureBtn
            )

            fun bindViewHolder(structure: Structure) {
                structure.drawImageTo(this.structureImageButton)

                this.structureImageButton.background.clearColorFilter()

                this@StructureSelectorAdapter.selectedItemPosition?.also { selectedPos ->
                    if (this.adapterPosition == selectedPos) {
                        this.structureImageButton.background.colorFilter = LightingColorFilter(
                            0x000000, 0xAAFFAA
                        )
                    }
                }

                this.structureImageButton.setOnClickListener {

                    State.gameData.buildIntent = BuildIntent(
                        this@BuildToolFragment.context, structure
                    )

                    this@StructureSelectorAdapter.selectedItemPosition?.also { selectedPos ->
                        this@StructureSelectorAdapter.notifyItemChanged(selectedPos)
                    }

                    this@StructureSelectorAdapter.selectedItemPosition = this.adapterPosition
                    this@StructureSelectorAdapter.notifyItemChanged(this.adapterPosition)
                }
            }
        }
    }

    companion object {
        private const val PACKAGE = "com.alec.mad.assignment2.view.fragment.BuildToolFragment"
        const val BUNDLE_STRUCTURE_TYPE_ORDINAL = "$PACKAGE.structureTypeOrdinal"
    }
}