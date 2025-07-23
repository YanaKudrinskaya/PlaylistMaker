package com.yanakudrinskaya.playlistmaker.playlist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.playlistmaker.playlist.domain.PlaylistInteractor
import com.yanakudrinskaya.playlistmaker.playlist.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.playlist.ui.models.PlaylistScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var playlistJob: Job? = null

    private val playlistStateLiveData = MutableLiveData<PlaylistScreenState>()
    fun getPlaylistStateLiveData(): LiveData<PlaylistScreenState> = playlistStateLiveData

    init {
        getPlaylist()
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
            playlistStateLiveData.postValue(PlaylistScreenState.Empty)
        } else {
            playlistStateLiveData.postValue(PlaylistScreenState.Content(list))
        }
    }
}