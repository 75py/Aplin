package com.nagopy.android.aplin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.nagopy.android.aplin.loader.AppLoader

class MainActivity : AppCompatActivity() {

    val injector = KodeinInjector()

    val appLoader: AppLoader by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(appKodein())

        val apps = appLoader.load()
        setContentView(TextView(this).apply {
            text = apps.joinToString { it.toString() }
        })
    }

}