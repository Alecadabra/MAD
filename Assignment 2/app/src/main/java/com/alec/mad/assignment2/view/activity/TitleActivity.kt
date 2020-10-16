package com.alec.mad.assignment2.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.singleton.State

class TitleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        // TODO Change place
        // Initialise game
        State.initialise()

        // Start the map
        startActivity(MapActivity.getIntent(this))
    }
}