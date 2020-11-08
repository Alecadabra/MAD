package com.alec.mad.assignment2.model

import androidx.fragment.app.Fragment
import com.alec.mad.assignment2.controller.BuildIntent
import com.alec.mad.assignment2.controller.WeatherHandler
import com.alec.mad.assignment2.controller.observer.GameDataObserver
import com.alec.mad.assignment2.controller.observer.GameMapObserver
import com.alec.mad.assignment2.controller.observer.ObservableState
import com.alec.mad.assignment2.view.fragment.tool.BuildToolFragment
import com.alec.mad.assignment2.view.fragment.tool.SimpleToolFragment
import kotlin.math.roundToInt
import kotlin.properties.Delegates.observable

/**
 * Model to hold the entire game state. One instance per game instance.
 */
class GameData(
    val gameMap: GameMap,
    val settings: Settings
) : ObservableState<GameDataObserver>(), GameDataObserver, GameMapObserver {

    /**
     * Game time, represented by 'days' in the game UI.
     */
    var gameTime: Int by observable(initialValue = 1) { _, _, newValue ->
        notifyObservers { it.onUpdateGameTime(newValue) }
    }

    /**
     * Player's money, used to purchase buildings, the game is lost if this goes below zero.
     */
    var money: Int by observable(initialValue = this.settings.initialMoney) { _, _, newValue ->
        notifyObservers { it.onUpdateMoney(newValue) }
    }

    /**
     * The number of residential building that have been built.
     */
    var numResidential: Int by observable(initialValue = 0) { _, _, newValue ->
        notifyObservers { it.onUpdateNumResidential(newValue) }
    }

    /**
     * The number of commercial buildings that have been built.
     */
    var numCommercial: Int by observable(initialValue = 0) { _, _, newValue ->
        notifyObservers { it.onUpdateNumCommercial(newValue) }
    }

    /**
     * The most recent change in [money].
     */
    var income: Int by observable(initialValue = 0) { _, _, newValue ->
        notifyObservers { it.onUpdateIncome(newValue) }
    }

    /**
     * The number of residents.
     */
    private val population
        get() = this.settings.familySize * numResidential

    /**
     * The employment rate (From 0 to 1).
     */
    private val employmentRate: Float
        get() {
            val commercialUnits = this.numCommercial.toFloat() * this.settings.shopSize.toFloat()
            val residentialUnits = this.population.toFloat()
            val ratio = commercialUnits / residentialUnits
            // Ratio cannot surpass 1, and divide by zero defaults to 0
            return ratio.coerceAtMost(1f).takeIf { it.isFinite() } ?: 0f
        }

    /**
     * The most recent real-world weather temperature value, formatted as a string.
     */
    var temperature: String by observable(initialValue = "Loading") { _, _, newValue ->
        notifyObservers { it.onUpdateTemperature(newValue) }
    }

    /**
     * The current [BuildIntent].
     */
    var buildIntent: BuildIntent? by observable(initialValue = null) { _, _, newValue ->
        notifyObservers { it.onUpdateBuildIntent(newValue) }
    }

    /**
     * The currently selected [Tool].
     */
    var currentTool: Tool by observable(initialValue = Tool.BUILD_RESIDENTIAL) { _, _, newValue ->
        notifyObservers { it.onUpdateCurrentTool(newValue) }
    }

    /**
     * The instance of [WeatherHandler] that periodically updates [temperature].
     */
    private val weatherHandler = WeatherHandler(this)

    init {
        // Observe yourself!
        this.observers.add(this)

        // Observe the map changes for numResidential and numCommercial
        this.gameMap.observers.add(this)
        // Notify for each of the existing structures for initial values
        this.gameMap.notifyMe(this)

        // Start the weather handler
        this.weatherHandler.start()
    }

    override fun notifyMe(them: GameDataObserver) {
        them.apply {
            onUpdateMoney(money)
            onUpdateGameTime(gameTime)
            onUpdateNumResidential(numResidential)
            onUpdateNumCommercial(numCommercial)
            onUpdatePopulation(population)
            onUpdateEmploymentRate(employmentRate)
            onUpdateIncome(income)
            onUpdateBuildIntent(buildIntent)
            onUpdateCurrentTool(currentTool)
            onUpdateTemperature(temperature)
        }
    }

    override fun onUpdateGameTime(gameTime: Int) {
        // Income = population * (employmentRate * salary * taxRate - serviceCost)
        this.income = let {
            val grossTax = this.employmentRate * this.settings.salary * this.settings.taxRate
            val netTax = grossTax - this.settings.serviceCost
            val netIncome = this.population * netTax
            netIncome.roundToInt()
        }

        this.money += this.income
    }

    override fun onUpdateNumResidential(namResidential: Int) {
        // The population changes when numResidential changes
        notifyObservers { it.onUpdatePopulation(this.population) }
    }

    override fun onUpdateNumCommercial(numCommercial: Int) {
        notifyObservers { it.onUpdateEmploymentRate(this.employmentRate) }
    }

    override fun onUpdateCurrentTool(currentTool: Tool) {
        // Clear the build intent whenever the tool changes
        this.buildIntent = null
    }

    override fun onUpdateMapElement(i: Int, j: Int, mapElement: GameMap.MapElement) {
        // Count residential and commercial
        if (mapElement.structure?.structureType == StructureType.RESIDENTIAL) {
            this.numResidential++
        } else if (mapElement.structure?.structureType == StructureType.COMMERCIAL) {
            this.numCommercial++
        }
    }

    /**
     * Available tools to build, view info or demolish. Each has a display text and an
     * associated fragment instance to place in the tool frame.
     */
    enum class Tool(val displayName: String, val fragment: Fragment) {
        BUILD_RESIDENTIAL("Build - Residential", BuildToolFragment(StructureType.RESIDENTIAL)),
        BUILD_COMMERCIAL("Build - Commercial", BuildToolFragment(StructureType.COMMERCIAL)),
        BUILD_ROAD("Build - Road", BuildToolFragment(StructureType.ROAD)),
        DEMOLISH("Demolish", SimpleToolFragment("Tap a structure to demolish")),
        INFO("Info", SimpleToolFragment("Tap a structure to view info"))
    }
}

