package com.alec.mad.q4_a

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object Downloader {

    var time: String? = null

    fun downloadTime() {
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
                connection != null && connection.responseCode == HttpsURLConnection.HTTP_OK
            ) {
                try {
                    // Read everything into a string
                    connection.inputStream.bufferedReader().readText()
                } catch (ioE: IOException) {
                    // Log error and make time null
                    Log.e("Downloader", "Failed to download from connection", ioE)
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