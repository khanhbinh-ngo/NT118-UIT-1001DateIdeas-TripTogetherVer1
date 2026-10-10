package com.example.triptogether.utilities

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object LocationUtils {
    suspend fun getCoordinatesFromLocationName(
        location: String,
        apiKey: String,
    ): Pair<Double, Double>? {
        if (apiKey.isBlank() || location.isBlank()) return null
        return withContext(Dispatchers.IO) {
            val address = URLEncoder.encode(location, "UTF-8")
            val connection =
                URL(
                        "https://maps.googleapis.com/maps/api/geocode/json?address=$address&key=$apiKey"
                    )
                    .openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            try {
                if (connection.responseCode !in 200..299) return@withContext null
                val response =
                    connection.inputStream.bufferedReader().use { JSONObject(it.readText()) }
                if (response.optString("status") != "OK") return@withContext null
                val point =
                    response
                        .optJSONArray("results")
                        ?.optJSONObject(0)
                        ?.optJSONObject("geometry")
                        ?.optJSONObject("location") ?: return@withContext null
                val latitude = point.optDouble("lat", Double.NaN)
                val longitude = point.optDouble("lng", Double.NaN)
                if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) null
                else latitude to longitude
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                null
            } finally {
                connection.disconnect()
            }
        }
    }
}
