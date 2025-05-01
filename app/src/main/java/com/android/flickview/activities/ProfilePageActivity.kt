package com.android.flickview.activities

import android.annotation.SuppressLint
import android.app.Activity // Keep using Activity if you prefer, but AppCompatActivity is generally recommended
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
import android.widget.ImageView // Changed from ImageButton if it's just an ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity // Consider changing Activity to AppCompatActivity
import com.android.flickview.R
import com.google.android.material.bottomnavigation.BottomNavigationView
// Import Firebase Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// Consider changing Activity to AppCompatActivity for better lifecycle and feature support
class ProfilePageActivity : AppCompatActivity() { // Changed to AppCompatActivity

    private lateinit var auth: FirebaseAuth // Declare Firebase Auth instance

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val rootView = findViewById<View>(android.R.id.content)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        rootView.startAnimation(fadeIn)

        // Settings Button
        val buttonSettings: Button = findViewById(R.id.button_settings)
        buttonSettings.setOnClickListener {
            // Log.e("Settings", "Button is clicked") // Use Log.d or Log.i for info
            Log.d("ProfilePageActivity", "Settings button clicked")
            // Toast.makeText(this, "Settings is clicked", Toast.LENGTH_LONG).show() // Optional Toast
            val intent = Intent(this, SettingsPageActivity::class.java)
            startActivity(intent)
        }

        // Back Button
        val buttonBack: ImageView = findViewById(R.id.button_back)
        buttonBack.setOnClickListener {
            finish() // Closes the current activity
        }

        // Logout Button (Pop-up Confirmation)
        val buttonLogout: Button = findViewById(R.id.button_logout)
        buttonLogout.setOnClickListener {
            showCustomDialogBox("Are you sure you want to log out?")
        }

        val buttonEditProf: Button = findViewById(R.id.button_edit)
        buttonEditProf.setOnClickListener {
            // Log.e("EditProf", "Edit Profile is clicked") // Use Log.d or Log.i
            Log.d("ProfilePageActivity", "Edit Profile button clicked")
            // Toast.makeText(this, "Edit Profile is clicked", Toast.LENGTH_LONG).show() // Optional Toast
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Bottom Navigation View Setup
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView3)

        // Set the selected item to 'profile' when ProfilePageActivity is opened
        bottomNavigationView.selectedItemId = R.id.profile

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Check if LandingActivity is already in the stack to avoid creating multiple instances
                    val intent = Intent(this, LandingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT // Bring LandingActivity to front if exists
                    startActivity(intent)
                    // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Optional animation
                    true
                }
                R.id.profile -> {
                    // You are already in ProfileActivity, do nothing
                    true
                }
                R.id.favorites -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT // Bring FavoritesActivity to front if exists
                    startActivity(intent)
                    // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Optional animation
                    true
                }
                else -> false
            }
        }
    }

    private fun showCustomDialogBox(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false) // User must explicitly choose Cancel or Logout
        dialog.setContentView(R.layout.logout_page) // Ensure this layout has the correct IDs

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set dialog dimensions and gravity
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
        dialog.window?.setGravity(android.view.Gravity.CENTER)

        // Get views from the dialog's layout
        val textMessage: TextView = dialog.findViewById(R.id.logout_message)
        val buttonCancel: Button = dialog.findViewById(R.id.button_cancel_logout) // Verify ID in logout_page.xml
        val buttonLogoutConfirm: Button = dialog.findViewById(R.id.button_logout2)   // Verify ID in logout_page.xml

        textMessage.text = message
        textMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Optional text size adjustment

        // --- Updated Logout Button Logic ---
        buttonLogoutConfirm.setOnClickListener {
            // 1. Sign out from Firebase
            auth.signOut()

            // 2. Show feedback
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()

            // 3. Navigate to LoginActivity and clear the task stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            // 4. Dismiss the dialog
            dialog.dismiss()

            // 5. Finish the current activity (ProfilePageActivity)
            finish()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss() // Just close the dialog
        }

        dialog.show()
    }

    private fun showCustomDialogBox2() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.edit_profile_page)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Removed redundant setLayout calls
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(android.view.Gravity.CENTER)

        val buttonCancel: Button = dialog.findViewById(R.id.button_cancel) // Verify ID in edit_profile_page.xml
        val buttonSaveConfirm: Button = dialog.findViewById(R.id.button_save) // Verify ID in edit_profile_page.xml

        // Assuming save does something before dismissing, otherwise keep separate listeners
        buttonSaveConfirm.setOnClickListener {
            // Add save logic here first...
            Toast.makeText(this, "Profile Saved (Placeholder)", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}