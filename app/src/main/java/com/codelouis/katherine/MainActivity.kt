package com.codelouis.katherine


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


/**
 * Created by Luis Hernandez on 26/April/2018
 */


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private val TAG = MainActivity::class.java.simpleName

    private var tabLayout: TabLayout? = null
    private var mViewPager: ViewPager? = null
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private var headerView: View? = null

    private var photoImageView: ImageView? = null
    private var nameTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var backgroundView: ImageView? = null
    private var googleApiClient: GoogleApiClient? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/

        fab.setOnClickListener {
            val page = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager?.getCurrentItem()) as DataFragment
            page.refresh()
            Toast.makeText(applicationContext, "Endpoint server call (contacts example)", Toast.LENGTH_SHORT).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager?.setAdapter(mSectionsPagerAdapter)

        tabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout?.setupWithViewPager(mViewPager)

        headerView = nav_view.getHeaderView(0)

        setNavigationViewListner()

        photoImageView = headerView?.findViewById(R.id.avatar_photo) as ImageView
        nameTextView = headerView?.findViewById(R.id.avatar_name) as TextView
        emailTextView = headerView?.findViewById(R.id.avatar_mail) as TextView
        backgroundView = headerView?.findViewById(R.id.avatar_background) as ImageView

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                setUserData(user)
                nav_view.menu.getItem(0).isVisible = false
                nav_view.menu.getItem(1).isVisible = true
            } else {
                nameTextView?.text = "Guest"
                emailTextView?.text = ""
                backgroundView?.setImageResource(R.color.cardview_dark_background)
                photoImageView?.setImageResource(R.mipmap.ic_launcher_round)
                //goLogInScreen();
                nav_view.menu.getItem(0).isVisible = true
                nav_view.menu.getItem(1).isVisible = false
            }
        }



    }

    private fun setNavigationViewListner(){
        var navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setUserData(user: FirebaseUser){
        Toast.makeText(this, "Loging as " + user.email!!, Toast.LENGTH_SHORT).show()
        nameTextView?.text = user.displayName
        emailTextView?.text = user.email
        Glide.with(baseContext).load(user.photoUrl).into(photoImageView!!)
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
                goLogInScreen()
            }
            R.id.nav_gallery -> {
                revoke()
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


    inner class SectionsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return DataFragment().newInstance(position+1)
        }


        override fun getCount(): Int {
            // show 3 tabs
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0 -> return "Day"
                1 -> return "week"
                2 -> return "Month"
                else -> return "Day"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth?.addAuthStateListener(firebaseAuthListener!!)
    }

    fun goLogInScreen(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun revoke(){
        firebaseAuth?.signOut()

        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback { status ->
            if (status.isSuccess) {
                //goLogInScreen();
            } else {
                Toast.makeText(applicationContext, R.string.not_revoke, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
