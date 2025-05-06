package com.android.flickview.activities

import android.content.Context
import android.content.SharedPreferences
import com.android.flickview.Domains.FavoriteItem
import com.google.gson.Gson

object FavoritesHelper {

    private const val PREFS_NAME = "FavoritePrefs"
    private const val FAVORITE_IDS_KEY = "favorite_ids" // Key for the Set of IDs
    private const val FAVORITE_ITEM_PREFIX = "fav_item_" // Prefix for storing individual item JSON

    private val GSON = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getFavoriteItemKey(stringId: String): String {
        return FAVORITE_ITEM_PREFIX + stringId
    }

    // --- THIS IS THE FUNCTION TO MODIFY ---
    /**
     * Saves a FavoriteItem's JSON data using the stringId as part of the key,
     * and adds the stringId to the set of favorite IDs.
     *
     * @param context Context
     * @param item The FavoriteItem object containing movie details (like numeric ID, title, poster).
     * @param stringId The unique String identifier (IMDb ID or API ID as String) used for lookup and key generation.
     */
    fun saveFavoriteItem(context: Context, item: FavoriteItem, stringId: String) { // <-- Add stringId: String parameter
        val prefs = getPrefs(context)
        val editor = prefs.edit()

        val json = GSON.toJson(item)
        val itemKey = getFavoriteItemKey(stringId)
        editor.putString(itemKey, json)
        // Log.d("FavoritesHelper", "Saving item JSON with key: $itemKey") // Optional logging

        val currentIds = getFavoriteIds(context) // Get mutable copy
        currentIds.add(stringId)
        editor.putStringSet(FAVORITE_IDS_KEY, currentIds)
        // Log.d("FavoritesHelper", "Added ID '$stringId' to favorites set.") // Optional logging
        editor.apply()
    }

    /**
     * Removes a favorite item based on its String identifier.
     * Removes both the item's JSON data and its ID from the tracking set.
     *
     * @param context Context
     * @param stringId The String identifier of the item to remove.
     * @return true if the item ID was found and removed from the set, false otherwise.
     */
    fun removeFavoriteItem(context: Context, stringId: String): Boolean {
        val prefs = getPrefs(context)
        val editor = prefs.edit()

        // 1. Remove the item's JSON data
        val itemKey = getFavoriteItemKey(stringId)
        editor.remove(itemKey)
        // Log.d("FavoritesHelper", "Removing item JSON with key: $itemKey") // Optional logging

        // 2. Remove the stringId from the Set of favorite IDs
        val currentIds = getFavoriteIds(context) // Get mutable copy
        val removed = currentIds.remove(stringId) // remove returns true if element was present
        if (removed) {
            editor.putStringSet(FAVORITE_IDS_KEY, currentIds)
            // Log.d("FavoritesHelper", "Removed ID '$stringId' from favorites set.") // Optional logging
        } else {
            // Log.w("FavoritesHelper", "Attempted to remove ID '$stringId', but it was not in the set.") // Optional logging
        }

        // 3. Apply changes
        editor.apply()
        return removed // Return whether the ID was successfully removed from the set
    }

    /**
     * Checks if an item is favorited based on its String identifier.
     *
     * @param context Context
     * @param stringId The String identifier to check.
     * @return true if the stringId exists in the set of favorite IDs, false otherwise.
     */
    fun isFavorite(context: Context, stringId: String): Boolean {
        val currentIds = getFavoriteIds(context) // Use the non-mutable version is fine here
        val isFav = currentIds.contains(stringId)
        // Log.d("FavoritesHelper", "Checking favorite status for ID '$stringId': $isFav") // Optional logging
        return isFav
    }

    /**
     * Retrieves the Set of favorite String identifiers.
     * Returns a mutable copy to allow modification before saving back.
     *
     * @param context Context
     * @return A MutableSet<String> containing all favorite IDs. Returns an empty set if none exist.
     */
    private fun getFavoriteIds(context: Context): MutableSet<String> {
        val prefs = getPrefs(context)
        // Return a *copy* of the set so modifications don't affect the stored version until explicitly saved
        return prefs.getStringSet(FAVORITE_IDS_KEY, HashSet())?.toMutableSet() ?: mutableSetOf()
    }


    /**
     * Retrieves all saved FavoriteItem objects.
     * It iterates through the stored favorite IDs and retrieves the corresponding JSON data.
     *
     * @param context Context
     * @return A List<FavoriteItem> containing all saved favorite items.
     */
    fun getAllFavoriteItems(context: Context): List<FavoriteItem> {
        val prefs = getPrefs(context)
        val favoriteIds = getFavoriteIds(context) // Get the set of IDs
        val favoriteItems = mutableListOf<FavoriteItem>()

        favoriteIds.forEach { stringId ->
            val itemKey = getFavoriteItemKey(stringId)
            val json = prefs.getString(itemKey, null)
            if (json != null) {
                try {
                    val item = GSON.fromJson(json, FavoriteItem::class.java)
                    favoriteItems.add(item)
                } catch (e: Exception) {
                    // Log error if JSON is corrupted for a specific ID
                    // Log.e("FavoritesHelper", "Error parsing FavoriteItem JSON for key $itemKey: ${e.message}")
                    // Optionally remove the corrupted entry here
                    // removeFavoriteItem(context, stringId)
                }
            } else {
                // Log warning if an ID exists in the set but its JSON is missing
                // Log.w("FavoritesHelper", "Favorite ID '$stringId' found in set, but its JSON data (key $itemKey) is missing.")
                // Optionally remove the orphaned ID here
                // removeFavoriteItem(context, stringId) // Be careful with concurrent modification if iterating directly
            }
        }
        // Log.d("FavoritesHelper", "Retrieved ${favoriteItems.size} favorite items.") // Optional logging
        return favoriteItems
    }

    fun getFavoriteCount(context: Context): Int {
        return getAllFavoriteItems(context).size
    }
}