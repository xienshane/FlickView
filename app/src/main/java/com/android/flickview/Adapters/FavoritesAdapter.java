// In FavoritesAdapter.java
package com.android.flickview.Adapters; // Make sure package is correct

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull; // Use androidx annotations
import androidx.recyclerview.widget.RecyclerView;

import com.android.flickview.Domains.FavoriteItem; // Ensure correct import for your FavoriteItem class
import com.android.flickview.R;
import com.bumptech.glide.Glide; // Assuming you use Glide

import java.util.List; // Use java.util.List

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private final List<FavoriteItem> favoriteItems; // Use List<FavoriteItem> in Java

    // --- Constructor ---
    // Takes a List of non-null FavoriteItem objects
    public FavoritesAdapter(List<FavoriteItem> favoriteItems) {
        this.favoriteItems = favoriteItems;
    }

    // --- ViewHolder Definition ---
    // Define your ViewHolder according to your item layout (e.g., viewholder_favorite_item.xml)
    // Making it static is generally recommended in Java for RecyclerView adapters
    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        // Example: Get references to views in your item layout
        // Make fields public or provide getters if needed outside
        final TextView titleTextView; // CHANGE R.id TO YOUR ACTUAL ID
        final ImageView posterImageView; // CHANGE R.id TO YOUR ACTUAL ID
        // Add any other views like a remove button, etc.
        // final ImageButton removeButton;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views using findViewById
            titleTextView = itemView.findViewById(R.id.titleText); // CHANGE R.id TO YOUR ACTUAL ID
            posterImageView = itemView.findViewById(R.id.posterImage); // CHANGE R.id TO YOUR ACTUAL ID
            // removeButton = itemView.findViewById(R.id.item_favorite_remove_button);
        }
    }

    // --- Adapter Overrides ---
    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout XML file
        // CHANGE R.layout.viewholder_favorite_item TO YOUR ACTUAL LAYOUT FILE NAME
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.viewholder_favorite_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        // Get the item at the current position
        // Add null check for item just in case, though list type implies non-null
        final FavoriteItem item = favoriteItems.get(position);
        if (item == null) {
            return; // Should not happen if list contains non-nulls, but safe practice
        }


        // Bind data to the ViewHolder views
        // Use getters from the Java FavoriteItem class and handle potential nulls
        holder.titleTextView.setText(item.getTitle() != null ? item.getTitle() : "N/A"); // Handle potential null title

        Glide.with(holder.itemView.getContext())
                .load(item.getPosterUrl()) // Use getter: getPosterUrl()
                .placeholder(R.drawable.placeholder_poster) // Use your placeholder
                .error(R.drawable.placeholder_poster) // Use your error placeholder
                .into(holder.posterImageView);

        // Set click listeners if needed
        // holder.itemView.setOnClickListener(v -> { /* Go to detail? */ });
        // if (holder.removeButton != null) {
        //     holder.removeButton.setOnClickListener(v -> { /* Remove item, notify activity/viewmodel */ });
        // }
    }

    @Override
    public int getItemCount() {
        // Return 0 if the list itself is null, otherwise return its size
        return favoriteItems != null ? favoriteItems.size() : 0;
    }

    // Optional: You might have methods here to update the list,
    // but in your current setup, FavoritesActivity modifies the list directly
    // and calls notifyDataSetChanged(). If you add methods here, they should
    // modify 'this.favoriteItems' and call appropriate notifyItem... methods.
    // Example:
    // public void updateData(List<FavoriteItem> newItems) {
    //     this.favoriteItems.clear();
    //     if (newItems != null) {
    //         this.favoriteItems.addAll(newItems);
    //     }
    //     notifyDataSetChanged(); // Or use DiffUtil for better performance
    // }
}