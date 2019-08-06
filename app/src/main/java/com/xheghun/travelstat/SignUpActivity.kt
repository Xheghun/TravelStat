package com.xheghun.travelstat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : FirebaseAppCompactActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        sign_up_btn.setOnClickListener { signUp() }
    }


    fun signUp() {
        val username: String = username_layout_edit.text.toString()
        val email: String = email_layout_edit.text.toString()
        val password: String = password_layout_edit.toString()
        val error = "this field is required"
        when {
            username.isEmpty() -> {
                username_layout.isErrorEnabled = true
                username_layout.error = error
            }

            email.isEmpty() -> {
                email_layout.isErrorEnabled = true
                email_layout.error = error
            }

            password.isEmpty() -> {
                password_layout.isErrorEnabled = true
                password_layout.error = error
            }
            else -> {
                password_layout.error = ""; email_layout.error = ""; username_layout.error = ""
                password_layout.isErrorEnabled = false; email_layout.isErrorEnabled = false; username_layout.isErrorEnabled = false

                progress_horizontal.visibility = View.VISIBLE
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            when {
                                task.isSuccessful -> {
                                    Toast.makeText(this, "sign up success", Toast.LENGTH_SHORT).show()
                                    progress_horizontal.visibility = View.INVISIBLE
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                                else -> {
                                    progress_horizontal.visibility = View.INVISIBLE
                                    Snackbar.make(findViewById(R.id.root_view), "sign up failed", Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
            }
        }
    }

    fun signInActivity(view: View) {
        startActivity(Intent(this, SignInActivity::class.java))
    }
}
