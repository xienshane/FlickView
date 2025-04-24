package com.android.flickview.Domains;

public class FavoriteItem {

    private String title;       // Title of the favorite item
    private final String posterUrl;   // URL of the poster image
    private int id;             // Unique identifier for the item

    // Constructor
    public FavoriteItem(String title, String posterUrl, int id) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.id = id;
    }

    // Getter for the title
    public String getTitle() {
        return title;
    }

    // Getter for the poster URL
    public String getPosterUrl() {
        return posterUrl;
    }

    // Getter for the item ID
    public int getId() {
        return id;
    }

    // Optional: Setter methods if you need to update values later
    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }
}
