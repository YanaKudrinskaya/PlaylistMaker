package com.yanakudrinskaya.playlistmaker.player.domain.impl


import com.yanakudrinskaya.playlistmaker.player.domain.AudioPlayerRepository
import com.yanakudrinskaya.playlistmaker.player.domain.TrackPlayerInteractor

class TrackPlayerInteractorImpl(
    private val audioPlayerRepository: AudioPlayerRepository
) : TrackPlayerInteractor {

    override fun setDataSource(url: String) {
        audioPlayerRepository.setDataSource(url)
    }

    override fun prepareAsync() {
        audioPlayerRepository.prepareAsync()
    }

    override fun start() {
        audioPlayerRepository.start()
    }

    override fun pause() {
        audioPlayerRepository.pause()
    }

    override fun seekTo(position: Int) {
        audioPlayerRepository.seekTo(position)
    }

    override fun stop() {
        audioPlayerRepository.stop()
    }

    override fun release() {
        audioPlayerRepository.release()
    }

    override fun getCurrentPosition(): Int {
        return audioPlayerRepository.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return audioPlayerRepository.isPlaying()
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        audioPlayerRepository.setOnPreparedListener { listener() }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        audioPlayerRepository.setOnCompletionListener { listener() }
    }
}