package com.yanakudrinskaya.playlistmaker.edit_playlist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.CoverInteractor
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import com.yanakudrinskaya.playlistmaker.create_playlist.ui.models.CreationState
import com.yanakudrinskaya.playlistmaker.create_playlist.ui.view_model.CreatePlaylistViewModel
import com.yanakudrinskaya.playlistmaker.media.domain.use_cases.ResourcesProviderUseCase
import com.yanakudrinskaya.playlistmaker.playlists.domain.PlaylistInteractor
import com.yanakudrinskaya.playlistmaker.playlists.domain.PlaylistRepository
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    playlistInteractor: PlaylistInteractor,
    coverInteractor: CoverInteractor,
    resourcesProvider: ResourcesProviderUseCase,
    private val playlistId: Long
) : CreatePlaylistViewModel(
    playlistInteractor, coverInteractor, resourcesProvider
) {
    private val initialPlaylist = MutableLiveData<Playlist>()
    fun getInitialPlaylist(): LiveData<Playlist> = initialPlaylist

    lateinit var currentPlaylist: Playlist

    init {
        loadInitialPlaylist()
    }

    private fun loadInitialPlaylist() {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            playlist?.let {
                currentPlaylist = it
                initialPlaylist.postValue(it)
                coverState.value = PlaylistCover(it.cover)
            }
        }
    }

    override fun createPlaylist(title: String, description: String?) {
        viewModelScope.launch {
            try {

                val updatedPlaylist = currentPlaylist.copy(
                    title = title,
                    description = description ?: "",
                    cover = coverState.value?.filePath ?: currentPlaylist.cover
                )
                playlistInteractor.updatePlaylist(updatedPlaylist)
                creationState.value = CreationState.Success(
                    playlistId,
                    resourcesProvider.getString(R.string.playlist_updated, title)
                )
            } catch (e: Exception) {
                creationState.value = CreationState.Error(e.message ?: "Unknown error")
            }
        }
    }
}