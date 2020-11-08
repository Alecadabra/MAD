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
import com.alec.mad.assignment2.singleton.State
import com.alec.mad.assignment2.singleton.StructureData

/**
 * A fragment to be placed in the [com.alec.mad.assignment2.view.activity.GameActivity]'s
 * bottom frame to interface with a tool that represents a list of structures to build.
 */
class BuildToolFragment(private var structureType: StructureType? = null) : Fragment() {

    private lateinit var titleText: String

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
        this.titleText = when (this.structureType) {
            StructureType.RESIDENTIAL -> {
                "Build a residential building\nCost: $${State.gameData.settings.houseBuildCost}"
            }
            StructureType.COMMERCIAL -> {
                "Build a commercial building\nCost: $${State.gameData.settings.commBuildCost}"
            }
            StructureType.ROAD -> {
                "Build a road\nCost: $${State.gameData.settings.roadBuildCost}"
            }
            null -> error("Structure type not set")
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        this.structureType?.also {
            outState.putInt(BUNDLE_STRUCTURE_TYPE_ORDINAL, it.ordinal)
        }
    }

    /**
     * The recycler view holds the [structures] list as well as an extra first element which
     * is the [titleText] text.
     */
    inner class StructureSelectorAdapter
        : RecyclerView.Adapter<StructureSelectorAdapter.StructureSelectorViewHolder>() {

        /**
         * The adapter position of the selected list element, or null if none are selected.
         */
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
            if (position == 0) {
                // Binding the title TextView
                holder.bindTitle()
            } else {
                // Binding a structure ImageButton
                holder.bindStructureBtn(this@BuildToolFragment.structures[position - 1])
            }
        }

        // Add one due to the title
        override fun getItemCount(): Int = this@BuildToolFragment.structures.size + 1

        inner class StructureSelectorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

            private val structureImageButton: ImageButton = view.findViewById(
                R.id.buildToolElementStructureBtn
            )
            private val titleView: TextView = view.findViewById(
                R.id.buildToolElementTitle
            )

            fun bindTitle() {
                // This is the title
                this.structureImageButton.visibility = View.INVISIBLE
                this.titleView.text = this@BuildToolFragment.titleText

                this.structureImageButton.background.clearColorFilter()
                this.structureImageButton.setOnClickListener {  }
            }

            fun bindStructureBtn(structure: Structure) {
                this.structureImageButton.visibility = View.VISIBLE
                this.titleView.text = ""

                // This is an image button
                structure.drawImageTo(this.structureImageButton)

                this.structureImageButton.background.clearColorFilter()

                this@StructureSelectorAdapter.selectedItemPosition?.also { selectedPos ->
                    // If this is selected
                    if (this.adapterPosition == selectedPos) {
                        this.structureImageButton.background.colorFilter = LightingColorFilter(
                            0x000000, 0xAAFFAA
                        )
                    }
                }

                this.structureImageButton.setOnClickListener {
                    // Set the build intent
                    State.gameData.buildIntent = BuildIntent(
                        this@BuildToolFragment.context, structure
                    )

                    // Set the selected position to this and change the old selected item
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
        private const val PATH = "com.alec.mad.assignment2.view.fragment.BuildToolFragment"
        const val BUNDLE_STRUCTURE_TYPE_ORDINAL = "$PATH.structureTypeOrdinal"
    }
}