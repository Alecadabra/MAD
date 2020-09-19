package com.alec.mad.assignment1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.alec.mad.assignment1.fragment.StartingScreenFragment
import com.alec.mad.assignment1.fragment.selector.AbstractSelectorFragment
import com.alec.mad.assignment1.fragment.StatsBarFragment
import com.alec.mad.assignment1.fragment.selector.FlagQuestionSelectorFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        val fm: FragmentManager = supportFragmentManager
        var fragTransaction = fm.beginTransaction()

        // Add the starting screen to the game frame
        fragTransaction = fragTransaction.add(
            R.id.gameFragmentFrame,
            fm.findFragmentById(R.id.gameFragmentFrame) ?: StartingScreenFragment()
        )

        // Add the stats bar frame to it's frame
        fragTransaction = fragTransaction.add(
            R.id.statsBarFrame,
            fm.findFragmentById(R.id.statsBarFrame) ?: StatsBarFragment()
        )

        // Commit changes
        fragTransaction.commit()
    }
}