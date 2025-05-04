package com.android.flickview.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.flickview.activities.DetailActivity;
import com.android.flickview.Domains.Datum;
import com.android.flickview.Domains.ListFilm;
import com.android.flickview.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class FilmListAdapter extends RecyclerView.Adapter<FilmListAdapter.ViewHolder> implements Filterable {

    public ListFilm originalItems;
    private List<Datum> filteredItems;
    private Context context;

    public FilmListAdapter(ListFilm items) {
        this.originalItems = items;
        this.filteredItems = new ArrayList<>(items.getData());
    }

    @NonNull
    @Override
    public FilmListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_film, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmListAdapter.ViewHolder holder, int position) {
        String title = filteredItems.get(position).getTitle();
        holder.titleTxt.setText(title != null ? title : "No Title Available");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));

        Log.d("FilmListAdapter", "Movie Title: " + title);

        Glide.with(context)
                .load(filteredItems.get(position).getPoster())
                .apply(requestOptions)
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("id", filteredItems.get(position).getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Datum> filteredList = new ArrayList<>();
                String query = constraint != null ? constraint.toString().toLowerCase().trim() : "";

                if (query.isEmpty()) {
                    filteredList.addAll(originalItems.getData());
                } else {
                    for (Datum film : originalItems.getData()) {
                        if (film.getTitle() != null && film.getTitle().toLowerCase().contains(query)) {
                            filteredList.add(film);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems.clear();
                filteredItems.addAll((List<Datum>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
