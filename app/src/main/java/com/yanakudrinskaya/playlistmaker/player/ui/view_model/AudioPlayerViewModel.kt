package com.yanakudrinskaya.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.playlistmaker.player.domain.TrackPlayerInteractor
import com.yanakudrinskaya.playlistmaker.player.ui.model.PlayerState
import com.yanakudrinskaya.playlistmaker.player.ui.model.TrackScreenState
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.utils.formatTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val track: Track,
    private val interactor: TrackPlayerInteractor
) : ViewModel() {

    companion object {
        private const val PLAY_DELAY = 300L
    }

    private var timerJob: Job? = null

    private var screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default(formatTime(0)))
    fun observePlayerState(): LiveData<PlayerState> = playerState

    init {
        initMediaPlayer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onPlayButtonClicked() {
        when(playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> { }
        }
    }

    private fun initMediaPlayer() {
        interactor.setDataSource(track.previewUrl!!)
        interactor.prepareAsync()
        interactor.setOnPreparedListener {
            playerState.postValue(PlayerState.Prepared(formatTime(0)))
            screenStateLiveData.value = TrackScreenState.Content(track)
        }
        interactor.setOnCompletionListener {
            timerJob?.cancel()
            interactor.seekTo(0)
            playerState.postValue(PlayerState.Prepared(formatTime(0)))
        }
    }

    private fun startPlayer() {
        interactor.start()
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        interactor.pause()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer() {
        interactor.stop()
        interactor.release()
        playerState.value = PlayerState.Default(formatTime(0))
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (interactor.isPlaying()) {
                delay(PLAY_DELAY)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return formatTime(interactor.getCurrentPosition())
    }
}