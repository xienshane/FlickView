package com.android.flickview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AppLanguageListViewActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_language_page)
        val listView = findViewById<ListView>(R.id.Listview)
        val languageList = listOf("English", "Arabic", "Bulgarian", "Cantonese", "Czech", "Danish", "Dutch", "Finnish", "Filipino", "French", "German", "Hindi", "Indonesian", "Italian","Japanese", "Korean", "Mandarin", "Polish", "Portuguese", "Russian", "Spanish", "Vietnamese")

        val arrayAdapter = ArrayAdapter(this,R.layout.text_color_page, languageList)

        listView.adapter = arrayAdapter
    }
}