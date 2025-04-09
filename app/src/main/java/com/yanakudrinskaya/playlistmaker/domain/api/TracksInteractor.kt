package com.yanakudrinskaya.playlistmaker.domain.api


import com.yanakudrinskaya.playlistmaker.domain.models.TrackList

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(result: TrackList)
    }
}