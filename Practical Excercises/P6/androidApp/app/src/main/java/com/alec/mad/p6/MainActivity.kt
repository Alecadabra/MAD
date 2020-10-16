package com.alec.mad.p6

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.GeneralSecurityException
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var textArea: TextView
    private lateinit var downloadBtn: Button

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get view references
        this.textArea = findViewById(R.id.textArea)
        this.progressBar = findViewById(R.id.progressBar)
        this.downloadBtn = findViewById(R.id.downloadBtn)

        // Set up views
        this.progressBar.visibility = View.INVISIBLE
        this.downloadBtn.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                val jsonText: String
                val factionList: List<Faction>

                try {
                    // Sets up a connection and downloads the JSON string from the test server
                    jsonText = download(URL)
                    // Parses the json string into a list of factions
                    factionList = parseJSON(jsonText)

                    withContext(Dispatchers.Main) {
                        // Update the text with the downloaded factions
                        this@MainActivity.textArea.text =
                            factionList.joinToString(separator = "\n") { fac ->
                                fac.toString()
                            }
                    }
                } catch (ioException: IOException) {
                    Log.e(TAG, "IOException", ioException)
                } catch (securityException: GeneralSecurityException) {
                    Log.e(TAG, "GeneralSecurityException", securityException)
                } catch (jsonException: JSONException) {
                    Log.e(TAG, "JSON Parse error", jsonException)
                }
            }
        }
    }

    /**
     * Downloads the JSON string from the test server at the given [url].
     *
     * @throws IOException
     * @throws GeneralSecurityException
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun download(url: URL): String {
        val conn = url.openConnection() as HttpsURLConnection
        DownloadUtils.addCertificate(this, conn)

        if (conn.responseCode != HttpURLConnection.HTTP_OK) {
            throw IOException("HttpURLConnection code ${conn.responseCode}")
        }

        return try {
            conn.inputStream.bufferedReader().readText()
        } finally {
            conn.disconnect()
        }
    }

    /**
     * Parses the [jsonText] into a list of [Faction].
     *
     * @throws JSONException
     */
    private fun parseJSON(jsonText: String): List<Faction> {
        val jBase = JSONObject(jsonText)
        val jArray = jBase.getJSONArray("factions")
        return List(jArray.length()) { i ->
            val jFaction = jArray.getJSONObject(i)
            Faction(
                name = jFaction.getString("name"),
                strength = jFaction.getInt("strength"),
                relationship = jFaction.getString("relationship")
            )
        }
    }

    /* Unused
    private suspend fun updateProgressBar(progress: Int, max: Int) {
        withContext(Dispatchers.Main) {
            this@MainActivity.progressBar.visibility = View.VISIBLE
            this@MainActivity.progressBar.max = max
            this@MainActivity.progressBar.progress = progress
        }
    }
     */

    /**
     * Represents a faction.
     */
    data class Faction(
        val name: String,
        val strength: Int,
        val relationship: String
    )

    companion object {
        private const val TAG = "P6"

        // Network stuff
        private const val PROTOCOL = "https"
        private const val IP = "192.168.142.1"
        private const val PORT = "8000"
        private const val PATH = "/testwebservice/rest"
        private val QUERY_PARAMS = listOf(
            "method" to "thedata.getit",
            "api_key" to "01189998819991197253",
            "format" to "json"
        )
        private val URL_STRING =
            Uri.parse("$PROTOCOL://$IP:$PORT$PATH").buildUpon().also { builder ->
                    QUERY_PARAMS.forEach { (key, value) ->
                        builder.appendQueryParameter(key, value)
                    }
                }.build().toString()
        private val URL = URL(URL_STRING)
    }
}