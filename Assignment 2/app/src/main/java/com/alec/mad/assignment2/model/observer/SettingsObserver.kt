package com.alec.mad.assignment2.model.observer

interface SettingsObserver {

    fun onUpdateMapWidth(mapWidth: Int) {}

    fun onUpdateMapHeight(mapHeight: Int) {}

    fun onUpdateInitialMoney(initialMoney: Int) {}
}