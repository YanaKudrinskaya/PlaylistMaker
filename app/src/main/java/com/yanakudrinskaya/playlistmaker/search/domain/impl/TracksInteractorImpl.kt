package com.yanakudrinskaya.playlistmaker.search.domain.impl

import com.yanakudrinskaya.playlistmaker.search.Resource
import com.yanakudrinskaya.playlistmaker.search.domain.TracksInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl (private val repository: TracksRepository) : TracksInteractor
{

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
    }
}