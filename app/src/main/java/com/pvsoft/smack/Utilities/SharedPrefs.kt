package com.pvsoft.smack.Utilities

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by minhp on 1/14/2018.
 */
class SharedPrefs constructor(val context: Context) {

    val PREFS_FILENAME = "PREFS_FILENAME"
    val PREFS_VALUE_IS_LOGGED_IN = "PREFS_VALUE_IS_LOGGED_IN"
    val PREFS_VALUE_EMAIL = "PREFS_VALUE_EMAIL"
    val PREFS_VALUE_TOKEN = "PREFS_VALUE_TOKEN"

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)


    var isLoggedIn: Boolean
        get() = prefs.getBoolean(PREFS_VALUE_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(PREFS_VALUE_IS_LOGGED_IN, value).apply()
    var email: String
        get() = prefs.getString(PREFS_VALUE_EMAIL, "")
        set(value) = prefs.edit().putString(PREFS_VALUE_EMAIL, value).apply()
    var token: String
        get() = prefs.getString(PREFS_VALUE_TOKEN, "")
        set(value) = prefs.edit().putString(PREFS_VALUE_TOKEN, value).apply()


}