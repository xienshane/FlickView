package com.android.flickview.activities // Package declaration

import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {

    private const val PREFS_NAME = "app_preferences"

    // Existing Keys
    private const val KEY_THEME = "theme"
    private const val KEY_FONT_SIZE = "font_size" // General UI font size
    private const val KEY_PUSH_NOTIFICATIONS = "push_notifications"
    private const val KEY_EMAIL_UPDATES = "email_updates"
    private const val KEY_TRAILER_ALERTS = "trailer_alerts"
    private const val KEY_IN_APP_BANNERS = "in_app_banners"
    private const val KEY_DO_NOT_DISTURB = "do_not_disturb"
    private const val KEY_REGION = "region"
    private const val KEY_REGION_FORMAT = "region_format"
    private const val KEY_CONTENT_LANGUAGE = "content_language"
    private const val KEY_CONTENT_RATING_ENABLED = "content_rating_enabled"
    private const val KEY_CONTENT_RATING_FILTER = "content_rating_filter"

    // --- New Keys for Subtitle Preferences ---
    private const val KEY_SUBTITLES_ENABLED = "subtitles_enabled"
    private const val KEY_SUBTITLE_FONT_SIZE = "subtitle_font_size"
    private const val KEY_SUBTITLE_POSITION = "subtitle_position"
    private const val KEY_SUBTITLE_BACKGROUND_ENABLED = "subtitle_background_enabled"
    // --- End New Keys ---


    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // --- Existing Functions ---
    // (Theme, General Font Size, Notifications, Region, Content Rating, etc.)
    // ... (Keep all previous functions here) ...

    fun setTheme(context: Context, theme: String) {
        getSharedPreferences(context).edit().putString(KEY_THEME, theme).apply()
    }

    fun getTheme(context: Context): String {
        return getSharedPreferences(context).getString(KEY_THEME, "System Default") ?: "System Default"
    }

    fun setFontSize(context: Context, fontSize: String) {
        getSharedPreferences(context).edit().putString(KEY_FONT_SIZE, fontSize).apply()
    }

    fun getFontSize(context: Context): String {
        return getSharedPreferences(context).getString(KEY_FONT_SIZE, "Default") ?: "Default"
    }

    fun setPushNotifications(context: Context, isEnabled: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_PUSH_NOTIFICATIONS, isEnabled).apply()
    }

    fun getPushNotifications(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_PUSH_NOTIFICATIONS, true)
    }

    fun setEmailUpdates(context: Context, isEnabled: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_EMAIL_UPDATES, isEnabled).apply()
    }

    fun getEmailUpdates(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_EMAIL_UPDATES, true)
    }

    fun setTrailerAlerts(context: Context, isEnabled: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_TRAILER_ALERTS, isEnabled).apply()
    }

    fun getTrailerAlerts(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_TRAILER_ALERTS, true)
    }

    fun setInAppBanners(context: Context, isEnabled: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_IN_APP_BANNERS, isEnabled).apply()
    }

    fun getInAppBanners(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IN_APP_BANNERS, true)
    }

    fun setDoNotDisturb(context: Context, isEnabled: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_DO_NOT_DISTURB, isEnabled).apply()
    }

    fun getDoNotDisturb(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_DO_NOT_DISTURB, false)
    }

    fun setRegion(context: Context, region: String) {
        getSharedPreferences(context).edit().putString(KEY_REGION, region).apply()
    }

    fun getRegion(context: Context): String {
        return getSharedPreferences(context).getString(KEY_REGION, "US") ?: "US"
    }

    fun setRegionFormat(context: Context, regionFormat: String) {
        getSharedPreferences(context).edit().putString(KEY_REGION_FORMAT, regionFormat).apply()
    }

    fun getRegionFormat(context: Context): String {
        return getSharedPreferences(context).getString(KEY_REGION_FORMAT, "System Default") ?: "System Default"
    }

    fun setContentLanguage(context: Context, contentLanguage: String) {
        getSharedPreferences(context).edit().putString(KEY_CONTENT_LANGUAGE, contentLanguage).apply()
    }

    fun getContentLanguage(context: Context): String {
        return getSharedPreferences(context).getString(KEY_CONTENT_LANGUAGE, "en") ?: "en"
    }

    fun setContentRatingEnabled(context: Context, isEnabled: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_CONTENT_RATING_ENABLED, isEnabled).apply()
    }

    fun isContentRatingEnabled(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_CONTENT_RATING_ENABLED, true)
    }

    fun setContentRatingFilter(context: Context, isEnabled: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_CONTENT_RATING_FILTER, isEnabled).apply()
    }

    fun isContentRatingFilterEnabled(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_CONTENT_RATING_FILTER, false)
    }


    // --- New Functions for Subtitle Preferences ---

    /**
     * Sets whether subtitles are globally enabled or disabled.
     * @param context The application context.
     * @param isEnabled True to enable subtitles, false to disable them.
     */
    fun setSubtitlesEnabled(context: Context, isEnabled: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_SUBTITLES_ENABLED, isEnabled).apply()
    }

    /**
     * Checks if subtitles are globally enabled.
     * @param context The application context.
     * @return True if subtitles are enabled, false otherwise. Defaults to false.
     */
    fun isSubtitlesEnabled(context: Context): Boolean {
        // Defaulting to false (subtitles off), change if needed
        return getSharedPreferences(context).getBoolean(KEY_SUBTITLES_ENABLED, false)
    }

    /**
     * Sets the desired font size for subtitles. The interpretation of this value
     * (e.g., percentage, specific sp unit) depends on how it's used in the player.
     * @param context The application context.
     * @param fontSize An integer representing the font size (e.g., 100 for 100%).
     */
    fun setSubtitleFontSize(context: Context, fontSize: Int) {
        getSharedPreferences(context).edit().putInt(KEY_SUBTITLE_FONT_SIZE, fontSize).apply()
    }

    /**
     * Gets the preferred font size for subtitles.
     * @param context The application context.
     * @return An integer representing the font size. Defaults to 100.
     */
    fun getSubtitleFontSize(context: Context): Int {
        // Defaulting to 100 (e.g., 100%), adjust as needed
        return getSharedPreferences(context).getInt(KEY_SUBTITLE_FONT_SIZE, 100)
    }

    /**
     * Sets the desired position for subtitles. The interpretation of this value
     * (e.g., 0=Bottom, 1=Middle, 2=Top) depends on how it's used in the player.
     * @param context The application context.
     * @param position An integer representing the position.
     */
    fun setSubtitlePosition(context: Context, position: Int) {
        getSharedPreferences(context).edit().putInt(KEY_SUBTITLE_POSITION, position).apply()
    }

    /**
     * Gets the preferred position for subtitles.
     * @param context The application context.
     * @return An integer representing the position. Defaults to 0 (e.g., Bottom).
     */
    fun getSubtitlePosition(context: Context): Int {
        // Defaulting to 0 (e.g., Bottom), adjust based on your implementation
        // (e.g., 0=Bottom, 1=Middle, 2=Top)
        return getSharedPreferences(context).getInt(KEY_SUBTITLE_POSITION, 0)
    }

    /**
     * Sets whether a background should be rendered behind subtitles for better readability.
     * @param context The application context.
     * @param isEnabled True to enable the background, false to disable it.
     */
    fun setSubtitleBackground(context: Context, isEnabled: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_SUBTITLE_BACKGROUND_ENABLED, isEnabled).apply()
    }

    /**
     * Checks if the subtitle background is enabled.
     * @param context The application context.
     * @return True if the background is enabled, false otherwise. Defaults to true.
     */
    fun getSubtitleBackground(context: Context): Boolean {
        // Defaulting to true (background enabled for readability), change if needed
        return getSharedPreferences(context).getBoolean(KEY_SUBTITLE_BACKGROUND_ENABLED, true)
    }

    // --- End New Functions ---

}