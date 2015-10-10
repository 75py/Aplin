package com.nagopy.android.aplin

import android.app.Application
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nagopy.android.aplin.databinding.ActivityMainBinding
import com.nagopy.android.aplin.databinding.AplinDataBindingUtil
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var app: Application

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Aplin.getApplicationComponent().inject(this)

        val binding: ActivityMainBinding = AplinDataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.test = app.getString(R.string.app_name)
    }
}