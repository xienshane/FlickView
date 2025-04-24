package com.android.flickview.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.flickview.activities.DetailActivity;
import com.android.flickview.Domains.FavoriteItem;
import com.android.flickview.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private final List<FavoriteItem> favoriteItems;
    private Context context;

    // Constructor to initialize the list of favorites
    public FavoritesAdapter(List<FavoriteItem> favoriteItems) {
        this.favoriteItems = favoriteItems;
    }

    @NonNull
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_favorite_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, int position) {
        // Get the current favorite item
        FavoriteItem item = favoriteItems.get(position);

        // Set title
        String title = item.getTitle();
        holder.titleText.setText(title != null ? title : "No Title Available");

        // Set poster image using Glide with transformations
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));

        Glide.with(context)
                .load(item.getPosterUrl()) // Poster image URL
                .apply(requestOptions)
                .into(holder.posterImage);

        // Set the click listener to navigate to the detail activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("itemId", item.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    // ViewHolder for each item in the list
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        ImageView posterImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.txtTitle);
            posterImage = itemView.findViewById(R.id.posterImage);
        }
    }
}
