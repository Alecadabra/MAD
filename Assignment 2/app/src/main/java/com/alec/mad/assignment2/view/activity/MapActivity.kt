package com.alec.mad.assignment2.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.LightingColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.GameData
import com.alec.mad.assignment2.singleton.Settings
import com.alec.mad.assignment2.singleton.State
import com.alec.mad.assignment2.view.fragment.MapTileGridFragment
import kotlin.math.absoluteValue

@SuppressLint("SetTextI18n")
class MapActivity : AppCompatActivity() {

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

        supportFragmentManager.findFragmentById(R.id.mapActivityMapTileGridFrame)
            ?: supportFragmentManager.beginTransaction().also { transaction ->
                transaction.add(
                    R.id.mapActivityMapTileGridFrame,
                    MapTileGridFragment()
                )
                transaction.commit()
        }

        changeTool(State.gameData.currentTool)
        enableAllToolsExcept(this.views.toolResidential)

        this.title = Settings.islandName
        updateStats()

        this.views.toolResidential.setOnClickListener { view ->
            changeTool(GameData.Tool.BUILD_RESIDENTIAL)
            enableAllToolsExcept(view)
        }
        this.views.toolCommercial.setOnClickListener { view ->
            changeTool(GameData.Tool.BUILD_COMMERCIAL)
            enableAllToolsExcept(view)
        }
        this.views.toolRoad.setOnClickListener { view ->
            changeTool(GameData.Tool.BUILD_ROAD)
            enableAllToolsExcept(view)
        }

        this.views.toolDemolish.setOnClickListener { view ->
            changeTool(GameData.Tool.DEMOLISH)
            enableAllToolsExcept(view)
        }

        this.views.toolInfo.setOnClickListener { view ->
            changeTool(GameData.Tool.INFO)
            enableAllToolsExcept(view)
        }

        this.views.timeStep.setOnClickListener {
            State.gameData.timeStep()
            updateStats()
            if (State.gameData.money < 0) {
                // Lose condition
                Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show()
                this.views.tools.forEach {
                    it.isClickable = true
                    it.isEnabled = true
                }
                supportFragmentManager.findFragmentById(R.id.mapActivityToolFrame)?.also { frag ->
                    supportFragmentManager.beginTransaction().also { transaction ->
                        transaction.remove(frag)
                        transaction.commit()
                    }
                }
            }
        }
    }

    private fun updateStats() {
        val gameData = State.gameData
        this.views.statsTime.text = "Day ${gameData.gameTime}"
        this.views.statsPopulation.text = "${gameData.population} Residents"
        this.views.statsTemperature.text = "TODO Degrees"
        this.views.statsMoney.text = "Money: $${gameData.money}"
        val incomeSign = when {
            gameData.income < 0 -> "-"
            gameData.income > 0 -> "+"
            else -> ""
        }
        this.views.statsIncome.text = "Income: ${incomeSign}$${gameData.income.absoluteValue}"
        this.views.statsEmployment.text = "${gameData.employmentRate}% Employment"
    }

    private fun changeTool(tool: GameData.Tool) {
        supportFragmentManager.beginTransaction().also { transaction ->
            transaction.replace(
                R.id.mapActivityToolFrame,
                tool.fragment
            )
            transaction.commit()
        }
        this.views.toolText.text = "Selected Tool\n${tool.displayName}"
        State.gameData.currentTool = tool
    }

    private fun enableAllToolsExcept(view: View) {
        this.views.tools.forEach {
            it.isClickable = true
            it.isEnabled = true
            it.background.clearColorFilter()
        }
        view.isClickable = false
        view.isEnabled = false
        view.background.colorFilter = LightingColorFilter(0x000000, 0xAAFFAA)
    }

    class Views(
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
        val tools = setOf(toolResidential, toolCommercial, toolRoad, toolDemolish, toolInfo)
    }

    companion object {
        fun getIntent(c: Context): Intent = Intent(c, MapActivity::class.java)
    }
}