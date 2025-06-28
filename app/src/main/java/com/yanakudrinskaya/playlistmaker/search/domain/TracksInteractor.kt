package com.yanakudrinskaya.playlistmaker.search.domain

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface TracksInteractor {
    fun searchTracks(expression: String) : Flow<Pair<List<Track>?, String?>>

}