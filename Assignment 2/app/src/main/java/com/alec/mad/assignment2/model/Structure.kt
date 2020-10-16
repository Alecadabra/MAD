package com.alec.mad.assignment2.model

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import java.util.Locale

sealed class Structure(
    val type: StructureType,
    var name: String = type.toString()
) {
    abstract fun drawImageTo(view: ImageView)
}

class ImageIDStructure(
    structureType: StructureType,
    @DrawableRes val imageId: Int
) : Structure(structureType) {

    override fun drawImageTo(view: ImageView) {
        view.setImageResource(this.imageId)
    }
}

class ImageBitmapStructure(
    structureType: StructureType,
    private val imageBitmap: Bitmap
) : Structure(structureType) {

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