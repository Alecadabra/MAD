package com.alec.mad.assignment2.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.Settings
import kotlin.math.roundToInt

/**
 * Activity to edit and view a [Settings] object.
 */
class SettingsActivity : AppCompatActivity() {

    /**
     * View references.
     */
    private lateinit var views: Views

    /**
     * Settings to view/edit.
     */
    private lateinit var settings: Settings

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        this.title = "Settings"

        // Restore settings from intent and bundle
        this.settings = Settings()
        intent.extras?.also { extras ->
            writeBundleToSettings(extras, this.settings)
        }
        if (savedInstanceState != null) {
            writeBundleToSettings(savedInstanceState, this.settings)
        }

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

        this.settings.also { nullSafeSettings ->
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
        }

        this.views.backButton.setOnClickListener { onBackPressed() }
        this.views.saveButton.setOnClickListener { save() }
    }

    /**
     * Save all mutable settings to a result intent and set as result.
     */
    private fun save() {
        fun toast(text: String) { Toast.makeText(this, text, Toast.LENGTH_SHORT).show()  }

        // Get values from EditTexts and set result intent
        this.settings.also { nullSafeSettings ->
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

            val extras = Bundle()
            val resultIntent = Intent()
            writeSettingsToBundle(nullSafeSettings, extras)
            resultIntent.putExtras(extras)
            setResult(RESULT_OK, resultIntent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        writeSettingsToBundle(this.settings, outState)
    }

    /**
     * Holds all view references.
     */
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
        private const val PATH = "com.alec.mad.assignment2.view.activity.SettingsActivity"
        private const val ISLAND_NAME = "$PATH.islandName"
        private const val MAP_WIDTH = "$PATH.mapWidth"
        private const val MAP_HEIGHT = "$PATH.mapHeight"
        private const val INITIAL_MONEY = "$PATH.initialMoney"

        fun getIntent(
            c: Context,
            settings: Settings
        ): Intent = Intent(c, SettingsActivity::class.java).also { intent ->
            val extras = Bundle()
            writeSettingsToBundle(settings, extras)
            intent.putExtras(extras)
        }

        /**
         * Writes all mutable settings to the [bundle].
         */
        fun writeSettingsToBundle(settings: Settings, bundle: Bundle) {
            bundle.putString(ISLAND_NAME, settings.islandName)
            bundle.putInt(MAP_WIDTH, settings.mapWidth)
            bundle.putInt(MAP_HEIGHT, settings.mapHeight)
            bundle.putInt(INITIAL_MONEY, settings.initialMoney)
        }

        /**
         * Puts all mutable settings from the [bundle] generated with [writeSettingsToBundle] to
         * [settings].
         */
        fun writeBundleToSettings(bundle: Bundle, settings: Settings) {
            settings.islandName = getIslandNameOrNull(bundle) ?: settings.islandName
            settings.mapWidth = getMapWidthOrNull(bundle) ?: settings.mapWidth
            settings.mapHeight = getMapHeightOrNull(bundle) ?: settings.mapHeight
            settings.initialMoney = getInitialMoneyOrNull(bundle) ?: settings.initialMoney
        }

        private fun getIslandNameOrNull(bundle: Bundle?) = bundle?.getString(
            ISLAND_NAME,
            Settings.Default.ISLAND_NAME
        )

        private fun getMapWidthOrNull(bundle: Bundle?) = bundle?.getInt(
            MAP_WIDTH,
            Settings.Default.MAP_WIDTH
        )

        private fun getMapHeightOrNull(bundle: Bundle?) = bundle?.getInt(
            MAP_HEIGHT,
            Settings.Default.MAP_HEIGHT
        )

        private fun getInitialMoneyOrNull(bundle: Bundle?) = bundle?.getInt(
            INITIAL_MONEY,
            Settings.Default.INITIAL_MONEY
        )
    }
}