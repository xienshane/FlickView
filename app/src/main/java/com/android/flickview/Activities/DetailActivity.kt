package com.android.flickview.Activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.flickview.Adapters.ActorsListAdapter
import com.android.flickview.Adapters.CategoryEachFilmListAdapter
import com.android.flickview.Domains.FilmItem
import com.android.flickview.R
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson

class DetailActivity : Activity() {
    private lateinit var mRequestQueue: RequestQueue
    private lateinit var mStringRequest: StringRequest
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTxt: TextView
    private lateinit var movieRateText: TextView
    private lateinit var movieTimeTxt: TextView
    private lateinit var movieSummaryInfo: TextView
    private lateinit var movieActorsInfo: TextView
    private lateinit var pic2: ImageView
    private lateinit var backImg: ImageView
    private lateinit var recyclerViewActors: RecyclerView
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var scrollView: NestedScrollView
    private var idFilm: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moviedetail)

        idFilm = intent.getIntExtra("id", 0)
        initView()
        sendRequest()
    }

    private fun sendRequest() {
        mRequestQueue = Volley.newRequestQueue(this)
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE

        mStringRequest = StringRequest(
            Request.Method.GET,
            "https://moviesapi.ir/api/v1/movies/$idFilm",
            { response ->
                val gson = Gson()
                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE

                val item: FilmItem = gson.fromJson(response, FilmItem::class.java)

                Glide.with(this)
                    .load(item.poster)
                    .into(pic2)
                titleTxt.text = item.title
                movieRateText.text = item.imdbRating
                movieTimeTxt.text = item.runtime
                movieSummaryInfo.text = item.plot
                movieActorsInfo.text = item.actors

                item.images?.let {
                    recyclerViewActors.adapter = ActorsListAdapter(it)
                }
                item.genres?.let {
                    recyclerViewCategory.adapter = CategoryEachFilmListAdapter(it)
                }
            },
            { progressBar.visibility = View.GONE }
        )
        mRequestQueue.add(mStringRequest)
    }

    private fun initView() {
        titleTxt = findViewById(R.id.movieNameTxt)
        progressBar = findViewById(R.id.progressBarDetail)
        scrollView = findViewById(R.id.scrollView2)
        pic2 = findViewById(R.id.picDetail)
        movieRateText = findViewById(R.id.movieStar)
        movieTimeTxt = findViewById(R.id.movieTime)
        movieSummaryInfo = findViewById(R.id.movieSummary)
        movieActorsInfo = findViewById(R.id.movieActorInfo)
        backImg = findViewById(R.id.backImg)
        recyclerViewCategory = findViewById(R.id.genreView)
        recyclerViewActors = findViewById(R.id.actorsView)
        recyclerViewActors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        backImg.setOnClickListener { finish() }
    }
}


