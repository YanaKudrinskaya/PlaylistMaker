package com.yanakudrinskaya.playlistmaker.favorite.domain.impl

import com.yanakudrinskaya.playlistmaker.favorite.domain.db.FavoriteInteractor
import com.yanakudrinskaya.playlistmaker.favorite.domain.db.FavoriteRepository
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