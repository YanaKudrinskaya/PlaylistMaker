package com.yanakudrinskaya.playlistmaker.media.domain.db

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {

    suspend fun addTrackToFavorite(track: Track)

    suspend fun deleteTrackFromFavorite(trackId: Int)

    fun getFavoriteList(): Flow<List<Track>>
}