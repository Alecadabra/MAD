package com.alec.mad.assignment2.controller

import android.net.Uri
import android.util.Log
import com.alec.mad.assignment2.model.GameData
import com.alec.mad.assignment2.controller.observer.GameDataObserver
import com.alec.mad.assignment2.singleton.State
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Handles periodically retrieving the weather information from a remote server and using it
 * to update [gameData]'s temperature field.
 */
@Suppress("SpellCheckingInspection")
class WeatherHandler(private val gameData: GameData) : GameDataObserver {

    /**
     * True when the weather data is currently being downloaded from the remote server.
     */
    private var downloadInProgress = false

    /**
     * Start periodically retrieving the weather.
     */
    fun start() {
        this.gameData.observers.add(this)
        this.gameData.notifyMe(this)
    }

    /**
     * Performs the entire weather update; downloads the json, parses it and sets the gameData
     * temperature field.
     */
    private fun performWeatherUpdate() {
        downloadInProgress = true
        CoroutineScope(Dispatchers.IO).launch {
            val jsonText = download()
            if (jsonText != null) {
                val temp = getTemperatureFromJSON(jsonText)
                if (temp != null) {
                    withContext(Dispatchers.Main) {
                        this@WeatherHandler.gameData.temperature = formatWeather(temp)
                    }
                }
            }
        }
    }

    /**
     * Downloads and returns the JSON weather data string from the remote server, or null
     * if an error occurs
     */
    private fun download(): String? {
        val connection = try {
            URL.openConnection().also { it.connect() } as HttpURLConnection
        } catch (ioE: IOException) {
            documentError("IO Exception when setting up Http Connection to $URL_STRING", ioE)
            null
        }

        return when {
            connection == null -> null
            connection.responseCode != HttpURLConnection.HTTP_OK -> {
                val code = connection.responseCode
                val msg = connection.responseMessage
                documentError("Bad Http Connection code $code, message:\n$msg")
                null
            }
            else -> try {
                connection.inputStream.bufferedReader().readText()
            } catch (ioE: IOException) {
                documentError("IO Exception when getting data from connection", ioE)
                null
            } finally {
                connection.disconnect()
            }
        }
    }

    /**
     * Parses the given [jsonText] and retrieves and returns the temperature field, or null
     * if an error occurs.
     */
    private fun getTemperatureFromJSON(jsonText: String) : Double? = try {
        val jBase = JSONObject(jsonText)
        val jMainObject = jBase.getJSONObject("main")
        jMainObject.getDouble("temp")
    } catch (jsonE: JSONException) {
        documentError("Error parsing JSON", jsonE)
        null
    }

    private fun documentError(error: String) {
        CoroutineScope(Dispatchers.Main).launch {
            this@WeatherHandler.gameData.temperature = "Error"
            this@WeatherHandler.gameData.observers.remove(this@WeatherHandler)
        }
        Log.e("CitySim.WeatherHandler", error)
    }

    private fun documentError(error: String, e: Throwable) {
        CoroutineScope(Dispatchers.Main).launch {
            this@WeatherHandler.gameData.temperature = "Error"
            this@WeatherHandler.gameData.observers.remove(this@WeatherHandler)
        }
        Log.e("CitySim.WeatherHandler", error, e)
    }

    override fun onUpdateGameTime(gameTime: Int) {
        // Perform a weather update whenever the game time changes and a download is not
        // currently in progress
        if (!this.downloadInProgress) {
            performWeatherUpdate()
        }
    }

    override fun onUpdateTemperature(temperature: String) {
        // We finished updating the weather, so a download is no longer in progress
        this.downloadInProgress = false
    }

    companion object {
        private const val PROTOCOL = "https"
        private const val ADDRESS = "api.openweathermap.org"
        private const val PATH = "/data/2.5/weather"
        private val QUERY_PARAMS = setOf(
            "q" to "Perth,au",
            "appid" to "db39b3aa35b693f3a8418e3d4258155b",
            "units" to "metric"
        )
        private val URL_STRING =
            Uri.parse("$PROTOCOL://$ADDRESS$PATH").buildUpon().also { builder ->
                QUERY_PARAMS.forEach { (key, value) ->
                    builder.appendQueryParameter(key, value)
                }
            }.build().toString()
        private val URL = URL(URL_STRING)

        /**
         * Formats a temperature value [temp] to 1 decimal place followed by degrees C.
         */
        private fun formatWeather(temp: Double) : String {
            return "${"%.1f".format(temp)}°C"
        }
    }
}