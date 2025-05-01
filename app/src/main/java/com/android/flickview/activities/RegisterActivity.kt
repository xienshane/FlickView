package com.android.flickview.activities

import android.app.Activity
import android.content.Intent
// Remove SharedPreferences imports
// import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity // Change to AppCompatActivity
import com.android.flickview.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// Change Activity to AppCompatActivity
class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Define a TAG for logging
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val emailEditText = findViewById<EditText>(R.id.email)
        val usernameEditText = findViewById<EditText>(R.id.username) // Keep username for profile, but not for login
        val passwordEditText = findViewById<EditText>(R.id.password2) // Assuming this is password
        val confirmPasswordEditText = findViewById<EditText>(R.id.password) // Assuming this is confirm password
        val createAccBtn = findViewById<Button>(R.id.createAccountBtn)
        val alrHaveAccBtn = findViewById<Button>(R.id.alreadyHaveAnAccountBtn)

        // Remove SharedPreferences related code
        // val sharedPreferences: SharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        // val editor = sharedPreferences.edit()

        createAccBtn.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim() // We might store this elsewhere later (Firestore/Realtime DB)
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // --- Validation ---
            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Username validation (optional, as it's not used for login directly with Firebase Auth)
            if (username.length < 3 || username.length > 15) {
                Toast.makeText(this, "Username must be 3-15 characters long.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Firebase Auth enforces minimum password length (usually 6) automatically, but client-side check is good UX
            if (password.length < 6 || password.length > 20) { // Keep your upper limit if desired
                Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show() // Adjusted message
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // --- End Validation ---

            // showProgressDialog() // Optional: Show loading indicator

            // --- Create user with Firebase Auth ---
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // hideProgressDialog() // Optional: Hide loading indicator
                    if (task.isSuccessful) {
                        // Sign in success
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show()

                        // Optional: Save username to Firestore or Realtime Database associated with user.uid here
                        // Example: saveUsernameToDatabase(user?.uid, username)

                        // Navigate to Login Activity (or directly to LandingActivity if desired)
                        auth.signOut();
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish() // Finish RegisterActivity
                    } else {
                        // If sign up fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG).show() // Show specific Firebase error
                    }
                }
        }

        alrHaveAccBtn.setOnClickListener {
            // Go back to Login Activity
            // Using finish() is generally better than starting a new LoginActivity instance if it's already on the stack
            finish()
            // If LoginActivity might not be on the stack:
            // val intent = Intent(this, LoginActivity::class.java)
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Clears activities on top
            // startActivity(intent)
        }
    }

    // Example function placeholder (implement using Firestore or Realtime DB)
    // private fun saveUsernameToDatabase(userId: String?, username: String) {
    //     if (userId == null) return
    //     // Get Firestore instance
    //     // Create a user document/entry with the username under the userId
    //     Log.d(TAG, "Simulating saving username '$username' for user ID '$userId'")
    // }
}