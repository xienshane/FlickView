package com.android.flickview.activities

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

class FavoritesActivity : Activity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites)

        // Back Button
        val buttonBack: ImageButton = findViewById(R.id.button_back)
        buttonBack.setOnClickListener {
            finish()
        }

        // RecyclerView setup
        recyclerView = findViewById(R.id.recycler_view_favorites)
        recyclerView.layoutManager = LinearLayoutManager(this)
        favoritesAdapter = FavoritesAdapter(getFavoritesList())
        recyclerView.adapter = favoritesAdapter

        // Bottom Navigation setup
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView3)
        bottomNavigationView.selectedItemId = R.id.favorites // Highlight current page
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, LandingActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfilePageActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.favorites -> true

                else -> false
            }
        }
    }

    private fun getFavoritesList(): MutableList<FavoriteItem> {
        return mutableListOf(
            FavoriteItem("The Lord of the Rings: The Fellowship of the Ring", "https://moviesapi.ir/images/tt0120737_poster.jpg", 1),
            FavoriteItem( "Star Wars: Episode V - The Empire Strikes Back", "https://moviesapi.ir/images/tt0080684_poster.jpg", 2),
            FavoriteItem( "Forrest Gump", "https://moviesapi.ir/images/tt0109830_poster.jpg", 3)
        )
    }
}
