package com.example.check_in_speaker.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesUtil(context: Context) {
    private val prefs : SharedPreferences =
        context.getSharedPreferences("userData", Context.MODE_PRIVATE)

    fun getString(key: String, defaultValue: String): String{
        return prefs.getString(key, defaultValue).toString()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean{
        return prefs.getBoolean(key, defaultValue)
    }

    fun setString(key: String, str: String){
        prefs.edit().putString(key, str).apply()
    }

    fun setBoolean(key: String, bool: Boolean){
        prefs.edit().putBoolean(key, bool).apply()
    }

    fun removeValue(key: String){
        prefs.edit().remove(key).apply()
    }
}