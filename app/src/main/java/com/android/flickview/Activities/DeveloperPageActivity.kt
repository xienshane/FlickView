package com.android.flickview.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.ImageButton
import com.android.flickview.R

class DeveloperPageActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.developer_page)

        val buttonBack: ImageButton = findViewById(R.id.button_back2)
        buttonBack.setOnClickListener {
            finish()
        }
    }
}