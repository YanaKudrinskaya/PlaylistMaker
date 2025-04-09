package com.yanakudrinskaya.playlistmaker.domain.api


import com.yanakudrinskaya.playlistmaker.domain.models.TrackList

interface TracksRepository {
    fun searchTracks(expression: String): TrackList
}