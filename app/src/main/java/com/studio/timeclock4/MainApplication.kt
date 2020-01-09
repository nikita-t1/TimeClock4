package com.studio.timeclock4

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

open class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

}