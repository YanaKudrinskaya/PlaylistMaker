package com.yanakudrinskaya.playlistmaker.search.ui.view_model


import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanakudrinskaya.playlistmaker.player.domain.PlayerInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.TracksInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.search.ui.model.TrackState


class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private var tracksInteractor: TracksInteractor,
    private var playerInteractor: PlayerInteractor,
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val searchLiveData = MutableLiveData<TrackState>()
    fun getSearchLiveData(): LiveData<TrackState> = mediatorStateLiveData

    private val historyLiveData = MutableLiveData<MutableList<Track>>()
    fun getHistoryLiveData(): LiveData<MutableList<Track>> = historyLiveData

    private val searchTextLiveData = MutableLiveData<String>("")
    fun getSearchTextLiveData(): LiveData<String> = searchTextLiveData

    private var latestSearchText: String? = null

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText && getSearchLiveData().value != TrackState.Error) return

        this.latestSearchText = changedText
        searchTextLiveData.postValue(changedText)

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }


    fun clearSearchHistory() {
        searchHistoryInteractor.clearSearchHistory()
    }

    fun getHistoryList() {
        searchHistoryInteractor.getHistoryList(
            object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(history: MutableList<Track>) {
                    historyLiveData.postValue(history)
                }
            }
        )
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
        playerInteractor.setCurrentTrack(track)
        getHistoryList()
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TrackState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTrackList: List<Track>?, errorMessage: String?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTrackList != null) {
                        tracks.addAll(foundTrackList)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                TrackState.Error
                            )
                        }

                        tracks.isEmpty() -> {
                            renderState(
                                TrackState.Empty
                            )
                        }

                        else -> {
                            renderState(
                                TrackState.Content(
                                    tracks,
                                )
                            )
                        }
                    }

                }
            })
        }
    }

    private fun renderState(state: TrackState) {
        searchLiveData.postValue(state)
    }

    private val mediatorStateLiveData = MediatorLiveData<TrackState>().also { liveData ->
        liveData.addSource(searchLiveData) { trackState ->
            liveData.value = when (trackState) {
                is TrackState.Content -> TrackState.Content(trackState.tracks)
                is TrackState.Empty -> trackState
                is TrackState.Error -> trackState
                is TrackState.Loading -> trackState
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}
