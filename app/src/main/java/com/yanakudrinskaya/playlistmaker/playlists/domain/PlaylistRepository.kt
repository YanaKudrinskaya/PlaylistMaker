package com.yanakudrinskaya.playlistmaker.playlists.domain

import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(title: String, description: String, cover: PlaylistCover?): Long
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylistTracks(playlistId: Long, tracks: List<Track>)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylistById(id: Long): Playlist?
}