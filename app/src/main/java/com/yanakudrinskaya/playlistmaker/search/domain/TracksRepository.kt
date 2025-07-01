package com.yanakudrinskaya.playlistmaker.search.domain


import com.yanakudrinskaya.playlistmaker.search.Resource
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}