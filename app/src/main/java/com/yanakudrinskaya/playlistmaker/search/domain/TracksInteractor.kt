package com.yanakudrinskaya.playlistmaker.search.domain

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track


interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTrackList: List<Track>?, errorMessage: String?)
    }
}