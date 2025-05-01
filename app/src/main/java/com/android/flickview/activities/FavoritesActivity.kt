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
        setContentView(R.layout.favorites) // Ensure R.layout.favorites exists

        // Initialize Views
        buttonBack = findViewById(R.id.button_back) // Ensure this ID exists in favorites.xml
        recyclerView = findViewById(R.id.recycler_view_favorites) // Ensure this ID exists
        // **FIX 2: Uncomment the initialization of emptyStateTextView**
        emptyStateTextView = findViewById(R.id.empty_state_text) // Ensure this ID exists in favorites.xml
        bottomNavigationView = findViewById(R.id.bottomNavigationView3) // Ensure this ID exists

        // Verify that the TextView was found (optional debug check)
        if (emptyStateTextView == null) {
            Log.e(TAG, "Error: Could not find TextView with ID R.id.empty_state_text in layout R.layout.favorites")
            // Handle this error appropriately, maybe show a default message or crash gracefully.
            // For now, we'll let it potentially crash later if accessed, but the log helps debug.
        }


        // Back Button Setup
        buttonBack.setOnClickListener {
            Log.d(TAG, "Back button clicked.")
            finish() // Close this activity
            // Optional: Add transition animation
            // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // RecyclerView Setup
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Initialize adapter with the mutable list. Changes to favoritesList need notifyDataSetChanged().
        favoritesAdapter = FavoritesAdapter(favoritesList) // Assuming FavoritesAdapter constructor takes MutableList<FavoriteItem>
        recyclerView.adapter = favoritesAdapter
        Log.d(TAG, "RecyclerView and Adapter initialized.")

        // Bottom Navigation Setup
        setupBottomNavigation()

        // Initial load of favorites is handled in onResume, which is called after onCreate
    }

    // Load (or reload) favorites when the activity becomes visible or resumes
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called. Loading favorites.")
        loadFavorites()
    }

    private fun loadFavorites() {
        // Get the up-to-date list from the helper
        val updatedList = FavoritesHelper.getAllFavoriteItems(this)
        Log.d(TAG, "Loaded ${updatedList.size} favorites from FavoritesHelper.")

        // Update the adapter's data list *in place*
        favoritesList.clear() // Clear the existing list (which the adapter holds a reference to)
        favoritesList.addAll(updatedList) // Add all new items into the same list object
        favoritesAdapter.notifyDataSetChanged() // Tell the adapter the data has changed significantly

        // Show/Hide empty state message
        // This check should now work correctly as emptyStateTextView is initialized
        if (favoritesList.isEmpty()) {
            Log.d(TAG, "Favorites list is empty, showing empty state.")
            recyclerView.visibility = View.GONE
            emptyStateTextView.visibility = View.VISIBLE // Show the text view
        } else {
            Log.d(TAG, "Favorites list has items (${favoritesList.size}), showing RecyclerView.")
            recyclerView.visibility = View.VISIBLE // Show the list
            emptyStateTextView.visibility = View.GONE // Hide the text view
        }
    }

    private fun setupBottomNavigation() {
        // Ensure R.id.favorites exists in your menu resource (e.g., res/menu/bottom_nav_menu.xml)
        bottomNavigationView.selectedItemId = R.id.favorites // Highlight current page

        // Ensure ProfilePageActivity and LandingActivity exist in the correct package
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    Log.d(TAG, "BottomNav: Home selected.")
                    // Navigate back to LandingActivity cleanly
                    val intent = Intent(this, LandingActivity::class.java)
                    // Clear activity stack above LandingActivity and bring it to front
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Optional animation
                    true // Consume the event
                }
                R.id.profile -> {
                    Log.d(TAG, "BottomNav: Profile selected.")
                    startActivity(Intent(this, ProfilePageActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Optional animation
                    // Decide if you want to finish FavoritesActivity when going to Profile
                    // finish() // Uncomment if you want Favorites to close when going to Profile
                    true // Consume the event
                }
                R.id.favorites -> {
                    Log.d(TAG, "BottomNav: Favorites selected (already here).")
                    // No action needed, already on this screen
                    true // Consume the event
                }
                else -> {
                    Log.w(TAG, "BottomNav: Unknown item selected with ID: ${item.itemId}")
                    false // Do not consume the event for unknown items
                }
            }
        }
        Log.d(TAG, "Bottom Navigation setup complete.")
    }
}

// --- Add to res/layout/favorites.xml ---
// Make sure your favorites.xml layout file includes a TextView like this:
/*
<androidx.constraintlayout.widget.ConstraintLayout ...>

    <!-- Other views like Toolbar/TopBar -->
    <ImageButton
        android:id="@+id/button_back"
        ... />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler_view_favorites"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:layout_constraintTop_toBottomOf="@id/your_top_bar_id" <!-- Adjust constraint -->
        app:layout_constraintBottom_toTopOf="@id/bottomNavigationView3"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <TextView
        android:id="@+id/empty_state_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="No favorites added yet!"
        android:textSize="18sp"
        android:textColor="?android:attr/textColorSecondary" <!-- Or your desired color -->
        android:visibility="gone" <!-- Initially hidden, controlled by code -->
        app:layout_constraintTop_toTopOf="@id/recycler_view_favorites" <!-- Center within RecyclerView area -->
        app:layout_constraintBottom_toBottomOf="@id/recycler_view_favorites"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        tools:visibility="visible"/> <!-- Show in Layout Editor preview -->

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNavigationView3"
        ... />

</androidx.constraintlayout.widget.ConstraintLayout>
*/