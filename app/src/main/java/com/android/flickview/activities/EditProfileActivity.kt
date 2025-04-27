package com.android.flickview.activities;

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.flickview.Adapters.FavoritesAdapter
import com.android.flickview.Domains.FavoriteItem
import com.android.flickview.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class EditProfileActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_page)

        // Back Button
        val buttonBack : ImageButton = findViewById(R.id.buttonback)
        buttonBack.setOnClickListener{
            finish()
        }
    }
}
