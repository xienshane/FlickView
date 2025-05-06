// **FIX 1: Correct the package name**
package com.android.flickview.activities // Changed from com.android.flickview.Adapters

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView // Import TextView for empty state
import androidx.appcompat.app.AppCompatActivity // Use AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.flickview.Domains.FavoriteItem
import com.android.flickview.R
// Import the specific adapter and helper from their correct packages
import com.android.flickview.Adapters.FavoritesAdapter // Assuming adapter is in Adapters package
import com.android.flickview.activities.FavoritesHelper // Assuming helper is in activities package (or adjust if elsewhere)
// Import other activities used in navigation
import com.android.flickview.activities.LandingActivity
import com.android.flickview.activities.ProfilePageActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter

    // This list holds the data displayed by the adapter. Modifying it requires notifying the adapter.
    private var favoritesList: MutableList<FavoriteItem> = mutableListOf()
    private lateinit var emptyStateTextView: TextView // To show when list is empty
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var buttonBack: ImageButton

    private val TAG = "FavoritesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called.")
        setContentView(R.layout.favorites)

        // Initialize Views
        buttonBack = findViewById(R.id.button_back)
        recyclerView = findViewById(R.id.recycler_view_favorites)
        emptyStateTextView =
            findViewById(R.id.empty_state_text)
        bottomNavigationView = findViewById(R.id.bottomNavigationView3)

        if (emptyStateTextView == null) {
            Log.e(
                TAG,
                "Error: Could not find TextView with ID R.id.empty_state_text in layout R.layout.favorites"
            )
        }


        // Back Button Setup
        buttonBack.setOnClickListener {
            Log.d(TAG, "Back button clicked.")
            finish() // Close this activity
        }

        // RecyclerView Setup
        recyclerView.layoutManager = LinearLayoutManager(this)
        favoritesAdapter =
            FavoritesAdapter(favoritesList)
        recyclerView.adapter = favoritesAdapter
        Log.d(TAG, "RecyclerView and Adapter initialized.")

        setupBottomNavigation()

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called. Loading favorites.")
        loadFavorites()
    }

    private fun loadFavorites() {
        val updatedList = FavoritesHelper.getAllFavoriteItems(this)
        Log.d(TAG, "Loaded ${updatedList.size} favorites from FavoritesHelper.")

        favoritesList.clear()
        favoritesList.addAll(updatedList)
        favoritesAdapter.notifyDataSetChanged()

        if (favoritesList.isEmpty()) {
            Log.d(TAG, "Favorites list is empty, showing empty state.")
            recyclerView.visibility = View.GONE
            emptyStateTextView.visibility = View.VISIBLE
        } else {
            Log.d(TAG, "Favorites list has items (${favoritesList.size}), showing RecyclerView.")
            recyclerView.visibility = View.VISIBLE // Show the list
            emptyStateTextView.visibility = View.GONE
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.favorites

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.favorites -> {
                    true
                }
                R.id.home -> {
                    startActivity(Intent(this, LandingActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.profile -> {
                    // Navigate to ProfilePageActivity
                    startActivity(Intent(this, ProfilePageActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }



                else -> {
                    false
                }
            }
        }
        Log.d(TAG, "Bottom Navigation setup complete.")
    }


    private fun startActivityWithReorder(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
    }
}
