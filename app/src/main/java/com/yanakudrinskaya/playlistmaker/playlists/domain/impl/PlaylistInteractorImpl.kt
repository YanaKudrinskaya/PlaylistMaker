package com.yanakudrinskaya.playlistmaker.playlists.domain.impl

import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import com.yanakudrinskaya.playlistmaker.playlists.domain.PlaylistInteractor
import com.yanakudrinskaya.playlistmaker.playlists.domain.PlaylistRepository
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
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
        tracks: List<Track>
    ) {
        repository.updatePlaylistTracks(playlistId,tracks)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }
}