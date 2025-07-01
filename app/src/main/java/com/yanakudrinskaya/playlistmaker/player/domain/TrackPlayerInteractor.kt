package com.yanakudrinskaya.playlistmaker.player.domain

interface TrackPlayerInteractor {
    fun setDataSource(url: String)
    fun prepareAsync()
    fun start()
    fun pause()
    fun seekTo(position: Int)
    fun stop()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean

    fun setOnPreparedListener(listener: () -> Unit)
    fun setOnCompletionListener(listener: () -> Unit)
}