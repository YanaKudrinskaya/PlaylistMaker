package com.yanakudrinskaya.playlistmaker.player.domain.impl

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.yanakudrinskaya.playlistmaker.player.domain.PlayerRepository
import com.yanakudrinskaya.playlistmaker.player.domain.TrackPlayer
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track

class TrackPlayerImpl(repository: PlayerRepository) : TrackPlayer {

    private val track = repository.getCurrentTrack()
    private val trackUrl = track.previewUrl
    private var mediaPlayer: MediaPlayer? = null
    private var currentObserver: TrackPlayer.StatusObserver? = null
    private var isPrepared = false
    private var isPlay = false
    private val progressHandler = Handler(Looper.getMainLooper())
    private var lastProgressPosition = 0f

    override fun prepare(prepareCallback: (Track) -> Unit) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(trackUrl)
            prepareAsync()
            setOnPreparedListener {
                isPrepared = true
                prepareCallback(track)
            }
            setOnCompletionListener {
                isPlay = false
                currentObserver?.onCompletion()
            }
        }
    }

    override fun play(statusObserver: TrackPlayer.StatusObserver) {
        currentObserver = statusObserver
        if (isPrepared) {
            if (!isPlay) {
                if (lastProgressPosition == 0f) {
                    mediaPlayer?.seekTo(0)
                }
                mediaPlayer?.start()
                isPlay = true
                statusObserver.onPlay()
                startProgressUpdates()
            }
        }
    }

    override fun pause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                isPlay = false
                lastProgressPosition = formatMilliseconds(player.currentPosition)
                currentObserver?.onPause()
                stopProgressUpdates()
            }
        }
    }

    override fun seek(position: Float) {
        val milliseconds = parseTimeToMilliseconds(position)
        mediaPlayer?.seekTo(milliseconds)
        lastProgressPosition = position
    }

    override fun release() {
        stopProgressUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
        isPlay = false
        currentObserver = null
        lastProgressPosition = 0f
    }

    private val progressUpdateRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    lastProgressPosition = formatMilliseconds(player.currentPosition)
                    currentObserver?.onProgress(lastProgressPosition)
                    progressHandler.postDelayed(this, 1000)
                }
            }
        }
    }

    private fun startProgressUpdates() {
        progressHandler.removeCallbacks(progressUpdateRunnable)
        progressHandler.postDelayed(progressUpdateRunnable, 50)
    }

    private fun stopProgressUpdates() {
        progressHandler.removeCallbacks(progressUpdateRunnable)
    }

    private fun formatMilliseconds(millis: Int): Float {
        return millis / 1000f
    }

    private fun parseTimeToMilliseconds(time: Float): Int {
        return (time * 1000).toInt()
    }
}