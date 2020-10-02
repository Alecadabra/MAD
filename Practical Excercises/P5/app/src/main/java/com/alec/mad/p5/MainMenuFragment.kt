package com.alec.mad.p5

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.alec.mad.p5.demo.*

class MainMenuFragment : Fragment() {

    private lateinit var title: TextView
    private lateinit var btnPhoneLocation: Button
    private lateinit var btnThumbnail: Button
    private lateinit var btnContact: Button
    private lateinit var btnBigPhoto: Button

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_main_menu, container, false)

        this.title = view.findViewById(Id.TITLE)
        this.btnPhoneLocation = view.findViewById(Id.BTN_PHONE_LOCATION)
        this.btnThumbnail = view.findViewById(Id.BTN_THUMBNAIL)
        this.btnContact = view.findViewById(Id.BTN_CONTACT)
        this.btnBigPhoto = view.findViewById(Id.BTN_BIG_PHOTO)

        this.title.text = "Select a demo"
        this.btnPhoneLocation.text = "Phone calling and map location"
        this.btnThumbnail.text = "Take a thumbnail picture"
        this.btnContact.text = "Select a contact"
        this.btnBigPhoto.text = "Take a large picture"

        this.btnPhoneLocation.setOnClickListener { start(PhoneLocationFragment()) }
        this.btnThumbnail.setOnClickListener { start(ThumbnailFragment()) }
        this.btnContact.setOnClickListener { start(ContactFragment()) }
        this.btnBigPhoto.setOnClickListener { start(BigPhotoFragment()) }

        return view
    }

    private fun start(fragment: Fragment) = fragmentManager?.beginTransaction()?.also { transact ->
        transact.replace(MainActivity.Id.FRAME, fragment)
        transact.addToBackStack(null)
        transact.commit()
    }

    object Id {
        const val TITLE = R.id.mainMenuTitle
        const val BTN_PHONE_LOCATION = R.id.mainMenuBtnPhoneLocation
        const val BTN_THUMBNAIL = R.id.mainMenuBtnThumbnail
        const val BTN_CONTACT = R.id.mainMenuBtnContact
        const val BTN_BIG_PHOTO = R.id.mainMenuBtnBigPhoto
    }
}