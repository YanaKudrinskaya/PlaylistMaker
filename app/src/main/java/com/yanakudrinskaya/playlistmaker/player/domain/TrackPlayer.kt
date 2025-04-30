package com.yanakudrinskaya.playlistmaker.player.domain

import com.yanakudrinskaya.playlistmaker.search.domain.models.Track


interface TrackPlayer {
    fun prepare(prepareCallback: (Track) -> Unit)
    fun play(statusObserver: StatusObserver)
    fun pause()
    fun seek(position: Float)

    fun release()

    interface StatusObserver {
        fun onProgress(progress: Float)
        fun onPause()
        fun onPlay()
        fun onCompletion()
    }
}