package com.alec.mad.p5.demo

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.alec.mad.p5.MainActivity
import com.alec.mad.p5.R


class ThumbnailFragment : Fragment() {

    private lateinit var btn: Button
    private lateinit var image: ImageView

    private val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_thumbnail, container, false)

        this.btn = view.findViewById(Id.BTN)
        this.image = view.findViewById(Id.IMAGE)

        this.btn.text = getString(R.string.thumbnailBtnText)

        this.btn.setOnClickListener {
            startActivityForResult(this.intent, MainActivity.RequestCodes.THUMBNAIL)
        }

        // Disable button if needed
        activity?.packageManager?.also { pm ->
            this.btn.isEnabled = pm.resolveActivity(
                this.intent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) != null
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                MainActivity.RequestCodes.THUMBNAIL -> {
                    val bmp = data?.extras?.get("data") as Bitmap?
                    if (bmp != null) {
                        this.image.setImageBitmap(bmp)
                    }
                }
            }
        }
    }

    object Id {
        const val BTN = R.id.thumbnailBtn
        const val IMAGE = R.id.thumbnailImage
    }
}