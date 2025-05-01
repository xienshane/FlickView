package com.android.flickview.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.flickview.Adapters.ActorsListAdapter
import com.android.flickview.Adapters.CategoryEachFilmListAdapter
import com.android.flickview.Domains.FavoriteItem // Assuming FavoriteItem is correctly defined (Java/Kotlin)
import com.android.flickview.Domains.FilmItem // Assuming FilmItem structure matches API
import com.android.flickview.R
// Import your FavoritesHelper class
import com.android.flickview.activities.FavoritesHelper // Make sure this path is correct
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DetailActivity : AppCompatActivity() {

    // --- Constants for Intent Extras (Good practice) ---
    companion object {
        const val EXTRA_MOVIE_ID = "MOVIE_ID" // String identifier (IMDb or API ID)
        const val EXTRA_MOVIE_TITLE = "MOVIE_TITLE"
        const val EXTRA_MOVIE_POSTER_URL = "MOVIE_POSTER_URL"
        // Optional user details if ReviewActivity needs them initially
        const val EXTRA_USER_NAME = "USER_NAME"
        const val EXTRA_USER_PROFILE_URL = "USER_PROFILE_URL"
        // Constant for the incoming intent extra key
        const val INTENT_EXTRA_API_ID = "id" // The key used by the calling activity
    }

    // --- Views ---
    private lateinit var mRequestQueue: RequestQueue
    // No need for mStringRequest as a member variable if only used locally
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTxt: TextView
    private lateinit var movieRateText: TextView
    private lateinit var movieTimeTxt: TextView
    private lateinit var movieSummaryInfo: TextView
    private lateinit var movieActorsInfo: TextView
    private lateinit var pic2: ImageView // Movie Detail Poster
    private lateinit var backImg: ImageView
    private lateinit var recyclerViewActors: RecyclerView
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var scrollView: NestedScrollView
    private lateinit var reviewsButton: MaterialButton
    private lateinit var favoritesButton: ImageView // The heart button (imageView2)

    // --- Firebase ---
    private lateinit var auth: FirebaseAuth

    // --- Movie Data ---
    private var apiFilmId: Int = 0 // The numeric ID received from Intent
    private var currentFilmItem: FilmItem? = null // Store the whole loaded item
    private var movieIdentifierForAction: String? = null // The consistent STRING ID (IMDb or API ID) for reviews AND favorites
    private var movieTitle: String = ""
    private var moviePosterUrl: String? = null

    private val TAG = "DetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure your layout file is named moviedetail.xml and contains all the necessary views
        // including an ImageView with android:id="@+id/imageView2" for the favorites button
        setContentView(R.layout.moviedetail)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Get movie API ID from Intent (use the constant)
        apiFilmId = intent.getIntExtra(INTENT_EXTRA_API_ID, 0)
        if (apiFilmId == 0) {
            Toast.makeText(this, "Error: Invalid movie information.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Invalid or missing API movie ID received via Intent (key: '$INTENT_EXTRA_API_ID')")
            finish() // Close activity if ID is invalid
            return
        }
        Log.d(TAG, "Received API movie ID: $apiFilmId")

        initView() // Initialize all views, including the favorites button and its listener
        setupReviewButtonListener() // Setup listener for the reviews button
        sendRequest() // Fetch movie data from API
    }

    private fun sendRequest() {
        mRequestQueue = Volley.newRequestQueue(this)
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        reviewsButton.isEnabled = false // Keep disabled until data is loaded
        favoritesButton.isEnabled = false // Also disable favorites button initially
        updateFavoritesIcon(false) // Set to default (border)

        // Construct API URL using the received numeric ID
        val apiUrl = "https://moviesapi.ir/api/v1/movies/$apiFilmId"
        Log.d(TAG, "Requesting movie details from: $apiUrl")

        val stringRequest = StringRequest(
            Request.Method.GET,
            apiUrl,
            { response ->
                Log.d(TAG, "API Response Received.")
                progressBar.visibility = View.GONE // Hide progress bar on success or failure to parse
                try {
                    val gson = Gson()
                    // Make sure FilmItem class structure matches the JSON response
                    val item: FilmItem = gson.fromJson(response, FilmItem::class.java)
                    currentFilmItem = item // Store the loaded item for later use (favorites)
                    Log.d(TAG, "Successfully parsed FilmItem: ${item.title}")

                    scrollView.visibility = View.VISIBLE // Show content area

                    // --- Determine the best unique STRING identifier ---
                    // This ID will be used for both reviews and favorites storage
                    movieIdentifierForAction = when {
                        !item.imdbId.isNullOrBlank() -> {
                            Log.d(TAG, "Using IMDb ID: ${item.imdbId}")
                            item.imdbId // Prefer IMDb ID if available
                        }
                        item.id != null -> {
                            // Use the API's numeric ID *as a string* if imdbId is missing
                            val apiIdString = item.id.toString()
                            Log.d(TAG, "Using API numeric ID as string: $apiIdString")
                            apiIdString
                        }
                        else -> {
                            // Fallback: If somehow both are missing (shouldn't happen if API ID was used for request)
                            Log.e(TAG, "Critical Error: Both IMDb ID and API numeric ID are null/missing in response for API ID $apiFilmId!")
                            null // Indicate failure to get an identifier
                        }
                    }

                    // Store other necessary details locally
                    movieTitle = item.title ?: "Details" // Use "Details" as fallback title
                    moviePosterUrl = item.poster // Store poster URL

                    // Enable buttons only if we have a valid identifier
                    if (movieIdentifierForAction != null) {
                        reviewsButton.isEnabled = true
                        favoritesButton.isEnabled = true // Enable favorites button now
                        // Check initial favorite status using the determined string identifier
                        val isFav = FavoritesHelper.isFavorite(this, movieIdentifierForAction!!)
                        Log.d(TAG, "Movie ID '$movieIdentifierForAction' is favorite: $isFav")
                        updateFavoritesIcon(isFav)
                    } else {
                        // If we couldn't get an ID, keep buttons disabled and log error
                        Log.e(TAG, "Could not determine a valid identifier. Actions (reviews/favorites) disabled.")
                        reviewsButton.isEnabled = false
                        favoritesButton.isEnabled = false
                        updateFavoritesIcon(false) // Keep icon as default/border
                        Toast.makeText(this, "Error processing movie data.", Toast.LENGTH_SHORT).show()
                    }

                    // --- Populate UI Views ---
                    Glide.with(this@DetailActivity)
                        .load(moviePosterUrl)
                        // Add placeholder/error drawables to res/drawable
                        .placeholder(R.drawable.placeholder_poster) // e.g., placeholder_poster.xml
                        .error(R.drawable.placeholder_poster)       // e.g., placeholder_poster.xml
                        .into(pic2)

                    titleTxt.text = movieTitle
                    movieRateText.text = item.imdbRating ?: "N/A"
                    movieTimeTxt.text = item.runtime ?: "N/A"
                    movieSummaryInfo.text = item.plot ?: "No summary available."
                    movieActorsInfo.text = item.actors ?: "N/A"

                    // --- Setup RecyclerView Adapters ---
                    // Use ?.let for safe handling of potentially null lists
                    item.images?.let { imageList ->
                        if (imageList.isNotEmpty()) {
                            recyclerViewActors.adapter = ActorsListAdapter(imageList)
                            Log.d(TAG, "Set ActorsListAdapter with ${imageList.size} items.")
                        } else {
                            Log.d(TAG, "Actors image list (item.images) is empty.")
                            // Optionally hide the RecyclerView if empty: recyclerViewActors.visibility = View.GONE
                        }
                    } ?: Log.w(TAG, "Actors image list (item.images) is null.")

                    item.genres?.let { genreList ->
                        if (genreList.isNotEmpty()){
                            recyclerViewCategory.adapter = CategoryEachFilmListAdapter(genreList)
                            Log.d(TAG, "Set CategoryEachFilmListAdapter with ${genreList.size} items.")
                        } else {
                            Log.d(TAG, "Genres list (item.genres) is empty.")
                            // Optionally hide the RecyclerView if empty: recyclerViewCategory.visibility = View.GONE
                        }
                    } ?: Log.w(TAG, "Genres list (item.genres) is null.")

                } catch (e: Exception) {
                    // Error parsing JSON or other processing error
                    Log.e(TAG, "Error processing API response: ${e.message}", e)
                    Toast.makeText(this@DetailActivity, "Error loading movie details.", Toast.LENGTH_LONG).show()
                    scrollView.visibility = View.GONE // Hide content area on error
                    // Ensure buttons are disabled and fav icon is default on error
                    reviewsButton.isEnabled = false
                    favoritesButton.isEnabled = false
                    updateFavoritesIcon(false)
                }
            },
            { error ->
                // Volley network error
                progressBar.visibility = View.GONE // Hide progress bar
                Log.e(TAG, "Volley Network Error: ${error.toString()}")
                Toast.makeText(this@DetailActivity, "Network Error loading details.", Toast.LENGTH_LONG).show()
                scrollView.visibility = View.GONE // Hide content area on error
                // Ensure buttons are disabled and fav icon is default on error
                reviewsButton.isEnabled = false
                favoritesButton.isEnabled = false
                updateFavoritesIcon(false)
            }
        )
        Log.d(TAG, "Adding request to Volley queue.")
        mRequestQueue.add(stringRequest) // Add the request to the queue
    }

    private fun initView() {
        Log.d(TAG, "Initializing views.")
        titleTxt = findViewById(R.id.movieNameTxt)
        progressBar = findViewById(R.id.progressBarDetail)
        scrollView = findViewById(R.id.scrollView2)
        pic2 = findViewById(R.id.picDetail) // Poster inside detail view
        movieRateText = findViewById(R.id.movieStar)
        movieTimeTxt = findViewById(R.id.movieTime)
        movieSummaryInfo = findViewById(R.id.movieSummary)
        movieActorsInfo = findViewById(R.id.movieActorInfo)
        backImg = findViewById(R.id.backImg)
        recyclerViewCategory = findViewById(R.id.genreView)
        recyclerViewActors = findViewById(R.id.actorsView)
        reviewsButton = findViewById(R.id.ReviewsButton) // Ensure ID is correct in moviedetail.xml

        // --- Initialize the Favorites Button (imageView2) ---
        // Make sure you have an ImageView with this ID in moviedetail.xml
        favoritesButton = findViewById(R.id.imageView2)

        // Set Layout Managers for RecyclerViews
        recyclerViewActors.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Set Click Listener for Back Button
        backImg.setOnClickListener {
            Log.d(TAG, "Back button clicked.")
            finish() // Close the current activity
        }

        // --- Set Click Listener for the Favorites Button (TOGGLE) ---
        favoritesButton.setOnClickListener {
            Log.d(TAG, "Favorites button clicked.")
            // toggleFavoriteStatus() relies on data being loaded, handled there
            toggleFavoriteStatus()
        }
        // --- End of Favorites Button Setup ---

        // --- Initial State ---
        reviewsButton.isEnabled = false // Initially disabled until data loads
        favoritesButton.isEnabled = false // Initially disabled until data loads and ID is confirmed
        updateFavoritesIcon(false) // Set initial favorites icon state (border)
        Log.d(TAG, "Views initialized, buttons initially disabled.")
    }

    // --- Favorite Toggle Logic ---
    private fun toggleFavoriteStatus() {
        // Ensure we have the necessary data loaded (FilmItem and the string identifier)
        val film = currentFilmItem
        val currentStringId = movieIdentifierForAction // Use the consistent STRING identifier

        if (film == null || currentStringId == null) {
            Log.w(TAG, "Cannot toggle favorite: Movie details or identifier not available yet.")
            Toast.makeText(this, "Please wait for movie details to load.", Toast.LENGTH_SHORT).show()
            return // Do nothing if data isn't ready
        }

        // Check current favorite status using the helper and the STRING identifier
        val isCurrentlyFavorite = FavoritesHelper.isFavorite(this, currentStringId)
        Log.d(TAG, "Toggling favorite status for ID '$currentStringId'. Currently favorite: $isCurrentlyFavorite")

        if (isCurrentlyFavorite) {
            // --- Remove from Favorites ---
            Log.d(TAG, "Removing '$currentStringId' from favorites.")
            // Use the STRING identifier to remove from FavoritesHelper
            val removed = FavoritesHelper.removeFavoriteItem(this, currentStringId)
            if (removed) {
                Toast.makeText(this, "'${film.title ?: "Movie"}' removed from Favorites", Toast.LENGTH_SHORT).show()
                updateFavoritesIcon(false) // Update icon to bordered heart
            } else {
                Log.e(TAG, "Failed to remove favorite '$currentStringId' using helper.")
                Toast.makeText(this, "Error removing favorite.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // --- Add to Favorites ---
            Log.d(TAG, "Adding '$currentStringId' to favorites.")
            // Create a FavoriteItem from the loaded FilmItem data.
            // Ensure FavoriteItem constructor matches the Java definition (title, posterUrl, id).
            // Use the numeric ID from the FilmItem for the FavoriteItem object itself.
            val favoriteToAdd = FavoriteItem(
                film.title ?: "Unknown Title", // Provide default title if null
                film.poster, // Assuming poster URL is available
                film.id ?: -1 // Use the numeric API ID; use -1 or handle error if null
            )

            // Check if the numeric ID is valid before saving
            if (favoriteToAdd.id == -1) {
                Log.e(TAG, "Cannot add favorite: numeric ID is missing in FilmItem.")
                Toast.makeText(this, "Error saving favorite: missing data.", Toast.LENGTH_SHORT).show()
                return
            }

            // Save using FavoritesHelper, passing the FavoriteItem object AND the STRING identifier
            FavoritesHelper.saveFavoriteItem(this, favoriteToAdd, currentStringId)
            Toast.makeText(this, "'${film.title ?: "Movie"}' added to Favorites", Toast.LENGTH_SHORT).show()
            updateFavoritesIcon(true) // Update icon to filled heart
        }
    }

    // Helper function to update the favorites icon drawable
    private fun updateFavoritesIcon(isFavorite: Boolean) {
        Log.d(TAG, "Updating favorites icon. Is favorite: $isFavorite")
        // Make sure you have these drawables in your res/drawable folder:
        // - baseline_favorite_24.xml (filled heart)
        // - baseline_favorite_border_24.xml (empty heart border)
        // You can get standard Material Icons from Android Studio's Vector Asset tool.
        if (isFavorite) {
            favoritesButton.setImageResource(R.drawable.baseline_favorite_border_24) // Filled heart
            // Add content description for accessibility
            //favoritesButton.contentDescription = getString(R.string.baseline_favorite_border_24)
        } else {
            favoritesButton.setImageResource(R.drawable.baseline_favorite_border_24) // Bordered heart
            // Add content description for accessibility
            //favoritesButton.contentDescription = getString(R.string.add_to_favorites)
        }
    }
    // --- End Favorite Logic ---

    // --- Review Button Logic ---
    private fun setupReviewButtonListener() {
        reviewsButton.setOnClickListener {
            Log.d(TAG, "Reviews button clicked.")
            // Check if we have the necessary STRING identifier
            if (movieIdentifierForAction == null) {
                Log.w(TAG, "Cannot open reviews: movieIdentifierForAction is null.")
                Toast.makeText(this, "Cannot open reviews: Movie data not fully loaded.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get current user details (optional - ReviewActivity might fetch fresh data)
            val currentUser = auth.currentUser
            val currentUserName = currentUser?.displayName // Can be null
            val currentUserProfileUrl = currentUser?.photoUrl?.toString() // Can be null

            // Create Intent for ReviewActivity
            val intent = Intent(this, ReviewActivity::class.java).apply {
                // Add essential movie data as extras using constants
                // Pass the STRING identifier (IMDb or API ID)
                putExtra(EXTRA_MOVIE_ID, movieIdentifierForAction)
                putExtra(EXTRA_MOVIE_TITLE, movieTitle) // Pass the title
                putExtra(EXTRA_MOVIE_POSTER_URL, moviePosterUrl) // Pass poster URL

                // Add optional user data if needed for display in ReviewActivity
                // Only add if not null, or handle null in ReviewActivity
                currentUserName?.let { putExtra(EXTRA_USER_NAME, it) }
                currentUserProfileUrl?.let { putExtra(EXTRA_USER_PROFILE_URL, it) }
            }
            Log.d(TAG, "Starting ReviewActivity for movie ID: $movieIdentifierForAction, Title: $movieTitle")
            startActivity(intent)
        }
    }
    // --- End Review Button Logic ---
}

// --- Add these strings to res/values/strings.xml ---
/*
<resources>
    <string name="app_name">FlickView</string>
    <string name="add_to_favorites">Add to Favorites</string>
    <string name="remove_from_favorites">Remove from Favorites</string>
    <string name="go_to_favorites">Go to Favorites</string>
    <string name="back">Back</string>
    <string name="favorites">Favorites</string>
    <!-- Add other strings used in your app -->
</resources>
*/

// --- Add placeholder drawable res/drawable/placeholder_poster.xml (example vector) ---
/*
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="100dp"
    android:height="150dp"
    android:viewportWidth="100"
    android:viewportHeight="150">
  <path
      android:fillColor="#E0E0E0"
      android:pathData="M0,0h100v150h-100z"/>
  <path
      android:fillColor="#BDBDBD"
      android:pathData="M50,30m-15,0a15,15 0,1 1,30 0a15,15 0,1 1,-30 0"
      android:strokeWidth="1"/>
  <path
      android:fillColor="#BDBDBD"
      android:pathData="M30,100 l20,-30 20,30z"
      android:strokeWidth="1"/>
</vector>
*/

// --- Ensure FavoritesHelper is correctly implemented ---
// It needs methods like:
// - fun isFavorite(context: Context, stringId: String): Boolean
// - fun saveFavoriteItem(context: Context, item: FavoriteItem, stringId: String)
// - fun removeFavoriteItem(context: Context, stringId: String): Boolean
// - fun getAllFavoriteItems(context: Context): List<FavoriteItem>
// It likely uses SharedPreferences to store a Set<String> of favorite stringIds
// and also stores the JSON representation of each FavoriteItem using a key like "fav_item_{stringId}".