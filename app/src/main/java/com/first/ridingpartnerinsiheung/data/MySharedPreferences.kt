package com.first.ridingpartnerinsiheung.data

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {
    private val prefsFilename = "prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, 0)

    var accountImage: String?
        get() = prefs.getString("account_image", "")
        set(value) = prefs.edit().putString("account_image", value).apply()

    var accountName: String?
        get() = prefs.getString("account_name", "")
        set(value) = prefs.edit().putString("account_name", value).apply()
}