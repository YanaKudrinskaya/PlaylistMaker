package com.yanakudrinskaya.playlistmaker.player.ui.model

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

sealed class TrackScreenState {
    object Loading: TrackScreenState()
    data class Content(
        val trackModel: Track
    ): TrackScreenState()
}