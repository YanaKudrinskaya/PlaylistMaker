package com.yanakudrinskaya.playlistmaker.search.domain

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track



interface SearchHistoryInteractor {
    suspend fun getHistoryList() : MutableList<Track>
    fun saveHistoryList(list: List<Track>)
    suspend fun addTrackToHistory(track: Track)
    fun clearSearchHistory()

}