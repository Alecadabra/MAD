package com.alec.mad.p3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the fragments up
        if (supportFragmentManager.findFragmentById(R.id.map) == null) {
            supportFragmentManager.beginTransaction().add(R.id.map, MapFragment()).commit()
        }

        if (supportFragmentManager.findFragmentById(R.id.selector) == null) {
            supportFragmentManager.beginTransaction().add(R.id.selector, SelectorFragment())
                .commit()
        }
    }
}