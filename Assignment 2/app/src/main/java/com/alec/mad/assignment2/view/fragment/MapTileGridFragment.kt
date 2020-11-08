package com.alec.mad.assignment2.view.fragment

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.GameData.Tool
import com.alec.mad.assignment2.model.GameMap
import com.alec.mad.assignment2.controller.observer.GameMapObserver
import com.alec.mad.assignment2.model.StructureType
import com.alec.mad.assignment2.singleton.State
import com.alec.mad.assignment2.view.activity.DetailsActivity

/**
 * RecyclerView fragment to show the game map.
 */
class MapTileGridFragment : Fragment(), GameMapObserver {

    private var rv: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_tile_grid, container, false)

        // Set up recyclerview
        this.rv = view as? RecyclerView
        this.rv?.layoutManager = GridLayoutManager(
            context,
            State.gameData.settings.mapHeight,
            GridLayoutManager.HORIZONTAL,
            false
        )
        this.rv?.adapter = MapTileGridAdapter()

        // Observe changes to the game map
        State.gameData.gameMap.observers.add(this)
        State.gameData.gameMap.notifyMe(this)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()

        // Stop observing the game map
        State.gameData.gameMap.observers.remove(this)
    }

    override fun onUpdateMapElement(i: Int, j: Int, mapElement: GameMap.MapElement) {
        // When a map element changes, notify the adapter
        this.rv?.adapter?.also { adapter ->
            adapter.notifyItemChanged(GameMap.getAdapterPosition(i, j))
        }
    }

    inner class MapTileGridAdapter : RecyclerView.Adapter<MapTileGridAdapter.MapTileViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapTileViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_map_tile,
                parent,
                false
            )
            return MapTileViewHolder(view, parent)
        }

        override fun onBindViewHolder(holder: MapTileViewHolder, position: Int) {
            val i = GameMap.getI(position)
            val j = GameMap.getJ(position)
            holder.bindViewHolder(State.gameData.gameMap[i, j], i, j)
        }

        override fun getItemCount(): Int = State.gameData.settings.let { settings ->
            settings.mapWidth * settings.mapWidth
        }

        inner class MapTileViewHolder(
            val view: View,
            parent: ViewGroup
        ) : RecyclerView.ViewHolder(view) {

            private val bgImageView: ImageView = view.findViewById(R.id.mapTileBgImage)
            private val structureImageView: ImageView = view.findViewById(R.id.mapTileStructureImage)

            init {
                // Make square
                val size = parent.measuredHeight / State.gameData.settings.mapHeight + 1
                this.itemView.layoutParams.also { lp ->
                    lp.width = size
                    lp.height = size
                }

                // Rotate bgImage randomly, just per recycler view tile not per bind
                // It is intentional that this changes per view holder rather than tile, this is
                // a performance optimisation
                this.bgImageView.rotation = RIGHT_ANGLES.random()
            }

            fun bindViewHolder(mapElement: GameMap.MapElement, i: Int, j: Int) {
                // Set images
                this.bgImageView.setImageResource(mapElement.bgImage)
                mapElement.structure?.drawImageTo(this.structureImageView) ?: run {
                    this.structureImageView.setImageResource(android.R.color.transparent)
                }

                view.setOnClickListener {
                    when (State.gameData.currentTool) {

                        Tool.BUILD_RESIDENTIAL, Tool.BUILD_COMMERCIAL, Tool.BUILD_ROAD -> {
                            // Invoke the current build intent show error if null
                            State.gameData.buildIntent?.buildAt(i, j) ?: Toast.makeText(
                                this@MapTileGridFragment.context,
                                "You don't have a structure selected",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        Tool.DEMOLISH -> {
                            // Delete the structure or show error if null
                            if (mapElement.structure != null) {
                                // First update game state
                                when (mapElement.structure?.structureType) {
                                    StructureType.RESIDENTIAL -> {
                                        State.gameData.numResidential--
                                    }
                                    StructureType.COMMERCIAL -> {
                                        State.gameData.numCommercial--
                                    }
                                    else -> Unit // Do nothing
                                }

                                // Remove the structure
                                mapElement.structure = null
                            } else {
                                Toast.makeText(
                                    context,
                                    "Select a tile with a structure to demolish",
                                    Toast.LENGTH_SHORT
                                ).also { it.setGravity(Gravity.CENTER, 0, 0) }.show()
                            }
                        }

                        Tool.INFO -> {
                            // Start the details activity on the structure or show error if null
                            if (mapElement.structure != null) {
                                this@MapTileGridFragment.activity?.also { nullSafeActivity ->
                                    val intent = DetailsActivity.getIntent(
                                        nullSafeActivity, i, j
                                    )
                                    nullSafeActivity.startActivity(intent)
                                } ?: error("Activity null")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Select a tile with a structure to view info",
                                    Toast.LENGTH_SHORT
                                ).also { it.setGravity(Gravity.CENTER, 0, 0) }.show()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val RIGHT_ANGLES = setOf(0f, 90f, 180f, 270f)
    }
}