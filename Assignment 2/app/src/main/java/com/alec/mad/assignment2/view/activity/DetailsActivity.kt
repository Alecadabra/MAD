package com.alec.mad.assignment2.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.singleton.State
import java.lang.IllegalStateException

class DetailsActivity(
    private var row: Int? = null,
    private var col: Int? = null
) : AppCompatActivity() {

    private lateinit var views: Views

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        savedInstanceState?.also { bundle ->
            this.row ?: run { this.row = bundle.getInt(BUNDLE_ROW) }
            this.col ?: run { this.col = bundle.getInt(BUNDLE_COL) }
        }

        this.row ?: run {
            val localRow = this.intent.getIntExtra(BUNDLE_ROW, -1)
            if (localRow != -1) {
                this.row = localRow
            }
        }
        this.col ?: run {
            val localCol = this.intent.getIntExtra(BUNDLE_COL, -1)
            if (localCol != -1) {
                this.col = localCol
            }
        }

        val i = this.row ?: throw IllegalStateException("Row not set")
        val j = this.col ?: throw IllegalStateException("Col not set")
        val mapElement = State.gameData.map[i][j]
        val structure = mapElement.structure
            ?: throw IllegalStateException("Row/Col do not contain a structure")

        this.views = Views(
            gridCoordsTextView = findViewById(R.id.detailsActivityGridCoordsValue),
            structureTypeTextView = findViewById(R.id.detailsActivityStructureTypeValue),
            nameTextView = findViewById(R.id.detailsActivityNameValue),
            photoButton = findViewById(R.id.detailsActivityPhotoButton)
        )

        this.views.gridCoordsTextView.text = "Row: ${i + 1}, Col: ${j + 1}"

        this.views.structureTypeTextView.text = structure.type.toString()

        this.views.nameTextView.text = structure.name

        this.views.photoButton.setOnClickListener { photoButtonHandler() }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Set structure name from EditText
        val i = this.row
        val j = this.col
        
        if (i != null && j != null && this::views.isInitialized) {
            State.gameData.map[i][j].structure?.also { structure ->
                structure.name = this.views.nameTextView.text.toString()
            }
        }
    }

    private fun photoButtonHandler() {
        // TODO Take picture etc
    }

    private class Views(
        val gridCoordsTextView: TextView,
        val structureTypeTextView: TextView,
        val nameTextView: TextView,
        val photoButton: Button
    )

    companion object {
        private const val PACKAGE = "com.alec.mad.assignment2.view.activity.DetailsActivity"
        const val BUNDLE_ROW = "$PACKAGE.row"
        const val BUNDLE_COL = "$PACKAGE.col"

        fun getIntent(c: Context, row: Int, col: Int): Intent = Intent(
            c, DetailsActivity::class.java
        ).also {
            it.putExtra(BUNDLE_ROW, row)
            it.putExtra(BUNDLE_COL, col)
        }
    }
}