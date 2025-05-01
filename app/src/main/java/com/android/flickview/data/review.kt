package com.android.flickview.data // Or your appropriate package

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Review(
    var id: String = "", // Document ID from Firestore (optional here, can be added later)
    val movieId: String = "",
    val userId: String = "",
    val userName: String? = null, // Store display name for convenience
    val rating: Float = 0.0f,
    val reviewText: String = "",
    @ServerTimestamp // Automatically sets the timestamp on the server
    val timestamp: Date? = null // Use Date? as it will be null until set by server
) {
    // Add a no-argument constructor for Firestore deserialization
    constructor() : this("", "", "", null, 0.0f, "", null)
}