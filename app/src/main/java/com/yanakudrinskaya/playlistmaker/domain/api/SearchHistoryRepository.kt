package com.yanakudrinskaya.playlistmaker.domain.api

import com.yanakudrinskaya.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun getHistoryList() : Array<Track>
    fun saveHistoryList(list: List<Track>)
}