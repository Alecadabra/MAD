package com.alec.mad.assignment2.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.controller.RequestCodes
import com.alec.mad.assignment2.controller.database.DatabaseManager
import com.alec.mad.assignment2.model.Settings
import com.alec.mad.assignment2.model.StructureType
import com.alec.mad.assignment2.singleton.State
import com.alec.mad.assignment2.singleton.StructureData

/**
 * Activity to show the main menu.
 */
class TitleActivity : AppCompatActivity() {

    /**
     * View references.
     */
    private lateinit var views: Views

    /**
     * Settings object to use to start a new game or edit.
     */
    private lateinit var settings: Settings

    /**
     * Cute little icon to display
     */
    @DrawableRes
    private var iconId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        supportActionBar?.hide() // Easier than playing around with manifests

        // Start up the database manager
        val dbManager = DatabaseManager(this)

        // Restore settings from database and bundle
        this.settings = Settings()
        dbManager.settingsCursor.use { cur ->
            cur.moveToFirst()
            while (!cur.isAfterLast) {
                settings.islandName = cur.islandName
                settings.mapWidth = cur.mapWidth
                settings.mapHeight = cur.mapHeight
                settings.initialMoney = cur.initialMoney
                cur.moveToNext()
            }
        }
        if (savedInstanceState != null) {
            SettingsActivity.writeBundleToSettings(savedInstanceState, this.settings)
        }

        // Restore icon from bundle
        this.iconId = savedInstanceState?.getInt(BUNDLE_ICON)
            ?: StructureData.filter { it.structureType != StructureType.ROAD }.random().imageId

        // Get view references
        this.views = Views(
            newGameBtn = findViewById(R.id.titleActivityNewGame),
            continueBtn = findViewById(R.id.titleActivityContinue),
            settingsBtn = findViewById(R.id.titleActivitySettings),
            iconImageView = findViewById(R.id.titleActivityIcon)
        )

        this.views.newGameBtn.setOnClickListener {
            // Initialise game
            State.initialiseNewGame(this.settings, dbManager)
            // Start the game activity
            startActivity(GameActivity.getIntent(this))
        }

        this.views.continueBtn.setOnClickListener {
            // Initialise game
            State.initialiseContinueGame(this.settings, dbManager)
            // Start the game activity
            startActivity(GameActivity.getIntent(this))
        }

        this.views.settingsBtn.setOnClickListener {
            startActivityForResult(
                SettingsActivity.getIntent(this, this.settings),
                RequestCodes.SETTINGS
            )
        }

        this.views.iconImageView.setImageResource(this.iconId ?: error("Icon null"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCodes.SETTINGS && resultCode == RESULT_OK && data != null) {
            data.extras?.also { extras ->
                SettingsActivity.writeBundleToSettings(extras, this.settings)
            } ?: error("Settings bundle did not include extras")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save settings
        SettingsActivity.writeSettingsToBundle(this.settings, outState)
        // Save icon
        this.iconId?.also { outState.putInt(BUNDLE_ICON, it) }
    }

    /**
     * Holds all view references.
     */
    private class Views(
        val newGameBtn: Button,
        val continueBtn: Button,
        val settingsBtn: Button,
        val iconImageView: ImageView
    )

    companion object {
        private const val PATH = "com.alec.mad.assignment2.view.activity"
        const val BUNDLE_ICON = "$PATH.icon"
    }
}