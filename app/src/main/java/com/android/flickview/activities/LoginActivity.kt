package com.android.flickview.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
// Remove SharedPreferences import if no longer needed for other things
// import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity // Change to AppCompatActivity for better compatibility
import com.android.flickview.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginActivity"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        // Initialize Firebase Auth
        auth = Firebase.auth
        val emailEditText = findViewById<EditText>(R.id.email) // CHANGE ID HERE if needed
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val registerBtn = findViewById<Button>(R.id.signinButton) // This seems to be the register button?
        val forgotPasswordText = findViewById<TextView>(R.id.forgotPassword)

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            // Basic validation (Firebase handles more complex checks)
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop execution here
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // hideProgressDialog() // Hide progress indicator
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        //Toast.makeText(this, "Login Successful.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LandingActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish() // Finish LoginActivity
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG).show() // Show specific Firebase error
                    }
                }
        }

        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    // --- Check if user is already signed in ---
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "User already logged in: ${currentUser.email}")
            val intent = Intent(this, LandingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            Log.d(TAG, "No user logged in.")
        }
    }
}