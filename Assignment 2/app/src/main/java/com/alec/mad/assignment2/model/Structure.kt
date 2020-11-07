package com.alec.mad.assignment2.model

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import java.util.Locale

sealed class Structure(
    val structureType: StructureType,
    @DrawableRes val imageId: Int,
    var name: String = structureType.toString()
)  {
    abstract fun drawImageTo(view: ImageView)
}

class ImageIDStructure(
    structureType: StructureType,
    @DrawableRes imageId: Int,
    name: String = structureType.toString()
) : Structure(structureType, imageId, name) {

    override fun drawImageTo(view: ImageView) {
        view.setImageResource(this.imageId)
    }
}

class ImageBitmapStructure(
    structureType: StructureType,
    @DrawableRes imageId: Int,
    private val imageBitmap: Bitmap,
    name: String = structureType.toString()
) : Structure(structureType, imageId, name) {

    constructor(structure: Structure, imageBitmap: Bitmap) : this(
        structureType = structure.structureType,
        imageId = structure.imageId,
        imageBitmap = imageBitmap,
        name = structure.name
    )

    override fun drawImageTo(view: ImageView) {
        view.setImageBitmap(this.imageBitmap)
    }
}

enum class StructureType {
    RESIDENTIAL,
    COMMERCIAL,
    ROAD;

    // Turns 'RESIDENTIAL' into 'Residential'
    override fun toString() = this.name.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
}