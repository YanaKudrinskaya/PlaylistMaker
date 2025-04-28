package com.yanakudrinskaya.playlistmaker.search.ui.view_model

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.yanakudrinskaya.playlistmaker.creator.Creator
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.yanakudrinskaya.playlistmaker.search.domain.SearchHistoryInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.TracksInteractor
import com.yanakudrinskaya.playlistmaker.search.domain.models.Track
import com.yanakudrinskaya.playlistmaker.search.ui.model.TrackState
import com.yanakudrinskaya.playlistmaker.settings.data.impl.EXAMPLE_PREFERENCES
import com.yanakudrinskaya.playlistmaker.settings.data.impl.HISTORY_LIST_KEY

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
    private var tracksInteractor = Creator.provideTracksInteractor()
    private var playerInteractor = Creator.providePlayerInteractor()
    private val handler = Handler(Looper.getMainLooper())
    private val sharedPreferences = application.getSharedPreferences(EXAMPLE_PREFERENCES, MODE_PRIVATE)

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == HISTORY_LIST_KEY) {
            getHistoryList()
        }
    }
    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val searchLiveData = MutableLiveData<TrackState>()
    fun getSearchLiveData(): LiveData<TrackState> = mediatorStateLiveData

    private val historyLiveData = MutableLiveData<MutableList<Track>>()
    fun getHistoryLiveData(): LiveData<MutableList<Track>> = historyLiveData

    private val searchTextLiveData = MutableLiveData<String>("")
    fun getSearchTextLiveData(): LiveData<String> = searchTextLiveData

    private var latestSearchText: String? = null

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) return

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
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
