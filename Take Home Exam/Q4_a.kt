object Downloader {
    
    var time: String? = null

    // Sets the above time field
    fun downloadTime() {
        // Run in an IO thread
        CoroutineScope(Dispatchers.IO).launch {
            val connection = try {
                // Open and connect to URL
                URL("https://a.b.c.d/time").openConnection().also { 
                    it.connect() 
                } as HttpsURLConnection
            } catch (ioException: IOException) {
                // Log error and make connection null
                Log.e("Downloader", "Failed to connect to URL", ioException)
                null
            }

            val localTime = if (
                connection != null &&
                connection.responseCode == HttpsURLConnection.HTTP_OK
            ) {
                try {
                    // Read everything into a string
                    connection.inputStream.bufferedReader().readText()
                } catch (ioE: IOException) {
                    // Log error and make time null
                    Log.e("Downloader", "Failed to download", ioE)
                    null
                } finally {
                    connection.disconnect()
                }
            } else null // If connection failed time is null

            withContext(Dispatchers.Main) {
                // Set field
                this@Downloader.time = localTime
            }
        }
    }
}