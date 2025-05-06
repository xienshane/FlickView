package com.android.flickview.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.flickview.R
import com.android.flickview.Adapters.ReviewAdapter // You'll create this adapter
import com.android.flickview.data.Review
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReviewActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var userRatingBar: RatingBar
    private lateinit var reviewEditText: EditText
    private lateinit var submitReviewButton: Button
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var reviewsProgressBar: ProgressBar
    private lateinit var noReviewsText: TextView
    private lateinit var movieTitleReview: TextView
    private lateinit var moviePosterReview: ImageView
    private lateinit var reviewAdapter: ReviewAdapter
    private var currentMovieId: String? = null
    private var movieTitle: String? = null
    private var moviePosterUrl: String? = null
    private val reviewsList = mutableListOf<Review>()

    private val TAG = "ReviewActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reviews_page)

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        db = Firebase.firestore

        // Get Movie ID passed from the previous Activity
        currentMovieId = intent.getStringExtra("MOVIE_ID") // Make sure you pass this!
        movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: "Movie Reviews"
        moviePosterUrl = intent.getStringExtra("MOVIE_POSTER_URL")

        // --- View Binding ---
        userRatingBar = findViewById(R.id.userRatingBar)
        reviewEditText = findViewById(R.id.reviewEditText)
        submitReviewButton = findViewById(R.id.submitReviewButton)
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView)
        reviewsProgressBar = findViewById(R.id.reviewsProgressBar)
        noReviewsText = findViewById(R.id.noReviewsText)
        movieTitleReview = findViewById(R.id.movieTitleReview)
        val toolbar: Toolbar = findViewById(R.id.reviewToolbar)
        moviePosterReview = findViewById(R.id.moviePosterReview)

        // Setup Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = movieTitle // Set Toolbar title
        toolbar.setNavigationOnClickListener { finish() } // Handle back press

        // Basic check if Movie ID is present
        if (currentMovieId == null) {
            Toast.makeText(this, "Error: Movie ID missing.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Movie ID was null. Cannot load/submit reviews.")
            finish() // Close activity if no movie ID
            return
        }

        // Display Movie Title (you'd fetch this properly based on ID)
        val movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: "Movie Reviews"
        supportActionBar?.title = movieTitle
        movieTitleReview.text = movieTitle // Also set it in the layout body
        Glide.with(this)
            .load(moviePosterUrl)
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.placeholder_poster)
            .into(moviePosterReview)
        setupRecyclerView()
        setupSubmitButton()

        loadReviews()
    }

    // Function to configure the RecyclerView
    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter(reviewsList) // Initialize adapter with empty list initially
        reviewsRecyclerView.layoutManager = LinearLayoutManager(this)
        reviewsRecyclerView.adapter = reviewAdapter
    }

    // Function to setup the submit button listener
    private fun setupSubmitButton() {
        submitReviewButton.setOnClickListener {
            submitReview()
        }
    }

    // Inside ReviewActivity class
    private fun submitReview() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to submit a review.", Toast.LENGTH_SHORT).show()
            // Optionally, redirect to LoginActivity
            // startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        val rating = userRatingBar.rating
        val reviewText = reviewEditText.text.toString().trim()

        if (rating == 0.0f) {
            Toast.makeText(this, "Please select a rating.", Toast.LENGTH_SHORT).show()
            return
        }

        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Please write your review.", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentMovieId == null) {
            Toast.makeText(this, "Error: Cannot submit review without Movie ID.", Toast.LENGTH_LONG).show()
            return
        }

        submitReviewButton.isEnabled = false // Disable button while submitting
        Toast.makeText(this, "Submitting review...", Toast.LENGTH_SHORT).show()

        val userNameToStore: String = if (!currentUser.displayName.isNullOrBlank()) {
            // Use displayName if it's not null AND not just whitespace
            currentUser.displayName!!
        } else if (!currentUser.email.isNullOrBlank()) {
            // Fallback to email if it's not null or blank
            currentUser.email!!
        } else {
            "Anonymous"
        }

        // Create a Review object
        val review = Review(
            movieId = currentMovieId!!,
            userId = currentUser.uid,
            userName = userNameToStore,
            rating = rating,
            reviewText = reviewText
        )

        // Add the review to the 'reviews' collection in Firestore
        db.collection("reviews")
            .add(review) // Firestore generates a unique document ID
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Review added successfully with ID: ${documentReference.id}")
                Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show()
                // Clear input fields after successful submission
                userRatingBar.rating = 0f
                reviewEditText.text.clear()
                // Refresh the list to show the new review immediately
                loadReviews() // Or modify list locally for instant feedback
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding review", e)
                Toast.makeText(this, "Error submitting review: ${e.message}", Toast.LENGTH_LONG).show()
            }
            .addOnCompleteListener {
                submitReviewButton.isEnabled = true // Re-enable button regardless of outcome
            }
    }

    // Inside ReviewActivity class
    private fun loadReviews() {
        if (currentMovieId == null) {
            Log.e(TAG, "Cannot load reviews, currentMovieId is null.")
            return // Should have been caught in onCreate, but double-check
        }

        reviewsProgressBar.visibility = View.VISIBLE
        reviewsRecyclerView.visibility = View.GONE
        noReviewsText.visibility = View.GONE

        db.collection("reviews")
            .whereEqualTo("movieId", currentMovieId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                reviewsProgressBar.visibility = View.GONE
                reviewsList.clear()

                if (snapshots == null || snapshots.isEmpty) {
                    noReviewsText.visibility = View.VISIBLE
                    reviewsRecyclerView.visibility = View.GONE
                } else {
                    noReviewsText.visibility = View.GONE
                    reviewsRecyclerView.visibility = View.VISIBLE
                    for (doc in snapshots.documents) {
                         val review = doc.toObject(Review::class.java)?.apply { id = doc.id }
                         if (review != null) reviewsList.add(review)
                    }
                    reviewAdapter.notifyDataSetChanged()
                }
            }

    }
}
