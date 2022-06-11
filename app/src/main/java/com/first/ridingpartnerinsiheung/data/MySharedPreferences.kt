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

    var ridingDateList : String?
        get() = prefs.getString("riding_time","")
        set(value) = prefs.edit().putString("riding_time",value).apply()

    var goalDistance : Int
        get() = prefs.getInt("goal_distance", 0)
        set(value) = prefs.edit().putInt("goal_distance",value).apply()
}