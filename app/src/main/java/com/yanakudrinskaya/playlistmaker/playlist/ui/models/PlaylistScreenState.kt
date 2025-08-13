package com.yanakudrinskaya.playlistmaker.playlist.ui.models

import android.content.Intent
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

sealed interface PlaylistScreenState {
    data class Content(
        val coverUrl: String,
        val title: String,
        val description: String,
        val tracksCount: String,
        val duration: String,
        val tracks: List<Track>
    ) : PlaylistScreenState

    data class EmptyList(
        val message: String
    ): PlaylistScreenState
    data class Share (
        val intent: Intent,
        val errorMessage: String
    ) : PlaylistScreenState
}
