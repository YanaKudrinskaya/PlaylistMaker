package com.yanakudrinskaya.playlistmaker.settings.ui.model

import android.content.Intent

sealed class SettingsEvent {
    data class Event(val intent: Intent, val errorMessage: String) : SettingsEvent()
    data class Theme(val isDark: Boolean) : SettingsEvent()
}

