package com.android.flickview.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.android.flickview.R

class SettingsPageActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_page)

        val buttonDeveloper: Button = findViewById(R.id.button_developer)
        buttonDeveloper.setOnClickListener {
            Log.e("Developer", "Button is clicked")
            Toast.makeText(this, "Developer page is clicked", Toast.LENGTH_LONG).show()

            val intent = Intent(this, DeveloperPageActivity::class.java)
            startActivity(intent)
        }

        val buttonBack: ImageButton = findViewById(R.id.button_back1)
        buttonBack.setOnClickListener {
            finish() // Goes back to the previous page
        }
    }
}
