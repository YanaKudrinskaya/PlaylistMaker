package com.yanakudrinskaya.playlistmaker.media.ui.view_model

import androidx.lifecycle.ViewModel
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.media.domain.use_cases.ResourcesProviderUseCase

class MediaViewModel(
    private val resourcesProvider: ResourcesProviderUseCase
) : ViewModel() {

    fun getTabTitle(position: Int): String {
        return when(position) {
            0 -> resourcesProvider.getString(R.string.tab_favorite_tracks)
            1 -> resourcesProvider.getString(R.string.tab_playlists)
            else -> ""
        }
    }
}