package com.nagopy.android.aplin

import android.app.Application
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var app: Application

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Aplin.getApplicationComponent().inject(this)

        setContentView(R.layout.activity_main)
    }
}