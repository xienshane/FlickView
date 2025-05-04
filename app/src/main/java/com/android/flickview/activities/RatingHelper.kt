package com.android.flickview.activities

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object RatingHelper {

    data class Movie(val title: String, val rating: Float)

    fun getRatedMovies(context: Context, onComplete: (List<Movie>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onComplete(emptyList())

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("ratings")
            .get()
            .addOnSuccessListener { result ->
                val movies = result.documents.mapNotNull { doc ->
                    val title = doc.getString("title")
                    val rating = doc.getDouble("rating")?.toFloat()
                    if (title != null && rating != null) Movie(title, rating) else null
                }
                onComplete(movies)
            }
            .addOnFailureListener {
                Log.e("RatingHelper", "Failed to fetch ratings", it)
                onComplete(emptyList())
            }
    }

    fun getRatedCount(context: Context, onResult: (Int) -> Unit) {
        getRatedMovies(context) { movies ->
            onResult(movies.count { it.rating > 0 })
        }
    }

    fun getAverageRating(context: Context, onResult: (Float) -> Unit) {
        getRatedMovies(context) { movies ->
            val rated = movies.filter { it.rating > 0 }
            if (rated.isEmpty()) {
                onResult(0f)
            } else {
                val total = rated.sumOf { it.rating.toDouble() }
                onResult((total / rated.size).toFloat())
            }
        }
    }
}

