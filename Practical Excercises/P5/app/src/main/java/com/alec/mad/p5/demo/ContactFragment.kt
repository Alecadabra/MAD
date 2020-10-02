package com.alec.mad.p5.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alec.mad.p5.MainActivity
import com.alec.mad.p5.R

class ContactFragment : Fragment() {

    private lateinit var title: TextView
    private lateinit var btn: Button
    private lateinit var readout: TextView

    private val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        this.title = view.findViewById(Id.TITLE)
        this.btn = view.findViewById(Id.BTN)
        this.readout = view.findViewById(Id.READOUT)

        this.title.text = getString(R.string.contactTitleText)
        this.btn.text = getString(R.string.contactBtnText)
        this.readout.text = getString(R.string.contactReadoutTextDefault)

        this.btn.setOnClickListener {
            startActivityForResult(this.intent, MainActivity.RequestCodes.CONTACT)
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
                MainActivity.RequestCodes.CONTACT -> handleContactReceived(data)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleContactReceived(data: Intent?) {
        val id: Int
        val name: String
        val email: String
        val phoneNum: String

        // Get contact selection
        val idNamePair = data?.data?.let { uri ->
            val queryFields = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            )
            var localNullableId: Int? = null
            var localNullableName: String? = null

            activity?.contentResolver?.query(
                uri,
                queryFields,
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    localNullableId = cursor.getInt(0)
                    localNullableName = cursor.getString(1)
                }
            }

            return@let when {
                localNullableId == null -> null
                localNullableName == null -> null
                else -> Pair(localNullableId!!, localNullableName!!)
            }
        } ?: throw IllegalStateException("Query failed")

        id = idNamePair.first
        name = idNamePair.second

        email = activity?.contentResolver?.let { contentResolver ->
            var localNullableEmail: String? = null

            contentResolver.query(
                CommonDataKinds.Email.CONTENT_URI,
                arrayOf(CommonDataKinds.Email.ADDRESS),
                "${CommonDataKinds.Email.CONTACT_ID} = ?",
                arrayOf("$id"),
                null,
                null
            )?.use { cursor ->
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    localNullableEmail = cursor.getString(0)
                }
            }

            return@let localNullableEmail
        } ?: "None"

        phoneNum = activity?.contentResolver?.let { contentResolver ->
            var localNullablePhoneNum: String? = null

            contentResolver.query(
                CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(CommonDataKinds.Phone.NUMBER),
                "${CommonDataKinds.Phone.CONTACT_ID} = ?",
                arrayOf("$id"),
                null,
                null
            )?.use { cursor ->
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    localNullablePhoneNum = cursor.getString(0)
                }
            }

            return@let localNullablePhoneNum
        } ?: "None"

        this.readout.text = """
                        Contact ID: $id
                        Name: $name
                        Email address: $email
                        Phone number: $phoneNum
                    """.trimIndent()
    }

    object Id {
        const val TITLE = R.id.contactTitle
        const val BTN = R.id.contactBtn
        const val READOUT = R.id.contactReadout
    }
}