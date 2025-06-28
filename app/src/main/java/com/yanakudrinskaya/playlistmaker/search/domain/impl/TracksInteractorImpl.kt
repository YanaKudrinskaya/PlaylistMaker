package com.yanakudrinskaya.playlistmaker.search.domain.impl

import com.yanakudrinskaya.playlistmaker.search.Resource
import com.yanakudrinskaya.playlistmaker.search.domain.TracksInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.TracksRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl (private val repository: TracksRepository) : TracksInteractor
{
    override fun searchTracks(expression: String) : Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error<*> -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}