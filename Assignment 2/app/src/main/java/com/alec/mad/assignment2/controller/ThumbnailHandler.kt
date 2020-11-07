package com.alec.mad.assignment2.controller

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity

class ThumbnailHandler(private val activity: FragmentActivity, val requestCode: Int) {
    private val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    fun takeThumbnail() {
        activity.startActivityForResult(this.intent, this.requestCode)
    }

    fun getResult(resultCode: Int, data: Intent?): Bitmap? {
        return if (resultCode == Activity.RESULT_OK) {
            data?.extras?.get("data") as Bitmap?
        } else {
            null
        }
    }
 }