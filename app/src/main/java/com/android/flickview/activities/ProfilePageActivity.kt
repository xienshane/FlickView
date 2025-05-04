package com.android.flickview.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity // Using AppCompatActivity is generally better
import com.android.flickview.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore // Import Firestore
import com.google.firebase.firestore.ktx.firestore // Import Firestore KTX
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException // Import GlideException for detailed error logging
import com.bumptech.glide.request.RequestListener // Import RequestListener
import com.bumptech.glide.request.target.Target // Import Target
import de.hdodenhof.circleimageview.CircleImageView // Import CircleImageView

class ProfilePageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore // Firestore instance

    // --- Views for Profile Info ---
    private lateinit var profilePictureImage: CircleImageView
    private lateinit var profileUsernameText: TextView
    private lateinit var profileEmailText: TextView

    private val TAG = "ProfilePageActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page)
        Log.d(TAG, "onCreate: Activity starting")

        auth = Firebase.auth
        db = Firebase.firestore // Initialize Firestore

        // --- Find Profile Views ---
        profilePictureImage = findViewById(R.id.profilePictureImage)
        profileUsernameText = findViewById(R.id.username)
        profileEmailText = findViewById(R.id.editablename) // Assuming this ID is for email

        // --- Find Other Views ---
        val rootView = findViewById<View>(android.R.id.content)
        val buttonSettings: Button = findViewById(R.id.button_settings)
        val activityCenter: Button = findViewById(R.id.ActivityCenter)
        val buttonBack: ImageView = findViewById(R.id.button_back)
        val buttonLogout: Button = findViewById(R.id.button_logout)
        val buttonEditProf: Button = findViewById(R.id.button_edit)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView3)

        // Start animation
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        rootView.startAnimation(fadeIn)

        // --- Load and Display Profile Info ---
        // Initial load when activity is created
        fetchAndDisplayProfileInfo()

        // --- Setup Listeners ---
        buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsPageActivity::class.java))
        }

        activityCenter.setOnClickListener {
            startActivity(Intent(this, ActivityCenterActivity::class.java))
        }

        buttonBack.setOnClickListener {
            finish()
        }
        buttonLogout.setOnClickListener {
            showLogoutDialog("Are you sure you want to log out?")
        }
        buttonEditProf.setOnClickListener {
            Log.d(TAG, "Edit Profile button clicked")
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent) // Start EditProfileActivity
        }

        // --- Setup Bottom Navigation ---
        bottomNavigationView.selectedItemId = R.id.profile
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivityWithReorder(LandingActivity::class.java)
                    true
                }
                R.id.profile -> true // Already here
                R.id.favorites -> {
                    startActivityWithReorder(FavoritesActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }

    // --- Re-fetch data when returning to the activity ---
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity resuming, re-fetching profile info.")
        // Re-fetch data in case it was updated in EditProfileActivity
        fetchAndDisplayProfileInfo()
    }

    // --- Helper to start activities without creating new instances if they exist ---
    private fun startActivityWithReorder(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
    }

    // --- Function to load profile data ---
    private fun fetchAndDisplayProfileInfo() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            // Handle case where user is somehow null
            Log.e(TAG, "fetchAndDisplayProfileInfo: Current user is NULL!")
            profileUsernameText.text = "Not logged in"
            profileEmailText.text = ""
            profilePictureImage.setImageResource(R.drawable.user) // Reset to placeholder
            // Optionally navigate back to login
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        Log.i(TAG, "fetchAndDisplayProfileInfo: Loading data for UID: ${currentUser.uid}")

        // Set Email (usually available from Auth)
        profileEmailText.text = currentUser.email ?: "No email available"
        Log.d(TAG, "fetchAndDisplayProfileInfo: Email set from Auth: ${currentUser.email}")

        // Fetch Username AND Profile Image URL from Firestore
        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d(TAG, "fetchAndDisplayProfileInfo: Firestore document found for user.")

                    // --- Set Username (Prioritize Auth displayName, fallback to Firestore) ---
                    val authDisplayName = currentUser.displayName
                    if (!authDisplayName.isNullOrBlank()) {
                        profileUsernameText.text = authDisplayName
                        Log.d(TAG, "fetchAndDisplayProfileInfo: Username set from Auth profile: $authDisplayName")
                    } else {
                        val firestoreUsername = document.getString("username") // Get username field
                        if (!firestoreUsername.isNullOrBlank()) {
                            profileUsernameText.text = firestoreUsername
                            Log.d(TAG, "fetchAndDisplayProfileInfo: Username set from Firestore: $firestoreUsername")
                        } else {
                            profileUsernameText.text = "Username not set" // Fallback
                            Log.w(TAG, "fetchAndDisplayProfileInfo: Username null/blank in Auth and Firestore.")
                        }
                    }

                    // --- Set Profile Picture from Firestore 'profileImageUrl' field ---
                    val firestoreProfileImageUrl = document.getString("profileImageUrl") // Get the Cloudinary URL
                    Log.d(TAG, "fetchAndDisplayProfileInfo: Profile Image URL from Firestore: $firestoreProfileImageUrl")

                    Glide.with(this@ProfilePageActivity)
                        .load(firestoreProfileImageUrl) // Load Cloudinary URL from Firestore
                        .placeholder(R.drawable.user)   // Placeholder shown while loading
                        .error(R.drawable.user)         // Placeholder shown if URL is null or loading fails
                        .listener(object : RequestListener<android.graphics.drawable.Drawable> { // Optional: Add listener for debugging Glide
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<android.graphics.drawable.Drawable>?, isFirstResource: Boolean): Boolean {
                                Log.e(TAG, "Glide onLoadFailed for URL: $firestoreProfileImageUrl", e)
                                return false // Important: return false to allow Glide to handle the error placeholder
                            }
                            override fun onResourceReady(resource: android.graphics.drawable.Drawable?, model: Any?, target: Target<android.graphics.drawable.Drawable>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                                Log.d(TAG, "Glide onResourceReady for URL: $firestoreProfileImageUrl")
                                return false // Important: return false to allow Glide to display the image
                            }
                        })
                        .into(profilePictureImage)

                } else {
                    // Firestore document doesn't exist
                    Log.w(TAG, "fetchAndDisplayProfileInfo: Firestore user document NOT FOUND for UID: ${currentUser.uid}")
                    profileUsernameText.text = currentUser.displayName ?: "Username not set" // Use Auth name if available, else default
                    profilePictureImage.setImageResource(R.drawable.user) // Set default image explicitly
                }
            }
            .addOnFailureListener { e ->
                // Error fetching Firestore document
                Log.e(TAG, "fetchAndDisplayProfileInfo: Error fetching Firestore document", e)
                profileUsernameText.text = currentUser.displayName ?: "Error loading username" // Show Auth name or error text
                profilePictureImage.setImageResource(R.drawable.user) // Set default image on error
                Toast.makeText(this, "Error loading profile details.", Toast.LENGTH_SHORT).show()
            }
    }

    // --- Logout Dialog Function ---
    private fun showLogoutDialog(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.logout_page)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // ... (rest of the dialog setup remains the same) ...
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
        dialog.window?.setGravity(android.view.Gravity.CENTER)

        val textMessage: TextView = dialog.findViewById(R.id.logout_message)
        val buttonCancel: Button = dialog.findViewById(R.id.button_cancel_logout)
        val buttonLogoutConfirm: Button = dialog.findViewById(R.id.button_logout2)

        textMessage.text = message
        textMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        buttonLogoutConfirm.setOnClickListener {
            Log.i(TAG, "Logout confirmed by user.")
            auth.signOut()
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            dialog.dismiss()
            finishAffinity() // Finish this and all parent activities
        }

        buttonCancel.setOnClickListener {
            Log.d(TAG, "Logout cancelled.")
            dialog.dismiss()
        }
        dialog.show()
    }
}