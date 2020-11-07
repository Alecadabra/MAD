package com.alec.mad.assignment2.model

import androidx.fragment.app.Fragment
import com.alec.mad.assignment2.controller.BuildIntent
import com.alec.mad.assignment2.model.observer.GameDataObserver
import com.alec.mad.assignment2.model.observer.ObservableState
import com.alec.mad.assignment2.singleton.State
import com.alec.mad.assignment2.view.fragment.tool.BuildToolFragment
import com.alec.mad.assignment2.view.fragment.tool.SimpleToolFragment
import kotlin.math.roundToInt
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

class GameData(
    val map: GameMap,
    val settings: Settings
) : ObservableState<GameDataObserver>(), GameDataObserver {

    var gameTime: Int by observable(initialValue = 0) { _, _, newValue ->
        notifyObservers { it.onUpdateGameTime(newValue) }
    }

    var money: Int by observable(initialValue = this.settings.initialMoney) { _, _, newValue ->
        notifyObservers { it.onUpdateMoney(newValue) }
    }

    var numResidential: Int by observable(initialValue = 0) { _, _, newValue ->
        notifyObservers { it.onUpdateNumResidential(newValue) }
    }

    var numCommercial: Int by observable(initialValue = 0) { _, _, newValue ->
        notifyObservers { it.onUpdateNumCommercial(newValue) }
    }

    var income: Int by observable(initialValue = 0) { _, _, newValue ->
        notifyObservers { it.onUpdateIncome(newValue) }
    }

    private val population
        get() = this.settings.familySize * numResidential

    private val employmentRate: Float
        get() {
            val commercialUnits = this.numCommercial.toFloat() * this.settings.shopSize.toFloat()
            val residentialUnits = this.population.toFloat()
            return (commercialUnits / residentialUnits).coerceAtMost(1f).takeUnless {
                it.isNaN()
            } ?: 0f
        }

    var temperature: Int? by observable(initialValue = null) { _, _, newValue ->
        notifyObservers { it.onUpdateTemperature(newValue) }
    }

    var buildIntent: BuildIntent? by observable(initialValue = null) { _, _, newValue ->
        notifyObservers { it.onUpdateBuildIntent(newValue) }
    }

    var currentTool: Tool by observable(initialValue = Tool.BUILD_RESIDENTIAL) { _, _, newValue ->
        notifyObservers { it.onUpdateCurrentTool(newValue) }
    }

    init {
        // Observe yourself!
        this.observers.add(this)

        // Count numResidential and numCommercial
        this.map.flatten().forEach { mapElement ->
            mapElement.structure?.also { structure ->
                if (structure.structureType == StructureType.RESIDENTIAL) {
                    this.numResidential++
                } else if (structure.structureType == StructureType.COMMERCIAL) {
                    this.numCommercial++
                }
            }
        }
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
        this.income = (this.population * (
                this.employmentRate * this.settings.salary * this.settings.taxRate - this.settings.serviceCost
                )).roundToInt()
        this.money += this.income
    }

    override fun onUpdateNumResidential(namResidential: Int) {
        notifyObservers { it.onUpdatePopulation(this.population) }
    }

    override fun onUpdateNumCommercial(numCommercial: Int) {
        notifyObservers { it.onUpdateEmploymentRate(this.employmentRate) }
    }

    override fun onUpdateCurrentTool(currentTool: Tool) {
        this.buildIntent = null
    }

    enum class Tool(val displayName: String, val fragment: Fragment) {
        BUILD_RESIDENTIAL("Build - Residential", BuildToolFragment(StructureType.RESIDENTIAL)),
        BUILD_COMMERCIAL("Build - Commercial", BuildToolFragment(StructureType.COMMERCIAL)),
        BUILD_ROAD("Build - Road", BuildToolFragment(StructureType.ROAD)),
        DEMOLISH("Demolish", SimpleToolFragment("Tap a structure to demolish")),
        INFO("Info", SimpleToolFragment("Tap a structure to view info"))
    }
}

