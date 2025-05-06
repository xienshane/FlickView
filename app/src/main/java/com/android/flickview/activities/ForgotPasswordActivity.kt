package com.android.flickview.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.flickview.R

class ForgotPasswordActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val emailEditText = findViewById<EditText>(R.id.email)
        val sendEmailButton = findViewById<Button>(R.id.sendEmailButton)

        val txtBack: TextView = findViewById(R.id.backtoLogin)
        txtBack.setOnClickListener {
            finish()
        }

        sendEmailButton.setOnClickListener {
            val email = emailEditText.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            } else {
                // Simulate sending a reset link
                Toast.makeText(this, "Password reset link sent to $email", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
