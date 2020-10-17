package com.alec.mad.assignment2.model

import com.alec.mad.assignment2.singleton.Settings
import kotlin.math.roundToInt

class GameData(
    val map: List<List<MapElement>>,
    var money: Int
) {
    var gameTime = 1
        private set
    var numResidential = 0
    var numCommercial = 0
    val population
        get() = Settings.familySize * numResidential
    val employmentRate
        get() = runCatching {
            1.coerceAtMost(numCommercial * Settings.shopSize / population)
        }.getOrNull() ?: 0
    var income = 0
        private set

    fun timeStep() {
        this.gameTime++
        this.income = (this.population * (
                this.employmentRate * Settings.salary * Settings.taxRate - Settings.serviceCost
                )).roundToInt()
        this.money += this.income
    }
}