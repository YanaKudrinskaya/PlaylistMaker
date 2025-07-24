package com.yanakudrinskaya.playlistmaker.favorite.domain.db

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {

    suspend fun addTrackToFavorite(track: Track)

    suspend fun deleteTrackFromFavorite(trackId: Int)

    suspend fun trackIsFavorite(trackId: Int): Boolean

    fun getFavoriteList(): Flow<List<Track>>
}