package com.yanakudrinskaya.playlistmaker.domain.impl

import android.util.Log
import com.yanakudrinskaya.playlistmaker.domain.api.TracksInteractor
import com.yanakudrinskaya.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl (private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }
}