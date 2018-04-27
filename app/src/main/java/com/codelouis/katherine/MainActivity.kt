package com.codelouis.katherine

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Luis Hernandez on 26/April/2018
 */


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = MainActivity::class.java.simpleName

    private var pDialog: ProgressDialog? = null
    private var lv: ListView? = null

    // URL to get contacts JSON
    private val url = "https://api.androidhive.info/contacts/"


    var contactList: ArrayList<HashMap<String,String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        contactList = ArrayList()
        lv = findViewById(R.id.list) as ListView

        Log.d(TAG, "hola  " )
        GetContacts().execute()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }



    /**
     * Async task class to get json by making HTTP call
     */
    private inner class GetContacts : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(TAG, "pre  " )
            // Showing progress dialog
            pDialog = ProgressDialog(this@MainActivity)
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
                    runOnUiThread {
                        Toast.makeText(applicationContext,
                                "Json parsing error: " + e.message,
                                Toast.LENGTH_LONG)
                                .show()
                    }

                }

            } else {
                Log.d(TAG, "valio")
                Log.e(TAG, "Couldn't get json from server.")
                runOnUiThread {
                    Toast.makeText(applicationContext,
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
                    this@MainActivity, contactList,
                    R.layout.list_item, arrayOf("name", "email", "mobile"), intArrayOf(R.id.name, R.id.email, R.id.mobile))

            lv!!.setAdapter(adapter)
        }

    }




}
