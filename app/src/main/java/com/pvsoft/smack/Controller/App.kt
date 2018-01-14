package com.pvsoft.smack.Controller

import android.app.Application
import com.pvsoft.smack.Utilities.SharedPrefs

/**
 * Created by minhp on 1/14/2018.
 */
class App: Application() {

    companion object {
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = SharedPrefs(applicationContext)
    }
}