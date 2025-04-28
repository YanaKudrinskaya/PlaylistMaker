package com.yanakudrinskaya.playlistmaker.search.domain

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track


interface SearchHistoryRepository {
    fun getHistoryList() : Array<Track>
    fun saveHistoryList(list: List<Track>)
}