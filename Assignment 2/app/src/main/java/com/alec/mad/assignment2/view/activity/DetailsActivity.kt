package com.alec.mad.assignment2.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.alec.mad.assignment2.R
import com.alec.mad.assignment2.controller.RequestCodes
import com.alec.mad.assignment2.controller.ThumbnailHandler
import com.alec.mad.assignment2.model.ImageBitmapStructure
import com.alec.mad.assignment2.singleton.State

/**
 * Activity to view and edit the details of a structure placed on the game map.
 */
class DetailsActivity(
    private var row: Int? = null,
    private var col: Int? = null
) : AppCompatActivity() {

    /**
     * View references.
     */
    private lateinit var views: Views

    /**
     * Handles taking the thumbnail image.
     */
    private var thumbnailHandler: ThumbnailHandler? = null

    /**
     * Thumbnail returned from the [thumbnailHandler] or null.
     */
    private var bmp: Bitmap? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        this.title = "Structure Details"

        this.thumbnailHandler = ThumbnailHandler(this, RequestCodes.THUMBNAIL)

        // Get values for row/col from intent
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

        // Get values for row/col from bundle
        savedInstanceState?.also { bundle ->
            this.row ?: run { this.row = bundle.getInt(BUNDLE_ROW) }
            this.col ?: run { this.col = bundle.getInt(BUNDLE_COL) }
        }

        // Assert row/col values set
        val i = this.row ?: error("Row not set")
        val j = this.col ?: error("Col not set")

        val mapElement = State.gameData.gameMap[i, j]
        val structure = mapElement.structure ?: error("Row/Col do not contain a structure")

        this.views = Views(
            gridCoordsTextView = findViewById(R.id.detailsActivityGridCoordsValue),
            structureTypeTextView = findViewById(R.id.detailsActivityStructureTypeValue),
            nameTextView = findViewById(R.id.detailsActivityNameValue),
            photoButton = findViewById(R.id.detailsActivityPhotoButton),
            backButton = findViewById(R.id.detailsActivityBackButton),
            saveButton = findViewById(R.id.detailsActivitySaveButton)
        )

        this.views.gridCoordsTextView.text = "Row: ${i + 1}, Col: ${j + 1}"

        this.views.structureTypeTextView.text = structure.structureType.toString()

        this.views.nameTextView.setText(structure.name)

        this.views.photoButton.setOnClickListener {
            this.thumbnailHandler?.takeThumbnail() ?: error("Thumbnail handler not set")
        }

        this.views.backButton.setOnClickListener { onBackPressed() }
        this.views.saveButton.setOnClickListener { save() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCodes.THUMBNAIL) {
            this.bmp = this.thumbnailHandler?.getResult(resultCode, data)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        this.row?.also { outState.putInt(BUNDLE_ROW, it) }
        this.col?.also { outState.putInt(BUNDLE_COL, it) }
    }

    /**
     * Save the thumbnail image and custom structure name if either are non-null
     */
    private fun save() {
        val i = this.row
        val j = this.col
        if (i != null && j != null) {
            // Set bmp if non null
            this.bmp?.also { nullSafeBmp ->
                State.gameData.gameMap[i, j].structure = ImageBitmapStructure(
                    structure = State.gameData.gameMap[i, j].structure
                        ?: error("No structure at map element"),
                    imageBitmap = nullSafeBmp
                )
            }
            // Set name
            State.gameData.gameMap[i, j].structure?.also { structure ->
                structure.name = this.views.nameTextView.text.toString()
                State.gameData.gameMap[i, j].structure = structure
            }
        }
    }

    /**
     * Holder for view instances.
     */
    private class Views(
        val gridCoordsTextView: TextView,
        val structureTypeTextView: TextView,
        val nameTextView: EditText,
        val photoButton: Button,
        val backButton: Button,
        val saveButton: Button
    )

    companion object {
        private const val PATH = "com.alec.mad.assignment2.view.activity.DetailsActivity"
        const val BUNDLE_ROW = "$PATH.row"
        const val BUNDLE_COL = "$PATH.col"

        fun getIntent(c: Context, row: Int, col: Int): Intent = Intent(
            c, DetailsActivity::class.java
        ).also {
            it.putExtra(BUNDLE_ROW, row)
            it.putExtra(BUNDLE_COL, col)
        }
    }
}