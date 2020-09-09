package com.alec.mad.p4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // We've set everything up inside a fragment. This isn't strictly necessary, but can assist
        // maintainability, because fragments permit greater UI design flexibility.
        val fm: FragmentManager = supportFragmentManager
        val frag = fm.findFragmentById(R.id.fragmentContainer) ?: MainFragment()
        fm.beginTransaction().add(R.id.fragmentContainer, frag).commit()
    }
}