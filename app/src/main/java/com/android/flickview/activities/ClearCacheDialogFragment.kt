package com.android.flickview.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.flickview.R
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.view.WindowManager
import java.io.File
import android.view.animation.OvershootInterpolator
import androidx.cardview.widget.CardView // or MaterialCardView if you use it!

class ClearCacheDialogFragment : DialogFragment() {

    private var cardView: CardView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.clear_cache_page, null)

        cardView = view.findViewById(R.id.clear_cache_card) // ID of your CardView

        val cancelButton = view.findViewById<MaterialButton>(R.id.button_cancel_cache)
        val confirmButton = view.findViewById<MaterialButton>(R.id.button_confirm_cache)

        cancelButton.setOnClickListener {
            dismiss()
        }

        confirmButton.setOnClickListener {
            clearAppCache()
            Toast.makeText(requireContext(), "Cache cleared successfully!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        builder.setView(view)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setDimAmount(0f)

        return dialog
    }

    override fun onStart() {
        super.onStart()

        cardView?.apply {
            scaleX = 0.8f
            scaleY = 0.8f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(350)
                .setInterpolator(OvershootInterpolator())
                .start()
        }
    }



    private fun clearAppCache() {
        try {
            val dir = requireContext().cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (child in children.orEmpty()) {
                val success = deleteDir(File(dir, child))
                if (!success) return false
            }
        }
        return dir?.delete() ?: false
    }
}
