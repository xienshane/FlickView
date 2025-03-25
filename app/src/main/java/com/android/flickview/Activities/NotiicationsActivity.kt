package com.android.flickview.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import com.android.flickview.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotificationsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView3)
        bottomNavigationView.selectedItemId = R.id.notifications

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, LandingActivity::class.java))
                    finish()
                    true
                }
                R.id.browse -> {
                    startActivity(Intent(this, BrowseActivity::class.java))
                    finish()
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfilePageActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
