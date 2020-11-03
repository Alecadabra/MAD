package com.alec.mad.assignment2.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.GameData
import com.alec.mad.assignment2.model.GameData.Tool
import com.alec.mad.assignment2.model.MapElement
import com.alec.mad.assignment2.singleton.Settings
import com.alec.mad.assignment2.singleton.State

class MapTileGridFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_tile_grid, container, false)

        // Set up recyclerview
        val rv = view as RecyclerView
        rv.layoutManager = GridLayoutManager(
            context,
            Settings.mapHeight,
            GridLayoutManager.HORIZONTAL,
            false
        )
        rv.adapter = MapTileGridAdapter()

        return view
    }

    class MapTileGridAdapter : RecyclerView.Adapter<MapTileGridAdapter.MapTileViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapTileViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_map_tile,
                parent,
                false
            )
            return MapTileViewHolder(view, parent)
        }

        override fun onBindViewHolder(holder: MapTileViewHolder, position: Int) {
            val i = position % Settings.mapHeight
            val j = position / Settings.mapHeight
            holder.bindViewHolder(State.gameData.map[i][j], i, j)
        }

        override fun getItemCount(): Int = Settings.mapHeight * Settings.mapWidth

        inner class MapTileViewHolder(
            val view: View,
            parent: ViewGroup
        ) : RecyclerView.ViewHolder(view) {

            private val bgImageView: ImageView = view.findViewById(R.id.mapTileBgImage)
            private val structureImageView: ImageView = view.findViewById(R.id.mapTileStructureImage)

            init {
                // Make square
                val size = parent.measuredHeight / Settings.mapHeight + 1
                this.itemView.layoutParams.also { lp ->
                    lp.width = size
                    lp.height = size
                }

                // Rotate bgImage randomly, just per recycler view tile not per bind
                this.bgImageView.rotation = rightAngles.random()
            }

            fun bindViewHolder(mapElement: MapElement, i: Int, j: Int) {
                this.bgImageView.setImageResource(mapElement.bgImage)
                mapElement.structure?.drawImageTo(this.structureImageView) ?: run {
                    this.structureImageView.setImageResource(android.R.color.transparent)
                }
                view.setOnClickListener {
                    when (State.gameData.currentTool) {
                        Tool.BUILD_RESIDENTIAL, Tool.BUILD_COMMERCIAL, Tool.BUILD_ROAD -> {
                            val built = State.gameData.buildIntent?.buildAt(i, j) ?: false
                            if (built) {
                                this@MapTileGridAdapter.notifyItemChanged(this.adapterPosition)
                            }
                        }
                        Tool.DEMOLISH -> {
                            if (mapElement.structure != null) {
                                mapElement.structure = null
                                this@MapTileGridAdapter.notifyItemChanged(this.adapterPosition)
                            }
                        }
                        Tool.INFO -> TODO()
                    }

                }
            }
        }
    }

    companion object {
        val rightAngles = setOf(0f, 90f, 180f, 270f)
    }
}