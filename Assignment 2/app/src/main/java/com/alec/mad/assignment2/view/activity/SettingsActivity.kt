package com.alec.mad.assignment2.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.Settings
import kotlin.math.roundToInt

class SettingsActivity : AppCompatActivity() {

    private lateinit var views: Views

    private var settings: Settings? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        this.title = "Settings"

        // Restore settings from intent or bundle, else initialise new settings
        this.settings = writeBundleToSettings(this.settings ?: Settings(), intent.extras)
        this.settings = writeBundleToSettings(this.settings ?: Settings(), savedInstanceState)

        this.views = Views(
            islandNameEditText = findViewById(R.id.settingsActivityIslandNameEditText),
            mapWidthEditText = findViewById(R.id.settingsActivityMapWidthEditText),
            mapHeightEditText = findViewById(R.id.settingsActivityMapHeightEditText),
            initialMoneyEditText = findViewById(R.id.settingsActivityInitialMoneyEditText),
            familySizeText = findViewById(R.id.settingsActivityFamilySizeText),
            shopSizeText = findViewById(R.id.settingsActivityShopSizeText),
            salaryText = findViewById(R.id.settingsActivitySalaryText),
            taxRateText = findViewById(R.id.settingsActivityTaxRateText),
            serviceCostText = findViewById(R.id.settingsActivityServiceCostText),
            houseCostText = findViewById(R.id.settingsActivityResidentialCostText),
            commercialCostText = findViewById(R.id.settingsActivityCommercialCostText),
            roadCostText = findViewById(R.id.settingsActivityRoadCostText),
            backButton = findViewById(R.id.settingsActivityBackButton),
            saveButton = findViewById(R.id.settingsActivitySaveButton)
        )

        this.settings?.also { nullSafeSettings ->
            this.views.islandNameEditText.setText(nullSafeSettings.islandName)
            this.views.mapWidthEditText.setText(nullSafeSettings.mapWidth.toString())
            this.views.mapHeightEditText.setText(nullSafeSettings.mapHeight.toString())
            this.views.initialMoneyEditText.setText(nullSafeSettings.initialMoney.toString())
            this.views.familySizeText.text = nullSafeSettings.familySize.toString()
            this.views.shopSizeText.text = nullSafeSettings.shopSize.toString()
            this.views.salaryText.text = "$${nullSafeSettings.salary}"
            this.views.taxRateText.text = "${(nullSafeSettings.taxRate * 100).roundToInt()}%"
            this.views.serviceCostText.text = "$${nullSafeSettings.serviceCost}"
            this.views.houseCostText.text = "$${nullSafeSettings.houseBuildCost}"
            this.views.commercialCostText.text = "$${nullSafeSettings.commBuildCost}"
            this.views.roadCostText.text = "$${nullSafeSettings.roadBuildCost}"
        } ?: error("Settings null")

