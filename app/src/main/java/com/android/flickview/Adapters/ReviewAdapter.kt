// Create a new file, e.g., adapters/ReviewAdapter.kt
package com.android.flickview.Adapters // Or your package

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.flickview.R
import com.android.flickview.data.Review
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    // Formatter for the timestamp
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    // ViewHolder class to hold references to the views in item_review.xml
    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviewerName: TextView = itemView.findViewById(R.id.reviewerNameTextView)
        val ratingBar: RatingBar = itemView.findViewById(R.id.reviewRatingBarDisplay)
        val reviewText: TextView = itemView.findViewById(R.id.reviewTextTextView)
        val timestamp: TextView = itemView.findViewById(R.id.reviewTimestampTextView)
    }

    // Inflates the item layout and creates the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review_page, parent, false) // Use your item layout
        return ReviewViewHolder(view)
    }

    // Binds data from the Review object to the views in the ViewHolder
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        holder.reviewerName.text = review.userName ?: "Anonymous" // Handle null username
        holder.ratingBar.rating = review.rating
        holder.reviewText.text = review.reviewText
        holder.timestamp.text = review.timestamp?.let { dateFormat.format(it) } ?: "..." // Format date or show placeholder

    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int {
        return reviews.size
    }
}