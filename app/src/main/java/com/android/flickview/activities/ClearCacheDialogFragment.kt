package com.android.flickview.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.flickview.R
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import java.io.File

class ClearCacheDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogStyle)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.clear_cache_page, null)

        val cancelButton = view.findViewById<MaterialButton>(R.id.button_cancel_cache)
        val confirmButton = view.findViewById<MaterialButton>(R.id.button_confirm_cache)

        cancelButton.setOnClickListener {
            dismiss() // Close the dialog without doing anything
        }

        confirmButton.setOnClickListener {
            clearAppCache()
            Toast.makeText(requireContext(), "Cache cleared successfully!", Toast.LENGTH_SHORT).show()
            dismiss() // Close the dialog after clearing cache
        }

        builder.setView(view)
        val dialog = builder.create()

        // Modify the window to remove black background or dimming
        dialog.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent) // Remove background
            window.setDimAmount(0f) // Remove dimming effect
        }

        return dialog
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

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        // Optionally, you can call `activity?.finish()` if needed, but it's not required
    }
}
