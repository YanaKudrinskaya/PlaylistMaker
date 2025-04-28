package com.yanakudrinskaya.playlistmaker

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.yanakudrinskaya.playlistmaker.creator.Creator
import com.yanakudrinskaya.playlistmaker.settings.data.impl.EXAMPLE_PREFERENCES
import com.yanakudrinskaya.playlistmaker.settings.data.impl.THEME_KEY

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        val interactor = Creator.provideSettingsInteractor()
        interactor.updateThemeSetting(interactor.getThemeSettings())
    }
}