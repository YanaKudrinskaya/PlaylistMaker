package com.yanakudrinskaya.playlistmaker.settings.domain

import com.yanakudrinskaya.playlistmaker.settings.domain.model.ThemeSettings


interface SettingsRepository {

    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)

}