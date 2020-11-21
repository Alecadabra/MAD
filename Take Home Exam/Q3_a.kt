// i)

private fun pickContact() {
    val intent = Intent(
        Intent.ACTION_PICK,
        ContactsContract.Contacts.CONTENT_URI
    )
    startActivityForResult(intent, PICK_CONTACT_REQUEST)
}

companion object {
    const val PICK_CONTACT_REQUEST = 1
}

// ii)

override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
) {
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

    // Return email or "None"
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