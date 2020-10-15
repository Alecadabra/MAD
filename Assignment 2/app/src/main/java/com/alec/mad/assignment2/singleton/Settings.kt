package com.alec.mad.assignment2.singleton

object Settings {
    val mapWidth = Default.MAP_WIDTH
    val mapHeight = Default.MAP_HEIGHT
    val initialMoney = Default.INITIAL_MONEY
    val familySize = Default.FAMILY_SIZE
    val shopSize = Default.SHOP_SIZE
    val salary = Default.SALARY
    val taxRate = Default.TAX_RATE
    val serviceCost = Default.SERVICE_COST
    val houseBuildCost = Default.HOUSE_BUILD_COST
    val commBuildCost = Default.COMM_BUILD_COST
    val roadBuildCost = Default.ROAD_BUILD_COST

    private object Default {
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