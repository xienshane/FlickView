package com.android.flickview.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.android.flickview.R
import com.cloudinary.android.MediaManager

class DiscoverActivity : Activity() {

    private val CLOUDINARY_CLOUD_NAME = "dhkpgyxup" // <--- REPLACE THIS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1500)

        setContentView(R.layout.discover)

        val registerBtn = findViewById<Button>(R.id.getStartedBtn)
        registerBtn.setOnClickListener{
            Log.e("Sample Project", "Button is clicked!")

            Toast.makeText(this, "Button is clicked.", Toast.LENGTH_LONG).show()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()


            try {
                val config = mapOf(
                    "cloud_name" to CLOUDINARY_CLOUD_NAME
                    // "api_key" to "YOUR_API_KEY", // Not needed for basic init/unsigned
                    // "api_secret" to "YOUR_API_SECRET" // NEVER PUT SECRET HERE
                )
                MediaManager.init(this, config)
                Log.i("MyApplication", "Cloudinary initialized with cloud name: $CLOUDINARY_CLOUD_NAME")
            } catch (e: Exception) {
                Log.e("MyApplication", "Failed to initialize Cloudinary", e)
                // Handle initialization error appropriately
            }
        }
    }
}