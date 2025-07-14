package com.yanakudrinskaya.playlistmaker.media.domain.impl

import com.yanakudrinskaya.playlistmaker.media.domain.db.FavoriteInteractor
import com.yanakudrinskaya.playlistmaker.media.domain.db.FavoriteRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(
    private val repository: FavoriteRepository
) : FavoriteInteractor {

    override suspend fun addTrackToFavorite(track: Track) {
        repository.addTrackToFavorite(track)
    }

    override suspend fun deleteTrackFromFavorite(trackId: Int) {
        repository.deleteTrackFromFavorite(trackId)
    }

    override suspend fun trackIsFavorite(trackId: Int): Boolean {
        val favoriteIds = repository.getFavoriteTrackIds()
        return favoriteIds.contains(trackId)
    }

    override fun getFavoriteList(): Flow<List<Track>> {
        return repository.getFavoriteList()
    }
}