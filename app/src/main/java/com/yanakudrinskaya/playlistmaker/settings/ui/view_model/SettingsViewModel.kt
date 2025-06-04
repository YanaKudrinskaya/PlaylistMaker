package com.yanakudrinskaya.playlistmaker.settings.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanakudrinskaya.playlistmaker.settings.domain.SettingsInteractor
import com.yanakudrinskaya.playlistmaker.settings.domain.model.ThemeSettings
import com.yanakudrinskaya.playlistmaker.settings.ui.model.NavigationEvent
import com.yanakudrinskaya.playlistmaker.settings.ui.model.SettingsEvent
import com.yanakudrinskaya.playlistmaker.sharing.domain.SharingInteractor


class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val navigationEvents = MutableLiveData<SettingsEvent>()
    fun getNavigationEvents(): LiveData<SettingsEvent> = navigationEvents

    init {
        loadTheme()
    }

    private fun loadTheme() {
        navigationEvents.value = SettingsEvent.Theme(settingsInteractor.getThemeSettings().isDark)
    }

    fun updateTheme(isDark: Boolean) {
        settingsInteractor.updateThemeSetting(ThemeSettings(isDark))
        navigationEvents.postValue(
            SettingsEvent.Theme(isDark)
        )
    }

    fun getIntent(event: NavigationEvent) {
        when (event) {
            NavigationEvent.SHARE -> shareApp()
            NavigationEvent.SUPPORT -> contactSupport()
            NavigationEvent.AGREEMENT -> openAgreement()
        }
    }

    private fun shareApp() {
        navigationEvents.postValue(
            SettingsEvent.Event(
                intent = sharingInteractor.shareApp(),
                errorMessage = sharingInteractor.getShareError()
            )
        )
    }

    private fun contactSupport() {
        navigationEvents.postValue(
            SettingsEvent.Event(
                intent = sharingInteractor.openSupport(),
                errorMessage = sharingInteractor.getSupportError()
            )
        )
    }

    private fun openAgreement() {
        navigationEvents.postValue(
            SettingsEvent.Event(
                intent = sharingInteractor.openTerms(),
                errorMessage = sharingInteractor.getUserAgreementError()
            )
        )
    }

}



