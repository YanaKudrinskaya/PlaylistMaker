package com.yanakudrinskaya.playlistmaker.player.domain

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

interface PlayerRepository {
    fun setCurrentTrack(track: Track)
    fun getCurrentTrack(): Track
}