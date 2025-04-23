package com.android.flickview.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.android.flickview.R

class DeveloperPageActivity : AppCompatActivity() {
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