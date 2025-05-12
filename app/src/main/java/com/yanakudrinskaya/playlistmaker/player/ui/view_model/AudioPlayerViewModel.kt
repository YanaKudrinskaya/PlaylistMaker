package com.yanakudrinskaya.playlistmaker.player.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanakudrinskaya.playlistmaker.player.domain.TrackPlayer
import com.yanakudrinskaya.playlistmaker.player.ui.model.PlayStatus
import com.yanakudrinskaya.playlistmaker.player.ui.model.TrackScreenState

class AudioPlayerViewModel(
    private val trackPlayer: TrackPlayer
) : ViewModel() {

    private var screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData

    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    init {
        trackPlayer.prepare { track ->
            screenStateLiveData.postValue(
                TrackScreenState.Content(track)
            )
        }
    }

    fun play() {

        trackPlayer.play(
            statusObserver = object : TrackPlayer.StatusObserver {
                override fun onProgress(progress: Float) {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(
                        progress = formatTime(progress),
                    )
                }

                override fun onPause() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = false)
                }

                override fun onPlay() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
                }

                override fun onCompletion() {

                    playStatusLiveData.value = PlayStatus(
                        progress = "00:00",
                        isPlaying = false,
                    )
                }
            },
        )
    }

    private fun formatTime(progress: Float): String {
        val seconds = progress.toInt()
        val minutes = seconds / 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = "00:00", isPlaying = false)
    }

    fun pause() {
        trackPlayer.pause()
    }

    override fun onCleared() {
        trackPlayer.release()
        super.onCleared()
    }
}



