package com.yanakudrinskaya.playlistmaker.domain.api

interface SettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
    fun applyTheme(darkThemeEnabled: Boolean)
}