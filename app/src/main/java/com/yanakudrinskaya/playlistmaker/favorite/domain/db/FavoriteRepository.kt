package com.yanakudrinskaya.playlistmaker.favorite.domain.db

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun addTrackToFavorite(track: Track)

    suspend fun deleteTrackFromFavorite(trackId: Int)

    suspend fun getFavoriteTrackIds(): List<Int>

    fun getFavoriteList(): Flow<List<Track>>
}