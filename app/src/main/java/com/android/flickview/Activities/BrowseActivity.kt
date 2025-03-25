package com.android.flickview.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import com.android.flickview.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BrowseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browse)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView3)
        bottomNavigationView.selectedItemId = R.id.browse

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, LandingActivity::class.java))
                    finish()
                    true
                }
                R.id.notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
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
