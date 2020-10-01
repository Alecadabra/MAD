package com.alec.mad.p5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    private lateinit var frameLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.frameLayout = findViewById(Id.FRAME)

        supportFragmentManager.also { fm ->
            if (fm.findFragmentById(Id.FRAME) == null) {
                fm.beginTransaction().also { transaction ->
                    transaction.add(
                        Id.FRAME,
                        MainMenuFragment()
                    )

                    transaction.commit()
                }
            }
        }
    }

    object RequestCodes {
        const val THUMBNAIL = 1
        const val CONTACT = 2
    }

    object Id {
        const val FRAME = R.id.mainActivityFrame
    }
}