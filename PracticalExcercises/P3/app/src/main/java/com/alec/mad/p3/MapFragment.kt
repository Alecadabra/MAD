package com.alec.mad.p3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.grid_cell.*

class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val rv = activity?.findViewById<RecyclerView>(R.id.mapRecyclerView)

        rv?.apply {
            // Specify the layout of the recyclerview
            layoutManager = GridLayoutManager(
                this@MapFragment.activity,
                MapData.HEIGHT,
                GridLayoutManager.HORIZONTAL,
                false
            )
            // Create and hook up adapter
            adapter = MapAdapter(MapData.instance.grid)
        }
    }

    private inner class MapAdapter(val grid: Array<Array<MapElement?>>) :
        RecyclerView.Adapter<MapElementViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MapElementViewHolder(
            LayoutInflater.from(this@MapFragment.activity), parent
        )

        override fun onBindViewHolder(holder: MapElementViewHolder, position: Int) {
            this.grid[position % MapData.HEIGHT][position / MapData.HEIGHT]?.let { holder.bind(it) }
        }

        override fun getItemCount(): Int = MapData.WIDTH /** MapData.HEIGHT*/
    }

    private inner class MapElementViewHolder(li: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(li.inflate(R.layout.grid_cell, parent, false)) {

        lateinit var mapElement: MapElement

        init {
            // Make cells square
            (parent.measuredHeight / MapData.HEIGHT + 1).also {
                this.itemView.layoutParams.apply {
                    width = it
                    height = it
                }
            }
        }

        fun bind(mapElement: MapElement) {
            this.mapElement = mapElement
            with(this@MapFragment) {
                gridCellTopLeft?.setImageResource(mapElement.northWest)
                gridCellTopRight?.setImageResource(mapElement.northEast)
                gridCellBottomLeft?.setImageResource(mapElement.southWest)
                gridCellBottomRight?.setImageResource(mapElement.southEast)

                mapElement.structure?.also {
                    // If there's a structure, set the image accordingly
                    gridCellStructureImage?.setImageResource(it.drawableId)
                } ?: run {
                    // If there's no structure, set the image to transparent
                    gridCellStructureImage?.setImageResource(android.R.color.transparent)
                }
            }
        }
    }
}