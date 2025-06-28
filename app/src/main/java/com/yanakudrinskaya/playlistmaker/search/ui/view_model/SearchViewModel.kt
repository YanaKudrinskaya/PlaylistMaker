package com.yanakudrinskaya.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.TracksInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.search.ui.model.TrackState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchViewModel(
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private var tracksInteractor: TracksInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var searchJob: Job? = null

    private val searchLiveData = MutableLiveData<TrackState>()
    fun getSearchLiveData(): LiveData<TrackState> = mediatorStateLiveData

    private val historyLiveData = MutableLiveData<MutableList<Track>>()
    fun getHistoryLiveData(): LiveData<MutableList<Track>> = historyLiveData

    private val searchTextLiveData = MutableLiveData<String>("")
    fun getSearchTextLiveData(): LiveData<String> = searchTextLiveData

    private var latestSearchText: String? = null

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText && getSearchLiveData().value != TrackState.Error) return

        latestSearchText = changedText

        searchTextLiveData.postValue(changedText)

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
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
        getHistoryList()
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TrackState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTrackList: List<Track>?, errorMessage: String?) {
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
}
