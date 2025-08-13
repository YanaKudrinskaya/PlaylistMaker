package com.yanakudrinskaya.playlistmaker.playlists.ui.models

import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist

sealed interface PlaylistsScreenState {
    data class Content(
        val playlists: List<Playlist>
    ): PlaylistsScreenState
    object Empty: PlaylistsScreenState
}