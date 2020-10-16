package com.alec.mad.assignment2.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.model.StructureType
import com.alec.mad.assignment2.singleton.Settings
import com.alec.mad.assignment2.view.fragment.MapTileGridFragment
import com.alec.mad.assignment2.view.fragment.StructureSelectorFragment

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val rvHeight = Settings.mapHeight

        supportFragmentManager.findFragmentById(R.id.mapActivityMapTileGridFrame)
            ?: supportFragmentManager.beginTransaction().also { transaction ->
                transaction.add(
                    R.id.mapActivityMapTileGridFrame,
                    MapTileGridFragment()
                )
                transaction.commit()
        }

        supportFragmentManager.findFragmentById(R.id.mapActivityStructureSelectorFrame)
            ?: supportFragmentManager.beginTransaction().also { transaction ->
                transaction.add(
                    R.id.mapActivityStructureSelectorFrame,
                    StructureSelectorFragment(StructureType.RESIDENTIAL)
                )
                transaction.commit()
            }
    }

    companion object {
        fun getIntent(c: Context): Intent = Intent(c, MapActivity::class.java)
    }
}