package com.yanakudrinskaya.playlistmaker.player.data

import com.yanakudrinskaya.playlistmaker.player.domain.PlayerRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

object PlayerRepositoryImpl : PlayerRepository {

    private var currentTrack: Track? = null

    override fun setCurrentTrack(track: Track) {
        currentTrack = track
    }

    override fun getCurrentTrack(): Track = currentTrack!!

}