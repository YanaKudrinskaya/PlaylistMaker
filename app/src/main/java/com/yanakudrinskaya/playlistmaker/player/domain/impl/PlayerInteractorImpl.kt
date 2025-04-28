package com.yanakudrinskaya.playlistmaker.player.domain.impl

import com.yanakudrinskaya.playlistmaker.player.domain.PlayerInteractor
import com.yanakudrinskaya.playlistmaker.player.domain.PlayerRepository
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

class PlayerInteractorImpl (private val repository: PlayerRepository): PlayerInteractor {

    override fun setCurrentTrack(track: Track) {
        return repository.setCurrentTrack(track)
    }

    override fun getCurrentTrack(): Track? {
        return repository.getCurrentTrack()
    }
}