package com.android.flickview.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.android.flickview.R

class ProfilePageActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page)

        // Settings Button
        val buttonSettings: Button = findViewById(R.id.button_settings)
        buttonSettings.setOnClickListener {
            Log.e("Settings", "Button is clicked")
            Toast.makeText(this, "Settings is clicked", Toast.LENGTH_LONG).show()
            val intent = Intent(this, SettingsPageActivity::class.java)
            startActivity(intent)
        }

        // Back Button
        val buttonBack: ImageButton = findViewById(R.id.button_back)
        buttonBack.setOnClickListener {
            finish()
        }

        // Logout Button (Pop-up Confirmation)
        val buttonLogout: Button = findViewById(R.id.button_logout)
        buttonLogout.setOnClickListener {
            showCustomDialogBox("Are you sure you want to log out?")
        }

        val buttonEditProf: Button = findViewById(R.id.button_edit)
        buttonEditProf.setOnClickListener {
            showCustomDialogBox2()
        }
    }

    private fun showCustomDialogBox(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.logout_page)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(android.view.Gravity.CENTER)

        val textMessage: TextView = dialog.findViewById(R.id.button_message)
        val buttonCancel: Button = dialog.findViewById(R.id.button_cancel)
        val buttonLogoutConfirm: Button = dialog.findViewById(R.id.button_logout2)

        textMessage.text = message

        buttonLogoutConfirm.setOnClickListener {
           
            clearUserSession()

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

            // Navigate to Login Activity
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun clearUserSession() {

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()


    }

    private fun showCustomDialogBox2() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.edit_profile_page)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(android.view.Gravity.CENTER)

        val buttonCancel: Button = dialog.findViewById(R.id.button_cancel)
        val buttonSaveConfirm: Button = dialog.findViewById(R.id.button_save)

        listOf(buttonSaveConfirm, buttonCancel).forEach { it.setOnClickListener { dialog.dismiss() } }

        dialog.show()
    }
}