        this.views.backButton.setOnClickListener { onBackPressed() }
        this.views.saveButton.setOnClickListener { save() }
    }

    private fun save() {
        fun toast(text: String) { Toast.makeText(this, text, Toast.LENGTH_SHORT).show()  }

        // Get values from EditTexts and set result intent
        this.settings?.also { nullSafeSettings ->
            nullSafeSettings.islandName = this.views.islandNameEditText.text.toString()

            this.views.mapWidthEditText.text?.toString()?.toIntOrNull()?.also { mapWidth ->
                nullSafeSettings.mapWidth = mapWidth
            } ?: toast("Error: Map width must be an integer")

            this.views.mapHeightEditText.text?.toString()?.toIntOrNull()?.also { mapHeight ->
                nullSafeSettings.mapHeight = mapHeight
            } ?: toast("Error: Map height must be an integer")

            this.views.initialMoneyEditText.text?.toString()?.toIntOrNull()?.also { initialMoney ->
                nullSafeSettings.initialMoney = initialMoney
            } ?: toast("Error: Initial money must be an integer")

            Bundle().also { extras ->
                writeSettingsToBundle(nullSafeSettings, extras)
                setResult(RESULT_OK, Intent().also { it.putExtras(extras) })
            }
        } ?: error("Settings null")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        this.settings?.also { nullSafeSettings ->
            writeSettingsToBundle(nullSafeSettings, outState)
        }
    }

    private class Views(
        val islandNameEditText: EditText,
        val mapWidthEditText: EditText,
        val mapHeightEditText: EditText,
        val initialMoneyEditText: EditText,
        val familySizeText: TextView,
        val shopSizeText: TextView,
        val salaryText: TextView,
        val taxRateText: TextView,
        val serviceCostText: TextView,
        val houseCostText: TextView,
        val commercialCostText: TextView,
        val roadCostText: TextView,
        val backButton: Button,
        val saveButton: Button
    )

    companion object {
        private const val PACKAGE = "com.alec.mad.assignment2.view.activity.SettingsActivity"
        private const val ISLAND_NAME = "$PACKAGE.islandName"
        private const val MAP_WIDTH = "$PACKAGE.mapWidth"
        private const val MAP_HEIGHT = "$PACKAGE.mapHeight"
        private const val INITIAL_MONEY = "$PACKAGE.initialMoney"

        fun getIntent(
            c: Context,
            settings: Settings
        ): Intent = Intent(c, SettingsActivity::class.java).also { intent ->
            intent.extras?.also { extras -> writeSettingsToBundle(settings, extras) }
        }

        fun writeSettingsToBundle(settings: Settings, bundle: Bundle) {
            bundle.putString(ISLAND_NAME, settings.islandName)
            bundle.putInt(MAP_WIDTH, settings.mapWidth)
            bundle.putInt(MAP_HEIGHT, settings.mapHeight)
            bundle.putInt(INITIAL_MONEY, settings.initialMoney)
        }

        fun writeBundleToSettings(settings: Settings, bundle: Bundle?): Settings {
            return bundle?.let { nullSafeBundle ->
                settings.also {
                    it.islandName = getIslandNameOrNull(nullSafeBundle) ?: it.islandName
                    it.mapWidth = getMapWidthOrNull(nullSafeBundle) ?: it.mapWidth
                    it.mapHeight = getMapHeightOrNull(nullSafeBundle) ?: it.mapHeight
                    it.initialMoney = getInitialMoneyOrNull(nullSafeBundle) ?: it.initialMoney
                }
            } ?: settings
        }

        private fun getIslandNameOrNull(bundle: Bundle?) = bundle?.getString(ISLAND_NAME)?.also {
            Log.d("SettingsActivity", "Resolved island name ($it)")
        } ?: run { Log.d("SettingsActivity", "Failed to resolve island name"); null }

        private fun getMapWidthOrNull(bundle: Bundle?) = bundle?.getInt(
            MAP_WIDTH,
            Settings.Default.MAP_WIDTH
        ).let {
            if (it == Settings.Default.MAP_WIDTH) {
                Log.d("SettingsActivity", "Failed to resolve map width")
                null
            } else {
                Log.d("SettingsActivity", "Resolved map width ($it)")
                it
            }
        }

        private fun getMapHeightOrNull(bundle: Bundle?) = bundle?.getInt(
            MAP_HEIGHT,
            Settings.Default.MAP_HEIGHT
        ).let {
            if (it == Settings.Default.MAP_HEIGHT) {
                Log.d("SettingsActivity", "Failed to resolve map ht")
                null
            } else {
                Log.d("SettingsActivity", "Resolved map height ($it)")
                it
            }
        }

        private fun getInitialMoneyOrNull(bundle: Bundle?) = bundle?.getInt(
            INITIAL_MONEY,
            Settings.Default.INITIAL_MONEY
        ).let {
            if (it == Settings.Default.INITIAL_MONEY) {
                Log.d("SettingsActivity", "Failed to resolve init money")
                null
            } else {
                Log.d("SettingsActivity", "Resolved initial money ($it)")
                it
            }
        }
    }
}