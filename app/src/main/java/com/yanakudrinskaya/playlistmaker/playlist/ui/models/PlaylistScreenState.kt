package com.yanakudrinskaya.playlistmaker.playlist.ui.models

import com.yanakudrinskaya.playlistmaker.playlist.domain.models.Playlist

sealed interface PlaylistScreenState {
    data class Content(
        val playlists: List<Playlist>
    ): PlaylistScreenState
    object Empty: PlaylistScreenState
}