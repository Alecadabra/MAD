package com.alec.mad.p5.demo

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.alec.mad.p5.MainActivity
import com.alec.mad.p5.R
import java.io.File

class BigPhotoFragment : Fragment() {
    private lateinit var btn: Button
    private lateinit var image: ImageView

    private lateinit var photoFile: File

    private val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.photoFile = File(activity?.filesDir, FILENAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_big_photo, container, false)

        this.btn = view.findViewById(Id.BTN)
        this.image = view.findViewById(Id.IMAGE)

        this.btn.text = getString(R.string.bigPhotoBtnText)

        this.btn.setOnClickListener { handleBtn() }

        // Disable button if needed
        activity?.packageManager?.also { pm ->
            this.btn.isEnabled = pm.resolveActivity(
                this.intent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) != null
        }

        return view
    }

    private fun handleBtn() {
        activity?.also { safeActivity ->
            // Uri for the image file
            // This doesn't actually work, and I ran out of time to fix.
            val cameraUri = FileProvider.getUriForFile(
                safeActivity,
                "com.alec.mad.p5.fileprovider",
                this.photoFile
            )
            this.intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)

            // Get all activities that could take the photo
            val photoActivities = safeActivity.packageManager?.queryIntentActivities(
                this.intent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) ?: throw IllegalStateException("Package manager not present")

            // Give all activities that could take the photo permission to write to the uri
            photoActivities.forEach { resolveInfo ->
                safeActivity.grantUriPermission(
                    resolveInfo.activityInfo.packageName,
                    cameraUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        } ?: throw IllegalStateException("Activity null")

        startActivityForResult(this.intent, MainActivity.RequestCodes.BIG_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                MainActivity.RequestCodes.BIG_PHOTO -> {
                    val bmp = BitmapFactory.decodeFile(this.photoFile.toString())
                    if (bmp != null) {
                        this.image.setImageBitmap(bmp)
                    }
                }
            }
        }
    }

    companion object {
        private const val FILENAME = "bigPhotoImage"
    }

    object Id {
        const val BTN = R.id.bigPhotoBtn
        const val IMAGE = R.id.bigPhotoImage
    }
}