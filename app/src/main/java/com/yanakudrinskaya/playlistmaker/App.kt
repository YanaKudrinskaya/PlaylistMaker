package com.yanakudrinskaya.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

const val EXAMPLE_PREFERENCES = "example_preferences"
const val THEME_KEY = "key_for_dark_theme_switch"
const val HISTORY_LIST_KEY = "key_for_history_track_list"

class App: Application() {

    var darkTheme = false
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(EXAMPLE_PREFERENCES, MODE_PRIVATE)
        val phoneTheme = if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) true else false
        darkTheme = sharedPrefs.getBoolean(THEME_KEY, phoneTheme)
        theme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPrefs.edit()
            .putBoolean(THEME_KEY, darkTheme)
            .apply()
        theme(darkTheme)
    }

    fun theme (darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}