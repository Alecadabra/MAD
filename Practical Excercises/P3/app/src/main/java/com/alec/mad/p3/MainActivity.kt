package com.alec.mad.p3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.map_container, MapFragment()).commit()
            supportFragmentManager.beginTransaction().replace(R.id.selector_container, SelectorFragment()).commit()
        }

        /*
        // Set the fragments up
        if (supportFragmentManager.findFragmentById(R.id.map_container) == null) {
            supportFragmentManager.beginTransaction().add(R.id.map_container, MapFragment())
                .commit()
        }

        if (supportFragmentManager.findFragmentById(R.id.selector_container) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.selector_container, SelectorFragment())
                .commit()
        }*/
    }
}