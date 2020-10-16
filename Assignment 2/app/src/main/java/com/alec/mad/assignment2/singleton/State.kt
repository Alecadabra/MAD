package com.alec.mad.assignment2.singleton

import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.GameData
import com.alec.mad.assignment2.model.MapElement

object State {
    lateinit var gameData: GameData

    fun initialise() {
        this.gameData = GameData(
            map = initialiseMap(),
            money = Settings.initialMoney
        )
    }

    private fun initialiseMap(): List<List<MapElement>> {
        val bgDrawables = setOf(
            R.drawable.ic_grass1,
            R.drawable.ic_grass2,
            R.drawable.ic_grass3,
            R.drawable.ic_grass4
        )
        return List(Settings.mapHeight) {
            List(Settings.mapWidth) {
                MapElement(null, bgDrawables.random())
            }
        }
    }
}