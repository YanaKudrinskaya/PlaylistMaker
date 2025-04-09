package com.yanakudrinskaya.playlistmaker.domain.impl

import com.yanakudrinskaya.playlistmaker.domain.api.SettingsInteractor
import com.yanakudrinskaya.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl (private val repository: SettingsRepository) : SettingsInteractor {
    override fun isDarkThemeEnabled(consumer: SettingsInteractor.DarkThemeConsumer) {
        val darkTheme = repository.isDarkThemeEnabled()
        consumer.consume(darkTheme)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        repository.setDarkThemeEnabled(enabled)
    }

    override fun applyTheme(darkThemeEnabled: Boolean) {
        repository.applyTheme(darkThemeEnabled)
    }

}