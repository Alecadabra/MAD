package com.alec.mad.p6

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var textArea: TextView
    private lateinit var downloadBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get view references
        this.textArea = findViewById(R.id.textArea)
        this.progressBar = findViewById(R.id.progressBar)
        this.downloadBtn = findViewById(R.id.downloadBtn)

        // Set up views
        this.progressBar.visibility = View.INVISIBLE
        this.downloadBtn.setOnClickListener {
            // ...
        }
    }
}