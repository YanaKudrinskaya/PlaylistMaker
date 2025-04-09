package com.yanakudrinskaya.playlistmaker.domain.api

interface SettingsInteractor {
    fun isDarkThemeEnabled(consumer: DarkThemeConsumer)
    fun setDarkThemeEnabled(enabled: Boolean)
    fun applyTheme(darkThemeEnabled: Boolean)

    interface DarkThemeConsumer {
        fun consume(darkTheme: Boolean)
    }
}