package com.android.flickview.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.flickview.R

class LoginActivity : Activity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)

        val registerBtn = findViewById<Button>(R.id.signinButton)
        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val loginBtn = findViewById<Button>(R.id.loginButton)
        loginBtn.setOnClickListener {
            val usernameEditText = findViewById<EditText>(R.id.username)
            val passwordEditText = findViewById<EditText>(R.id.password)

            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            val savedUsername = sharedPreferences.getString("username", null)
            val savedPassword = sharedPreferences.getString("password", null)

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else if (username.length < 3 || username.length > 15) {
                Toast.makeText(this, "Username must be 3-15 characters long.", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6 || password.length > 20) {
                Toast.makeText(this, "Password must be 6-20 characters long.", Toast.LENGTH_SHORT).show()
            } else {
                if (username == savedUsername && password == savedPassword) {
                    val intent = Intent(this, LandingActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val forgotPasswordText = findViewById<TextView>(R.id.forgotPassword)
        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
