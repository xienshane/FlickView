package com.android.flickview.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.android.flickview.R


class CopyrightActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.copyright_info_page)

        val backButton: ImageButton = findViewById(R.id.button_back)

        backButton.setOnClickListener {
            finish()
        }
    }
}