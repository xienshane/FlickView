package com.android.flickview.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.DialogFragment
import com.android.flickview.R
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.widget.Toast

class LogoutDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate your custom logout layout
        return inflater.inflate(R.layout.logout_page, container, false)
    }

    private var cardView: CardView? = null

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
            Toast.makeText(context, "Logging out...", Toast.LENGTH_LONG).show()
            dismiss()

            // Start the LoginActivity after logging out
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
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
