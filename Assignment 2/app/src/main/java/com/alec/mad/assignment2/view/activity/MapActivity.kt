package com.alec.mad.assignment2.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.StructureType
import com.alec.mad.assignment2.singleton.Settings
import com.alec.mad.assignment2.singleton.State
import com.alec.mad.assignment2.view.fragment.MapTileGridFragment
import com.alec.mad.assignment2.view.fragment.BuildToolFragment
import com.alec.mad.assignment2.view.fragment.SimpleToolFragment

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

        val rvHeight = Settings.mapHeight

        supportFragmentManager.findFragmentById(R.id.mapActivityMapTileGridFrame)
            ?: supportFragmentManager.beginTransaction().also { transaction ->
                transaction.add(
                    R.id.mapActivityMapTileGridFrame,
                    MapTileGridFragment()
                )
                transaction.commit()
        }

        changeTool(
            BuildToolFragment().also { it.structureType = StructureType.RESIDENTIAL },
            "Build - Residential")

        updateStats()

        this.views.toolResidential.setOnClickListener { view ->
            changeTool(
                BuildToolFragment().also { it.structureType = StructureType.RESIDENTIAL },
                "Build - Residential"
            )
            enableAllToolsExcept(view)
        }
        this.views.toolCommercial.setOnClickListener { view ->
            changeTool(
                BuildToolFragment().also { it.structureType = StructureType.COMMERCIAL },
                "Build - Commercial"
            )
            enableAllToolsExcept(view)
        }
        this.views.toolRoad.setOnClickListener { view ->
            changeTool(
                BuildToolFragment().also { it.structureType = StructureType.ROAD },
                "Build - Road"
            )
            enableAllToolsExcept(view)
        }

        this.views.toolDemolish.setOnClickListener { view ->
            changeTool(
                SimpleToolFragment().also { it.text = "Tap a structure to demolish" },
                "Demolish"
            )
            enableAllToolsExcept(view)
        }

        this.views.toolInfo.setOnClickListener {
            changeTool(
                SimpleToolFragment().also { it.text = "Tap a structure to view info" },
                "Info"
            )
            enableAllToolsExcept(it)
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
                supportFragmentManager.also { fm ->
                    fm.findFragmentById(R.id.mapActivityToolFrame)?.also { fragment ->
                        fm.beginTransaction().also { transaction ->
                            transaction.remove(fragment)
                            transaction.commit()
                        }
                    }
                }
            }
        }
    }

    private fun updateStats() {
        val gameData = State.gameData
        this.views.statsTime.text = "Time: ${gameData.gameTime}"
        this.views.statsPopulation.text = "Population: ${gameData.population}"
        this.views.statsTemperature.text = "Temperature: TODO"
        this.views.statsMoney.text = "Money: $${gameData.money}"
        val incomeSign = when {
            gameData.income < 0 -> "-"
            gameData.income > 0 -> "+"
            else -> ""
        }
        this.views.statsIncome.text = "Income: ${incomeSign}$${gameData.income}"
    }

    private fun changeTool(toolFragment: Fragment, name: String) {
        supportFragmentManager.beginTransaction().also { transaction ->
            transaction.replace(
                R.id.mapActivityToolFrame,
                toolFragment
            )
            transaction.commit()
        }
        this.views.toolText.text = "Selected Tool\nname"
    }

    private fun enableAllToolsExcept(view: View) {
        this.views.tools.forEach {
            it.isClickable = true
            it.isEnabled = true
        }
        view.isClickable = false
        view.isEnabled = false
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