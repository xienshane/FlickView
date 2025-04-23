package com.android.flickview.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.android.flickview.R

class ClearDownloadsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clear_downloads_page)

        val cancelButton: Button = findViewById(R.id.button_cancel_downloads)
        val confirmButton: Button = findViewById(R.id.button_confirm_downloads)

        cancelButton.setOnClickListener {
            finish()
        }

        confirmButton.setOnClickListener {
            // TODO: Add actual delete logic
            Toast.makeText(this, "Downloads cleared successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}