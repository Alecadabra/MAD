package com.alec.mad.assignment2.controller

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity

/**
 * Handles invoking the camera app to take a thumbnail image.
 */
class ThumbnailHandler(private val activity: FragmentActivity, private val requestCode: Int) {
    private val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    /**
     * Invoke the camera app to take a thumbnail image.
     */
    fun takeThumbnail() {
        activity.startActivityForResult(this.intent, this.requestCode)
    }

    /**
     * Get the bitmap image after the camera app gives it's result, or null if not found
     */
    fun getResult(resultCode: Int, data: Intent?): Bitmap? {
        return if (resultCode == Activity.RESULT_OK) {
            data?.extras?.get("data") as Bitmap?
        } else null
    }
 }