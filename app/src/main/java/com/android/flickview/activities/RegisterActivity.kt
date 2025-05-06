package com.android.flickview.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar // Optional: for loading indicator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.flickview.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest // Import for setting displayName
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue // Import for server timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        db = Firebase.firestore // Initialize Firestore

        val emailEditText = findViewById<EditText>(R.id.email)
        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password2)
        val confirmPasswordEditText = findViewById<EditText>(R.id.password)
        val createAccBtn = findViewById<Button>(R.id.createAccountBtn)
        val alrHaveAccBtn = findViewById<Button>(R.id.alreadyHaveAnAccountBtn)

        createAccBtn.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim() // Get username
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // --- Validation ---
            if (!validateInput(email, username, password, confirmPassword)) {
                return@setOnClickListener // Stop if validation fails
            }
            // --- End Validation ---

            createAccBtn.isEnabled = false

            // --- Create user with Firebase Auth ---
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        val userId = user?.uid

                        if (userId == null) {
                            Log.e(TAG, "User created but UID is null!")
                            Toast.makeText(baseContext, "Registration failed: Could not get user ID.", Toast.LENGTH_LONG).show()
                            createAccBtn.isEnabled = true
                            return@addOnCompleteListener
                        }

                        updateUserProfile(username) { profileUpdateSuccess ->
                            if (!profileUpdateSuccess) {
                                // Log failure but continue to save to Firestore anyway
                                Log.w(TAG, "Failed to update Auth profile displayName, but proceeding.")
                            }

                            saveUserDataToFirestore(userId, username, email) { firestoreSaveSuccess ->
                                createAccBtn.isEnabled = true

                                if (firestoreSaveSuccess) {
                                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                    // Navigate directly to LandingActivity (recommended)
                                    navigateToLandingActivity()
                                } else {
                                    // Firestore save failed, Auth user exists. Inform user.
                                    // More complex recovery could involve deleting the auth user.
                                    Toast.makeText(baseContext, "Account created, but failed to save details.", Toast.LENGTH_LONG).show()
                                    // Decide where to navigate - maybe back to Login?
                                    navigateToLoginActivity()
                                }
                            }
                        }

                    } else {
                        // --- Auth User Creation Failed ---
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        // Hide progress indicator (optional)
                        // progressBar.visibility = View.GONE
                        createAccBtn.isEnabled = true // Re-enable button
                    }
                }
        }

        alrHaveAccBtn.setOnClickListener {
            finish()
        }
    }

    // --- Helper function for Input Validation ---
    private fun validateInput(email: String, username: String, pass: String, confirmPass: String): Boolean {
        if (email.isEmpty() || username.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (username.length < 3 || username.length > 15) {
            Toast.makeText(this, "Username must be 3-15 characters long.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass.length < 6 || pass.length > 20) {
            Toast.makeText(this, "Password must be 6-20 characters long.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass != confirmPass) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // --- Helper function to update Firebase Auth Profile ---
    private fun updateUserProfile(username: String, callback: (Boolean) -> Unit) {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            // .setPhotoUri(...) // Can add profile picture later
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile display name updated successfully.")
                    callback(true)
                } else {
                    Log.w(TAG, "Failed to update user profile display name.", task.exception)
                    callback(false) // Indicate failure, but we might continue anyway
                }
            } ?: callback(false) // If user is somehow null here, treat as failure
    }


    // --- Helper function to save data to Firestore ---
    private fun saveUserDataToFirestore(userId: String, username: String, email: String?, callback: (Boolean) -> Unit) {
        val userData = hashMapOf(
            "username" to username,
            "email" to email,
            "createdAt" to FieldValue.serverTimestamp()
        )

        // Save to 'users' collection with the document ID being the user's UID
        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "User data successfully written to Firestore for UID: $userId")
                callback(true) // Signal success
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error writing user data to Firestore for UID: $userId", e)
                callback(false) // Signal failure
            }
    }

    // --- Helper function for Navigation to Landing Activity ---
    private fun navigateToLandingActivity() {
        val intent = Intent(this, LandingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finish RegisterActivity
    }

    // --- Helper function for Navigation to Login Activity (Fallback on error) ---
    private fun navigateToLoginActivity() {
        auth.signOut() // Sign out if Firestore save failed but auth succeeded
        val intent = Intent(this, LoginActivity::class.java)
        // Optional: Clear top if you want login to be the only thing left
        // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish() // Finish RegisterActivity
    }
}