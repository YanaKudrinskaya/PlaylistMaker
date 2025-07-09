package com.yanakudrinskaya.playlistmaker.media.ui.model

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

sealed interface  FavoriteScreenState {
    data class Content(
        val tracks: List<Track>
    ): FavoriteScreenState
    object Empty: FavoriteScreenState
}