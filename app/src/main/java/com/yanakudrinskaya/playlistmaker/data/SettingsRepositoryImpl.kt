package com.yanakudrinskaya.playlistmaker.data

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.yanakudrinskaya.playlistmaker.domain.api.SettingsRepository


const val EXAMPLE_PREFERENCES = "example_preferences"
const val THEME_KEY = "key_for_dark_theme_switch"
const val HISTORY_LIST_KEY = "key_for_history_track_list"

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val prefs = context.getSharedPreferences(EXAMPLE_PREFERENCES, Context.MODE_PRIVATE)
    private val resources = context.resources

    override fun isDarkThemeEnabled(): Boolean {
        val phoneTheme = isSystemDarkTheme()
        return prefs.getBoolean(THEME_KEY, phoneTheme)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(THEME_KEY, enabled).apply()
    }

    override fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnabled) MODE_NIGHT_YES
            else MODE_NIGHT_NO
        )
    }

    private fun isSystemDarkTheme(): Boolean {
        return (resources.configuration.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES
    }
}