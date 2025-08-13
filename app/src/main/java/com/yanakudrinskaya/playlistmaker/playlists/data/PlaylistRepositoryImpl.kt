package com.yanakudrinskaya.playlistmaker.playlists.data

import android.util.Log
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import com.yanakudrinskaya.playlistmaker.favorite.data.converters.TrackDbConvertor
import com.yanakudrinskaya.playlistmaker.favorite.data.db.dao.TrackDao
import com.yanakudrinskaya.playlistmaker.playlists.data.converters.PlaylistConverter
import com.yanakudrinskaya.playlistmaker.playlists.data.db.dao.PlaylistDao
import com.yanakudrinskaya.playlistmaker.playlists.data.db.dao.PlaylistTrackDao
import com.yanakudrinskaya.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.yanakudrinskaya.playlistmaker.playlists.data.db.entity.PlaylistTrackCrossRef
import com.yanakudrinskaya.playlistmaker.playlists.domain.PlaylistRepository
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val trackDao: TrackDao,
    private val playlistConverter: PlaylistConverter,
    private val trackDbConvertor: TrackDbConvertor
) : PlaylistRepository {

    override suspend fun createPlaylist(
        title: String,
        description: String,
        cover: PlaylistCover?
    ): Long {
        return playlistDao.insertPlaylist(
            PlaylistEntity(title = title, description = description, cover = cover?.filePath ?: "")
        )
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
            .map { playlists ->
                playlists.map { playlistEntity ->
                    val trackIds = playlistTrackDao.getTracksForPlaylist(playlistEntity.id)
                        .map { it.trackId }
                    val tracks = trackDao.getTracksByIds(trackIds)
                        .map { trackDbConvertor.map(it) }
                    playlistConverter.map(playlistEntity, tracks)
                }
            }
    }

    override suspend fun updatePlaylistTracks(playlistId: Long, tracks: List<Track>) {
        playlistDao.clearPlaylistTracks(playlistId)
        val favoriteTrackIds = trackDao.getIdsTracks()
        tracks.forEach { track ->
            val isFavorite = favoriteTrackIds.contains(track.trackId)
            trackDao.insertTrack(trackDbConvertor.map(track, isFavorite))
        }

        val refs = tracks.mapIndexed { index, track ->
            PlaylistTrackCrossRef(playlistId, track.trackId, index)
        }
        playlistDao.insertTrackRefs(refs)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistTrackDao.getTracksForPlaylist(playlistId).forEach { crossRef ->
            val trackId = crossRef.trackId

            val isTrackInOtherPlaylists = playlistTrackDao.getTracksForPlaylist(playlistId)
                .any { it.trackId == trackId && it.playlistId != playlistId }

            val trackEntity = trackDao.getTracksByIds(listOf(trackId)).firstOrNull()

            if (!isTrackInOtherPlaylists && trackEntity?.isFavorite != true) {
                trackDao.deleteTrack(trackId)
            }
        }

        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(
            PlaylistEntity(
                id = playlist.id,
                title = playlist.title,
                description = playlist.description,
                cover = playlist.cover
            )
        )
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val playlistEntity = playlistDao.getPlaylistById(id) ?: return null
        val crossRefs = playlistTrackDao.getTracksForPlaylist(id)
        val tracks = trackDao.getTracksByIds(crossRefs.map { it.trackId }).map { trackDbConvertor.map(it) }
        val sortedTracks = crossRefs.map { crossRef ->
            tracks.first { it.trackId == crossRef.trackId }
        }
        return playlistConverter.map(playlistEntity, sortedTracks)
    }
}