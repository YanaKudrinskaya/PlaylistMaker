package com.yanakudrinskaya.playlistmaker.playlist.domain

import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import com.yanakudrinskaya.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(title: String, description: String, cover: PlaylistCover?): Long
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylistTracks(playlistId: Long, trackIds: List<Int>)
}