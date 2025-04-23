package com.android.flickview.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Switch
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.flickview.R

class SubtitlePreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subtitle_page)

        val switchSubtitles: Switch = findViewById(R.id.switch_subtitles)
        val subtitleSpinner: Spinner = findViewById(R.id.subtitle_spinner)
        val seekbarFontSize: SeekBar = findViewById(R.id.seekbar_font_size)
        val seekbarPosition: SeekBar = findViewById(R.id.seekbar_position)
        val switchSubtitleBackground: Switch = findViewById(R.id.switch_subtitle_background)

        // Load saved subtitle preferences
        loadSubtitlePreferences()

        // Enable/Disable Subtitles
        switchSubtitles.setOnCheckedChangeListener { _, isChecked ->
            PreferencesHelper.setSubtitlesEnabled(this, isChecked)
            Toast.makeText(this, "Subtitles ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Set subtitle language selection
        val subtitleLanguages = resources.getStringArray(R.array.subtitle_languages)
        val subtitleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subtitleLanguages)
        subtitleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        subtitleSpinner.adapter = subtitleAdapter

        // Font size and position settings
        seekbarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                PreferencesHelper.setSubtitleFontSize(this@SubtitlePreferencesActivity, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekbarPosition.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                PreferencesHelper.setSubtitlePosition(this@SubtitlePreferencesActivity, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Background setting
        switchSubtitleBackground.setOnCheckedChangeListener { _, isChecked ->
            PreferencesHelper.setSubtitleBackground(this, isChecked)
            Toast.makeText(this, "Subtitle background ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSubtitlePreferences() {
        // Load saved preferences (Font Size, Position, Background)
        val fontSize = PreferencesHelper.getSubtitleFontSize(this)
        val position = PreferencesHelper.getSubtitlePosition(this)
        val backgroundEnabled = PreferencesHelper.getSubtitleBackground(this)

        // Set the UI elements (SeekBars, Switches) based on saved preferences
    }
}
