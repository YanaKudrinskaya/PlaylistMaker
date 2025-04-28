package com.yanakudrinskaya.playlistmaker.settings.domain.impl

import com.yanakudrinskaya.playlistmaker.settings.domain.SettingsInteractor
import com.yanakudrinskaya.playlistmaker.settings.domain.SettingsRepository
import com.yanakudrinskaya.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl (private val repository: SettingsRepository) : SettingsInteractor
{

    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        repository.updateThemeSetting(settings)
    }

}