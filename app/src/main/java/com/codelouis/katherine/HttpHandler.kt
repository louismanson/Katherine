package com.codelouis.katherine

import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL

/**
 * Created by Luis Hernandez on 26/April/2018
 */

class HttpHandler {
    private val TAG = HttpHandler::class.java.simpleName

    fun makeServiceCall(reqUrl: String): String? {
        var response: String? = null
        Log.d(TAG, "Httphand " + reqUrl)
        try {
            Log.d(TAG, "Httphand try")
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            // read the response
            val `in` = BufferedInputStream(conn.inputStream)
            response = convertStreamToString(`in`)
        } catch (e: ProtocolException) {
            Log.e(TAG, "ProtocolException: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "IOException: " + e.message)
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }
        Log.d(TAG, "fin")
        return response
    }

    private fun convertStreamToString(`is`: InputStream): String {
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()
        Log.d(TAG, "convertStreamToString")

        var line: String? = null

        try {
            Log.d(TAG, "convertStreamToString try")

            line = reader.readLine()
            while (line!=null){
                sb.append(line).append('\n')
                Log.d(TAG, "convertStreamToString nel")
                line = reader.readLine()
            }

            sb.append("sandia").append('\n')
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        //print(sb.toString())
        Log.d(TAG, "convertStreamToString" + sb.toString())
        return sb.toString()
    }
}