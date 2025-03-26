
package com.android.flickview.Activities

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.android.flickview.R

class AppLanguageListViewActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_language_page)
        val listView = findViewById<ListView>(R.id.Listview)
        val languageList = listOf("English", "Arabic", "Bulgarian", "Cantonese", "Czech", "Danish", "Dutch", "Finnish", "Filipino", "French", "German", "Hindi", "Indonesian", "Italian","Japanese", "Korean", "Mandarin", "Polish", "Portuguese", "Russian", "Spanish", "Vietnamese")

        val arrayAdapter = ArrayAdapter(this, R.layout.text_color, languageList)

        listView.adapter = arrayAdapter
    }
}
