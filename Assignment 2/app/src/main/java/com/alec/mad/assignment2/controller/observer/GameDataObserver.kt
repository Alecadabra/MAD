package com.alec.mad.assignment2.controller.observer

import com.alec.mad.assignment2.controller.BuildIntent
import com.alec.mad.assignment2.model.GameData

/**
 * Implementing classes observe changes to a [GameData] object.
 */
interface GameDataObserver {

    fun onUpdateMoney(money: Int) {}

    fun onUpdateGameTime(gameTime: Int) {}

    fun onUpdateNumResidential(namResidential: Int) {}

    fun onUpdateNumCommercial(numCommercial: Int) {}

    fun onUpdatePopulation(population: Int) {}

    fun onUpdateEmploymentRate(employmentRate: Float) {}

    fun onUpdateIncome(income: Int) {}

    fun onUpdateBuildIntent(buildIntent: BuildIntent?) {}

    fun onUpdateCurrentTool(currentTool: GameData.Tool) {}

    fun onUpdateTemperature(temperature: String) {}

}