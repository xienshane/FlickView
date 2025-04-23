package com.android.flickview.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.android.flickview.R


class CopyrightActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.copyright_info_page)

        val backButton: Button = findViewById(R.id.button_back)

        backButton.setOnClickListener {
            finish()
        }
    }
}