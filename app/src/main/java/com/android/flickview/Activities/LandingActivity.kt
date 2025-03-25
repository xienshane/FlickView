package com.android.flickview.Activities

import android.app.Activity
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
        val url = "https://moviesapi.ir/api/v1/movies?page=4"

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
        val url = "https://moviesapi.ir/api/v1/movies?page=6"

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
            SliderItems(R.drawable.wide1),
            SliderItems(R.drawable.wide2),
            SliderItems(R.drawable.wide3)
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
    }
}