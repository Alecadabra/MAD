package com.alec.mad.assignment2.model

import com.alec.mad.assignment2.controller.observer.ObservableState
import com.alec.mad.assignment2.controller.observer.SettingsObserver
import kotlin.properties.Delegates.observable

/**
 * Holds all of the game's settings, some mutable, most not.
 */
class Settings : ObservableState<SettingsObserver>() {
    var islandName: String by observable(initialValue = Default.ISLAND_NAME) { _, _, newValue ->
        notifyObservers { it.onUpdateIslandName(newValue) }
    }
    var mapWidth: Int by observable(initialValue = Default.MAP_WIDTH) { _, _, newValue ->
        notifyObservers { it.onUpdateMapWidth(newValue) }
    }
    var mapHeight: Int by observable(initialValue = Default.MAP_HEIGHT) { _, _, newValue ->
        notifyObservers { it.onUpdateMapHeight(newValue) }
    }
    var initialMoney: Int by observable(initialValue = Default.INITIAL_MONEY) { _, _, newValue ->
        notifyObservers { it.onUpdateInitialMoney(newValue) }
    }
    val familySize = Default.FAMILY_SIZE
    val shopSize = Default.SHOP_SIZE
    val salary = Default.SALARY
    val taxRate = Default.TAX_RATE
    val serviceCost = Default.SERVICE_COST
    val houseBuildCost = Default.HOUSE_BUILD_COST
    val commBuildCost = Default.COMM_BUILD_COST
    val roadBuildCost = Default.ROAD_BUILD_COST

    override fun notifyMe(them: SettingsObserver) {
        them.apply {
            onUpdateIslandName(islandName)
            onUpdateMapWidth(mapWidth)
            onUpdateMapHeight(mapHeight)
            onUpdateInitialMoney(initialMoney)
        }
    }

    /**
     * Default values for all settings.
     */
    object Default {
        const val ISLAND_NAME = "My Island"
        const val MAP_WIDTH = 50
        const val MAP_HEIGHT = 10
        const val INITIAL_MONEY = 1000
        const val FAMILY_SIZE = 4
        const val SHOP_SIZE = 6
        const val SALARY = 10
        const val TAX_RATE = 0.3f
        const val SERVICE_COST = 2
        const val HOUSE_BUILD_COST = 100
        const val COMM_BUILD_COST = 500
        const val ROAD_BUILD_COST = 20
    }
}