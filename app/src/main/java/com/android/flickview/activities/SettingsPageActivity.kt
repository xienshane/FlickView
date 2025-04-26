package com.android.flickview.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.flickview.R
import com.android.flickview.dialogs.ClearCacheDialogFragment

class SettingsPageActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_page)

        val rootView = findViewById<View>(android.R.id.content)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        rootView.startAnimation(fadeIn)

        val buttonAppLang: LinearLayout = findViewById(R.id.app)
        buttonAppLang.setOnClickListener {
            Log.e("Developer", "Button is clicked")
            Toast.makeText(this, "App Language page is clicked", Toast.LENGTH_LONG).show()

            val intent = Intent(this, AppLanguageListViewActivity::class.java)
            startActivity(intent)
        }

        val buttonDeveloper: LinearLayout = findViewById(R.id.developer)
        buttonDeveloper.setOnClickListener {
            Log.e("Developer", "Button is clicked")
            Toast.makeText(this, "Developer page is clicked", Toast.LENGTH_LONG).show()

            val intent = Intent(this, DeveloperPageActivity::class.java)
            startActivity(intent)
        }

        val buttonBack: ImageButton = findViewById(R.id.button_back1)
        buttonBack.setOnClickListener {
            finish() // Goes back to the previous page
        }

        val buttonRegion: LinearLayout = findViewById(R.id.button_region)
        buttonRegion.setOnClickListener {
            Log.e("Region", "Button is clicked")
            Toast.makeText(this, "Region Preferences page is clicked", Toast.LENGTH_LONG).show()

            val intent = Intent(this, RegionPreferencesActivity::class.java)
            startActivity(intent)
        }

//        val buttonSubtitles: Button = findViewById(R.id.button_subtitles)
//        buttonSubtitles.setOnClickListener {
//            Log.d("Subtitles", "Subtitles button clicked");
//            Toast.makeText(this, "Subtitles settings clicked", Toast.LENGTH_SHORT).show();
//
//            val intent = Intent(this, SubtitlePreferencesActivity::class.java)
//            startActivity(intent)
//        }

        val buttonDisplay: LinearLayout = findViewById(R.id.button_display)
        buttonDisplay.setOnClickListener {
            Log.d("Display", "Display button clicked");
            Toast.makeText(this, "Display settings clicked", Toast.LENGTH_SHORT).show();

             val intent = Intent(this, DisplayActivity::class.java)
             startActivity(intent)
        }

        val buttonNotifications: LinearLayout = findViewById(R.id.button_notifications)
        buttonNotifications.setOnClickListener {
            Log.d("Notifications", "Notifications button clicked");
            Toast.makeText(this, "Notifications settings clicked", Toast.LENGTH_SHORT).show();

            val intent = Intent(this, NotificationsActivity::class.java)
             startActivity(intent)
        }

        //val buttonFavorites: Button = findViewById(R.id.button_favorites)
        //buttonFavorites.setOnClickListener {
           // Log.d("Favorites", "Favorites button clicked");
          //  Toast.makeText(this, "Favorites settings clicked", Toast.LENGTH_SHORT).show();

           // val intent = Intent(this, Favorites::class.java)
          //  startActivity(intent)
       // }

//        val buttonDownloads: Button = findViewById(R.id.button_downloads)
//        buttonDownloads.setOnClickListener {
//            Log.d("Downloads", "Downloads button clicked");
//            Toast.makeText(this, "Downloads settings clicked", Toast.LENGTH_SHORT).show();
//
//            val intent = Intent(this, ClearDownloadsActivity::class.java)
//            startActivity(intent)
//        }

        val buttonCache: LinearLayout = findViewById(R.id.button_clear_cache)
        buttonCache.setOnClickListener {
            Log.d("Cache", "Cache button clicked")
            Toast.makeText(this, "Cache settings clicked", Toast.LENGTH_SHORT).show()

            // Show the ClearCacheDialogFragment directly
            val dialog = ClearCacheDialogFragment()
            dialog.show(supportFragmentManager, "ClearCacheDialog")
        }



        val buttonPrivacy: LinearLayout = findViewById(R.id.button_privacy_policy)
        buttonPrivacy.setOnClickListener {
            Log.d("Privacy", "Privacy button clicked");
            Toast.makeText(this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show();

            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        val buttonCopyright: LinearLayout = findViewById(R.id.button_copyright)
        buttonCopyright.setOnClickListener {
            Log.d("Copyright", "Copyright button clicked");
            Toast.makeText(this, "Copyright Info clicked", Toast.LENGTH_SHORT).show();

             val intent = Intent(this, CopyrightActivity::class.java)
             startActivity(intent)
        }

        val buttonRate: LinearLayout = findViewById(R.id.button_rate_us)
        buttonRate.setOnClickListener {
            Log.d("Rate", "Rate App button clicked");
            Toast.makeText(this, "Rate App clicked", Toast.LENGTH_SHORT).show();

            val intent = Intent(this, RateUsActivity::class.java)
            startActivity(intent)
        }
    }
}
