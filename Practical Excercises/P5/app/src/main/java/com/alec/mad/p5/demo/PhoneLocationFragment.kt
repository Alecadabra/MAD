package com.alec.mad.p5.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.alec.mad.p5.R

class PhoneLocationFragment : Fragment() {

    private lateinit var phoneTitle: TextView
    private lateinit var phoneEditText: EditText
    private lateinit var phoneBtn: Button
    private lateinit var locationTitle: TextView
    private lateinit var locationEditText: EditText
    private lateinit var locationBtn: Button

    private val phoneIntent = Intent(Intent.ACTION_DIAL)
    private val locationIntent = Intent(Intent.ACTION_VIEW)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_phone_locaiton, container, false)

        this.phoneTitle = view.findViewById(ID.PHONE_TITLE)
        this.phoneEditText = view.findViewById(ID.PHONE_EDIT_TEXT)
        this.phoneBtn = view.findViewById(ID.PHONE_BTN)
        this.locationTitle = view.findViewById(ID.LOCATION_TITLE)
        this.locationEditText = view.findViewById(ID.LOCATION_EDIT_TEXT)
        this.locationBtn = view.findViewById(ID.LOCATION_BTN)

        this.phoneTitle.text = getString(R.string.phoneLocationPhoneTitle)
        this.locationTitle.text = getString(R.string.phoneLocationLocationTitle)

        this.phoneBtn.text = getString(R.string.phoneLocationPhoneBtnText)
        this.locationBtn.text = getString(R.string.phoneLocationLocationBtnText)

        this.phoneBtn.setOnClickListener {
            phoneHandler(this.phoneEditText.text.toString())
        }
        this.locationBtn.setOnClickListener {
            locationHandler(this.locationEditText.text.toString())
        }

        return view
    }

    private fun phoneHandler(phoneStr: String) {
        phoneStr.toIntOrNull()?.also { phoneNum ->
            this.phoneIntent.data = Uri.parse("tel:$phoneNum")
            startActivity(this.phoneIntent)
        } ?: run {
            // Error case
            val toastText = getString(R.string.phoneLocationPhoneError)
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        }
    }

    private fun locationHandler(locStr: String) {
        val splitList = locStr.replace(" ", "").split(",", limit = 2)
            // Remove whitespace and split into two strings

        val latitude = runCatching { splitList[0].toFloatOrNull() }.getOrNull()
        val longitude = runCatching { splitList[1].toFloatOrNull() }.getOrNull()

        if (latitude == null || longitude == null) {
            // Error case
            val toastText = getString(R.string.phoneLocationLocationError)
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        }
        else {
            this.locationIntent.data = Uri.parse("geo:$latitude,$longitude")
            startActivity(this.locationIntent)
        }
    }

    object ID {
        const val PHONE_TITLE = R.id.phoneLocationPhoneTitle
        const val PHONE_EDIT_TEXT = R.id.phoneLocationPhoneEditText
        const val PHONE_BTN = R.id.phoneLocationPhoneBtn
        const val LOCATION_TITLE = R.id.phoneLocationLocationTitle
        const val LOCATION_EDIT_TEXT = R.id.phoneLocationLocationEditText
        const val LOCATION_BTN = R.id.phoneLocationLocationBtn
    }
}