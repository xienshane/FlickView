package com.android.flickview.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.android.flickview.R


class RateUsActivity : Activity() {
    private var ratingBar: RatingBar? = null
    private var submitButton: Button? = null
    private var laterButton:android.widget.Button? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rate_page)
        val ratingBar = findViewById<RatingBar>(R.id.button_rating)
        val submitButton = findViewById<Button>(R.id.button_submit)
        val laterButton = findViewById<Button>(R.id.button_later)

        submitButton.setOnClickListener(View.OnClickListener { view: View? ->
            val rating = ratingBar.getRating()
            if (rating >= 4) {
                // Send user to Play Store or review page
                openAppInPlayStore()
            } else if (rating > 0) {
                // Thank them and optionally open feedback form
                Toast.makeText(this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show()
                // TODO: Optionally redirect to in-app feedback form
            } else {
                Toast.makeText(this, "Please select a rating first.", Toast.LENGTH_SHORT).show()
            }
        })
        laterButton.setOnClickListener(View.OnClickListener { view: View? -> finish() })
    }

    private fun openAppInPlayStore() {
        val packageName = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }
}