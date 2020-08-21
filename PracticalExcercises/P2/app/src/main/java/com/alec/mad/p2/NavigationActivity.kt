package com.alec.mad.p2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startGame()
    }

    private fun startGame() {
        north.setOnClickListener { move(Bearing.NORTH) }
        south.setOnClickListener { move(Bearing.SOUTH) }
        east.setOnClickListener { move(Bearing.EAST) }
        west.setOnClickListener { move(Bearing.WEST) }

        options.setOnClickListener { when(GameState.gameMap[GameState.player].type) {
            Area.AreaType.TOWN -> townOptions()
            Area.AreaType.WILDERNESS -> wildernessOptions()
        } }

        updateText()
    }

    private fun townOptions() {
        startActivity(ShopActivity.getIntent(this))
        // TODO("Not yet implemented")
    }

    private fun wildernessOptions() {
        // TODO("Not yet implemented")
    }

    private fun move(bearing: Bearing) {
        val player = GameState.player

        if(boundsCheck(player.xLoc, player.yLoc, bearing)) {
            // Bounds check passes, player can move in this direction
            player.xLoc += bearing.x
            player.yLoc += bearing.y

            player.health =
                0.0f.coerceAtLeast((player.health - 5.0f - (player.equipmentMass / 2.0f)))

            updateText()
        }
        else {
            // Bounds check fails, player can not move in this direction
            @SuppressLint("SetTextI18n")
            errorDesc.text = "You are on the edge of the map and cannot move ${
                bearing.name.toLowerCase()}"
        }
    }

    private fun boundsCheck(xLoc: Int, yLoc: Int, bearing: Bearing): Boolean =
        xLoc + bearing.x < GameState.gameMap.size
                && xLoc + bearing.x >= 0
                && yLoc + bearing.y < GameState.gameMap[0].size
                && yLoc + bearing.y >= 0

    @SuppressLint("SetTextI18n")
    private fun updateText() {
        val player = GameState.player

        if(player.health > 0.0f) {

            // Update location text to show location
            locationDesc.text = "You are at ${player.xLoc}, ${player.yLoc}"

            // Update area description text and options button text
            when (GameState.gameMap[player].type) {
                Area.AreaType.TOWN -> {
                    areaDesc.text = "You are in a town"
                    options.text = "View shop"
                }
                Area.AreaType.WILDERNESS -> {
                    areaDesc.text = "You are in the wilderness"
                    options.text = "Forage for items"
                }
            }
        }
        else {
            locationDesc.text = "You have died"
            areaDesc.text = "Game Over"
            options.text = "Restart Game"

            north.setOnClickListener {  }
            south.setOnClickListener {  }
            east.setOnClickListener {  }
            west.setOnClickListener {  }

            options.setOnClickListener {
                GameState.resetGame()
                startGame()
            }
        }

        // Reset the error message
        errorDesc.text = ""
    }

    enum class Bearing(val x: Int, val y: Int) {
        NORTH(0, 1),
        SOUTH(0, -1),
        EAST(1, 0),
        WEST(-1, 0)
    }

    companion object IntentHandler {
        fun getIntent(c: Context) : Intent {
            return Intent(c, NavigationActivity::class.java)
        }
    }
}