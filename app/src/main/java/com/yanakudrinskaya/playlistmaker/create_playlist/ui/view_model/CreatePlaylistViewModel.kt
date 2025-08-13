package com.yanakudrinskaya.playlistmaker.create_playlist.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.CoverInteractor
import com.yanakudrinskaya.playlistmaker.create_playlist.domain.model.PlaylistCover
import com.yanakudrinskaya.playlistmaker.create_playlist.ui.models.CreationState
import com.yanakudrinskaya.playlistmaker.create_playlist.ui.models.DialogText
import com.yanakudrinskaya.playlistmaker.media.domain.use_cases.ResourcesProviderUseCase
import com.yanakudrinskaya.playlistmaker.playlists.domain.PlaylistInteractor
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    val playlistInteractor: PlaylistInteractor,
    val coverInteractor: CoverInteractor,
    val resourcesProvider: ResourcesProviderUseCase
) : ViewModel() {

    val coverState = MutableLiveData<PlaylistCover?>()
    fun getCoverState(): LiveData<PlaylistCover?> = coverState

    val creationState = MutableLiveData<CreationState>()
    fun getCreationState(): LiveData<CreationState> = creationState

    fun saveCover(uri: Uri) {
        viewModelScope.launch {
            try {
                coverState.value = coverInteractor.saveCoverToStorage(uri)
            } catch (e: Exception) {
                creationState.value = CreationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    open fun createPlaylist(title: String, description: String?) {
        viewModelScope.launch {
            try {
                val id = playlistInteractor.createPlaylist(title, description?: "", coverState.value)
                creationState.value = CreationState.Success(
                    id,
                    resourcesProvider.getString(R.string.playlist_created, title)
                )
            } catch (e: Exception) {
                creationState.value = CreationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getDialogText(): DialogText {
        return DialogText(
            title = resourcesProvider.getString(R.string.dialog_title_exit_creation),
            message = resourcesProvider.getString(R.string.dialog_message_unsaved_changes),
            positiveButton = resourcesProvider.getString(R.string.dialog_button_exit),
            neutralButton = resourcesProvider.getString(R.string.dialog_button_cancel)
        )
    }

}