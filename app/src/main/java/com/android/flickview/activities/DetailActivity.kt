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

    companion object {
        const val EXTRA_MOVIE_ID = "MOVIE_ID" // String identifier (IMDb or API ID)
        const val EXTRA_MOVIE_TITLE = "MOVIE_TITLE"
        const val EXTRA_MOVIE_POSTER_URL = "MOVIE_POSTER_URL"
        const val EXTRA_USER_NAME = "USER_NAME"
        const val EXTRA_USER_PROFILE_URL = "USER_PROFILE_URL"
        const val INTENT_EXTRA_API_ID = "id" // The key used by the calling activity
    }

    // --- Views ---
    private lateinit var mRequestQueue: RequestQueue
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTxt: TextView
    private lateinit var movieRateText: TextView
    private lateinit var movieTimeTxt: TextView
    private lateinit var movieSummaryInfo: TextView
    private lateinit var movieActorsInfo: TextView
    private lateinit var pic2: ImageView
    private lateinit var backImg: ImageView
    private lateinit var recyclerViewActors: RecyclerView
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var scrollView: NestedScrollView
    private lateinit var reviewsButton: MaterialButton
    private lateinit var favoritesButton: ImageView
    private lateinit var auth: FirebaseAuth
    private var apiFilmId: Int = 0
    private var currentFilmItem: FilmItem? = null
    private var movieIdentifierForAction: String? = null
    private var movieTitle: String = ""
    private var moviePosterUrl: String? = null

    private val TAG = "DetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moviedetail)

        // Initialize Firebase Auth
        auth = Firebase.auth
        // Get movie API ID from Intent (use the constant)
        apiFilmId = intent.getIntExtra(INTENT_EXTRA_API_ID, 0)
        if (apiFilmId == 0) {
            Toast.makeText(this, "Error: Invalid movie information.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Invalid or missing API movie ID received via Intent (key: '$INTENT_EXTRA_API_ID')")
            finish()
            return
        }
        Log.d(TAG, "Received API movie ID: $apiFilmId")

        initView()
        setupReviewButtonListener()
        sendRequest()
    }

    private fun sendRequest() {
        mRequestQueue = Volley.newRequestQueue(this)
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        favoritesButton.isEnabled = false
        updateFavoritesIcon(false)

        // Construct API URL using the received numeric ID
        val apiUrl = "https://moviesapi.ir/api/v1/movies/$apiFilmId"
        Log.d(TAG, "Requesting movie details from: $apiUrl")

        val stringRequest = StringRequest(
            Request.Method.GET,
            apiUrl,
            { response ->
                Log.d(TAG, "API Response Received.")
                progressBar.visibility = View.GONE
                try {
                    val gson = Gson()
                    val item: FilmItem = gson.fromJson(response, FilmItem::class.java)
                    currentFilmItem = item // Store the loaded item for later use (favorites)
                    Log.d(TAG, "Successfully parsed FilmItem: ${item.title}")

                    scrollView.visibility = View.VISIBLE // Show content area

                    movieIdentifierForAction = when {
                        !item.imdbId.isNullOrBlank() -> {
                            Log.d(TAG, "Using IMDb ID: ${item.imdbId}")
                            item.imdbId // Prefer IMDb ID if available
                        }
                        item.id != null -> {
                            val apiIdString = item.id.toString()
                            Log.d(TAG, "Using API numeric ID as string: $apiIdString")
                            apiIdString
                        }
                        else -> {
                            Log.e(TAG, "Critical Error: Both IMDb ID and API numeric ID are null/missing in response for API ID $apiFilmId!")
                            null
                        }
                    }

                    movieTitle = item.title ?: "Details"
                    moviePosterUrl = item.poster

                    if (movieIdentifierForAction != null) {
                        reviewsButton.isEnabled = true
                        favoritesButton.isEnabled = true // Enable favorites button now
                        // Check initial favorite status using the determined string identifier
                        val isFav = FavoritesHelper.isFavorite(this, movieIdentifierForAction!!)
                        Log.d(TAG, "Movie ID '$movieIdentifierForAction' is favorite: $isFav")
                        updateFavoritesIcon(isFav)
                    } else {
                        Log.e(TAG, "Could not determine a valid identifier. Actions (reviews/favorites) disabled.")
                        reviewsButton.isEnabled = false
                        favoritesButton.isEnabled = false
                        updateFavoritesIcon(false) // Keep icon as default/border
                        Toast.makeText(this, "Error processing movie data.", Toast.LENGTH_SHORT).show()
                    }

                    Glide.with(this@DetailActivity)
                        .load(moviePosterUrl)
                        .placeholder(R.drawable.placeholder_poster)
                        .error(R.drawable.placeholder_poster)
                        .into(pic2)

                    titleTxt.text = movieTitle
                    movieRateText.text = item.imdbRating ?: "N/A"
                    movieTimeTxt.text = item.runtime ?: "N/A"
                    movieSummaryInfo.text = item.plot ?: "No summary available."
                    movieActorsInfo.text = item.actors ?: "N/A"


                    item.images?.let { imageList ->
                        if (imageList.isNotEmpty()) {
                            recyclerViewActors.adapter = ActorsListAdapter(imageList)
                            Log.d(TAG, "Set ActorsListAdapter with ${imageList.size} items.")
                        } else {
                            Log.d(TAG, "Actors image list (item.images) is empty.")
                        }
                    } ?: Log.w(TAG, "Actors image list (item.images) is null.")

                    item.genres?.let { genreList ->
                        if (genreList.isNotEmpty()){
                            recyclerViewCategory.adapter = CategoryEachFilmListAdapter(genreList)
                            Log.d(TAG, "Set CategoryEachFilmListAdapter with ${genreList.size} items.")
                        } else {
                            Log.d(TAG, "Genres list (item.genres) is empty.")
                        }
                    } ?: Log.w(TAG, "Genres list (item.genres) is null.")

                } catch (e: Exception) {
                    // Error parsing JSON or other processing error
                    Log.e(TAG, "Error processing API response: ${e.message}", e)
                    Toast.makeText(this@DetailActivity, "Error loading movie details.", Toast.LENGTH_LONG).show()
                    scrollView.visibility = View.GONE
                    reviewsButton.isEnabled = false
                    favoritesButton.isEnabled = false
                    updateFavoritesIcon(false)
                }
            },
            { error ->
                // Volley network error
                progressBar.visibility = View.GONE
                Log.e(TAG, "Volley Network Error: ${error.toString()}")
                Toast.makeText(this@DetailActivity, "Network Error loading details.", Toast.LENGTH_LONG).show()
                scrollView.visibility = View.GONE
                reviewsButton.isEnabled = false
                favoritesButton.isEnabled = false
                updateFavoritesIcon(false)
            }
        )
        Log.d(TAG, "Adding request to Volley queue.")
        mRequestQueue.add(stringRequest)
    }

    private fun initView() {
        Log.d(TAG, "Initializing views.")
        titleTxt = findViewById(R.id.movieNameTxt)
        progressBar = findViewById(R.id.progressBarDetail)
        scrollView = findViewById(R.id.scrollView2)
        pic2 = findViewById(R.id.picDetail)
        movieRateText = findViewById(R.id.movieStar)
        movieTimeTxt = findViewById(R.id.movieTime)
        movieSummaryInfo = findViewById(R.id.movieSummary)
        movieActorsInfo = findViewById(R.id.movieActorInfo)
        backImg = findViewById(R.id.backImg)
        recyclerViewCategory = findViewById(R.id.genreView)
        recyclerViewActors = findViewById(R.id.actorsView)
        reviewsButton = findViewById(R.id.ReviewsButton)
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
            toggleFavoriteStatus()
        }

        reviewsButton.isEnabled = false // Initially disabled until data loads
        favoritesButton.isEnabled = false // Initially disabled until data loads and ID is confirmed
        updateFavoritesIcon(false) // Set initial favorites icon state (border)
        Log.d(TAG, "Views initialized, buttons initially disabled.")
    }

    // --- Favorite Toggle Logic ---
    private fun toggleFavoriteStatus() {
        val film = currentFilmItem
        val currentStringId = movieIdentifierForAction

        if (film == null || currentStringId == null) {
            Log.w(TAG, "Cannot toggle favorite: Movie details or identifier not available yet.")
            Toast.makeText(this, "Please wait for movie details to load.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check current favorite status using the helper and the STRING identifier
        val isCurrentlyFavorite = FavoritesHelper.isFavorite(this, currentStringId)
        Log.d(TAG, "Toggling favorite status for ID '$currentStringId'. Currently favorite: $isCurrentlyFavorite")

        if (isCurrentlyFavorite) {
            Log.d(TAG, "Removing '$currentStringId' from favorites.")
            val removed = FavoritesHelper.removeFavoriteItem(this, currentStringId)
            if (removed) {
                Toast.makeText(this, "'${film.title ?: "Movie"}' removed from Favorites", Toast.LENGTH_SHORT).show()
                updateFavoritesIcon(false)
            } else {
                Log.e(TAG, "Failed to remove favorite '$currentStringId' using helper.")
                Toast.makeText(this, "Error removing favorite.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // --- Add to Favorites ---
            Log.d(TAG, "Adding '$currentStringId' to favorites.")
            val favoriteToAdd = FavoriteItem(
                film.title ?: "Unknown Title",
                film.poster,
                film.id ?: -1
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
            updateFavoritesIcon(true)
        }
    }

    private fun updateFavoritesIcon(isFavorite: Boolean) {
        Log.d(TAG, "Updating favorites icon. Is favorite: $isFavorite")
        if (isFavorite) {
            favoritesButton.setImageResource(R.drawable.baseline_favorite_25)
        } else {
            favoritesButton.setImageResource(R.drawable.baseline_favorite_border_24)

        }
    }

    private fun setupReviewButtonListener() {
        reviewsButton.setOnClickListener {
            Log.d(TAG, "Reviews button clicked.")
            if (movieIdentifierForAction == null) {
                Log.w(TAG, "Cannot open reviews: movieIdentifierForAction is null.")
                Toast.makeText(this, "Cannot open reviews: Movie data not fully loaded.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser
            val currentUserName = currentUser?.displayName
            val currentUserProfileUrl = currentUser?.photoUrl?.toString()

            val intent = Intent(this, ReviewActivity::class.java).apply {
                putExtra(EXTRA_MOVIE_ID, movieIdentifierForAction)
                putExtra(EXTRA_MOVIE_TITLE, movieTitle)
                putExtra(EXTRA_MOVIE_POSTER_URL, moviePosterUrl)


                currentUserName?.let { putExtra(EXTRA_USER_NAME, it) }
                currentUserProfileUrl?.let { putExtra(EXTRA_USER_PROFILE_URL, it) }
            }
            Log.d(TAG, "Starting ReviewActivity for movie ID: $movieIdentifierForAction, Title: $movieTitle")
            startActivity(intent)
        }
    }
}
