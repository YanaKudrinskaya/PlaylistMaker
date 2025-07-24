package com.yanakudrinskaya.playlistmaker.playlist.domain.impl

import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import com.yanakudrinskaya.playlistmaker.playlist.domain.PlaylistInteractor
import com.yanakudrinskaya.playlistmaker.playlist.domain.PlaylistRepository
import com.yanakudrinskaya.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistIntractorImpl(
    private val repository: PlaylistRepository
): PlaylistInteractor {
    override suspend fun createPlaylist(
        title: String,
        description: String,
        cover: PlaylistCover?
    ): Long {
        return repository.createPlaylist(title, description, cover)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun updatePlaylistTracks(
        playlistId: Long,
        trackIds: List<Int>
    ) {
        repository.updatePlaylistTracks(playlistId,trackIds)
    }
}