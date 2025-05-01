package com.android.flickview.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.android.flickview.R
import com.google.android.material.button.MaterialButton
// Add Firebase Auth imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogoutDialogFragment : DialogFragment() {

    private var cardView: CardView? = null
    private lateinit var auth: FirebaseAuth // Declare Firebase Auth instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Firebase Auth
        auth = Firebase.auth
        // Inflate your custom logout layout
        return inflater.inflate(R.layout.logout_page, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reference the UI elements with correct IDs
        val textMessage: TextView = view.findViewById(R.id.logout_message)
        val cancelButton: MaterialButton = view.findViewById(R.id.button_cancel_logout) // Corrected ID
        val logoutButton: MaterialButton = view.findViewById(R.id.button_logout2)

        cardView = view.findViewById(R.id.logout)
        // Set the text message in the dialog
        textMessage.text = "Are you sure you want to log out?"

        // Handle Cancel Button Click
        cancelButton.setOnClickListener {
            dismiss() // Close the dialog
        }

        // Handle Logout Button Click
        logoutButton.setOnClickListener {
            // 1. SIGN OUT FROM FIREBASE
            auth.signOut()

            // 2. Give feedback
            Toast.makeText(context, "Logged out successfully.", Toast.LENGTH_SHORT).show() // Adjusted message

            // 3. Start the LoginActivity AFTER logging out and CLEAR THE TASK STACK
            val intent = Intent(activity, LoginActivity::class.java)
            // Add flags to clear the back stack and start LoginActivity as a new task
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            // 4. Dismiss the dialog
            dismiss()

            // 5. Optional but recommended: Finish the activity hosting the dialog (e.g., LandingActivity)
            activity?.finish()
        }
    }


    override fun onStart() {
        super.onStart()

        // Customize dialog appearance (optional)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}