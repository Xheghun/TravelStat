package com.xheghun.travelstat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : FirebaseAppCompactActivity() {

    private val RC_SIGN_IN = 7
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var root: ViewGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        root = findViewById(R.id.root_view)
        sign_with_google_btn.setOnClickListener { googleSignIn()}
        sign_up_btn.setOnClickListener { emailSignIn() }

    }

    //sign in with google
    private fun googleSignIn() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        token_id.text = gso.serverClientId
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //create new user with email and password
    private fun emailSignIn() {
        val email: String  = email_layout_edit.text.toString()
        val password: String = password_layout_edit.text.toString()
        val error = "this field is required"
        when {
            email.isEmpty() -> {
                email_layout.isErrorEnabled = true
                email_layout.error = error
            }
            password.isEmpty() -> {
                password_layout.isErrorEnabled = true
                password_layout.error = error
            }
            else -> auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {task ->
                        when {
                            task.isSuccessful -> {
                                //Sign in success, goto next activity
                                val user = auth.currentUser
                                updateUI(user)
                                startActivity(Intent(this,MainActivity::class.java))
                            } else -> {
                                Snackbar.make(root_view,"unable to sign in",Snackbar.LENGTH_SHORT)
                                        .show()
                            updateUI(null)
                        }
                        }
                    }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,"fail ${e.statusCode}",Toast.LENGTH_LONG).show()
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:"+ account.id!!)

        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) {task ->
                    if (task.isSuccessful) {
                        //sign-in success
                        Log.d(TAG,"signINWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)

                    } else {
                        //If sign in fails, display message to the user.
                        Log.w(TAG, "signInWithCredentials:failure", task.exception)
                        Toast.makeText(this,"Authentication Failed.",Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {

        }
    }

    fun signUpActivity(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}
