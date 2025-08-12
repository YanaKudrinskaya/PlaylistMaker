package com.yanakudrinskaya.playlistmaker.playlist.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.playlistmaker.R
import com.yanakudrinskaya.playlistmaker.media.domain.use_cases.ResourcesProviderUseCase
import com.yanakudrinskaya.playlistmaker.player.ui.model.BottomSheetState
import com.yanakudrinskaya.playlistmaker.playlist.ui.models.PlaylistScreenState
import com.yanakudrinskaya.playlistmaker.playlists.domain.PlaylistInteractor
import com.yanakudrinskaya.playlistmaker.playlists.domain.models.Playlist
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.sharing.domain.SharingInteractor
import com.yanakudrinskaya.playlistmaker.utils.formatDuration
import com.yanakudrinskaya.playlistmaker.utils.formatTrackCount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor,
    private val resourcesProvider: ResourcesProviderUseCase,
    private val sharingInteractor: SharingInteractor,

) : ViewModel() {

    private val playlistStateLiveData = MutableLiveData<PlaylistScreenState>()
    fun getPlaylistStateLiveData(): LiveData<PlaylistScreenState> = playlistStateLiveData

    private val bottomSheetState = MutableStateFlow<BottomSheetState>(BottomSheetState.HIDDEN)
    fun getBottomSheetState(): StateFlow<BottomSheetState> = bottomSheetState

    private val _playlistDeleted = MutableLiveData<Boolean>()
    val playlistDeleted: LiveData<Boolean> = _playlistDeleted

    lateinit var currentPlaylist: Playlist

    init {
        loadPlaylist()
    }

    fun loadPlaylist() {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            playlist?.let {
                currentPlaylist = it
                updateContent(playlist)
            }
        }
    }

    private fun updateContent(playlist: Playlist) {
        val tracksCountText = formatTrackCount(playlist.tracks.size)
        val totalDuration = calculateTotalDuration(playlist.tracks)
        val durationText = formatDuration(totalDuration)

        playlistStateLiveData.postValue(
            PlaylistScreenState.Content(
                coverUrl = playlist.cover,
                title = playlist.title,
                description = playlist.description,
                tracksCount = tracksCountText,
                duration = durationText,
                tracks = playlist.tracks
            )
        )
    }

    private fun calculateTotalDuration(tracks: List<Track>): Long {
        return tracks.sumOf { it.trackTimeMillis.toLong() }
    }

    fun updatePlaylistTracks(tracks: List<Track>) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylistTracks(playlistId, tracks)
            loadPlaylist()
        }
    }

    fun sharing() {
        hideBottomSheet()
        if (currentPlaylist.tracks.isEmpty()) playlistStateLiveData.value =
            PlaylistScreenState.EmptyList(
                resourcesProvider.getString(R.string.playlist_empty_share)
            )
        else {
            val shareMessage = getPlaylistShareMessage()
            val shareIntent = sharingInteractor.sharePlaylist(shareMessage)
            playlistStateLiveData.postValue(
                PlaylistScreenState.Share(shareIntent, sharingInteractor.getShareError())
            )
        }
    }

    fun showBottomSheet() {
        bottomSheetState.value = BottomSheetState.COLLAPSED
    }

    fun hideBottomSheet() {
        bottomSheetState.value = BottomSheetState.HIDDEN
    }

    private fun getPlaylistShareMessage(): String {
        val playlist = currentPlaylist
        val tracks = playlist.tracks

        val message = StringBuilder()
        message.append("${playlist.title}\n")
        message.append("${playlist.description}\n")
        message.append("${formatTrackCount(tracks.size)}\n\n")

        tracks.forEachIndexed { index, track ->
            val trackNumber = index + 1
            val duration = formatDuration(track.trackTimeMillis.toLong())
            message.append("$trackNumber. ${track.artistName} - ${track.trackName} ($duration)\n")
        }

        return message.toString()
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
            _playlistDeleted.postValue(true)
        }
    }

}