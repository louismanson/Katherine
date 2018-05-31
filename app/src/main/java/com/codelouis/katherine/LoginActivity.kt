package com.codelouis.katherine

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Created by Luis Hernandez on 30/mayo/2018
 */

class LoginActivity: AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var signInButton: SignInButton? = null
    val SIGN_IN_CODE = 777

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        signInButton = findViewById(R.id.signInButton)
        signInButton?.setSize(SignInButton.SIZE_WIDE)
        signInButton?.setColorScheme(SignInButton.COLOR_DARK)
        signInButton?.setOnClickListener(View.OnClickListener {
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(intent, SIGN_IN_CODE)
        })
        firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                goMainScreen()
                //LoginActivity.super.onBackPressed();
            }
        }

        progressBar = findViewById(R.id.progressBar)
    }

    fun goMainScreen(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_CODE){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    fun handleSignInResult(result: GoogleSignInResult){
        if(result.isSuccess)
            firebaseAuthWithGoogle(result.signInAccount!!)
        else
            Toast.makeText(this, R.string.not_log_in, Toast.LENGTH_SHORT).show()
    }
    fun firebaseAuthWithGoogle(signInAccount: GoogleSignInAccount){
        progressBar?.visibility = View.VISIBLE
        signInButton?.visibility = View.GONE

        var credential = GoogleAuthProvider.getCredential(signInAccount.idToken, null)
        firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            progressBar?.visibility = View.GONE
            signInButton?.visibility = View.VISIBLE

            if (!task.isSuccessful) {
                Toast.makeText(applicationContext, R.string.not_firebase_auth, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth?.addAuthStateListener(firebaseAuthListener!!)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

}