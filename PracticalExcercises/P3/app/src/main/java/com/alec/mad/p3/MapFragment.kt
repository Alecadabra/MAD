package com.alec.mad.p3

import MapData
import MapElement
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_selector.*
import kotlinx.android.synthetic.main.grid_cell.*

class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        return view
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
            try {
                adapter = MapAdapter(MapData.grid)
            }
            catch (e: Error) {
                throw RuntimeException(e.message)
            }
        }
    }

    private inner class MapAdapter(val grid: Array<Array<MapElement>>) :
        RecyclerView.Adapter<MapElementViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MapElementViewHolder(
            LayoutInflater.from(this@MapFragment.activity), parent
        )

        override fun onBindViewHolder(holder: MapElementViewHolder, position: Int) {
            holder.bind(this.grid[position % MapData.HEIGHT][position / MapData.HEIGHT])
        }

        override fun getItemCount(): Int = /*MapData.WIDTH **/ MapData.HEIGHT
    }

    private inner class MapElementViewHolder(li: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(li.inflate(R.layout.grid_cell, parent, false)) {

        lateinit var mapElement: MapElement

        fun bind(mapElement: MapElement) {
            this.mapElement = mapElement
            this@MapFragment.gridCellTopLeft?.setImageResource(mapElement.northWest)
            this@MapFragment.gridCellTopRight?.setImageResource(mapElement.northEast)
            this@MapFragment.gridCellBottomLeft?.setImageResource(mapElement.southWest)
            this@MapFragment.gridCellBottomRight?.setImageResource(mapElement.southEast)

            mapElement.structure?.also {
                // If there's a structure, set the image accordingly
                this@MapFragment.gridCellStructureImage?.setImageResource(it.drawableId)
            } ?: also {
                // If there's no structure, set the image to transparent
                this@MapFragment.gridCellStructureImage?.setImageResource(
                    android.R.color.transparent
                )
            }
        }
    }
}