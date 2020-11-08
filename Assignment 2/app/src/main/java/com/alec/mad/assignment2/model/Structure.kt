package com.alec.mad.assignment2.model

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import java.util.Locale

/**
 * Abstract representation of a Structure to be place on a [GameMap]. Derived classes provide
 * an implementation of [drawImageTo].
 */
sealed class Structure(
    val structureType: StructureType,
    @DrawableRes val imageId: Int,
    var name: String = structureType.toString()
)  {
    /**
     * Draw this structure's image to the given [ImageView].
     */
    abstract fun drawImageTo(view: ImageView)
}

/**
 * A [Structure] that uses a [DrawableRes] image id as it's image.
 */
class ImageIDStructure(
    structureType: StructureType,
    @DrawableRes imageId: Int,
    name: String = structureType.toString()
) : Structure(structureType, imageId, name) {

    override fun drawImageTo(view: ImageView) {
        view.setImageResource(this.imageId)
    }
}

/**
 * A [Structure] that uses a [Bitmap] object as it's image.
 */
class ImageBitmapStructure(
    structureType: StructureType,
    @DrawableRes imageId: Int,
    private val imageBitmap: Bitmap,
    name: String = structureType.toString()
) : Structure(structureType, imageId, name) {

    /**
     * Copy constructor
     */
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