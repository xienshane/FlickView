package com.android.flickview.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.android.flickview.R

class ClearCacheActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clear_cache_page)

        val cancelButton: Button = findViewById(R.id.button_cancel_cache)
        val confirmButton: Button = findViewById(R.id.button_confirm_cache)

        cancelButton.setOnClickListener {
            finish()
        }

        confirmButton.setOnClickListener {
            clearAppCache()
            Toast.makeText(this, "Cache cleared successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun clearAppCache() {
        try {
            val dir = cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: java.io.File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (child in children) {
                val success = deleteDir(java.io.File(dir, child))
                if (!success) return false
            }
        }
        return dir?.delete() ?: false
    }
}
