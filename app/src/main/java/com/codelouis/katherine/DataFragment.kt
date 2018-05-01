package com.codelouis.katherine

import android.app.ProgressDialog
import android.os.AsyncTask
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Luis Hernandez on 01/May/2018
 */
class DataFragment : Fragment() {

    private val TAG = DataFragment::class.simpleName

    private var pDialog: ProgressDialog? = null
    private var lv: ListView? = null

    // URL to get contacts JSON
    private val url = "https://api.androidhive.info/contacts/"


    var contactList: ArrayList<HashMap<String,String>>? = null

    /**
     * Async task class to get json by making HTTP call
     */
    private inner class GetContacts : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(TAG, "pre  " )
            // Showing progress dialog
            pDialog = ProgressDialog(context)
            pDialog!!.setMessage("Please wait...")
            pDialog!!.setCancelable(false)
            pDialog!!.show()

        }



        override fun doInBackground(vararg arg0: Void): String? {
            val sh = HttpHandler()
            Log.d(TAG, "doinback ")

            // Making a request to url and getting response
            val jsonStr = sh.makeServiceCall(url)

            Log.e(TAG, "Response from url: " + jsonStr!!)

            if (jsonStr != null) {
                Log.d(TAG, "jsonStr != null  " )
                try {
                    val jsonObj = JSONObject(jsonStr)

                    // Getting JSON Array node
                    val contacts = jsonObj.getJSONArray("contacts")
                    Log.d(TAG, "try  " )

                    // looping through All Contacts
                    for (i in 0 until contacts.length()) {
                        val c = contacts.getJSONObject(i)

                        Log.d(TAG, "for  " )
                        val id = c.getString("id")
                        val name = c.getString("name")
                        val email = c.getString("email")
                        val address = c.getString("address")
                        val gender = c.getString("gender")

                        // Phone node is JSON Object
                        val phone = c.getJSONObject("phone")
                        val mobile = phone.getString("mobile")
                        val home = phone.getString("home")
                        val office = phone.getString("office")

                        // tmp hash map for single contact
                        val contact = java.util.HashMap<String, String>()

                        // adding each child node to HashMap key => value
                        contact["id"] = id
                        contact["name"] = name
                        contact["email"] = email
                        contact["mobile"] = mobile

                        // adding contact to contact list
                        contactList!!.add(contact)
                    }
                } catch (e: JSONException) {
                    Log.d(TAG, "valio pito")
                    Log.e(TAG, "Json parsing error: " + e.message)
                    activity?.runOnUiThread {
                        Toast.makeText(context,
                                "Json parsing error: " + e.message,
                                Toast.LENGTH_LONG)
                                .show()
                    }

                }

            } else {
                Log.d(TAG, "valio")
                Log.e(TAG, "Couldn't get json from server.")
                activity?.runOnUiThread {
                    Toast.makeText(context,
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG)
                            .show()
                }

            }

            return " "
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d(TAG, "fin")
            // Dismiss the progress dialog
            if (pDialog!!.isShowing)
                pDialog!!.dismiss()
            /**
             * Updating parsed JSON data into ListView
             */
            val adapter = SimpleAdapter(
                    context, contactList,
                    R.layout.list_item, arrayOf("name", "email", "mobile"), intArrayOf(R.id.name, R.id.email, R.id.mobile))

            lv!!.setAdapter(adapter)
        }

    }

}