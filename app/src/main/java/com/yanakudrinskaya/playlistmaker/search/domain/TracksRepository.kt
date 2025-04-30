package com.yanakudrinskaya.playlistmaker.search.domain


import com.yanakudrinskaya.playlistmaker.search.Resource
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}