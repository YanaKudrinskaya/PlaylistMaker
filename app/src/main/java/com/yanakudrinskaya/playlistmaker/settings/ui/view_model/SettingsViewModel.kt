package com.yanakudrinskaya.playlistmaker.settings.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.yanakudrinskaya.playlistmaker.creator.Creator
import com.yanakudrinskaya.playlistmaker.settings.domain.SettingsInteractor
import com.yanakudrinskaya.playlistmaker.settings.domain.model.ThemeSettings
import com.yanakudrinskaya.playlistmaker.settings.ui.model.Event
import com.yanakudrinskaya.playlistmaker.settings.ui.model.NavigationEvent
import com.yanakudrinskaya.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val themeState = MutableLiveData(false)
    fun getThemeState(): LiveData<Boolean> = themeState

    private val navigationEvents = MutableLiveData<Event>()
    fun getNavigationEvents(): LiveData<Event> = navigationEvents

    init {
        Log.d("TEST", "init!")
        loadTheme()
    }

    private fun loadTheme() {
        themeState.value = settingsInteractor.getThemeSettings().isDark
    }

    fun updateTheme(isDark: Boolean) {
            settingsInteractor.updateThemeSetting(ThemeSettings(isDark))
            themeState.value = isDark
    }

    fun getIntent(event: NavigationEvent) {
        when(event) {
            NavigationEvent.SHARE -> shareApp()
            NavigationEvent.SUPPORT -> contactSupport()
            NavigationEvent.AGREEMENT -> openAgreement()
        }
    }

    private fun shareApp() {
            navigationEvents.postValue(
                Event(
                    intent = sharingInteractor.shareApp(),
                    errorMessage = sharingInteractor.getShareError()
                )
            )
    }

    private fun contactSupport() {
            navigationEvents.postValue(
                Event(
                    intent = sharingInteractor.openSupport(),
                    errorMessage = sharingInteractor.getSupportError()
                )
            )
    }

    private fun openAgreement() {
            navigationEvents.postValue(
                Event(
                    intent = sharingInteractor.openTerms(),
                    errorMessage = sharingInteractor.getUserAgreementError()
                )
            )
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsInteractor = Creator.provideSettingsInteractor()
                val sharingInteractor = Creator.provideSharingInteractor()
                SettingsViewModel(sharingInteractor, settingsInteractor)
            }
        }
    }
}



