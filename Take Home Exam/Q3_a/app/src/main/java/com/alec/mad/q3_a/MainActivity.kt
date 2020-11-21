package com.alec.mad.q3_a

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, PICK_CONTACT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_CONTACT_REQUEST -> getEmail(data)
            }
        }
    }

    private fun getEmail(intent: Intent?): String {
        // Get ID
        val id: Int = intent?.data?.let { uri ->
            var nullableId: Int? = null

            contentResolver?.query(
                uri,
                arrayOf(ContactsContract.Contacts._ID),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    nullableId = cursor.getInt(0)
                }
            }
            return@let nullableId
        } ?: error("Query failed")

        // Return email
        return contentResolver?.let { contentResolver ->
            var nullableEmail: String? = null

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
                    nullableEmail = cursor.getString(0)
                }
            }

            return@let nullableEmail
        } ?: "None"
    }

    companion object {
        const val PICK_CONTACT_REQUEST = 1
    }
}