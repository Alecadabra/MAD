package com.alec.mad.assignment1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        val fm: FragmentManager = supportFragmentManager
        var fragTransaction = fm.beginTransaction()

        // Add the starting screen to the game frame
        fragTransaction = fragTransaction.add(
            R.id.gameFragmentFrame,
            fm.findFragmentById(R.id.gameFragmentFrame) ?: StartingScreen()
        )

        // Add the stats bar frame to it's frame
        fragTransaction = fragTransaction.add(
            R.id.statsBarFrame,
            fm.findFragmentById(R.id.statsBarFrame) ?: StatsBar()
        )

        // Commit changes
        fragTransaction.commit()
    }
}