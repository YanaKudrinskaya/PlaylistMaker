package com.yanakudrinskaya.playlistmaker.playlists.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.playlistmaker.playlists.domain.PlaylistInteractor
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.playlists.ui.models.PlaylistsScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var playlistJob: Job? = null

    private val playlistStateLiveData = MutableLiveData<PlaylistsScreenState>()
    fun getPlaylistStateLiveData(): LiveData<PlaylistsScreenState> = playlistStateLiveData

    init {
        getPlaylist()
    }

    fun getPlaylist() {
        playlistJob?.cancel()
        playlistJob = viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { list ->
                    processResult(list)
                    Log.d("MyLogDeleted", "${list.size}")
                }
        }
    }

    private fun processResult(list: List<Playlist>) {
        if (list.isEmpty()) {
            playlistStateLiveData.postValue(PlaylistsScreenState.Empty)
        } else {
            playlistStateLiveData.postValue(PlaylistsScreenState.Content(list))
        }
    }
}