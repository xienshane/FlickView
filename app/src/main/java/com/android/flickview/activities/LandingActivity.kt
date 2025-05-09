package com.android.flickview.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.android.flickview.Adapters.CategoryListAdapter
import com.android.flickview.Adapters.FilmListAdapter
import com.android.flickview.Adapters.SliderAdapters
import com.android.flickview.Domains.GenresItem
import com.android.flickview.Domains.SliderItems
import com.android.flickview.R
import com.android.volley.toolbox.Volley
import com.android.flickview.Domains.ListFilm
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText



class LandingActivity : Activity() {
    private lateinit var adapterBestMovies: RecyclerView.Adapter<*>
    private lateinit var adapterUpcoming: RecyclerView.Adapter<*>
    private lateinit var adapterCategory: RecyclerView.Adapter<*>
    private lateinit var recycleViewBestMovies: RecyclerView
    private lateinit var recycleViewUpcoming: RecyclerView
    private lateinit var recycleViewCategory: RecyclerView
    private lateinit var mRequestQueue: RequestQueue
    private lateinit var loading1: ProgressBar
    private lateinit var loading2: ProgressBar
    private lateinit var loading3: ProgressBar
    private lateinit var viewPager2: ViewPager2
    private val slideHandler = Handler(Looper.getMainLooper())
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchAdapter: FilmListAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing)

        initView()
        banners()
        sendRequestBestMovies()
        sendRequestUpcoming()
        sendRequestCategory()
    }

    private fun sendRequestBestMovies() {
        mRequestQueue = Volley.newRequestQueue(this)
        loading1.visibility = View.VISIBLE
        val url = "https://moviesapi.ir/api/v1/movies?page=1"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val gson = Gson()
                loading1.visibility = View.GONE
                val items = gson.fromJson(response, ListFilm::class.java)
                adapterBestMovies = FilmListAdapter(items);
                recycleViewBestMovies.adapter = adapterBestMovies
            },
            { error ->
                loading1.visibility = View.GONE
                Log.i("UiLover", "onErrorResponse: ${'$'}{error}")
            }
        )
        mRequestQueue.add(stringRequest)
    }

    private fun sendRequestUpcoming() {
        mRequestQueue = Volley.newRequestQueue(this)
        loading3.visibility = View.VISIBLE
        val url = "https://moviesapi.ir/api/v1/movies?page=2"

        val stringRequest3 = StringRequest(
            Request.Method.GET, url,
            { response ->
                val gson = Gson()
                loading3.visibility = View.GONE
                val items = gson.fromJson(response, ListFilm::class.java)
                adapterUpcoming = FilmListAdapter(items);
                recycleViewUpcoming.adapter = adapterUpcoming
            },
            { error ->
                loading3.visibility = View.GONE
                Log.i("UiLover", "onErrorResponse: ${'$'}{error}")
            }
        )
        mRequestQueue.add(stringRequest3)
    }

    private fun sendRequestCategory() {
        mRequestQueue = Volley.newRequestQueue(this)
        loading2.visibility = View.VISIBLE
        val url = "https://moviesapi.ir/api/v1/genres"

        val stringRequest2 = StringRequest(
            Request.Method.GET, url,
            { response ->
                val gson = Gson()
                loading2.visibility = View.GONE
                val catList: ArrayList<GenresItem> = Gson().fromJson(response, object : TypeToken<ArrayList<GenresItem>>() {}.type)
                adapterCategory = CategoryListAdapter(catList);
                recycleViewCategory.adapter = adapterCategory
            },
            { error ->
                loading2.visibility = View.GONE
                Log.i("UiLover", "onErrorResponse: ${'$'}{error}")
            }
        )
        mRequestQueue.add(stringRequest2)
    }

    private fun banners() {
        val sliderItems = listOf(
            SliderItems(R.drawable.wide16),
            SliderItems(R.drawable.wide6),
            SliderItems(R.drawable.wide15),
            SliderItems(R.drawable.wide4),
            SliderItems(R.drawable.wide8),
            SliderItems(R.drawable.wide7),
            SliderItems(R.drawable.wide2),
            SliderItems(R.drawable.wide1),
            SliderItems(R.drawable.wide9),
            SliderItems(R.drawable.wide10),
            SliderItems(R.drawable.wide11),
            SliderItems(R.drawable.wide12),
            SliderItems(R.drawable.wide13),
            SliderItems(R.drawable.wide14),
            SliderItems(R.drawable.wide3),
            SliderItems(R.drawable.wide5)
        )

        viewPager2.adapter = SliderAdapters(sliderItems, viewPager2)
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
            addTransformer { page: View, position: Float ->
                val scale = 0.85f + (1 - kotlin.math.abs(position)) * 0.15f
                page.scaleY = scale
            }
        }

        viewPager2.setPageTransformer(compositePageTransformer)
        viewPager2.currentItem = 1
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                slideHandler.removeCallbacks(sliderRunnable)
            }
        })
    }

    private val sliderRunnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    override fun onPause() {
        super.onPause()
        slideHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        slideHandler.postDelayed(sliderRunnable, 2000)
    }

    private fun initView() {
        viewPager2 = findViewById(R.id.view)
        recycleViewBestMovies = findViewById(R.id.view1)
        recycleViewBestMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycleViewBestMovies.setHasFixedSize(true)
        recycleViewUpcoming = findViewById(R.id.view2)
        recycleViewUpcoming.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycleViewCategory = findViewById(R.id.view3)
        recycleViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        loading1 = findViewById(R.id.progressBar1)
        loading2 = findViewById(R.id.progressBar2)
        loading3 = findViewById(R.id.progressBar3)
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        searchResultsRecyclerView.setHasFixedSize(true)



        val etSearchMovies = findViewById<EditText>(R.id.etSearchMovies)
        etSearchMovies.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty() && ::adapterBestMovies.isInitialized) {
                    val originalAdapter = adapterBestMovies as FilmListAdapter
                    searchAdapter = FilmListAdapter(originalAdapter.originalItems)
                    searchAdapter.filter.filter(query)

                    // Show only search results
                    searchResultsRecyclerView.adapter = searchAdapter
                    searchResultsRecyclerView.visibility = View.VISIBLE

                    recycleViewBestMovies.visibility = View.GONE
                    recycleViewUpcoming.visibility = View.GONE
                    recycleViewCategory.visibility = View.GONE
                    viewPager2.visibility = View.GONE

                        val query = s?.toString() ?: ""
                        if (query.isNotEmpty()) {
                            filterMovies(query)
                            fadeView(searchResultsRecyclerView, true)
                            fadeView(recycleViewBestMovies, false)
                            fadeView(recycleViewUpcoming, false)
                            fadeView(recycleViewCategory, false)
                        } else {
                            fadeView(searchResultsRecyclerView, false)
                            fadeView(recycleViewBestMovies, true)
                            fadeView(recycleViewUpcoming, true)
                            fadeView(recycleViewCategory, true)
                        }


                } else {
                    // Restore original layout
                    searchResultsRecyclerView.visibility = View.GONE
                    recycleViewBestMovies.visibility = View.VISIBLE
                    recycleViewUpcoming.visibility = View.VISIBLE
                    recycleViewCategory.visibility = View.VISIBLE
                    viewPager2.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView3)
        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Already on LandingActivity, optionally refresh or scroll to top
                    // Example: scroll a RecyclerView to the top
                    recycleViewBestMovies.smoothScrollToPosition(0)
                    true
                }
                R.id.favorites -> {
                    // Navigate to FavoritesActivity
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.profile -> {
                    // Navigate to ProfilePageActivity
                    startActivity(Intent(this, ProfilePageActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                else -> false
            }
        }


    }

    private fun fadeView(view: View, show: Boolean) {
        val duration = 300L
        if (show) {
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate().alpha(1f).setDuration(duration).start()
        } else {
            view.animate().alpha(0f).setDuration(duration).withEndAction {
                view.visibility = View.GONE
            }.start()
        }
    }


    private fun filterMovies(query: String) {
        if (::adapterBestMovies.isInitialized && adapterBestMovies is FilmListAdapter) {
            val filmAdapter = adapterBestMovies as FilmListAdapter
            filmAdapter.filter.filter(query)
        }
    }
}