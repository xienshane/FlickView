package com.android.flickview.Activities

import android.app.Activity
import android.os.Bundle
import com.android.flickview.R

class DetailActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.moviedetail)
    }
}