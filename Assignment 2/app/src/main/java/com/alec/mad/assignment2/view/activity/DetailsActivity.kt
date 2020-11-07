package com.alec.mad.assignment2.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.controller.ThumbnailHandler
import com.alec.mad.assignment2.model.ImageBitmapStructure
import com.alec.mad.assignment2.singleton.State

class DetailsActivity(
    private var row: Int? = null,
    private var col: Int? = null
) : AppCompatActivity() {

    private lateinit var views: Views

    private var thumbnailHandler: ThumbnailHandler? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        this.title = "Structure Details"

        this.thumbnailHandler = ThumbnailHandler(this, THUMBNAIL_REQUEST_CODE)

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

        val i = this.row ?: error("Row not set")
        val j = this.col ?: error("Col not set")
        val mapElement = State.gameData.map[i, j]
        val structure = mapElement.structure ?: error("Row/Col do not contain a structure")

        this.views = Views(
            gridCoordsTextView = findViewById(R.id.detailsActivityGridCoordsValue),
            structureTypeTextView = findViewById(R.id.detailsActivityStructureTypeValue),
            nameTextView = findViewById(R.id.detailsActivityNameValue),
            photoButton = findViewById(R.id.detailsActivityPhotoButton),
            backButton = findViewById(R.id.detailsActivityBackButton)
        )

        this.views.gridCoordsTextView.text = "Row: ${i + 1}, Col: ${j + 1}"

        this.views.structureTypeTextView.text = structure.structureType.toString()

        this.views.nameTextView.setText(structure.name)

        this.views.photoButton.setOnClickListener {
            this.thumbnailHandler?.takeThumbnail()
                ?: error("Thumbnail handler not set")
        }

        this.views.backButton.setOnClickListener { onBackPressed() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == THUMBNAIL_REQUEST_CODE) {
            val i = this.row
            val j = this.col
            val bmp = this.thumbnailHandler?.getResult(resultCode, data)
            if (i != null && j != null && bmp != null) {
                State.gameData.map[i, j].structure = ImageBitmapStructure(
                    structure = State.gameData.map[i, j].structure
                        ?: error("No structure at map element"),
                    imageBitmap = bmp
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        this.row?.also { outState.putInt(BUNDLE_ROW, it) }
        this.col?.also { outState.putInt(BUNDLE_COL, it) }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Set structure name from EditText
        val i = this.row
        val j = this.col
        if (i != null && j != null && this::views.isInitialized) {
            State.gameData.map[i, j].structure?.also { structure ->
                structure.name = this.views.nameTextView.text.toString()
                State.gameData.map[i, j].structure = structure
            }
        }
    }

    private class Views(
        val gridCoordsTextView: TextView,
        val structureTypeTextView: TextView,
        val nameTextView: EditText,
        val photoButton: Button,
        val backButton: Button
    )

    companion object {
        private const val PACKAGE = "com.alec.mad.assignment2.view.activity.DetailsActivity"
        const val BUNDLE_ROW = "$PACKAGE.row"
        const val BUNDLE_COL = "$PACKAGE.col"

        const val THUMBNAIL_REQUEST_CODE = 1

        fun getIntent(c: Context, row: Int, col: Int): Intent = Intent(
            c, DetailsActivity::class.java
        ).also {
            it.putExtra(BUNDLE_ROW, row)
            it.putExtra(BUNDLE_COL, col)
        }
    }
}