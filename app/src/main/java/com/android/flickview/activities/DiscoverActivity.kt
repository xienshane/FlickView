package com.android.flickview.activities

import android.annotation.SuppressLint
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
        registerBtn.setOnHoverListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_HOVER_ENTER -> {
                    val growX = android.animation.AnimatorInflater.loadAnimator(this, R.animator.button_hover_grow)
                    val growY = android.animation.AnimatorInflater.loadAnimator(this, R.animator.button_hover_grow_y)
                    growX.setTarget(v)
                    growY.setTarget(v)
                    growX.start()
                    growY.start()
                }
                android.view.MotionEvent.ACTION_HOVER_EXIT -> {
                    val shrinkX = android.animation.AnimatorInflater.loadAnimator(this, R.animator.button_hover_shrink)
                    val shrinkY = android.animation.AnimatorInflater.loadAnimator(this, R.animator.button_hover_shrink_y)
                    shrinkX.setTarget(v)
                    shrinkY.setTarget(v)
                    shrinkX.start()
                    shrinkY.start()
                }
            }
            false
        }

        registerBtn.setOnClickListener{
            Log.e("Sample Project", "Button is clicked!")

            //Toast.makeText(this, "Button is clicked.", Toast.LENGTH_LONG).show()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()


            try {
                val config = mapOf(
                    "cloud_name" to CLOUDINARY_CLOUD_NAME
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