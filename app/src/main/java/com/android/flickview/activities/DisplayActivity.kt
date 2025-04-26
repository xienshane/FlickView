package com.android.flickview.activities

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.flickview.R

class DisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTheme()

        setContentView(R.layout.display_page)

        val themeSpinner: Spinner = findViewById(R.id.theme_spinner)
        val fontSpinner: Spinner = findViewById(R.id.font_spinner)
        val switchPosters: Switch = findViewById(R.id.switch_posters)
        val switchReduceAnim: Switch = findViewById(R.id.switch_reduce_anim)

        val buttonBack: ImageView = findViewById(R.id.button_back2)
        buttonBack.setOnClickListener {
            finish()
        }

        // Load saved preferences
        loadPreferences()

        // Setup Theme Spinner
        val themeOptions = resources.getStringArray(R.array.theme_options)
        val themeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themeOptions)
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        themeSpinner.adapter = themeAdapter

        themeSpinner.setSelection(themeOptions.indexOf(PreferencesHelper.getTheme(this)))

        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: android.view.View?, position: Int, id: Long) {
                val selectedTheme = themeOptions[position]
                PreferencesHelper.setTheme(this@DisplayActivity, selectedTheme)
                applyTheme()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Setup Font Size Spinner
        val fontOptions = resources.getStringArray(R.array.font_options)
        val fontAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fontOptions)
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontSpinner.adapter = fontAdapter

        fontSpinner.setSelection(fontOptions.indexOf(PreferencesHelper.getFontSize(this)))
        fontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: android.view.View?, position: Int, id: Long) {
                val selectedFontSize = fontOptions[position]
                PreferencesHelper.setFontSize(this@DisplayActivity, selectedFontSize)
                applyFontSize(selectedFontSize)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Save other preferences
        switchPosters.isChecked = PreferencesHelper.getPushNotifications(this)
        switchReduceAnim.isChecked = PreferencesHelper.getDoNotDisturb(this)

        switchPosters.setOnCheckedChangeListener { _, isChecked ->
            PreferencesHelper.setPushNotifications(this, isChecked)
        }

        switchReduceAnim.setOnCheckedChangeListener { _, isChecked ->
            PreferencesHelper.setDoNotDisturb(this, isChecked)
        }
    }

    private fun applyTheme() {
        when (PreferencesHelper.getTheme(this)) {
            "Light" -> setTheme(R.style.Theme_Flickview)
            "Dark" -> setTheme(R.style.Theme_Flickview)
            //"System Default" -> setTheme(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //    setTheme(androidx.appcompat.R.style.Theme_AppCompat) // System default theme
            //} else {
            //    setTheme(R.style.Base_Theme_FlickView) // Fallback to light theme for older versions
            //})
        }
    }

    private fun applyFontSize(fontSize: String) {
        when (fontSize) {
            "Small" -> {
                // Apply small font size
                // This could be dynamically applied in your layout or style
                Toast.makeText(this, "Small font size applied", Toast.LENGTH_SHORT).show()
            }
            "Default" -> {
                // Apply default font size
                Toast.makeText(this, "Default font size applied", Toast.LENGTH_SHORT).show()
            }
            "Large" -> {
                // Apply large font size
                Toast.makeText(this, "Large font size applied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPreferences() {
        // Apply loaded preferences
        PreferencesHelper.getTheme(this)
        PreferencesHelper.getFontSize(this)
    }
}
