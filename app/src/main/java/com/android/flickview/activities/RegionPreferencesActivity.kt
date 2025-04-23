package com.android.flickview.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Switch
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.flickview.R

class RegionPreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.region_preferences_page)

        val regionSpinner: Spinner = findViewById(R.id.region_spinner)
        val regionFormatSpinner: Spinner = findViewById(R.id.region_format_spinner)
        val contentLanguageSpinner: Spinner = findViewById(R.id.content_language_spinner)
        val switchContentRating: Switch = findViewById(R.id.switch_content_rating)

        // Set up spinners for region, format, and language
        setupRegionSpinner(regionSpinner)
        setupRegionFormatSpinner(regionFormatSpinner)
        setupContentLanguageSpinner(contentLanguageSpinner)

        // Load preferences
        loadRegionPreferences()

        // Set content rating filter
        switchContentRating.setOnCheckedChangeListener { _, isChecked ->
            PreferencesHelper.setContentRatingFilter(this, isChecked)
            Toast.makeText(this, "Content Rating Filter ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRegionSpinner(regionSpinner: Spinner) {
        val regionOptions = resources.getStringArray(R.array.region_options)
        val regionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, regionOptions)
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = regionAdapter
    }

    private fun setupRegionFormatSpinner(regionFormatSpinner: Spinner) {
        val regionFormats = resources.getStringArray(R.array.region_formats)
        val regionFormatAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, regionFormats)
        regionFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionFormatSpinner.adapter = regionFormatAdapter
    }

    private fun setupContentLanguageSpinner(contentLanguageSpinner: Spinner) {
        val contentLanguages = resources.getStringArray(R.array.content_languages)
        val contentLanguageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, contentLanguages)
        contentLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        contentLanguageSpinner.adapter = contentLanguageAdapter
    }

    private fun loadRegionPreferences() {
        // Load saved preferences (Region, Format, Language, Rating filter)
        val region = PreferencesHelper.getRegion(this)
        val regionFormat = PreferencesHelper.getRegionFormat(this)
        val contentLanguage = PreferencesHelper.getContentLanguage(this)
        val isContentRatingEnabled = PreferencesHelper.isContentRatingEnabled(this)

        // Update UI with saved preferences
        // Set spinners and switches accordingly
    }
}
