package com.alec.mad.assignment2.controller.observer

/**
 * Implementing classes observe changes to a [com.alec.mad.assignment2.model.Settings] object
 */
interface SettingsObserver {

    fun onUpdateIslandName(islandName: String) {}

    fun onUpdateMapWidth(mapWidth: Int) {}

    fun onUpdateMapHeight(mapHeight: Int) {}

    fun onUpdateInitialMoney(initialMoney: Int) {}
}