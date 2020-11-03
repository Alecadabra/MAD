package com.alec.mad.assignment2.model

import androidx.fragment.app.Fragment
import com.alec.mad.assignment2.controller.BuildIntent
import com.alec.mad.assignment2.singleton.Settings
import com.alec.mad.assignment2.view.fragment.tool.BuildToolFragment
import com.alec.mad.assignment2.view.fragment.tool.SimpleToolFragment
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
        }.getOrDefault(0)

    var income = 0
        private set

    var buildIntent: BuildIntent? = null

    var currentTool = Tool.BUILD_COMMERCIAL

    fun timeStep() {
        this.gameTime++
        this.income = (this.population * (
                this.employmentRate * Settings.salary * Settings.taxRate - Settings.serviceCost
                )).roundToInt()
        this.money += this.income
    }

    enum class Tool(val displayName: String, val fragment: Fragment) {
        BUILD_RESIDENTIAL("Build - Residential", BuildToolFragment(StructureType.RESIDENTIAL)),
        BUILD_COMMERCIAL("Build - Commercial", BuildToolFragment(StructureType.COMMERCIAL)),
        BUILD_ROAD("Build - Road", BuildToolFragment(StructureType.ROAD)),
        DEMOLISH("Demolish", SimpleToolFragment("Tap a structure to demolish")),
        INFO("Info", SimpleToolFragment("Tap a structure to view info"));

        val isBuild: Boolean
            get() = this == BUILD_RESIDENTIAL || this == BUILD_COMMERCIAL || this == BUILD_ROAD
    }
}