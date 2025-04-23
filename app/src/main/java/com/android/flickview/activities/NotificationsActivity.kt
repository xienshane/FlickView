package com.android.flickview.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.flickview.R

class NotificationsActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications_page)

        val switchPush: Switch = findViewById(R.id.switch_push)
        val switchEmail: Switch = findViewById(R.id.switch_email)
        val switchTrailers: Switch = findViewById(R.id.switch_trailers)
        val switchBanners: Switch = findViewById(R.id.switch_banners)
        val switchDnd: Switch = findViewById(R.id.switch_dnd)

        // Load saved preferences
        loadNotificationPreferences()

        switchPush.setOnCheckedChangeListener { _, isChecked ->
            PreferencesHelper.setPushNotifications(this, isChecked)
            Toast.makeText(this, "Push notifications ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        switchEmail.setOnCheckedChangeListener { _, isChecked ->
            PreferencesHelper.setEmailUpdates(this, isChecked)
            Toast.makeText(this, "Email updates ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        switchTrailers.setOnCheckedChangeListener { _, isChecked ->
            PreferencesHelper.setTrailerAlerts(this, isChecked)
            Toast.makeText(this, "Trailer alerts ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        switchBanners.setOnCheckedChangeListener { _, isChecked ->
            PreferencesHelper.setInAppBanners(this, isChecked)
            Toast.makeText(this, "In-app banners ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        switchDnd.setOnCheckedChangeListener { _, isChecked ->
            PreferencesHelper.setDoNotDisturb(this, isChecked)
            Toast.makeText(this, "Do Not Disturb ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNotificationPreferences() {
        val switchPush: Switch = findViewById(R.id.switch_push)
        val switchEmail: Switch = findViewById(R.id.switch_email)
        val switchTrailers: Switch = findViewById(R.id.switch_trailers)
        val switchBanners: Switch = findViewById(R.id.switch_banners)
        val switchDnd: Switch = findViewById(R.id.switch_dnd)

        switchPush.isChecked = PreferencesHelper.getPushNotifications(this)
        switchEmail.isChecked = PreferencesHelper.getEmailUpdates(this)
        switchTrailers.isChecked = PreferencesHelper.getTrailerAlerts(this)
        switchBanners.isChecked = PreferencesHelper.getInAppBanners(this)
        switchDnd.isChecked = PreferencesHelper.getDoNotDisturb(this)
    }
}
