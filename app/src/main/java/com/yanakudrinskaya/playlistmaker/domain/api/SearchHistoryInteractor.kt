package com.yanakudrinskaya.playlistmaker.domain.api

import com.yanakudrinskaya.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun getHistoryList(consumer: SearchHistoryConsumer) //: MutableList<Track>
    fun saveHistoryList(list: List<Track>)
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()

    interface SearchHistoryConsumer {
        fun consume(history: MutableList<Track>)
    }
}