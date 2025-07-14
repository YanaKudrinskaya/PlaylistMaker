package com.yanakudrinskaya.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.playlistmaker.media.domain.db.FavoriteInteractor
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
    private val trackPlayerInteractor: TrackPlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor

) : ViewModel() {



    private var timerJob: Job? = null
    private var favoriteJob: Job? = null

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

    fun onFavoriteButtonClicked() {
        track.isFavorite = !track.isFavorite
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            if (track.isFavorite) {
                favoriteInteractor.addTrackToFavorite(track)
            } else favoriteInteractor.deleteTrackFromFavorite(track.trackId)
        }
        screenStateLiveData.value = TrackScreenState.Favorite(track.isFavorite)
    }

    private fun initMediaPlayer() {
        viewModelScope.launch {
            track.isFavorite = favoriteInteractor.trackIsFavorite(track.trackId)
        }
        trackPlayerInteractor.setDataSource(track.previewUrl!!)
        trackPlayerInteractor.prepareAsync()
        trackPlayerInteractor.setOnPreparedListener {
            playerState.postValue(PlayerState.Prepared(formatTime(0)))
            screenStateLiveData.value = TrackScreenState.Content(track)
        }
        trackPlayerInteractor.setOnCompletionListener {
            timerJob?.cancel()
            trackPlayerInteractor.seekTo(0)
            playerState.postValue(PlayerState.Prepared(formatTime(0)))
        }
    }

    private fun startPlayer() {
        trackPlayerInteractor.start()
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        trackPlayerInteractor.pause()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer() {
        trackPlayerInteractor.stop()
        trackPlayerInteractor.release()
        playerState.value = PlayerState.Default(formatTime(0))
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (trackPlayerInteractor.isPlaying()) {
                delay(PLAY_DELAY)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return formatTime(trackPlayerInteractor.getCurrentPosition())
    }

    companion object {
        private const val PLAY_DELAY = 300L
    }
}