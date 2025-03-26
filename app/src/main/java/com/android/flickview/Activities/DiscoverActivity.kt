package com.android.flickview.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.android.flickview.R

class DiscoverActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1500)

        setContentView(R.layout.discover)

        val registerBtn = findViewById<Button>(R.id.getStartedBtn)
        registerBtn.setOnClickListener{
            Log.e("Sample Project", "Button is clicked!")

            Toast.makeText(this, "Button is clicked.", Toast.LENGTH_LONG).show()

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()


        }
    }
}