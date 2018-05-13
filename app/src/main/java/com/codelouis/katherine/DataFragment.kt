package com.codelouis.katherine

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.jjoe64.graphview.GraphView
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

    //private var pDialog: ProgressDialog? = null
    private var lv: ListView? = null

    // URL to get contacts JSON
    private lateinit var url: String


    var dataList: ArrayList<HashMap<String,String>>? = null

    private var mLoadingFragment: AnimationDialogFragment? = null
    private lateinit var mGraph: GraphView
    private lateinit var mPeopleNumber: TextView



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

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_datafragment, container, false)
        val textView = rootView.findViewById(R.id.section_label) as TextView
        mPeopleNumber = rootView.findViewById(R.id.person_count) as TextView
        textView.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))

        dataList = ArrayList()
        lv = rootView.findViewById(R.id.list3)


        mGraph = rootView.findViewById(R.id.graph) as GraphView

        when (arguments!!.getInt(ARG_SECTION_NUMBER)) {
            1 -> {
                textView.text = "Today"
                url = "https://firebasestorage.googleapis.com/v0/b/polyfireapp2.appspot.com/o/pruebajson.json?alt=media&token=d5c47f6c-2eb1-433a-8cdd-060f9df93ab0"
                GetContacts().execute()
            }
            2 -> {
                textView.text = "Last Month"
                url = "https://firebasestorage.googleapis.com/v0/b/polyfireapp2.appspot.com/o/pruebaano.json?alt=media&token=9919f0eb-4022-4ef2-80e2-c939a246f4d2"
                GetContacts().execute()
            }
            3 -> {
                textView.text = "Last Year"
                url = "https://firebasestorage.googleapis.com/v0/b/polyfireapp2.appspot.com/o/pruebames.json?alt=media&token=92a5465a-bcbe-4dc8-9814-6388ee46b34c"
                GetContacts().execute()
            }
        }

        return rootView
    }

    fun refresh(){
        GetContacts().execute()
    }



    /**
     * Async task class to get json by making HTTP call
     */
    @SuppressLint("StaticFieldLeak")
    private inner class GetContacts : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(TAG, "pre  " )
            mLoadingFragment = AnimationDialogFragment()
            mLoadingFragment?.show(fragmentManager, "Loading")
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

                    dataList?.clear()

                    // Getting JSON Array node
                    val contacts = jsonObj.getJSONArray("lecturas")
                    Log.d(TAG, "try  " )

                    // looping through All Contacts
                    for (i in 0 until contacts.length()) {
                        val c = contacts.getJSONObject(i)

                        val date = c.getString("date")
                        val time = c.getString("time")
                        val count = c.getString("count")

                        val data = java.util.HashMap<String, String>()

                        // adding each child node to HashMap key => value

                        data["time"] = time
                        data["date"] = date
                        data["count"] = count

                        // adding contact to contact list
                        dataList?.add(data)
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
            /*if (pDialog!!.isShowing)
                pDialog!!.dismiss()*/
            mLoadingFragment?.dismiss()

            //render data
            var counter = 0
            var serie: LineGraphSeries<DataPoint> = LineGraphSeries()

            for(i in 0 until dataList!!.size){
                counter += dataList!!.get(i)["count"]!!.toInt()
                serie.appendData(
                        DataPoint(i.toDouble(), (dataList!!.get(i)["count"])!!.toDouble() ),
                        true,
                        dataList!!.size
                )
            }
            mPeopleNumber.text = counter.toString()
            mGraph.addSeries(serie)
        }

    }

}