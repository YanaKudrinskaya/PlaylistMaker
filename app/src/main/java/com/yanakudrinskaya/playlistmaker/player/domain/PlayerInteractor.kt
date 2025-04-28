package com.yanakudrinskaya.playlistmaker.player.domain

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

interface PlayerInteractor {
    fun setCurrentTrack(track: Track)
    fun getCurrentTrack(): Track?
}