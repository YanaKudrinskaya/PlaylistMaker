package com.yanakudrinskaya.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.favorite.domain.db.FavoriteInteractor
import com.yanakudrinskaya.playlistmaker.media.domain.use_cases.ResourcesProviderUseCase
import com.yanakudrinskaya.playlistmaker.player.domain.TrackPlayerInteractor
import com.yanakudrinskaya.playlistmaker.player.ui.model.BottomSheetState
import com.yanakudrinskaya.playlistmaker.player.ui.model.PlayerState
import com.yanakudrinskaya.playlistmaker.player.ui.model.ToastState
import com.yanakudrinskaya.playlistmaker.player.ui.model.TrackScreenState
import com.yanakudrinskaya.playlistmaker.playlists.domain.PlaylistInteractor
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.playlists.ui.models.PlaylistsScreenState
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.utils.formatTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val track: Track,
    private val trackPlayerInteractor: TrackPlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val resourcesProvider: ResourcesProviderUseCase

) : ViewModel() {

    private var timerJob: Job? = null
    private var favoriteJob: Job? = null
    private var playlistJob: Job? = null

    private var screenStateLiveData = MutableLiveData<TrackScreenState>(TrackScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<TrackScreenState> = screenStateLiveData

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default(formatTime(0)))
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val bottomPlaylistStateLiveData = MutableLiveData<PlaylistsScreenState>()
    fun getBottomPlaylistStateLiveData(): LiveData<PlaylistsScreenState> = bottomPlaylistStateLiveData

    private val bottomSheetState = MutableStateFlow<BottomSheetState>(BottomSheetState.HIDDEN)
    fun getBottomSheetState(): StateFlow<BottomSheetState> = bottomSheetState

    private val toastMessage = MutableLiveData<ToastState>()
    fun getToastMessage(): LiveData<ToastState> = toastMessage


    init {
        initMediaPlayer()
    }

    fun showBottomSheet() {
        getPlaylist()
        bottomSheetState.value = BottomSheetState.COLLAPSED
    }

    fun hideBottomSheet() {
        bottomSheetState.value = BottomSheetState.HIDDEN
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onPlayButtonClicked() {
        when (playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
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

    private fun getPlaylist() {
        playlistJob?.cancel()
        playlistJob = viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { list ->
                    processResult(list)
                }
        }
    }

    private fun processResult(list: List<Playlist>) {
        if (list.isEmpty()) {
            bottomPlaylistStateLiveData.postValue(PlaylistsScreenState.Empty)
        } else {
            bottomPlaylistStateLiveData.postValue(PlaylistsScreenState.Content(list))
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {

        val currentTracks = playlist.tracks.toMutableList()
        if (track !in currentTracks) {
            currentTracks.add(0, track)
            viewModelScope.launch {
                playlistInteractor.updatePlaylistTracks(
                    playlistId = playlist.id,
                    tracks = currentTracks
                )
            }
            toastMessage.value =
                ToastState.Show(
                    true,
                    resourcesProvider.getString(R.string.added_to_playlist, playlist.title)
                )
        } else {
            toastMessage.value =
                ToastState.Show(
                    false,
                    resourcesProvider.getString(R.string.track_already_in_playlist, playlist.title)
                )
        }
    }

    fun clearToast() {
        toastMessage.value = ToastState.DontShow
    }

    companion object {
        private const val PLAY_DELAY = 300L
    }
}