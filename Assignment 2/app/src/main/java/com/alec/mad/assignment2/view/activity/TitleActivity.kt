package com.alec.mad.assignment2.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.controller.database.DatabaseManager
import com.alec.mad.assignment2.model.Settings
import com.alec.mad.assignment2.model.observer.GameDataObserver
import com.alec.mad.assignment2.singleton.State

class TitleActivity : AppCompatActivity() {

    private lateinit var views: Views

    private lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        supportActionBar?.hide() // Easier than playing around with manifests

        // Restore settings from bundle
        this.settings = Settings()
        if (savedInstanceState != null) {
            SettingsActivity.writeBundleToSettings(savedInstanceState, this.settings)
        }

        // Start up the database manager
        val dbManager = DatabaseManager(this)

        // Get view references
        this.views = Views(
            newGameBtn = findViewById(R.id.titleActivityNewGame),
            continueBtn = findViewById(R.id.titleActivityContinue),
            settingsBtn = findViewById(R.id.titleActivitySettings)
        )

        this.views.newGameBtn.setOnClickListener {
            // Initialise game
            State.initialiseNewGame(this.settings, dbManager)
            // Start the map
            startActivity(MapActivity.getIntent(this))
        }

        this.views.continueBtn.setOnClickListener {
            // Initialise game
            State.initialiseContinueGame(this.settings, dbManager)
            // Start the map
            startActivity(MapActivity.getIntent(this))
        }

        this.views.settingsBtn.setOnClickListener {
            startActivityForResult(
                SettingsActivity.getIntent(this, this.settings),
                REQUEST_SETTINGS
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SETTINGS && resultCode == RESULT_OK && data != null) {
            data.extras?.also { extras ->
                SettingsActivity.writeBundleToSettings(
                    extras,
                    this.settings
                )
            } ?: error("Settings bundle did not include extras")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save settings
        SettingsActivity.writeSettingsToBundle(this.settings, outState)
    }

    class Views(
        val newGameBtn: Button,
        val continueBtn: Button,
        val settingsBtn: Button
    )

    companion object {
        private const val REQUEST_SETTINGS = 2
    }
}