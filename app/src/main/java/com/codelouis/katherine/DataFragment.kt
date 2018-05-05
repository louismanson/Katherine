package com.codelouis.katherine

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Luis Hernandez on 01/May/2018
 */
class DataFragment : Fragment() {

    private val TAG = DataFragment::class.java.simpleName

    private val ARG_SECTION_NUMBER = "section_number"

    private var pDialog: ProgressDialog? = null
    private var lv: ListView? = null

    // URL to get contacts JSON
    private val url = "https://api.androidhive.info/contacts/"


    var contactList: ArrayList<HashMap<String,String>>? = null

    lateinit var series: LineGraphSeries<DataPoint>

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    fun newInstance(sectionNumber: Int): DataFragment {
        val fragment = DataFragment()
        val args = Bundle()
        args.putInt(ARG_SECTION_NUMBER, sectionNumber)
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var rootView = inflater.inflate(R.layout.fragment_datafragment, container, false)
        var textView = rootView.findViewById(R.id.section_label) as TextView
        var mPeopleNumber = rootView.findViewById(R.id.person_count) as TextView
        textView.setText(getString(R.string.section_format,getArguments()?.getInt(ARG_SECTION_NUMBER)))

        contactList = ArrayList()
        lv = rootView.findViewById(R.id.list3)


        when (arguments!!.getInt(ARG_SECTION_NUMBER)) {
            1 -> {
                textView.text = "Today"
                mPeopleNumber.setText("310")
                series = LineGraphSeries(arrayOf(DataPoint(0.0, 1.0), DataPoint(1.0, 2.0), DataPoint(2.0, 3.0), DataPoint(3.0, 4.0), DataPoint(4.0, 6.0)))
            }
            2 -> {
                textView.text = "Last Month"
                mPeopleNumber.setText("1,321")
                series = LineGraphSeries(arrayOf(DataPoint(0.0, 1.0), DataPoint(1.0, 5.0), DataPoint(2.0, 6.0), DataPoint(3.0, 2.0), DataPoint(4.0, 1.0)))
            }
            3 -> {
                textView.text = "Last Year"
                mPeopleNumber.setText("12,453")
                series = LineGraphSeries(arrayOf(DataPoint(0.0, 1.0), DataPoint(1.0, 5.0), DataPoint(2.0, 3.0), DataPoint(3.0, 2.0), DataPoint(4.0, 6.0), DataPoint(5.0, 1.0), DataPoint(6.0, 5.0), DataPoint(7.0, 7.0), DataPoint(8.0, 2.0), DataPoint(9.0, 6.0)))
            }
        }

        GetContacts().execute()

        return rootView
    }

    fun refresh(){
        GetContacts().execute()
    }



    /**
     * Async task class to get json by making HTTP call
     */
    private inner class GetContacts : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(TAG, "pre  " )
            // Showing progress dialog
            pDialog = ProgressDialog(context)
            pDialog?.setMessage("Please wait...")
            pDialog?.setCancelable(false)
            pDialog?.show()

        }



        override fun doInBackground(vararg arg0: Void): String? {
            val sh = HttpHandler()
            Log.d(TAG, "doinback ")

            // Making a request to url and getting response
            val jsonStr = sh.makeServiceCall(url)

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
                        contactList?.add(contact)
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
            lv?.setAdapter(adapter)

        }

    }

}