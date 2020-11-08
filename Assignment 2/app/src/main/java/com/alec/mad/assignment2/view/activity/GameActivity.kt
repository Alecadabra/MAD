package com.alec.mad.assignment2.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.LightingColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.GameData.Tool
import com.alec.mad.assignment2.controller.observer.GameDataObserver
import com.alec.mad.assignment2.singleton.State
import com.alec.mad.assignment2.view.fragment.MapTileGridFragment
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Activity to display the main game screen
 */
@SuppressLint("SetTextI18n")
class GameActivity : AppCompatActivity(), GameDataObserver {

    /**
     * View references.
     */
    private lateinit var views: Views

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        this.views = Views(
            statsTime = findViewById(R.id.mapActivityStatusBarTime),
            statsPopulation = findViewById(R.id.mapActivityStatusBarPopulation),
            statsTemperature = findViewById(R.id.mapActivityStatusBarTemperature),
            statsMoney = findViewById(R.id.mapActivityStatusBarMoney),
            statsIncome = findViewById(R.id.mapActivityStatusBarIncome),
            statsEmployment = findViewById(R.id.mapActivityStatusBarEmployment),
            toolResidential = findViewById(R.id.mapActivityToolSelectorResidential),
            toolCommercial = findViewById(R.id.mapActivityToolSelectorCommercial),
            toolRoad = findViewById(R.id.mapActivityToolSelectorRoad),
            toolDemolish = findViewById(R.id.mapActivityToolSelectorDemolish),
            toolInfo = findViewById(R.id.mapActivityToolSelectorInfo),
            timeStep = findViewById(R.id.mapActivityTimeStep),
            toolText = findViewById(R.id.mapActivityToolText)
        )

        // Bring in map tile grid fragment
        supportFragmentManager.findFragmentById(R.id.mapActivityMapTileGridFrame)
            ?: supportFragmentManager.beginTransaction().also { transaction ->
                transaction.add(R.id.mapActivityMapTileGridFrame, MapTileGridFragment())
                transaction.commit()
        }

        // Observe changes to the game data
        State.gameData.observers.add(this)
        State.gameData.notifyMe(this)

        // Set top bar text
        this.title = State.gameData.settings.islandName

        // Set on click listeners for tools
        this.views.tools.forEach { toolBtn ->
            toolBtn.setOnClickListener {
                State.gameData.currentTool = this.views.buttonsToTools[toolBtn]
                    ?: error("Buttons to tools maps incorrect")
            }
        }

        this.views.timeStep.setOnClickListener {
            State.gameData.gameTime++
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Deregister observers
        State.gameData.observers.remove(this)
    }

    override fun onUpdateCurrentTool(currentTool: Tool) {
        // Move fragment in
        supportFragmentManager.beginTransaction().also { transaction ->
            transaction.replace(
                R.id.mapActivityToolFrame,
                currentTool.fragment
            )
            transaction.commit()
        }

        // Update text
        this.views.toolText.text = "Selected Tool\n${currentTool.displayName}"

        // Enable only this tool
        this.views.toolsToButtons[currentTool]?.also { view ->
            this.views.tools.forEach {
                it.isClickable = true
                it.isEnabled = true
                it.background.clearColorFilter()
            }
            view.isClickable = false
            view.isEnabled = false
            view.background.colorFilter = LightingColorFilter(0x000000, 0xAAFFAA)
        }
    }

    override fun onUpdateGameTime(gameTime: Int) {
        this.views.statsTime.text = "Day $gameTime"
    }

    override fun onUpdatePopulation(population: Int) {
        this.views.statsPopulation.text = "$population Residents"
    }

    override fun onUpdateTemperature(temperature: String) {
        this.views.statsTemperature.text = "Temperature: $temperature"
    }

    override fun onUpdateMoney(money: Int) {
        this.views.statsMoney.text = "Money: $$money"

        if (State.gameData.money < 0) {
            // Lose condition
            Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show()

            // Buttons that can't be pressed anymore
            val buttonsToDisable = this.views.tools.toMutableList().also {
                it.add(this.views.timeStep)
            }
            buttonsToDisable.forEach {
                it.isClickable = false
                it.isEnabled = false
            }

            // Remove tool fragment
            supportFragmentManager.findFragmentById(R.id.mapActivityToolFrame)?.also { frag ->
                supportFragmentManager.beginTransaction().also { transaction ->
                    transaction.remove(frag)
                    transaction.commit()
                }
            }

            // Remove build intent if present
            State.gameData.buildIntent = null
        }
    }

    override fun onUpdateIncome(income: Int) {
        val incomeSign = when {
            income < 0 -> "-"
            income > 0 -> "+"
            else -> ""
        }
        this.views.statsIncome.text = "Income: ${incomeSign}$${income.absoluteValue}"
    }

    override fun onUpdateEmploymentRate(employmentRate: Float) {
        this.views.statsEmployment.text = "${(employmentRate * 100).roundToInt()}% Employment"
    }

    /**
     * Holder for view instances.
     */
    private class Views(
        val statsTime: TextView,
        val statsPopulation: TextView,
        val statsTemperature: TextView,
        val statsMoney: TextView,
        val statsIncome: TextView,
        val statsEmployment: TextView,
        val toolResidential: ImageButton,
        val toolCommercial: ImageButton,
        val toolRoad: ImageButton,
        val toolDemolish: ImageButton,
        val toolInfo: ImageButton,
        val timeStep: ImageButton,
        val toolText: TextView
    ) {
        /**
         * All of the tool buttons.
         */
        val tools = setOf(toolResidential, toolCommercial, toolRoad, toolDemolish, toolInfo)

        private val toolButtonPairs = setOf(
            Tool.BUILD_RESIDENTIAL to this.toolResidential,
            Tool.BUILD_COMMERCIAL to this.toolCommercial,
            Tool.BUILD_ROAD to this.toolRoad,
            Tool.DEMOLISH to this.toolDemolish,
            Tool.INFO to this.toolInfo
        )

        /**
         * Maps tool enums to their button's.
         */
        val toolsToButtons: Map<Tool, ImageButton> = toolButtonPairs.toMap()

        /**
         * Maps buttons to their Tool enum's.
         */
        val buttonsToTools: Map<ImageButton, Tool> = this.toolButtonPairs.map {
            // Flip pairs
            it.second to it.first
        }.toMap()
    }

    companion object {
        fun getIntent(c: Context): Intent = Intent(c, GameActivity::class.java)
    }
}