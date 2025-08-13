package com.yanakudrinskaya.playlistmaker.favorite.data


import com.yanakudrinskaya.playlistmaker.favorite.data.converters.TrackDbConvertor
import com.yanakudrinskaya.playlistmaker.favorite.data.db.dao.TrackDao
import com.yanakudrinskaya.playlistmaker.favorite.data.db.entity.TrackEntity
import com.yanakudrinskaya.playlistmaker.favorite.domain.db.FavoriteRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val appDatabase: TrackDao,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteRepository {

    override suspend fun addTrackToFavorite(track: Track) {
        appDatabase.insertTrack(trackDbConvertor.map(track, isFavorite = true))
    }

     override suspend fun deleteTrackFromFavorite(trackId: Int) {
        appDatabase.updateFavoriteStatus(trackId, isFavorite = false)
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return appDatabase.getIdsTracks()
    }

    override fun getFavoriteList(): Flow<List<Track>> {
        return appDatabase.getTracks()
            .map { tracks ->
                tracks.filter { it.isFavorite }
                    .map { trackDbConvertor.map(it) }
            }
    }
}