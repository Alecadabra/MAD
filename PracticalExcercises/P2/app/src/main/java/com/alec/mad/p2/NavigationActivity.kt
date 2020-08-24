package com.alec.mad.p2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class NavigationActivity : AppCompatActivity() {

    private var saveTimes: Int by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.saveTimes = savedInstanceState?.getInt(SAVE_TIMES) ?: 0

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Nobody wants your null intents
        if (data == null) return

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SHOP_ACTIVITY)
            if (ShopActivity.areTheyDead(data)) theyDied()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SAVE_TIMES, saveTimes + 1)
    }

    private fun townOptions() {
        val intent = ShopActivity.getIntent(this, "this is a string wow")
        startActivityForResult(intent, IntentHandler.REQUEST_CODE_SHOP_ACTIVITY)
    }

    private fun wildernessOptions() {
        @SuppressLint("SetTextI18n")
        options.text = "JK I never implemented that"
        errorDesc.text = this.saveTimes.toString()
    }

    private fun move(bearing: Bearing) {
        val player = GameState.player

        if(boundsCheck(bearing)) {
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

    @SuppressLint("SetTextI18n")
    private fun theyDied() {
        locationDesc.text = "You have died"
        areaDesc.text = "Game Over"
        options.text = "Restart Game"

        north.isEnabled = false
        north.setTextColor(Color.GRAY)
        south.isEnabled = false
        south.setTextColor(Color.GRAY)
        east.isEnabled = false
        east.setTextColor(Color.GRAY)
        west.isEnabled = false
        west.setTextColor(Color.GRAY)

        options.setOnClickListener {
            GameState.resetGame()
            north.isEnabled = true
            south.isEnabled = true
            east.isEnabled = true
            west.isEnabled = true
            updateText()
            options.setOnClickListener { when(GameState.gameMap[GameState.player].type) {
                Area.AreaType.TOWN -> townOptions()
                Area.AreaType.WILDERNESS -> wildernessOptions()
            } }
        }
    }

    private fun boundsCheck(bearing: Bearing): Boolean {
        val xLoc = GameState.player.xLoc
        val yLoc = GameState.player.yLoc

        return xLoc + bearing.x < GameState.gameMap.size
                && xLoc + bearing.x >= 0
                && yLoc + bearing.y < GameState.gameMap[0].size
                && yLoc + bearing.y >= 0
    }

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
        else theyDied()

        // Reset the error message
        errorDesc.text = ""

        north.setTextColor(when (boundsCheck(Bearing.NORTH)) {
            true -> Color.BLACK
            false -> Color.GRAY
        })
        south.setTextColor(when (boundsCheck(Bearing.SOUTH)) {
            true -> Color.BLACK
            false -> Color.GRAY
        })
        east.setTextColor(when (boundsCheck(Bearing.EAST)) {
            true -> Color.BLACK
            false -> Color.GRAY
        })
        west.setTextColor(when (boundsCheck(Bearing.WEST)) {
            true -> Color.BLACK
            false -> Color.GRAY
        })
    }

    enum class Bearing(val x: Int, val y: Int) {
        NORTH(0, 1),
        SOUTH(0, -1),
        EAST(1, 0),
        WEST(-1, 0)
    }

    companion object IntentHandler {
        private const val SAVE_TIMES = "com.alec.mad.p2.saveTimes"

        private const val REQUEST_CODE_SHOP_ACTIVITY = 0

        fun getIntent(c: Context) : Intent = Intent(c, NavigationActivity::class.java)
    }
}