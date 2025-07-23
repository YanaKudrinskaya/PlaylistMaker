package com.yanakudrinskaya.playlistmaker.playlist.data

import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import com.yanakudrinskaya.playlistmaker.playlist.data.converters.PlaylistConverter
import com.yanakudrinskaya.playlistmaker.playlist.data.db.dao.PlaylistDao
import com.yanakudrinskaya.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.yanakudrinskaya.playlistmaker.playlist.data.db.entity.PlaylistTrackCrossRef
import com.yanakudrinskaya.playlistmaker.playlist.domain.PlaylistRepository
import com.yanakudrinskaya.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: PlaylistDao,
    private val converter: PlaylistConverter
) : PlaylistRepository {

    override suspend fun createPlaylist(
        title: String,
        description: String,
        cover: PlaylistCover?
    ): Long {
        return appDatabase.insertPlaylist(
            PlaylistEntity(title = title, description = description, cover = cover?.filePath ?: "")
        )
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.getAllPlaylists()
            .map { playlists ->
                playlists.map { playlistEntity ->
                    val trackIds = appDatabase.getTrackIdsForPlaylist(playlistEntity.id)
                    converter.map(playlistEntity, trackIds)
                }
            }
    }

    override suspend fun updatePlaylistTracks(playlistId: Long, trackIds: List<Int>) {
        appDatabase.clearPlaylistTracks(playlistId)
        val refs = trackIds.mapIndexed { index, trackId ->
            PlaylistTrackCrossRef(playlistId, trackId, index)
        }
        appDatabase.insertTrackRefs(refs)
    }
}