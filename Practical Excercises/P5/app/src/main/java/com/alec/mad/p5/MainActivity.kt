package com.alec.mad.p5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    object ID {
        const val FRAME = R.id.mainActivityFrame
    }

    private lateinit var frameLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.frameLayout = findViewById(ID.FRAME)

        supportFragmentManager.also { fm ->
            if (fm.findFragmentById(ID.FRAME) == null) {
                fm.beginTransaction().also { transaction ->
                    transaction.add(
                        ID.FRAME,
                        MainMenuFragment()
                    )

                    transaction.commit()
                }
            }
        }
    }
}